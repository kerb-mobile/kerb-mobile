# Submit day — one page, both consoles

**Drafted 24 Nov 2026.** The click order for App Store Connect and Google Play, in the
order that shortens the critical path rather than the order the docs were written in.
Everything here is assembled from `submission-guide.md` (console fields, build commands),
`listing.md` (copy), `compliance.md` (privacy, data safety, ratings), `review-notes.md`
(reviewer text) and `status.md` (what is still open). Nothing on this page is new work; it
is the ordering. When a check here disagrees with another file, this page says which file
wins and why.

**Read the gate first.** If any row in Part 0 is red, submitting does not fail fast — it
fails in review, 24–48 hours later, against a build that has already been uploaded and a
listing that has already been read.

---

## Part 0 — the gate: do not press submit until every row is green

| # | Check | How to check it | If red |
|---|---|---|---|
| 1 | **The site serves.** `https://www.kerbgo.com/` and `/signin` return 200 with the real home page and sign-in screen | Open both in a browser, or `curl -sS -o /dev/null -w '%{http_code}\n' https://www.kerbgo.com/` — expect `200`, not `404` | **Stop.** This is B15 and it alone guarantees a 2.1 rejection: the shell opens onto *"There is no site at this address"* |
| 2 | Rider demo account signs in | `www.kerbgo.com/signin` with the rider credentials — no verification step, no code | B1. do not submit with the notes' own "UNVERIFIED" line in them |
| 3 | `KERBREVIEW` zeroes a fare | Book as the rider, enter the code at payment, see `$0.00` + "Promo KERBREVIEW applied", no card field | B2 — the whole no-charge review path collapses without it |
| 4 | Rider has the saved place and one past trip | Account tab: "Home — 118 N College St" chip, a trip ~2 weeks old with a receipt | B3 |
| 5 | Driver is approved with documents **approved**, not uploaded | Admin view: licence, registration, insurance each show approved | B14 |
| 6 | A pending offer is waiting on `/driver` without the driver going online | Sign in as driver, open `/driver`, do not press *Go online* | §4 of `site-changes.md`; a reviewer on a fresh install may never go online |
| 7 | Driver's vehicle line reads the same as the notes will | Dashboard → *Your car*: note year, make, model, colour, plate | B13 is closed by matching the notes to the record, not the reverse — paste what you see |
| 8 | `support@kerbgo.com` receives mail | Send a message from another account, confirm it arrives | B10. Every support link the reviewer follows lands here |
| 9 | Demo password rotated, new value in both consoles | Rotation recorded, old value dead | B11 |
| 10 | Twelve steps of `review-notes.md` walked on a clean install | The walkthrough, in order, signed out at the start | B12 |
| 11 | Apple screenshot set exists: 6 scenes at `1206 × 2622`, no alpha | `store/screenshots/` has the Play set only; Apple's is A4 | **Stop for iOS.** App Store Connect will not accept the version page without it |
| 12 | Apple membership active and a Team ID exists | developer.apple.com → Account → **Membership details**: Program status **Active**, 10-character **Team ID** shown | A1. See `a1b-individual-enrolment.md` |

Rows 1–10 are the same for both stores. Row 11 blocks iOS only. Row 12 blocks everything
Apple-side, including signing.

---

## Part 1 — the day before (no console access needed)

Do these first because each one can fail without touching a store.

1. **Rebuild the AAB if anything under `www/` changed since 19 Nov.**
   ```
   JAVA_HOME=".toolchain/jdk/Contents/Home" ANDROID_HOME=".toolchain/sdk" \
     android/gradlew -p android bundleRelease
   ```
   Expect `android/app/build/outputs/bundle/release/app-release.aab` (~3.7 MB, signed with
   `android/kerb-upload.jks`). Confirm it is signed with `jarsigner -verify`, not
   `apksigner` (apksigner cannot read an AAB).
2. **Back up the upload key.** `android/kerb-upload.jks` plus its password in
   `android/keystore.properties`, somewhere off this laptop. Losing it is the only way to
   lose the Play listing.
3. **Dry-run the iOS release:** Actions → *iOS release* → Run workflow → **untick
   `upload`**. It signs, verifies and exports a `.ipa` artifact and sends nothing to Apple.
   This is A2's proof and it costs one runner run. Do not skip straight to a real upload.
4. **Collect the twelve tester addresses** (G2) in a spreadsheet. The Play clock cannot
   start without them, and it is the single longest remaining wait — see Part 4.
5. **Fill the `[you]` fields** from `listing.md` into a scratch note: review contact name,
   phone, monitored email. Never commit the demo passwords.
6. **Settle the two `[confirm]` answers** in `compliance.md`: the payment processor (Stripe)
   so Apple's Payment Info row is truthful, and whether riders and drivers can call or
   message each other (the IARC interaction question).

---

## Part 2 — Apple, in click order

Console: `appstoreconnect.apple.com`. Portal: `developer.apple.com/account`.

### Once, then never again (~45 minutes, and only step 5 wants a Mac)

| # | Do this | Collect |
|---|---|---|
| 1 | developer.apple.com → Account → **Membership details** | `APPLE_TEAM_ID` (10 chars). Confirm Program status **Active** and that the Apple Account's first/last name is your **legal** name — an alias or company name there delays Apple's checks |
| 2 | App Store Connect → **Business** → Agreements → accept the **Free Apps** agreement | A free app needs no Paid Apps agreement, no tax forms and no bank account. Leave all of that alone |
| 3 | Portal → Certificates, Identifiers & Profiles → Identifiers → **+** → App IDs → App → bundle ID `com.kerbgo.app`, **explicit**. No capabilities | The App ID |
| 4 | App Store Connect → Users and Access → Integrations → **App Store Connect API** → Team Keys → **+**, name `kerb-ci`, role **App Manager** | `APPSTORE_KEY_ID`, `APPSTORE_ISSUER_ID`, and `AuthKey_<KEYID>.p8` — **downloadable once** |
| 5 | Keychain Access → Certificate Assistant → *Request a Certificate From a Certificate Authority* → save the `.csr` → Portal → Certificates → **+** → **Apple Distribution** → upload the CSR → download the `.cer` → double-click it → in Keychain Access export the certificate **with its private key** as `.p12` | `base64 -i cert.p12 -o -` = `APPLE_CERTIFICATE_P12`; the password you chose = `APPLE_CERTIFICATE_PASSWORD`. A `.cer` alone cannot sign |
| 6 | GitHub → Settings → Secrets and variables → Actions → add all six | All six set. The workflow names any that are missing instead of failing halfway |

An individual membership can invite people into App Store Connect, but they are not members
of your developer team and do not get certificates or profiles — step 5 is yours either way.

### The app record

1. My Apps → **+** → New App: iOS, name **`Kerb`**, primary language English (U.S.),
   bundle ID `com.kerbgo.app` (the one from step 3 — it will only appear if the App ID
   exists), SKU **`KERB-IOS-1`**, full access.
2. **App Information:** subtitle `Local rides, upfront fares`; category **Travel**,
   secondary **Lifestyle**; content rights: does not contain third-party content; age
   rating questionnaire → **4+**.
3. **Pricing and Availability:** Free; availability **United States** only.
4. **App Privacy:** the nine rows from `compliance.md` / `listing.md` — every one
   *collected*, *linked to identity*, **not** used for tracking, purpose *App
   Functionality*. Usage Data and Diagnostics stay **No**. No ATT prompt is needed as a
   result; do not add one.

### The build, then the version page

1. Actions → **iOS release** → Run workflow → **tick `upload`**. Leave `build_number` blank:
   the workflow derives `run × 100 + attempt`, so a re-run cannot collide with a build
   App Store Connect already holds.
2. Wait for the build to finish processing in App Store Connect (TestFlight tab, usually
   15–60 minutes after upload). A build that is "Processing" cannot be selected on a
   version page.
3. Version page **1.0**: description from `listing.md`; keywords
   `rideshare,ride,taxi,local,driver,fare,airport,trip,booking,curb,grocery,delivery`;
   promotional text; **Support URL** `https://www.kerbgo.com/help`; **Marketing URL**
   `https://www.kerbgo.com`; **Privacy Policy URL** `https://www.kerbgo.com/privacy`;
   copyright `Kerb`; **Build** = the one that just processed.
4. **Screenshots:** the six scenes at `1206 × 2622`, opaque. iPhone only —
   `TARGETED_DEVICE_FAMILY = 1` means no iPad set is asked for.
5. **App Review Information:** contact name/phone/email (a phone that answers in US
   business hours — they do call); both demo accounts; the Notes text from
   `review-notes.md`, with the deletion sentence and the vehicle line **filled in**, not
   left as a bracket. Mention export compliance and that no card is charged.
6. **Sign in with both demo accounts one final time**, on a clean install, immediately
   before pressing submit. The commonest way a green build fails is a demo account that
   stopped working between setup and review.
7. **Add for Review → Submit.** Expect 24–48 hours.

---

## Part 3 — Google Play, in click order

Console: `play.google.com/console`.

### App record and declarations

1. **Create app:** name **`Kerb: Local Rides`**, default language English (United States),
   type **App**, **Free**, accept the developer-program declarations.
2. **App content** — every item, because Play will not enable a track until they are done:
   - Privacy policy: `https://www.kerbgo.com/privacy`
   - **App access:** *All or some functionality is restricted* → both demo accounts with the
     instructions pasted from `review-notes.md` (G4 — this is the one that needs B1)
   - Ads: **No**
   - Content rating: IARC questionnaire → low rating, no violence/sexual/profanity/
     gambling/UGC
   - Target audience: **18+**
   - News app: **No**. Government app: **No**. Health: no. Financial features: **none of
     the listed categories** (rides are a transport service paid by card)
   - **Data safety:** the answers in `compliance.md` — collects the nine types, nothing
     shared, encrypted in transit, deletion at `Account → Delete account` plus the URL
   - **Data deletion URL:** `https://www.kerbgo.com/delete-account` — **only after it
     answers 200 again** (G6). An entered field that 404s is worse than an empty one
3. **Store listing:** short description, full description and category **Maps &
   Navigation** from `listing.md` (this file supersedes the older "Travel & Local" line in
   `submission-guide.md`); icon `assets/play-store-icon-512.png`; feature graphic
   `store/graphics/feature-graphic-1024x500.png`; the six screenshots in
   `store/screenshots/`; contact email `support@kerbgo.com`; website
   `https://www.kerbgo.com`; countries: **US only**.
4. **Play App Signing:** enrolled by default. Keep the upload key; you keep using it for
   every future upload.

### The track — this is where the calendar is

1. **Testing → Internal testing → Create new release:** upload
   `android/app/build/outputs/bundle/release/app-release.aab`, release notes "First build
   for review", roll out.
2. Upload to internal testing first and install it yourself from the opt-in link — an
   AAB that fails to install is caught here rather than after 14 days of testing.
3. **Testing → Closed testing → Create track**, add the twelve testers (email list or Google
   Group), same build or a promoted one, and start it. **The 14-day clock starts when
   testers are opted in and the release is live on the track**, not when the AAB is uploaded.
4. If your Play account was created after 13 Nov 2023, individual and new organisation
   accounts must run this closed test with **12 testers for 14 continuous days** before
   applying for production access. Nothing shortens it — plan the dates around it.
5. After the 14 days: **Production → Apply for access**, answer the questions honestly
   (who tested, what changed), then create the production release from the same track.

---

## Part 4 — the ordering rule

**Start the Play closed test first, the same hour the site is back.** It is a 14-day wall
clock that runs in the background while you do everything else; Apple's review is 1–2 days
and can be sent the same day once its screenshots exist. Doing Apple first and Play second
costs two weeks for no gain.

The shape of submit day, once Part 0 is green:

1. Rebuild and upload the AAB → internal test → closed testing live (15 minutes).
2. iOS dry run if you have not already → real upload → version page → submit (1–2 hours
   including processing).
3. Console declarations for Play can be finished while the iOS build processes; both
   consoles are open in tabs.

---

## Part 5 — if it comes back rejected

| Guideline | What it looks like | The prepared answer |
|---|---|---|
| **2.1** App completeness | The reviewer opens the app and the WebView shows an error page, a blank screen, or a spinner that never resolves | The site was not serving. Nothing to fix in the app — check Part 0 row 1 before resubmitting, and say so in the reply |
| **4.2** Minimum functionality | "The app is primarily a website wrapper" | The reply is the native surface, itemised: native Navigate action sheet handing off to Apple Maps/Google Maps/Waze, device location for pickup and trip following, native tab bar and driver dashboard, system camera for document capture. Never argue that a WebView is fine |
| **5.1.1(v)** Account deletion | Deletion not reachable in-app | `Account → Delete account` exists and `https://www.kerbgo.com/delete-account` answers — paste the path into the notes |
| **2.3.1** Metadata | A screenshot or note shows something the build does not | Commonest cause here: the vehicle line in the notes disagreeing with the record (B13), or a screenshot from an older build. Re-shoot rather than re-word |
| **3.1.1** Payments | "Rides are unlocked with a card in the app" | Payment is for a physical service (a ride), processed on the web backend, which 3.1.3(e)/3.1.5(a) permit. Say it plainly and point at the review notes |
| **Play: broken functionality** | Same as 2.1 | Same answer, plus the internal-test install from Part 3 step 2 as evidence it installs |

Reply in the console, in the reviewer's thread. Do not resubmit a new binary for a metadata
rejection, and do not change the demo password during review — a password that rotated is
indistinguishable from one that never worked.

## Part 6 — do not, on submit day

- Change anything on the live site mid-review. A deploy during review is how an approved
  build becomes a rejected one. `site-migration.md` and `plan-b-recovery.md` both say it;
  this page repeats it because submit day is when the temptation arrives.
- Past the demo passwords into this repository, or into `listing.md`.
- Upload a second binary under a different bundle ID or package name to "get it in faster".
  It restarts both clocks and splits the reviews.
- Tick `upload` on a workflow run whose secrets you have not proved with an unticked run.
