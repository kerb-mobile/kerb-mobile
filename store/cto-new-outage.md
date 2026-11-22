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
| Re-check | 22 Nov 2026, 17:38 UTC — unchanged a day on: `/`, `/signin`, `/app`, `/terms`, `/privacy`, `/help` on `www` **and** the apex `/` and `/privacy` all still 404 with the same 32-byte body and `cache-control: no-store`; `sites.cto-new.app/` too (checked 17:16Z). DNS unchanged, TLS valid. Per-request `cf-ray` values are in `status.md` — one ray per request, not one for the set. B15 still open; see the follow-up below |
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

## The follow-up (drafted 22 Nov 2026, 17:20 UTC — send if it is still 404)

Send this only if a check just before sending still shows the 404. It supersedes the
message above rather than repeating it, and it leads with the one diagnostic the first
report could not make: `sites.cto-new.app` itself 404s, so the deployment does not exist
and re-attaching domains cannot help.

```
Subject: kerbgo.com has now been down over 24 hours — the deployment is missing, not
          the domain

Following up on yesterday's report (sent 21 Nov 16:45Z). It is still down: first seen
21 Nov 16:18Z, still failing 22 Nov 17:20Z. Both store apps load this URL, so App
Review is blocked on it for the second day.

EVIDENCE (checked from outside, Sun 22 Nov 2026, 17:20Z)

- https://www.kerbgo.com/  -> HTTP 404, content-type: text/plain;charset=utf-8,
  32-byte body: "There is no site at this address"
- Same on /signin, /app, /terms, /privacy and /help
- Apex https://kerbgo.com/ and https://kerbgo.com/privacy -> same 404, same body
- Headers: server: cloudflare, cache-control: no-store,
  cf-ray a5e99adaaff16779-ATL, date Sun, 22 Nov 2026 17:20:27 GMT
- https://sites.cto-new.app/ -> the SAME 404 and the same body
- DNS is unchanged and correct: www.kerbgo.com is a CNAME to sites.cto-new.app,
  resolving to 104.21.42.107 / 172.67.205.64; the apex is on the same pair. TLS
  completes on both hostnames.

WHAT THE sites.cto-new.app RESULT SETTLES

That hostname is the project's own. If the custom domains had merely come loose, it
would still serve the site. It does not, so there is no production deployment behind
the project at all — nothing is being routed to anything. That means re-attaching
www.kerbgo.com and kerbgo.com as custom domains will not fix this, and DNS is still not
the problem. Only a publish creates the deployment, so everything else is downstream of
that one action.

PLEASE

1. Publish the project (Site tab -> publish) and tell me the timestamp; I will verify
   from outside within minutes.
2. Tell me whether the deployment disappeared or was never live. If the Site tab shows
   no deployment, or publishing is refused, say which refusal (quota, suspended
   project, billing) — that is the part I cannot see, and it decides whether this is a
   button or a plan/support escalation.
3. If the project is no longer in the dashboard at all, say so plainly today. Rebuilding
   the deployment from the repository is a different, larger job and I need to know
   before I promise either store a review date.
4. Do not change DNS. The CNAME is correct; re-pointing it while the project serves
   nothing turns this 404 into a 522, which is harder to diagnose.

HOW I WILL VERIFY

/, /signin, /app, /terms, /privacy, /help and the apex / each returning 200 with real
HTML rather than a 32-byte text body — then the apps come back on their own with no
rebuild, and I re-take the screenshots the outage has been blocking.
```

## Two cautions before you send

- The quota explanation is labelled as a read, not a fact. Only the symptom is
  observable from here; if cto.new knows otherwise, they should say so rather than
  chase that cause.
- The DNS warning is deliberate. The record looks like the obvious thing to fix, and
  touching it converts a 404 into a 522. The follow-up keeps it for the same reason, and
  adds the `sites.cto-new.app` check precisely so "fix the DNS" is not the reply.
- The follow-up asks for a refusal reason. If cto.new answers only "still looking", that
  is the answer: nothing has been published, and the ask in step 1 stands.

## After it is back

Re-run the six routes plus `/` and record the restore time against B15 in `status.md`,
then re-take the A4 captures — the simulator artifact from run #4 is a photograph of
this outage.
