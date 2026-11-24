# Store submission — open blockers

**Status page, 24 Nov 2026, 16:18 UTC.** Every blocker still open, who owns it,
and what "done" looks like. Evidence for anything dated lives in
`site-changes.md`; this page is only the index. When an item closes, update the
row here rather than adding a new section.

| Owner | Means |
|---|---|
| **Me** | Only you can do it — accounts, payments, consoles, passwords, pushes. |
| **cto.new** | The site/build team. Its weekly quota reset on 21 Nov (B8), and its deployment is still not serving at all (B15), which comes before any of its other rows. |
| **Unstarted** | Required, and nobody has begun it. |

> **The live site has answered 404 since at least 21 Nov 2026, 16:18 UTC, and was
> still 404 at 24 Nov 16:18 UTC.** `https://www.kerbgo.com/` returns
> Cloudflare's 32-byte `There is no site at this address` with HTTP 404 and
> `text/plain`, on every route checked — `/`, `/terms`, `/privacy`, `/help`, `/signin`,
> `/app`, `/delete-account`. `www.kerbgo.com` is a CNAME to `sites.cto-new.app`, and
> that host answers 404 too, so this is the cto.new deployment not serving rather than
> a DNS fault. The shell apps load this URL, so **the apps currently show this error
> too** (B15). Fix this before anything else on this page, including the Apple
> enrolment.

**If you only do three things today:** get the site back (B15), set the six Apple secrets
and confirm the Team ID (A2 — the membership itself is paid as of 24 Nov 2026), and create
the `support@kerbgo.com` mailbox (B10) — it gates A7 and every support link a reviewer
follows. The order for the day itself is `submit-day.md`.

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
| B10 | `support@kerbgo.com` mailbox does not exist | Me | Created in Google Workspace and receiving mail — MX and SPF already route `kerbgo.com` mail to Workspace (checked 24 Nov 2026), so the mailbox is the only missing part |
| B11 | Demo driver password was posted in the team chat and sits in that transcript | Me | Rotated, and the new value in both consoles instead of the old one |
| B12 | Reviewer walkthrough never run end to end on a clean install | Unstarted | The twelve steps in `review-notes.md` walked on a fresh install, signed out, with no console errors |
| B14 | Demo driver's documents not confirmed approved | cto.new | Licence, registration and insurance approved in the admin view, not merely uploaded |
| B15 | **Live site down** — every route returns Cloudflare 404 `There is no site at this address` (first photographed 21 Nov, 16:18 UTC) | cto.new | `https://www.kerbgo.com/` serves the real home page, and `/signin`, `/app`, `/terms`, `/privacy`, `/help` and `/delete-account` answer as they did on 20 Nov |
| B16 | **The Apple Account password for the paid membership was posted in the chat** on 24 Nov 2026 | Me | Rotated, two-factor authentication confirmed on, and the new value in a password manager only — in no transcript, browser note, file or commit |

**B15 re-checked 24 Nov 2026, 16:18 UTC — still down on the third day, nothing to record
but the check.** Every route B15 names, plus the apex, `/delete-account` and
`sites.cto-new.app/` itself, answers the same way: HTTP 404,
`text/plain;charset=utf-8`, 32 bytes, body `There is no site at this address`, with
`server: cloudflare` and `cache-control: no-store`. One `cf-ray` per request, in the
order they were made — each ray is the evidence for that request alone, not for the set:

| Request | Status | `cf-ray` | `date` |
|---|---|---|---|
| `https://www.kerbgo.com/` | 404 | `a5f9bab76a3dc36f-ATL` | 16:18:24Z |
| `https://www.kerbgo.com/signin` | 404 | `a5f9baba1b29b577-ATL` | 16:18:25Z |
| `https://www.kerbgo.com/app` | 404 | `a5f9babc9973dec9-ATL` | 16:18:25Z |
| `https://www.kerbgo.com/terms` | 404 | `a5f9babf5b68d7da-ATL` | 16:18:25Z |
| `https://www.kerbgo.com/privacy` | 404 | `a5f9bac0db45b0ac-ATL` | 16:18:26Z |
| `https://www.kerbgo.com/help` | 404 | `a5f9bac35efd1eff-ATL` | 16:18:26Z |
| `https://www.kerbgo.com/delete-account` | 404 | `a5f9bac4da1bb4be-ATL` | 16:18:26Z |
| `https://kerbgo.com/` (apex) | 404 | `a5f9bac81bb1eb0c-ATL` | 16:18:27Z |
| `https://kerbgo.com/privacy` (apex) | 404 | `a5f9bacb8bc34724-ATL` | 16:18:28Z |
| `https://sites.cto-new.app/` | 404 | `a5f9bace380bc102-ATL` | 16:18:28Z |

The three earlier passes reached the same result and are not evidence for these requests:
the 23 Nov 17:09 UTC pass covered these same ten routes, the 22 Nov 17:38 UTC pass covered
eight, and the 22 Nov 17:16–17:20 UTC pass captured headers on only three —
`a5e99adaaff16779-ATL` for the apex `/`, `a5e99add4998bcdf-ATL` for the apex `/privacy`,
`a5e99adfdfe5b048-ATL` for `www /privacy`. Each of those rays belongs to its own request;
the table above is the citable record.

`sites.cto-new.app/` returning the identical response again settles the diagnosis: the
project has no deployment behind it, so this is not a detached custom domain and not
DNS. DNS is unchanged — re-checked the same minute, `www.kerbgo.com` CNAME
`sites.cto-new.app`, and both hostnames resolve to `172.67.205.64` and
`104.21.42.107`; TLS is valid on both.

That is **72 h 0 m** since the error was first photographed (21 Nov, 16:18 UTC) and
71 h 33 m since it was reported. There is no restore time to record, so B15 stays open
and everything that needs a serving site — the A4 captures, the item 4 recovery check in
`site-changes.md` §7, the reviewer walkthrough — still waits on cto.new, who have a
follow-up refreshed with this same pass in `cto-new-outage.md`. If no publish arrives by
the deadline that follow-up sets, `plan-b-recovery.md` is the recovery note: who holds the
domain, the source, the data and the serving, and what each of them needs. What does not
need the site is listed in the next section, because three days of waiting should not be
three days without progress.

**B8 closed 21 Nov 2026, 06:47 UTC.** The weekly quota reset on schedule, so the choice
between waiting and paying the `$30.52` reset never needed making. The site was still
404 more than two days later, so quota is not what keeps the deployment offline — the
follow-up in `cto-new-outage.md` asks for a publish, not a payment.

**B9 closed 20 Nov 2026, 20:48 UTC.** `main` went to `origin/main` as `777157e`
(`e2017e4..777157e`, 44 commits), with the store docs in it, and the repository is
level with the remote again.

**B13 closed 24 Nov 2026.** The review notes no longer name a vehicle production never had.
The `2019 Toyota Camry`, silver, `DEMO123` line is gone, replaced by the demo driver's own
record — a `Kia K4` when it was measured on 19 Nov 2026 — with year, colour and plate now
copied from the record at submission instead of remembered. Nothing here needed the site,
which is why it was the one open blocker that could be settled in the docs; the same edit
adds a plate check, so a record whose plate is not obviously synthetic gets changed rather
than described.

## While B15 is open — store work that does not need the site

**Added 22 Nov 2026, 25 hours into the outage; checked row by row against the blockers
on 23 Nov 2026.** Only items with an open blocker row are here — B8 is closed, and
`listing.md`'s copy is not a blocker of its own. A1 is closed as of 24 Nov 2026, so what is
left behind it is only A2 (the six secrets) and A3 (the app record). The rest can be
finished now and pasted into a console later, instead of waiting on the deployment.

| Work | Why the site is not needed | Owner | Start with |
|---|---|---|---|
| A2 six repository secrets, plus *iOS release* with upload unticked | Signing runs in CI; it needs a Team ID, not a serving site | Me | After A1: set all six, then the dry run |
| A3 App Store Connect app record | Record creation and every metadata field except screenshots | Me | Under the new Team ID: free, US only, 4+ |
| A8 App Privacy answers | Nine data types from `compliance.md`; the only outside input is the Stripe confirmation | Me | Enter now; Usage Data and Diagnostics stay "No" |
| G1 Play Console app record | Console only | Me | Create the app, free, US only |
| G5 Data safety, content rating, target audience | Answers come from `compliance.md`; the two `[confirm]` items are owner decisions, not site facts | Me | Enter them, and settle the payment-processor and interaction answers |
| G3 AAB upload | The signed AAB already exists: `android/app/build/outputs/bundle/release/app-release.aab` | Me | Upload once G1 exists |
| G2 tester recruitment | Twelve addresses are an email-and-spreadsheet task; opting in needs the track, but the 14-day clock only starts then | Unstarted | Collect the twelve now |
| B10 `support@kerbgo.com` mailbox | Google Workspace, not Cloudflare — and the zone already delivers mail there: MX `1 smtp.google.com`, SPF `include:_spf.google.com` and a `google-site-verification` TXT, checked 24 Nov 2026. So this is creating a mailbox, not setting up mail. It also unblocks A7 | Me | Create it, send a test message, and add a DMARC record — `_dmarc.kerbgo.com` is empty today. Steps and the record to add are in `b10-support-mailbox.md` |
| A7 review contact details | Needs B10's mailbox and a decision on the phone number | Me | Name and phone now, email after B10 |
| R2, R4 paperwork | UZURV's written screening confirmation and the TNC-4 certificate travel by email as PDFs | Me | Request both; they are also two of the §9d inputs cto.new is missing |
| R7 inputs | Kerb's registered legal name, state of formation and a publishable address are owner records — and the name is what A1's D‑U‑N‑S must match exactly | Me | Settle them first, they gate the D&B lookup; then send them with the R2/R4 request |

Blocked until the deployment serves: **A4** (the screenshots need the real site — the
shells render the 404 instead), **B1, B2, B3, B12, G4** (every demo-login and walkthrough
step happens on the live site), **B4–B7, B14, R1, R3, R5, R6** (deployment or admin
view changes, or checks that need one), **A6** (the deletion sentence and both demo
logins are pasted from `review-notes.md` once B1 works), **G6** (the field is only worth
filling when `/delete-account` answers 200 again), `site-changes.md` §7's five acceptance
checks including the recovery test, and every other "as served" 200 check. **B11** is
blocked too, and it is easy to miss: rotating the demo password happens in the admin view
the outage took down.

Two ordering notes. A2 and B10 are the only items with other work queued behind them, so
they go first (A1 closed 24 Nov 2026), and G1 has G3 behind it. And G6 is deliberately not in this table: the URL
field is only worth filling when `/delete-account` answers 200 again, because an entered
field that 404s is worse than an empty one.

## Apple App Store

| # | Blocker | Owner | Done when |
|---|---|---|---|
| A2 | All six repository secrets absent | Me | `APPLE_TEAM_ID`, `APPSTORE_KEY_ID`, `APPSTORE_ISSUER_ID`, `APPSTORE_PRIVATE_KEY`, `APPLE_CERTIFICATE_P12`, `APPLE_CERTIFICATE_PASSWORD` set, and a dry run of *iOS release* with upload unticked produces a signed `.ipa` |
| A3 | App Store Connect app record not created | Me | App record for `com.kerbgo.app`, free, US only, age rating 4+ |
| A4 | No usable screenshots — the simulator artifact has the right size but photographs the live outage, and carries an alpha channel | cto.new | Six scenes captured at `1206 × 2622`, opaque, showing the real app on a working site; the Play set at `1080 × 2160` cannot be reused |
| A6 | Review notes still carry placeholders — the deletion path sentence and both demo logins | Me | Pasted from `review-notes.md` once B1 works |
| A7 | Review contact details not chosen | Me | Name, monitored email and a phone that answers during US business hours |
| A8 | App Privacy answers not entered in App Store Connect | Me | All nine data types entered as in `compliance.md` — collected, linked to identity, not tracked, purpose App Functionality — and the payment processor confirmed as Stripe so the Payment Info row is right. Usage Data and Diagnostics stay "No" |

**B16 opened 24 Nov 2026.** The password for the Apple Account that holds the membership was
pasted into the chat that drafts these documents. It is deliberately **not** recorded here:
this repository is public, and a transcript is the same thing as a leak. Rotate it now,
confirm two-factor authentication is on (the enrolment requires it, and the account holder
controls the certificate, the API key and the app record), and keep the new value in a
password manager. This is B11's failure mode a second time, in a more valuable account.

The CI never needs that password. What it needs is a 10-character **Team ID** — not a
secret, safe to paste anywhere — and a **Team API key**, both of which belong in GitHub
repository secrets and nowhere else. See `a1b-individual-enrolment.md`.

That address is also the **demo driver login** recorded at `site-changes.md` line 104 —
so the same personal Apple Account is both the membership holder and a login written into
a public repository, and the one a reviewer will be handed. Two consequences: rotate the
Apple Account password now rather than at the next renewal, and move the demo driver onto
a dedicated mailbox from B10 (`demo.driver@kerbgo.com`) with its own password, then update
that line. A reviewer should never hold a credential that unlocks the account controlling
the certificate, the API key and the app record.

**A1 closed 24 Nov 2026 — the membership is paid, as an individual.** The owner confirmed a
sole proprietorship trading as Kerb under a personal name, so this is the route Apple
requires: an individual enrolment needs no D‑U‑N‑S number and no public website, and the
personal legal name becomes the seller on the App Store. What remains is not enrolment but
console work — confirm **Program status Active** and copy the **Team ID** in *Membership
details*, then A2's six secrets and A3's app record. The trade and the four checks are in
`a1b-individual-enrolment.md`; `a1-apple-enrolment.md` is now the reference for the
organisation route only.

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
| G6 | Play Data deletion URL not entered | Me | `https://www.kerbgo.com/delete-account` in the field once it answers `200` again — it did on 20 Nov 2026, but every route has 404'd since B15, so re-check before submitting |

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
| R7 | `/terms` and `/privacy` identify no company, address or jurisdiction — the counterparty a rider is contracting with is unnamed. The clause is drafted in `safety-and-licensing.md` §9 (20 Nov 2026); it waits on the registered name, state of formation and a publishable address | Me + cto.new | Both pages carry the §9 text word for word: Kerb's operating entity named, and rides provided by UZURV Holdings, Inc. under TNC-4. A1 no longer waits on it — the enrolment is individual (`a1b-individual-enrolment.md`) — but the site itself still names no counterparty |
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
- **Incoming-offer screenshot** (recommended, waits on B15 like A4): the standing offer
  was live on 20 Nov 2026, so the card can be photographed in one session once the site
  serves again — it is the strongest driver-side image.
- **Live-trip screenshot** (optional): needs a real booking accepted by a driver.
- **Keystore backup**: `android/kerb-upload.jks` and its password exist in this working copy
  only; confirm the backup lives outside the repo before you rely on it.
- **Stripe Connect payments phase** (`site-handover.md` P1–P4): explicitly not a review item
  and must not start while either app is in review.
- **Site migration off cto.new** (`site-migration.md`): not needed for submission.

## Already done — do not redo

Everything below was verified against the serving site before 21 Nov 2026. While B15
is open the site answers 404 on every route, so none of it can be re-observed until
the deployment is published again.

- **In-app account deletion, end to end** (§1): as served on 20 Nov 2026,
  `/delete-account` returned `200` with `cache-control: no-store`, the flow demanded
  `DELETE` typed in, then cleared the session, redirected `/app` to sign-in and refused
  the old credentials. The route is 404 with the rest of the site while B15 is open.
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
  `.github/workflows/android.yml` now builds the debug APK on every push (green in
  *Android build* #2, `cc7da4f`), but the app has still never been run.
- **Support address on the site**: `support@kerbgo.com` on `/help` and in every
  footer, with no `ctomail.io` left. The mailbox behind it is B10.
- **Android upload asset**: signed AAB at
  `android/app/build/outputs/bundle/release/app-release.aab`; upload key created
  19 Nov (its backup is the one open question in *Open but not blocking*).
- **Play creative**: 512 icon with alpha, 1024×500 feature graphic, six 1080×2160
  screenshots (legal, though not the 9:16 recommendation).
- **Legal pages**: privacy policy (16 Nov 2026), support URL and marketing URL all live
  on 20 Nov 2026.
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
