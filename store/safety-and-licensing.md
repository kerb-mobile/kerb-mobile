# Safety, screening and licensing — what the site can now say

Written 20 Nov 2026, after a review-summary of kerbgo.com claimed the site is silent
on driver screening, safety tools and state licensing. Revised the same day after
the owner confirmed two things that change the copy:

1. **A permit exists, in UZURV's name.** APSC Permit **TNC-4** is held by **UZURV
   Holdings, Inc. d/b/a UZURV**, and the owner has confirmed the operating structure
   is **B**: Kerbgo is a separate company, UZURV is the **TNC of record** whose
   screening and insurance cover every Kerb trip, and Kerb is the storefront. That
   is lawful **if it is disclosed and if UZURV's cover really extends to every Kerb
   trip** — §2a.
2. **Screening is done by hand at both layers**: UZURV runs the statutory checks as
   the TNC of record, and Kerb reads each driver's licence, registration and insurance
   itself, enters pass or fail on the account, and revokes within 24 hours where
   needed.

So the screening sentences are publishable now, in the honest form: "background
checks and MVR on all drivers" becomes a description of a manual check that actually
runs, with the statute's own standard named. The licensing sentence is **not**
publishable until the permit names the entity that operates Kerb. The page is written
to be handed to cto.new as-is, with the licensing paragraph behind a gate.

---

## 1. What the live site says today (checked 20 Nov 2026)

| Claim in the critique | State of the live site |
|---|---|
| No mention of background checks | It mentions them — and says they don't happen: **"Automated background checks are a later release."** (`/help`). That line has to change with this page, or the two pages contradict |
| No MVR screening | `MVR` appears **0** times on `/`, `/help`, `/privacy`, `/terms`. Nothing describes a driving-record check |
| No mention of insurance | One mention on `/`: drivers *upload* an insurance photo for review. No coverage, limits or carrier anywhere |
| No safety page or emergency tools | `/safety`, `/trust`, `/about`, `/legal` all **404**; `911` and `emergency` appear **0** times on the four live pages |
| No state licensing | Correct today. The permit is UZURV's, the TNC of record, and the site names neither UZURV nor permit TNC-4 anywhere — §2 |

## 2. Licensing — the permit is in UZURV's name, not Kerb's

Alabama regulates rideshare at state level: Act 2016-409, codified at **Ala. Code
Title 32, Chapter 7C**, administered by the **Alabama Public Service Commission**
under **Chapter 770-X-12**. A permit is mandatory (Rule 770-X-12-.03(5)); it runs
**14 months** (.03(6)) and renews for **$300** by 1 July, with $100 per business day
after that (.03(7)).

What the record actually says, checked 20 Nov 2026:

- **APSC Permit TNC-4** is held by **UZURV Holdings, Inc. d/b/a UZURV**, renewed by
  order effective **1 July 2026** (docket 33063). The order names that entity and
  that trade name, and nothing else.
- The Commission's regulated-TNC page lists **Uzurv Holdings, Inc.** — again, not
  Kerb, and not Kerbgo.
- **UZURV Holdings, Inc.** is a **Delaware corporation headquartered in Richmond,
  Virginia** (101 S 15th St, Ste 200), founded 2017, describing itself as an
  assisted-mobility TNC in 20 states. Its site, its APSC filing and its other state
  permits (Massachusetts `24-TNCDP-06`, for one) name UZURV only. The **owner confirms
  owning both companies** (20 Nov 2026), which explains the affiliation — but common
  ownership does not put Kerb on the permit, which is why the page discloses the
  operating structure instead of claiming a shared licence.
- **The site names no legal entity at all.** `/terms` says "we" and "us" mean
  **Kerb**; `/privacy` does the same. No LLC, Inc., registered address or state of
  formation appears on either — so nothing on the live site currently ties the
  service to the permit holder, and a reviewer cannot tell who they are contracting
  with.

### 2a. Why a sister company's licence is not a licence

Rule 770-X-12-.03(5): *"A person must have a valid and current permit to operate as a
TNC."* The permit is issued to the applicant that filed Form TNC-1 — the sworn
statement, the certificate of insurance, the screening policy and the law-enforcement
procedures are all that entity's. Rule .03(9) binds the permittee to the terms of
*its* permit. Nothing in Chapter 770-X-12 lets an affiliate, a parent, a sister
company or a brand run a separate network under the permittee's permit, and the
d/b/a on the order is a fixed word: **UZURV**.

The three possible structures. **B is the confirmed one (20 Nov 2026)** — the other
two are kept here so the reasoning can be re-checked, and so nobody drifts back into
describing Kerb as the licensee:

| Structure | Lawful? | What has to happen |
|---|---|---|
| **A. Same entity trading as Kerb** — Kerb is a brand of UZURV Holdings, Inc. | Yes, once filed | Register/confirm the Kerb dba and get the Commission to show it against Permit TNC-4; insurance certificate naming the dba. Then the page can say UZURV holds TNC-4 and operates Kerb |
| **B. Separate company, rides legally UZURV's** — UZURV is the TNC of record, its screening and insurance cover the trip, Kerb is the storefront | Yes, if disclosed — **this is the confirmed structure, and the owner confirms sole ownership of both companies** | The disclosure must say rides are provided by UZURV, and UZURV's screening and insurance must actually cover every Kerb trip. Kerb's own drivers are not covered unless UZURV screened and insured them. Common ownership makes the arrangement easy to administer; it does not make the permit transferable |
| **C. Separate company, own drivers, own dispatch** | **No** | Operating a TNC without a permit. Form TNC-1, $700, hearing — the same path the Commission denied Aux Rideshare on 4 Nov 2026 (docket 33732) |

**Structure C stays ruled out.** It is not a paperwork gap: the APSC certificate of
insurance is issued for the permittee's operations, so a Kerb-dispatched trip has no
TNC coverage behind it and an injury claim lands on the driver personally.

Under B, one sentence is publishable, and it is the one the critique asked for:

> Rides booked on Kerb are provided by UZURV Holdings, Inc., an Alabama Public Service
> Commission permitted transportation network company (permit TNC-4).

It passes the "checkable" test the failed version never could: the Commission's own
page lists Uzurv Holdings, the order names the permit, and nothing claims Kerb is the
permittee. What it does **not** say is "Kerb is licensed" — because Kerb isn't, and
pretending otherwise is what a regulator or a plaintiff's lawyer picks apart first.

**What B obliges, in exchange for that sentence.** Three things have to be true, and
two of them are documents you should fetch before the page deploys:

1. **UZURV's insurance certificate covers trips booked through Kerb**, not just trips
   dispatched on UZURV's own network. If the certificate names only UZURV's
   operations, a Kerb trip is an uninsured TNC trip.
2. **UZURV's screening applies to Kerb's drivers**, and the results are on file — that
   is the statutory check under §32-7C-29, and it is UZURV's duty as the TNC, not
   Kerb's.
3. **The driver-facing disclosure under §32-7C-3** tells drivers who insures them and
   at what limits. Under B that is UZURV's coverage described accurately, not a Kerb
   policy.

Any of the three failing does not make B "a bit risky" — it makes the disclosure
false, which is the same problem as C with better paperwork.

### 2c. The claim this exposes, and it is already live

`/terms` and the homepage say Kerb serves **"the entire United States"**, and the app
lists **51 service states**. TNC permits are per-state: UZURV is permitted in Alabama,
Massachusetts and others, and neither company is permitted everywhere. The state-line
fee in the terms ("each state line crossed adds $25") makes interstate trips an
explicit product feature, which invites exactly the question of who is permitted to
carry that trip in the second state. This is not a site-copy fix — it is a scope
question — but it belongs on the same list as the permit, and it is R8 in `status.md`.

### 2b. The paragraph to publish (structure B)

> Rides booked on Kerb are provided by UZURV Holdings, Inc., an Alabama Public Service
> Commission permitted transportation network company (permit TNC-4). The Commission
> licenses rideshare companies in Alabama and takes complaints about them.

No permit expiry date is needed, and none should be invented: TNC-4 was renewed
effective 1 July 2026 and runs 14 months (.03(6)). If a date is ever quoted, take it
from the order.

Two variants, if the facts move: if Kerb later becomes a dba of UZURV (structure A),
the sentence becomes "Kerb is operated by UZURV Holdings, Inc."; if Kerb obtains its
own permit (structure C resolved), it names that permit instead. Neither is a
paraphrase to improvise on the live page.

## 3. What the permit obliges the TNC to do — and therefore what the page can say

Under structure B these duties bind **UZURV as permittee**, and Kerb's page describes
them rather than owning them. That matters for wording: "we screen" is Kerb's manual
document review; the statutory screening is UZURV's, and the page says so.

| Requirement | Source | What it lets the site say |
|---|---|---|
| Complete the statutory checks **before** a driver may accept a trip; screen drivers **annually** afterwards | Rule 770-X-12-.03(1)(d) | "Every driver is checked before their first trip, and again every year" |
| The check itself: a **local and national criminal background check** including a multi-state/multi-jurisdiction criminal records locator **or similar commercial nationwide database with validation (primary source search)**, plus the **DOJ National Sex Offender Public Website**, plus **obtaining and reviewing a driving history report** | Ala. Code **§32-7C-29(a)(2)–(3)** | The three checks, named plainly, as screening UZURV performs — with Kerb's own document review alongside it |
| The TNC **may conduct the checks itself**, or have a third party conduct them — no vendor is required by the statute | §32-7C-29(a)(2)–(3) | UZURV may run them in-house; compliant **only if those three artefacts exist per driver**. Kerb's manual review is additional, never a substitute |
| Barred drivers: more than three moving violations in three years, or fleeing/eluding, reckless driving, or driving on a suspended or revoked licence in that period; convictions in the past seven years for the offences the section lists | §32-7C-29(b) | "We don't accept drivers the statute bars" — do not enumerate the list from memory |
| **Zero-tolerance** intoxicating-substance policy, **notice posted prominently on the website**, with the means to complain | Rule 770-X-12-.03(1)(e)–(f) | The standing notice on `/safety` |
| On a zero-tolerance complaint, **immediately suspend** the driver's network access and keep it suspended for the investigation | Rule 770-X-12-.03(1)(g) | "A report of impairment suspends the driver's access immediately" |
| The zero-tolerance policy must describe the **corrective measures** it applies | Rule 770-X-12-.03(1)(h) | One line on what happens to a confirmed violation — **owner must confirm the wording in §4** |
| Written coverage disclosure to each driver, before their first trip | Ala. Code §32-7C-3 | Driver-facing, not this page |
| Primary liability while logged on and available: **$50,000 / $100,000 / $25,000** | Ala. Code §32-7C-2(b)(1)a | The waiting-coverage figures, once a policy is bound |
| Primary liability while a prearranged ride is in progress: at least **$1,000,000** for death, bodily injury and property damage | Ala. Code §32-7C-2(c)(1)a | The on-trip figure |
| Driver records kept **two years** past the end of the relationship; trip records two years; Commission may audit a sample | Ala. Code §32-7C-33 | Nothing public — but it is why the screening records must be stored, not just acted on |

The one thing that is still not true: **automated** screening. It is manual, and the
page says so. That is a strength, not an apology — a person reads the result.

## 4. Ship this now — `/safety`

Copy for cto.new, verbatim. It closes the "no safety page", "no emergency guidance"
and "no zero-tolerance notice" findings, and it is the first page on the site to
describe how drivers are actually checked. The licensing paragraph is the one piece
depending on a structure decision outside this document — §2b.

Two things gate the page, and neither is cosmetic:

- **The licensing paragraph (§2b).** It is written below for structure A. If the
  structure is B, swap in that sentence; if it is C, **delete the whole paragraph** —
  do not ship a bracketed string and do not ship "Kerb holds a permit" without knowing
  which entity holds it.
- **The complaint route** — see §5. The APSC channel works today; the `support@`
  address depends on B10.

```
Safety at Kerb

In an emergency, call 911. Tell the operator where you are, that you are in a
rideshare, and the make, model and plate of the car — they are in the app before
you get in. Kerb cannot send help for you.

Before you get in
The app shows your driver's first name and photo, and the make, model and plate of
their car. Check all four against the car at the kerb. If they don't match, don't
get in, and tell us.

How drivers are checked
Rides booked on Kerb are provided by UZURV, an Alabama-permitted TNC. Every driver is
screened before their first trip and re-checked every year, and each screening covers:

  - a local and national criminal records search, run through a nationwide
    multi-jurisdiction database and validated against the primary source;
  - the US Department of Justice national sex offender public website;
  - a driving history report.

We also read every driver's licence, vehicle registration and insurance ourselves,
and no driver can accept a trip until both checks have passed. Drivers with repeated
moving violations, or with the serious driving and criminal convictions Alabama's
rideshare statute bars, are not accepted.

Alcohol and drugs
Kerb has a zero-tolerance policy for intoxicating substances. A driver may not have
any amount of alcohol or drugs in their system while giving a ride. If you think a
driver is impaired, do not get in — or ask them to stop somewhere safe — and contact
us. A report of impairment suspends that driver's access immediately, before we
finish investigating, and the suspension lasts for the investigation. Where we
confirm a violation, the driver is removed from Kerb.

Something went wrong
Tell us the trip, the driver and what happened. Safety reports about a driver are
reviewed and acted on within 24 hours, and an account is switched off where the
report warrants it.

  Email: support@kerbgo.com
  Alabama Public Service Commission, Transportation Network Companies:
  334-242-5176 or 1-888-505-9047, mcs.ued@psc.alabama.gov

You can complain to the Commission as well as to us, about safety, sobriety, or
anything else about a trip.

Your data
Trip details stay with the trip. The privacy policy says what we keep, for how
long, and how to delete your account and its data.

Regulatory
Rides booked on Kerb are provided by UZURV Holdings, Inc., an Alabama Public Service
Commission permitted transportation network company (permit TNC-4). The Commission
licenses rideshare companies in Alabama and takes complaints about them.
```

That closing paragraph is structure B, confirmed 20 Nov 2026. It carries the licensing
answer the critique wanted without claiming Kerb holds the permit — and because it
credits UZURV for the ride, the screening paragraph above credits UZURV for the
screening, which is where the statutory duty actually sits.

**If B10 is still open when this deploys, delete the `Email: support@kerbgo.com`
line from the block. The APSC block stays — it is the means to complain that actually
works today, and Rule .03(1)(f) needs one.**

### 4a. Edits elsewhere on the site, in the same deploy

| Where | Change |
|---|---|
| `/help` | Replace **"Automated background checks are a later release. We deliberately don't show a working-looking button for something that isn't finished."** with **"Background checks are completed before a driver's first trip, and again every year, by UZURV — the Alabama-permitted TNC that provides rides on Kerb."** Drop the "later release" promise entirely: it describes Kerb's roadmap for a duty that now sits with UZURV, and it is the line that contradicts `/safety` |
| `/help` | Add the 911 line at the top of the safety-related answer: "In an emergency call 911 first." |
| Every footer | Add **Safety** next to **Legal**, linking to `/safety` |
| `/` | No change. Do not add a "fully licensed" badge or claim to the landing page — the permit statement belongs where the number is |

### 4b. Two sentences that must not ship

- **"Fully licensed"** — no Alabama licence covers this; the permit is a TNC permit
  from the APSC, and only the entity named on it can rely on it.
- **"We operate under our sister company's licence"** — there is no such thing in
  Chapter 770-X-12. An affiliate is either the same legal entity (then say UZURV
  operates Kerb), or it needs its own permit.
- **"Background checks on all drivers"** with no qualifier — true because UZURV
  screens every driver who can accept a trip on Kerb. Say who screens and how, or the
  claim is marketing with a statute behind it that a rider cannot see.

Add one more sentence once **live trip sharing** is live (`live-trip-sharing.md`):
"Share your trip: the app can send a live link to someone you trust, showing the car
and where it is until you arrive." Not before it ships.

## 5. The complaint route has to work before the page promises it

Rule .03(1)(f) requires a *means to complain* on the website, so this is not
cosmetic. There are two routes, and they are not equally ready:

| Route | Ready? | Use |
|---|---|---|
| APSC — 334-242-5176, 1-888-505-9047, `mcs.ued@psc.alabama.gov` | **Yes**, published on the Commission's TNC page | Ship now. It is the regulator's own channel and it is monitored |
| `support@kerbgo.com` | **No** — the site shows the address, but the mailbox does not exist yet (B10) | Ship the line only once the mailbox receives mail. A complaint address that bounces is worse than none |

If the page must go live before B10 closes, drop the `Email: support@kerbgo.com`
line and keep the APSC block; add the address in the next deploy.

## 6. Gated copy — publish each block only when its evidence exists

**Gate A — insurance (owner).** Under structure B the coverage is **UZURV's**, already
filed with the APSC as part of TNC-4. Closes when a copy of that certificate is in
hand and it covers trips booked through Kerb, not only UZURV's own dispatch. Then, on
`/safety` and in the driver terms:

> While a driver is on a trip, the ride is covered by primary liability insurance of
> at least $1,000,000 for death, bodily injury and property damage, and while they
> are waiting for a request, $50,000 per person / $100,000 per incident / $25,000
> for property damage. [Carrier], policy [number].

Until then the page says nothing about coverage. Do not say "insured" as a
reassurance: an uninsured claim is the one thing a rider will sue over, and the
Commission has the certificate either way.

**Gate B — the APSC listing.** Closes when **Kerb** appears on
`psc.alabama.gov/transportation-network-companies` — as a dba of UZURV, or as its own
permittee. Then add to the licensing paragraph:

> Our permit is listed with the Commission at [URL]. You can check it there, or call
> 334-242-5176.

Until then, do not invite riders to check the Commission's list: UZURV is on it and
Kerb is not, and the gap is the first thing a suspicious reader finds.

**Gate C — automated screening, if it is ever wanted.** Nothing requires it. If a
vendor (Checkr, Sterling or similar) is added later, the copy changes from "done by
"screened by UZURV" to naming the vendor, the cadence, and the adverse-action path — and
the FCRA consent, disclosure and pre-adverse-action notice must exist before the
first automated check runs. Do **not** switch the copy to "automated" while the
owner is still running checks by hand.

**Gate D — safety tools beyond trip sharing.** In-app 911 calling, 24/7 incident
response, an audio-recorded ride and an emergency button do not exist. Live trip
sharing is the one that is shipping (`live-trip-sharing.md`). None of the others may
be implied on the page until they are real.

## 7. What "manual screening done properly" means, per driver

Not for the page — for the owner, so the page stays true. Under B the first four rows
are UZURV's artefacts and the last two are Kerb's own; get the first four in writing
before the page credits UZURV with them. For each driver, before their account is
approved:

| Step | Evidence to keep |
|---|---|
| Identity and licence | The four uploaded photos (licence front and back, registration, insurance) reviewed by a person |
| Criminal search | A nationwide multi-jurisdiction search result, **validated against the primary source**, filed to the driver's record |
| Sex-offender registry | A dated screenshot or result from the DOJ national public website |
| Driving history | The MVR/driving-record report itself, not just a note that it passed |
| Decision | Pass or fail entered on the account, with the date and who made the call |
| Annual re-check | Same three artefacts, dated, within 12 months of the last |

Keep them for **two years** past the end of the driver relationship (§32-7C-33) —
the Commission can audit a sample. A pass/fail flag with no underlying report is the
gap a compliance audit finds first.

## 8. Owners

| Item | Owner | Done when |
|---|---|---|
| Obtain UZURV's TNC-4 insurance certificate and a written confirmation that its screening covers Kerb's drivers | Me | Both documents in hand, certificate naming Kerb-booked trips; then Gate A copy publishes and R2/R4 close |
| Decide what "the entire United States" means against per-state permits | Me | Service-area claim narrowed to permitted states, or permits obtained for the rest (§2c, R8) |
| Legal entity named in `/terms` and `/privacy` | Me + cto.new | Both pages say who the counterparty is — the permittee — with its state of formation and address |
| Zero-tolerance corrective-measure wording confirmed | Me | The "removed from Kerb" line approved as written, or replaced with what will actually be applied |
| `support@kerbgo.com` mailbox | Me | Receives mail (B10) — otherwise the page ships with the APSC route only |
| `/safety` page, `/help` fix, footer link | cto.new | Live and reachable signed out, from §4 |
| Screening runs per driver with records kept | Me | §7 artefacts exist for every approved driver, and the annual re-check is diary'd |
| TNC insurance bound, certificate filed | Me | Gate A copy publishable |
| Live trip sharing | cto.new | `live-trip-sharing.md` acceptance checks pass |

Tracked as R1–R6 in `status.md`.
