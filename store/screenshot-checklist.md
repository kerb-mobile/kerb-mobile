# Kerb — screenshot checklist

Screenshots must be captured from the real app, on a real device or simulator,
signed in with demo data. Do not use the website in a desktop browser, and do not
mock up screens.

## Apple App Store

**Required:** at least one screenshot for **iPhone with Dynamic Island (medium
display)** — the 6.3-inch class (iPhone 16 Pro / 17 / 17 Pro).

| Item | Value |
|---|---|
| Portrait size | `1206 × 2622` or `1179 × 2556` px |
| Landscape size | same numbers reversed |
| Count | 1–10 per size |
| Format | `.png` / `.jpg` / `.jpeg`, **no transparency** |
| Larger (optional) | `1320 × 2868`, `1290 × 2796`, `1260 × 2736` |

**iPad:** not needed. The project is already iPhone-only
(`TARGETED_DEVICE_FAMILY = 1` in both build configurations), so App Store
Connect accepts an iPhone set on its own.

**The Android PNGs cannot be reused here.** The six images in `store/screenshots/`
are `1080 × 2160` (and `1080 × 1320`), which App Store Connect does not accept at
any iPhone size — the slots are fixed pixel sizes and 1080 × 2160 matches none of
them. The set to capture is the one required row above: **`1206 × 2622`**, or
`1179 × 2556`, on an iPhone 17 / 17 Pro / 18 Pro simulator, no transparency and no
device frame. The larger sizes are optional extras that App Store Connect scales
down from this set — they are not a substitute for it, and uploading only a
`1290 × 2796` set is the mistake older guides still recommend. `ios-simulator.yml`
already installs, launches and screenshots the app on an iPhone, and the device it
picks is the right one: it prefers **iPhone 17 Pro**, then iPhone 16 Pro, 17 and 16
— the first two are the medium-display class whose captures are `1206 × 2622`, and
the step prints the real dimensions with `sips` before uploading. What its
artifacts do **not** cover is content: it shoots two states only, launch and the
loaded home screen. The six-screen set above still needs the rider and driver
flows walked through by hand. As of 20 Nov 2026 there are **zero** captures at a
valid Apple size. (Checked against Apple's screenshot specifications on 20 Nov
2026, when the required slot was the medium Dynamic Island display.)

Shortcuts are not set on the app, so no shortcut screenshots are needed.

## Google Play

| Item | Value |
|---|---|
| Phone screenshots | 2–8 required to publish |
| Sizes | each side 320–3840 px, long side ≤ 2× short side |
| Recommended | `1080 × 1920` (9:16) — also qualifies for Play's larger promo layouts with 4+ |
| Format | JPEG or 24-bit PNG, **no alpha** |
| Feature graphic | `1024 × 500`, JPEG/24-bit PNG, no alpha — **required** |
| Listing icon | `512 × 512` 32-bit PNG ≤ 1024 KB — already at `assets/play-store-icon-512.png` |

## Which screens to shoot (in this order)

1. **Fare before you book** — pickup, destination and the full fare breakdown. This is the product's promise; lead with it.
2. **Live trip** — driver approaching / trip in progress on the map.
3. **Sign-in and account** — shows it is a real account app, not a brochure.
4. **Driver: incoming offer** — the driver side that riders never see.
5. **Driver: trip card with the Navigate sheet open** — Apple Maps / Google Maps / Waze / Kerb map. This is the clearest native feature in the app.
6. **Driver: earnings** — 75% keep, state-line amounts.

Use a demo rider named something neutral ("Demo Rider"), not a real customer.
Keep prices plausible but not absurd. If grocery is still a waitlist, do not
screenshot it as if it were live.

## Capture

**iPhone** (device or simulator):
- Device: press Side + Volume Up; files land in Photos, AirDrop them to the Mac.
- Simulator: `xcrun simctl io booted screenshot shot.png` (needs Xcode — not on
  this Mac, so use the CI Mac or your phone).

**Android** (this machine already has `adb` in the project toolchain):
```bash
.toolchain/sdk/platform-tools/adb exec-out screencap -p > store/shots/01-fare.png
```
Then resize to 1080×1920 without alpha, e.g.:
```bash
node -e "require('sharp')('store/shots/01-fare.png').resize(1080,1920,{fit:'cover'}).flatten({background:'#0B0B0C'}).png().toFile('store/shots/01-fare-play.png')"
```

## Rules that get submissions rejected

- No other platform's UI or branding in screenshots (no Android status bar in iOS shots, and vice versa).
- No "download now", "coming soon", price promotions, or App Store/Play badges.
- No misleading content: everything shown must exist in the build you submit, in the same state.
- Status bar tidy: full signal and battery, and on iOS 9:41 is conventional.
- No real personal data, real phone numbers or real card numbers — Apple rejects these and it is a privacy problem.
- Apple: no transparency in the PNG, and do not include the device frame as part of the required size.
- Google: feature graphic must not be a screenshot with text over the icon.

## Blocking items before you submit

1. **In-app account deletion.** Apple guideline 5.1.1(v) requires an app that lets
   people create an account to let them delete it in the app. The route is
   `kerbgo.com/delete-account`, reached from the Account tab; it returns 404 today
   and is specified, with the entry-point markup the shell already reads, in
   `site-changes.md` §1. There is no native deletion flow — site work only, no app
   release.
2. **Demo state, not just two accounts.** A rider with a saved place and a past
   trip, an approved driver with a vehicle and an offer always waiting on
   `/driver`, and promo code `KERBREVIEW` so the booking path costs nothing. The
   full spec is in `review-notes.md`; the standing offer it depends on is
   `site-changes.md` §4, and the promo code is `site-changes.md` §6.
3. **Driver destination attribute** — `data-kerb-destination` on the driver trip
   card (`site-changes.md` §2), otherwise the Navigate button reports that it
   cannot read the destination (it will not guess).
Not on this list, and recorded only so it is not mistaken for a blocker: the iPad
set. The app is iPhone-only (`TARGETED_DEVICE_FAMILY = 1`), so App Store Connect
accepts an iPhone set on its own and nothing needs capturing for iPad.
