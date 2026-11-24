# B10 — `support@kerbgo.com`: create the mailbox, prove it receives mail, add DMARC

**Drafted 24 Nov 2026.** B10 is open because the address exists everywhere and the mailbox
behind it does not: the site renders it, `listing.md` and `review-notes.md` point reviewers
at it, and `safety-and-licensing.md` §9 will publish it as the complaint route. It gates A7
(the review contact) and every support link a reviewer follows. None of it needs the site
to be serving.

## What is already in place — checked 24 Nov 2026, 16:35 UTC, DKIM at 16:59 UTC

| Record | Value | What it means |
|---|---|---|
| MX | `1 smtp.google.com` | Google Workspace is already the mail host for `kerbgo.com` |
| SPF | `v=spf1 include:_spf.google.com ~all` | Google is authorised to send as the domain |
| Other TXT | `google-site-verification=XMQetYcW…` | Someone already verified the domain in a Google account |
| DMARC | none — `_dmarc.kerbgo.com` returns nothing | The gap this note closes last |
| DKIM | `google._domainkey` TXT: `v=DKIM1;k=rsa;p=MIIBIjAN…` (TTL 300); no `default`, `selector1`, `s1` or `k1` selector exists | Google already signs outbound mail as `kerbgo.com`. Do **not** generate a new key |

So this is creating a user in a tenant that already exists, not setting up mail. Nothing in
the MX or SPF records needs to change, and they should not: they are correct, and the
records are the thing keeping the domain's mail working.

## 0. First, confirm you can administer the tenant

The MX, SPF and verification records prove a Workspace tenant exists for `kerbgo.com`. They
do not prove which Google account holds it. Before touching anything:

1. Sign in at `admin.google.com` with the account that owns the tenant, and confirm the
   domain shows as **verified** and you hold the super admin role.
2. If you cannot get in, stop and treat it as an access-recovery job, not a mailbox job.
   The domain is at Gandi, the same account as the DNS zone, so recovery can be proven by
   placing a record there — which is also why the Gandi login matters for A1 and plan B.

Note the tenant type: a paid Workspace subscription and a legacy free G Suite tenant look
the same in the console but bill differently. The console shows the current seat price;
creating a **user** needs a seat, a **group** or **alias** does not.

## 1. Create the mailbox

**As a user (a real inbox, needs a seat)** — the version reviewers will notice, because
replies come from the address itself:

1. `admin.google.com` → **Directory → Users → Add new user**.
2. First name `Kerb`, last name `Support` (the display name reviewers see), primary email
   `support@kerbgo.com`.
3. Set a strong password, and keep it wherever the other console credentials live — never
   in this repository.
4. Save. Then sign in as that user once to accept the terms, so the account is live and not
   waiting on a first-login screen.
5. If more than one person answers support, put the user in a Google Group and give the
   group the address instead, so replies are not owned by one person's inbox.

**Without a seat (group or alias)** — fine for receiving, weaker for A7's "monitored email":

- **Group**: `Groups → Create group`, address `support@kerbgo.com`, add the people who
  answer as members, and allow external senders.
- **Alias**: on an existing user, `Directory → Users → <user> → Alternate emails (aliases)`
  → add `support@kerbgo.com`. Mail lands in that person's inbox, and replies sent from the
  alias keep the right From address once it is added under *Send mail as*.

Either way the address must **receive from outside the domain**, which is the check that
closes B10.

## 2. Prove it receives mail — this is B10's done-when

Send from an address that is **not** `@kerbgo.com`; a personal Gmail is ideal because it
is a different mail system, and a second test from Outlook or a work account is worth the
extra minute.

| # | Do this | Pass looks like |
|---|---|---|
| 1 | Send a plain message from the external address to `support@kerbgo.com` | It arrives in the inbox, not spam or quarantine |
| 2 | Open it → **Show original** | A normal inbound message, so the authentication results are the **sender's** domain, not `kerbgo.com` — this row proves delivery only |
| 3 | Reply from `support@kerbgo.com` to the external address | It arrives, and the From is `support@kerbgo.com`, not the underlying user |
| 4 | On the reply, **Show original** in the receiving account — this is the test of `kerbgo.com` itself | `spf=pass` and `dkim=pass` with `d=kerbgo.com`; `dmarc=none` until step 4's record exists and `dmarc=pass` after it |
| 5 | Send to a deliberately wrong address, e.g. `supportx@kerbgo.com` | It **bounces** — a catch-all that swallows typos hides a real mailbox failure |

Keep the two `Authentication-Results` header lines and the date; they are the evidence for
the B10 row in `status.md`, whose done-when is "created and receiving mail". Paste the
headers into `status.md` (or a run log) rather than describing them.

## 3. DKIM — already published, so do not regenerate

`google._domainkey.kerbgo.com` answers with a `v=DKIM1;k=rsa` TXT record, so DKIM is already
signing outbound mail for the domain and there is nothing to do here. Generating a new key
would replace the selector: until the new record is live and authentication has started,
mail is signed with a key receivers cannot verify, which is worse than sending with none.

The one thing still worth checking is that **Authenticate email** in the admin console shows
authentication as started — a published record that was never switched on signs nothing.

If the record ever has to be recreated:

1. `admin.google.com` → **Apps → Google Workspace → Gmail → Authenticate email**.
2. Generate a **2048-bit** key for `kerbgo.com`.
3. Add the TXT record it gives you at Gandi, on `google._domainkey`, and wait for
   propagation.
4. Return to the same screen and click **Start authentication**.

Do it in this order: generating a second key before the first record is live replaces the
selector and signs nothing.

## 4. The DMARC record to add

Add one TXT record, at Gandi, alongside the existing ones:

| Name | Type | Value |
|---|---|---|
| `_dmarc.kerbgo.com` | TXT | `v=DMARC1; p=none; rua=mailto:support@kerbgo.com; pct=100; adkim=r; aspf=r` |

**Why `p=none` first.** DMARC's policy decides what receivers do with mail that fails —
`none` only reports, `quarantine` and `reject` start discarding. The domain has a live site,
a transactional sender somewhere in `site-migration.md`'s port, and possibly a form that
sends as `@kerbgo.com`; none of that is inventoried yet. `p=none` plus reports tells you who
sends as the domain **before** you can break them.

Then, in order, and only with the reports to justify it:

1. Read `rua` reports for about two weeks. Every legitimate sender should show `pass` with
   an aligned SPF or DKIM.
2. Move to `p=quarantine`, keep `rua`, and watch for anything that starts failing.
3. Move to `p=reject` last. Add `sp=reject` for subdomains only once you know nothing sends
   as one.

Two constraints worth knowing before you type it:

- **One TXT record per name.** A second `_dmarc` TXT makes the policy unreadable and is
  treated as no policy at all.
- **DMARC needs alignment, not just authorisation.** SPF must match the envelope domain and
  DKIM must sign as `kerbgo.com` — which is why step 3 comes before this one. A third-party
  sender later (the site's email provider after a port) needs its own aligned DKIM or an SPF
  include, or its mail will fail DMARC while Google's keeps passing.

## What this unblocks

- **A7** — the review contact email, which cannot be chosen until an address someone reads
  exists.
- The support links in `listing.md` and `review-notes.md`, which currently point at an
  address that bounces.
- `safety-and-licensing.md` §9's `Email: support@kerbgo.com` line, which is the one part of
  that page deliberately held back until the mailbox answers.
- A1's cleanest Apple Account, on the organisation's own domain.

## Evidence to record

| Date | From → to | Arrived | `Authentication-Results` | Reply received |
|---|---|---|---|---|
| | | | | |
