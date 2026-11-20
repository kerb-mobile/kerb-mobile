# Store payments — paying each store at the moment of the order

**Not built.** This is a specification for the kerbgo.com codebase, which lives in
the cto.new builder, not in this repository. Nothing in `www/`, `ios/` or `android/`
changes: the store portal is a page on the site, and both apps already load the site
in a WebView. This document is the work order for whoever holds that access.

## What this changes, and what it does not

Today every cent of an order lands in the platform's Stripe account, and the store's
share is forwarded to them later by hand. The ask is the opposite: the store's money
reaches the store at the moment of the charge, and only Kerb's share stays with Kerb.

| | Before | After |
|---|---|---|
| Goods and delivery charged to the customer | **already charged by card, in one charge** — the grocery checkout sends `storeSlug`, `lines` and `paymentMethodId` into the same Stripe flow the rides use, and the total covers items + delivery fee | one charge, split at capture |
| The store's share | sits in Kerb's Stripe balance; the store portal shows no money at all — no balance, no statement, no payout history — so it is settled outside the product | lands in the store's own Stripe balance, paid out by Stripe |
| Kerb's share | everything, until something is settled by hand | stays in Kerb's balance at capture |
| Driver pay | Kerb pays the driver manually | **unchanged** — still manual, still out of Kerb's share |
| Rider flow, fares, app build | — | **unchanged** |

Checked against the deployed client, 20 Nov 2026: none of `transfer_data`,
`application_fee` or `stripe_account` appears anywhere in the 69 route chunks or the
entry bundle, so no money splits today and every cent of a store order lands on
Kerb's account. Orders do carry a `paymentStatus` and the admin order view renders
it, but `/retailer` renders no money whatsoever.

## The constraint that decides the design

A customer taps one button and pays once, so there is exactly **one card charge and
one merchant of record**. That means a store cannot be paid through its own separate
processor (Square, Clover, its bank) as part of the same charge — one charge cannot
be split across two processors. The realistic options are:

1. **Stripe Connect, Express accounts, destination charges** — one charge on Kerb's
   Stripe account, the store's portion transferred to its connected account at
   capture, Kerb's fee retained. *Recommended, and specified below.*
2. **Keep collecting and paying out** — what happens today. No store onboarding, but
   the manual work and the float stay with Kerb.
3. **Separate charges** — the customer pays twice. Bad experience, and it doubles
   the failure cases. Not recommended.

Stripe's own account table rules out the obvious alternative: **Standard accounts
support direct charges only**, so they cannot be the destination of a split charge.
Express (or Custom, which needs far more engineering) is the supported route.

## Money flow, one order

Destination charge with `application_fee_amount`, on the store's connected account:

| Step | Amount | Where it goes |
|---|---|---|
| Customer is charged the order total | `$40.00` | Kerb's Stripe account, then immediately to the store |
| The full total is transferred to the store's connected account | `$40.00` | store's pending balance |
| `application_fee_amount` returns to Kerb | `$7.75` | Kerb's balance |
| Stripe's processing fee | `$1.46` | **debited from Kerb's balance** |
| Stripe pays the store out | `$32.25` | store's bank, on Stripe's schedule |

The fee is everything Kerb keeps: the booking fee, the delivery margin, and any
commission on goods. In the example that is `$3.00 + $1.75 + $3.00 = $7.75`.

That table assumes Option A below — Kerb pays Stripe's card fee. It is the one
decision in this document that changes what stores and Kerb each keep, so it is set
out in full.

## Who pays Stripe's card fee

With destination charges the platform always pays: Stripe debits its fee from
*Kerb's* balance, on the **whole** charge, before the store's share is transferred.
On a `$40.00` order that is `$1.46` — Stripe being paid on the store's goods, money
Kerb never touches. Today the cost is invisible because Kerb holds all the money
first and settles by hand.

Raising `application_fee_amount` does **not** change Stripe's fee: it is charged on
the total, whatever the split. So pass-through needs no gross-up formula — add the
fee to Kerb's share and the store's transfer drops by exactly that fee.

US domestic card, 2.9% + 30¢, rates checked 20 Nov 2026. Re-check before quoting.

| Order total | Stripe's fee |
|---|---|
| `$12.00` | `$0.65` |
| `$26.00` | `$1.05` |
| `$40.00` | `$1.46` |
| `$80.00` | `$2.62` |
| `$120.00` | `$3.78` |

### Option A — Kerb absorbs it

| | `$26.00` order | `$40.00` order |
|---|---|---|
| Kerb's share before Stripe | `$5.25` | `$7.75` |
| `application_fee_amount` | `$5.25` | `$7.75` |
| Transferred to the store | `$20.75` | `$32.25` |
| Stripe's fee, from Kerb's balance | `$1.05` | `$1.46` |
| **Kerb keeps** | **`$4.20`** | **`$6.29`** |
| Store is charged a fee | no | no |

Nothing changes for stores and no terms change is needed. The cost is real and
proportional: Stripe's fee is 20% of Kerb's `$5.25` take on the `$26.00` order and
19% of its `$7.75` on the `$40.00` one, and it grows with international cards
(+1.5%) or manually entered ones. Small orders hurt most, because the 30¢ is fixed.

### Option B — the store pays it

| | `$26.00` order | `$40.00` order |
|---|---|---|
| Kerb's share before Stripe | `$5.25` | `$7.75` |
| Stripe's fee added | `$1.05` | `$1.46` |
| `application_fee_amount` | `$6.30` | `$9.21` |
| Transferred to the store | `$19.70` | `$30.79` |
| Stripe's fee, from Kerb's balance | `$1.05` | `$1.46` |
| **Kerb keeps** | **`$5.25`** | **`$7.75`** |
| Cost to the store, vs Option A | `$1.05` (4.0%) | `$1.46` (3.6%) |

Kerb's take is whole, and every store now carries a card cost it never had when it
was settled by hand — 4% of a small order. That has to be in their terms and it shows
up on their Stripe statement as a bigger platform fee, so it is a conversation, not
just a constant in the code.

B can be softened to the fee on the goods only: at `$40.00` with `$30.00` of goods
that is `$1.10`, `application_fee_amount` `$8.85`, and Kerb keeps `$7.39`. It leaves
Stripe's fee on Kerb's own delivery and booking revenue, where it arguably belongs.
Either way, pick one and write it into the store terms.

### Connect's own fees, which Kerb pays either way

This design is Stripe's *you handle pricing* model, so the platform also pays `$2`
per month for each account that received a payout and `0.25% + 25¢` per payout,
billed monthly on payout volume. They land per store, not per order, and they are
not part of the split above. A store taking 40 orders a month at `$40.00` receives
about `$1,290`, so with daily payouts (Express's default) that is
`$2 + $3.23 + 30 × 25¢ ≈ $12.70` a month against `$58.40` of card fees on the same
orders — about 22% on top. Weekly payouts cut it to roughly `$6.20`. Worth choosing
a payout schedule with this in mind before onboarding a low-volume store.

## What to build

### 1. Store onboarding, in `/retailer`

- A **Payouts** card in the store's account area: a *Connect payouts* button, the
  current state (not started / details needed / payouts enabled), and a *Finish
  setup* button while it is incomplete.
- The button creates a Stripe **Account Link** for that store and redirects to it;
  Stripe collects the business details and the bank account. Never collect bank
  details on Kerb's own form.
- Two flags per store, both set from Stripe, never from the client:
  `charges_enabled` and `payouts_enabled`. They arrive on the `account.updated`
  webhook — treat Stripe as the source of truth, and re-read the account rather than
  trusting the redirect back.
- The requirement list from Stripe (`requirements.currently_due`) should be shown to
  the store, or onboarding stalls with no explanation.

### 2. The charge

- Storage: `merchants.stripe_account_id`, plus the two flags above.
- Where the order's PaymentIntent is created, add
  `transfer_data[destination] = <store account>` and
  `application_fee_amount = <Kerb's share in cents>`, computed from the same fare
  math that produces the order total, so the stored totals and the charge agree.
- Record on the order: the fee, the destination account, and the charge id — a
  refund or a dispute needs all three.
- A store that has not finished onboarding must not take orders that pay it:
  either block new orders for that store (clear message, no dead end) or keep
  collecting to Kerb and pay out manually as today. Pick one; **do not** create a
  destination charge to an account that is not `charges_enabled` — Stripe rejects it
  and the customer sees a failure at the last step.

### 3. Refunds and disputes — the part that bites

With destination charges the platform is liable for both:

- **Refund** default behaviour is wrong for Kerb: the store keeps its money and
  Kerb's balance absorbs the refund. Every refund must pass `reverse_transfer: true`
  so the store's share comes back, and should also refund the application fee
  proportionally (`refund_application_fee: true`) so Kerb does not profit from a
  cancelled order.
- **Chargebacks** debit Kerb's balance and can be recovered by reversing the
  transfer. Listen for `charge.dispute.created` and reverse the transfer for that
  charge; a store with a negative balance is Kerb's problem unless
  `debit_negative_balances` is enabled on the account.
- The refund path in `/admin` and the store-facing one must both use the same
  helper, or one of them will quietly leave the store holding refunded money.

### 4. Webhooks

`account.updated`, `transfer.created`, `transfer.failed`,
`charge.refunded`, `charge.dispute.created`. Verify signatures; make each handler
idempotent by event id.

## What this does not decide

- **Tax.** Whether sales tax on goods is collected and remitted changes hands here:
  the store receives the goods money directly, so the store's accountant needs to
  agree who remits what. Do not guess in code.
- **Store terms.** Stores now need a Stripe account and their own KYC, and take on
  Stripe's terms. Some will refuse; the fallback in §2 covers them.
- **Who pays Stripe's card fee** — the section above sets out Option A and Option B
  with worked numbers; unpicked as of 20 Nov 2026, and if nobody picks, the code
  silently becomes A.
- **Refunds of past orders** — only new orders route to the store.

## Acceptance checks

1. A store completes onboarding in `/retailer`, shows payouts enabled, and its two
   flags flip on `account.updated` — verified in Stripe test mode, not by the
   redirect alone.
2. One test order to that store: the total arrives on the connected account, the
   application fee returns to the platform, the order record holds the fee, account
   and charge ids.
3. A full refund reverses the transfer and the application fee; the store's balance
   returns to zero and Kerb does not end up out of pocket beyond Stripe's fee.
4. A partial refund reverses proportionally.
5. A store that has not finished onboarding cannot take a paid order, and the
   message says why.
6. The driver is still paid out of Kerb's share, exactly as before.
