# Plan B — recovering kerbgo.com if cto.new misses 25 Nov

**Drafted 24 Nov 2026.** A decision note, not a plan of record. It is triggered only if
the 25 Nov 2026, 16:18Z deadline in `cto-new-outage.md` passes with no publish and no
source, database export or account handover. Read it beside `site-migration.md`, which is
the technical port plan and is still correct — this note records what stopped being
possible after the site stopped serving.

## What changed since `site-migration.md` was written (19 Nov)

That document was measured against a serving site, and two of its steps depend on that:

- its order of work, step 2 — "take the backup from `/admin/storage` (SQL, every table and
  row)" — cannot run from outside any more.
- the same for `/api/admin/backup.db`, which it lists among the plain HTTP routes.

Both are 404 with the rest of the site (B15). So plan B does not start with an export; it
starts with other people's accounts, and the data export becomes something to *ask for*
rather than something to take.

## Who holds what — checked 24 Nov 2026, 16:35 UTC

| Layer | State | Evidence |
|---|---|---|
| Registration | Gandi SAS, created 31 Oct 2026, expires 31 Oct 2027, `clientTransferProhibited`, registrant **Kerb RideSharing**, Alabama | `whois kerbgo.com` |
| DNS zone | Gandi is authoritative: NS `ns-18-c.gandi.net`, `ns-186-b.gandi.net`, `ns-56-a.gandi.net`; SOA `ns1.gandi.net hostmaster.gandi.net` | `dig NS` / `dig SOA kerbgo.com` |
| Serving | Cloudflare edge in front of cto.new's project: `www` CNAME `sites.cto-new.app`, apex on the same pair (`172.67.205.64`, `104.21.42.107`) | `dig`, response `server: cloudflare` |
| Hostname claim | `_cf-custom-hostname.kerbgo.com TXT = a7affe06-059e-457d-b765-f60e30675789` — kerbgo.com is a **Cloudflare-for-SaaS custom hostname on cto.new's zone** | `dig TXT` |
| TLS | `CN=www.kerbgo.com`, issued by `Cloudflare TLS Issuing ECC CA 4`, 31 Oct 2026 → 29 Jan 2027 — issued through that same zone | `openssl s_client` |
| Origin | **Blaxel** behind CloudFront, per `site-migration.md`; not observable while nothing serves | `site-migration.md` |
| Mail | MX `1 smtp.google.com`, SPF `include:_spf.google.com`, a `google-site-verification` TXT, **no DMARC** | `dig MX` / `dig TXT` |
| Source | not in this repository and not served by the site | `site-migration.md`; `capacitor.config.ts` loads the live URL, `www/` holds only the offline screen |

The reading: **the domain is not hostage; the serving is.** Registration and the
authoritative zone sit at a registrar that is separate from the serving stack, and the
registrant is already named as Kerb RideSharing. The routing, the certificate and the
builder all belong to cto.new's Cloudflare zone and its Blaxel origin. So plan B can
repoint the domain without cto.new's cooperation — it cannot produce a site without their
source, data and secrets.

## What recovery needs, in the order it blocks

1. **Account access.** Four things, owned by four different parties until proven
   otherwise: the Gandi account (zone and registrar), the cto.new project or an export of
   it, the Blaxel account (origin host, and where the Postgres or file-backed database
   lived), and whatever holds the Stripe/Mapbox/VAPID/email secrets. Only the Gandi
   registrant is confirmed as Kerb's; the account holder is not known and is the one
   question that can be settled today.
2. **The source.** A cloneable repository or an archive. Nothing below it can be sized
   before this exists — `site-migration.md` says the same and it is still true.
3. **The data — the only irreplaceable item.** Riders, drivers, trips, payments, document
   records, settings and the email outbox. Note that `site-migration.md` found driver
   document **bytes** written to a folder on the instance, so the rows may survive while
   the licence and insurance images behind them do not. An SQL export with matching row
   counts, or nothing.
4. **Secrets and config**, names only: database URL, Stripe publishable/restricted keys and
   the webhook secret, Mapbox token, VAPID key pair, email provider key, session secret,
   support address, base URL. The webhook secret and session secret should be rotated at
   cutover rather than carried over.
5. **A host that claims `www.kerbgo.com`.** `site-migration.md`'s data-path decision still
   stands — Postgres over an HTTP driver, or a D1/R2 port — and is still decided from the
   dump.
6. **The cutover, attach first.** Stand the new host up so it serves the hostname with its
   own certificate, verify `site-migration.md`'s eight acceptance checks against it, then
   point `www` at Gandi and redirect the apex. No shell rebuild: `capacitor.config.ts`
   points at that origin and `errorPath` stays `index.html`. Sessions survive only if the
   cookie name and attributes are preserved.

## What the 404→522 warning means here

`cto-new-outage.md` tells cto.new not to touch DNS, and that still holds while nothing
claims the name. Plan B is the deliberate exception: the same record that turns a 404
into a 522 when pointed at a project that serves nothing is the correct cutover once a new
host claims the hostname. The order is attach, verify, then flip — never the reverse.

## What plan B cannot do

- **Recover the data from the client.** The bundle can be swept for routes and library
  names, which `site-migration.md` already did; it does not contain rows.
- **Serve a stand-in.** A mock, a static page or a local build put in front of a reviewer
  is a false claim about the product, and it is worse than a delayed submission.
- **Move to a different origin.** The shells are pinned to `https://www.kerbgo.com` and the
  session cookie is first-party to it, so a rebuild has to answer on the same hostnames.
- **Buy time by resubmitting.** A 404 site fails review under 2.1 whatever the app does.

So: if the database export never arrives, the store work restarts at the demo data (B3,
B7) and the A4 captures, and the honest position is a rebuild with a new date rather than
a republish with a new excuse.

## Store consequences while plan B runs

- Nothing goes to either store while the site 404s. A4's captures, the `site-changes.md`
  §7 checks and the reviewer walkthrough all stay void until the cutover serves.
- If a Play 14-day closed test has been started against the current build, decide
  deliberately whether it counts: the clock begins when testers opt in, and testers
  installed against a 404 app are not a credible test.
- A1, the Apple enrolment, is independent of all of this and should not wait for it.

## Before the deadline — the cheap work that shortens plan B

- The source, database and account asks are already in the follow-up in
  `cto-new-outage.md`; nothing extra needs sending.
- Add one question when you send it: **is the Blaxel app, and its database, still
  running?** If it is, that console is the shortest route to both the data and a working
  deployment, and it does not depend on cto.new's publish button.
- Confirm you can log in to the Gandi account today, and that 2FA is set up on it. That is
  the one asset that can be verified without waiting, and it decides whether the cutover
  is a ten-minute DNS change or a registrar recovery case. `clientTransferProhibited` is
  normal and harmless; losing the account is not.
- Set up DMARC before any transactional email moves: SPF and DKIM already point at Google
  Workspace, and `_dmarc.kerbgo.com` is currently empty, which is exactly the gap that puts
  password resets in spam after a provider change.

## Open questions this note cannot answer

- Whether cto.new's project still exists in any form, or was deleted.
- Whether the Blaxel origin and its database are still running.
- Whether the source was ever exported, and by whom.
- Which database path the port takes — decided from the dump, not from here.
- Who holds the Gandi account, and whether Kerb has the recovery email for it.
