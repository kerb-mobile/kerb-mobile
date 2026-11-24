# Review notes

Paste-ready text for App Review and Play. The two demo accounts are specified
below; fill the bracketed credentials at submission time and never commit them to
this repository.

The point of the demo state is that a reviewer never meets an empty screen: no
zero-trip account, no offline-by-default driver with nothing to accept, and no
payment form that asks for a real card.

## Demo accounts — what must exist

Two accounts, both active at review time, both pre-verified.

| Account | Sign-in | Must be able to |
|---|---|---|
| Rider | **UNVERIFIED — this login does not work.** `demo.rider@kerbgo.com`, password held outside this repository. Sign-in refused at `/signin` on 20 Nov 2026 at 17:47 and 17:51 UTC | See a saved place and a past trip; book a ride to $0.00 without a card. **Not demonstrated** — and the account is new, so it has no saved place and no past trip yet |
| Driver | **[not created yet]** target `demo.driver@kerbgo.com` — paste the console credentials only once that account signs in, is approved with all three documents, and holds the standing offer (`demo-driver-migration.md`) | Land on an approved dashboard, see a pending offer, accept it, read the dropoff with Navigate |

**Sign in with both rows before submitting.** On 20 Nov 2026 the rider address
above refused its password at `/signin` — the row is filled in because that is
the intended account, not because it has been shown to work. That account is
also new, so the saved place and the past trip it is meant to carry still have
to be created and checked.

Both sign in with **email and password only**. No SMS code, no email
confirmation link, no 2FA prompt — a reviewer cannot receive either. Addresses
must not be shared with a real customer, and the passwords must not rotate until
review is complete.

### Rider

- Display name **Demo Rider**; phone number in the reserved `555-0100`–`555-0199`
  range. No real customer data anywhere on the account.
- **One saved place**: `Home — 118 N College St, Auburn, AL 36830`. This is the
  address the shell turns into a destination chip, so it must be a geocodable
  address the site's own place picker already resolves.
- **One completed trip** in history, roughly two weeks old, Auburn → Opelika,
  total around `$12.40`, with its receipt reachable from `Your trips`. The shell
  reads recent trips (`a[href*="/app/rides/"]`) for chips, so the trip must
  render as a normal row there — not a synthetic entry somewhere else.
- **No real card on file.** The no-charge path below must work without one.
- Account deletion reachable (`Account → Delete account`), so the 5.1.1(v)
  answer in the notes matches something a reviewer can actually find.

### Driver

- **Approved**, not pending: the approval step a person performs has been done,
  and licence, registration and insurance are uploaded **and approved**.
  Documents must be synthetic images — no real licence, plate or address.
- **Vehicle**: whatever the demo driver's own record holds. On 19 Nov 2026 that
  was a **`Kia K4`** (`site-changes.md`, signed in as the demo driver); these notes
  used to name a `2019 Toyota Camry`, silver, plate `DEMO123`, which production
  never had. Do not paste a model from memory: fill the year, colour and plate
  from the record at submission time, and confirm the plate is obviously
  synthetic (`DEMO…`) — if it is not, change it on the record before review
  rather than describing it here. The record must exist so the dashboard's own
  vehicle line is not blank.
- **One completed trip** in earnings history so the Earnings tab has a week and
  an all-time figure rather than zeros. Use the 75% split that the product
  actually applies.
- **One pending offer that is always visible.** This is the part that needs a
  site change: today offers only exist while dispatch is running, which
  `listing.md` already records as the reason the incoming-offer screenshot could
  never be captured. The demo driver needs an offer that:
  - is present every time `/driver` is opened, without a second user booking
    anything;
  - does not expire for at least 7 days, or is re-seeded by a scheduled job;
  - carries a pickup (`118 N College St, Auburn, AL`) and a **real, geocodable
    dropoff** (`334 W Magnolia Ave, Auburn, AL`), because accepting it is what
    proves the Navigate control can read a destination;
  - shows a plausible fare (`$14.80`) and the driver's 75% share.
  A per-account demo flag or an admin "re-seed demo offer" action is enough; it
  must not require the reviewer to do anything in a particular order.

### No-charge path through booking

The reviewer must reach a confirmed booking without a card and without moving
money. Add one promo code:

| Field | Value |
|---|---|
| Code | `KERBREVIEW` |
| Discount | 100% of the total, fare and booking fee |
| Use | Re-usable for the demo rider account (not single-use) |
| Effect | `$0.00 due`, a visible "Promo KERBREVIEW applied" line, no charge, no receipt amount collected |
| Requirement | The code path **bypasses the saved-card requirement** — no card is added to the account and none is requested at checkout |

This is new site work rather than a configuration change — the promo code does not
exist in the deployed client at all. It is specified in `site-changes.md` §6.

If the reviewer does book, the request is dispatched like any other. Pointing it
at the demo driver (who is online) is the nicest version: the reviewer books as
the rider, signs in as the driver, and finds the trip waiting. Do not make the
review depend on it — the standing offer above is the deterministic path.

## Where each piece is created

| Record | Where |
|---|---|
| Both accounts | Normal signup on `kerbgo.com`, then verify the address out of band so no email step remains at sign-in |
| Rider saved place | Rider app → `Account → Saved places → Add` |
| Rider past trip | Complete one trip with the demo pair through the product, not by inserting rows — receipts, chips and earnings aggregates are derived, and seeded rows tend to disagree with them |
| Driver approval + documents | The existing admin approval step |
| Driver vehicle | The vehicle form in driver onboarding — on the replacement driver account (`demo-driver-migration.md`), not the owner's personal one |
| Standing offer | The demo flag or admin re-seed action described above |
| `KERBREVIEW` | The promo table added in `site-changes.md` §6 — no promo surface exists on the site today |

## Apple — App Review Information → Notes

```
Kerb is a ride-booking app for local rides in the United States. The app is a
thin native shell around our own web application (kerbgo.com), with native
controls layered on top for the parts a browser cannot do well:

- A native "Navigate" control on the driver dashboard, with an action sheet that
  hands the trip destination to Apple Maps, Google Maps or Waze.
- Device location for pickup and trip following.
- A native tab bar and driver dashboard layout, so the app does not feel like a
  website in a frame.
- The native camera for driver document photos.

Both demo accounts are pre-verified and sign in with email and password only, so
no code or confirmation email is needed.

How to review the rider flow:
1. Sign in with the rider demo account below.
2. The home screen asks for a pickup and a destination. Tap the saved "Home"
   place under the destination field, or type any two US addresses (for example
   "West Magnolia Avenue, Auburn, AL" and "East Thach Avenue, Auburn, AL").
3. The fare is shown before booking, itemised by base, distance, time, minimum
   and booking fee. The Account tab already holds a past trip and its receipt.
4. Tap "Book ride" and enter promo code KERBREVIEW at the payment step. The
   total becomes $0.00 and no card is charged, so the flow can be completed
   without entering payment details.

How to review the driver flow:
1. Sign out, then sign in with the driver demo account below.
2. The dashboard shows the driver's approved status, today's earnings, and a
   pending ride offer. The other tabs are Earnings and Account.
3. Tap the offer to accept it. The trip card opens with the pickup and dropoff.
4. Tap Navigate to see the native action sheet: Kerb's own map, Apple Maps,
   Google Maps or Waze. The dropoff address carries across.

Rider demo account:    UNVERIFIED — demo.rider@kerbgo.com was refused by /signin on
                       20 Nov 2026 (17:47 and 17:51 UTC). Do not publish this line
                       as a working login. Password: [paste at submission time]
Driver demo account:   [email] / [password]
                       Not created yet. The demo driver moves to a dedicated
                       address (demo-driver-migration.md); do not publish this
                       line until that account signs in and is approved.

Notes:
- The app targets iPhone only (TARGETED_DEVICE_FAMILY = 1).
- Export compliance: the app uses no encryption beyond standard HTTPS; the
  Info.plist declares ITSAppUsesNonExemptEncryption = false.
- Account deletion is [describe the in-app path here — required by 5.1.1(v)].
- No real payment is taken during review: the rider account's demo code zeroes
  the fare, and no card is stored on either account.
- Location is used only while the app is open. There is no background location.
- No third-party advertising or analytics SDKs are included.
- Support: support@kerbgo.com — https://www.kerbgo.com/help
```

The account-deletion line is the 5.1.1(v) answer. The path to describe is
`Account → Delete account → confirm`, which ships with the site change in
`site-changes.md` §1 — fill the sentence in once that is deployed, not before.
The driver walkthrough expects the trip card's `data-kerb-destination`
(`site-changes.md` §2) for the Navigate control to read the dropoff, and the
standing offer above to make that step reachable.

**Support address.** The site hardcodes no address at all: every one it shows is
interpolated from the `supportEmail` value in **`/admin/branding`**, and as of
20 Nov 2026 that field holds **`support@kerbgo.com`**. That is not an inference: the
live home page serves its branding record in the server payload, and it reads
`supportEmail:"support@kerbgo.com"`. Every page that shows an address — `/`,
`/help`, `/privacy`, `/terms`, `/delete-account`, `/signin` — renders it, and none
contains `ctomail`. The mailbox behind it still has to be created in
Google Workspace — until it exists, a reviewer who follows the site's own support
link reaches nobody (`status.md` B10). Use the same address in the Play listing.

## Apple — App Review Information → Contact

Use the account owner's name, a monitored email and a phone number that answers
during US business hours. Reviewers do call.

## Google Play — App access

Choose **All or some functionality is restricted**, then add both accounts:

```
Rider account — full ride-booking flow
Username: UNVERIFIED — demo.rider@kerbgo.com (sign-in refused 20 Nov 2026)
Password: [paste at submission time — not committed]
Instructions: Sign in at the Welcome screen (no verification code is sent). Tap
the saved "Home" place under the destination field, or enter any two US addresses
to see a fare estimate. At the payment step enter promo code KERBREVIEW: the
total becomes $0.00 and the booking completes without a card. The Account tab
holds a past trip and its receipt.

Driver account — driver dashboard, offer, Navigate and earnings
Username: [email]
Password: [password]
Instructions: Sign in at the Welcome screen. The dashboard opens on /driver with
an approved status and a pending ride offer. Accept the offer to open the trip,
then tap "Navigate" for the native map chooser. The Earnings tab shows pay
details.
```

## Google Play — Review notes (optional field)

```
Kerb books local rides in the United States. The app wraps our web application
with native controls: a native Navigate action sheet (Apple Maps, Google Maps,
Waze), device location, a native tab bar and the system camera for driver
documents. No ads, no third-party analytics, no in-app purchases — rides are paid
with a card through our payment processor.

Both demo accounts are pre-verified and sign in with email and password only. No
real payment is taken during review: promo code KERBREVIEW zeroes the fare on the
rider demo account and no card is stored on either account.

Location permission is used only while the app is open (foreground only).
```

## Reviewer walkthrough, in order

Do this yourself the day before submitting, on a clean install, signed out:

1. Fresh install, no session → Welcome screen, "Create a free account" visible.
2. Sign in as the rider → rider home, three tabs, no console errors.
3. Destination field → the saved "Home" place appears as a chip; tapping it
   fills the address.
4. Account tab → the past trip is listed with its receipt.
5. Enter a pickup and a destination → fare card with the itemised breakdown.
6. Tap "Book ride" → payment step, enter `KERBREVIEW` → `$0.00` and a promo line;
   complete the booking without any card being requested.
7. Sign out, sign in as the driver → `/driver`, an offer is waiting, Navigate pill
   above the tab bar.
8. Accept the offer → trip card with pickup and dropoff.
9. Tap Navigate → map options; choose Google Maps and confirm the dropoff carries
   across. If it says "Destination not available", the site's trip card is
   missing `data-kerb-destination`.
10. Earnings tab → `/driver/earnings` renders, Navigate pill disappears.
11. Turn off location in Settings, reopen → the app still works and the location
    chip explains the state instead of hanging.
12. Airplane mode, reopen → the app recovers on its own when the connection comes
    back, instead of parking on the offline screen.

## Re-check this list the day before submitting

- Both accounts still sign in, with no verification step.
- The rider still has the saved place and the past trip.
- The driver is still approved, still has the vehicle, and still has an offer
  waiting on `/driver` — a standing offer that quietly expired is the most likely
  thing to break between setup and review. Read the vehicle line while you are
  there and paste its year, colour and plate into the notes, so the two cannot
  drift apart again.
- `KERBREVIEW` still zeroes the total and still bypasses the card requirement.
- Neither account has a real card, real licence or real phone number attached.
