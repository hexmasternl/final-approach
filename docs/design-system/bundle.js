/* Final Approach world kit. Vanilla custom elements, no dependencies.
   window.FinalApproach = { AIRCRAFT, ICONS, aircraftSvg, iconSvg, runwaySvg, helipadSvg, cloudSvg, windsockSvg, burstSvg }
   Elements: <fa-aircraft> <fa-side-aircraft> <fa-silhouette> <fa-icon> <fa-runway> <fa-helipad> <fa-cloud> <fa-windsock> <fa-burst>
   Paint comes from bundle.css (.fa-p-*), which reads the tokens. */
(function () {
  'use strict';

  /* {{kind}} in a sprite becomes class="fa-p fa-p-kind" here, and literal paint in exported assets. */
  function paint(svg, painter) {
    return svg.replace(/\{\{([a-z-]+)\}\}/g, function (_, k) { return painter(k); });
  }
  function cls(k) { return 'class="fa-p fa-p-' + k + '"'; }

  var AIRCRAFT = {
    'light-prop': {
      label: 'Light prop', kind: 'airplane', landsOn: 'runway', speed: 'Slow', turn: 'High', size: 56,
      svg:
        '<rect {{liv}} x="30" y="72" width="36" height="11" rx="5.5"/>' +
        '<path {{body}} d="M48 13c9 0 12 14 11 32-1 16-5 34-11 41-6-7-10-25-11-41-1-18 2-32 11-32z"/>' +
        '<rect {{liv}} x="6" y="33" width="84" height="17" rx="8.5"/>' +
        '<path {{glass}} d="M39.5 31q8.500-9 17 0v-6q-8.500-8-17 0z"/>' +
        '<path {{shine}} d="M43 25.500l3-2.500"/>' +
        '<g class="fa-prop"><rect {{dark}} x="29" y="6.500" width="38" height="6" rx="3"/></g>' +
        '<circle {{dark}} cx="48" cy="11" r="4.500"/>'
    },
    turboprop: {
      label: 'Turboprop', kind: 'airplane', landsOn: 'runway', speed: 'Medium', turn: 'Medium', size: 68,
      svg:
        '<rect {{liv}} x="31" y="76" width="34" height="10" rx="5"/>' +
        '<rect {{body}} x="3" y="38" width="90" height="15" rx="7.500"/>' +
        '<rect {{shade}} x="19" y="27" width="11" height="24" rx="5.500"/>' +
        '<rect {{shade}} x="66" y="27" width="11" height="24" rx="5.500"/>' +
        '<path {{body}} d="M48 5c7.500 0 9.500 12 9.500 25l-1 35c-1 12-4.500 21-8.500 27-4-6-7.500-15-8.500-27l-1-35c0-13 2-25 9.500-25z"/>' +
        '<rect {{liv}} x="45" y="30" width="6" height="40" rx="3"/>' +
        '<path {{glass}} d="M41 19q7-8.500 14 0v-5.500q-7-7.500-14 0z"/>' +
        '<path {{shine}} d="M44 14.500l2.500-2"/>' +
        '<g class="fa-prop"><rect {{dark}} x="12.500" y="23" width="24" height="5.500" rx="2.750"/></g>' +
        '<g class="fa-prop"><rect {{dark}} x="59.500" y="23" width="24" height="5.500" rx="2.750"/></g>'
    },
    jet: {
      label: 'Jet', kind: 'airplane', landsOn: 'runway', speed: 'Fast', turn: 'Low', size: 72,
      svg:
        '<path {{liv}} d="M48 72l17 13q2 5-3 5l-14-4-14 4q-5 0-3-5z"/>' +
        '<path {{liv}} d="M48 32l39 26q3 7-4 7l-27-7h-16l-27 7q-7 0-4-7z"/>' +
        '<rect {{shade}} x="32" y="60" width="9" height="16" rx="4.500"/>' +
        '<rect {{shade}} x="55" y="60" width="9" height="16" rx="4.500"/>' +
        '<path {{body}} d="M48 3c6.500 0 8.500 12 8.500 25l-1 42c-1 10-4 17-7.500 22-3.500-5-6.500-12-7.500-22l-1-42c0-13 2-25 8.500-25z"/>' +
        '<path {{glass}} d="M41.500 18q6.500-8.500 13 0v-6q-6.500-7.500-13 0z"/>' +
        '<path {{shine}} d="M44.500 13.500l2.500-2"/>'
    },
    jumbo: {
      label: 'Jumbo', kind: 'airplane', landsOn: 'runway', speed: 'Medium-fast', turn: 'Very low', size: 104,
      svg:
        '<path {{liv}} d="M48 74l21 13q2 5.500-3.500 5.500l-17.500-4.500-17.500 4.500q-5.500 0-3.500-5.500z"/>' +
        '<rect {{shade}} x="13" y="46" width="8" height="15" rx="4"/>' +
        '<rect {{shade}} x="26" y="38" width="8" height="15" rx="4"/>' +
        '<rect {{shade}} x="62" y="38" width="8" height="15" rx="4"/>' +
        '<rect {{shade}} x="75" y="46" width="8" height="15" rx="4"/>' +
        '<path {{body}} d="M48 30l44 27q3 7.500-4 7.500l-31-8h-18l-31 8q-7 0-4-7.500z"/>' +
        '<path {{body}} d="M48 2c9.500 0 12 12 12 26l-1 42c-1 11-6 19-11 24-5-5-10-13-11-24l-1-42c0-14 2.500-26 12-26z"/>' +
        '<rect {{liv}} x="44.500" y="26" width="7" height="46" rx="3.500"/>' +
        '<path {{glass}} d="M39.500 17q8.500-9.500 17 0v-6q-8.500-8.500-17 0z"/>' +
        '<path {{shine}} d="M43 12l3-2.500"/>'
    },
    helicopter: {
      label: 'Helicopter', kind: 'helicopter', landsOn: 'helipad', speed: 'Slow', turn: 'Very high', size: 64,
      svg:
        '<path {{line}} d="M31 30v30M65 30v30M31 38h8M65 38h-8M31 52h8M65 52h-8"/>' +
        '<rect {{liv}} x="44.500" y="56" width="7" height="32" rx="3.500"/>' +
        '<rect {{dark}} x="38" y="84" width="20" height="5.500" rx="2.750"/>' +
        '<ellipse {{liv}} cx="48" cy="44" rx="17" ry="21"/>' +
        '<path {{glass}} d="M35 41q-1-15.500 13-15.500t13 15.500q-13-6.500-26 0z"/>' +
        '<path {{shine}} d="M41.500 33.500l3-3"/>' +
        '<circle {{blur}} cx="48" cy="44" r="42"/>' +
        '<g class="fa-rotor"><rect {{dark}} x="7" y="41.500" width="82" height="5" rx="2.500"/><rect {{dark}} x="45.500" y="3" width="5" height="82" rx="2.500"/></g>' +
        '<circle {{dark}} cx="48" cy="44" r="5"/>'
    }
  };

  /* Side-view sprites, nose to the right, on a 96 by 64 grid. Used where the scene is seen
     from the side (the splash page), never in gameplay. */
  var SIDE_AIRCRAFT = {
    'light-prop': {
      label: 'Light prop, side view', width: 96, height: 64,
      svg:
        '<rect {{liv}} x="5" y="27" width="16" height="6" rx="3"/>' +
        '<path {{liv}} d="M13 31L8 10q0-4 5-4l8 2 9 23z"/>' +
        '<path {{line}} d="M40 46v6M60 46v6"/>' +
        '<circle {{dark}} cx="40" cy="54" r="5"/>' +
        '<circle {{dark}} cx="60" cy="54" r="5"/>' +
        '<path {{body}} d="M9 34q0-8 12-8l42-4q22 0 26 12 0 10-18 12H26q-17-2-17-12z"/>' +
        '<rect {{liv}} x="20" y="36" width="44" height="5" rx="2.500"/>' +
        '<path {{glass}} d="M58 25.500h15q7 2 8.500 8.500H60z"/>' +
        '<path {{shine}} d="M64 28.500l-2 3"/>' +
        '<path {{line}} d="M52 22v-5"/>' +
        '<rect {{liv}} x="36" y="10" width="36" height="8" rx="4"/>' +
        '<g class="fa-prop-side"><rect {{dark}} x="88" y="16" width="5" height="36" rx="2.500"/></g>' +
        '<circle {{dark}} cx="89.500" cy="34" r="4.500"/>'
    }
  };

  /* The airport silhouette of the splash page, on a 1000 by 336 grid. The buildings stand on
     the line y = baseY; the ground fills everything below it across the whole screen. */
  var SILHOUETTE = {
    width: 1000, height: 336, baseY: 290,
    shapes: [
      'M30 290V276q0-8 10-8h88q10 0 10 8v14z',
      'M60 290l24-58h16l12 58z',
      'M150 290v-52q75-52 150 0v52z',
      'M330 290v-70h290v70z',
      'M360 220v-16h230v16z',
      'M640 290V100h32v190z',
      'M624 100L614 54h84l-10 46z',
      'M620 54V44h72v10z',
      'M653.500 44V10h5v34z',
      'M740 290v-16q0-8 10-8h140q10 0 10 8v16z',
      'M770 290l20-62h18l16 62z',
      'M914 268a26 26 0 1 0 52 0a26 26 0 1 0-52 0z',
      'M952 276a20 20 0 1 0 40 0a20 20 0 1 0-40 0z'
    ],
    lights: ['M630 92l-7-30h66l-7 30z']
  };

  /* The rest of the sunset scene. bandEnds: where each of the four sky bands ends, as a share
     of the screen height from the top. sun: centre and radius on the silhouette grid, low and
     partly behind the terminal. tower: the control tower, antenna tip to ground, is at least
     minShare and at most maxShare of the screen height; the silhouette scales as a whole. */
  var SUNSET = {
    bandEnds: [0.30, 0.52, 0.72, 1],
    sun: { x: 400, y: 154, r: 88 },
    tower: { topY: 10, minShare: 0.24, maxShare: 0.44 }
  };

  /* 24px icons. s = stroked path data, f = filled markup. */
  var ICONS = {
    play: { f: '<path d="M8.500 5.500v13l10.500-6.500z" stroke-width="3" stroke-linejoin="round"/>' },
    pause: { f: '<rect x="6" y="5" width="4.500" height="14" rx="1.500"/><rect x="13.500" y="5" width="4.500" height="14" rx="1.500"/>' },
    restart: { s: 'M19 12a7 7 0 1 1-2.600-5.400M17.500 2.800v4.700h-4.700' },
    home: { s: 'M3.500 11.500L12 4l8.500 7.500M6 10v9.500h12V10' },
    'sound-on': { f: '<path d="M4 9.500h3.500L12 6v12l-4.500-3.500H4z" stroke-width="2" stroke-linejoin="round"/>', s: 'M15.500 9.500a4 4 0 0 1 0 5M18.200 7a7.500 7.500 0 0 1 0 10' },
    'sound-off': { f: '<path d="M4 9.500h3.500L12 6v12l-4.500-3.500H4z" stroke-width="2" stroke-linejoin="round"/>', s: 'M16 9.500l5 5M21 9.500l-5 5' },
    music: { f: '<circle cx="6.500" cy="18" r="2.800"/><circle cx="16.500" cy="16" r="2.800"/>', s: 'M9.300 18V6.500l10-2V16' },
    settings: { f: '<circle cx="15" cy="7" r="2.800"/><circle cx="9" cy="17" r="2.800"/>', s: 'M3.500 7h8M18.500 7h2M3.500 17h2M12.500 17h8' },
    heart: { f: '<path d="M12 20S4 14.500 4 9a4.200 4.200 0 0 1 8-1.800A4.200 4.200 0 0 1 20 9c0 5.500-8 11-8 11z" stroke-width="2" stroke-linejoin="round"/>' },
    star: { f: '<path d="M12 3.500l2.600 5.400 5.900.8-4.300 4.100 1 5.900L12 16.900l-5.200 2.800 1-5.900-4.300-4.100 5.900-.8z" stroke-width="2" stroke-linejoin="round"/>' },
    bolt: { f: '<path d="M13 2.500L5 13.500h5.500l-1 8 9-12H13z" stroke-width="2" stroke-linejoin="round"/>' },
    lock: { f: '<rect x="5" y="10.500" width="14" height="9.500" rx="2.500"/>', s: 'M8.500 10.500V8a3.500 3.500 0 0 1 7 0v2.500' },
    check: { s: 'M5 12.500l4.500 4.500L19 7.500' },
    close: { s: 'M6 6l12 12M18 6L6 18' },
    'arrow-left': { s: 'M11 5l-7 7 7 7M4.500 12H20' },
    'arrow-right': { s: 'M13 5l7 7-7 7M19.500 12H4' },
    plane: { f: '<path d="M12 2.500c1.200 0 1.800 1.600 1.800 3.500v3l7.200 4.500v2.200l-7.200-2v4l2.200 1.800V21L12 20l-4 1v-1.500l2.200-1.800v-4l-7.200 2v-2.200L10.200 9V6c0-1.900.6-3.500 1.800-3.500z" stroke-width="1.500" stroke-linejoin="round"/>' },
    helicopter: { f: '<ellipse cx="12" cy="11.500" rx="3.800" ry="4.800"/>', s: 'M12 16v5M9.500 21h5M4 7.500l16 8M20 7.500l-16 8' },
    warning: { f: '<circle cx="12" cy="16.300" r="1.300"/>', s: 'M12 4l9 15.500H3zM12 10v3' },
    tap: { f: '<circle cx="12" cy="10" r="2.800"/>', s: 'M7.500 10a4.500 4.500 0 0 1 9 0M4 10a8 8 0 0 1 16 0M12 13v8' },
    wind: { s: 'M3 9h10a3 3 0 1 0-3-3M3 14.500h14a3 3 0 1 1-3 3' }
  };

  function aircraftSvg(type, painter) {
    var a = AIRCRAFT[type] || AIRCRAFT['light-prop'];
    return '<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 96 96" aria-hidden="true">' + paint(a.svg, painter || cls) + '</svg>';
  }

  function sideAircraftSvg(type, painter) {
    var a = SIDE_AIRCRAFT[type] || SIDE_AIRCRAFT['light-prop'];
    return '<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 ' + a.width + ' ' + a.height + '" aria-hidden="true">' + paint(a.svg, painter || cls) + '</svg>';
  }

  function silhouetteSvg() {
    var s = '<rect class="fa-p-silhouette" x="-2000" y="' + (SILHOUETTE.baseY - 1) + '" width="5000" height="' + (SILHOUETTE.height - SILHOUETTE.baseY + 1) + '"/>';
    SILHOUETTE.shapes.forEach(function (d) { s += '<path class="fa-p-silhouette" d="' + d + '"/>'; });
    SILHOUETTE.lights.forEach(function (d) { s += '<path class="fa-p-tower-light" d="' + d + '"/>'; });
    return '<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 ' + SILHOUETTE.width + ' ' + SILHOUETTE.height + '" preserveAspectRatio="xMidYMax meet" aria-hidden="true">' + s + '</svg>';
  }

  function iconSvg(name, color) {
    var i = ICONS[name] || ICONS.close, c = color || 'currentColor';
    return '<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" aria-hidden="true">' +
      (i.f ? '<g fill="' + c + '" stroke="' + c + '" stroke-width="0">' + i.f + '</g>' : '') +
      (i.s ? '<path d="' + i.s + '" fill="none" stroke="' + c + '" stroke-width="3" stroke-linecap="round" stroke-linejoin="round"/>' : '') +
      '</svg>';
  }

  /* Runway: drawn left to right, length x 64. numbers = "09/27". */
  function runwaySvg(length, numbers) {
    var L = Math.max(200, +length || 360), W = 64, n = String(numbers || '09/27').split('/'), s = '';
    s += '<rect class="fa-zone" x="5" y="5" width="' + (L + 6) + '" height="' + (W + 6) + '" rx="13"/>';
    s += '<rect {{asphalt}} x="8" y="8" width="' + L + '" height="' + W + '" rx="10"/>';
    s += '<rect {{tyre}} x="74" y="26" width="46" height="28" rx="8"/><rect {{tyre}} x="' + (L - 104) + '" y="26" width="46" height="28" rx="8"/>';
    for (var e = 0; e < 2; e++) for (var b = 0; b < 4; b++)
      s += '<rect {{mark}} x="' + (e ? L - 8 : 18) + '" y="' + (16 + b * 13) + '" width="6" height="9" rx="2"/>';
    for (var x = 134; x < L - 132; x += 40) s += '<rect {{mark}} x="' + x + '" y="37" width="24" height="6" rx="3"/>';
    s += '<text class="fa-p-num" transform="translate(48 40) rotate(90)" text-anchor="middle" dominant-baseline="central">' + (n[0] || '') + '</text>';
    s += '<text class="fa-p-num" transform="translate(' + (L - 32) + ' 40) rotate(-90)" text-anchor="middle" dominant-baseline="central">' + (n[1] || '') + '</text>';
    return '<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 ' + (L + 16) + ' ' + (W + 16) + '" aria-hidden="true">' + paint(s, cls) + '</svg>';
  }

  function helipadSvg() {
    return '<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 112 112" aria-hidden="true">' + paint(
      '<circle class="fa-zone" cx="56" cy="56" r="51"/>' +
      '<circle {{asphalt}} cx="56" cy="56" r="46"/>' +
      '<circle {{ring}} cx="56" cy="56" r="34"/>' +
      '<path {{h}} d="M45 41v30M67 41v30M45 56h22"/>', cls) + '</svg>';
  }

  /* Blobs: outlined circles first, then the same circles as fill, so only the silhouette keeps a stroke. */
  function blob(circles, edge, fill) {
    var a = '', b = '';
    circles.forEach(function (c) {
      a += '<circle ' + edge + ' cx="' + c[0] + '" cy="' + c[1] + '" r="' + c[2] + '"/>';
      b += '<circle ' + fill + ' cx="' + c[0] + '" cy="' + c[1] + '" r="' + c[2] + '"/>';
    });
    return a + b;
  }

  function cloudSvg() {
    return '<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 132 76" aria-hidden="true">' +
      blob([[30, 46, 18], [54, 32, 24], [84, 38, 20], [104, 50, 14], [66, 52, 18], [46, 52, 14], [88, 53, 13]], cls('edge'), cls('cloud')) +
      '<path ' + cls('cloud-shade') + ' d="M22 58q44 12 90 0" /></svg>';
  }

  function windsockSvg() {
    return '<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 88 72" aria-hidden="true">' + paint(
      '<circle {{dark}} cx="14" cy="60" r="6"/><path {{pole}} d="M14 60V12"/>' +
      '<g class="fa-sock"><path {{sock}} d="M14 6l62 7v12l-62 5z"/>' +
      '<path {{sock-w}} d="M29.500 7.800l15.500 1.700v18.200l-15.500 1.200zM60.500 11.300L76 13v12l-15.500 1.200z"/>' +
      '<path {{sock-o}} d="M14 6l62 7v12l-62 5z"/></g>', cls) + '</svg>';
  }

  function burstSvg(kind) {
    var s;
    if (kind === 'poof') {
      s = blob([[60, 62, 26], [38, 52, 18], [84, 52, 19], [50, 36, 17], [74, 36, 16], [44, 78, 15], [78, 78, 16]], cls('edge'), cls('poof')) +
        '<path ' + cls('poof-shade') + ' d="M42 82q18 10 38 0"/>' +
        '<circle ' + cls('poof-o') + ' cx="16" cy="34" r="7"/><circle ' + cls('poof-o') + ' cx="104" cy="30" r="8"/><circle ' + cls('poof-o') + ' cx="100" cy="98" r="6"/><circle ' + cls('poof-o') + ' cx="18" cy="92" r="5"/>';
    } else {
      var star = 'M0-26l7.600 15.800 17.200 2.300-12.600 12 3 17.200L0 13l-15.200 8.300 3-17.200-12.600-12 17.200-2.300z';
      s = '<path ' + cls('ray') + ' d="M60 8v14M60 98v14M8 60h14M98 60h14M23 23l10 10M87 87l10 10M97 23L87 33M33 87L23 97"/>' +
        '<path ' + cls('star') + ' transform="translate(60 61)" d="' + star + '"/>' +
        '<path ' + cls('star') + ' transform="translate(98 30) scale(.36) rotate(14)" d="' + star + '"/>' +
        '<path ' + cls('star') + ' transform="translate(22 86) scale(.3) rotate(-12)" d="' + star + '"/>';
    }
    return '<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 120 120" aria-hidden="true">' + s + '</svg>';
  }

  var api = {
    AIRCRAFT: AIRCRAFT, SIDE_AIRCRAFT: SIDE_AIRCRAFT, SILHOUETTE: SILHOUETTE, SUNSET: SUNSET, ICONS: ICONS, paint: paint,
    sideAircraftSvg: sideAircraftSvg, silhouetteSvg: silhouetteSvg,
    aircraftSvg: aircraftSvg, iconSvg: iconSvg, runwaySvg: runwaySvg, helipadSvg: helipadSvg,
    cloudSvg: cloudSvg, windsockSvg: windsockSvg, burstSvg: burstSvg
  };
  var root = typeof window !== 'undefined' ? window : globalThis;
  root.FinalApproach = Object.assign(root.FinalApproach || {}, api);
  if (typeof module !== 'undefined' && module.exports) module.exports = api;
  if (typeof customElements === 'undefined' || typeof HTMLElement === 'undefined') return;

  function define(tag, attrs, render) {
    if (customElements.get(tag)) return;
    var C = function () { return Reflect.construct(HTMLElement, [], C); };
    C.prototype = Object.create(HTMLElement.prototype);
    C.prototype.constructor = C;
    C.observedAttributes = attrs;
    C.prototype.connectedCallback = function () { render(this); };
    C.prototype.attributeChangedCallback = function () { if (this.isConnected) render(this); };
    Object.setPrototypeOf(C, HTMLElement);
    customElements.define(tag, C);
  }

  define('fa-aircraft', ['type', 'livery', 'heading', 'selected', 'callsign', 'size'], function (el) {
    var type = el.getAttribute('type') || 'light-prop', a = AIRCRAFT[type] || AIRCRAFT['light-prop'];
    var livery = Math.min(6, Math.max(1, parseInt(el.getAttribute('livery'), 10) || 1));
    var size = parseFloat(el.getAttribute('size')) || a.size;
    el.style.setProperty('--fa-livery', 'var(--path-' + livery + ')');
    el.style.setProperty('--fa-size', size + 'px');
    el.setAttribute('role', 'img');
    el.setAttribute('aria-label', a.label + (el.hasAttribute('selected') ? ', selected' : ''));
    var ring = el.hasAttribute('selected')
      ? '<svg class="fa-aircraft__ring" viewBox="0 0 100 100" aria-hidden="true"><circle class="fa-ring-casing" cx="50" cy="50" r="45"/><circle class="fa-ring-dash" cx="50" cy="50" r="45"/></svg>' : '';
    var tag = el.getAttribute('callsign') ? '<span class="fa-callsign">' + String(el.getAttribute('callsign')).replace(/[<>&"]/g, '') + '</span>' : '';
    el.innerHTML = ring + '<span class="fa-aircraft__sprite" style="transform:rotate(' + (parseFloat(el.getAttribute('heading')) || 0) + 'deg)">' + aircraftSvg(type) + '</span>' + tag;
  });

  define('fa-side-aircraft', ['type', 'livery'], function (el) {
    var livery = Math.min(6, Math.max(1, parseInt(el.getAttribute('livery'), 10) || 1));
    el.style.setProperty('--fa-livery', 'var(--path-' + livery + ')');
    el.innerHTML = sideAircraftSvg(el.getAttribute('type'));
  });
  define('fa-silhouette', [], function (el) { el.innerHTML = silhouetteSvg(); });
  define('fa-icon', ['name'], function (el) { el.innerHTML = iconSvg(el.getAttribute('name')); });
  define('fa-runway', ['length', 'numbers'], function (el) {
    var L = Math.max(200, parseFloat(el.getAttribute('length')) || 360);
    el.style.setProperty('--fa-length', (L + 16) + 'px');
    el.innerHTML = runwaySvg(L, el.getAttribute('numbers'));
  });
  define('fa-helipad', [], function (el) { el.innerHTML = helipadSvg(); });
  define('fa-cloud', [], function (el) { el.innerHTML = cloudSvg(); });
  define('fa-windsock', [], function (el) { el.innerHTML = windsockSvg(); });
  define('fa-burst', ['kind'], function (el) { el.innerHTML = burstSvg(el.getAttribute('kind')); });
})();
