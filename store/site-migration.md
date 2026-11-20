# Moving kerbgo.com off cto.new — what the deployed site actually uses

For whoever owns the kerbgo.com codebase. This is **not** a redesign or a hosting
change that has been decided: nothing below has been started, and the site's
source is not in this repository. It is the dependency and deployment picture
measured from the live site, so the move can be scoped against what is really
there instead of a guess.

**Do not start the cutover during an App Review or Play review.** Both shells
load `https://www.kerbgo.com` in a WebView, so the review is conducted against
whatever the site serves at that moment, and `review-notes.md` and the
screenshots describe today's site. A half-migrated site in front of a reviewer is
the cheapest way to fail under 2.1. Finish the review, then move.

## Status — checked 19 Nov 2026

| Item | State |
|---|---|
| Evidence | the `/assets/index-*.js` entry bundle plus all 69 route chunks, swept locally |
| Source code | **not available** — not in this repo, and not served by the site |
| Server-side code | **not observable** — only what the browser is told through `_serverFn` and `/api/*` |
| Migration | **not started** |

Everything below is quoted from, or directly implied by, the shipped client.
Anything about the server is marked as inference and written so it can be checked
once the source is exported.

## What the deployment is today

| Layer | What is there | Evidence |
|---|---|---|
| Framework | TanStack Start (SSR) on Vite, React 19, client-routed after hydration | entry bundle + 69 chunks, `__TSR` markers, `_serverFn` calls, compiled `e.jsx` output |
| Runtime | Bun | `/admin/storage` prints `bun run db:import <file>` as the restore command |
| Origin host | **Blaxel** | homepage response: `access-control-allow-headers: … X-Blaxel-Preview-Token, X-Blaxel-Token, X-Blaxel-Device-Fingerprint`, `access-control-expose-headers: X-Blaxel-Source, X-Blaxel-Error-Code, X-Blaxel-Dispatch-State` |
| CDN | **CloudFront** in front of Blaxel | `via: 1.1 …cloudfront.net (CloudFront)`, `x-cache: Miss from cloudfront`, `x-amz-cf-pop: SEA900-P13` |
| DNS | Cloudflare in front of CloudFront | `server: cloudflare`, `cf-ray`, `cf-cache-status: DYNAMIC` |
| Database | **Postgres** when configured, file-backed otherwise (inference) | `/admin/storage` reads `backend`, `dbPath`, `dbSizeBytes` and branches on `backend === "postgres"` |
| File storage | a **writable folder on the instance** | `/admin/storage` writes a marker file into each candidate folder on every start and reports whether it was writable |
| Builder | cto.new's engine, still attached | `new EventSource("/__engine/events")` in the shipped bundle |

The storage model is stated by the site itself on `/admin/storage`, which is the
most useful page for this move:

> Live data is wiped by a publish

> Every start of the app writes a small marker file into each of these folders,
> after reading whatever was already there. A folder holding a marker from an
> earlier start is a folder that survives.

> Take a copy of everything — One file with every rider, driver, ride, payment,
> document record and setting in it, written as SQL that recreates every table,
> index and row.

> run `bun run db:import <file>` with the hosted connection set — the script is
> replayed into the database in one transaction.

So the app is written for a host that replaces the application folder on every
publish, keeps live data outside it, and exposes a SQL dump and a transactional
import for moving that data. That is the shape a Workers deploy does not have,
and the reason this is a port rather than a re-host.

## The four libraries, as found

| Concern | What the deployed build uses | Where | Works on Workers as-is? |
|---|---|---|---|
| Card payments | **Stripe.js v3** (`https://js.stripe.com/v3/`) + **Elements** card field, `confirmCardPayment` for the bank challenge | `PlacePicker-B9rie09e.js` (loader, card-error copy), `app.book-Cd0nqN4T.js`, `grocery_._slug-HCmHjGV_.js` | **Yes** — the browser code is unchanged, and the server side is HTTPS+JSON |
| Password hashing | **argon2** | `/help`: "Passwords are stored as argon2 hashes, never as text" | **No** — native binding. Needs a JS/WASM argon2 or an outside hasher |
| Email | a transactional provider behind an **outbox table**; the provider is not named anywhere in the client | `admin.messages-*.js`: `status`, `attempts`, `provider_id`, `last_error`, `canResend`, per-message *Resend* | **Yes if the provider is an HTTPS API**; **no if it is SMTP** |
| Images | a plain **multipart upload** to `POST /api/driver/documents`, XHR with real progress; HEIC/HEIF refused; JPEG/PNG/WebP with an MB cap | `driver_.documents-*.js` | **Yes**, but the bytes need somewhere to live (R2) |

### Payments — Stripe

No card data touches the site's own code: the field is a Stripe iframe, the
database holds brand and last four only (`cards-*.js`), and `/admin/storage` says
so — "Card numbers are never in this database at all: only a payment…".

The server side is visible only as shapes, and they are unambiguous:

- A config server function returns `{ configured, publishableKey }`. Booking and
  grocery both gate the card field on it, so the key is injected per environment.
- Booking creates the payment **server-side** and returns either done or
  `requiresAction: { clientSecret, paymentIntentId }`; the browser runs
  `stripe.confirmCardPayment(clientSecret)` and re-submits with the
  `paymentIntentId`. That is the three-D-Secure step-up, and "Your bank hasn't
  confirmed that card yet. Nothing has been charged" is its failure path.
- Card errors are mapped by Stripe code (`card_declined`, `expired_card`,
  `incorrect_cvc`, `insufficient_funds`, `authentication_failure`) into
  rider-facing sentences.
- A console booking path exists too: `POST /api/dispatch/rides` takes
  `paymentMethodId`, `callerName`, `callerPhone`, `notes` and an `attemptId`
  (a client-generated `crypto.randomUUID()` reused on retry — idempotency).

For the move, the browser half needs no change: `<div id="kerb-card-element">`
and the Elements style object (hardcoded hex, no CSS custom properties) travel
as-is. The server half needs the `stripe` package running on `fetch` rather than
Node's HTTP module — check the installed version's HTTP client — plus
`publishableKey`, the webhook secret and the restricted key as per-environment
secrets, and the `attemptId` idempotency still working.

### Password hashing — argon2

`/help` states argon2, and nothing in the client hashes anything: the password
goes over TLS to a server function and the session comes back as an httpOnly
cookie ("Sessions are held in an httpOnly cookie and validated against the…").

This is the one hard blocker of the four. A native `argon2` binding does not run
on Workers, and you **cannot** quietly switch to PBKDF2 or a WebCrypto HMAC: the
existing rows are argon2, so the first sign-in after the move has to *verify* an
argon2 hash. Two routes work, and both want a test before anything is cut over:

1. **argon2 in JS/WASM** in the Worker. Fewest moving parts, but it is CPU-bound
   and the per-request CPU budget is real — measure a sign-in before committing.
2. **Hash outside the Worker** — an HTTP endpoint on the old infrastructure, or a
   small service, that verifies and re-hashes. Keeps the parameters honest at the
   cost of a hop and a second thing to run.

Either way the migration can re-hash transparently: verify with argon2, and if
the stored parameters are below target, re-hash the plaintext in hand at that
moment. That is the only chance to raise the cost parameters without a password
reset.

### Email — an outbox, not a fire-and-forget send

The provider is not named in the client, and that does not matter: the site
already treats email as a queue it can inspect and retry.

`/admin/messages` — "Every email Kerb has sent, newest first — receipts, password
resets and the notices to drivers" — shows per message: template
(`password_reset`, …), recipient, subject, created/sent times, `status`,
`attempts`, `provider_id`, `last_error`, and a **Resend** button that "Sends the
same message to the same address", disabled with a `resendBlockedReason` when it
should not be used. Templates in the chunk include `password_reset`, driver
notices (document not accepted, driving privilege stopped/restored) and the
store/order notices to the Kerb inbox.

The port needs the same table, the same provider call over HTTPS and the same
retry. Two things must not be lost: the reset link is live in the body ("a reset
email carries a live link", which is why the page shows no message contents),
and the `Can't receive mail at that address?` fallback on `/forgot-password`
points at the support address from site branding.

If the provider turns out to be **SMTP**, that does not run on Workers at all, and
an HTTPS transactional-email API becomes a prerequisite together with SPF and
DKIM on the sending domain — which is also what keeps reset emails out of spam,
and password reset is the only way back into an account.

### Images — driver documents

The one upload in the product is driver paperwork, and the client is specific:

- `POST /api/driver/documents`, `multipart/form-data`, `kind` + `file`, sent with
  `XMLHttpRequest` purely so the UI can show real progress (`Uploading… 43%`).
- HEIC and HEIF are refused before the request: "An iPhone photo saved as HEIC
  can't be read — take it as JPEG, or screenshot it first."
- JPEG, PNG, WebP, up to a cap formatted as `x.x MB`.
- The response hands back a `fileUrl` per document, rendered as
  `<img src={fileUrl}>` at 64×64.

There is **no** image library in the browser — no `sharp`, no `jimp`, no
compressor — so whatever resizing or EXIF-stripping happens, happens on the
server, and this sweep cannot see it. That decides the storage design:

| If the server does… | Then on Workers |
|---|---|
| stores bytes unchanged and serves them back | R2 + a route that streams the object |
| resizes or converts (the obvious reason for `sharp`) | R2 + a resizing step; `sharp` is unusable, so image transformations or a resize before upload |
| keeps files on the instance disk | the files are **already** at the mercy of "a publish wipes it" — pull them out with the dump before cutting over |

These are identity documents — licence, registration, insurance. They do not
belong on a public bucket path, and `fileUrl` should stay an authenticated route
rather than become a guessable R2 URL.

## What else the port has to carry

| Moving part | Detail | Port note |
|---|---|---|
| TanStack server functions | every call is a `_serverFn` POST carrying `{ data }`, answering `{ ok: true } \| { ok: false, error }` | mechanical once the framework's Workers entry is in |
| Plain HTTP routes | `/api/dispatch/rides`, `…/{id}/assign`, `…/{id}/cancel`, `/api/dispatch/drivers?lat=&lng=`, `/api/driver/documents`, `/api/admin/staff`, `/api/admin/payouts/{id}/csv`, `/api/admin/backup.db` | same handlers, new host. `/api/health` is `404` today (checked 20 Nov 2026); nothing depends on it |
| Sessions | httpOnly cookie, validated server-side on every request | keep the same name and attributes or every signed-in user and both WebView shells are signed out |
| Maps | Mapbox GL JS with a server-supplied token, plus a direct `api.mapbox.com/directions` call from the browser | unchanged; the token stays server-supplied so it can be rotated and URL-restricted |
| Web push | `/sw.js`, VAPID public key from the server, live subscription compared against it | changing the key invalidates every subscription, and sending needs ES256 signing — check the library runs on Workers |
| Admin storage page | marker files, candidate folders, SQL dump, `db:import` | rewrite for the new host, or it reports on a filesystem that no longer exists |
| Builder hook | `new EventSource("/__engine/events")` | **delete** — it is cto.new's, and elsewhere it is a permanent failing request |

## The data path — the real decision

Everything else here is mechanical. This is not, and it should be decided from
the exported SQL dump rather than from this page.

| Path | What it means | Catch |
|---|---|---|
| **Keep Postgres** | same schema, same queries, same `db:import`; the driver becomes HTTP-based instead of a TCP socket. A plain Workers deploy with the connection string as a secret — Sites' managed storage is D1/R2 only, so this is not a Sites deployment | new database host, pooling, and a provider whose HTTP driver works inside a Worker |
| **Move to D1** | SQLite via the Sites `DB` binding, with R2 beside it | every query and migration re-typed: `serial`/`bigserial`, `now()`, `jsonb`, `returning`, `on conflict`, partial indexes and transactions all differ; `db:import` rewritten; no `pg_dump`-shaped restore |

Either way the dump is the artefact that matters: **take it from `/admin/storage`
before anything else**, keep it off the instance, and confirm row counts against
the admin pages. That page exists because the app knows its host can lose the
folder; it would be a poor trade to lose the data on the way out.

## Order of work

1. **Export the source** — a repo URL or a zip out of cto.new. Nothing below
   starts without it, and no estimate is honest until the dependency list is in.
2. **Take the backup** from `/admin/storage` (SQL, every table and row), confirm
   row counts, keep it off the instance.
3. **Record the environment**, names only: the database URL, Stripe keys, Mapbox
   token, VAPID key pair, email provider key, support address, base URL. Values
   stay out of the repo, as now.
4. **Run it locally on Bun** against a copy of the dump. This is where the four
   libraries stop being inferred: the actual argon2 package, the actual email
   provider, whatever the upload route does to the bytes.
5. **Strip cto.new**: `/__engine/events`, the publish-wipes-the-folder assumption
   (marker files, boot checks, the storage page), and anything reading an
   instance path for uploads.
6. **Port the runtime**: the framework's Workers entry and Vite adapter, then
   argon2, email, documents, push and any timers — one at a time, each verified.
7. **Move the data**, then diff the admin pages against the dump.
8. **Cut over DNS** with `www.kerbgo.com` unchanged and apex redirecting to it,
   TTLs lowered a day ahead. The shells need **no rebuild** — `capacitor.config.ts`
   points at that origin and `errorPath` stays `index.html`, so the offline retry
   in `www/` keeps working and the AAB changes only if `www/` itself does.

## Acceptance checks

Run these against the cutover host before DNS points at it, as a throwaway
account. They are chosen to hit each of the four libraries.

1. Sign in and out with an **existing** account — proves argon2 verification
   against real rows, not a fresh hash.
2. Book a ride with a card that triggers the bank challenge — the Elements field
   loads, `confirmCardPayment` completes, the ride appears, and the saved card
   shows the right brand and last four.
3. Decline a card — the same rider-facing sentence as today.
4. Upload a licence — progress reaches 100%, the thumbnail renders from
   `fileUrl`, and a HEIC file is still refused with its own message.
5. Request a password reset — it arrives, the link works, the outbox shows it
   sent with a `provider_id`, and *Resend* on a failed message succeeds.
6. `/driver` with an active trip draws the map, and the route line comes from
   `api.mapbox.com` with the new token.
7. A push notification arrives on a phone that subscribed **after** the cutover,
   and no stale subscription is attempted.
8. `/admin/storage` either works against the new backing store or is gone.

## What this does not decide

- **Whether to move, and when.** The four site changes in `site-changes.md` are
  still prerequisites for store submission and are unaffected by hosting. Doing
  them first leaves the port one known-good version to reproduce instead of a
  moving target.
- **Which database** — decided from the dump.
- **Which email provider** — only that it must be an HTTPS API, with SPF and DKIM
  moving with the sending domain.
- **The merchant portal and grocery waitlist** — same site, same database, so
  they come along; the merchant app work in `submission-guide.md` is unaffected.
- **Cost** — it depends on the database path and how much traffic the CDN absorbs.
