# Live trip sharing — build spec

For cto.new. The safety page (`safety-and-licensing.md` §4) leans on this: once it
ships, `/safety` gains one sentence about sharing a trip. Nothing on the site may
promise it before then.

The goal is the smallest real version: a rider taps **Share trip** during a ride and
sends a link to someone they trust. The recipient opens it signed out, sees which
car they should be looking for and where it is, and the link stops working when the
ride ends.

## 1. What the recipient sees

One page, `https://www.kerbgo.com/trip/<token>`, **no sign-in**:

- Driver's first name, their photo, and the vehicle make, model, plate and colour —
  the same details Rule 770-X-12-.03(1)(b) already requires the app to show the rider.
- Where the trip is: **"On the way to you"**, **"On the trip"**, or **"Trip ended"**.
- Live position on the map the site already uses, plus **"Updated 8s ago"**.
- Destination, as the rider sees it.
- A short line: "This is a live location shared by a Kerb rider. If there is an
  emergency, call 911."

Not on the page: rider name, rider phone, rider email, fare, receipt, payment
details, trip history, or any link back into the rider's account.

## 2. How the link is created

- A **Share trip** button on the rider's active-trip screen, from the moment a driver
  accepts through completion.
- `POST /_serverFn/<share-create>` — authenticated as the rider, scoped to a trip
  **they own**; trip id from the session's own booking, never from the request body.
- Response: the absolute URL. Browser uses `navigator.share({url})` where available,
  otherwise writes it to the clipboard with a visible "Link copied" state.
- One live share per trip is enough. Pressing the button again returns the same link
  rather than minting more (a table row per press is fine, but the UI should show one).
- An **Unshare** control next to it, which revokes immediately.

## 3. Data

```sql
create table trip_shares (
  id          uuid primary key default gen_random_uuid(),
  trip_id     uuid not null references trips(id) on delete cascade,
  token_hash  text not null unique,   -- sha256 of the token; never store the token
  created_by  uuid not null,          -- rider user id
  created_at  timestamptz not null default now(),
  expires_at  timestamptz not null,
  revoked_at  timestamptz
);
create index on trip_shares (trip_id);
```

- Token: 32 hex characters from a CSPRNG (128 bits). Store only the hash; look up by
  hash on read. No sequential ids, no trip id in the URL, nothing guessable.
- `expires_at` = trip end + 60 minutes, and never more than 12 hours after creation.
- A row is dead when `revoked_at` is set, `expires_at` has passed, or the trip is
  completed/cancelled more than 60 minutes ago. Read checks all three.

## 4. The read endpoint

`GET /api/trip-share/<token>` — public, read-only, no cookie required. Returns:

```json
{
  "status": "en_route_pickup" | "on_trip" | "ended",
  "driver": { "first_name": "…", "photo_url": "…" },
  "vehicle": { "make": "…", "model": "…", "colour": "…", "plate": "…" },
  "destination": "334 W Magnolia Ave, Auburn, AL 36830",
  "position": { "lat": 32.6099, "lng": -85.4808, "at": "2026-11-20T19:04:11Z" } | null,
  "updated_at": "…"
}
```

- Page polls every 10 seconds while visible, pauses on `visibilitychange`.
- `position` is `null` when the last fix is older than 60 seconds, and the page says
  "Position not available right now" rather than showing a stale dot as live.
- `cache-control: no-store`, `referrer-policy: no-referrer`, `x-robots-tag: noindex`,
  no CORS headers (same origin only).
- Rate limit: 60 requests per minute per IP, 10 per minute per token, `429` beyond.
- An unknown, expired or revoked token returns **404** — not 403, and no distinction
  between "never existed" and "switched off".

## 5. Ending the share

- Completing or cancelling the trip sets `revoked_at` on every open share for it.
- After that the link shows "Trip ended" for 60 minutes (the rider's contact may
  still be looking), then 404s.
- **Unshare** revokes immediately and switches the page to 404, not "Trip ended".
- Deleting the account deletes that rider's shares — the deletion path in
  `site-changes.md` §1b gets `trip_shares` added to its list.

## 6. Where the position comes from

The driver app already reports location during a trip — that is what draws the
rider's map and drives the turn guidance. Reuse that stream:

- Read the driver's last persisted position for the trip. Do not add a second
  location channel, and do not have the share page ask the driver's browser for
  anything.
- If the driver's location is off, sharing still works and shows the trip state
  without a position. That is the honest behaviour; do not substitute the pickup
  pin as if it were the car.
- Update cadence: whatever the trip already writes, at most once every 5 seconds.

## 7. Privacy and the store answers

- The link grants **read-only access to one trip's public facts** — the same facts
  the rider sees on screen and the driver displays on the car. No new personal data
  leaves the system.
- It is not a data collection: no recipient account, no recipient email, no SMS sent
  by us. The copy step is the rider's own messaging app.
- Access is revocable and time-boxed, which is what makes it defensible in the
  privacy policy: one sentence under "Who can see my trip" — "If you share a trip,
  anyone with the link can see the driver, the car and the live location until the
  trip ends or you stop sharing."
- App Privacy / Data safety answers do not change: no new data type is collected.
  In-app 911 calling, audio recording or background location are **not** part of this.

## 8. Acceptance checks

1. Signed in as a rider with a driver on the way, **Share trip** produces a URL.
2. Open it in a private window with no session: the page loads, shows the driver's
   first name, photo, make/model/plate/colour, and "Updated Ns ago".
3. Move the driver (or wait for the next fix): the position and timestamp update
   without a reload.
4. Complete the trip: the page flips to "Trip ended" within one poll, and 404s after
   60 minutes.
5. **Unshare** mid-trip: immediate 404.
6. A token with one character changed: 404.
7. `curl -sI` on a live share: `no-store`, `no-referrer`, `noindex`.
8. The JSON for a live share contains no rider name, phone, email, fare or receipt.
9. Delete the rider's account: every share for that rider is gone.
10. `/safety` gains the sharing sentence only after 1–9 pass, and the sentence says
    "the app can send a live link", not "we watch your trip".

## 9. Out of scope for this build

In-app 911 dialling, an emergency button, 24/7 incident monitoring, SMS or email
sending by Kerb, sharing to a contact list, live chat with the driver, trip recording
and a public "safety centre" beyond the one page. Adding any of these to the page's
copy before it exists is what got the site criticised in the first place.

## 10. Order of work

1. `trip_shares` table and the token helper.
2. Share/unshare server functions, scoped to the rider's own active trip.
3. The public read endpoint with the position join.
4. The `/trip/<token>` page.
5. The Share/Unshare controls on the rider's active trip.
6. Add the deletion-path row, then the `/safety` sentence, then the privacy-policy
   paragraph.

Ships with the site, no app release: both shells load kerbgo.com, and the page works
in a mobile browser for the person receiving it.
