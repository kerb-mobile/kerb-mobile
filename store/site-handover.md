# Site handover — kerbgo.com, for whoever holds the cto.new access

Six items: five code changes and one admin field. Both apps load the live site in a
WebView, so all of it takes effect on deploy, with no app release and no store
update. **None of it has landed as of 20 Nov 2026** — each row was checked against
the live site that day. Full detail, constraints and acceptance checks are in
`site-changes.md`; this page is the work order.

| # | Change | What ships | How to check it landed | Spec |
|---|---|---|---|---|
| 1 | `/delete-account` | The page, its signed-out state, and the entry rows in Account | `curl -sI https://www.kerbgo.com/delete-account` → `200` (a 404 today); page also loads without a session | §1 |
| 2 | Driver dropoff attribute | `data-kerb-destination` on the driver trip card, holding the dropoff | Signed in as the demo driver with a trip accepted: the card carries exactly one such attribute and the Navigate pill appears | §2 |
| 3 | Landing copy | Removes the lines saying there is no app | `curl -s https://www.kerbgo.com/ \| grep -c "no app"` → `0` (two today) | §3 |
| 4 | Standing demo offer | A pending offer waiting on `/driver` | The demo driver lands on an offer with nobody tapping anything | §4 |
| 5 | `KERBREVIEW` promo code | 100% off fare and fee, no card required, booking still dispatches | The demo rider, with no card on file, books at `$0.00` and the demo driver receives the trip | §6 |

Outside the builder, one admin field:

| Where | Set it to | Why |
|---|---|---|
| `/admin/branding` → Support email | `support@kerbgo.com` | Every address the site shows — help page, footer, cancel-trip copy, sign-out — is interpolated from this one field. It currently holds `kerb-221901fc@ctomail.io`, a cto.new disposable mailbox, so anyone who follows the site's own support link today reaches nobody. No deploy needed. |

**Order.** 1 first — it is the one item that blocks both stores (Apple 5.1.1(v),
Play's account-deletion policy). Then 4 and 2 together, since they are what makes the
driver flow walkable. Then 3, then 5. The retailer Accept/Decline change (§5) is not
needed for either store; leave it out of this pass.

**What not to do.** Do not rebuild or redesign the site, do not change hosting, do
not add analytics or tracking, and do not commit credentials. Both apps are in review
against the site as it behaves today, with only these six items changed.

**When it is done**, say so and the checks above get re-run against the live site,
with the status table in `site-changes.md` updated to match. If something cannot be
done as written — a promo table that the schema cannot hold, a driver card that
renders the dropoff from a different field — report which item and why rather than
working around it. Moving the site off cto.new is separate work and must not start
during a review (`site-migration.md`).
