# kerbgo.com — the five site changes App Review and Play review need

For whoever owns the kerbgo.com codebase. Nothing here requires a new app
release: both apps load the live site in a WebView, so these changes take effect
as soon as they are deployed. The app side is already built and checked in.

Order of work: 1 (blocks both stores), 4 and 2 together (they are what makes the
driver flow reviewable), 3 (one-line copy fix), 6 (so a reviewer can finish a
booking at all). §5, the retailer Accept/Decline change, is no store's
prerequisite — it is here because it is the same owner's work.

## Status — checked against the live site, 20 Nov 2026, 16:42–17:05 UTC

All five store changes (§1, §2, §3, §4, §6) are deployed; §5 is not, and is not
a store prerequisite. What follows is what a check from this machine could
confirm on that date, item by item, with the evidence — nothing here is taken
from the build team's own report.

| § | Change | Live | Evidence, 20 Nov 2026 |
|---|---|---|---|
| 1 | `/delete-account` route, entry rows, deletion | **passes end to end** | `GET /delete-account` → `200`, `cache-control: no-store`. Signed in, the page names the account, requires `DELETE` typed into a field, and posts. The confirmation state (`?deleted=1`) reads "Your Kerb account and the data that belonged to it have been deleted … You are signed out". Afterwards `/app` → `307 /signin?redirect=%2Fapp`, no session cookie, and the old email and password at `/signin` → "Email or password is incorrect." `Delete account` rows are in both `app.index-*.js` and `driver-*.js` as `<details>` siblings of the other rows |
| 2 | `data-kerb-destination` on the trip card | **passes** | signed in as the demo driver on `/driver`: `document.querySelectorAll('[data-kerb-destination]').length` → `0` on the idle board, `1` from the instant the offer was accepted and still `1` after arrive and start, the single element carrying `334 W Magnolia Ave, Auburn, AL 36830`, then `0` again after *Complete trip*. One element, whole address, every state |
| 3 | Landing copy | **passes** | both replacement sentences are served on `/`, and `no app`, `browser only` and `download` are all gone from it |
| 4 | Standing demo offer | **passes, with three defects** | `/driver` opened with no other account acting and a `$14.80` offer arrived unaccepted: 118 N College St → 334 W Magnolia Ave, `$10.58` (75%), 10.2 mi, Accept and Decline. Accepted → arrived → started → completed, and a new `$14.80` offer was already waiting on the same board with no reload. Defects below |
| 5 | Retailer Accept/Decline and the order alarm | **not landed** | `retailer_decision` appears in none of the deployed chunks, unchanged from 19 Nov. Not a store prerequisite |
| 6 | Promo code `KERBREVIEW` | **not verified — the Demo Rider login is refused** | 20 Nov 2026, refused at 17:47 UTC and again at 17:51 UTC, in a clean tab: `demo.rider@kerbgo.com` with the password the build team published returns "Couldn't sign you in — Email or password is incorrect." at `/signin?redirect=%2Fapp` every time, so the `$0.00` flow could not be run. The client does carry the promo field, the server-side price call with `promoCode`, the "Promo … applied" line and the no-card bypass, and the team says it ran a booking on production — none of that is confirmed here |

Also dated today: `support@kerbgo.com` on `/help` and in every footer, with no
`ctomail.io` left anywhere.

### Acceptance checks — 20 Nov 2026

| # | Check | Result |
|---|---|---|
| 1 | `GET /delete-account` → `200` | **pass** |
| 2 | signed out, the page explains the flow and links to sign-in | **pass** |
| 3 | row in Account only; confirmation; cookie cleared; `/app` signed out; old credentials refused | **pass** — run end to end on a throwaway rider, which was then deleted |
| 4 | the same row on the `/driver` Account tab | **pass** in markup; the native tab split was measured on 19 Nov |
| 5 | `[data-kerb-destination]` → `1` during a trip, `0` after | **pass** — `1` accepted, on the way, on the trip; `0` after completion, with the full dropoff in the attribute |
| 6 | native Navigate on Android and iOS | **not run** — needs a device or simulator |
| 7 | landing page free of both old sentences | **pass** |
| 8 | offer waiting; accept, finish, offer waiting again | **pass** — offer present on open with nothing else acting, and a fresh identical offer waiting immediately after completion |
| 9 | the accepted trip carries the check-5 attribute | **pass** — the offer's own trip card carried `334 W Magnolia Ave, Auburn, AL 36830` |
| 10 | `KERBREVIEW` → `$0.00`, no card, booking reaches the driver | **not run** — the Demo Rider login is refused on production (see §6) |

### Defects the driver run exposed

- **The offer countdown prints raw seconds.** The board reads `604800S`, and
  `604801S` after the completed trip — seven days expressed in seconds with an
  `S` where a time belongs. It is the first thing a reviewer sees on `/driver`.
- **The turn panel still says `NEXT TURN — TO PICKUP` once the trip is running**,
  when the next turn is to the drop-off.
- **`I've arrived` stays disabled until the page has a position fix.** With no
  location in a desktop browser the driver cannot move the trip at all; the run
  above had to supply a scripted position near the pickup to get past it. A phone
  with location granted is unaffected, but a reviewer who declines location meets
  the same dead end.
- **The seeder races**, found by the build team while re-seeding: two identical
  demo rides 91 ms apart, each with a pending offer. One was retired by hand and
  the fix is not published.

### Fix status for the two defects — re-checked live 20 Nov 2026, 18:06 UTC

**Neither fix has shipped. Both defects reproduced live, signed in as the demo
driver on `/driver`.**

- **Countdown: still bare seconds.** The offer card's timer read `604798s`.
  After accepting, arriving, starting and completing that trip, the replacement
  offer's timer read `604796s`. Both are the seconds value with an `s` after it,
  which is what the deployed `driver-DvjMAkgQ.js` renders: `` `${n}s` ``. A
  humanised fix — days and hours, or `mm:ss` — is not there.
- **Turn label: still `NEXT TURN — TO PICKUP` after the trip starts.** Accepted
  the offer, tapped *I've arrived*, then *Start trip*. With the page in its
  on-trip state — the primary control reads *Complete trip* and the card reads
  "On the way to your destination" — the guidance header still said
  `NEXT TURN — TO PICKUP`. It should point at the drop-off.
- Supporting evidence, unchanged since 16:55 UTC: the entry chunk is still
  `/assets/index-Bf5939qW.js`, which embeds the whole chunk manifest, and the
  driver route still loads `driver-DvjMAkgQ.js`. A rebuilt chunk would take a new
  content hash and rewrite that manifest, so no deploy has landed since.
- The tab loaded normally at 18:06 and did not peg. The peg seen between 17:52
  and 17:59 did not reproduce, so it is not recorded as a product fault.

Re-check trigger: a new entry hash in a fresh `GET /` means something shipped;
until then these two stay open. The trip used here was completed on production
and a fresh `$14.80` offer was waiting afterwards, with
`[data-kerb-destination]` back to `0`.

### What still stands between this and a submission

- **The Demo Rider account cannot sign in.** `demo.rider@kerbgo.com` was created
  for exactly this purpose and the `KERBREVIEW` scope moved onto it, but on
  20 Nov 2026 at 17:47 and 17:51 UTC that address with the published password is
  refused at `/signin`. Either the account was never written to the production database or it
  holds a different password. Until one of those is fixed, check 10 cannot be run
  and the rider row in `review-notes.md` would lock a reviewer out.
- **`KERBREVIEW` is still unproven on production.** The build team says it ran a
  `$0.00` booking there; its earlier `$0.00` evidence was from a hosted Postgres
  test branch, and neither run is reproducible from here without a working rider
  login.
- **The build team is out of quota.** cto.new reports its weekly limit reached on
  20 Nov 2026 at 17:47 UTC, usage resuming in about 13 hours, so it cannot create
  or repair the account until then.
- **The demo driver login works** — `brian.ritter37@icloud.com`, the only
  approved driver in production, signs in, lands on `/driver` and takes the
  standing offer end to end (checks 5, 8 and 9). Trip reference `8C359807`.
- **The standing-offer seeder races.** The build team caught two identical demo
  rides created 91 ms apart, each with its own pending offer, and retired the
  duplicate by hand. Until the fix ships a reviewer can land on a board carrying
  two offers for the same ride.
- **The `support@kerbgo.com` mailbox** does not exist yet, and every support link
  on the site now points at it.

### 19 Nov 2026 check (superseded)

As of that date every item below was still missing from the live site.

| § | Change | Live | Evidence |
|---|---|---|---|
| 1 | `/delete-account` route and the entry rows | **not landed** | `GET https://www.kerbgo.com/delete-account` → `404` (host error page, `no-store`). No shipped JavaScript contains `delete-account`, so no row links to it either |
| 2 | `data-kerb-destination` on the trip card | **not landed** | Signed in as the demo driver on `/driver`: `document.querySelectorAll('[data-kerb-destination]').length` → `0`, and the string appears in none of the 69 deployed JS chunks |
| 3 | Landing copy | **not landed** | The homepage source still contains both `no app-store download` and `no app to download`; neither replacement sentence is present |
| 4 | Standing demo offer | **not landed** | Demo driver signed in on `/driver` with no active trip: the dashboard reads `Offline`, `0 trips`, shows no offer card, and only the four settings rows (`Where you are and what Kerb sees`, `Your car`, `Offer alerts on this phone`, `Refer another driver`) |
| 5 | Retailer Accept/Decline and the new-order alarm | **not landed** | `assets/retailer-*.js` has the seven order statuses and mark-ready only: no `retailer_decision`, no accept or decline control, no alarm, and the card shows neither the items nor the prep time |
| 6 | Promo code `KERBREVIEW` | **not landed** | No `KERBREVIEW` string, and no `promo` identifier of any kind, in the deployed client — the payment step has no promo input |

How that was measured, so a later check can repeat it: the site is a
client-rendered SPA (`/assets/index-*.js` plus 69 chunks), so served HTML proves
nothing about `/app` or `/driver`. The bundle and every chunk were swept for the
five markers, and `/driver` was loaded in a browser signed in as the demo driver
(no trip accepted, nothing written to the account).

Two things this check cannot decide:

- **Whether an offer appears once the driver is online.** The dashboard carries a
  *Go online* control and the account is approved, but going online puts a live
  account into real dispatch, so it was not pressed. §4a already requires an offer
  "every time the demo driver opens it", which reads as regardless of online
  state; whoever deploys it should confirm the offer is visible offline, because a
  reviewer opening a fresh install may never press *Go online*. Acceptance check 8
  still covers the online case.
- **Whether the demo driver is still the account described in `review-notes.md`.**
  It signs in, is admin-approved, and has a vehicle (Kia K4), but its saved state
  is whatever it was after earlier testing — the rider-side booking, saved place
  and past trip in the notes were not re-verified here.

---

## 1. In-app account deletion — blocks Apple 5.1.1(v) and Play's account-deletion policy

**Fixed, verified live 20 Nov 2026.** Until then the only deletion path was "email
us" in the privacy policy and this route returned **404** (checked 19 Nov 2026).
Both stores require deletion to be **initiated in the app**. Email is not
acceptable as the only route. A page on the site counts as "in the app", because
the shells load kerbgo.com itself.

### 1a. Route contract

| Item | Requirement |
|---|---|
| URL | `GET https://www.kerbgo.com/delete-account` (also serve on `kerbgo.com`) |
| Auth | Page must load signed out as well as signed in. Signed in → deletion flow. Signed out → short explanation, a link to sign in (`/signin?next=/delete-account` or the site's equivalent), and the email fallback. |
| Host | Stay on kerbgo.com. No redirects to another domain, no `target="_blank"`, no `window.open` — the shells only keep kerbgo.com inside the app WebView. |
| Caching | `Cache-Control: no-store` on the page and on the confirmation response. |

This URL is also the value for **Play Console → App content → Data deletion →
Data deletion URL**. It has to work for a signed-out visitor.

### 1b. Deletion behaviour

- **Confirmation step required.** Not one tap: re-enter the password, or type
  `DELETE` into a field, then a final button. Apple rejects a single accidental
  tap path.
- **Actually delete**, server-side, for that signed-in user only (never an id
  from the request body): account row, saved addresses, session tokens, driver
  vehicle record, uploaded driver-document photos, grocery-waitlist entry for
  that email. Detach/delete the payment customer or saved card at the payment
  processor. Completed trip and payment records may be retained only where tax
  or fraud law requires it — say so on the page and in the privacy policy.
- **Invalidate every session** for the account and clear the session cookie in
  the response, then land on a signed-out confirmation state (e.g.
  `/delete-account?deleted=1`, or a `/delete-account/done` page). Reloading
  `/app` afterwards must show the signed-out screen; signing in again with the
  old credentials must fail.
- **POST only** for the destructive action; a `GET` must never delete. A
  signed-out POST returns 401 or redirects to sign-in — not a 500.
- Idempotent: a second POST after success shows the confirmation state.

### 1c. In-app entry points (markup the shell already reads)

The shells add no native "delete" item. The Account tab renders the site's own
`<details>` rows, so the entry point is markup on the two authenticated home
pages.

**Rider — `/app`:** add the block as the **last `<details>` child of the container
that already holds `Your trips`, `Saved places`, `Payment methods` and `Help and
your account`**. Give it no container of its own:

```html
<details>
  <summary>Delete account</summary>
  <p>Deleting your account removes your profile, saved addresses, trip history and
     saved payment method. This cannot be undone.</p>
  <a href="/delete-account">Continue to delete your account</a>
</details>
```

Placement is what decides the tab, not the page. The shell hides that one
container on Home, then splits the rows inside it by label: `Your trips` goes to
the Trips tab, every other `<details>` row goes to Account. So with the row
inside the shared container it is Account-only:

| View | Result |
|---|---|
| Home | container hidden — no delete row |
| Trips | container shown, non-`Your trips` rows hidden — no delete row |
| Account | container shown, all other rows visible — delete row sits below the existing rows |

The only way to get this wrong is to give the row its own container or a sibling
block: the shell then shows it on Home, where a destructive entry point has no
business. If the page renders its rows in more than one container, put the delete
row in the one holding `Your trips`.

That is measured, not inferred: on the Android debug build with a signed-in rider
(19 Nov 2026), Home rendered none of the four rows, Account rendered all but
`Your trips`, and Trips rendered only `Your trips`.

**Driver — `/driver`:** add the same `<details>` block to the list that also
holds the `Where you are and what Kerb sees` row. The native Account view shows
that list and hides it on Home, so the row appears on the Account tab next to
Sign out with no app change.

Constraints the native layout depends on — do not rename or restructure these
while adding the row:

- `/app` and `/driver` must stay at those exact paths (the shell only patches
  those two).
- Rider rows must remain `<details>` elements inside one shared container; that
  is how the Account/Trips tabs split them, and the delete row relies on it.
- Driver controls must keep the exact text `Earnings & trips`, `Documents`,
  `Help`, `Sign out`, and a `<summary>` beginning `Where you are and what Kerb
  sees`. If any one is missing, the shell skips the whole native Account section.
- Do not restructure that container into several siblings, and do not move the
  existing rows into a new element, to make room for the delete row.

### 1d. Policy and listing follow-up

- Privacy policy, "How long we keep it": replace the email-only sentence with
  "You can delete your account and its data in the app: Account → Delete
  account, or at https://www.kerbgo.com/delete-account. If you cannot sign in,
  email …". Keep the email route as a fallback for locked-out users.
- Play Console Data deletion URL: `https://www.kerbgo.com/delete-account`.
- App Review notes: fill the placeholder in `store/review-notes.md` with
  "Account tab → Delete account → confirm".

---

## 2. `data-kerb-destination` on the driver trip card — fixes the Navigate flow

The native Navigate control appears only on `/driver`, and reads the dropoff at
the moment the driver taps. It uses exactly this script (identical in
`android/app/src/main/java/com/kerbgo/app/MainActivity.java` and
`ios/App/App/SceneDelegate.swift`):

```js
var el = document.querySelector('[data-kerb-destination]');
if (!el) return '';
var value = el.getAttribute('data-kerb-destination') || el.textContent || '';
return value.replace(/\s+/g, ' ').trim();
```

That gives the rules:

- **Exactly one** element carrying the attribute on `/driver`. `querySelector`
  takes the first in document order, so a second (a saved place, a past trip, a
  marketing address) can silently win. Keep it to the active trip card.
- **The attribute value wins** over the element's text. Put the whole address in
  the attribute, single line, no label, no HTML: `123 E Magnolia Ave, Auburn, AL
  36830`. A blank attribute falls back to the element's text content.
- **Present whenever a trip is active** — from offer accepted through arrive,
  pickup and complete; the address does not change across those states.
- **Absent when there is no active trip** (offline, between trips, after
  complete/cancel). With no element the app says "Destination not available"
  instead of navigating somewhere stale — which is the correct fallback, but
  during a live trip it is a visible failure, and a review risk under 2.1.
- It must exist in the rendered DOM (not inside `<template>`, and not in a
  closed shadow root). It is read on tap, so no event or observer is needed; it
  just has to match what is on screen.

Example on the trip card:

```html
<div class="…">
  <p>Dropoff</p>
  <p data-kerb-destination="123 E Magnolia Ave, Auburn, AL 36830">
    123 E Magnolia Ave<br>Auburn, AL 36830
  </p>
</div>
```

Do not add it to rider pages, `/driver/earnings` or `/driver/documents` — the
Navigate control does not exist there.

---

## 3. Landing copy — the site currently says there is no app

Both apps open on the marketing landing page, and it tells the user not to
download an app. That is now false, and it is the first thing a reviewer reads.

Two sentences on `https://www.kerbgo.com`:

1. Hero paragraph, "Local rides" section:

   > Kerb is a local rides service: pick two places, see the fare before you
   > book, and pay by card — **no app-store download**. Local drivers who live
   > where you live.

   Replace with:

   > Kerb is a local rides service: pick two places, see the fare before you
   > book, and pay by card — **in your browser or the Kerb app**. Local drivers
   > who live where you live.

2. "How it works" intro:

   > Rides are live today. Three steps, in your phone's browser — **no app to
   > download**.

   Replace with:

   > Rides are live today. Three steps, in your phone's browser or the Kerb app.

Also sweep the page for any other "no app", "browser only" or "download"
phrasing and remove it. Do **not** add "Download now", store badges, a QR code
or a countdown: promoting an unreleased app is its own review problem, and store
screenshots may not contain badges. Once both listings are live, a plain "Get
the app" link to the store listing is fine in place of the removed sentence.

---

## 4. A standing demo offer on `/driver` — so a reviewer can reach the driver flow

Today an offer exists only while dispatch is running. An approved driver who signs
in to an idle system lands on an empty dashboard, which is why the incoming-offer
screenshot has never been capturable (`listing.md`) and why the driver half of the
app is the weakest part of a review: the reviewer is told to accept an offer and
there is none to accept. The demo driver has to find one waiting every time
`/driver` opens, without another user booking anything first.

### 4a. What the offer must be

| Item | Requirement |
|---|---|
| Presence | A pending offer on `/driver` every time the demo driver opens it, before any other account acts |
| Lifetime | At least 7 days, or re-seeded by a scheduled job — an offer that quietly expires is the likeliest thing to break between setup and review |
| Pickup | `118 N College St, Auburn, AL 36830`, geocodable by the site's own place picker |
| Dropoff | `334 W Magnolia Ave, Auburn, AL 36830`, geocodable — this is the address that has to reach the trip card |
| Fare | `$14.80`, with the driver's 75% share shown the way a real offer shows it |
| Scope | The demo driver account only. No real driver sees it and it never enters real dispatch |

### 4b. How to implement it

A per-account demo flag or an admin "re-seed demo offer" action is enough. Take
whichever fits the existing dispatch code with less surgery:

- **Flag on the account** — when the demo driver loads `/driver`, ensure a pending
  offer exists for them, creating one if the last was accepted, completed, cancelled
  or expired.
- **Re-seed action** — a control in the admin surface that creates the same offer.
  Less code, but it relies on a person running it after every change, so it belongs
  next to the pre-submission re-check in `review-notes.md`.

Either way the offer must be a **real offer in the normal dispatch model**, not a
hard-coded card in the template: accepting it has to go through the product's own
accept path and open the real trip screen. A convincing-looking card that dead-ends
at acceptance is worse than the empty dashboard.

### 4c. What it depends on

- **§2's `data-kerb-destination`.** Accepting the offer is what proves the native
  Navigate control works, so the trip card it opens must carry the dropoff in that
  attribute. Without both, the offer leads straight to "Destination not available".
- **No ordering requirement.** The reviewer must not have to book as the rider first,
  or act on one device to make an offer appear on another.

Pointing a rider booking at the demo driver while they are online is a nice extra —
the reviewer who does book finds the trip waiting on the driver side — but it is not
the path to rely on. The standing offer is.

The companion piece of demo state, promo code `KERBREVIEW` (100% off, bypassing
the saved-card requirement so a booking costs nothing), is specified in §6. It is
new work rather than configuration: the deployed client has no promo concept at
all, so "if promo codes cannot express it today" resolves to — they cannot.

---

## 5. Retailer Accept/Decline and the new-order alarm — not a store prerequisite

An order lands on `/retailer` with a mark-ready button and nothing else: the shop
cannot say yes or no, cannot see what to cook, and nothing calls the counter when
an order arrives. The patch is written against the deployed route and committed
in `store/merchant-order-response.md`; **none of it is deployed.**

- **Decision.** `retailer_decision` (`pending` | `accepted` | `declined`) on the
  order, Accept/Decline inside the existing order card, decline with a short
  reason. Do not reuse `status = "accepted"` — that is the courier accepting the
  delivery.
- **Card.** The order's lines and its prep time, so the shop decides from the card
  rather than opening the customer record.
- **Alarm.** A loud two-beep tone every four seconds and a flashing tab title
  while any order is unanswered, armed with one tap, silent once the page is
  closed or the phone is locked — the limit that justifies the merchant app.
- **Before it ships:** declining must refund through the same path as a customer
  cancellation and release the courier, and order writes must be clear of the
  `503 Upstream unavailable` seen on 19 Nov 2026.

## 6. Promo code `KERBREVIEW` — so a reviewer can book without a card

A reviewer cannot be asked for a real card. Without a code path the only way to
finish a booking is to enter payment details, and the walkthrough in
`review-notes.md` stops at the payment step. The behaviour the demo needs is
specified there; this is the site work behind it.

The deployed client has no customer-facing discount mechanism of any kind:
`promo`, `coupon`, `voucher` and `discount` appear in none of the 69 chunks, and
the payment step has no field for one. The only credit-like surface is
`/admin/referrals`, which is driver referral payouts — a ledger of money owed, not
a discount on a fare. This is a new feature, not a configuration change.

### 6a. What the code must do

| Item | Requirement |
|---|---|
| Code | `KERBREVIEW`, matched case-insensitively and trimmed |
| Discount | 100% of the total, fare and booking fee |
| Scope | The demo rider account only — not a public code, not single-use |
| Effect | Total `$0.00`, a visible `Promo KERBREVIEW applied` line, no payment intent, no charge, no receipt amount |
| Card | **Bypasses the saved-card requirement**: the card field is not shown and none is requested |
| Invalid code | A plain message beside the field, with the card requirement left exactly as it is today — a wrong code must never quietly book a full-price ride |
| Ride | Dispatched like any other booking, so the driver side still has a trip to accept |

The demo rider must have **no card on file** when it is used. Working with an
empty card list, not merely with a card that is never charged, is the whole point
of the code.

### 6b. Where it goes

Follow the existing booking path rather than a parallel one:

- A promo table (`code`, `percent_off`, `active`, plus whatever scope column the
  schema already uses) with one row for `KERBREVIEW`, scoped to the demo rider.
- Validation inside the booking server function, next to the fare calculation, so
  the discount is applied where the total is computed and the stored booking
  agrees with what was shown.
- The input on the payment step, beside the card field, because that is where a
  reviewer looks for it. When the code applies, the card field and its
  requirement are skipped for that booking only.
- Reuse the branch the payment step already takes when Stripe is not configured:
  the booking path already allows a booking with no card (`configured` false ⇒ no
  card required), so ride on that rather than adding a second no-card path. Do
  not show that state's "Card payments aren't switched on yet" copy when a code
  is applied — to a reviewer it reads as a broken payment step.
- A booking without a promo code keeps today's behaviour exactly: card required,
  payment created, charged on completion.

### 6c. What must not happen

- Do not make it a general discount. It must not appear in any listing, marketing
  copy or public surface, and it must not work on a real account.
- Do not drop the booking record, the dispatch or the driver offer because the
  total is zero — the reviewer's next step is the driver flow.
- Do not seed the booking by hand: fares, receipts and earnings aggregates are
  derived, and a hand-edited row will disagree with them.

## Acceptance checks

Run these against the live site before telling the app side it is ready.

1. `curl -sI https://www.kerbgo.com/delete-account` → `200`, not `404`.
2. Signed out, the page loads, explains the flow, links to sign in, and does not
   crash or 500.
3. Signed in with a throwaway rider account: the Account tab shows **Delete
   account**, the Home and Trips tabs do not; the flow asks for confirmation;
   after confirming, the cookie is cleared, `/app` is signed out, and the old
   credentials no longer sign in.
4. The same row appears in the driver Account tab on `/driver`.
5. On `/driver` with an active trip, in devtools:
   `document.querySelectorAll('[data-kerb-destination]').length` → `1`, and its
   value is the full dropoff address. After completing or cancelling:
   → `0`.
6. Native check, Android: open the trip, tap **Navigate**, choose Google Maps —
   it opens with the dropoff address. Same on iOS with Apple Maps. No
   "Destination not available" during a live trip.
7. Landing page source contains neither `no app-store download` nor `no app to
download`.
8. Signed in as the demo driver, `/driver` shows a pending offer with no other
   account having acted. Accept it, finish or cancel the trip, reopen `/driver` →
   an offer is waiting again. A different driver account sees no such offer.
9. The trip card that offer opens is the same one from check 5: one
   `[data-kerb-destination]`, carrying the dropoff.
10. Signed in as the demo rider **with no card on file**: entering `KERBREVIEW`
    at the payment step makes the total `$0.00`, shows the promo line, hides the
    card field, and completes the booking — which then reaches the demo driver as
    an offer. Without the code the card field is back, and the code is refused on
    any other account.

## What does not change

- No app build or store update is needed for any of these five; they go live
  with the site.
- The shell never modifies the site's markup in the database or on the server.
  It only hides a few blocks on the two home screens, so `/delete-account` and
  the `data-kerb-destination` contract must live in the site's own templates.

---

## 7. App shell — a 4xx shows the branded screen, not the platform page

**Added 21 Nov 2026, verified in simulator run #7.** This is the only change here that
lives in the shells rather than the site. The outage (B15) exposed it: when
kerbgo.com answered 404, the app painted WebKit's own `There is no site at this
address` text instead of the branded screen `www/index.html` that was built for exactly
this moment.

### Why it happened

Capacitor's `server.errorPath` fires on a *failed load*. A 4xx is a successful load of
an error document, so on iOS `errorPath` never ran at all. Android is different: its
`BridgeWebViewClient.onReceivedHttpError` already swaps in `errorPath` for a main-frame
HTTP error. So the two platforms had opposite problems — iOS showed nothing branded,
Android showed it but could never leave it.

### What each shell does now

| | iOS | Android |
|---|---|---|
| Detecting the 4xx | One native `HEAD` of the loaded URL, once per finished load and again on foreground | None — Capacitor swaps the page on a main-frame HTTP error, and the shell swaps its `WebViewClient` for a subclass so 401 and 403 do not trigger it |
| Showing the screen | Loads the bundled `index.html` with `?reason=http` | Capacitor's own `errorPath` load |
| Leaving it | Polls `https://www.kerbgo.com/` every 5 s while the screen is up; reloads on any status below 400 | Same poll, same 5 s interval, plus an immediate check on `onResume` |
| Retry button | Reloads the site; if it is still failing the screen comes straight back | Same |

The error page suppresses its own auto-retry when `?reason=http` is present. Without
that, a 404 resolves a `no-cors` fetch, so "connected" was indistinguishable from
"still broken" and the screen would leave and return in a loop. The probe is now a
CORS request that only accepts a status below 400.

Two bugs were found by running it, not by reading it, and both are worth remembering:

- `evaluateJavaScript` returns the *promise object*, not its resolved value, so a
  `.then` chain always read as `0` and the first shipped probe never fired.
- `callAsyncJavaScript` has no completion-handler form in this SDK, so the second
  attempt failed to compile (`extra trailing closure passed in call`).

The final iOS code avoids both: no JavaScript in the detection path at all, just one
native `HEAD` of the document URL.

Three statuses are deliberately not treated as the outage, on **both** platforms:
`405`, because it means the server does not implement `HEAD`; and `401` and `403`,
because those mean the site answered and withheld the document — a session or
bot-protection matter that a Retry card would misreport as downtime. iOS excludes them
in its own status check. Android has no check to adjust, so it instead replaces the
bridge's `WebViewClient` (`Bridge.setWebViewClient` is public) with a `BridgeWebViewClient`
subclass that returns before calling `super` on a main-frame 401 or 403; every other
status, and every subresource, still goes through Capacitor unchanged. The cost is that
Capacitor's `WebViewListener` callbacks are not fired for those two main-frame
responses — nothing in this app subscribes to them.

### Evidence

| Run | Commit | Result |
|---|---|---|
| *iOS simulator run* #4 | `ebeb74b` | green, screenshots show the raw platform page |
| #5 | `8b6bfa6` | green, still the raw page — the promise bug |
| #6 | `bbc2f9d` | **failed to compile** — `callAsyncJavaScript` |
| #7 | `6ba89ee` | **green**, both frames show the branded card: "Kerb is temporarily unavailable", body copy, **Try again**, "The Kerb service is not answering." |
| #8 | `6654194` | **green**, same card, produced by the simplified native-`HEAD` check with no JavaScript in the detection path |

Run #8 is the evidence for the code that is on `main`. Its two frames are
`1206 × 2622` and still carry an alpha channel, so they remain unusable as App Store
screenshots (A4) — they are evidence for this fix, not listings.

### Not verified

Android compiles (`:app:compileDebugJavaWithJavac`, exit 0) but **has never been run**:
no device or emulator here, and the repository has no Android workflow to run it in.
Its detection is Capacitor's own, so the untested parts are the recovery poll, the
`onResume` re-check and the new 401/403 client swap — the last of which can only be
confirmed by pointing the app at a page that returns 401 or 403.

### Acceptance checks, when the site is back

1. With the site healthy, both apps open the site normally and no `HEAD` polling is
   visible in a proxy after the page settles.
2. Point the app at a 404 (the current outage does this for free): the branded card
   appears within a second or two, with **Try again**.
3. Tap **Try again** while still failing: the card returns rather than parking on the
   platform text.
4. Republish the site, then wait without touching the phone: the app leaves the card
   and loads the site on its own within ~5 s.
5. Background and foreground the app while the site is failing: the card is still
   there, and the check runs once per foreground rather than on a timer.
