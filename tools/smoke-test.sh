#!/usr/bin/env bash
# End-to-end smoke test against the installed debug build on the Android emulator.
#
# Exercises what a store reviewer will touch: sign-in, the rider home, the tab
# bar, the location state, the driver dashboard, the Navigate control and the
# earnings page. Everything is driven through the WebView's DevTools protocol, so
# it tests the real app, not a browser.
#
# Credentials come from the environment:
#   KERB_RIDER_EMAIL / KERB_RIDER_PASSWORD
#   KERB_DRIVER_EMAIL / KERB_DRIVER_PASSWORD
#
# Usage: ./.toolchain/smoke-test.sh [--no-install]

set -uo pipefail
cd "$(dirname "$0")/.."

ADB="$PWD/.toolchain/sdk/platform-tools/adb"
NODE="$PWD/.toolchain/node/bin/node"
APK="android/app/build/outputs/apk/debug/app-debug.apk"
FAILED=0
STEP=0

wsurl() {
  curl -s --max-time 5 http://127.0.0.1:9222/json \
    | python3 -c 'import json,sys
ts=[t for t in json.load(sys.stdin) if t["type"]=="page"]
print(ts[0]["webSocketDebuggerUrl"] if ts else "")'
}

# cdp.mjs prints non-string results as JSON, so a JSON string comes back escaped.
# Unwrap one layer so assertions can compare against plain text.
ev() {
  "$NODE" .toolchain/cdp.mjs "$(wsurl)" eval "$1" | python3 -c '
import json, sys
raw = sys.stdin.read().strip()
try:
    value = json.loads(raw)
except Exception:
    print(raw)
else:
    print(value if isinstance(value, str) else json.dumps(value))
'
}

pass() { echo "PASS  $1"; }
fail() { echo "FAIL  $1"; echo "      got: ${2:0:200}"; FAILED=1; }

expect() { # label, needle, haystack
  case "$3" in *"$2"*) pass "$1" ;; *) fail "$1" "$3" ;; esac
}

step() { STEP=$((STEP + 1)); echo; echo "-- $STEP. $*"; }

attach() {
  local sock
  sock=$($ADB shell cat /proc/net/unix 2>/dev/null | grep -o 'webview_devtools_remote_[0-9]*' | head -1 | tr -d '\r')
  [ -n "$sock" ] || { fail "WebView devtools socket" "none"; return 1; }
  $ADB forward --remove-all >/dev/null 2>&1
  $ADB forward tcp:9222 localabstract:"$sock" >/dev/null
  sleep 1
}

# The site occasionally serves its own transient error page; a reload clears it.
ensure_page() { # path, marker
  local path=$1 marker=$2 body i
  for i in 1 2 3; do
    ev "location.replace('https://www.kerbgo.com$path?t=' + Date.now()); 'go'" >/dev/null
    sleep 9
    body=$(ev "JSON.stringify({marker: !!document.querySelector('$marker'), title: document.title})")
    case "$body" in *'"marker":true'*) echo "loaded $path"; return 0 ;; esac
    echo "retry $path ($body)"
    ev "caches.keys().then(function(k){return Promise.all(k.map(function(x){return caches.delete(x);}));}).then(function(){return navigator.serviceWorker.getRegistrations();}).then(function(r){return Promise.all(r.map(function(x){return x.unregister();}));})" >/dev/null
  done
  return 1
}

# Sign-in sometimes ends on Capacitor's bundled index.html while the session is
# already set, so fall back to opening the route directly.
settle() { # path, expected marker in JSON
  local path=$1 marker=$2 body i
  for i in 1 2; do
    body=$(ev "JSON.stringify({path: location.pathname, tabs: document.querySelectorAll('.kerb-tab').length, patch: !!window.__kerbRiderPatchInstalled})")
    case "$body" in *"$marker"*) echo "$body"; return 0 ;; esac
    echo "recovering to $path (was $body)" >&2
    ev "location.replace('https://www.kerbgo.com$path'); 'go'" >/dev/null
    sleep 12
  done
  ev "JSON.stringify({path: location.pathname, tabs: document.querySelectorAll('.kerb-tab').length, patch: !!window.__kerbRiderPatchInstalled})"
}

if [ "${1:-}" != "--no-install" ]; then
  step "Install the debug build"
  $ADB install -r "$APK" 2>&1 | tail -1
fi

step "Launch and attach"
$ADB shell am force-stop com.kerbgo.app
$ADB shell am start -n com.kerbgo.app/.MainActivity >/dev/null 2>&1
sleep 14
attach || exit 1
expect "app launched on the site" "kerbgo.com" "$(ev 'location.href')"

step "Sign in as the rider"
ensure_page /signin 'input[type=password]' || fail "rider sign-in page" "did not load"
ev "window.__smoke = {}; 'ok'" >/dev/null
"$NODE" .toolchain/cdp.mjs "$(wsurl)" login "${KERB_RIDER_EMAIL:-}" "${KERB_RIDER_PASSWORD:-}" >/dev/null
sleep 20
RIDER=$(settle /app '"path":"/app"')
expect "rider lands on /app" '"path":"/app"' "$RIDER"
expect "shell patch applied" '"patch":true' "$RIDER"
expect "tab bar has 3 tabs" '"tabs":3' "$RIDER"

step "Rider tab bar switches views in place"
ev "document.querySelector('.kerb-tab[data-kerb-tab=trips]').click(); 'ok'" >/dev/null
sleep 2
expect "Trips view shows recent trips" "Your trips" "$(ev 'document.body.innerText')"
ev "document.querySelector('.kerb-tab[data-kerb-tab=account]').click(); 'ok'" >/dev/null
sleep 2
ACCOUNT=$(ev "JSON.stringify({path: location.pathname, account: !!document.querySelector('.kerb-account, details')})")
expect "Account view stays on /app" '"path":"/app"' "$ACCOUNT"
ev "document.querySelector('.kerb-tab[data-kerb-tab=home]').click(); 'ok'" >/dev/null

step "Driver sign-in and dashboard"
ensure_page /signin 'input[type=password]' || fail "driver sign-in page" "did not load"
"$NODE" .toolchain/cdp.mjs "$(wsurl)" login "${KERB_DRIVER_EMAIL:-}" "${KERB_DRIVER_PASSWORD:-}" >/dev/null
sleep 22
DRIVER=$(settle /driver '"path":"/driver"')
expect "driver lands on /driver" '"path":"/driver"' "$DRIVER"
expect "driver shell patch applied" '"patch":true' "$DRIVER"
expect "driver tab bar has 3 tabs" '"tabs":3' "$DRIVER"

$ADB shell uiautomator dump /sdcard/smoke.xml >/dev/null 2>&1
UI=$($ADB shell cat /sdcard/smoke.xml 2>/dev/null)
expect "Navigate pill is on screen" 'text="Navigate"' "$UI"

step "Navigate chooser"
BOUNDS=$(echo "$UI" | python3 -c 'import re,sys
m=re.search(r"text=\"Navigate\"[^>]*bounds=\"\[(\d+),(\d+)\]\[(\d+),(\d+)\]\"", sys.stdin.read())
print("" if not m else "%d %d" % ((int(m.group(1))+int(m.group(3)))//2, (int(m.group(2))+int(m.group(4)))//2))')
if [ -n "$BOUNDS" ]; then
  $ADB shell input tap $BOUNDS >/dev/null
  sleep 2
  $ADB shell uiautomator dump /sdcard/smoke2.xml >/dev/null 2>&1
  CHOOSER=$($ADB shell cat /sdcard/smoke2.xml 2>/dev/null)
  expect "chooser offers the in-app map" "Kerb map (in app)" "$CHOOSER"
  expect "chooser offers Google Maps" "Google Maps" "$CHOOSER"
  expect "chooser offers Waze" "Waze" "$CHOOSER"
  $ADB shell input keyevent 4 >/dev/null 2>&1
  sleep 1
else
  fail "Navigate pill coordinates" "not found"
fi

step "Driver earnings route"
ev "document.querySelector('.kerb-tab[data-kerb-tab=earnings]').click(); 'ok'" >/dev/null
sleep 8
EARN=$(ev "JSON.stringify({path: location.pathname, text: document.body.innerText.slice(0,120)})")
expect "earnings tab opens /driver/earnings" '"/driver/earnings"' "$EARN"
expect "earnings page renders" "earnings" "$(echo "$EARN" | tr 'A-Z' 'a-z')"
expect "Navigate pill hidden off the dashboard" "true" "$($ADB shell uiautomator dump /sdcard/smoke3.xml >/dev/null 2>&1; $ADB shell cat /sdcard/smoke3.xml 2>/dev/null | grep -c 'text="Navigate"' | awk '{print ($1==0)?"true":"false"}')"

echo
if [ "$FAILED" -eq 0 ]; then
  echo "SMOKE TEST PASSED"
else
  echo "SMOKE TEST FAILED"
fi
exit "$FAILED"
