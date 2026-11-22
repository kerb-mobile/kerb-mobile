# cto.new — kerbgo.com is down (paste-ready message)

**Raised 21 Nov 2026, 16:45 UTC.** The live site stopped serving; every route answers
Cloudflare's 32-byte `There is no site at this address`. Tracked as B15 in `status.md`.
Send the block below to cto.new as-is. Re-verified before sending: still 404 at
16:45:54Z, while `cto.new` itself answers 200.

## What the evidence shows

| Check | Result |
|---|---|
| `https://www.kerbgo.com/` | 404, `text/plain`, 32 bytes: `There is no site at this address` |
| `/signin`, `/app`, `/terms`, `/privacy`, `/help`, apex `kerbgo.com` | same 404, same 32-byte body |
| Headers | `server: cloudflare`, `cf-ray a5e12af2bdd05598-ATL`, `date 21 Nov 2026 16:45:57Z` |
| DNS | `www.kerbgo.com` CNAME `sites.cto-new.app` → `104.21.42.107` / `172.67.205.64`; apex on the same pair — correct, no change needed |
| Re-check | 22 Nov 2026, 17:16 UTC — unchanged a day on: all routes and `sites.cto-new.app/` still 404 with the same 32-byte body, DNS unchanged, so B15 is still open |
| Platform | `https://cto.new/` → 200, so the platform is up |
| First seen | 21 Nov 16:18Z, in the iOS simulator run #4 screenshot (A4) |
| Last seen failing | 21 Nov 16:45:54Z |

The response is what Cloudflare returns when a hostname points at a Pages/Workers
project with no active production deployment, or one that no longer claims the
hostname. The site served normally at 20 Nov 21:02Z and the account hit its weekly usage
limit that evening, so the working assumption — an inference, not something visible from
outside — is that serving stopped when the usage-limit grace expired and did not resume
on the quota reset. A suspended site normally needs publishing again.

## The message

```
kerbgo.com has stopped serving sitewide — and it is the hosting layer, not DNS and not the code.

EVIDENCE (all checked from outside, UTC, 21 Nov 2026)

- https://www.kerbgo.com/  -> HTTP 404, content-type: text/plain, 32-byte body:
  "There is no site at this address"
- Same 404 and same body on /signin, /app, /terms, /privacy, /help,
  and on the apex https://kerbgo.com/
- Response headers: server: cloudflare, cf-ray a5e12af2bdd05598-ATL,
  date Sat, 21 Nov 2026 16:45:57 GMT
- DNS is correct and needs no change: www.kerbgo.com is a CNAME to
  sites.cto-new.app, resolving to 104.21.42.107 / 172.67.205.64; the apex is on
  the same pair. Both are proxied through Cloudflare.
- cto.new itself answers 200, so the platform is up. It is this project's
  hostname that is not being served.
- First observed 21 Nov 16:18Z: the iOS simulator run #4 screenshot shows that
  exact error page inside the app. Still failing at 16:45Z.

WHAT THAT MESSAGE MEANS

Cloudflare returns "There is no site at this address" when a hostname points at a
Pages/Workers project that has no active production deployment, or no longer
claims that hostname. The site was serving normally on 20 Nov at 21:02Z. The
account hit its weekly usage limit that evening, so my read is that serving
stopped when the usage-limit grace window expired and did not resume on the quota
reset (~06:47Z today). A suspended site usually needs to be published again.

PLEASE DO

1. Republish the Kerb project (Site tab -> publish) so a production deployment
   exists behind the domain again.
2. Check the project's own sites.cto-new.app subdomain first: if it serves the
   site, only the custom domain needs re-attaching; if it also 404s, the
   deployment is missing and step 1 is the fix.
3. If the Site tab no longer lists www.kerbgo.com and kerbgo.com as custom
   domains, re-attach both.
4. Do NOT change DNS. The CNAME is correct, and re-pointing it while the project
   does not claim the hostname turns this 404 into a 522, which is harder to
   diagnose.
5. Tell me the publish time and I will verify from outside.

HOW I WILL VERIFY

/, /signin, /app, /terms, /privacy and /help each returning 200 with real HTML
rather than a 32-byte text body.

IMPACT

Both store shells (iOS and Android) load https://www.kerbgo.com live in the
WebView, so the apps currently open on that error page, and App Review cannot
run until it is back. No app rebuild is needed once the site returns — the shells
pick the site up as soon as it serves.

WHILE YOU ARE IN THERE

If you publish a fresh build, please include the two driver fixes already specced
in store/site-changes.md: B4, the offer countdown printing raw seconds
("604798s"), and B5, "NEXT TURN — TO PICKUP" still showing after Start trip.
```

## Two cautions before you send

- The quota explanation is labelled as a read, not a fact. Only the symptom is
  observable from here; if cto.new knows otherwise, they should say so rather than
  chase that cause.
- The DNS warning is deliberate. The record looks like the obvious thing to fix, and
  touching it converts a 404 into a 522.

## After it is back

Re-run the six routes plus `/` and record the restore time against B15 in `status.md`,
then re-take the A4 captures — the simulator artifact from run #4 is a photograph of
this outage.
