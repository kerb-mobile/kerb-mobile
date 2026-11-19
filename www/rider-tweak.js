/**
 * Kerb rider home — native presentation patch.
 *
 * Runs only inside the iOS and Android shells, and only on the rider home
 * ("/app"). The website itself is untouched: this script hides a few blocks the
 * native chrome does not need and adds two small affordances that a phone
 * benefits from.
 *
 * What it does
 *   1. Removes the two text overlays that cover the map (the dispatch-area
 *      explanation and the "this phone can't tell us where you are" box) and
 *      replaces the second one with a compact "Location off" chip that taps
 *      through to the site's own Locate me button.
 *   2. Hides the pre-booking "Kerb Ride from $x" card, its "No surge pricing"
 *      line and the "Serving the entire United States." line.
 *   3. Surfaces recent and saved places as chips under the destination field.
 *      Tapping a chip fills the destination field, so the site's own place
 *      picker proposes the match.
 *
 * Everything is derived from text and a handful of stable structural hooks
 * (role="combobox", <details> rows, .mapboxgl-map), never from hashed class
 * names, and every step is idempotent. The site re-renders on its own, so a
 * MutationObserver (plus a slow interval) re-applies what React may have
 * replaced.
 */
(function () {
  'use strict';

  if (window.__kerbRiderPatchInstalled) {
    return;
  }
  window.__kerbRiderPatchInstalled = true;

  var STYLE_ID = 'kerb-rider-patch';

  function isRiderHome() {
    var path = window.location.pathname;
    return path === '/app' || path === '/app/';
  }

  /**
   * Layout-aware text. textContent joins sibling blocks with no separator
   * ("Kerb Ride" + "from $5.94" becomes "Kerb Ridefrom $5.94"), so innerText is
   * the only reliable source for matching copy here.
   */
  function textOf(el) {
    if (!el) {
      return '';
    }
    var raw = '';
    if (typeof el.innerText === 'string' && el.innerText.trim()) {
      raw = el.innerText;
    } else if (typeof el.textContent === 'string') {
      raw = el.textContent;
    }
    return raw.replace(/\s+/g, ' ').trim();
  }

  function hide(el) {
    if (el) {
      el.style.setProperty('display', 'none', 'important');
    }
  }

  function installStyles() {
    if (document.getElementById(STYLE_ID)) {
      return;
    }
    var css = [
      '.kerb-loc-chip{position:absolute;left:12px;top:12px;z-index:5;display:inline-flex;align-items:center;',
      'gap:6px;padding:7px 11px;border-radius:999px;background:rgba(255,255,255,.96);',
      'border:1px solid rgba(11,11,12,.16);box-shadow:0 1px 3px rgba(11,11,12,.14);',
      'font:600 12px/1 -apple-system,system-ui,"Segoe UI",Roboto,sans-serif;color:#0b0b0c;}',
      '.kerb-loc-chip:active{opacity:.65;}',
      '.kerb-loc-dot{width:7px;height:7px;border-radius:50%;background:#c43e02;}',
      '.kerb-chips{display:flex;gap:8px;margin-top:10px;overflow-x:auto;-webkit-overflow-scrolling:touch;}',
      '.kerb-chips::-webkit-scrollbar{display:none;}',
      '.kerb-chip{flex:0 0 auto;max-width:68vw;padding:9px 12px;border-radius:999px;',
      'border:1px solid rgba(11,11,12,.14);background:#fff;color:#0b0b0c;',
      'font:600 13px/1.2 -apple-system,system-ui,"Segoe UI",Roboto,sans-serif;',
      'white-space:nowrap;overflow:hidden;text-overflow:ellipsis;}',
      '.kerb-chip:active{background:#fff0e8;}'
    ].join('');
    var style = document.createElement('style');
    style.id = STYLE_ID;
    style.textContent = css;
    (document.head || document.documentElement).appendChild(style);
  }

  // --- 1. Map overlays -----------------------------------------------------

  function cleanMapOverlays() {
    var map = document.querySelector('.mapboxgl-map');
    if (!map || !map.parentElement) {
      return false;
    }
    var wrap = map.parentElement;
    var removedLocation = false;

    Array.prototype.forEach.call(Array.prototype.slice.call(wrap.children), function (child) {
      if (child === map || child.contains(map)) {
        return;
      }
      var label = textOf(child);
      if (!label) {
        return;
      }
      if (/dashed ring/i.test(label)) {
        hide(child);
      } else if (/can't tell us where you are|map stays on/i.test(label)) {
        hide(child);
        removedLocation = true;
      }
    });

    if (removedLocation) {
      addLocationChip(wrap);
    }
    return removedLocation;
  }

  function addLocationChip(wrap) {
    if (!wrap || wrap.querySelector('.kerb-loc-chip')) {
      return;
    }
    var chip = document.createElement('button');
    chip.type = 'button';
    chip.className = 'kerb-loc-chip';
    chip.setAttribute('aria-label', 'Location is off. Tap to use your location.');
    var dot = document.createElement('span');
    dot.className = 'kerb-loc-dot';
    chip.appendChild(dot);
    chip.appendChild(document.createTextNode('Location off'));
    chip.addEventListener('click', function () {
      var locate = findLocateButton();
      if (locate) {
        locate.click();
      }
    });
    wrap.appendChild(chip);
  }

  /**
   * The site parks "Locate me" in the bottom-right corner, which is exactly where
   * Mapbox renders its attribution toggle, so on a phone the two controls sit on
   * top of each other. Lift ours clear of the attribution row.
   */
  function liftLocateButton() {
    var locate = findLocateButton();
    if (!locate) {
      return;
    }
    var tracker = locate.parentElement;
    if (tracker && /absolute/.test(tracker.className || '') && !tracker.__kerbLifted) {
      tracker.style.setProperty('bottom', '34px', 'important');
      tracker.__kerbLifted = true;
    }
  }

  function findLocateButton() {
    var buttons = document.querySelectorAll('button, a');
    for (var i = 0; i < buttons.length; i++) {
      var label = textOf(buttons[i]);
      if (label === 'Locate me' || /centre the map on my location/i.test(buttons[i].getAttribute('aria-label') || '')) {
        return buttons[i];
      }
    }
    return null;
  }

  // --- 2. Pre-booking marketing -------------------------------------------

  function removeMarketing() {
    var nodes = document.querySelectorAll('div, p');
    for (var i = 0; i < nodes.length; i++) {
      var el = nodes[i];
      if (el.classList && el.classList.contains('kerb-chips')) {
        continue;
      }
      var label = textOf(el);
      if (!label || label.length > 220) {
        continue;
      }
      if (/^Kerb Ride from \$/.test(label) && /No surge pricing/i.test(label)) {
        hide(el);
      } else if (label === 'Serving the entire United States.') {
        hide(el);
      }
    }
  }

  // --- 3. Recent / saved place chips ---------------------------------------

  function findRow(label) {
    var rows = document.querySelectorAll('details');
    for (var i = 0; i < rows.length; i++) {
      var summary = rows[i].querySelector('summary');
      if (summary && textOf(summary).indexOf(label) === 0) {
        return rows[i];
      }
    }
    return null;
  }

  /**
   * A recent-trip row reads "origin → destination" inside one leaf span, with the
   * status badge sitting next to it in the markup (the rows are collapsed, so
   * textContent runs them together). Read the leaf address span only and keep the
   * destination side: that is the place a rider is most likely to want again.
   */
  function placeFromAnchor(anchor) {
    if (!anchor) {
      return null;
    }
    var address = '';
    var spans = anchor.querySelectorAll('span');
    for (var i = 0; i < spans.length; i++) {
      if (spans[i].children.length === 0) {
        var candidate = (spans[i].textContent || '').replace(/\s+/g, ' ').trim();
        if (candidate && (candidate.indexOf('→') >= 0 || candidate.indexOf(',') >= 0)) {
          address = candidate;
          break;
        }
      }
    }
    if (!address) {
      return null;
    }
    if (address.indexOf('→') >= 0) {
      address = address.split('→').pop().replace(/\s+/g, ' ').trim();
    }
    if (!address) {
      return null;
    }
    var short = address.split(',')[0].trim();
    return { label: short || address, address: address };
  }

  function collectPlaces() {
    var places = [];
    var seen = {};

    function add(anchor) {
      var place = placeFromAnchor(anchor);
      if (!place || !place.address || place.address.length < 3 || seen[place.address]) {
        return;
      }
      seen[place.address] = true;
      places.push(place);
    }

    var trips = findRow('Your trips');
    if (trips) {
      var recent = trips.querySelectorAll('a[href*="/app/rides/"]');
      for (var i = 0; i < recent.length && places.length < 2; i++) {
        add(recent[i]);
      }
    }

    var saved = findRow('Saved places');
    if (saved) {
      var savedLinks = saved.querySelectorAll('a[href]');
      for (var j = 0; j < savedLinks.length && places.length < 3; j++) {
        add(savedLinks[j]);
      }
    }

    return places.slice(0, 3);
  }

  function destinationInput() {
    return document.querySelector('input[role="combobox"]')
      || document.querySelector('input[placeholder="Street address or landmark"]');
  }

  function fillDestination(address) {
    var input = destinationInput();
    if (!input) {
      return;
    }
    var setter = Object.getOwnPropertyDescriptor(window.HTMLInputElement.prototype, 'value').set;
    setter.call(input, address);
    input.dispatchEvent(new Event('input', { bubbles: true }));
    input.focus();
  }

  function addPlaceChips() {
    var input = destinationInput();
    if (!input) {
      return;
    }
    var holder = input.closest('.mt-3') || (input.parentElement && input.parentElement.parentElement);
    if (!holder || holder.querySelector('.kerb-chips')) {
      return;
    }
    var places = collectPlaces();
    if (!places.length) {
      return;
    }
    var row = document.createElement('div');
    row.className = 'kerb-chips';
    places.forEach(function (place) {
      var chip = document.createElement('button');
      chip.type = 'button';
      chip.className = 'kerb-chip';
      chip.textContent = place.label;
      chip.setAttribute('aria-label', 'Use ' + place.address + ' as the destination');
      chip.addEventListener('click', function () {
        fillDestination(place.address);
      });
      row.appendChild(chip);
    });
    holder.appendChild(row);
  }

  // --- Run -----------------------------------------------------------------

  function apply() {
    if (!isRiderHome()) {
      return;
    }
    installStyles();
    cleanMapOverlays();
    liftLocateButton();
    removeMarketing();
    addPlaceChips();
  }

  var scheduled = false;
  function schedule() {
    if (scheduled) {
      return;
    }
    scheduled = true;
    window.setTimeout(function () {
      scheduled = false;
      try {
        apply();
      } catch (e) {
        // A patch failure must never break the app.
      }
    }, 250);
  }

  try {
    apply();
  } catch (e) {
    // ignore
  }

  if (window.MutationObserver && document.body) {
    new MutationObserver(schedule).observe(document.body, { childList: true, subtree: true });
  }
  // Slow safety net for re-renders the observer cannot see.
  window.setInterval(function () {
    try {
      apply();
    } catch (e) {
      // ignore
    }
  }, 4000);
})();
