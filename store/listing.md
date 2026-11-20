# Kerb — store listing copy

Copy for both stores. Fields marked **[you]** need your details before submission.
Character limits are the stores' own; the copy below is inside them.
Phone screenshots are captured and filed in `store/screenshots/`, and the Play
feature graphic is in `store/graphics/`. The Screenshots section at the end lists
them and the shots that still need to be taken.

Submitting is documented separately: `store/submission-guide.md` (console fields,
build and upload commands), `store/compliance.md` (privacy, data safety, ratings,
rejection risks) and `store/review-notes.md` (paste-ready reviewer text).

Bundle / package ID: `com.kerbgo.app` · Version 1.0 (build 1) · Platform: iPhone, Android phone.

---

## Apple App Store

| Field | Limit | Copy |
|---|---|---|
| App name | 30 | `Kerb` |
| Subtitle | 30 | `Local rides, upfront fares` |
| Promotional text | 170 | `See the full fare before you book. No surge pricing, ever. Rides across the US up to 160 miles, with drivers who live where you live.` |
| Keywords | 100 | `rideshare,ride,taxi,local,driver,fare,airport,trip,booking,curb,grocery,delivery` |
| Primary category | — | Travel |
| Secondary category | — | Lifestyle |
| Support URL | — | `https://www.kerbgo.com/help` |
| Marketing URL | — | `https://www.kerbgo.com` |
| Privacy policy URL | — | `https://www.kerbgo.com/privacy` |
| Copyright | — | `Kerb` |

**Description**

```
Rides and groceries, right to your curb.

Kerb is a local rides service. Pick two places, see the fare before you book, and pay by card — no surprises at the end of the trip.

UPFRONT FARES
The price we quote is the price you pay. No demand multiplier, no time-of-day multiplier, no exceptions. Your fare is calculated from the rate card: base fare, per mile and per minute, shown in full before you confirm.

LOCAL DRIVERS
Drivers who live where you live. Every driver signs up, adds a vehicle, uploads their licence, registration and insurance, and is approved by a person before their first trip.

PAY BY CARD
Your card is authorised when you book and charged when the ride completes. Your card is saved to your account for next time — Kerb never stores your card details.

FOLLOW YOUR TRIP
Watch your driver approach and follow the trip live on the map, from pickup to dropoff.

COAST TO COAST
Kerb serves the entire United States. Trips run up to 160 miles, and each state line a trip crosses is added to the fare in full to the driver.

DRIVERS KEEP 75%
Drivers keep 75% of the fare, and the whole state-line amount. Sign up, get approved, go online, and offers arrive in the dashboard.

GROCERIES: COMING SOON
Grocery ordering is a waitlist today — join it in the app and we'll email you when it opens in your area.

Questions? Support is at kerbgo.com/help.
```

**App Review Information**

- Demo rider account: **[you]** email / password
- Demo driver account: **[you]** email / password (approved, with a vehicle, and a **pending offer already waiting** on the dashboard, so the offer, the trip card and the Navigate button are all reachable without booking anything first)
- Enter both accounts in App Store Connect and the Play Console only. Do not paste the passwords into this file or commit them to the repository.
- Notes: `Kerb is a native iOS app that presents kerbgo.com with native location, camera and navigation integration. Sign in with the demo rider account to book a ride; sign in with the demo driver account and accept the offer already waiting on the dashboard to reach the trip and the driver view. The "Navigate" control appears only on driver routes and offers Apple Maps, Google Maps, Waze, or Kerb's own map. Payment is by card for a physical transport service, processed on the web backend.`
- Contact: **[you]** name, phone, email

**App Privacy answers** (must match `ios/App/App/PrivacyInfo.xcprivacy`)

| Data type | Collected | Linked to you | Used for tracking |
|---|---|---|---|
| Name | Yes | Yes | No |
| Email address | Yes | Yes | No |
| Phone number | Yes (optional field) | Yes | No |
| Precise location | Yes | Yes | No |
| Coarse location | Yes | Yes | No |
| Payment info | Yes | Yes | No |
| Photos or videos | Yes (driver documents) | Yes | No |
| User ID | Yes | Yes | No |
| Purchase history | Yes (trips, fares) | Yes | No |

Purpose for every row: **App Functionality**. No data is used for tracking, so no App Tracking Transparency prompt is required.

**Age rating**: answer the questionnaire honestly. The app has location use and card payments, no user-generated content, no gambling, no mature content, no unrestricted web browsing outside kerbgo.com (only kerbgo.com stays in the app; other links open in the system browser).

**Export compliance**: already declared in `Info.plist` (`ITSAppUsesNonExemptEncryption = false`).

---

## Google Play

| Field | Limit | Copy |
|---|---|---|
| App name | 30 | `Kerb: Local Rides` |
| Short description | 80 | `Book local rides across the US with upfront fares. No surge pricing, ever.` (74) |
| Category | — | Maps & Navigation |
| Tags | — | rideshare, taxi, travel |
| Contact email | — | `support@kerbgo.com` |
| Website | — | `https://www.kerbgo.com` |
| Privacy policy | — | `https://www.kerbgo.com/privacy` |

**The contact email.** `support@kerbgo.com` is the domain's support mailbox, on
Google Workspace. The site does not render it yet: the help page, footer and
cancel-trip copy all read the `supportEmail` field in `/admin/branding`, which
still holds `kerb-221901fc@ctomail.io`, a cto.new disposable address. Change that
field to `support@kerbgo.com` before submitting — one admin edit, no deploy — so
this row, `store/review-notes.md` and every address on the site agree.

**Full description**

```
Rides and groceries, right to your curb.

Kerb is a local rides service across the United States. Pick two places, see the fare before you book, and pay by card — no surprises at the end of the trip.

UPFRONT FARES
The price you're quoted is the price you pay. No demand multiplier, no time-of-day multiplier, no exceptions. Fares come from a published rate card — base fare, per mile, per minute — shown in full before you confirm.

LOCAL DRIVERS
Drivers who live where you live. Every driver adds a vehicle and uploads their licence, registration and insurance, and a person reviews it before their first trip.

PAY BY CARD
Your card is authorised when you book and charged when the ride completes. Kerb never stores your card details.

FOLLOW YOUR TRIP
Watch your driver approach and follow the trip live on the map.

COAST TO COAST
Trips up to 160 miles, anywhere in the United States. Each state line crossed is added to the fare in full to the driver.

DRIVERS KEEP 75%
Drivers keep 75% of the fare, plus the whole state-line amount.

GROCERIES: COMING SOON
Grocery ordering is a waitlist today — join it in the app and we'll email you when it opens near you.

Support: kerbgo.com/help
```

**App access** (required — the app needs a login)

Provide the same demo rider and driver credentials as for Apple, with the note that reviewers should sign in to reach the booking and driver screens.

**Data safety answers** (must match the iOS manifest)

- Does your app collect or share required user data types? **Yes, collects**
- Data types: Name, Email address, Phone number, Precise location, Approximate location, Payment info, Photos, User IDs, Purchase history
- Shared with third parties? **No** (payment processing by the payment provider is service-provider processing, not "sharing" under Play's definition — confirm with your payment provider's terms)
- Processing is ephemeral? **No**
- Required or optional? Name, email, location and payment are required for the service; phone number is optional
- Users can request deletion? **Yes** — by email today, as the policy states.
  Play's Data deletion URL becomes `https://www.kerbgo.com/delete-account`
  (`site-changes.md` §1) once it answers 200; it is a 404 today, so leave the
  field empty until then rather than shipping a dead link
- Data encrypted in transit? **Yes** (HTTPS only; the app disables cleartext traffic)

**Content rating**: complete the IARC questionnaire. Expect a low rating (no violence, no sexual content, no profanity, no gambling, no user-generated content). Location sharing and card payments are declared separately, not through the rating.

**Other declarations**: no ads; app is not a news app; not COVID-related; not a government app; no financial-services features (rides are a transport service paid for by card, not banking, lending or investment).

---

## Screenshots — captured

Six phone screenshots were taken from the Android debug build of `com.kerbgo.app`
running on the Android emulator, signed in to the rider and driver accounts, then
flattened to 24-bit PNG and cropped only by scroll position (nothing is mocked or
recomposed). They are `store/screenshots/`, and they are committed — the raw
`adb` captures went to `store/shots/`, which is git-ignored and empty, so the
committed PNGs are the only copies:

| # | Scene | File | Pixels |
|---|---|---|---|
| 1 | Fare before you book — ride types and prices for a 1.5 mi trip | `01-fare-types.png` | 1080 × 2160 |
| 2 | Fare before you book — itemised estimate and the Book ride button | `02-fare-breakdown.png` | 1080 × 2160 |
| 3 | Sign-in | `03-signin.png` | 1080 × 2160 |
| 4 | Driver dashboard — approved, offline, week and all-time earnings, Navigate pill, tab bar | `04-driver-home.png` | 1080 × 2160 |
| 5 | Driver Navigate sheet — Kerb map (in app), Google Maps, Waze | `05-driver-navigate.png` | 1080 × 2160 |
| 6 | Driver earnings — 75% share, still owed, paid so far | `06-driver-earnings.png` | 1080 × 1320 |

All six are 24-bit PNG with no alpha, which is what Google Play requires. Five of
them are exactly 9:16; the earnings shot is shorter on purpose, cropped just below
the earnings card and just above the "Your trips" list so the driver's email
address and the customer addresses on past trips stay out of the image.

The fare shots use two public streets in Auburn (West Magnolia Avenue as the
pickup, East Thach Avenue as the destination), not anybody's home address. The
masked card (`Visa •••• 8209`) is the app's own saved-payment display; if you would
rather not show a real card's last four digits in public, save a test card to the
demo account and recapture shots 1 and 2.

**How they were taken** (repeatable, no Xcode needed):

1. Emulator at 1080 × 2160 — `adb shell wm size 1080x2160`, battery level pinned to 100.
2. Installed `android/app/build/outputs/apk/debug/app-debug.apk`, granted location,
   `adb emu geo fix -85.4808 32.6099` so the rider home is not showing "Location off".
3. Signed in as rider, opened Book a ride, chose the two streets, captured with
   `adb exec-out screencap -p > raw.png`.
4. Signed in as driver, captured the dashboard, tapped Navigate for the sheet, then
   the Earnings tab.
5. Flattened each capture to 24-bit PNG with sharp (`.flatten({background:'#ffffff'})`).

### Still to capture

| Scene | Why it is not here |
|---|---|
| Live trip — driver approaching / trip in progress | Needs a real booking accepted by a driver; no trip was active during capture. |
| Driver — incoming offer | No longer blocked, just not captured: the demo driver holds a standing offer (`site-changes.md` §4), so any session signed in as that account can photograph the card and accept it. Waiting on that site change being deployed. |
| The full Apple set | The app now runs and is screenshotted on an iPhone simulator in CI (`.github/workflows/ios-simulator.yml`), but the job cannot sign in, so its only capture is the landing page at 1206 × 2622. The five app screens still need a session: re-run that job with a demo account signed in, or capture on a real iPhone. Sizes: `1206 × 2622` or `1179 × 2556` (or `1320 × 2868` / `1290 × 2796` / `1260 × 2736`). |
| 13-inch iPad set | Not required: `TARGETED_DEVICE_FAMILY = 1` in both build configurations. |

## Assets already generated

| Store | Asset | File |
|---|---|---|
| Apple | 1024×1024 app icon (no alpha) | `ios/App/App/Assets.xcassets/AppIcon.appiconset/` |
| Google Play | 512×512 listing icon, 32-bit PNG with alpha | `assets/play-store-icon-512.png` — regenerate with `npm run icon:play` |
| Both | Launch splash | `ios/App/App/Assets.xcassets/Splash.imageset/`, `android/app/src/main/res/drawable*/splash.png` |
| Google Play | 6 phone screenshots (1080 wide, no alpha) | `store/screenshots/01`–`06` (see above) |
| Google Play | Feature graphic 1024 × 500 | `store/graphics/feature-graphic-1024x500.png` — icon, wordmark and tagline on the brand gradient; 24-bit, no alpha. Regenerate with `tools/feature-graphic.mjs` |

## Before you press submit

1. Fill in the **[you]** fields: reviewer accounts (in the consoles, not here),
   support contact name/phone/email.
2. Capture the Apple screenshot set on an iPhone or a signed-in simulator (the six
   scenes above are the intended order; the first five map straight onto iOS).
3. Confirm the iPhone-only setting (`TARGETED_DEVICE_FAMILY = 1`, already in the
   project) so no iPad set is required.
4. Close the blocking items in `store/screenshot-checklist.md`: in-app account
   deletion, the demo state (accounts, saved place, past trip, standing offer,
   `KERBREVIEW`) and `data-kerb-destination` on the driver trip card. The iPad set
   is not needed.
5. Capture the incoming-offer shot once the standing demo offer is live
   (`site-changes.md` §4) — it is the strongest driver-side image and needs no
   second session. The live-trip shot still needs a real booking accepted by a
   driver, so it is optional.
