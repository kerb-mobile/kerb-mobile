# A1b — the individual (sole proprietor) route: what it unblocks, and what it costs

**Drafted 24 Nov 2026, after the owner confirmed three facts:** the Apple Developer Program
membership has been **signed up for and paid**, Kerb is run as a **sole proprietorship under
the owner's personal name**, and **no D‑U‑N‑S number is required**. That makes the
organisation path in `a1-apple-enrolment.md` the wrong page: Apple does not let a sole
proprietor enrol as an organisation, and `a1-apple-enrolment.md` says so ("your organisation
is not listed as a legal entity" is what the D&B check returns for one). This page is the
individual route, with the trade stated rather than buried.

Apple's own wording, read 24 Nov 2026: *"If you're an individual or sole proprietor/single-person
business, your personal legal name will be listed as the seller on the App Store… If you have
enrolled as an individual and need to convert your individual account to an organization
account, please contact us."*

## What the individual route asks for — and what it does not

| Requirement | Individual | Note |
|---|---|---|
| Apple Account with 2FA, in your **legal** first and last name | **Yes** | An alias, nickname or company name in those fields delays approval of the enrolment. Check it now, before Apple does |
| Legal age of majority | Yes | — |
| Email, phone and a **street address** | Yes | P.O. boxes are refused |
| Government-issued photo ID | Yes, photographed during enrolment | Passport or driver's licence, name matching the Apple Account |
| Annual fee | **99 USD, already paid** | Identical to the organisation fee — the individual route saves no money |
| D‑U‑N‑S number | **No** | This is the ~5 business days at D&B plus up to 2 at Apple that the organisation route pays |
| Public working website on the organisation's domain | **No** | So A1 no longer waits on B15. The *app* still does — see Part 0 of `submit-day.md` |
| Notarised business documents, legal-binding-authority check | **No** | Apple's organisation verification has no equivalent here |

Consequence for the schedule: nothing external is left in the enrolment. What remains is
console work you can do today — the Team ID, the API key, the distribution certificate, the
six repository secrets, and the app record. That is A2, A3 and A8 in `status.md`, and it is
an afternoon, not two weeks.

## Check the membership is actually active before building on it

Payment is not the same as an active program. developer.apple.com → Account →
**Membership details**:

1. **Program status** reads *Active*. If it reads *Pending* or asks for identity
   verification, finish that first — nothing below exists until it clears.
2. **Team ID** is shown: 10 characters, e.g. `A1B2C3D4E5`. That value is the
   `APPLE_TEAM_ID` secret.
3. **Entity type** reads *Individual*, and the name on the membership is your legal name —
   the same name the seller will show.
4. **Agreements:** App Store Connect → Business → Agreements shows the **Free Apps**
   agreement accepted. A free app needs no Paid Apps agreement, no tax forms and no bank
   details; do not fill those in for this submission.

## What the individual route gives up — plainly

For a free app whose only job is to get approved, none of these stops the submission. Each
one is a real cost, so none of them is hidden:

1. **Your personal legal name is the seller, permanently, for as long as the account is an
   individual one.** Every App Store product page shows *Seller: <your name>* under the app
   name, and every App Store receipt names you. "Kerb" cannot be the seller no matter what
   the app is called — Apple's rule for sole proprietors is explicit, and the only way to
   change it is to stop being one for Apple's purposes.
2. **The app is publicly attached to a person, not a business.** Anyone — a rider, a
   competitor, a journalist — can read that seller line. If the brand is meant to look like
   a company from day one, this is the part that shows otherwise.
3. **Money and tax are personal.** Apple pays the seller, which is you: the earnings are
   your income, reported to you personally, not to a company. If Kerb incorporates later,
   that history does not move with the app.
4. **You cannot add developer-team members.** An individual membership can invite up to 50
   people into **App Store Connect** to work on your content, but they are not part of your
   Apple Developer Program team and do not get the membership resources — in practice,
   certificates, identifiers and profiles stay with you. A contractor who needs to manage
   signing cannot be given that access; you hold the certificate and the API key.
5. **Converting later is a support request that asks for everything you skipped.** Apple
   converts individual → organisation on request, and the request wants the legal entity and
   a D‑U‑N‑S number registered to it, plus proof you can bind it — the same records R7 is
   still chasing and the same 5 + 2 business days. Until Apple approves the conversion the
   seller name stays personal, including while the app is live, so a conversion attempted
   mid-review can add delay to an approval that was otherwise done.
6. **Selling the business later means transferring an individually held app.** If a buyer is
   a company, the account converts first, which is the same support request again.
7. **The entity question does not disappear, it moves to the site.** `/terms` and `/privacy`
   still need a real counterparty named (R7), and for a sole proprietorship the honest
   version is your name, trading as Kerb. The App Store will be saying the same thing in
   public, so the two should agree rather than quietly contradict.

What the individual route **does not** cost: the fee (same 99 USD), the app's name, bundle
ID or review outcome, TestFlight, CI signing with an App Store Connect API key, adding App
Store Connect users, or the ability to ship to the App Store at all. It is a seller-identity
and team-scope trade, not a capability trade.

## What this closes, and what it does not

| Item | Effect |
|---|---|
| **A1** Apple Developer Program enrolment | **Closed**, subject to the Program status check above being *Active* |
| **A2** six repository secrets | Now unblocked. The distribution certificate and the API key both exist only for a member |
| **A3** App Store Connect app record | Now unblocked, under the individual Team ID |
| **A8** App Privacy answers | Unblocked, and independent of the entity question |
| **R7** `/terms` and `/privacy` naming an entity | **Not closed.** It is no longer an input to Apple, but the site still needs a named counterparty, and the name to use is your name trading as Kerb until there is an entity |
| **A4** screenshots | **Still blocked on B15.** A membership does not put a site behind the WebView |
| **B15, B1–B7, B14** | Untouched. The app still cannot be submitted while the site 404s |

The one thing to be clear about: **the membership was the longest-lead item, and it is now
done — but it was never the thing blocking submission.** What blocks submission is Part 0 of
`submit-day.md`, and the row that matters is row 1: the site.

## Order of work from here

1. **Confirm the four membership checks above** (Program status, Team ID, entity type, Free
   Apps agreement). If the Program status is not Active, chase that first — everything
   Apple-side waits on it.
2. **Create the App ID and the API key**, then the distribution certificate, then all six
   secrets (`submission-guide.md`, "Portal steps, once"). About 45 minutes, and only the
   certificate step wants a Mac.
3. **Dry-run *iOS release* with `upload` unticked.** A signed `.ipa` artifact with nothing
   sent to Apple is the proof the signing works.
4. **Create the app record** — free, US only, `com.kerbgo.app`, SKU `KERB-IOS-1`, 4+, and
   enter the nine App Privacy rows.
5. **Then wait on the site**, and use the wait for the Play work that does not need it:
   the app record, the declarations, and the twelve testers (`submit-day.md` Parts 1 and 3).

Do not start the organisation conversion now. It cannot succeed while Kerb is a sole
proprietorship, and starting it mid-submission only adds an open case against the account
that is about to be reviewed.
