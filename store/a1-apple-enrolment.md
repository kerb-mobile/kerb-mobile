# A1 — Apple Developer Program enrolment: what it asks for, and the order to do it in

**Drafted 24 Nov 2026.** A1 is the longest lead time you own: it gates A2 (the six CI
secrets), A3 (the app record) and every signed iOS build, and none of it waits on the site
coming back — with one exception, recorded under *Where this collides with B15*. Apple's
requirements below are from its own enrolment and D‑U‑N‑S pages, read 24 Nov 2026.

## What Apple asks for, enrolling as an organisation

| Requirement | Detail | Why it matters here |
|---|---|---|
| Apple Account with two-factor authentication | In your **legal** first and last name — an alias, nickname or company name in those fields delays approval. Apple recommends the address be on the organisation's domain | A `@kerbgo.com` address is the clean version; B10 creates it |
| Legal age of majority in your region | — | — |
| **Legal entity status** | The organisation must be able to enter contracts with Apple. DBAs, fictitious business names, trade names and branches are **not** accepted, and the legal entity name becomes the **seller name** on the App Store | The seller on a Kerb app is a company name, not yours — if you choose the organisation path |
| **D‑U‑N‑S Number** | Nine digits, from Dun & Bradstreet, registered to **that legal entity**, and used to verify identity, legal status and address. Free in most regions; look yours up first, request it if absent (up to five business days) | The slowest external step, and the one that depends on R7's records |
| **Legal binding authority** | Whoever enrols becomes the Account Holder and must be able to bind the organisation: owner/founder, executive, senior project lead, or an employee authorised by one, with a reference if not the owner | You are the owner, so this is satisfied on the facts — Apple may still ask |
| A public, working website on the organisation's domain | Social pages, empty sites and a registrar's parked page are refused. Apple's verification may look at it | **kerbgo.com answers 404 on every route today** — see below |
| Notarised business documents | Apple may ask for notarised copies of incorporation documents | Have the state filing to hand before starting |
| Government-issued photo ID | Identity verification asks you to photograph a passport or driver's licence during enrolment | Have it, with the name matching the Apple Account |
| Annual fee | **99 USD** per membership year, local currency where available; an auto-renewing subscription in the Apple Developer app, a one-off purchase on the web. Waivers exist for nonprofits, accredited education and government | Not applicable to Kerb, but a card is needed at the end |
| Device | Enrolling in the **Apple Developer app** needs an iPhone/iPad with Face ID, Touch ID or a passcode, or a Mac with T2/Apple Silicon — and the *same* device for the whole process. The web route has no such constraint | Pick the web route unless there is a reason not to |

Apple publishes no review time for an organisation enrolment, but it does publish the
queue before it: **up to five business days** for D&B to assign the number, then **up to two
business days** before Apple has it, and only then can you enrol. Budget **two weeks from
today to an active membership** and treat that as an estimate, not a promise.

## The two decisions only you can make

**1. Which entity enrols.** The name typed into the enrolment becomes the App Store seller
and must match the D‑U‑N‑S record character for character, including the suffix. Both
companies are yours, so either can be the seller; the question is which one should be named
on a Kerb-branded app, and which one holds the registered name, state and address that R7
asks for. **R7 is therefore an input to A1, not just paperwork for cto.new** — the same
registered-name answer is one of the §9d inputs they are missing. Note the domain's
registrant reads `Kerb RideSharing, Alabama` in WHOIS; that is what the registrar has, not
evidence of a state filing, and Apple checks the filing and D&B, not WHOIS.

**2. Organisation or individual.** An individual enrolment needs no D‑U‑N‑S, no legal
entity and no website, so it is the one path that does not touch B15. It also puts your
personal legal name in the App Store as the seller. Apple does allow the change later — an
individual membership is converted to an organisation by a **support request**, not by a
setting on the account — but that request wants the same legal entity and D‑U‑N‑S you were
avoiding, and the seller name stays personal until Apple approves it. So the individual
path buys time now and settles nothing; make that trade deliberately.

## What to have ready before you start

- The legal entity's **registered name, state of formation and address** (R7).
- The **D‑U‑N‑S number** for that entity, or D&B's lookup confirming it has none.
- An **Apple Account** with 2FA on, in your legal name, in the US region.
- A **phone that answers** during US business hours, and a mailing address — no P.O. boxes.
- The **state filing** (and a notarised copy if Apple asks).
- A card for the 99 USD.
- A **government-issued photo ID** — enrolment photographs it for identity verification.
- Ideally the `@kerbgo.com` mailbox from B10, so the Apple Account is on the domain.

## The order to do it in

1. **Settle R7 today** — registered legal name, state, address. Nothing else can be typed
   correctly until this is decided.
2. **Look up the D‑U‑N‑S number** for that exact name and address; request one if D&B has
   nothing. This is the only step with an external queue (up to five business days), so it
   starts first and runs in the background.
3. **Create the mailbox (B10)** while D&B works — the domain's MX and SPF already point at
   Google Workspace, so this is creating the user, not setting up mail.
4. **Create or tidy the Apple Account**: legal first and last name, 2FA, US region, recovery
   method, and the `@kerbgo.com` address if step 3 is done.
5. **Enrol as an Organisation** on the web, with the entity name exactly as D&B has it,
   declaring your binding authority as owner. Upload notarised documents if asked.
6. **Pay, and accept the Program License Agreement.** Keep the confirmation email and the
   **Enrollment ID**; if no email arrives within 24 hours, contact Apple quoting that ID.
7. **Record the Team ID** from Membership details once it is active — that is `A1` done, and
   it is `APPLE_TEAM_ID`.
8. **Then A2**: create the App Store Connect **API key** (Admin → Integrations → App Store
   Connect API; the `.p8` downloads once, and you get a Key ID and Issuer ID) and the
   **distribution certificate** (`.p12` + password). That is the other five secrets; then run
   *iOS release* with upload unticked and check the signed `.ipa`.
9. **Then A3**: the app record for `com.kerbgo.app`, free, US only, age rating 4+.
10. **Then the portal answers** — A8 (App Privacy), A7 (contacts), A6 (review notes, once
    B1's login works). None of these need the site, only the console.

## Where this collides with B15

Every step above runs while the site is down, except the website check: Apple expects a
public, working site on the organisation's domain, and `kerbgo.com` currently answers the
32-byte 404 on every route. So:

- **Start steps 1–4 today regardless.** The entity, the D&B record and the Apple Account are
  the slow, site-independent parts, and none of them can bounce on the website.
- **Decide the submission moment separately.** If the site is back before D&B answers, this
  is moot. If it is not, enrol anyway and answer the website question truthfully — a dated
  note that the site is being restored is a better answer than a parked page, and an
  enrolment is not refused for having said so. Do not stand up a placeholder site to pass
  the check; the same domain has to survive review later.
- **Do not let the website question push you to the individual path.** It solves this week
  and leaves the seller name personal until a conversion request is approved, which asks
  for the same entity and D‑U‑N‑S.

## What not to do

- **Do not create the app record before the membership exists** — there is no team to attach
  it to, and the bundle ID is claimed per team.
- **Do not put an alias or company name in the Apple Account's name fields**; Apple calls
  that out as a delay.
- **Do not share the Account Holder's credentials with CI.** The six secrets are an API key
  and a certificate precisely so the workflow never needs the account.
- **Do not promise a review date** off this note. It is an estimate of enrolment, not of
  review.

## Fill this in as you go

| Item | Value |
|---|---|
| Legal entity name as enrolled | |
| D‑U‑N‑S number | |
| Enrollment ID | |
| Date submitted / date active | |
| Team ID | |
| Membership renews | |

## What this note does not decide

- Which entity enrols — that is R7, and it is a legal question, not a technical one.
- Organisation versus individual — recorded above, so the trade-off is explicit when you
  choose.
- Anything about the fee waiver: not applicable to Kerb, and it needs nonprofit or
  accredited-education status.

## Appendix — the D&B lookup and the D‑U‑N‑S request

Apple's route is a lookup first, a request second: `developer.apple.com/enroll/duns-lookup/`.
The lookup asks for the **legal entity name, headquarters address, mailing address and your
work contact information** — four fields, no document upload. If the entity is not listed,
the same page offers to submit it to Dun & Bradstreet for a free number. Apple's stated
clock from there: up to **five business days** for D&B, then up to **two business days**
before Apple receives it; if the request passes two weeks, email D&B through the support
form linked from Apple's page. A D&B representative may call to ask the business type or
employee count, so the phone must answer and the **registration documents** should be at
hand.

**The one outcome that changes the plan:** if the entity comes back under a different legal
status — a sole proprietorship, or unverified — Apple refuses the organisation enrolment
with *"Your organization is not listed as a legal entity"*, and no amount of documents sent
to Apple fixes it; D&B has to correct the record first. If Kerb's filing turns out to be a
sole proprietorship rather than a corporation, LLC or limited partnership, organisation
enrolment is not available at all and the choice in *The two decisions* collapses to
individual. Check that before paying anything.

### Fill these in first

Nothing below can be typed correctly until R7 is settled — this is the same list, in the
shape D&B wants it.

- Legal entity name, exactly as filed, including the suffix: `__________`
- Business type (LLC / corporation / limited partnership): `__________`
- State of formation and date: `__________`
- State registration or file number: `__________`
- Headquarters street address (no P.O. box): `__________`
- City, state, ZIP: `__________`
- Mailing address, if different: `same as headquarters` / `__________`
- Business phone that answers, US: `__________`
- Work email on the company domain: `__________`
- Website: `__________` — this is also the site Apple's enrolment check looks at, so see
  *Where this collides with B15*
- Employees and year established, if D&B asks: `__________`
- The person making the request, legal name and title: `__________`

### What the lookup can return

| Result | What to do |
|---|---|
| A match, right legal status and address | Copy the nine digits into the log above and skip to enrolment |
| A match, wrong status or address | Ask D&B to correct it through their support form, with the state filing attached — up to two business days to reach Apple |
| No match | Submit the information from the same page; the number is free |

### Wording for the request

Use this in the fields the lookup page presents, or as the body if D&B asks you to email.
Replace every bracket; a bracket left in is worse than a slow request.

```
Subject: D-U-N-S Number request — [LEGAL ENTITY NAME]

I am requesting a D-U-N-S Number for my company. I need it to enrol in the Apple
Developer Program as an organisation, which requires a number registered to the
legal entity.

Legal entity name: [EXACTLY AS FILED, INCLUDING INC./LLC]
Business type: [LLC / CORPORATION / LIMITED PARTNERSHIP]
State of formation: [STATE], formed [YYYY-MM-DD]
State registration number: [NUMBER]
Headquarters address: [STREET, CITY, STATE ZIP]
Mailing address: [SAME AS ABOVE / OTHER]
Business phone: [NUMBER THAT ANSWERS]
Work email: [ADDRESS ON THE COMPANY DOMAIN]
Website: [URL]
Employees: [NUMBER]
Year established: [YEAR]

Contact for verification: [LEGAL NAME], [TITLE]
Business registration documents: [ATTACHED / AVAILABLE ON REQUEST]

Please call the number above if you need anything further. I am the owner and can
confirm the entity's details.
```

### After the number arrives

Record it in the log at the top of this note, wait out the two business days for Apple to
receive the D&B record, then start the enrolment at step 5 of *The order to do it in* — and
type the entity name exactly as D&B has it, not as the filing abbreviates it.
