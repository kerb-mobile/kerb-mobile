# cto.new — kerbgo.com is down (paste-ready message)

**Raised 21 Nov 2026, 16:45 UTC.** The live site stopped serving; every route answers
Cloudflare's 32-byte `There is no site at this address`. Tracked as B15 in `status.md`.
The block below is the report as sent on 21 Nov, re-verified before sending: still 404
at 16:45:54Z, while `cto.new` itself answers 200. The follow-up further down carries the
24 Nov re-check and is what to send now.

## What the evidence shows

| Check | Result |
|---|---|
| `https://www.kerbgo.com/` | 404, `text/plain`, 32 bytes: `There is no site at this address` |
| `/signin`, `/app`, `/terms`, `/privacy`, `/help`, apex `kerbgo.com` | same 404, same 32-byte body |
| Headers | `server: cloudflare`, `cf-ray a5e12af2bdd05598-ATL`, `date 21 Nov 2026 16:45:57Z` |
| DNS | `www.kerbgo.com` CNAME `sites.cto-new.app` → `104.21.42.107` / `172.67.205.64`; apex on the same pair — correct, no change needed |
| Re-check | 24 Nov 2026, 16:18 UTC — unchanged on the third day: `/`, `/signin`, `/app`, `/terms`, `/privacy`, `/help` and `/delete-account` on `www` **and** the apex `/` and `/privacy`, plus `sites.cto-new.app/`, all still 404 with the same 32-byte body and `cache-control: no-store`. The 22 Nov 17:38 UTC pass reached the same result. DNS unchanged (`sites.cto-new.app`, `104.21.42.107` / `172.67.205.64`), TLS valid. One `cf-ray` per request, not one for the set — the rays are listed in `status.md` and in the follow-up below |
| Platform | `https://cto.new/` → 200, so the platform is up |
| First seen | 21 Nov 16:18Z, in the iOS simulator run #4 screenshot (A4) |
| Last seen failing | 24 Nov 2026, 16:18:28Z |

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

## The follow-up (drafted 22 Nov, refreshed 24 Nov 2026, 16:18 UTC — send if it is still 404)

Send this only if a check just before sending still shows the 404. It supersedes the
message above rather than repeating it, and it leads with the one diagnostic the first
report could not make: `sites.cto-new.app` itself 404s, so the deployment does not exist
and re-attaching domains cannot help. The 24 Nov pass replaced the 22 Nov evidence — the
same ten URLs, the same 32-byte body, a ray for each request, and exactly 72 hours since
the error was first photographed. It also carries an escalation ask — source, database
export, account holder and a publish deadline — because three days of a publish-only
request have produced no deployment.

```
Subject: kerbgo.com has now been down 72 hours — the deployment is still missing, not
          the domain

This follows my report of 21 Nov 16:45Z. Nothing has changed: first seen down
21 Nov 16:18Z, still failing 24 Nov 16:18Z — 72 hours exactly, the third day. Both store
apps load this URL, so App Review is blocked on it for the third day, and I need to know
today whether a production deployment exists behind the project at all.

EVIDENCE (checked from outside, Tue 24 Nov 2026, 16:18Z — one cf-ray per request)

- https://www.kerbgo.com/  -> HTTP 404, content-type: text/plain;charset=utf-8,
  32-byte body: "There is no site at this address", cache-control: no-store,
  cf-ray a5f9bab76a3dc36f-ATL, date Tue, 24 Nov 2026 16:18:24 GMT
- Same 404, same body, same no-store on /signin a5f9baba1b29b577-ATL,
  /app a5f9babc9973dec9-ATL, /terms a5f9babf5b68d7da-ATL,
  /privacy a5f9bac0db45b0ac-ATL, /help a5f9bac35efd1eff-ATL and
  /delete-account a5f9bac4da1bb4be-ATL
- Apex https://kerbgo.com/ a5f9bac81bb1eb0c-ATL and https://kerbgo.com/privacy
  a5f9bacb8bc34724-ATL -> same 404, same body
- https://sites.cto-new.app/ a5f9bace380bc102-ATL -> the SAME 404 and the same body
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

WHAT I AM ASKING FOR, AND BY WHEN

Publish, or tell me plainly that you cannot, by Wed 25 Nov 2026, 16:18Z — 96 hours after
I first saw this. I am not asking for 96 hours of work. I am asking for one of two
answers by then, because a recovery plan that does not depend on you takes weeks, and I
cannot run it and wait at the same time.

5. If you can publish, do it and send me the timestamp; I will confirm from outside
   within minutes.
6. If you cannot, or do not know when, send me these three things today:
   a. the site's source — a repository URL I can clone, or an archive;
   b. an export of the project's database, including the demo accounts, the standing
      offer and the trip history;
   c. the name of whoever holds the Cloudflare account this project deploys under, and
      what it would take for me to be let into it, or for them to press publish.

Those are the project's own assets, not a favour, so I am asking where they are rather
than for anything unusual. If any of them are held personally rather than by the project,
say that plainly instead of leaving it out — I would rather know this week than in two
weeks. Nothing in this message is a complaint about the outage; it is me planning around
it.

HOW I WILL VERIFY

/, /signin, /app, /terms, /privacy, /help, /delete-account and the apex / each returning
200 with real HTML rather than a 32-byte text body — then the apps come back on their own
with no rebuild, and I re-take the screenshots the outage has been blocking.
```

## Cautions before you send

- The quota explanation is labelled as a read, not a fact. Only the symptom is
  observable from here; if cto.new knows otherwise, they should say so rather than
  chase that cause.
- The DNS warning is deliberate. The record looks like the obvious thing to fix, and
  touching it converts a 404 into a 522. The follow-up keeps it for the same reason, and
  adds the `sites.cto-new.app` check precisely so "fix the DNS" is not the reply.
- The follow-up asks for a refusal reason. If cto.new answers only "still looking", that
  is the answer: nothing has been published, and the ask in step 1 stands.
- The hour count is anchored to the first photograph, 21 Nov 16:18Z, not to the 16:45Z
  report, so 24 Nov 16:18Z is exactly 72 hours. A later send changes the number in the
  subject and the opening line only — the evidence block is the 16:18Z pass and should
  not be re-dated to match the send.
- If any route answers before you send this, do not send it. Probe first and record the
  restore time against B15 instead; the follow-up exists only for a still-404 check.
- The deadline is anchored to the same observation as the rest of the evidence: 24 Nov
  16:18Z plus 96 hours, so Wed 25 Nov 16:18Z. If you move it, change the date but keep it
  inside the message — a deadline quoted in chat and not in the request is not a deadline.
- If cto.new replies with their own date, their date wins. Record it against B15 in
  `status.md` and hold them to that instead of this one; the point is a date that exists,
  not a date that is mine.
- The source and database ask is not a negotiating position. This repository holds only
  the iOS and Android shells (`capacitor.config.ts` loads the live site in the WebView),
  so if cto.new cannot publish and cannot hand those over, nobody can restore the
  deployment from here. Ask for them in the same message as the publish request, not
  after the deadline passes.

## After it is back

Re-run the ten URLs in the follow-up's evidence block and record the restore time against
B15 in `status.md`; `/delete-account` answering 200 again is also what unblocks G6. Then
re-take the A4 captures — the simulator artifact from run #4 is a photograph of this
outage.
