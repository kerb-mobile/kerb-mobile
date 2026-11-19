# Submission guide

Everything needed to create the two app records, build the two uploads and answer
the review questions. Written to be followed top to bottom on submission day.

**Status tonight:** the Android upload can be produced as soon as an upload key
exists. The iOS upload needs a Mac with Xcode and the Apple Developer team — this
machine has neither. Two policy gaps (account deletion, "no app-store download"
copy on the landing page) are real rejection risks and are described in
`compliance.md`.

Those two documents plus this one are the whole submission kit:

| File | Use |
|---|---|
| `store/listing.md` | Store copy: names, descriptions, keywords, screenshots inventory |
| `store/compliance.md` | Privacy answers, data safety, ratings, rejection risks |
| `store/review-notes.md` | The text to paste into App Review notes and Play App access |
| `store/submission-guide.md` | This file: console fields, build commands, order of work |

## Asset inventory

| Asset | Where | State |
|---|---|---|
| App icon (Play) | `assets/play-store-icon-512.png` | 512×512, 24-bit, no alpha — ready |
| Feature graphic (Play) | `store/graphics/feature-graphic-1024x500.png` | 1024×500, 24-bit, no alpha — drafted tonight, review the wording |
| Play phone screenshots | `store/screenshots/01`–`06` | 6 shots from the Android build — ready |
| Apple 6.3" screenshots | — | **missing.** One real 1206×2622 iOS capture of the landing page exists as `.toolchain/ios-artifacts/artifacts/ios-02-loaded.png`; it is not a store screenshot and the other five scenes have not been captured on iOS |
| Apple iPad screenshots | — | **missing, and avoidable**: set `TARGETED_DEVICE_FAMILY = 1` and they are not required |
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
6. **Before screenshots:** set `TARGETED_DEVICE_FAMILY = 1` in both build
   configurations in `ios/App/App.xcodeproj/project.pbxproj` (it is `"1,2"`
   today, which makes an iPad set mandatory).

### Build and upload (needs a Mac with Xcode)

```
open ios/App/App.xcodeproj
# Signing & Capabilities → Team: <your Apple Developer team>, automatic signing
xcodebuild -project ios/App/App.xcodeproj -scheme App \
  -configuration Release \
  -destination 'generic/platform=iOS' \
  -archivePath build/Kerb.xcarchive archive
xcodebuild -exportArchive -archivePath build/Kerb.xcarchive \
  -exportOptionsPlist ExportOptions.plist -exportPath build/export
xcrun altool --upload-app -f build/export/Kerb.ipa --type ios \
  -u <apple-id> -p <app-specific-password>
```

`DEVELOPMENT_TEAM` is not set in the project, so the team must be selected once in
Xcode and committed before an archive can be signed.

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

### Upload key (do this once)

```
cd android
keytool -genkeypair -v -keystore kerb-upload.jks -alias kerb \
  -keyalg RSA -keysize 2048 -validity 10000
```

Then create `android/keystore.properties` (git-ignored — never commit it):

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

Verified tonight: this command succeeds and produces a 3.6 MB AAB. Unsigned
until `keystore.properties` exists; Play rejects an unsigned bundle.

## Pre-submission checks that already pass

- `./.toolchain/smoke-test.sh` — 17 end-to-end assertions against the Android
  build: launch, rider sign-in, `/app` route, shell patch, tab bar, tab
  switching, driver sign-in, `/driver`, driver patch, Navigate pill, all three
  chooser options, earnings tab, pill hidden on sub-routes. Green tonight.
- `gh run list` → "iOS build" and "iOS simulator run" both green; the simulator
  job installs and launches the app on iPhone and screenshots it, which is the
  only place the iOS presentation layer is ever executed.

## Order of work on submission day

1. Add the upload key, build the AAB, upload to Play internal testing.
2. In Xcode: set the team, set `TARGETED_DEVICE_FAMILY = 1`, archive, upload.
3. Create the two demo accounts and put their credentials in both consoles.
4. Capture the Apple screenshot set on an iPhone (5 scenes from
   `screenshot-checklist.md`; the sign-in scene already exists as
   `03-signin.png` and only needs an iOS re-shoot at 1206×2622).
5. Fill in the answers from `compliance.md`, paste `review-notes.md`, submit.
