# Store submission — open blockers

**Status page, 21 Nov 2026, 16:35 UTC.** Every blocker still open, who owns it,
and what "done" looks like. Evidence for anything dated lives in
`site-changes.md`; this page is only the index. When an item closes, update the
row here rather than adding a new section.

| Owner | Means |
|---|---|
| **Me** | Only you can do it — accounts, payments, consoles, passwords, pushes. |
| **cto.new** | The site/build team. Was out of weekly quota until about 06:47 UTC on 21 Nov, so the reset has passed — but its deployment is not serving at all right now (B15), which comes before any of its other rows. |
| **Unstarted** | Required, and nobody has begun it. |

> **The live site is down, 21 Nov 2026, 16:32 UTC.** `https://www.kerbgo.com/` returns
> Cloudflare's 32-byte `There is no site at this address` with HTTP 404 and
> `text/plain`, on every route checked — `/`, `/terms`, `/privacy`, `/help`, `/signin`,
> `/app`. `www.kerbgo.com` is a CNAME to `sites.cto-new.app`, and that host answers 404
> too, so this is the cto.new deployment not serving rather than a DNS fault. The shell
> apps load this URL, so **the apps currently show this error too** (B15). Fix this
> before anything else on this page, including the Apple enrolment.

**If you only do three things today:** get the site back (B15), enrol in the Apple
Developer Program (longest lead time, gates every iOS step), and decide the cto.new
quota — the outage makes that decision urgent, because the demo state both reviews
depend on sits on the same deployment.

---

## Both stores — the demo state and the live site

| # | Blocker | Owner | Done when |
|---|---|---|---|
| B1 | Demo Rider login refused on the live site (20 Nov, 17:47 and 17:51 UTC) | cto.new | `demo.rider@kerbgo.com` signs in at `www.kerbgo.com/signin` with the recorded password; account has no card on file |
| B2 | `KERBREVIEW` never proven on production | cto.new | On kerbgo.com: total `$0.00`, "Promo KERBREVIEW applied", no card field, booking completes and reaches the driver; a wrong code gives a plain message with the card step unchanged |
| B3 | Demo Rider has no saved place and no past trip | cto.new | "Home — 118 N College St, Auburn, AL 36830" saved; one completed trip about two weeks old, ~`$12.40`, receipt reachable from *Your trips* |
| B4 | Offer countdown prints raw seconds (`604798s`) | cto.new | Humanised — days and hours, or `mm:ss` — and counting |
| B5 | Turn label still `NEXT TURN — TO PICKUP` after *Start trip* | cto.new | Points at the drop-off once the trip has started |
| B6 | `I've arrived` unusable without a position fix | cto.new | Fixed, or documented as a location requirement in the review notes |
| B7 | Standing-offer seeder can create two identical offers 91 ms apart | cto.new | Exactly one pending offer per driver, every time |
| B8 | cto.new is out of weekly quota | Me | Decide: wait for ~06:47 UTC 21 Nov, or pay the `$30.52` reset |
| B10 | `support@kerbgo.com` mailbox does not exist | Me | Created in Google Workspace and receiving mail |
| B11 | Demo driver password was posted in the team chat and sits in that transcript | Me | Rotated, and the new value in both consoles instead of the old one |
| B12 | Reviewer walkthrough never run end to end on a clean install | Unstarted | The twelve steps in `review-notes.md` walked on a fresh install, signed out, with no console errors |
| B13 | Notes promise a `2019 Toyota Camry`, silver, `DEMO123`; production shows `Kia K4` | Me | Either the notes or the vehicle record changed so both say the same thing |
| B14 | Demo driver's documents not confirmed approved | cto.new | Licence, registration and insurance approved in the admin view, not merely uploaded |
| B15 | **Live site down** — every route returns Cloudflare 404 `There is no site at this address` (21 Nov, 16:32 UTC) | cto.new | `https://www.kerbgo.com/` serves the real home page, and `/signin`, `/app`, `/terms`, `/privacy` and `/help` answer as they did on 20 Nov |

**B9 closed 20 Nov 2026, 20:48 UTC.** `main` went to `origin/main` as `777157e`
(`e2017e4..777157e`, 44 commits), with the store docs in it, and the repository is
level with the remote again.

## Apple App Store

| # | Blocker | Owner | Done when |
|---|---|---|---|
| A1 | Apple Developer Program not enrolled | Me | Membership active and a Team ID exists |
| A2 | All six repository secrets absent | Me | `APPLE_TEAM_ID`, `APPSTORE_KEY_ID`, `APPSTORE_ISSUER_ID`, `APPSTORE_PRIVATE_KEY`, `APPLE_CERTIFICATE_P12`, `APPLE_CERTIFICATE_PASSWORD` set, and a dry run of *iOS release* with upload unticked produces a signed `.ipa` |
| A3 | App Store Connect app record not created | Me | App record for `com.kerbgo.app`, free, US only, age rating 4+ |
| A4 | No usable screenshots — the simulator artifact has the right size but photographs the live outage, and carries an alpha channel | cto.new | Six scenes captured at `1206 × 2622`, opaque, showing the real app on a working site; the Play set at `1080 × 2160` cannot be reused |
| A6 | Review notes still carry placeholders — the deletion path sentence and both demo logins | Me | Pasted from `review-notes.md` once B1 works |
| A7 | Review contact details not chosen | Me | Name, monitored email and a phone that answers during US business hours |
| A8 | App Privacy answers not entered in App Store Connect | Me | All nine data types entered as in `compliance.md` — collected, linked to identity, not tracked, purpose App Functionality — and the payment processor confirmed as Stripe so the Payment Info row is right. Usage Data and Diagnostics stay "No" |

**A5 closed 20 Nov 2026, 21:00 UTC.** The B9 push at 20:48:31 ran both workflows on
`777157e`: *iOS build* #14 `success` in 110 s, and *iOS simulator run* #3 `success` in
11.6 min (finished 21:00:06). No dispatch was needed — the push did it. The run it
replaces is *#13* on `e2017e4`, which failed on 19 Nov, so the two green runs are the
newest evidence and the "cancelled" note is history.

**Checked again 21 Nov 2026, 16:20 UTC.** The `ebeb74b` push produced *iOS build* #15,
`success`, 05:04:20 → 05:06:26 (126 s). Its *iOS simulator run* did not start: the
trigger is path-filtered to `ios/**`, `www/**` and its own file, and `ebeb74b` touched
only `store/**` — the earlier push ran it because path filters cover the whole pushed
range, which included `www/**` changes from commits that were still local. So the
simulator was dispatched by hand on `ebeb74b`: *iOS simulator run* #4,
`workflow_dispatch`, **`success`**, 16:11:03 → 16:19:50 (8.8 min), artifact
`ios-simulator-run` (90 KB).

Both workflows are now green on the same newest commit — build #15 and simulator #4 on
`ebeb74b` — so A5's condition holds on one SHA rather than across two. The artifact was
opened afterwards and does not help A4: both frames photograph the live 404 (B15), not
the app. See the A4 note below.

**21 Nov 2026, 18:03 UTC — run #9 is the current evidence.** After the shell change
landed, *iOS simulator run* #7 (`6ba89ee`) went green and both frames show the branded
card; #8 (`6654194`) repeated it on the simplified single-`HEAD` check, and **#9
(`242b324`) then rendered the same screen on the commit that keeps 401 and 403 off it**.
#9 is the newest green run and matches what is on `main`, so it is the evidence. The two
runs between #7 and #8 were the failures in `site-changes.md` §7: #5 green but still the
raw page, #6 did not compile. A5 stays closed.

**A4 checked 21 Nov 2026, 16:20 UTC.** Downloaded artifact `ios-simulator-run` from
simulator run #4 (90 KB, three files) and inspected both PNGs:

| Check | Result |
|---|---|
| Size | **`1206 × 2622`** on both — the required medium-display size, correct |
| Format | 8-bit PNG but **RGBA, alpha channel present** — App Store Connect rejects transparency, so these would have to be flattened before upload |
| Content | Both show WebKit's **"There is no site at this address"**, at 4:18 and 4:19 — the live outage (B15). No splash, no home screen, no rider or driver UI |
| Scenes | 2 of the 6 required, and neither is one of them |

So A4 gains no usable capture — still **0 of 6** scenes at a valid size with valid
content. What the run does prove is the pipeline: build, install, launch and screenshot
all work, and the device choice lands on the right pixel size. The remaining gap is
content and a working site, not tooling.

The later run #7 artifact shows the branded screen instead, which confirms the shell fix
in `site-changes.md` §7 — it is still not app UI, so A4 is unchanged.

One defect it exposes alongside B15: the branded offline screen in `www/index.html`
did not appear. A 404 is a *successful* HTTP response, so Capacitor's
`server.errorPath` never fires and the WebView renders the platform's raw error text
instead. That screen is only shown for a genuine load failure (no network, DNS), which
is the commoner case on a phone, but it is worth knowing that a hosting outage looks
like this.

## Google Play

| # | Blocker | Owner | Done when |
|---|---|---|---|
| G1 | Play Console app record not created | Me | App created, free, US only |
| G2 | Closed testing with 12 testers has not started | Unstarted | 12 testers opted in; the 14-day clock running |
| G3 | AAB not uploaded | Me | Signed AAB uploaded to a testing track (the asset already exists — see below) |
| G4 | App access instructions not written into the console | Me | Both demo logins pasted, blocked on B1 and the driver password |
| G5 | Data safety, content rating and target audience answers not submitted | Me | Answers from `compliance.md` entered, including its two `[confirm]` items: the payment processor, and whether riders and drivers can call or message each other (the IARC interaction question) |
| G6 | Play Data deletion URL not entered | Me | `https://www.kerbgo.com/delete-account` in the field — it answers `200` now, so the older "leave it empty until it stops 404ing" advice no longer applies |

---

## Regulatory and safety gaps — added 20 Nov 2026

Raised by a public review-summary of kerbgo.com that said the site is silent on
background checks, safety tools and licensing. The site is silent on the last two,
and on the first it is worse than silent — `/help` states that automated background
checks are "a later release". Citations and gated copy are in
`safety-and-licensing.md`. **These are not store blockers; R1 is the one that
matters.**

Revised the same day: **screening runs by hand at two layers** — UZURV completes the
statutory checks as the TNC of record, and Kerb reads each driver's licence,
registration and insurance, enters pass or fail on the account, and revokes within 24
hours where needed. §32-7C-29(a)(2)–(3) lets the TNC run the checks itself, so this is
compliant provided the three artefacts exist per driver. That half of the copy is
publishable now.

**The licensing half resolved on 20 Nov 2026: structure B.** The only permit in
evidence is **APSC Permit TNC-4, held by UZURV Holdings, Inc. d/b/a UZURV** (Delaware,
Richmond VA, renewed effective 1 July 2026), and the owner confirmed that Kerbgo is a
separate company for which **UZURV is the TNC of record** — its screening and insurance
cover every Kerb trip, Kerb is the storefront. The **owner confirms sole ownership of
both companies**, which explains the affiliation; it does not make TNC-4 Kerb's permit.
That is lawful if disclosed, so the page now carries the disclosure sentence from
`safety-and-licensing.md` §2b. It does not say Kerb is licensed, because Kerb is not the
permittee.

| # | Blocker | Owner | Done when |
|---|---|---|---|
| R1 | Licensing disclosure not live: rides on Kerb are provided by UZURV Holdings, Inc. under APSC permit TNC-4 (structure B), and the site names neither UZURV nor the permit | cto.new | `/safety` carries the §2b sentence; `/terms` says who operates Kerb and that rides are provided by UZURV |
| R2 | Screening records are undocumented. Statutory screening is UZURV's duty as the TNC; Kerb's own manual document review has no per-driver record | Me | Written confirmation that UZURV screens every Kerb driver to §32-7C-29, plus Kerb's own licence/registration/insurance review recorded per driver and kept two years (§32-7C-33) |
| R3 | Zero-tolerance notice not on the site, and the corrective measure not yet confirmed in writing | Me | `safety-and-licensing.md` §4's "removed from Kerb" line approved or replaced, and the notice live |
| R4 | Coverage is UZURV's TNC-4 certificate, and it has not been checked for whether it covers trips booked through Kerb | Me | Copy of UZURV's certificate in hand, covering Kerb-booked trips at §32-7C-2(b)/(c) limits; then the coverage paragraph (Gate A) publishes |
| R5 | No safety page, no 911 guidance, no report route, no zero-tolerance notice | cto.new | `/safety` live from `safety-and-licensing.md` §4 word for word, footer **Safety** link, and `/help` changed off "automated background checks are a later release" — all three in one deploy |
| R6 | No live trip sharing | cto.new | `live-trip-sharing.md` checks 1–9 pass on a real trip; then `/safety` gains the sharing sentence |
| R7 | `/terms` and `/privacy` identify no company, address or jurisdiction — the counterparty a rider is contracting with is unnamed. The clause is drafted in `safety-and-licensing.md` §9 (20 Nov 2026); it waits on the registered name, state of formation and a publishable address | Me + cto.new | Both pages carry the §9 text word for word: Kerb's operating entity named, and rides provided by UZURV Holdings, Inc. under TNC-4 |
| R8 | Site claims service across "the entire United States" with 51 service states and a $25 per-state-line fee; TNC permits are per-state (UZURV holds AL, MA and others, not all) | Me | Claim narrowed to permitted states, or permits obtained. Not a store blocker, but the same exposure as R1 |

`/safety` ships **with** the §2b licensing paragraph — structure B is settled — and
with the **APSC** complaint channel. The `support@kerbgo.com` line waits for B10,
because a bounce is worse than no address.

Still true, and still the rule: nothing on the site may say **"fully licensed"**, and
nothing may say **"we operate under our sister company's licence"** — that is not a
thing in Chapter 770-X-12. The only licensing sentence available is the one that names
UZURV as permittee under the structure that is actually true. "Every driver is checked
before their first trip" is now true, but only while R2's records exist behind it, and
only as long as UZURV's screening and insurance actually cover Kerb trips — that is
what R2 and R4 are for.

---

## Open but not blocking

- **Play 9:16 promo set** (optional): the six `1080 × 2160` shots are legal but miss Play's
  recommendation layouts; a re-capture at `1080 × 1920` would qualify. Not needed to publish.
- **Incoming-offer screenshot** (recommended, unblocked): the standing offer is live, so the
  card can be photographed in one session — it is the strongest driver-side image.
- **Live-trip screenshot** (optional): needs a real booking accepted by a driver.
- **Keystore backup**: `android/kerb-upload.jks` and its password exist in this working copy
  only; confirm the backup lives outside the repo before you rely on it.
- **Stripe Connect payments phase** (`site-handover.md` P1–P4): explicitly not a review item
  and must not start while either app is in review.
- **Site migration off cto.new** (`site-migration.md`): not needed for submission.

## Already done — do not redo

- **In-app account deletion, end to end** (§1): `/delete-account` returns `200` with
  `cache-control: no-store`, the flow demands `DELETE` typed in, then clears the
  session, redirects `/app` to sign-in and refuses the old credentials.
- **`data-kerb-destination`** (§2): `1` from accept through arrive and start,
  carrying `334 W Magnolia Ave, Auburn, AL 36830`, and `0` after completion.
- **Standing demo offer** (§4): waiting on `/driver` with nothing else acting,
  accept → arrive → start → complete, and a fresh `$14.80` offer already there.
- **Landing copy** (§3): both replacement sentences live; `no app`, `browser only`
  and `download` gone.
- **App shell 4xx screen** (`site-changes.md` §7): a hosting error now shows the
  branded card with **Try again** instead of the platform's own error text. Neither
  platform treats a 401 or 403 as an outage. iOS checks once per finished load with a
  native `HEAD` and re-checks on foreground; Android uses Capacitor's `errorPath` swap,
  with a `WebViewClient` subclass that skips it for those two statuses, and only polls
  for recovery. Verified on iOS in simulator run #9; Android compiles, and
  `.github/workflows/android.yml` now builds the debug APK on every push, but the app
  has still never been run.
- **Support address on the site**: `support@kerbgo.com` on `/help` and in every
  footer, with no `ctomail.io` left. The mailbox behind it is B10.
- **Android upload asset**: signed AAB at
  `android/app/build/outputs/bundle/release/app-release.aab`; upload key created
  19 Nov (its backup is the one open question in *Open but not blocking*).
- **Play creative**: 512 icon with alpha, 1024×500 feature graphic, six 1080×2160
  screenshots (legal, though not the 9:16 recommendation).
- **Legal pages**: privacy policy (16 Nov 2026), support URL and marketing URL all live.
- **iOS build settings**: `ITSAppUsesNonExemptEncryption = false`, iPhone-only so
  no iPad screenshots are needed.

## Evidence and detail

| File | What it holds |
|---|---|
| `site-changes.md` | The five site changes, dated live evidence, the two open driver defects and the fix-status log |
| `safety-and-licensing.md` | The `/safety` copy to ship, the screening and licensing evidence, and the gated claims |
| `live-trip-sharing.md` | Build spec for the share link — data, endpoint, privacy, acceptance checks |
| `review-notes.md` | Paste-ready review text; the rider row is still marked **UNVERIFIED** |
| `submission-guide.md` | Console fields, the six secrets, build commands, submission-day order |
| `screenshot-checklist.md` | Exact sizes and why the Android set cannot be reused |
| `compliance.md` | Privacy, data safety, ratings, rejection risks |
| `listing.md` | Store copy, keywords, asset inventory |

### Older docs — stale claims corrected 20 Nov 2026

The contradictions this page used to list are fixed in `submission-guide.md`,
`site-handover.md`, `listing.md`, `compliance.md`, `review-notes.md`,
`screenshot-checklist.md` and `site-migration.md`: the five site changes now read as
landed and verified, the support address as served from `/admin/branding`, and
`/delete-account` as `200` rather than a 404. Where a document still describes the
19 Nov state it is dated as history — `site-changes.md` keeps its superseded table
deliberately. The rider login stays marked **UNVERIFIED** wherever it appears.
