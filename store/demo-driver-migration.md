# Moving the demo driver off the personal Apple ID

**Drafted 24 Nov 2026.** `site-changes.md` line 104 recorded the demo driver login as the
owner's personal iCloud address, and that address is also the Apple Account holding the paid
membership (B16). Two problems, in order of severity: an App Review reviewer would be handed
a credential that unlocks the account controlling the certificate, the API key and the app
record; and the address sits in a **public repository**, so it is already published whatever
this file does next.

Target: **`demo.driver@kerbgo.com`**, on the Workspace tenant that already delivers
`kerbgo.com` mail (B10), with its own password, in no transcript and no commit.

The one thing to internalise before starting: **a driver's demo state is attached to the
account, not to the address.** Nothing moves. The approved documents, the vehicle record,
the earnings history and the standing offer all have to exist on the new account before it
can stand in for the old one. That is the actual work, and it is why the phases below are
ordered the way they are.

---

## Phase 1 — today, no site needed (~40 minutes)

1. **Create the mailbox in Google Workspace** — a **user**, not a group and not an alias.
   Only a user has a password and a real inbox; an alias forwards mail to somewhere else and
   a group has no password, so neither can receive the site's verification message in a way
   you can read. Cost: one extra seat on the plan (Business Starter is about 8 USD a month).
2. **Give it a long unique password** and put it in the password manager only. Not in this
   repo, not in a chat, not in the team transcript — that is B11's failure repeated, and B11
   exists because the previous demo driver password went through that channel.
3. **Leave Google-side two-step verification off for this mailbox.** It is a demo mailbox
   that only you read, and a 2FA challenge on it would turn every site email step into a
   support call during review. The Kerb app login is separate from the Google account, so
   this does not weaken the account that matters.
4. **Prove it receives mail:** send from any other address and confirm arrival. Then send a
   password-reset from the site (once it serves) and confirm that lands too — the site's
   verification mail is the one that has to work.
5. **Rotate the Apple Account password** (B16). This is independent of the migration and
   should not wait for it: the value is in a transcript now. Two-factor stays on.

## Phase 2 — when the site serves again (B15), the real work

6. **Sign up on `kerbgo.com` as `demo.driver@kerbgo.com`.** Verify the address out of band
   so that no email step remains at sign-in — a reviewer cannot receive one, which is the
   rule the rider row is already held to.
7. **Complete driver onboarding** on the new account: a vehicle with an obviously synthetic
   plate (`DEMO…`), and synthetic photographs of licence, registration and insurance. No
   real licence, plate or address anywhere on it.
8. **Get it approved by a person**: approve the driver and each of the three documents
   (B14's condition — uploaded is not approved). Until this is done the account is not a
   substitute for the old one.
9. **Build the state the notes promise**: one completed trip so the Earnings tab shows a
   week and an all-time figure at the product's own 75% split; the standing offer from
   `site-changes.md` §4, present every time `/driver` opens without going online.
10. **Walk the driver steps on a clean install** — sign in at `/signin`, land on `/driver`,
    accept the offer, open the trip card, tap Navigate, open Earnings. This is steps 7–10 of
    `review-notes.md` and it is the acceptance test for the migration.

## Phase 3 — retire the old login, in this order

11. **Only after step 10 passes**, remove or disable the old driver account in the admin
    view, so exactly one demo driver login exists. Disabling before the new account is
    approved leaves no approved driver at all, which is worse than the address exposure.
12. **Check nothing else Kerb-side uses the Apple ID** — a second account, a saved login, a
    test booking. If it does, that is another migration, not a password change.

---

## What to change in the documents, and when

The rule applied here: **a document may name the target address, but must not present it as
a working login until it works.** Everything stays a placeholder or a marker until step 10.

| File | Now | After step 10 |
|---|---|---|
| `site-changes.md` line 104 | The address is **removed** as of 24 Nov 2026 with the reason stated, and the observation is kept — the driver sign-in worked on 19 Nov, it was simply not this document's job to publish whose account it was | Left as historical record. The 19 Nov check stays a check; the new account's own verification is a new dated line |
| `review-notes.md` driver row | **[not created yet]** with the target address and the instruction to paste the console credentials only after it signs in and is approved | Address filled in, password pasted **into the consoles only** — never into the file |
| `review-notes.md` "Where each piece is created" | Unchanged: the vehicle and approval still happen through the product's own admin and onboarding | Unchanged |
| `listing.md` line 66 | Same marker as `review-notes.md`, one line | Address filled in |
| `status.md` B11 | Points at this file: the exposed password is retired by moving the account, not rotated on it | B11 closes when the old account is disabled |
| `status.md` B16 | Points at this file for the driver-login half of the exposure | Closes at step 11 |

**What removing the line does not do:** the address is in git history from commit `c0024d0`
on a public repository, and editing the working copy does not unpublish it. Rewriting history
on a public repo to hide one personal email address breaks every clone and fork for very
little — the right response is the password rotation in step 5, which is what makes the
published address harmless. Say so out loud rather than implying the removal cleaned it up.

---

## Gates and fallbacks

- **Phase 1 is free today** and is the only part not gated on B15.
- **Phase 2 needs the site, the admin view and the §4 seeder** — the three things cto.new
  owes in `status.md`. Until they land, the migration cannot finish and the old account
  cannot be retired.
- **If the site returns late and review has to go first**, the fallback is the old account
  with a **rotated** password entered only in the consoles, and this migration finished
  immediately afterwards. That is a deliberate exception, not a quiet default: the reviewer
  would again hold a credential to the membership account, so it is a one-review risk, not a
  state to settle into.
