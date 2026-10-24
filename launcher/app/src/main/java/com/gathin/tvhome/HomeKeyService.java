package com.gathin.tvhome;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.os.UserManager;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.provider.Settings;
import android.view.KeyEvent;
import android.view.ViewConfiguration;
import android.view.accessibility.AccessibilityEvent;
import android.widget.Toast;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Sends the remote's Home button to this launcher on remotes that emit KEYCODE_ALL_APPS for Home
 * (Xiaomi sticks and boxes). Android TV 9 routes that key only to the system-image launcher, so once
 * the stock launcher is disabled the button does nothing. Every other key passes through untouched.
 *
 * With stock_home_redirect set, it also sends the stock launcher's home screen back here, for firmware that turns
 * that launcher on again after it was disabled (XGIMI's xrmservice does, on its own remote keys), or that cannot be
 * disabled at all, and a short press of Home opens this launcher even when the stock home is already on screen.
 *
 * With boot_app_redirect set, it sends the app the firmware opens at power-on (the OnePlus Y1's Live TV) back here,
 * once per boot, when that app is the first thing on screen.
 *
 * Enable over adb:
 *   settings put secure enabled_accessibility_services com.gathin.tvhome/.HomeKeyService
 *   settings put secure accessibility_enabled 1
 */
public class HomeKeyService extends AccessibilityService {

    /** restore_key_services was on when the config was read: this helper is noted for ServiceRepair. */
    private boolean repairRecord;
    /** Remote buttons that should open Settings (config.xml, settings_key_scancodes and settings_key_keycodes). */
    private int[] settingsScancodes = new int[0];
    private int[] settingsKeycodes = new int[0];
    private boolean settingsPanel;
    /** consume_volume_keys: Volume Up/Down go to VolumeKeys instead of on to the firmware. */
    private boolean consumeVolumeKeys;
    /** app_keys, the same maps InputKeyService reads, for setups that run only this service. */
    private Map<Integer, String> appKeys = Collections.emptyMap();
    private Map<Integer, String> appScancodes = Collections.emptyMap();
    private Map<Integer, String> holdAppKeys = Collections.emptyMap();
    private Map<Integer, String> holdAppScancodes = Collections.emptyMap();

    /** Waits for the user to unlock when Android binds this service at boot; null once the config is read. */
    private BroadcastReceiver unlockReceiver;
    /** The stock launcher's HOME activities (stock_home_redirect), disabled ones included. */
    private final Set<String> stockHome = new HashSet<>();
    /** boot_app_redirect while it is armed for this boot; null when it is off or has decided. */
    private String bootApp, bootActivity;
    /** elapsedRealtime when boot_app_redirect was armed. */
    private long bootArmedAt;
    /** Stops key filtering while a game controller is connected; null unless gamepad_passthrough and key_filter, or before the unlock. */
    private GamepadPassthrough gamepads;

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        settingsScancodes = getResources().getIntArray(R.array.settings_key_scancodes);
        // The service is directBootAware: Android 11 skips a service that is not while the user is still
        // locked at boot, writes accessibility_enabled=0 and, on TCL G03 firmware, never binds it after the
        // unlock. Config.load reads credential-protected storage, so wait for the unlock before reading it.
        UserManager users = getSystemService(UserManager.class);
        if (users.isUserUnlocked()) {
            loadConfig();
            return;
        }
        unlockReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                stopWaitingForUnlock();
                loadConfig();
            }
        };
        registerReceiver(unlockReceiver, new IntentFilter(Intent.ACTION_USER_UNLOCKED));
        // The unlock may have happened between the check and the registration.
        if (users.isUserUnlocked()) {
            stopWaitingForUnlock();
            loadConfig();
        }
    }

    private void stopWaitingForUnlock() {
        if (unlockReceiver == null) return;
        unregisterReceiver(unlockReceiver);
        unlockReceiver = null;
    }

    @Override
    public void onDestroy() {
        stopWaitingForUnlock();
        if (gamepads != null) gamepads.stop();
        if (consumeVolumeKeys) VolumeKeys.stop();
        super.onDestroy();
    }

    private void loadConfig() {
        // Keycodes and the panel choice can also come from a theme; theme.sh restarts this service.
        Config config = Config.load(this);
        // restore_key_services: note that this helper runs, so Home can put it back after a force stop. Here, not
        // in onServiceConnected: the note is in credential-protected storage too, readable only after the unlock.
        repairRecord = config.restoreKeyServices;
        if (repairRecord) ServiceRepair.connected(this, getClass());
        settingsKeycodes = config.settingsKeycodes;
        settingsPanel = config.settingsKeyPanel;
        consumeVolumeKeys = config.consumeVolumeKeys;
        appKeys = RemoteKeys.parseAppKeys(config.appKeys);
        appScancodes = RemoteKeys.parseAppScancodes(config.appKeys);
        RemoteKeys.applyKeyFilter(this, config.keyFilter);
        stockHome.clear();
        if (!config.stockHomeRedirect.isEmpty()) {
            Intent home = new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME).setPackage(config.stockHomeRedirect);
            for (ResolveInfo ri : getPackageManager().queryIntentActivities(home, PackageManager.MATCH_DISABLED_COMPONENTS)) {
                stockHome.add(ri.activityInfo.name);
            }
        }
        // boot_app_redirect: armed at the first bind of a boot only, so a rebind (theme.sh) never re-arms it. The boot
        // app itself, Home on screen, BOOT_APP_ARM_MS or (with key_filter on) a key then ends it, and the window
        // events it asked for stop.
        bootApp = null;
        android.content.ComponentName boot = config.bootAppRedirect.isEmpty()
                ? null : android.content.ComponentName.unflattenFromString(config.bootAppRedirect);
        if (boot != null) {
            int count = Settings.Global.getInt(getContentResolver(), Settings.Global.BOOT_COUNT, -1);
            SharedPreferences prefs = getSharedPreferences("boot", MODE_PRIVATE);
            long now = SystemClock.elapsedRealtime();
            if (firstBindAfterBoot(count, prefs.getInt("boot_app_boot", -1), now)) {
                // commit, not apply: if the launcher is killed in the first minutes of a boot, a lost record would
                // re-arm the redirect at the next bind and could send a Live TV or HDMI window the owner chose Home.
                prefs.edit().putInt("boot_app_boot", count).commit();
                bootApp = boot.getPackageName();
                bootActivity = boot.getClassName();
                bootArmedAt = now;
            }
        }
        // home_key_service.xml subscribes to no events, so a redirect asks for window changes only when it is on.
        boolean watch = !stockHome.isEmpty() || bootApp != null;
        AccessibilityServiceInfo info = getServiceInfo();
        if (info != null && watch) {
            info.eventTypes |= AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED;
            setServiceInfo(info);
        }
        if (watch) probeAtBoot();
        holdAppKeys = RemoteKeys.parseHoldAppKeys(config.appKeys);
        holdAppScancodes = RemoteKeys.parseHoldAppScancodes(config.appKeys);
        // gamepad_passthrough, off by default. Only with key_filter on: off, the service never filters keys.
        if (config.gamepadPassthrough && config.keyFilter && gamepads == null) {
            gamepads = new GamepadPassthrough(this);
            gamepads.start();
        }
    }

    private boolean isSettingsKey(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_SETTINGS) return true;
        for (int code : settingsScancodes) if (event.getScanCode() == code) return true;
        for (int code : settingsKeycodes) if (event.getKeyCode() == code) return true;
        return false;
    }

    @Override
    protected boolean onKeyEvent(KeyEvent event) {
        // boot_app_redirect: after a key from the remote, whatever opens next is the user's choice.
        disarmBootApp();
        if (consumeVolumeKeys && VolumeKeys.handle(this, event)) return true;
        // A scancode entry is more specific than any keycode, so it goes first.
        if (RemoteKeys.handle(this, event, appScancodes.get(event.getScanCode()),
                holdAppScancodes.get(event.getScanCode()))) return true;
        // The stock launcher handled the remote's Settings button; with it disabled the button does nothing.
        if (isSettingsKey(event)) {
            if (event.getAction() == KeyEvent.ACTION_UP && !event.isCanceled()) {
                Intent open = settingsPanel ? new Intent(this, QuickSettingsActivity.class)
                        : SystemSettings.intent(this, Settings.ACTION_SETTINGS);
                try {
                    startActivity(open.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                } catch (ActivityNotFoundException | SecurityException unavailable) {
                    Toast.makeText(this, R.string.not_available, Toast.LENGTH_SHORT).show();
                }
            }
            return true;
        }
        if (RemoteKeys.handle(this, event, appKeys.get(event.getKeyCode()),
                holdAppKeys.get(event.getKeyCode()))) return true;
        if (event.getKeyCode() == KeyEvent.KEYCODE_HOME && !stockHome.isEmpty()) {
            redirectHomeKey(event);
            return false;
        }
        if (event.getKeyCode() != KeyEvent.KEYCODE_ALL_APPS) return false;
        if (event.getAction() == KeyEvent.ACTION_UP && !event.isCanceled()) {
            Intent home = new Intent(Intent.ACTION_MAIN)
                    .addCategory(Intent.CATEGORY_HOME)
                    .setPackage(getPackageName())
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
            startActivity(home);
        }
        return true;
    }

    /** Unbound by Android: the user turned the helper off, so Home doesn't put it back (ServiceRepair). */
    @Override
    public boolean onUnbind(Intent intent) {
        if (repairRecord) ServiceRepair.disconnected(this, getClass());
        return super.onUnbind(intent);
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (event.getEventType() != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) return;
        CharSequence cls = event.getClassName();
        if (bootApp != null) {
            long now = SystemClock.elapsedRealtime();
            int verdict = bootVerdict(bootApp, bootActivity, MainActivity.class.getName(), event.getPackageName(), cls,
                    now, now - bootArmedAt);
            if (verdict != BOOT_WAIT) disarmBootApp();
            if (verdict == BOOT_REDIRECT) {
                handler.postDelayed(this::openHome, HOME_KEY_DELAY_MS);
                return;
            }
        }
        // stock_home_redirect: the firmware turned the stock launcher back on and its home screen came up.
        if (cls != null && stockHome.contains(cls.toString())) openHome();
    }

    /** boot_app_redirect: what a window that comes up while it is armed means. */
    static final int BOOT_WAIT = 0, BOOT_REDIRECT = 1, BOOT_DISARM = 2;
    /** How long boot_app_redirect stays armed. The firmware starts its app at the same unlock that arms it, and the
     *  probe makes the window in front report within a second, so this is margin, not a wait. Without key filtering
     *  no key can disarm it, so it must not stay armed for long. */
    static final long BOOT_APP_ARM_MS = 30_000L;

    /** The boot app decides a redirect; this launcher's home screen first means the TV booted to Home, so a later
     *  Live TV or HDMI window is the user's. Anything else (ProbeActivity, a toast, another window of the same app)
     *  waits. After BOOT_APP_ARM_MS from arming, or past the boot window, it is over either way. */
    static int bootVerdict(String app, String activity, String home, CharSequence pkg, CharSequence cls, long uptimeMs,
                           long sinceArmedMs) {
        if (uptimeMs >= BOOT_WINDOW_MS || sinceArmedMs >= BOOT_APP_ARM_MS) return BOOT_DISARM;
        if (pkg == null || cls == null) return BOOT_WAIT;
        if (app.contentEquals(pkg) && activity.contentEquals(cls)) return BOOT_REDIRECT;
        if (home.contentEquals(cls)) return BOOT_DISARM;
        return BOOT_WAIT;
    }

    /** boot_app_redirect has decided for this boot: drop the window events it asked for, unless stock_home_redirect
     *  still needs them (home_key_service.xml subscribes to none, so no window change reaches the launcher). */
    private void disarmBootApp() {
        if (bootApp == null) return;
        bootApp = null;
        if (!stockHome.isEmpty()) return;
        AccessibilityServiceInfo info = getServiceInfo();
        if (info == null) return;
        info.eventTypes &= ~AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED;
        setServiceInfo(info);
    }

    /** How long after boot the first bind still counts as "at boot", for probeAtBoot and for arming boot_app_redirect. */
    static final long BOOT_WINDOW_MS = 3 * 60 * 1000L;

    /** At boot, for stock_home_redirect and boot_app_redirect: the window in front is often there before Android
     *  binds this service, so no window event for it arrives (#1116). Once per boot, open ProbeActivity: when it
     *  closes, whatever was in front comes back and sends a window event, and onAccessibilityEvent acts on it only if
     *  it is the stock home or the armed boot_app_redirect activity (whatever input that activity shows, HDMI
     *  included). Any other app or input the TV resumed at boot is left alone. */
    private void probeAtBoot() {
        int boot = Settings.Global.getInt(getContentResolver(), Settings.Global.BOOT_COUNT, -1);
        SharedPreferences prefs = getSharedPreferences("boot", MODE_PRIVATE);
        if (!firstBindAfterBoot(boot, prefs.getInt("probe_boot", -1), SystemClock.elapsedRealtime())) return;
        prefs.edit().putInt("probe_boot", boot).apply();
        try {
            startActivity(new Intent(this, ProbeActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION));
        } catch (ActivityNotFoundException | SecurityException ignored) {
        }
    }

    static boolean firstBindAfterBoot(int boot, int lastBoot, long uptimeMs) {
        return boot >= 0 && boot != lastBoot && uptimeMs < BOOT_WINDOW_MS;
    }

    /** Lets the system see the key, so a long press still does what the firmware gives it. */
    private static final long HOME_KEY_DELAY_MS = 250;
    private final Handler handler = new Handler(Looper.getMainLooper());

    /** stock_home_redirect on the Home key: when the stock home is already on screen (it came up at boot, before
     *  Android bound this service), Home brings it up again with no window change, so the redirect above never
     *  fires and the TV stays on the stock home until another app opens (#1116). A short press opens this
     *  launcher shortly after the system has handled the key; a long press (Google TV's quick settings) is left
     *  alone. Held time, not repeats: key repeats are made after accessibility filtering, so none arrive here. */
    private void redirectHomeKey(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_UP && !event.isCanceled()
                && isShortPress(event.getDownTime(), event.getEventTime(), ViewConfiguration.getLongPressTimeout())) {
            handler.postDelayed(this::openHome, HOME_KEY_DELAY_MS);
        }
    }

    static boolean isShortPress(long downTime, long upTime, long longPressTimeout) {
        return upTime - downTime < longPressTimeout;
    }

    private void openHome() {
        startActivity(new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME).setPackage(getPackageName())
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED));
    }

    @Override
    public void onInterrupt() {
    }
}
