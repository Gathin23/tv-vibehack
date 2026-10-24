# Coocaa 14K / 8S61 (China domestic, Android 4.4.2) — disable guard, debloat not feasible

Report for the mainland-China Coocaa firmware on the oldest family member seen so far. Source: [#1261](https://github.com/Gathin23/cleanyourtv/issues/1261). Related family reports — #417 (8R145 / K3 Pro, Android 11), #692 (8R40_K5C, Android 8), #537 (7S60_A5, Android 8) — do **not** transfer: this board is four Android versions older, runs an MStar SoC instead of a Realtek one, and is the only one with an active debloat guard.

> **Warning:** `pm disable-user` fails on every package with a framework exception, and `pm block`, which does execute, can wedge the boot (system_server unresponsive, adbd stuck offline, only the mains switch recovers). If a boot wedges, the recovery window is the first ~40 seconds of the next boot, while system_server is still limping: connect and `pm unblock` everything.

## Device

| Field | Value |
| :--- | :--- |
| Retail model | Coocaa 14K (酷开), China domestic; getprop shows "Skyworth 8S61 14K" |
| Product / device | `sky628_8s61` ("madison" platform, MStar SoC) |
| Android / SDK | 4.4.2 / 19 |
| Build | `full_sky628_8s61-userdebug 4.4.2 KOT49H 018.010.130 test-keys`, dated 2018-10-13 |
| Build type | userdebug + test-keys, but `ro.secure=1`, so `adb root` is refused |
| Packages | 47 total |

## ADB entry

- No Developer options in Settings. The factory menu opens from the **本机信息** (device info) page: press the remote's direction keys **up up down down left right left right** and then keep the remote still for about two seconds.
- The ADB switch is factory menu → 其他设置 (other settings) → **Adbd Enable** 0→1. Everything else in the factory menu is panel calibration (ADC, white balance, Panel Timing and friends) — leave it alone.
- Unlike the toggles in #417 and #692, this one is **persistent across reboots**: adbd comes back on its own after a power cycle.
- TCP adb connects with no authorisation prompt.
- **`adb reboot` powers the TV off** on this board instead of rebooting. All reboot testing has to go through the remote or the mains switch.

## The disable guard

`pm disable` and `pm disable-user --user 0` fail on **every** package, tianci and non-tianci alike:

```
java.lang.RuntimeException: i am here
	at android.os.Process.killProcess(Process.java:943)
```

The framework throws from `Process.killProcess` and the operation aborts, so the enabled setting never changes. It is not a "process is running" side effect: reproduced after `am kill` on the target. `pm hide` does not exist on this 4.4 pm. The guard looks deliberate — a debloat brake left in the custom framework.

## `pm block`: executes, then breaks

`pm block --user 0` succeeds (blocked state confirmed via `dumpsys package`, reversible with `pm unblock`). Two problems showed up:

1. **UI pollution:** the stock Coocaa launcher's "My Apps" then lists every blocked package as a raw package-name icon (`com.tianci`, `com.skyw`, ...).
2. **Boot wedges.** After blocking 15 junk candidates identified by live inspection (`com.tianci.ad`, `com.tianci.push`, `com.tianci.autoinstaller`, `com.tianci.ipc`, `com.tianci.user`, `com.coocaa.mall`, `com.coocaa.messageprovider`, `com.coocaa.tvpitv`, `com.icoolme.tv.weather`, `com.android.mcastdemo`, `com.js.litchi`, `com.skyworth.tv_browser`, `com.skyworth.miracast`, `com.coocaa.remotectrlservice`, `com.skyworth.simplehomepage`):

   - the first boot survived, but the home screen did not autostart (`am start -n com.tianci.movieplatform/com.coocaa.homepage.vast.RealHomePageActivity` brought it up fine);
   - subsequent boots intermittently **wedged**: the source screen visible but system_server unresponsive, remote dead, adbd stuck in `offline`. Same block state, different outcome per boot — a race against the boot chain, which on this firmware appears to run signal source → boot ad → home;
   - `com.gzads.tvac` (third-party ad SDK, `/system/app/TVAC.apk`) was already disabled from the factory.

Everything was reverted with `pm unblock --user 0`; boots are stable again on stock.

**Recovery window:** when a boot wedges, adbd still comes up roughly 40 seconds into the next boot while system_server is limping. Connect at once and unblock everything — this worked on the first try.

## Package notes

- 47 packages, `com.tianci.*` / `com.skyworth.*` / `com.coocaa.*` namespace, MStar platform services (`com.mstar.*`).
- The user-visible "iQiyi/银河奇异果" app on this TV is `com.js.litchi` (a user-installed APK in `/data/app`); the GITV player framework itself is embedded in the stock launcher `com.tianci.movieplatform` (`com.tianci.framework.player.iqiyi.*`, receivers for `com.gitvdemo.video.action.*`). Keeping the stock launcher keeps the video app.
- `pm list packages` output on this pm is complete but `dumpsys package` is the more reliable cross-check.

## Conclusion

Package-level debloat is **not feasible** on this firmware: the disable guard is deliberate, and the boot chain is coupled to the ad/push components tightly enough that blocking them wedges boots. The launcher side can never apply either — `minSdk` 26 needs Android 8.0. For this model the honest end state is stock firmware with the ADB knowledge above; other 8S61 / "madison" owners should not repeat the block experiment.

## Open questions

- Whether a single-package block (for example `com.tianci.ad` alone) boots stably was not tested; the owner stopped after the wedges.
- Root on MStar "madison" 4.4.2 is unexplored; `ro.secure=1` rules out `adb root` without an exploit.
