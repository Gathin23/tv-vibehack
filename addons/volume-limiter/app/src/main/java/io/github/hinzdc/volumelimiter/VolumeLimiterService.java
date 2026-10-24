package io.github.hinzdc.volumelimiter;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ServiceInfo;
import android.database.ContentObserver;
import android.media.AudioManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;

/**
 * Foreground service that watches audio volume changes and enforces a maximum volume cap.
 * If volume exceeds the configured limit, it pulls it back down to the limit.
 * If volume is lower than the limit, it does not alter or interfere with it.
 */
public class VolumeLimiterService extends Service {
    private static final String TAG = "VolumeLimiter";
    public static final String ACTION_VOLUME_UPDATED = "io.github.hinzdc.volumelimiter.VOLUME_UPDATED";
    private static final String CHANNEL_ID = "volume_guard_channel";
    private static final int NOTIFICATION_ID = 101;

    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private AudioManager audioManager;
    private boolean receiverRegistered = false;

    private final SharedPreferences.OnSharedPreferenceChangeListener prefsListener =
            (sharedPreferences, key) -> enforceLimit();

    private final BroadcastReceiver volumeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            enforceLimit();
        }
    };

    private final ContentObserver volumeObserver = new ContentObserver(mainHandler) {
        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            enforceLimit();
        }
    };

    public static void start(Context context) {
        try {
            Intent intent = new Intent(context, VolumeLimiterService.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent);
            } else {
                context.startService(intent);
            }
        } catch (Exception e) {
            Log.w(TAG, "Failed to start service: " + e.getMessage());
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        createNotificationChannel();
        startInForeground();

        Prefs.of(this).registerOnSharedPreferenceChangeListener(prefsListener);

        IntentFilter filter = new IntentFilter();
        filter.addAction("android.media.VOLUME_CHANGED_ACTION");
        try {
            registerReceiver(volumeReceiver, filter);
            receiverRegistered = true;
        } catch (Exception e) {
            Log.w(TAG, "Failed to register volume receiver: " + e.getMessage());
        }

        try {
            getContentResolver().registerContentObserver(Settings.System.CONTENT_URI, true, volumeObserver);
        } catch (Exception e) {
            Log.w(TAG, "Failed to register content observer: " + e.getMessage());
        }

        enforceLimit();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        enforceLimit();
        return START_STICKY;
    }

    private void enforceLimit() {
        if (audioManager == null) return;
        if (!Prefs.isEnabled(this)) return;

        int limit = Prefs.getLimit(this);
        if (limit <= 0) return;

        try {
            int current = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            if (current > limit) {
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, limit, AudioManager.FLAG_SHOW_UI);
            }
            sendBroadcast(new Intent(ACTION_VOLUME_UPDATED).setPackage(getPackageName()));
        } catch (Exception e) {
            Log.w(TAG, "Error enforcing volume limit: " + e.getMessage());
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager nm = getSystemService(NotificationManager.class);
            if (nm != null) {
                NotificationChannel channel = new NotificationChannel(
                        CHANNEL_ID,
                        getString(R.string.notification_channel),
                        NotificationManager.IMPORTANCE_MIN
                );
                channel.setDescription(getString(R.string.notification_text));
                nm.createNotificationChannel(channel);
            }
        }
    }

    private void startInForeground() {
        Notification.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder = new Notification.Builder(this, CHANNEL_ID);
        } else {
            builder = new Notification.Builder(this);
        }

        Notification notification = builder
                .setSmallIcon(R.drawable.banner)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(getString(R.string.notification_text))
                .setOngoing(true)
                .build();

        if (Build.VERSION.SDK_INT >= 34) {
            startForeground(NOTIFICATION_ID, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE);
        } else {
            startForeground(NOTIFICATION_ID, notification);
        }
    }

    @Override
    public void onDestroy() {
        Prefs.of(this).unregisterOnSharedPreferenceChangeListener(prefsListener);
        if (receiverRegistered) {
            try {
                unregisterReceiver(volumeReceiver);
            } catch (Exception ignored) {}
            receiverRegistered = false;
        }
        try {
            getContentResolver().unregisterContentObserver(volumeObserver);
        } catch (Exception ignored) {}
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
