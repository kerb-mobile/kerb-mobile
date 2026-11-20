# Site handover — kerbgo.com, for whoever holds the cto.new access

Six items: five code changes and one admin field. Both apps load the live site in a
WebView, so all of it takes effect on deploy, with no app release and no store
update. **All six landed and were checked against the live site on 20 Nov 2026.**
Two driver-flow defects found afterwards — the raw-seconds offer countdown and the
turn label still reading *to pickup* after the trip starts — are in `status.md`. Full detail, constraints and acceptance checks are in
`site-changes.md`; this page is the work order.

A second and larger piece of work follows in its own section: paying each store at
the moment of the charge, instead of collecting everything and settling by hand. It
is **not** a review item and must not start while either app is in review.

| # | Change | What ships | How to check it landed | Spec |
|---|---|---|---|---|
| 1 | `/delete-account` | The page, its signed-out state, and the entry rows in Account | `curl -sI https://www.kerbgo.com/delete-account` → `200`; page also loads without a session | §1 |
| 2 | Driver dropoff attribute | `data-kerb-destination` on the driver trip card, holding the dropoff | Signed in as the demo driver with a trip accepted: the card carries exactly one such attribute and the Navigate pill appears | §2 |
| 3 | Landing copy | Removes the lines saying there is no app | `curl -s https://www.kerbgo.com/ \| grep -c "no app"` → `0` | §3 |
| 4 | Standing demo offer | A pending offer waiting on `/driver` | The demo driver lands on an offer with nobody tapping anything | §4 |
| 5 | `KERBREVIEW` promo code | 100% off fare and fee, no card required, booking still dispatches | The demo rider, with no card on file, books at `$0.00` and the demo driver receives the trip | §6 |

Outside the builder, one admin field:

| Where | Set it to | Why |
|---|---|---|
| `/admin/branding` → Support email | `support@kerbgo.com` | Every address the site shows — help page, footer, cancel-trip copy, sign-out — is interpolated from this one field. **Set, and confirmed live on 20 Nov 2026:** the home page's server payload carries `supportEmail:"support@kerbgo.com"`, and all six public pages render it with no `ctomail` left anywhere. What remains is creating that mailbox in Google Workspace (`status.md` B10). No deploy needed. |

**Order.** 1 first — it is the one item that blocks both stores (Apple 5.1.1(v),
Play's account-deletion policy). Then 4 and 2 together, since they are what makes the
driver flow walkable. Then 3, then 5. The retailer Accept/Decline change (§5) is not
needed for either store; leave it out of this pass.

## After the review: paying each store at capture

A different change, on the same site: today every cent of an order lands on Kerb's
Stripe account and each store's share is forwarded by hand later. The change is to
split the single card charge at capture with Stripe Connect, so the store's share
reaches the store and only Kerb's share stays. Driver pay is unchanged — still
manual, still out of Kerb's share. Full spec, including why Express accounts and
destination charges, is `merchant-payments.md`; this is only the order of work, and
the § numbers in the Spec column are its sections, not `site-changes.md`'s.

**Before P1: three switches in the Stripe Dashboard, not in the code.** None of
this can be done from the codebase, and P1 fails on the first API call without it.
The account owner does it, on the same Stripe account that already takes ride
payments — in test mode first, then live before the first real order. Test-mode
stores do not carry over; onboarding is repeated per mode.

| Switch | Where | Who, and why |
|---|---|---|
| Connect enabled | Dashboard → Connect → get started; accept the platform agreement | Owner. Until it is on, creating a connected account is refused outright |
| Platform profile | Settings → Connect → branding and public business name, plus a support email and phone | Owner. This is what the store sees inside Stripe's onboarding and what its customers see on a statement, so it must read Kerb — `support@kerbgo.com` — and not a placeholder |
| Payout schedule | Settings → Connect → payout schedule: interval and delay for new connected accounts | Owner, after the fee decision below. Daily versus weekly changes what Kerb pays per store each month (see `merchant-payments.md` → *Connect's own fees*) |

If Connect cannot be enabled on that account — country, business type, or a
verification step still outstanding — stop and report it. There is no code-side
fallback for P1.

| # | Change | What ships | How to check it landed | Spec |
|---|---|---|---|---|
| P1 | Connect onboarding in `/retailer` | A Payouts card: a Connect button, the current state, Finish setup while incomplete. Stripe collects the business details and bank account — never a Kerb form | A test store finishes onboarding in Stripe test mode and both flags read enabled from `account.updated`, not from the redirect back | §1 |
| P2 | The split charge | `transfer_data[destination]` and `application_fee_amount` on the order's PaymentIntent; the fee, account and charge ids stored on the order | A test order: the total sits on the store's account, the application fee is back on Kerb's, the order row holds all three ids | §2 |
| P3 | Refunds and disputes | `reverse_transfer: true` and `refund_application_fee: true` on every refund — `/admin` and `/retailer` through one helper — plus transfer reversal on `charge.dispute.created` | Full and partial refunds return the store's balance to zero and leave Kerb short only Stripe's fee | §3 |
| P4 | Webhooks | `account.updated`, `transfer.created`, `transfer.failed`, `charge.refunded`, `charge.dispute.created`: signature-verified, idempotent by event id | A replayed event changes nothing and each handler runs once | §4 |

**Three decisions come first — they are the owner's, not the implementer's.**

1. **Who pays Stripe's card fee.** Kerb absorbs it (Option A) or the store does
   (Option B), worked numbers for both in `merchant-payments.md` → *Who pays
   Stripe's card fee*. On a `$40.00` order: Kerb keeps `$6.29` under A, and the
   store receives `$30.79` instead of `$32.25` under B. Unpicked, it silently
   becomes A.
2. **A store that has not finished onboarding.** Block new orders for that store
   with a clear message, or keep collecting to Kerb and settle as today. Pick one;
   the code must never send a destination charge to an account that is not
   `charges_enabled` — Stripe rejects it at the customer's last step.
3. **Sales tax on goods.** The store now receives the goods money directly, so
   which side remits needs the store's accountant. Do not guess in code.

**Order inside this phase.** P1 before P2, because a charge can only route to an
onboarded account. P3 and P4 after the first real order — refunds and disputes only
exist once money has moved. Only new orders route to the store; past orders stay as
they are and are still settled by hand.

**What not to do.** Do not rebuild or redesign the site, do not change hosting, do
not add analytics or tracking, and do not commit credentials. Both apps are in review
against the site as it behaves today, with only these six items changed. The payments
phase above changes what the customer is charged: start it after both apps are
approved, and leave the rider flow, fares and both app builds alone when you do.
The retailer Accept/Decline change (§5) is still out of scope.

**When it is done**, say so and the six review checks above get re-run against the
live site, with the status table in `site-changes.md` updated to match. The payments
phase is accepted against the six checks at the end of `merchant-payments.md`, in
Stripe test mode, not against these. If something cannot be done as written — a
promo table that the schema cannot hold, a driver card that renders the dropoff from
a different field — report which item and why rather than working around it. Moving
the site off cto.new is separate work and must not start during a review
(`site-migration.md`).
