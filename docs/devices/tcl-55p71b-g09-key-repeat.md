# TCL 55P71B Pro (G09, Google TV 12): a bound key helper costs remote key auto-repeat

Owner report: [#763](https://github.com/Gathin23/cleanyourtv/issues/763). One unit, one session: TCL
55P71B Pro "Smart TV Pro", device `G09`, Android 12 (Google TV), firmware `STT2.230203.001.AS23`,
Amlogic.

## The symptom

Hold volume up or down and the volume moves one step, on the home screen and inside an app. Pressing
the button that many times gives that many steps. The remote is not necessarily the problem and neither
is the debloat: the same remote ramps normally again as soon as no key-filtering accessibility service is
bound, and where the repeats go is one `getevent -lt` away - if the kernel still prints them while the
button is held, they are being dropped above the driver.

## The bisect

The owner enabled the launcher's two optional helpers one at a time and then together:

| `enabled_accessibility_services` | holding volume up/down |
| --- | --- |
| both `HomeKeyService` and `InputKeyService` | one step per press |
| `HomeKeyService` alone | one step per press |
| `InputKeyService` alone | one step per press |
| `""`, with `accessibility_enabled 0` | ramps continuously |

All or nothing. This is not one helper mishandling `VOLUME_UP`: the dump while broken lists both as
`Service[label=Home button, capabilities=8]` and `Service[label=Home, capabilities=8]`, and capability
8 is `CAPABILITY_CAN_FILTER_KEY_EVENTS`. Any bound service that asks to filter key events puts this
firmware's input path in a mode where the first press arrives and the repeats that follow are dropped.

Read the state without changing anything:

```sh
tools/tv.py 'settings get secure enabled_accessibility_services'
tools/tv.py 'settings get secure accessibility_enabled'
tools/tv.py 'dumpsys accessibility'
```

To get the repeat back for a test, and to put the helpers back afterwards:

```sh
# keep the string the first read printed; the empty list also unbinds TalkBack and any other service
tools/tv.py "settings put secure enabled_accessibility_services ''; settings put secure accessibility_enabled 0"
tools/tv.py "settings put secure enabled_accessibility_services com.gathin.tvhome/com.gathin.tvhome.HomeKeyService:com.gathin.tvhome/com.gathin.tvhome.InputKeyService; settings put secure accessibility_enabled 1"
```

That second line is the launcher's two helpers, not "the value": write back what the read printed.
`theme.sh` already re-puts the list after a theme push, `am force-stop com.gathin.tvhome` drops it on
its own (see [TROUBLESHOOTING](../TROUBLESHOOTING.md)), and re-running `launcher/build-and-install.sh`
without `INPUT_KEY_SERVICE=1`/`HOME_KEY_SERVICE=1` leaves the helpers off.

## The launcher is not the one eating the repeats

Both helpers return `false` for keys they do not map, so a volume key never reaches launcher code and
is not consumed there: `InputKeyService.onKeyEvent` passes anything that is not in `app_keys`,
`input_key_keycodes`, `KEYCODE_TV` or `KEYCODE_TV_INPUT`, and `HomeKeyService` passes everything except
`KEYCODE_ALL_APPS` and the settings keycodes/scancodes. The repeat is lost before the app sees the
event. What the report does not contain is an `onKeyEvent` line for a held volume key, so "the
platform's filter drops the repeat" is the reporter's reading of the bisect, not a captured log. A
future report that logs the repeat event arriving at a helper and being passed through would move the
blame back into the launcher.

## What the helpers buy, and what this costs

The buttons a helper exists for on this remote:

| Button | What arrives | Helper |
| --- | --- | --- |
| Gear (TV settings) | `KEYCODE_NOTIFICATION` 83, scancode 561 | `settings_key_keycodes` / `settings_key_scancodes`, `HOME_KEY_SERVICE=1` |
| Netflix | vendor keycode 4062, scancode 472 | `app_keys`, `INPUT_KEY_SERVICE=1` |
| Prime Video | vendor keycode 4099 | `app_keys` |
| YouTube | vendor keycode 4065 | `app_keys` |
| Input | `KEYCODE_TV` / `KEYCODE_TV_INPUT` | `InputKeyService`, and TCL draws that panel itself on this set |

Vendor keycodes reach nothing on their own: `com.tcl.ttvs`'s `KeyDownBehaviorReceiver` logs
`keyString=4062 keyCode=4062 type=IR` and acts on nothing. These buttons were answered by the stock
home, and the [G09 example list](../../examples/tcl-g09-google-tv/debloat.txt) disables launcherx, so
after the cleanup only a helper can answer them. That is where the trade comes from: on this firmware
the buttons and the volume ramp cannot both be had, because both need the same filter capability.

## The same trade on a Xiaomi MediaTek set

A second owner reported it on [#944](https://github.com/Gathin23/cleanyourtv/issues/944): Xiaomi
MiTV-MOSR4 (`irobot`, MediaTek), Google TV / Android 11 (SDK 30), firmware `RTM5.220609.003.1265`.
Holding volume up with `InputKeyService` and `HomeKeyService` bound moves it one step, and
`InputKeyService` alone is still one step; `dumpsys accessibility` lists the bound service with
`capabilities=8`. Same symptom, same capability, second vendor and second Android version.

Two things differ from the bisect above, and both are worth keeping. The empty-list control was not run:
on this set clearing the list unhooks the buttons the helpers were installed for - with launcherx
disabled, its Input, Settings, YouTube and Netflix buttons reach only
`com.xiaomi.android.tvsetup.partnercustomizer/.GlobalKeyReceiver`, which starts launcherx activities and
crashes (the [MOSR4 note](xiaomi-mitv-mosr4.md) has that part, and its install line passes both helpers).
So "the ramp returns with no service bound" is still measured on the G09 alone. And the build string in
the issue is not the one that note carries (`RTM5.220609.003/1585`, written on another unit of the same
model), so treat the two as separate units until both fingerprints are read: the model, the firmware
family and the behaviour are what they agree on.

That is still enough to unhook the mechanism from "this firmware": the volume keys take the early
question from the window policy on Android 11 and 12, on two vendors, whenever a bound service holds
`CAPABILITY_CAN_FILTER_KEY_EVENTS`. The trade and the choices below are the same on both sets. The
reporter's own suggestion - a helper that synthesizes the repeat with `adjustSuggestedStreamVolume` while
a volume key is held - is launcher code, and an off-by-default switch at best; it is not in this note.

## Where the repeat goes on a Hisense set with a CEC soundbar

A third set, with a log of the held key: Hisense 75U75N (`hengshan`, MediaTek), Google TV / Android 12,
firmware `STT2.230310.002`, US market, one unit, one evening. Sound goes over HDMI eARC to a soundbar with
HDMI-CEC system audio on, so each volume step leaves the TV as a CEC `<User Control Pressed>` volume key.

| `enabled_accessibility_services` | holding volume up |
| --- | --- |
| `HomeKeyService` alone (`capabilities=8` in `dumpsys accessibility`) | one step per press |
| `""`, with `accessibility_enabled 0` | ramps continuously |

`InputKeyService` was not tried on this set. With the helper bound, `logcat` for one press held for 2.7 s:

- `InputDispatcher: notifyKey` shows the down and, 2.7 s later, the up, and nothing between. The remote sends
  no repeats of its own; the dispatcher makes them while a key it has dispatched stays down.
- `WindowManager: interceptKeyTi keyCode=24 down=true` comes twice, the first time with
  `interceptDelayTime=144`, the second 145 ms later.
- `HdmiCecLocalDeviceTv` then sends one volume step, and the CEC history in `dumpsys hdmi_control` holds one
  `<User Control Pressed> 05:44:41` and its release for the whole hold.

That pattern fits Android 12's accessibility key filter: with a service asking to filter keys, volume keys
go to the window policy's `interceptKeyBeforeDispatching` before any service sees them, and the second call is
that check retried after the delay it asked for. On this firmware the policy answers by sending the CEC
step itself and reporting the key consumed, so the event ends inside the filter. No service receives it, and
the dispatcher never gets the held key it would make repeats from. A test build whose helper stepped the
volume itself while a volume key was held sent no second CEC step, so on this set the helper never saw the
key either. That is a log of the framework's side, read against the AOSP filter; it still has no
`onKeyEvent` line from a helper, which is what the "launcher is not the one eating the repeats" section asks
for.

What the helpers would buy on this set, from `dumpsys window policy` and `logcat`:

| Button | What arrives | Without a helper |
| --- | --- | --- |
| Gear | `KEYCODE_NOTIFICATION` 83, scancode 598 | a global key for launcherx's `GlobalKeyListener_Receiver`, so nothing once it is disabled |
| YouTube | `KEYCODE_BUTTON_3` 190 | the same global key route |
| Prime Video, Disney+, Tubi | 295, 607, 606 | the policy consumes them and Hisense's `com.vt.tv.smartsense` asks launcherx to open the app with `com.google.global_button.ACTION_LAUNCH_APP`, addressed to launcherx; another app registered for that action received nothing |
| Netflix, Input | | work, through Hisense and MediaTek receivers that stay enabled |

Keeping launcherx enabled while another launcher holds Home did not work here: re-enabled, it took Home back
(`cmd package resolve-activity` named it although `android.app.role.HOME` was given to the new launcher), and
the shell may not disable its home activity alone (`SecurityException` from `pm disable-user` on the
component). The owner kept the ramp and dropped the five buttons.

## A second Hisense set, without a soundbar

A Hisense SmartTV 4K FFM (`songshan`, MediaTek, Android 11, `RTT2.220118.001`, the second unit in
[`examples/hisense-smarttv-4k-ffm-android11`](../../examples/hisense-smarttv-4k-ffm-android11/debloat.txt))
does the same with `InputKeyService` alone bound: held volume up arrives as one press, and the firmware's
own key log (`WindowManager: interceptKeyTi keyCode=24 down=true repeatCount=0`) shows no repeat after
it. The empty-list control was not run there either. The owner kept the helper for the YouTube, Prime
Video, Disney+, Peacock and gear buttons and lives with one step per press.

## Choices

- **Keep the repeat and skip the helpers.** Do not pass `INPUT_KEY_SERVICE=1` or `HOME_KEY_SERVICE=1`
  when installing. The price is the gear button and the remote's app buttons: see the
  [G09 example](../../examples/tcl-g09-google-tv/README.md), whose install line passes
  `INPUT_KEY_SERVICE=1` for exactly those buttons, so a set that behaves like this one pays for them
  with the volume ramp. The Input button keeps working there without any helper.
- **Keep the buttons and accept stepwise volume.** The volume keys still change the volume, one step
  per press; the launcher cannot make the platform ramp it again. Setting volume from a phone app or
  through an AVR/soundbar remote was not compared on this set.
- **Keep launcherx enabled and bind no helper.** The stock home answers those buttons, so the repeat
  survives. This is the only configuration that has both, and the price is the stock home screen the
  cleanup was replacing.

- **Keep both with a listener of your own.** A script run by the `shell` user can answer the app
  buttons without any service bound, so the ramp stays; see "Both at once" below. It needs network ADB left
  on to start itself after a reboot, which is a real cost.

It is a toggle, not a fix: two one-line `settings` writes switch between the two behaviours whenever
you want, but never both at once.

## Both at once: a listener on the TV, started at boot

A unit with the same product, `G09_4K_GB`, and the same build, `STT2.230203.001.AS23` (Android 12, security
patch 2025-09-05), and a Bluetooth remote that names itself `BT_RC813A_B4`, had the three app buttons and the
volume ramp together, with no accessibility service bound. That does not contradict the bisect above: it is
about services that filter keys, and this uses none. One owner, one unit, and the only checks were pressing
the real Netflix, Prime Video and YouTube buttons, holding volume up and down, and rebooting once.

**What reads the buttons.** With `enabled_accessibility_services` empty and `accessibility_enabled 0` (the
control in the bisect), a script run by the `shell` user reads the remote's consumer-control input node with
`getevent` and starts an app when one of three keys goes down. The keys are the scancodes in the table above,
472, 473 and 569, which `getevent` prints as `0001 01d8`, `0001 01d9` and `0001 0239`. The node is found by
name, not by `/dev/input/event<N>`, as the [TROUBLESHOOTING](../TROUBLESHOOTING.md) section on remapping the
buttons without a key helper explains, along with the limit that a reader cannot consume a press. Here that
costs nothing visible only because these vendor keycodes reach nothing else once launcherx is disabled
(`KeyDownBehaviorReceiver` above); that is read from this note, not re-measured with the script. The script
that ran, with its log lines dropped:

```sh
#!/system/bin/sh
NAME='BT_RC813A_B4 Consumer Control'   # the node's name in `getevent -p`
echo $$ > /data/local/tmp/keys.pid
find_dev() { getevent -p 2>/dev/null | grep -B1 "name: .*\"$NAME\"" | head -1 | sed 's/.*: //'; }
open_app() { am start -a android.intent.action.MAIN -c android.intent.category.LEANBACK_LAUNCHER -n "$1" >/dev/null 2>&1; }
while true; do
  DEV=$(find_dev)
  if [ -z "$DEV" ]; then sleep 5; continue; fi
  getevent "$DEV" 2>/dev/null | while read -r line; do
    case "$line" in
      *"0001 01d8 00000001"*) open_app com.netflix.ninja/.MainActivity ;;
      *"0001 01d9 00000001"*) open_app com.amazon.amazonvideo.livingroom/com.amazon.ignition.IgnitionActivity ;;
      *"0001 0239 00000001"*) open_app com.google.android.youtube.tv/com.google.android.apps.youtube.tv.activity.ShellActivity ;;
    esac
  done
  sleep 2
done
```

It is started detached from the debugging session, or it dies with it:
`setsid nohup sh /data/local/tmp/keys.sh > /dev/null 2>&1 < /dev/null &`. The loop waits for the remote when it
is not connected and reads again when it comes back.

**What starts it again after a reboot.** Nothing on the TV does by itself, which is the gap the
[TROUBLESHOOTING](../TROUBLESHOOTING.md) section above leaves open. This unit closed it with a small app of the
owner's own: on `BOOT_COMPLETED` it opens a TCP ADB connection to `127.0.0.1:5555` (the `dadb` library, a
plain-Java ADB client), signs in with a key it generated for itself, checks the pid file and starts the script if
it is not running. The first run shows the TV's "Allow USB debugging" prompt for that key once, answered with
"Always allow". After a reboot started over ADB, the port was back and the listener was running about 30
seconds later, with no computer involved. The app needs `RECEIVE_BOOT_COMPLETED` and `INTERNET` (for the
loopback socket), and its install also ran `appops set <package> AUTO_START allow`, as the sleep-timer add-on
does for TCL's boot manager; whether the receiver needs that was not tried without it. Its source is about 80
lines and is not part of this change.

**This needs network ADB on, permanently.** The listener comes back after a reboot only because `adbd` still
listens on port 5555 and the app can reach it. That is the setting the closing step in
[README](../../README.md) turns off, and the reason the maintainer asked for a reboot recipe
built on it to be dropped from the section above, so read this as a trade, not a recommendation:

- A connection needs a key the TV has accepted. A device that knows the TV's address and has no accepted key
  gets no shell, so having port 5555 reachable is not the same as being open. It is not zero risk either: a
  bug in `adbd` on a firmware that is behind on patches, or a prompt answered by mistake, or a copy of an
  accepted private key (the app's own, or the computer's), each gives a shell on the TV to anything that can reach the port.
- Never accept a debugging prompt that appears on the screen and that you did not start, and do not forward
  port 5555 on the router.
- To undo it all: uninstall the app, `kill $(cat /data/local/tmp/keys.pid)`, delete the script and the pid
  file, then turn network debugging off and revoke the USB debugging authorisations in Developer options,
  which is the README's closing step.

Not covered: the Gear button (that stays with a helper, and so with the lost repeat), the buttons the system
answers itself, a cold boot after power loss (the reboot was started over ADB), standby, a second remote, and
whether any firmware other than this build lets the `shell` user read the remote's node.

## Limits

One unit, one firmware build (`STT2.230203.001.AS23`), one session. Only volume up/down was held:
channel up/down, a held D-pad (seek) and the media keys were not tried, so "auto-repeat" here means the
key that was measured. Whether a filter service that is not the launcher's (TalkBack, a remote app)
does the same is untested; the capability in the dump is the only common factor seen. No cold boot, no
second remote. The two other G09 units in this repo (UK `G09_4K_GB` AS24 in Brazil, and
`G09_4K_GB_32BIT` AS10) never looked at this, so the claim belongs to the build, not to "G09". No
launcher-side fix is offered, and none should be expected: an app can decline the key - these two
already do - but cannot hand a repeat that the platform dropped back to the input stack, and putting
`VOLUME_UP` in `app_keys` would open an app rather than ramp the volume.
