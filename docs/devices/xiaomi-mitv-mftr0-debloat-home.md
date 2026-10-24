# Xiaomi MiTV-MFTR0: debloat and third-party Home on HyperOS TV 2, and the OS3.0 OTA

Companion to [xiaomi-mitv-mftr0-freeguy.txt](xiaomi-mitv-mftr0-freeguy.txt) (input
routes, Xiaomi settings intents, app discovery). This note covers the 2026-10-22
debloat/Home session on a MiTV-MFTR0 (`freeguy`, MediaTek mt9653), **China firmware
`OS2.0.23.0.RFTMATV` (HyperOS TV 2), Android 11 (SDK 30)** — the second CN set from
the same sessions as [MiTV-ASTR0](xiaomi-mitv-astr0-blueplanet.md) (HyperOS TV 3,
Android 14), and the starting point was the ASTR0 findings. Everything below was
checked per-package on this set first.

A unit of this model that took the OS3.0 OTA answers the Home question differently:
[After the OS3.0.28 OTA](#after-the-os3028-ota-home-does-not-move).

## Same ad screensaver, one generation earlier

`com.xiaomi.mitv.hyper.screensaver` exists on OS2 with identical settings
(`screen_saver_timeout=480000`, time/weather cards open, AOSP `screensaver_enabled=0`
unrelated). It was disabled without waiting for a live capture: same package, same
settings shape, same firmware family as the ASTR0 evidence. The classic
`com.mitv.screensaver` / `com.xiaomi.mitv.advertise` do not exist here either;
`boot_ad=0` out of the box.

## Home: the allowlist trampoline works on OS2 too

Same sequence as ASTR0, same outcome: with the allowlist-identity home
(`com.ts.launcher`) installed and PatchWall (`com.mitv.tvhome`) disabled, HOME
resolves to the trampoline and lands on the installed third-party launcher
(`com.oversea.aslauncher`, Emotn UI), across reboots — the set boots straight into
it. `cmd package set-home-activity` on an ordinary app is equally cosmetic here
(reports `Success`, changes nothing). So on CN Xiaomi sets the pattern so far:
OS2/Android 11 and OS3/Android 14 both honor the package-name allowlist, MiTV-MFFU0
(OS3.0.115) included. As on ASTRP0-style lists: install
the trampoline first, disable PatchWall second; `pm enable com.mitv.tvhome` plus a
reboot undoes it.

## What differs from the ASTR0 set

- No WPS preload (`cn.wps.moffice_i18n_TV` absent), no `com.mitv.overseaservice`,
  no AOSP adservices/ondevicepersonalization/federatedcompute module packages
  (Android 11 predates them).
- `com.emotn.browser` (Emotn's browser, distinct from their launcher) ships as a
  preload here and was disabled.
- Keep-list verified again: `com.xiaomi.mitv.smartpanel` is the Menu-button panel;
  `com.sohu.inputmethod.sogou.tv` is the only IME; `com.xiaomi.mitv.tvplayer`,
  `com.xiaomi.mitv.settings`, `com.xiaomi.mitv.systemui`, `mitv.service` untouched.

## Two operational notes

- First ADB-over-TCP connect needs the on-TV `允许USB调试` RSA confirmation (tick
  "always allow"); if the dialog never appears, `adb kill-server` + reconnect
  re-triggers the handshake. ADB closes again on every reboot.
- **Classify preloads by `pm path`, not by memory**: an owner-installed Moonlight
  (`com.limelight`) sat in an early draft of the list because a same-named package
  is a factory preload on other sets; `pm path` showed `/data/app/...` (user
  install) and it was dropped from the list and re-enabled. `pm disable-user`
  hides the icon, which reads as "deleted" to the owner; `pm enable` restores it
  with data intact.

## After the OS3.0.28 OTA: Home does not move

One owner's unit took a silent OTA on 2026-10-24 from OS2.0 to **`OS3.0.28.0.UFTMATV`
(HyperOS TV 3), Android 14 (SDK 34)** (`persist.os2.upgraded=true`, and
`persist.sys.boot.reason.history` ending in `reboot,ota,...`). Everything above was
written on OS2.0.23.0 / Android 11 and still describes that build. The report is
[#1355](https://github.com/Gathin23/cleanyourtv/issues/1355): one unit, one session,
nothing re-run here. That unit ran a plain third-party home app (`app.orchard.tv`,
MAIN + HOME + DEFAULT) with PatchWall left enabled - not the allowlist identity this
repo installs on CN Xiaomi sets, so this section records what an ordinary third-party
home app meets on that build, not what the trampoline meets.

### Both standard commands report success and change nothing

```shell
cmd role add-role-holder --user 0 android.app.role.HOME app.orchard.tv
cmd role get-role-holders --user 0 android.app.role.HOME     # app.orchard.tv
cmd package set-home-activity --user 0 app.orchard.tv/.MainActivity    # Success
cmd package resolve-activity --brief -a android.intent.action.MAIN -c android.intent.category.HOME
# com.mitv.tvhome/.MainActivityUserMode
cmd package query-activities --brief -a android.intent.action.MAIN -c android.intent.category.HOME
# two activities: com.mitv.tvhome/.MainActivityUserMode (0), com.xiaomi.mitv.settings/.entry.FallbackHome (-1000)
```

Four samples in the eight seconds after `input keyevent KEYCODE_HOME` all landed on
PatchWall, and the resolver lists two candidates: PatchWall at priority 0 and
`com.xiaomi.mitv.settings/.entry.FallbackHome` at -1000. The new home app was not one of
them, so this is a resolver choice, not a missing default-home setting.

### Why: a persistent preferred activity with `mAlways=true`

`dumpsys package com.mitv.tvhome` carries the record that outranks both the role and
`set-home-activity`:

```
PreferredActivities:
  mitv.intent.action.PW_CUSTOMIZATION:
    com.mitv.tvhome/.MainActivityUserMode
     mMatch=0x100000 mAlways=true
      Selected from:
        com.mitv.tvhome/.MainActivityUserMode
        com.xiaomi.mitv.settings/.entry.FallbackHome
      Action: "android.intent.action.MAIN"
      Category: "android.intent.category.HOME"
```

The owner's question was how to clear that without root. `clear-package-preferred-activities`
answers `Unknown command` on this build, and nothing in this repo writes
preferred-activity records: the tools disable and re-enable packages and set a home
activity, nothing more. So there is no step to offer for this state, and none is
invented here.

### Disabling the stock launcher leaves HOME with no window on this build

The OS2 sequence above works because the home app is installed under the
allowlist identity (`com.ts.launcher`) and the firmware hands HOME to it once PatchWall
is disabled. With that record in place and no allowlisted home app,
`pm disable-user --user 0 com.mitv.tvhome` was accepted and HOME then resolved to
nothing: `mCurrentFocus=null`, no usable window, until the owner ran
`pm enable --user 0 com.mitv.tvhome` and
`cmd package set-home-activity --user 0 com.mitv.tvhome/.MainActivityUserMode` over ADB.
Until an allowlist-identity build has been tried on this firmware, treat PatchWall as the
only HOME target.

### A second unit of the same model reports the opposite

Another owner of the same model and build (an `MT5879` board, `L55MA-SPL`, 4K panel)
had Projectivy (`com.spocky.projengmenu`) take Home after disabling Xiaomi TV Home,
Google TV Launcher and Xiaomi Settings `FallbackHome`, and it was still in front after
a power cycle. The two units differ in board and in what was disabled, so this note
records both accounts and calls neither settled.

### What else the same build does not let you keep

- No accessibility item anywhere in MiTV settings: `android.settings.ACCESSIBILITY_SETTINGS`
  and the `com.android.tv.settings/.oemlink.Accessibility*` activities the owner tried all
  left the OEM settings app in front.
- Settings written over ADB do not survive a cold start:
  `enabled_accessibility_services` came back `null` and `accessibility_enabled` `0`.
- `pm grant <pkg> android.permission.WRITE_SECURE_SETTINGS` reads back
  `granted=false` after a restart.

For this repo that means a key helper or a settings write on this firmware is a
session-length change: re-apply it after every boot, and do not count on an
accessibility helper surviving one.

### What this note leaves open

- The allowlist route was not tried on this build. It works on OS3.0.106 (ASTR0) and
  OS3.0.115 (MFFU0), and nothing here says it fails on OS3.0.28.
- Where the record came from is not established and that OTA is silent; a factory
  reset was not tried.
- Nothing here was measured with this repo's launcher: the unit ran a third-party home
  app.
