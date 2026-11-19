# Review notes

Paste-ready text for App Review and Play. Fill the two bracketed account blocks
before submitting — never commit real credentials to this repository.

## Apple — App Review Information → Notes

```
Kerb is a ride-booking app for local rides in the United States. The app is a
thin native shell around our own web application (kerbgo.com), with native
controls layered on top for the parts a browser cannot do well:

- A native "Navigate" control on the driver dashboard, with an action sheet that
  hands the trip destination to Apple Maps, Google Maps or Waze.
- Device location for pickup and trip following.
- A native tab bar and driver dashboard layout, so the app does not feel like a
  website in a frame.
- The native camera for driver document photos.

How to review the rider flow:
1. Sign in with the rider demo account below.
2. The home screen asks for a pickup and a destination. Enter any two US
   addresses (for example "West Magnolia Avenue, Auburn, AL" and
   "East Thach Avenue, Auburn, AL").
3. The fare is shown before booking, itemised by base, distance, time, minimum
   and booking fee. Tap "Book ride" to reach the payment step.

How to review the driver flow:
1. Sign out, then sign in with the driver demo account below.
2. The dashboard shows the driver's status, today's earnings and the Navigate
   control.
3. Tap Navigate to see the native action sheet. The other tabs are Earnings and
   Account.

Rider demo account:    [email] / [password]
Driver demo account:   [email] / [password]

Notes:
- The app targets iPhone only (TARGETED_DEVICE_FAMILY = 1).
- Export compliance: the app uses no encryption beyond standard HTTPS; the
  Info.plist declares ITSAppUsesNonExemptEncryption = false.
- Account deletion is [describe the in-app path here — required by 5.1.1(v)].
- Location is used only while the app is open. There is no background location.
- No third-party advertising or analytics SDKs are included.
- Support: support@kerbgo.com — https://www.kerbgo.com/help
```

The account-deletion line is the 5.1.1(v) answer. The path to describe is
`Account → Delete account → confirm`, which ships with the site change in
`site-changes.md` §1 — fill the sentence in once that is deployed, not before.
The driver walkthrough below expects the trip card's `data-kerb-destination`
(`site-changes.md` §2) for the Navigate control to read the dropoff.

## Apple — App Review Information → Contact

Use the account owner's name, a monitored email and a phone number that answers
during US business hours. Reviewers do call.

## Google Play — App access

Choose **All or some functionality is restricted**, then add both accounts:

```
Rider account — full ride-booking flow
Username: [email]
Password: [password]
Instructions: Sign in at the Welcome screen. Enter any two US addresses to see a
fare estimate and reach the booking step.

Driver account — driver dashboard, Navigate and earnings
Username: [email]
Password: [password]
Instructions: Sign in at the Welcome screen. The dashboard opens on /driver.
Tap "Navigate" for the native map chooser, and the Earnings tab for pay details.
```

## Google Play — Review notes (optional field)

```
Kerb books local rides in the United States. The app wraps our web application
with native controls: a native Navigate action sheet (Apple Maps, Google Maps,
Waze), device location, a native tab bar and the system camera for driver
documents. No ads, no third-party analytics, no in-app purchases — rides are paid
with a card through our payment processor.

Location permission is used only while the app is open (foreground only).
```

## Reviewer walkthrough, in order

Do this yourself the day before submitting, on a clean install:

1. Fresh install, no session → Welcome screen, "Create a free account" visible.
2. Sign in as the rider → rider home, three tabs, no console errors.
3. Enter a pickup and a destination → fare card with the itemised breakdown.
4. Tap "Book ride" → payment step renders.
5. Sign out, sign in as the driver → `/driver`, Navigate pill above the tab bar.
6. Tap Navigate → three map options; choose Google Maps and confirm the address
   carries across. If it says "Destination not available", the site's trip card
   is missing `data-kerb-destination`.
7. Earnings tab → `/driver/earnings` renders, Navigate pill disappears.
8. Turn off location in Settings, reopen → the app still works and the location
   chip explains the state instead of hanging.
9. Airplane mode, reopen → the app shows a reconnect path, not a blank screen.
