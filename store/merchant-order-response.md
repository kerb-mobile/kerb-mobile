# Merchant orders: Accept / Decline and a ringing alert

Paste-ready change for the retailer portal (`/retailer`). It could not be applied
directly: the site's source lives in the cto.new builder, and this repository has
no site source (the word `retailer` appears only in these notes). So this file is
the patch.

Written against the **deployed** route, read out of `assets/retailer-DIwk2HPg.js`
on 19 Nov 2026:

- The route pulls `{ store, items, mustChangePassword, orders }` from the loader,
  keeps `busy`, a flash message `{tone, text}`, and a helper `p(run, successText)`
  that sets busy, runs the server call, shows `m.error` on failure and calls
  `router.invalidate()` on success.
- Server calls are imported functions called as `fn({ data })` and return
  `{ ok: true }` or `{ ok: false, error }`. Mark-ready is `T({ data: { orderId } })`.
- `items` from the loader is **the store's menu**, not what the customer bought.
  The order card shows a badge, courier, city/state, distance and a mark-ready
  button; it never shows the bag or how long the shop has.
- The card enables mark-ready when
  `readyAt === null && (status === "placed" || status === "offered" || status === "accepted")`.

Nothing below replaces that card. Four insertions: two columns, one server
function, one block inside the existing card, one alarm hook plus one strip above
the list.

---

## 1. Store the decision separately from the order status

Do **not** reuse `status = "accepted"` for the store's answer — in this app
`accepted` is the courier accepting the delivery, and dispatch, the rider's trip
screen and driver earnings read that field. Give the store its own:

```sql
ALTER TABLE orders ADD COLUMN retailer_decision text NOT NULL DEFAULT 'pending';
ALTER TABLE orders ADD COLUMN retailer_decision_at timestamptz;
ALTER TABLE orders ADD COLUMN decline_reason text;
-- Snapshot the prep promise at order time; the store's setting can change later.
ALTER TABLE orders ADD COLUMN prep_minutes integer;
```

`retailer_decision` is `pending` | `accepted` | `declined`. Existing rows become
`pending`, which is right: every open order in the portal today is unanswered.
A decline also sets `status = "cancelled"` — the order is off — and that is when
the refund has to happen (part 6).

---

## 2. Put the bag and the prep clock on the order

One loader change, where the retailer's orders are queried:

```ts
// in the retailer loader, where its orders are selected
const theirOrders = await db.query.orders.findMany({
  where: and(eq(orders.storeId, store.id), ne(orders.status, "delivered")),
  with: { lines: { columns: { name: true, quantity: true } } }, // ADAPT: your line table
  orderBy: desc(orders.createdAt),
});
// return it as `orders`
```

Set `prepMinutes` when the order is created (`order.prepMinutes = store.prepMinutes`),
and read it back with a fallback for orders already in flight:

```ts
const PREP_FALLBACK_MIN = 30;

export function prepMinutes(order: { prepMinutes?: number | null }, store: { prepMinutes?: number | null }) {
  return order.prepMinutes ?? store.prepMinutes ?? PREP_FALLBACK_MIN;
}

/** When the shop promised the bag, from when the order landed. */
export function readyAround(
  order: { prepMinutes?: number | null; placedAt?: Date | string | null; createdAt: Date | string },
  store: { prepMinutes?: number | null },
) {
  const base = new Date(order.placedAt ?? order.createdAt).getTime();
  return new Date(base + prepMinutes(order, store) * 60_000);
}
```

If your line table is named differently, the only requirement is `name` and
`quantity` per line — that is all the card renders.

---

## 3. The server function

Same shape as mark-ready: validate, check the store owns the order, return
`{ ok, error }`.

```ts
// beside the file that exports markOrderReady
import { createServerFn } from "@tanstack/react-start";
import { eq } from "drizzle-orm";
import { z } from "zod";
import { db } from "~/db";                        // ADAPT: your db handle
import { orders } from "~/db/schema";             // ADAPT: your orders table
import { requireRetailerStore } from "./session"; // ADAPT: what markOrderReady uses

const Input = z.object({
  orderId: z.string().min(1),
  decision: z.enum(["accept", "decline"]),
  reason: z.string().max(140).optional(),
});

export const respondToOrder = createServerFn({ method: "POST" })
  .inputValidator(Input)
  .handler(async ({ data }) => {
    const store = await requireRetailerStore();
    if (!store) return { ok: false as const, error: "Sign in as a store to answer orders." };

    const order = await db.query.orders.findFirst({ where: eq(orders.id, data.orderId) });
    if (!order || order.storeId !== store.id) {
      return { ok: false as const, error: "That order isn't yours." };
    }
    if (order.retailerDecision !== "pending") {
      return { ok: false as const, error: "This order has already been answered." };
    }
    if (["picked_up", "delivered", "cancelled"].includes(order.status)) {
      return { ok: false as const, error: "That order is already out of the shop." };
    }

    if (data.decision === "accept") {
      await db
        .update(orders)
        .set({ retailerDecision: "accepted", retailerDecisionAt: new Date() })
        .where(eq(orders.id, order.id));
      return { ok: true as const };
    }

    // Decline: take it off the board, refund it, free the courier.
    await db
      .update(orders)
      .set({
        retailerDecision: "declined",
        retailerDecisionAt: new Date(),
        declineReason: data.reason ?? "The store can't fill this order",
        status: "cancelled",
      })
      .where(eq(orders.id, order.id));

    await refundOrder(order.id);       // ADAPT: same path as a customer cancellation
    await releaseCourierJob(order.id); // ADAPT: cancel/unassign the delivery

    return { ok: true as const };
  });
```

---

## 4. The card: one block inserted under the courier line

Two predicates first — they are the whole behaviour:

```ts
export function isAwaitingResponse(order: {
  readyAt: Date | null; status: string; retailerDecision?: string | null;
}) {
  return (
    (order.retailerDecision ?? "pending") === "pending" &&
    order.readyAt === null &&
    (order.status === "placed" || order.status === "offered" || order.status === "accepted")
  );
}

/** Mark-ready exists only after the shop has said yes. */
export function canMarkReady(order: { readyAt: Date | null; retailerDecision?: string | null }) {
  return order.readyAt === null && order.retailerDecision === "accepted";
}
```

Then this component, dropped **inside the existing `<li>`/card, directly under the
line that prints `Courier: … · city, state · distance`** — existing badge, courier
line, ready-since line and mark-ready button all stay exactly as they are:

```tsx
const DECLINE_REASONS = ["Out of stock", "Too busy right now", "Closing early"];

function WaitingForYou({ order, store, busy, onRespond }: {
  order: Order;                     // now carries lines[] and prepMinutes
  store: Store;                     // for the prep-time fallback
  busy: boolean;
  onRespond: (decision: "accept" | "decline", reason?: string) => void;
}) {
  const [declining, setDeclining] = useState(false);
  const lines = order.lines ?? [];
  const count = lines.reduce((n, l) => n + l.quantity, 0);

  return (
    <div className="mt-1 rounded-xl border border-line p-3">
      <p className="text-sm font-semibold">
        {count} item{count === 1 ? "" : "s"} · prep {prepMinutes(order, store)} min · ready about {C(readyAround(order, store))}
      </p>

      <ul className="mt-2 flex flex-col gap-1 text-sm">
        {lines.length === 0 ? (
          <li className="text-muted">No item list on this order — open it on the customer side.</li>
        ) : (
          lines.map((l, i) => (
            <li key={`${l.name}-${i}`}>
              <span className="font-semibold">{l.quantity} ×</span> {l.name}
            </li>
          ))
        )}
      </ul>

      {declining ? (
        <div className="mt-3 flex flex-wrap items-center gap-2">
          <span className="text-sm">Why can't you fill it?</span>
          {DECLINE_REASONS.map((reason) => (
            <button key={reason} type="button" disabled={busy}
              onClick={() => { setDeclining(false); onRespond("decline", reason); }}
              className="min-h-11 rounded-xl border border-line px-3 text-sm disabled:opacity-50">
              {reason}
            </button>
          ))}
          <button type="button" onClick={() => setDeclining(false)} className="min-h-11 px-3 text-sm underline">
            Keep it
          </button>
        </div>
      ) : (
        <div className="mt-3 flex flex-wrap gap-2">
          <button type="button" disabled={busy} onClick={() => onRespond("accept")}
            className="min-h-12 rounded-xl bg-ink px-5 font-semibold text-white disabled:opacity-50">
            Accept order
          </button>
          <button type="button" disabled={busy} onClick={() => setDeclining(true)}
            className="min-h-12 rounded-xl border border-line px-5 font-semibold disabled:opacity-50">
            Decline
          </button>
        </div>
      )}
    </div>
  );
}
```

`C` is the route's existing time formatter (`C` in the bundle); `Order`, `Store`,
`text-muted` and `bg-ink` are the local types and tokens. Declined orders need no
new markup — the existing badge and status tone already show `cancelled`; add the
reason to the ready-since slot only if you want it:

```tsx
{order.retailerDecision === "declined" && order.declineReason ? (
  <p className="text-sm text-muted">Declined — {order.declineReason}</p>
) : null}
```

The existing mark-ready button's condition changes from
`readyAt === null && (placed|offered|accepted)` to `canMarkReady(order)`; the
button body is untouched.

---

## 5. The ringing alert

### 5a. Hook

Rings while any order is unanswered, stops when none are. Web Audio synthesises
the tone, so there is no sound file to ship.

```tsx
// use-new-order-alarm.ts
import { useCallback, useEffect, useRef, useState } from "react";

const STORAGE_KEY = "kerb.retailer.alarm";
const RING_EVERY_MS = 4000;

/** Two loud square-wave beeps. 0.9 gain is about as loud as a page can be. */
function ring(ctx: AudioContext) {
  const now = ctx.currentTime;
  for (const [i, offset] of [0, 0.3].entries()) {
    const osc = ctx.createOscillator();
    const gain = ctx.createGain();
    osc.type = "square";
    osc.frequency.setValueAtTime(i === 0 ? 880 : 1180, now + offset);
    gain.gain.setValueAtTime(0.0001, now + offset);
    gain.gain.exponentialRampToValueAtTime(0.9, now + offset + 0.02);
    gain.gain.exponentialRampToValueAtTime(0.0001, now + offset + 0.26);
    osc.connect(gain).connect(ctx.destination);
    osc.start(now + offset);
    osc.stop(now + offset + 0.28);
  }
}

export function useNewOrderAlarm(waiting: number) {
  const ctxRef = useRef<AudioContext | null>(null);
  const baseTitle = useRef("");
  const [armed, setArmed] = useState(false);
  const [blocked, setBlocked] = useState(false);

  useEffect(() => {
    baseTitle.current = document.title;
    setArmed(localStorage.getItem(STORAGE_KEY) === "on");
  }, []);

  // Browsers only start audio from a tap, so arming is an explicit button.
  const arm = useCallback(async () => {
    const Ctor =
      window.AudioContext ??
      (window as unknown as { webkitAudioContext: typeof AudioContext }).webkitAudioContext;
    const ctx = ctxRef.current ?? new Ctor();
    ctxRef.current = ctx;
    await ctx.resume();
    setBlocked(ctx.state !== "running");
    setArmed(true);
    localStorage.setItem(STORAGE_KEY, "on");
  }, []);

  const disarm = useCallback(() => {
    setArmed(false);
    localStorage.setItem(STORAGE_KEY, "off");
  }, []);

  useEffect(() => {
    if (!armed || waiting === 0) {
      document.title = baseTitle.current;
      return;
    }

    const fire = () => {
      const ctx = ctxRef.current;
      if (!ctx) return;
      if (ctx.state === "suspended") void ctx.resume().then(() => setBlocked(ctx.state !== "running"));
      ring(ctx);
      navigator.vibrate?.([300, 150, 300]);
    };

    fire(); // ring now, not in 4s
    const ringTimer = window.setInterval(fire, RING_EVERY_MS);

    // A backgrounded tab keeps the title as its only sign of life.
    let lit = false;
    const titleTimer = window.setInterval(() => {
      lit = !lit;
      document.title = lit ? `New order — ${waiting} waiting` : baseTitle.current;
    }, 800);

    // Timers are throttled while hidden; catch up the moment the tab is back.
    const onVisible = () => { if (document.visibilityState === "visible") fire(); };
    document.addEventListener("visibilitychange", onVisible);

    return () => {
      window.clearInterval(ringTimer);
      window.clearInterval(titleTimer);
      document.removeEventListener("visibilitychange", onVisible);
      document.title = baseTitle.current;
    };
  }, [armed, waiting]);

  return { armed, blocked, arm, disarm };
}
```

### 5b. One strip above the existing list

Without a tap nothing can make a sound; this is that tap, and the count.

```tsx
function AlarmBar({ waiting, armed, blocked, onArm }: {
  waiting: number; armed: boolean; blocked: boolean; onArm: () => void;
}) {
  if (waiting > 0 && armed && !blocked) {
    return <p className="mt-3 text-sm font-semibold text-danger">{waiting} waiting for you to accept or decline.</p>;
  }
  if (waiting === 0) {
    return <p className="mt-3 text-sm text-muted">Alarm is {armed ? "on" : "off"} — it rings here while an order waits, as long as this page stays open.</p>;
  }
  return (
    <div className="mt-3 flex flex-wrap items-center gap-3 rounded-xl border border-danger p-4">
      <p className="text-sm font-semibold">The alarm is off, so this page can't call you when an order lands.</p>
      <button type="button" onClick={onArm} className="min-h-11 rounded-xl bg-danger px-4 font-semibold text-white">
        Turn the alarm on
      </button>
    </div>
  );
}
```

### 5c. Wiring

Beside the existing filter that counts open orders:

```ts
const waiting = r.filter(isAwaitingResponse).length;
const alarm = useNewOrderAlarm(waiting);
```

In the Orders tabpanel, above the existing `<OrderList …>` — keep its current
props and add `onRespond`:

```tsx
<>
  <AlarmBar waiting={waiting} armed={alarm.armed} blocked={alarm.blocked} onArm={alarm.arm} />
  <OrderList
    orders={r}
    busy={b}
    onReady={(n) => { /* unchanged */ }}
    onRespond={(id, decision, reason) =>
      p(
        async () => {
          const res = await respondToOrder({ data: { orderId: id, decision, reason } });
          return res.ok ? { ok: true } : { ok: false, error: res.error };
        },
        decision === "accept" ? "Order accepted — start cooking." : "Order declined and refunded.",
      )
    }
  />
</>
```

In `OrderList`'s card, `onRespond` is called with the order id already bound:
`onRespond={(decision, reason) => onRespond(order.id, decision, reason)}`.
`router.invalidate()` inside `p` refetches, so the ring stops by itself once the
last waiting order is answered — no stop call, no stale ring.

---

## 6. What this still does not do

- **It only rings while the page is open.** A closed tab, a locked phone or a
  phone in a pocket gets nothing. Today's useful case is the counter tablet on
  the Orders tab; a phone in someone's pocket needs the native merchant app with
  push.
- **Declining must refund and release the courier.** `refundOrder` and
  `releaseCourierJob` are placeholders and must be wired before this ships, plus
  a customer-facing "The store couldn't fill your order" message, or the rider
  turns up to a cancelled order with no explanation.
- **Fix the 503s first.** On 19 Nov 2026 item and address writes returned
  `503 Upstream unavailable` (geocode recovered on retry). If order writes share
  that path, Accept and Decline will fail the same way.

## 7. How to check it in the builder

1. Sign in as a test store, open Orders, press **Turn the alarm on** — the tone
   plays twice.
2. Place a test order as a rider in another session. It appears within a refresh
   with its items and prep time, the tone repeats every four seconds, and the tab
   title flashes.
3. **Accept** → ring stops, Accepted badge, mark-ready appears, prep line stays.
4. **Decline** with a reason → ring stops, order cancelled and refunded, courier
   released, customer sees why.
5. Reload armed with no orders → silence. Reload with an unanswered order →
   rings again. Answer in one of two tabs → the other stops on its next refetch.
