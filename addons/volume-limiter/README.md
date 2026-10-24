# Volume Limiter

An Android TV add-on that enforces a maximum volume limit on the TV's audio. When enabled, if anyone turns the volume up beyond the set limit, the volume is automatically pulled back down to the limit. Turning the volume down below the limit works completely normally and is never affected.

For the [Clean Your TV](../../README.md) launcher it declares a [module card](../../docs/modules.md): once you answer **Show card** its tile reads "Volume Limit" with a green accent glow. On any other Android TV launcher it behaves as a normal Leanback application.

![Volume Limiter on TV](screenshot.jpg)

## Features

- **Maximum volume cap**: Set a custom volume threshold (1 to 100, default: 10).
- **One-way enforcement**: Turning volume down below the limit is unaffected; only turning volume up beyond the limit triggers the rollback.
- **D-pad friendly TV UI**: Clean glass interface with slider progress bar and quick preset buttons.
- **Background persistence**: Runs as a lightweight background service that restores on boot.
- **No key filtering**: Does not consume or drop key repeat events on TV remotes.

## Permissions

| Permission | Why |
|---|---|
| `FOREGROUND_SERVICE` | Keeps the volume listener active in the background without being killed by Android. |
| `FOREGROUND_SERVICE_SPECIAL_USE` | Required on Android 14+ for foreground services monitoring system audio limit. |
| `RECEIVE_BOOT_COMPLETED` | Restores the volume limit guard automatically after the TV boots. |

No `INTERNET` permission: no network access, no telemetry, no analytics, no external libraries.

## Build and install

JDK 17 and Android SDK 34+. From the repo root, with ADB on:

```bash
cd addons/volume-limiter && ./gradlew assembleRelease && cd ../..
TV_HOST=<tv ip> tools/venv/bin/python tools/tv.py --push addons/volume-limiter/app/build/outputs/apk/release/app-release.apk /data/local/tmp/volume-limiter.apk
TV_HOST=<tv ip> tools/venv/bin/python tools/tv.py "pm install -r /data/local/tmp/volume-limiter.apk; rm -f /data/local/tmp/volume-limiter.apk"
```
