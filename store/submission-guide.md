# Submission guide

Everything needed to create the two app records, build the two uploads and answer
the review questions. Written to be followed top to bottom on submission day.

**Status 19 Nov 2026:** the Android upload exists and is signed —
`android/app/build/outputs/bundle/release/app-release.aab`, signed with the
upload key created that day. The iOS archive and upload run from GitHub Actions
(`.github/workflows/ios-release.yml`), so no Mac with Xcode is needed — but the
Apple Developer Program membership, the six repository secrets and the portal
steps below are still outstanding. Two policy gaps (account deletion, "no app-store download"
copy on the landing page) are real rejection risks and are described in
`compliance.md`.

Those five documents plus this one are the whole submission kit. The last row
is the move itself, and no store needs it:

| File | Use |
|---|---|
| `store/listing.md` | Store copy: names, descriptions, keywords, screenshots inventory |
| `store/compliance.md` | Privacy answers, data safety, ratings, rejection risks |
| `store/review-notes.md` | The text to paste into App Review notes and Play App access |
| `store/site-changes.md` | The site-side prerequisites for review: in-app account deletion, the driver dropoff attribute, the landing copy, a standing demo offer, the `KERBREVIEW` promo code, and the retailer change that is no store's prerequisite. **None of it has landed yet** |
| `store/site-handover.md` | One page for whoever holds the cto.new access: the five site changes and the support-email field, each with the check that proves it landed |
| `store/submission-guide.md` | This file: console fields, build commands, order of work |
| `store/site-migration.md` | Not needed for submission: the plan for moving the site off cto.new, and what its payment, hashing, email and image code needs first. Do not start it during a review |

## One app for riders and drivers — decided 19 Nov 2026

The submission is **one binary and one listing per store**, serving rider and
driver from the same shell. The merchant app is separate work, not a third tab
(see below).

Why: `/app` and `/driver` are two URLs on the same site behind one session, and
`www/shell-tweak.js` already splits the tabs by account type, so a second binary
adds no functionality — only a second wrapper over the same website. Several
listings wrapping one site is the shape App Review rejects under 4.3 (duplicate
apps), it multiplies the 4.2 minimum-functionality exposure, and it triples the
Play 12-tester/14-day gates, the screenshot sets and the rebuilds for every
change to `www/`. A merchant console shipped as a plain wrapper would be the
worst case of all: a dashboard in a WebView is the weakest possible 4.2 argument,
and Play scrutinises business-only apps.

**Merchant app — later, with its own listing.** What a shop needs from an app is
the loud alert, not the dashboard. A new order has to reach someone on the floor
the moment it arrives, or the food is not started and the prep clock is already
running. That is the app's reason to exist, and no phone browser can do it: a tab
gets no notification once it is closed or the phone is locked, and on iOS a
suspended WebView cannot alert at all. So this app is built around native
APNs/FCM rather than wrapped around the portal, and the alert is the part to get
right first.

The alert to design against:

- Fires on the order landing, within seconds, with the app closed and the phone
  locked — not just while the Orders tab is open.
- Audible at full volume with its own loud sound, not the default notification
  chime, and repeats or escalates until someone deals with it.
- Gets through Do Not Disturb and silent mode where the platform allows it. On
  iOS that means Apple's critical-alerts entitlement, which is requested and
  granted separately — apply early, it is not automatic.
- Leaves a visible trace either way: lock-screen banner and an unread badge, so a
  missed sound is still an unread order.
- Opens straight onto the new order, with its prep time and items, ready to mark
  ready for pickup.

The portal is what a shop uses in a phone browser today, and it does not fill
this gap: an order arrives silently in the Orders tab, so it is seen only if
someone happens to be looking. Checked 19 Nov 2026 with a test shopfront: an order
simply arrives — there is no accept or decline step — and the only action on it is
"mark ready for pickup", which the courier sees on arrival. The portal also has a
Store & items tab (open/closed, prep time, address, item form), but both the item
and the address writes were returning `503 Upstream unavailable`, so the order loop
has never been seen end to end — no order was placed, and mark-ready is untested
against a live order.

**Shops should be able to decline — but that is store-side work, not an app
feature.** An order is currently auto-accepted: it appears in the portal and the
only action is mark ready. That leaves a shop no way out of an order it cannot
fill — closed early, out of the item, already at capacity — so the order sits, a
courier is dispatched and the customer waits for food nobody is cooking. Every
order-taking platform lets the merchant reject an order, and this one needs it
too, with a reason the customer sees and a refund path. It has to exist in the
store API and in the portal first, on the site; it is not something an app can
invent, and it is not part of the rider-and-driver submission. So decline is out
of the merchant app's trigger below: the app is built around the alert and shows
the actions the portal already has.

It is not part of this submission: grocery ordering is still a waitlist on the
site, and a merchant app without the alert would only be a wrapper.
**Trigger for the merchant app:** grocery live in at least one zone with shops
working the real order loop from the web portal — then submit it with the loud
new-order alert above and mark-ready, plus decline once the store side has one,
its own demo merchant account and its own review notes, after the rider app is
approved.

**Trigger for a separate driver app:** after a first approval, and only if
driver volume justifies driver-only needs the rider app should not carry —
background location, persistent offer alerts, navigation, earnings. Reuse
`www/shell-tweak.js` behind a role flag so one codebase still drives both shells.
Until then driver work ships as the `/driver` route, which needs no app release.

## Asset inventory

| Asset | Where | State |
|---|---|---|
| App icon (Play) | `assets/play-store-icon-512.png` | 512×512, 24-bit, no alpha — ready |
| Feature graphic (Play) | `store/graphics/feature-graphic-1024x500.png` | 1024×500, 24-bit, no alpha — drafted tonight, review the wording |
| Play phone screenshots | `store/screenshots/01`–`06` | 6 shots from the Android build — ready |
| Apple 6.3" screenshots | — | **missing.** One real 1206×2622 iOS capture of the landing page exists as `.toolchain/ios-artifacts/artifacts/ios-02-loaded.png`; it is not a store screenshot and the other five scenes have not been captured on iOS |
| Apple iPad screenshots | — | not required: the app is iPhone-only (`TARGETED_DEVICE_FAMILY = 1`) |
| Privacy policy | https://www.kerbgo.com/privacy | live, dated 16 November 2026 |
| Support URL | https://www.kerbgo.com/help | live |
| Marketing URL | https://www.kerbgo.com | live |
| Demo accounts | — | **missing.** Needed before review; see `review-notes.md` |

## App identity

| Field | Value |
|---|---|
| App name | Kerb |
| Bundle ID / application ID | `com.kerbgo.app` |
| Version | 1.0 (build 1 / versionCode 1) |
| Category | Apple: Travel (secondary Lifestyle). Play: Travel & Local |
| Price | Free |
| Coverage | United States only — restrict availability to the US in both consoles |

## Apple App Store Connect

### App record

1. My Apps → **+ New App**: platform iOS, name `Kerb`, primary language English
   (U.S.), bundle ID `com.kerbgo.app`, SKU `KERB-IOS-1`, full access.
2. Pricing: Free, availability **United States** only.
3. App Privacy → answers are tabulated in `compliance.md`.
4. Age rating: 4+ (no objectionable content, no unrestricted web access — the app
   can only open kerbgo.com and external map apps).
5. Export compliance: already declared in `Info.plist`
   (`ITSAppUsesNonExemptEncryption = false`), so the questionnaire pre-answers
   itself. Mention it in review notes anyway.
6. **iPad:** already settled — `TARGETED_DEVICE_FAMILY = 1` in both build
   configurations in `ios/App/App.xcodeproj/project.pbxproj`, so App Store
   Connect asks for an iPhone set only. Nothing to change here.

### Build and upload — GitHub Actions, no Mac and no Xcode

`.github/workflows/ios-release.yml` archives, exports and uploads in one run, using
`ios/ExportOptions.plist` (`method: app-store-connect`, automatic signing, build
number controlled by the workflow). It is manual on purpose — an upload cannot be
undone — so it never runs on push:

**Actions → iOS release → Run workflow.** Leave `build_number` blank to derive one
from the run number **and the attempt**: a re-run of the same job gets a new build
number instead of colliding with a build App Store Connect already holds. Pass a
whole number only to re-upload deliberately, and remember that number is then used
as-is.

**Prove it before uploading anything.** Untick `upload` and run it: the job signs
with the real distribution certificate, verifies the archive, exports the App Store
`.ipa` and downloads it as the `kerb-ios-<run>-<attempt>` artifact — but sends
nothing to Apple. That is the cheapest way to confirm the six secrets are right,
and it costs one runner run. Tick `upload` for the real submission.

The job runs `npm ci` and `npx cap sync ios` first, so the archive contains the
web bundle as committed. It sets `DEVELOPMENT_TEAM` and `CURRENT_PROJECT_VERSION`
as build settings, which means the project file still needs no team filled in. It
also fails fast on a missing secret, on a build number that is not numeric, and on
an archive signed by a team other than `APPLE_TEAM_ID` or carrying a build number
other than the one it asked for. Uploading is the only difference between a dry run
and a real one.

The default build number is `run_number × 100 + run_attempt`, so run 7 attempt 2
uploads build 702 — above run 7 attempt 1 (701) and below run 8 (801).

#### The six repository secrets

Settings → Secrets and variables → Actions → **New repository secret**, then add
all six. The job names any that are missing instead of failing halfway.

| Secret | Value | Where it comes from |
|---|---|---|
| `APPLE_TEAM_ID` | 10 characters, e.g. `A1B2C3D4E5` | developer.apple.com → Membership details → Team ID |
| `APPSTORE_KEY_ID` | 10 characters | App Store Connect → Users and Access → Integrations → App Store Connect API → Key ID of the key you create |
| `APPSTORE_ISSUER_ID` | a UUID | Same page, Issuer ID, shown above the key list |
| `APPSTORE_PRIVATE_KEY` | the **contents** of `AuthKey_<KEYID>.p8`, `BEGIN`/`END` lines included | Downloadable once, when the key is created; Apple never shows it again |
| `APPLE_CERTIFICATE_P12` | base64 of the `.p12`: `base64 -i cert.p12 -o -` | Exported from Keychain Access on a Mac |
| `APPLE_CERTIFICATE_PASSWORD` | the password chosen while exporting that `.p12` | You choose it |

#### Portal steps, once (about 30 minutes)

1. **Join the Apple Developer Program** — $99/year, individual or organisation. An
   organisation needs a D-U-N-S number and the Account Holder to accept the
   agreement. Nothing below exists until this is done.
2. **Team ID** — developer.apple.com → Account → Membership details. That 10-character
   value is `APPLE_TEAM_ID`.
3. **API key** — App Store Connect → Users and Access → Integrations → App Store
   Connect API → Team Keys → **+**. Name it `kerb-ci`, role **App Manager** (Admin
   also works; a Developer-role key cannot upload). Note the Key ID and the Issuer
   ID and download the `.p8` immediately — it is the only copy.
4. **App ID** — Certificates, Identifiers & Profiles → Identifiers → **+** → App IDs
   → App → bundle ID `com.kerbgo.app`, explicit. No extra capabilities are needed.
5. **Apple Distribution certificate** — this is the one step that wants a Mac, but
   **not** Xcode: Keychain Access → Certificate Assistant → *Request a Certificate
   From a Certificate Authority* → save to disk; upload that `.csr` at Certificates
   → **+** → Apple Distribution; download the `.cer` and double-click it; then in
   Keychain Access right-click the certificate → **Export** → `.p12` with a
   password. That file, base64-encoded, is `APPLE_CERTIFICATE_P12`. Export the
   private key with it: a `.cer` alone cannot sign.
6. **App record** — App Store Connect → My Apps → **+ New App**, using the bundle ID
   from step 4 (details in the App record section above).

The App Store provisioning profile is **not** on this list: the workflow passes
`-allowProvisioningUpdates` with the API key, which creates and downloads it. If
Apple ever refuses that, download the profile manually and add it to the keychain
step — the export options already ask for automatic signing.

#### The same commands, by hand (only if you have Xcode)

```
xcodebuild -project ios/App/App.xcodeproj -scheme App -configuration Release \
  -destination 'generic/platform=iOS' -archivePath build/Kerb.xcarchive \
  DEVELOPMENT_TEAM=<TEAMID> CURRENT_PROJECT_VERSION=<n> archive
xcodebuild -exportArchive -archivePath build/Kerb.xcarchive \
  -exportPath build/export -exportOptionsPlist ios/ExportOptions.plist
```

Then drag `build/export/*.ipa` into Transporter, or upload it with
`xcrun altool --upload-app -f <ipa> -t ios --apiKey <KEYID> --apiIssuer <ISSUER>`.

## Google Play Console

### App record

1. Create app: name `Kerb`, default language English (United States), app,
   free, accept the declarations.
2. **Store listing:** short description and full description from `listing.md`;
   icon, feature graphic and the six screenshots from the asset table above.
   Only US countries/regions.
3. **App access:** "All or some functionality is restricted" → give the two demo
   accounts from `review-notes.md`, with instructions to reach the ride flow.
4. **Ads:** No. **Content rating:** questionnaire answers in `compliance.md`.
   **Target audience:** 18+. **News app:** No. **Data safety:** `compliance.md`.
   **Government app:** No. **Financial features:** None of the listed categories
   (rides are paid, but the app does not provide banking, loans, crypto or
   trading). **Health:** No.
5. **Play App Signing:** enrolled by default on a new app — keep the upload key
   you generate below; losing it is the only way to lock yourself out.

### Upload key (already created — do not repeat)

**Created 19 Nov 2026.** `android/kerb-upload.jks` exists (alias `kerb`, RSA
2048, valid to 6 April 2054) together with `android/keystore.properties`; both
are git-ignored. Certificate SHA-256:
`E5:94:EB:90:12:5A:65:13:89:5B:06:44:77:4A:E4:A4:4E:18:E8:16:A7:6B:25:46:89:D5:47:E3:2C:54:46:81`

Back up both files together with the password inside `keystore.properties` —
Play needs the same key for every future upload. Re-running the command below
against the existing keystore will fail or overwrite it, so it is kept only as
the recipe for a fresh checkout:

```
cd android
keytool -genkeypair -v -keystore kerb-upload.jks -alias kerb \
  -keyalg RSA -keysize 2048 -validity 10000
```

The properties file `app/build.gradle` reads is (git-ignored — never commit it):

```
storeFile=kerb-upload.jks
storePassword=<the password you just typed>
keyAlias=kerb
keyPassword=<same password>
```

`app/build.gradle` reads that file automatically. Without it the release build is
unsigned instead of broken, which is how CI keeps working.

### Build the upload

```
JAVA_HOME=".toolchain/jdk/Contents/Home" ANDROID_HOME=".toolchain/sdk" \
  android/gradlew -p android bundleRelease
# → android/app/build/outputs/bundle/release/app-release.aab
```

Verified 19 Nov 2026: this command produces a 3.7 MB AAB signed with the upload
key. `jarsigner -verify` reports "jar verified" and the certificate in the
bundle matches the keystore. `apksigner` cannot read an AAB (it expects a root
`AndroidManifest.xml`; an AAB keeps it at `base/manifest/`), so verify the bundle
with `jarsigner`, or run `assembleRelease` and point `apksigner` at the APK:

```
.toolchain/sdk/build-tools/35.0.0/apksigner verify --print-certs \
  android/app/build/outputs/apk/release/app-release.apk
```

## Pre-submission checks that already pass

- `./tools/smoke-test.sh` — 16 end-to-end assertions against the Android
  build: launch, rider sign-in, `/app` route, shell patch, tab bar, tab
  switching, driver sign-in, `/driver`, driver patch, Navigate pill, all three
  chooser options, earnings tab, pill hidden on sub-routes. Green when last run
  (19 Nov 2026).
- `gh run list` → "iOS build" and "iOS simulator run" both green **as of 19 Nov
  2026**; the simulator job installs and launches the app on iPhone and
  screenshots it, which is the only place the iOS presentation layer is ever
  executed. That is a dated observation, not a standing fact: the GitHub CLI is
  not installed on the machine this kit was last reviewed from (20 Nov 2026), so
  it could not be re-confirmed, and the workflows are cheap to re-run. Check both
  jobs yourself on submission day.

## Order of work on submission day

1. Rebuild the AAB if anything changed since 19 Nov, upload it to Play internal
   testing. The upload key is already in place.
2. Run **Actions → iOS release** (all six secrets in place), then wait for the build
   to appear in App Store Connect. Add it to the 1.0 version page.
3. Build the demo state from `review-notes.md` — two accounts, the rider's saved
   place and past trip, the driver's vehicle and standing offer, promo code
   `KERBREVIEW` — then put the credentials in both consoles.
4. Capture the Apple screenshot set on an iPhone (5 scenes from
   `screenshot-checklist.md`; the sign-in scene already exists as
   `03-signin.png` and only needs an iOS re-shoot at 1206×2622).
5. Fill in the answers from `compliance.md`, paste `review-notes.md`, submit.
