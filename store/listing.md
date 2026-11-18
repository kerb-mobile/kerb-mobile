# Kerb — store listing copy

Copy for both stores. Fields marked **[you]** need your details before submission.
Character limits are the stores' own; the copy below is inside them.

Bundle / package ID: `com.kerbgo.app` · Version 1.0 (build 1) · Platform: iPhone, Android phone.

---

## Apple App Store

| Field | Limit | Copy |
|---|---|---|
| App name | 30 | `Kerb` |
| Subtitle | 30 | `Local rides, upfront fares` |
| Promotional text | 170 | `See the full fare before you book. No surge pricing, ever. Rides across the US up to 160 miles, with drivers who live where you live.` |
| Keywords | 100 | `rideshare,ride,taxi,local,driver,fare,airport,trip,booking,curb,grocery,delivery` |
| Primary category | — | Travel |
| Secondary category | — | Lifestyle |
| Support URL | — | `https://www.kerbgo.com/help` |
| Marketing URL | — | `https://www.kerbgo.com` |
| Privacy policy URL | — | `https://www.kerbgo.com/privacy` |
| Copyright | — | `Kerb` |

**Description**

```
Rides and groceries, right to your curb.

Kerb is a local rides service. Pick two places, see the fare before you book, and pay by card — no surprises at the end of the trip.

UPFRONT FARES
The price we quote is the price you pay. No demand multiplier, no time-of-day multiplier, no exceptions. Your fare is calculated from the rate card: base fare, per mile and per minute, shown in full before you confirm.

LOCAL DRIVERS
Drivers who live where you live. Every driver signs up, adds a vehicle, uploads their licence, registration and insurance, and is approved by a person before their first trip.

PAY BY CARD
Your card is authorised when you book and charged when the ride completes. Your card is saved to your account for next time — Kerb never stores your card details.

FOLLOW YOUR TRIP
Watch your driver approach and follow the trip live on the map, from pickup to dropoff.

COAST TO COAST
Kerb serves the entire United States. Trips run up to 160 miles, and each state line a trip crosses is added to the fare in full to the driver.

DRIVERS KEEP 75%
Drivers keep 75% of the fare, and the whole state-line amount. Sign up, get approved, go online, and offers arrive in the dashboard.

GROCERIES: COMING SOON
Grocery ordering is a waitlist today — join it in the app and we'll email you when it opens in your area.

Questions? Support is at kerbgo.com/help.
```

**App Review Information**

- Demo rider account: **[you]** email / password
- Demo driver account: **[you]** email / password (approved, with a vehicle, so the driver dashboard and the Navigate button are reachable)
- Notes: `Kerb is a native iOS app that presents kerbgo.com with native location, camera and navigation integration. Sign in with the demo rider account to book a ride; sign in with the demo driver account and open the driver dashboard to see the driver view. The "Navigate" control appears only on driver routes and offers Apple Maps, Google Maps, Waze, or Kerb's own map. Payment is by card for a physical transport service, processed on the web backend.`
- Contact: **[you]** name, phone, email

**App Privacy answers** (must match `ios/App/App/PrivacyInfo.xcprivacy`)

| Data type | Collected | Linked to you | Used for tracking |
|---|---|---|---|
| Name | Yes | Yes | No |
| Email address | Yes | Yes | No |
| Phone number | Yes (optional field) | Yes | No |
| Precise location | Yes | Yes | No |
| Coarse location | Yes | Yes | No |
| Payment info | Yes | Yes | No |
| Photos or videos | Yes (driver documents) | Yes | No |
| User ID | Yes | Yes | No |
| Purchase history | Yes (trips, fares) | Yes | No |

Purpose for every row: **App Functionality**. No data is used for tracking, so no App Tracking Transparency prompt is required.

**Age rating**: answer the questionnaire honestly. The app has location use and card payments, no user-generated content, no gambling, no mature content, no unrestricted web browsing outside kerbgo.com (only kerbgo.com stays in the app; other links open in the system browser).

**Export compliance**: already declared in `Info.plist` (`ITSAppUsesNonExemptEncryption = false`).

---

## Google Play

| Field | Limit | Copy |
|---|---|---|
| App name | 30 | `Kerb: Local Rides` |
| Short description | 80 | `Book local rides across the US with upfront fares. No surge pricing, ever.` (73) |
| Category | — | Maps & Navigation |
| Tags | — | rideshare, taxi, travel |
| Contact email | — | `kerb-221901fc@ctomail.io` |
| Website | — | `https://www.kerbgo.com` |
| Privacy policy | — | `https://www.kerbgo.com/privacy` |

**Full description**

```
Rides and groceries, right to your curb.

Kerb is a local rides service across the United States. Pick two places, see the fare before you book, and pay by card — no surprises at the end of the trip.

UPFRONT FARES
The price you're quoted is the price you pay. No demand multiplier, no time-of-day multiplier, no exceptions. Fares come from a published rate card — base fare, per mile, per minute — shown in full before you confirm.

LOCAL DRIVERS
Drivers who live where you live. Every driver adds a vehicle and uploads their licence, registration and insurance, and a person reviews it before their first trip.

PAY BY CARD
Your card is authorised when you book and charged when the ride completes. Kerb never stores your card details.

FOLLOW YOUR TRIP
Watch your driver approach and follow the trip live on the map.

COAST TO COAST
Trips up to 160 miles, anywhere in the United States. Each state line crossed is added to the fare in full to the driver.

DRIVERS KEEP 75%
Drivers keep 75% of the fare, plus the whole state-line amount.

GROCERIES: COMING SOON
Grocery ordering is a waitlist today — join it in the app and we'll email you when it opens near you.

Support: kerbgo.com/help
```

**App access** (required — the app needs a login)

Provide the same demo rider and driver credentials as for Apple, with the note that reviewers should sign in to reach the booking and driver screens.

**Data safety answers** (must match the iOS manifest)

- Does your app collect or share required user data types? **Yes, collects**
- Data types: Name, Email address, Phone number, Precise location, Approximate location, Payment info, Photos, User IDs, Purchase history
- Shared with third parties? **No** (payment processing by the payment provider is service-provider processing, not "sharing" under Play's definition — confirm with your payment provider's terms)
- Processing is ephemeral? **No**
- Required or optional? Name, email, location and payment are required for the service; phone number is optional
- Users can request deletion? **Yes** — point to the account deletion route
- Data encrypted in transit? **Yes** (HTTPS only; the app disables cleartext traffic)

**Content rating**: complete the IARC questionnaire. Expect a low rating (no violence, no sexual content, no profanity, no gambling, no user-generated content). Location sharing and card payments are declared separately, not through the rating.

**Other declarations**: no ads; app is not a news app; not COVID-related; not a government app; no financial-services features (rides are a transport service paid for by card, not banking, lending or investment).

---

## Assets already generated

| Store | Asset | File |
|---|---|---|
| Apple | 1024×1024 app icon (no alpha) | `ios/App/App/Assets.xcassets/AppIcon.appiconset/` |
| Google Play | 512×512 listing icon | `assets/play-store-icon-512.png` |
| Both | Launch splash | `ios/App/App/Assets.xcassets/Splash.imageset/`, `android/app/src/main/res/drawable*/splash.png` |

Screenshots and the Play feature graphic are covered in `store/screenshot-checklist.md`.
