# Compliance answers and rejection risks

Derived from https://www.kerbgo.com/privacy (last updated 16 November 2026),
`ios/App/App/PrivacyInfo.xcprivacy`, `Info.plist` and the app's own screens.
Where a fact can only be confirmed by the account owner it is marked
**[confirm]** — answer those from the live console, not from this file.

**Site-side prerequisites:** five fixes had to land on kerbgo.com before either
store would approve the shells — in-app account deletion, `data-kerb-destination`
on the driver trip card, the landing copy, a standing demo offer so the driver
flow can be walked at all, and the `KERBREVIEW` promo code so a booking can be
finished without a card. **All five landed and were verified live on 20 Nov
2026**, except that the promo code has never been proven on production because
the Demo Rider login it needs is refused. Open items are in `status.md`; the
specs are `site-changes.md` §1–§4 and §6.

## Apple — App Privacy

Data type, then: collected / linked to identity / used for tracking / purpose.

| Apple category | Declare | Linked | Tracking | Purpose |
|---|---|---|---|---|
| Contact Info → Name | Yes | Yes | No | App Functionality |
| Contact Info → Email Address | Yes | Yes | No | App Functionality |
| Contact Info → Phone Number | Yes (optional field) | Yes | No | App Functionality |
| Location → Coarse & Precise | Yes | Yes | No | App Functionality |
| Financial Info → Payment Info | Yes **[confirm]** | Yes | No | App Functionality |
| Financial Info → Payment History | Yes | Yes | No | App Functionality |
| User Content → Photos or Videos | Yes (driver documents) | Yes | No | App Functionality |
| Identifiers → User ID | Yes | Yes | No | App Functionality |
| Usage Data | No | — | — | No analytics in the policy |
| Diagnostics | No | — | — | No crash reporting wired up |

**Tracking:** No. The policy states no third-party advertising trackers and no
sale of data, which matches turning App Tracking Transparency off entirely.
Do **not** add `NSUserTrackingUsageDescription`; there is nothing to track.

**Payment info:** cards are entered in the app and go to a payment processor; the
policy says the operator never stores card details on its own servers. If the
processor is Stripe, Apple treats it as a service provider and this stays a
"collected, not shared" answer. **[confirm the processor before submitting.]**

The privacy manifest already covers the required-reason APIs the Capacitor stack
uses (`UserDefaults`, file timestamp, disk space) and declares no tracking
domains. It is bundled in the app target.

## Apple — review risk, ranked

**Updated 20 Nov 2026:** risks 1, 3 and 4 are fixed on the site — account
deletion, the landing copy and the driver destination attribute were all verified
live, with the standing offer behind risk 4 in place. They stay listed because the
reasoning still explains what a reviewer looks for. Everything still open is in
`status.md`.

1. **5.1.1(v) account deletion — was a rejection as it stood.** The app creates
   accounts and the only deletion path is "email us" in the privacy policy
   (`You can ask us to delete your account ... by emailing`). Apple requires
   deletion to be *initiated in the app*. The fix is one route, not two: the
   site's own `/delete-account` page, reached from the Account tab, because the
   shells add no native deletion item and only render the site's markup.
   The site is deliberately untouched by this repository, so this fix cannot be
   made from here — nor can any of the other site-side items in
   `site-changes.md`. The exact route, deletion semantics and
   entry-point markup the shell already reads are specified in `site-changes.md`
   §1; that change closes this risk with no app release.
2. **4.2 minimum functionality.** A WebView over a website is the classic 4.2
   rejection. What already helps: a native Navigate control with an action sheet,
   native hand-off to Apple/Google/Waze maps, device location, a native tab bar,
   a native camera path for driver documents, and a startup screen that keeps
   retrying the site on its own instead of parking on "you are offline". What
   would help more, in order of effort: push notifications for offers, background
   location while online, and the native review prompt. State the native
   integrations explicitly in review notes — reviewers often miss them.
3. **The landing page contradicts the app.** The first screen inside the app
   reads "pay by card — no app-store download". A reviewer opening the app to
   find a page that says there is no app to download is a bad first impression
   and weakens the 4.2 argument. Recommend adjusting that sentence on the site
   (and it is now factually wrong, since there will be an app). Both sentences
   and their replacements are in `site-changes.md` §3.
4. **2.1 completeness.** Anything a reviewer can tap that shows an error, or a
   flow with nothing in it, is a finding. The Navigate control shows "Destination
   not available" whenever the trip page does not carry `data-kerb-destination`;
   that needs the site-side attribute (see `screenshot-checklist.md`, and
   `site-changes.md` §2 for the contract the native reader enforces) or the driver
   will be told the app cannot read their own trip. The same applies one step
   earlier: the driver demo account is told to accept an offer, and offers only
   exist while dispatch is running, so an idle system presents an empty dashboard.
   `site-changes.md` §4 specifies the standing demo offer that fixes it.
5. **3.1.1 in-app purchase.** Not a risk, but worth knowing: rides are physical
   services, so card payment outside IAP is correct. Do not add IAP.

## Apple — age rating and metadata

4+, no unrestricted web access (the WebView is locked to kerbgo.com and opens
external map apps only), no user-generated public content, no gambling, no
violence, no mature themes.

## Google Play — Data safety

| Question | Answer |
|---|---|
| Does your app collect or share required user data types? | Yes |
| Personal info → Name, Email, Phone, User IDs | Collected, not shared, encrypted in transit, used for app functionality, deletable on request |
| Location → Approximate and precise | Collected, not shared, used for app functionality |
| Financial info → Payment info, Purchase history | Collected, not shared with third parties beyond the payment processor **[confirm]**, used for app functionality |
| Photos → Photos | Collected (driver documents), used for app functionality |
| App activity → Other user-generated content (trip history, saved addresses) | Collected, used for app functionality |
| Device or other IDs | Not collected by us; the session cookie is server-side, first-party |
| Data deletion | **Yes** — in the app at `Account → Delete account`, plus the email fallback the policy keeps for locked-out users. Data deletion URL: `https://www.kerbgo.com/delete-account`, verified `200` with `cache-control: no-store` on 20 Nov 2026. |

**Ads:** No. **News:** No. **COVID-19:** No. **Government:** No.
**Financial features:** none of the listed categories. **Health:** No.

## Google Play — content rating (IARC questionnaire)

Category: **Utility, Productivity, Communication or Other**. Answers: no
violence, no sexuality, no profanity, no controlled substances, no gambling, no
user-generated content shared with other users, no personal information shared
with third parties, no digital purchases, does not allow users to interact or
exchange content **[confirm — riders and drivers can call or message each other?]**.
Expected rating: Everyone / 3+.

Target audience: **18+**, and therefore not in the Designed for Families
programme. Ride-hailing with payment is an adult-audience product; declaring a
younger audience invites extra policy obligations with no benefit.

## Google Play — policy items to complete before review

- **Data deletion URL** — Play surfaces this field in the listing. Enter
  `https://www.kerbgo.com/delete-account`: it answers `200`, serves its
  signed-out state, and carries `cache-control: no-store`.
- **Account deletion** — Play's policy mirrors Apple's: if users can create an
  account, deletion must be available. Same fix as Apple's item 1.
- **App access** — every reviewer must be able to sign in, and the account they
  sign in with must have something to show. Two demo accounts and the demo state
  they need are in `review-notes.md`; the one piece of it that is site work rather
  than account setup is the standing driver offer in `site-changes.md` §4.
- **Location permissions** — Play requires a declaration only for background
  location. The app requests foreground location only, so nothing to declare
  today. Adding background location later triggers the video-review requirement.
