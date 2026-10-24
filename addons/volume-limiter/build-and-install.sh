#!/usr/bin/env bash
# Build Volume Limiter and install it on the TV.
#
# Usage: TV_HOST=192.168.1.50 addons/volume-limiter/build-and-install.sh
#        TV_HOST=192.168.1.50 addons/volume-limiter/build-and-install.sh --uninstall
set -euo pipefail
HERE="$(cd "$(dirname "$0")" && pwd)"
TOOLS="$HERE/../../tools"
: "${TV_HOST:?set TV_HOST to the TV IP address, e.g. TV_HOST=192.168.1.50 $0}"
PY="$TOOLS/venv/bin/python"
[ -x "$PY" ] || PY="$TOOLS/venv/Scripts/python.exe"
[ -x "$PY" ] || { echo "run: python3 -m venv tools/venv && tools/venv/bin/pip install -r tools/requirements.txt" >&2; exit 1; }
export MSYS_NO_PATHCONV=1
PKG=io.github.hinzdc.volumelimiter
tv() { "$PY" -I "$TOOLS/tv.py" "$@"; }

if [ "${1:-}" = --uninstall ]; then
  tv "pm uninstall $PKG"
  echo "Uninstalled Volume Limiter."
  exit 0
fi

(cd "$HERE" && ./gradlew assembleRelease -q) || { echo "build failed" >&2; exit 1; }
tv --push "$HERE/app/build/outputs/apk/release/app-release.apk" /data/local/tmp/volume-limiter.apk
tv "pm install -r /data/local/tmp/volume-limiter.apk && rm -f /data/local/tmp/volume-limiter.apk"
tv "am start -n $PKG/.MainActivity"
echo "Volume Limiter installed and launched."
