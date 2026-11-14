/* Final Approach world kit. Vanilla custom elements, no dependencies.
   window.FinalApproach = { AIRCRAFT, ICONS, WORLD, aircraftSvg, iconSvg, runwaySvg, helipadSvg, cloudSvg, windsockSvg, burstSvg, worldSvg, sceneSvg }
   Elements: <fa-aircraft> <fa-side-aircraft> <fa-silhouette> <fa-icon> <fa-runway> <fa-helipad> <fa-cloud> <fa-windsock> <fa-burst> <fa-scenery> <fa-scene>
   Aircraft paint comes from bundle.css (.fa-p-*); world paint is written as token variables. Both follow the theme. */
(function () {
  'use strict';

  /* A paint key {{...}} in a sprite becomes a class or a style here, and literal paint in exported assets. */
  function paint(svg, painter) {
    return svg.replace(/\{\{([a-z0-9\/.-]+)\}\}/g, function (_, k) { return painter(k); });
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
    finger: { s: 'M10 13V5.500a2 2 0 0 1 4 0V12M14 12h2.500a3.500 3.500 0 0 1 3.500 3.500V17a4 4 0 0 1-4 4h-4.500a4 4 0 0 1-3.200-1.600L5.500 16.500a1.800 1.800 0 0 1 2.800-2.200L10 16' },
    wind: { s: 'M3 9h10a3 3 0 1 0-3-3M3 14.500h14a3 3 0 1 1-3 3' }
  };

  /* ---------- World (design system v2) ----------
     Everything on the ground follows the world rules W1 to W9 of world.md:
     one sun from the top-left, three flat tones per material, a navy silhouette with tinted
     inner lines, line weight as rank, standing things on a south wall, flat ground without
     an outline, transitions on every edge, detail in clusters, and no aircraft colours.

     A sprite is drawn around its own origin, in world units, with paint keys:
       {{fill}}                 a flat fill in that colour token
       {{fill/stroke/width}}    the same with a stroke in a token and a width in units
       {{-/stroke/width}}       a stroke only
     Every sprite has a height tier (S, M or L), says whether it may rotate, and carries the
     silhouette of its cast shadow (`sh`) with the offset that tier throws it (`off`). Standing
     sprites do not rotate and have their shadow baked in place (`off` is 0, 0). */

  var O = 'outline';
  function k(fill, stroke, width, extra) {
    return '{{' + (fill || '-') + (stroke ? '/' + stroke + '/' + width : '') + '}}' + (extra ? ' ' + extra : '');
  }
  function worldPaint(key) {
    var p = key.split('/');
    return { fill: p[0] === '-' ? null : p[0], stroke: p[1] || null, width: p[2] ? +p[2] : 0 };
  }
  function worldCss(key) {
    var p = worldPaint(key);
    return 'style="fill:' + (p.fill ? 'var(--' + p.fill + ')' : 'none') + (p.stroke ? ';stroke:var(--' + p.stroke + ');stroke-width:' + p.width : '') + '"';
  }
  /* The most main materials a sprite may use (W2). On top of those it may name up to two trim
     materials in `trim`, for small parts such as a chimney or an awning; a sprite that carries
     other things (`carries`: a vessel with containers) is exempt. And what it may never use (W9). */
  var WORLD_MAX_MATERIALS = 3;
  var WORLD_BANNED = ['fuselage', 'fuselage-shade', 'sun', 'sun-500', 'path-1', 'path-2', 'path-3', 'path-4', 'path-5', 'path-6'];
  /* The two accents scenery keeps from v1: the tower cab and the windsock. */
  var WORLD_ACCENT = { tower: 'cherry', windsock: 'tangerine', 'approach-lights': 'cherry' };
  /* Tokens that are not a material of their own: lights, glass, small trims, and the dark of tyres and openings. */
  var WORLD_TRIM = ['outline', 'glass', 'glass-700', 'glass-shine', 'window', 'beacon', 'muzzle', 'foam', 'marking', 'marking-warn', 'shadow-cast', 'asphalt-900', 'mint-500'];
  /* How far each height tier throws its shadow, down-right, in world units. */
  var TIER_SHADOW = { S: [3, 4], M: [12, 14], L: [11, 13] };

  function rnd(seed) {
    var a = seed;
    return function () { a |= 0; a = a + 0x6D2B79F5 | 0; var t = Math.imul(a ^ a >>> 15, 1 | a); t = t + Math.imul(t ^ t >>> 7, 61 | t) ^ t; return ((t ^ t >>> 14) >>> 0) / 4294967296; };
  }
  function r1(v) { return Math.round(v * 10) / 10; }
  /* A closed outline of `n` outward lobes around a circle: canopies and bushes. */
  function lobes(cx, cy, r, n, rng, jit, bulge) {
    jit = jit === undefined ? 0.1 : jit; bulge = bulge || 0.62;
    var pts = [], i;
    for (i = 0; i < n; i++) {
      var a = (i + (rng() - 0.5) * 0.4) / n * Math.PI * 2 - Math.PI / 2, rr = r * (1 + (rng() - 0.5) * 2 * jit);
      pts.push([cx + Math.cos(a) * rr, cy + Math.sin(a) * rr]);
    }
    var d = 'M' + r1(pts[0][0]) + ' ' + r1(pts[0][1]);
    for (i = 1; i <= n; i++) {
      var p = pts[i % n], q = pts[i - 1], ar = r1(Math.hypot(p[0] - q[0], p[1] - q[1]) * bulge);
      d += 'A' + ar + ' ' + ar + ' 0 0 1 ' + r1(p[0]) + ' ' + r1(p[1]);
    }
    return d + 'Z';
  }
  /* A smooth curve through points: ponds, patches, roads. */
  function smooth(pts, closed) {
    if (closed === undefined) closed = true;
    var L = pts.length, m = closed ? L : L - 1, d = 'M' + r1(pts[0][0]) + ' ' + r1(pts[0][1]);
    function g(i) { return closed ? pts[(i + L) % L] : pts[Math.max(0, Math.min(L - 1, i))]; }
    for (var i = 0; i < m; i++) {
      var p0 = g(i - 1), p1 = g(i), p2 = g(i + 1), p3 = g(i + 2);
      d += 'C' + r1(p1[0] + (p2[0] - p0[0]) / 6) + ' ' + r1(p1[1] + (p2[1] - p0[1]) / 6) + ' ' + r1(p2[0] - (p3[0] - p1[0]) / 6) + ' ' + r1(p2[1] - (p3[1] - p1[1]) / 6) + ' ' + r1(p2[0]) + ' ' + r1(p2[1]);
    }
    return d + (closed ? 'Z' : '');
  }
  function organic(cx, cy, rx, ry, n, rng, jit) {
    jit = jit === undefined ? 0.16 : jit;
    var pts = [];
    for (var i = 0; i < n; i++) { var a = i / n * Math.PI * 2, j = 1 + (rng() - 0.5) * 2 * jit; pts.push([cx + Math.cos(a) * rx * j, cy + Math.sin(a) * ry * j]); }
    return pts;
  }
  function star(cx, cy, r, n, inner, rot) {
    var d = '';
    for (var i = 0; i < n * 2; i++) { var a = i / (n * 2) * Math.PI * 2 + (rot || 0), rr = i % 2 ? r * inner : r; d += (i ? 'L' : 'M') + r1(cx + Math.cos(a) * rr) + ' ' + r1(cy + Math.sin(a) * rr); }
    return d + 'Z';
  }
  function oct(cx, cy, r) {
    var pts = [];
    for (var i = 0; i < 8; i++) { var a = (22.5 + 45 * i) * Math.PI / 180; pts.push([r1(cx + Math.cos(a) * r), r1(cy + Math.sin(a) * r)]); }
    return pts;
  }
  function poly(pts) { return pts.map(function (p) { return p.join(','); }).join(' '); }
  /* Cast shadow of a block with footprint x0..x1, y0..y1 thrown by (dx, dy). */
  function blockShadow(x0, y0, x1, y1, dx, dy) {
    return '<polygon points="' + poly([[x1, y0], [x1 + dx, y0 + dy], [x1 + dx, y1 + dy], [x0 + dx, y1 + dy], [x0, y1]]) + '"/>';
  }

  var WORLD = {};
  function sprite(name, o) { WORLD[name] = o; }

  /* --- plants */
  function tree(name, label, r, n, seed) {
    var rng = rnd(seed), C = lobes(0, -4, r, n, rng), H = lobes(-r * 0.3, -4 - r * 0.36, r * 0.42, 5, rng, 0.08);
    sprite(name, {
      label: label, group: 'Plants', tier: 'L', rotates: false, w: r * 2 + 34, h: r * 2 + 34, off: [r1(r * 0.42), r1(r * 0.5)],
      sh: '<ellipse cx="0" cy="-2" rx="' + r + '" ry="' + r1(r * 0.94) + '"/>',
      svg: '<rect ' + k('wood-500', O, 2.2) + ' x="-4" y="' + (r - 12) + '" width="8" height="16" rx="3"/><rect ' + k('wood-700') + ' x="0.5" y="' + (r - 11) + '" width="2.5" height="14" rx="1.2"/>' +
        '<clipPath id="fa-cp-' + name + '"><path d="' + C + '"/></clipPath><path ' + k('leaf-700') + ' d="' + C + '"/>' +
        '<g clip-path="url(#fa-cp-' + name + ')"><path ' + k('leaf-500') + ' d="' + C + '" transform="translate(' + r1(-r * 0.17) + ',' + r1(-r * 0.2) + ')"/><path ' + k('leaf-300') + ' d="' + H + '"/></g>' +
        '<path ' + k('', 'leaf-900', 1.3) + ' d="M' + r1(-r * 0.12) + ' ' + r1(r * 0.1) + 'q' + r1(r * 0.14) + ' ' + r1(-r * 0.2) + ' ' + r1(r * 0.36) + ' ' + r1(-r * 0.04) + 'M' + r1(r * 0.18) + ' ' + r1(-r * 0.44) + 'q' + r1(r * 0.12) + ' ' + r1(-r * 0.16) + ' ' + r1(r * 0.3) + ' ' + r1(-r * 0.02) + 'M' + r1(-r * 0.56) + ' ' + r1(r * 0.02) + 'q' + r1(r * 0.1) + ' ' + r1(-r * 0.16) + ' ' + r1(r * 0.26) + ' ' + r1(-r * 0.03) + '"/>' +
        '<path ' + k('', O, 2.5) + ' d="' + C + '"/>'
    });
  }
  tree('tree', 'Tree', 26, 9, 11);
  tree('tree-small', 'Small tree', 21, 8, 29);
  tree('tree-oak', 'Old oak', 30, 10, 47);

  function pine(name, label, r, seed) {
    var rot = seed * 0.37;
    sprite(name, {
      label: label, group: 'Plants', tier: 'L', rotates: false, w: r * 2 + 36, h: r * 2 + 36, off: [r1(r * 0.5), r1(r * 0.6)],
      sh: '<path d="' + star(0, 0, r, 9, 0.74, rot) + '"/>',
      svg: '<path ' + k('pine-700', O, 2.5) + ' d="' + star(0, 0, r, 9, 0.72, rot) + '"/><path ' + k('pine-500', 'pine-900', 1.2) + ' d="' + star(-r * 0.1, -r * 0.13, r * 0.68, 9, 0.7, rot + 0.3) + '"/><path ' + k('pine-300', 'pine-900', 1.1) + ' d="' + star(-r * 0.19, -r * 0.26, r * 0.36, 7, 0.66, rot + 0.6) + '"/>'
    });
  }
  pine('pine', 'Pine', 23, 1);
  pine('pine-small', 'Small pine', 18, 2);

  (function () {
    var C = lobes(0, 0, 11, 6, rnd(5), 0.12);
    sprite('bush', {
      label: 'Bush', group: 'Plants', tier: 'S', rotates: true, w: 56, h: 56, off: TIER_SHADOW.S, sh: '<circle r="11"/>',
      svg: '<clipPath id="fa-cp-bush"><path d="' + C + '"/></clipPath><path ' + k('leaf-700') + ' d="' + C + '"/><g clip-path="url(#fa-cp-bush)"><path ' + k('leaf-500') + ' d="' + C + '" transform="translate(-2.5,-3)"/><circle ' + k('leaf-300') + ' cx="-4" cy="-5" r="4"/></g><path ' + k('', O, 2.2) + ' d="' + C + '"/>' +
        '<circle ' + k('wall-100') + ' cx="3" cy="-1" r="1.5"/><circle ' + k('wall-100') + ' cx="-2" cy="4" r="1.5"/><circle ' + k('wall-100') + ' cx="6" cy="5" r="1.3"/>'
    });
    /* One piece of a hedge: pieces are placed every 13 units along a line and share one outline. */
    sprite('hedge', {
      label: 'Hedge (one piece of a row)', group: 'Plants', tier: 'S', rotates: true, w: 44, h: 44, off: [4, 5], sh: '<circle r="9.5"/>',
      svg: '<circle ' + k('leaf-700', O, 5) + ' r="9.5"/><circle ' + k('leaf-500') + ' r="9.5"/><circle ' + k('leaf-300') + ' cx="-2" cy="-3" r="5.2"/>'
    });
    /* Acacia: a wide, flat crown on a bare trunk. */
    var A = lobes(0, -8, 30, 11, rnd(61), 0.07, 0.72), AH = lobes(-9, -16, 14, 6, rnd(62), 0.06);
    sprite('acacia', {
      label: 'Acacia', group: 'Plants', tier: 'L', rotates: false, w: 100, h: 96, off: [14, 16],
      sh: '<ellipse cx="0" cy="-6" rx="31" ry="23"/>',
      svg: '<path ' + k('wood-500', O, 2.2) + ' d="M-3 12q-2 12 -1 24h7q1 -12 -1 -24z"/><path ' + k('', 'wood-900', 1.2) + ' d="M0 16q-1 8 0 16"/>' +
        '<g transform="scale(1,0.74)"><clipPath id="fa-cp-acacia"><path d="' + A + '"/></clipPath><path ' + k('leaf-700') + ' d="' + A + '"/><g clip-path="url(#fa-cp-acacia)"><path ' + k('leaf-500') + ' d="' + A + '" transform="translate(-5,-7)"/><path ' + k('leaf-300') + ' d="' + AH + '"/></g>' +
        '<path ' + k('', 'leaf-900', 1.3) + ' d="M-4 -2q5 -6 12 -1M8 -18q4 -5 10 -1M-20 -6q4 -5 9 -1"/><path ' + k('', O, 2.5, 'vector-effect="non-scaling-stroke"') + ' d="' + A + '"/></g>'
    });
    /* Palm: seven fronds around the head of a leaning trunk. */
    var fr = '', frs = '';
    [-80, -30, 15, 65, 115, 165, 215].forEach(function (deg, i) {
      var a = deg * Math.PI / 180, len = 24 + (i % 2) * 3, tx = r1(Math.cos(a) * len), ty = r1(-14 + Math.sin(a) * len * 0.82);
      var mx = r1(Math.cos(a) * len * 0.5 - Math.sin(a) * 7), my = r1(-14 + Math.sin(a) * len * 0.41 + Math.cos(a) * 7);
      var nx = r1(Math.cos(a) * len * 0.5 + Math.sin(a) * 7), ny = r1(-14 + Math.sin(a) * len * 0.41 - Math.cos(a) * 7);
      var d = 'M0 -14Q' + mx + ' ' + my + ' ' + tx + ' ' + ty + 'Q' + nx + ' ' + ny + ' 0 -14Z';
      fr += '<path ' + k(i < 2 || i > 5 ? 'leaf-300' : (i === 3 || i === 4 ? 'leaf-700' : 'leaf-500'), O, 2.2) + ' d="' + d + '"/><path ' + k('', 'leaf-900', 1.1) + ' d="M0 -14L' + r1(tx * 0.86) + ' ' + r1(-14 + (ty + 14) * 0.86) + '"/>';
      frs += '<path d="' + d + '"/>';
    });
    sprite('palm', {
      label: 'Palm', group: 'Plants', tier: 'L', rotates: false, w: 96, h: 96, off: [13, 15], sh: frs,
      svg: '<path ' + k('wood-500', O, 2.2) + ' d="M-3 -12q-4 14 1 30h6q-4 -16 0 -30z"/><path ' + k('', 'wood-900', 1.1) + ' d="M-2 -4h5M-2 3h5.500M-1 10h5.500"/>' + fr + '<circle ' + k('wood-700', O, 1.6) + ' cx="-3" cy="-12" r="2.6"/><circle ' + k('wood-700', O, 1.6) + ' cx="3" cy="-11" r="2.6"/>'
    });
  })();

  /* --- small things */
  sprite('rock', {
    label: 'Rock', group: 'Ground', tier: 'S', rotates: true, w: 64, h: 56, off: TIER_SHADOW.S,
    sh: '<polygon points="-13,2 -9,-9 3,-12 12,-5 14,6 5,11 -7,10"/><circle cx="14" cy="9" r="5"/>',
    svg: '<polygon ' + k('stone-500', O, 2.2) + ' points="-13,2 -9,-9 3,-12 12,-5 14,6 5,11 -7,10"/><polygon ' + k('stone-300') + ' points="-9,-9 3,-12 5,-3 -6,-1"/><polygon ' + k('stone-700') + ' points="5,-3 12,-5 14,6 5,11 3,3"/><polygon ' + k('', O, 2.2) + ' points="-13,2 -9,-9 3,-12 12,-5 14,6 5,11 -7,10"/><path ' + k('', 'stone-700', 1.2) + ' d="M-6 -1l-3 6"/><circle ' + k('stone-300', O, 2) + ' cx="14" cy="9" r="4.5"/>'
  });
  sprite('hay-bale', {
    label: 'Hay bale', group: 'Farm', tier: 'S', rotates: true, w: 48, h: 48, off: TIER_SHADOW.S, sh: '<circle r="9"/>',
    svg: '<clipPath id="fa-cp-hay"><circle r="9"/></clipPath><circle ' + k('straw-500') + ' r="9"/><g clip-path="url(#fa-cp-hay)"><circle ' + k('straw-300') + ' cx="-2" cy="-2.5" r="9"/></g><path ' + k('', 'straw-700', 1.2) + ' d="M0.500 0.500a2 2 0 1 1 -2.500 -2a4.500 4.500 0 1 1 5 6a6.500 6.500 0 0 1 -9 -5"/><circle ' + k('', O, 2) + ' r="9"/>'
  });
  /* One piece of a fence: pieces repeat every 16 units along a line. */
  sprite('fence', {
    label: 'Fence (one piece of a row)', group: 'Farm', tier: 'S', rotates: true, w: 40, h: 40, off: [2.5, 3.5],
    sh: '<path ' + k('', 'shadow-cast', 3) + ' d="M-8 0h16"/>',
    svg: '<path ' + k('', O, 4.6) + ' d="M-8 0h16"/><path ' + k('', 'wood-300', 2) + ' d="M-8 0h16"/><circle ' + k('wood-300', O, 1.6) + ' cx="-8" cy="0" r="3"/><circle ' + k('wood-300', O, 1.6) + ' cx="8" cy="0" r="3"/>'
  });

  /* --- farm buildings */
  sprite('farmhouse', {
    label: 'Farmhouse', group: 'Farm', tier: 'M', rotates: false, trim: ['stone'], w: 150, h: 126, off: [0, 0], sh: blockShadow(-46, -38, 46, 30, 12, 14),
    svg: '<rect ' + k('stone-300', 'stone-700', 1.2) + ' x="-11" y="30" width="22" height="5" rx="1.5"/>' +
      '<rect ' + k('wall-100', O, 2.5) + ' x="-42" y="10" width="84" height="20"/><rect ' + k('wall-300') + ' x="28" y="11.5" width="12.5" height="17"/><rect ' + k('wall-500') + ' x="-40.7" y="14" width="81.4" height="3.5"/>' +
      '<rect ' + k('wood-500', 'wood-900', 1.3) + ' x="-6" y="17" width="12" height="13" rx="2"/><circle ' + k('beacon') + ' cx="3" cy="24" r="1"/>' +
      [-36, -21, 18, 33].map(function (x) { return '<rect ' + k('wood-700', 'wood-900', 1) + ' x="' + x + '" y="17" width="3" height="9"/>'; }).join('') +
      [-33, 21].map(function (x) { return '<rect ' + k('window', 'wood-900', 1.2) + ' x="' + x + '" y="17" width="12" height="9" rx="1"/><path ' + k('', 'wood-900', 1) + ' d="M' + (x + 6) + ' 17v9M' + x + ' 21.500h12"/>'; }).join('') +
      '<rect ' + k('wood-700') + ' x="-34" y="26.500" width="14" height="2.600" rx="1"/><circle ' + k('muzzle') + ' cx="-31" cy="26" r="1.300"/><circle ' + k('wall-100') + ' cx="-27" cy="25.600" r="1.300"/><circle ' + k('muzzle') + ' cx="-23" cy="26" r="1.300"/>' +
      '<rect ' + k('roof-500') + ' x="-46" y="-38" width="92" height="52" rx="3"/><path ' + k('roof-300') + ' d="M-46 -12v-23a3 3 0 0 1 3-3h86a3 3 0 0 1 3 3v23z"/>' +
      '<path ' + k('', 'roof-500', 1.2) + ' d="M-46 -30h92M-46 -21h92"/><path ' + k('', 'roof-700', 1.2) + ' d="M-46 -3h92M-46 6h92"/>' +
      '<path ' + k('', 'roof-500', 1) + ' d="M-34 -38v8M-12 -38v8M10 -38v8M32 -38v8M-23 -30v9M-1 -30v9M21 -30v9M-34 -21v9M-12 -21v9M10 -21v9M32 -21v9"/><path ' + k('', 'roof-700', 1) + ' d="M-23 -12v9M-1 -12v9M21 -12v9M-34 -3v9M-12 -3v9M10 -3v9M32 -3v9M-23 6v8M-1 6v8M21 6v8"/>' +
      '<path ' + k('', 'roof-900', 1.8) + ' d="M-46 -12h92"/>' +
      '<path ' + k('roof-700') + ' d="M30 -24l9 10h-11l-9 -10z"/><rect ' + k('stone-300', O, 2) + ' x="19" y="-35" width="11" height="11" rx="1.500"/><rect ' + k('asphalt-900') + ' x="22" y="-32" width="5" height="5" rx="1"/>' +
      '<rect ' + k('', O, 2.5) + ' x="-46" y="-38" width="92" height="52" rx="3"/><rect ' + k('roof-700', O, 2) + ' x="-12" y="9" width="24" height="7" rx="2"/>'
  });
  sprite('barn', {
    label: 'Barn', group: 'Farm', tier: 'M', rotates: false, w: 138, h: 126, off: [0, 0], sh: blockShadow(-40, -44, 40, 30, 14, 16),
    svg: '<rect ' + k('roof-500', O, 2.5) + ' x="-36" y="8" width="72" height="22"/><rect ' + k('roof-700') + ' x="23" y="9.500" width="11.500" height="19"/><rect ' + k('roof-700') + ' x="-34.700" y="12" width="69.400" height="3"/>' +
      '<rect ' + k('wall-100', 'roof-900', 1.3) + ' x="-13" y="14" width="26" height="16" rx="1"/><path ' + k('', 'roof-700', 1.3) + ' d="M0 14v16M-13 14l13 16M0 14l-13 16M0 14l13 16M13 14l-13 16"/>' +
      '<clipPath id="fa-cp-barn"><rect x="-40" y="-44" width="80" height="56" rx="3"/></clipPath>' +
      '<g clip-path="url(#fa-cp-barn)"><rect ' + k('tin-500') + ' x="-40" y="-44" width="80" height="13"/><rect ' + k('tin-100') + ' x="-40" y="-31" width="80" height="17"/><rect ' + k('tin-300') + ' x="-40" y="-14" width="80" height="16"/><rect ' + k('tin-700') + ' x="-40" y="2" width="80" height="10"/>' +
      '<path ' + k('', 'tin-700', 1, 'opacity="0.55"') + ' d="M-28 -44v56M-16 -44v56M-4 -44v56M8 -44v56M20 -44v56M32 -44v56"/></g>' +
      '<path ' + k('', 'tin-700', 1.2) + ' d="M-40 -31h80M-40 2h80"/><path ' + k('', 'tin-900', 1.8) + ' d="M-40 -14h80"/><rect ' + k('', O, 2.5) + ' x="-40" y="-44" width="80" height="56" rx="3"/>'
  });
  sprite('silo', {
    label: 'Silo', group: 'Farm', tier: 'L', rotates: false, w: 76, h: 76, off: [13, 15], sh: '<circle r="14"/>',
    svg: '<clipPath id="fa-cp-silo"><circle r="14"/></clipPath><circle ' + k('tin-700') + ' r="14"/><g clip-path="url(#fa-cp-silo)"><circle ' + k('tin-300') + ' cx="-3" cy="-3.500" r="14"/><circle ' + k('tin-100') + ' cx="-6" cy="-7" r="9"/></g><path ' + k('', 'tin-700', 0.9, 'opacity="0.6"') + ' d="M0 -14v28M-14 0h28M-10 -10l20 20M10 -10l-20 20"/><circle ' + k('', O, 2.5) + ' r="14"/><circle ' + k('tin-500', 'tin-900', 1.3) + ' r="4.500"/>'
  });

  /* --- airfield buildings */
  (function () {
    var o1 = oct(0, -30, 22), o2 = oct(0, -37, 22), o3 = oct(0, -37, 14);
    var mull = o2.map(function (p, i) { return 'M' + o3[i][0] + ' ' + o3[i][1] + 'L' + p[0] + ' ' + p[1]; }).join('');
    sprite('tower', {
      label: 'Control tower', group: 'Airfield', tier: 'L', rotates: false, w: 132, h: 150, off: [0, 0],
      sh: blockShadow(-24, -6, 24, 42, 12, 14) + '<polygon points="' + poly(oct(30, 0, 22)) + '"/><polygon points="9,-30 39,0 21,18 -9,-12"/>',
      svg: '<rect ' + k('wall-100', O, 2.5) + ' x="-22" y="28" width="44" height="14"/><rect ' + k('wall-300') + ' x="10" y="29.500" width="10.500" height="11"/><rect ' + k('wood-500', 'wood-900', 1.3) + ' x="-5" y="31" width="10" height="11" rx="1.500"/><rect ' + k('window', 'wood-900', 1.1) + ' x="-17" y="32" width="7" height="5" rx="1"/>' +
        '<rect ' + k('tin-300', O, 2.5) + ' x="-24" y="-6" width="48" height="36" rx="3"/><rect ' + k('tin-500') + ' x="12" y="-4.700" width="10.700" height="33.400"/><rect ' + k('tin-500', 'tin-900', 1.2) + ' x="-18" y="14" width="9" height="9" rx="1.500"/><circle ' + k('tin-700') + ' cx="-13.500" cy="18.500" r="2.500"/>' +
        '<rect ' + k('wall-100', O, 2.5) + ' x="-9" y="-28" width="18" height="34"/><rect ' + k('wall-300') + ' x="2" y="-26.700" width="5.700" height="31.400"/><rect ' + k('window', 'wood-900', 1) + ' x="-5.500" y="-10" width="4" height="6" rx="1"/>' +
        '<polygon ' + k('glass-700', O, 2.5) + ' points="' + poly(o1) + '"/><polygon ' + k('glass', O, 2.5) + ' points="' + poly(o2) + '"/><path ' + k('', 'glass-700', 1.3) + ' d="' + mull + '"/><path ' + k('', 'glass-shine', 2.2) + ' d="M-17 -43l5 -8"/>' +
        '<polygon ' + k('cherry-500', O, 2.2) + ' points="' + poly(o3) + '"/><polygon ' + k('cherry-200') + ' points="' + poly([o3[4], o3[5], o3[6], [0, -37]]) + '"/><polygon ' + k('', O, 2.2) + ' points="' + poly(o3) + '"/>' +
        '<path ' + k('', O, 2) + ' d="M0 -37l8 -14"/><circle ' + k(O) + ' cx="8" cy="-51" r="1.800"/><circle ' + k('beacon', O, 1.5) + ' cx="0" cy="-37" r="3.400"/>'
    });
  })();
  sprite('terminal', {
    label: 'Terminal', group: 'Airfield', tier: 'M', rotates: false, trim: ['roof', 'wall'], w: 206, h: 126, off: [0, 0], sh: blockShadow(-76, -34, 76, 36, 12, 14),
    svg: '<rect ' + k('concrete-300', 'concrete-700', 1.2) + ' x="-82" y="32" width="164" height="10" rx="3"/>' +
      '<rect ' + k('window', O, 2.5) + ' x="-72" y="12" width="144" height="22"/><path ' + k('', 'glass-700', 1.3) + ' d="M-60 12v22M-48 12v22M-36 12v22M-24 12v22M24 12v22M36 12v22M48 12v22M60 12v22"/><path ' + k('', 'glass-700', 1.1) + ' d="M-70 23h52M18 23h52"/><path ' + k('', 'glass-shine', 1.8, 'opacity="0.7"') + ' d="M-56 30l8 -12M-51 30l8 -12M40 30l8 -12"/>' +
      '<rect ' + k('tin-700') + ' x="-70.700" y="30" width="141.400" height="3"/><rect ' + k('glass-700', O, 1.8) + ' x="-9" y="20" width="18" height="14" rx="1"/><path ' + k('', O, 1.3) + ' d="M0 20v14"/>' +
      '<rect ' + k('roof-500', O, 2) + ' x="-17" y="13" width="34" height="8" rx="2"/><path ' + k('', 'wall-100', 2.2) + ' d="M-10 13v8M-3 13v8M4 13v8M11 13v8"/><rect ' + k('', O, 2) + ' x="-17" y="13" width="34" height="8" rx="2"/>' +
      '<rect ' + k('tin-300') + ' x="-76" y="-34" width="152" height="48" rx="4"/><rect ' + k('tin-500') + ' x="60" y="-34" width="16" height="48"/><rect ' + k('tin-500') + ' x="-76" y="5" width="152" height="9"/><path ' + k('', 'tin-500', 1.2) + ' d="M-38 -34v39M0 -34v39M38 -34v39"/>' +
      '<rect ' + k('', O, 2.5) + ' x="-76" y="-34" width="152" height="48" rx="4"/>' +
      '<rect ' + k('glass', O, 2) + ' x="-52" y="-27" width="104" height="13" rx="6.500"/><path ' + k('', 'glass-700', 1.2) + ' d="M-39 -27v13M-26 -27v13M-13 -27v13M0 -27v13M13 -27v13M26 -27v13M39 -27v13"/><path ' + k('', 'glass-shine', 1.8) + ' d="M-46 -17l5 -7"/>' +
      '<rect ' + k('tin-500', 'tin-900', 1.2) + ' x="-68" y="-8" width="12" height="10" rx="2"/><circle ' + k('tin-700') + ' cx="-62" cy="-3" r="3"/><rect ' + k('tin-700', 'tin-900', 1.2) + ' x="52" y="-9" width="12" height="10" rx="2"/>' +
      '<rect ' + k(O) + ' x="-27" y="-8" width="54" height="10" rx="3"/><path ' + k('', 'wall-100', 2.4) + ' d="M-21 -3h5M-13 -3h3M-7 -3h6M2 -3h4M9 -3h5M17 -3h4"/>'
  });
  sprite('hangar', {
    label: 'Hangar', group: 'Airfield', tier: 'M', rotates: false, w: 166, h: 140, off: [0, 0], sh: blockShadow(-56, -40, 56, 44, 13, 15),
    svg: '<rect ' + k('tin-500', O, 2.5) + ' x="-54" y="22" width="108" height="22"/><rect ' + k('tin-700') + ' x="38" y="23.500" width="14.500" height="19"/>' +
      '<rect ' + k('asphalt-900') + ' x="-34" y="27" width="68" height="17"/><path ' + k('tin-100', O, 1.6) + ' d="M-3 44C-3 33 15 33 15 44Z"/><path ' + k('', 'glass', 2.2) + ' d="M2 39q4 -3.500 8 0"/>' +
      '<rect ' + k('tin-300', 'tin-900', 1.2) + ' x="-34" y="27" width="27" height="17"/><path ' + k('', 'tin-700', 1) + ' d="M-27 27v17M-20 27v17M-13 27v17"/><rect ' + k('', O, 1.8) + ' x="-34" y="27" width="68" height="17"/>' +
      '<clipPath id="fa-cp-hangar"><rect x="-56" y="-40" width="112" height="66" rx="10"/></clipPath>' +
      '<g clip-path="url(#fa-cp-hangar)"><rect ' + k('tin-300') + ' x="-56" y="-40" width="112" height="66"/><rect ' + k('tin-100') + ' x="-56" y="-40" width="26" height="66"/><rect ' + k('tin-500') + ' x="24" y="-40" width="32" height="66"/><rect ' + k('tin-700', '', 0, 'opacity="0.5"') + ' x="-56" y="17" width="112" height="9"/>' +
      '<path ' + k('', 'tin-700', 1.1, 'opacity="0.7"') + ' d="M-56 -29h112M-56 -18h112M-56 -7h112M-56 4h112M-56 15h112"/></g>' +
      '<rect ' + k('', O, 2.5) + ' x="-56" y="-40" width="112" height="66" rx="10"/><circle ' + k('tin-500', 'tin-900', 1.2) + ' cx="-4" cy="-24" r="3.500"/><circle ' + k('tin-500', 'tin-900', 1.2) + ' cx="-4" cy="2" r="3.500"/>'
  });
  sprite('windsock', {
    label: 'Windsock', group: 'Airfield', tier: 'L', rotates: false, w: 84, h: 76, off: [9, 11],
    sh: '<path ' + k('', 'shadow-cast', 3) + ' d="M0 0v-26"/><polygon points="0,-31 34,-27.500 34,-22.500 0,-21" transform="rotate(14 0 -26)"/>',
    svg: '<circle ' + k('concrete-300', O, 1.8) + ' r="4.500"/><path ' + k('', O, 3.2) + ' d="M0 0v-27"/><path ' + k('', 'tin-300', 1) + ' d="M-0.500 -2v-22"/>' +
      '<g transform="rotate(14 0 -26)"><clipPath id="fa-cp-sock"><polygon points="0,-31.500 34,-28 34,-23 0,-20.500"/></clipPath><polygon ' + k('tangerine-500') + ' points="0,-31.500 34,-28 34,-23 0,-20.500"/>' +
      '<g clip-path="url(#fa-cp-sock)"><rect ' + k('foam') + ' x="7" y="-33" width="7" height="14"/><rect ' + k('foam') + ' x="21" y="-33" width="7" height="14"/><rect ' + k('tangerine-900', '', 0, 'opacity="0.2"') + ' x="0" y="-25" width="34" height="6"/></g>' +
      '<polygon ' + k('', O, 2) + ' points="0,-31.500 34,-28 34,-23 0,-20.500"/></g><circle ' + k('tin-300', O, 1.4) + ' cx="0" cy="-26" r="2.200"/>'
  });

  /* --- vehicles: strictly top-down, front up, so they can drive in any direction */
  function wheels(xs, ys) {
    var s = '';
    xs.forEach(function (x) { ys.forEach(function (y) { s += '<rect ' + k('asphalt-900') + ' x="' + x + '" y="' + y[0] + '" width="5" height="' + y[1] + '" rx="2"/>'; }); });
    return s;
  }
  sprite('car', {
    label: 'Car', group: 'Vehicles', tier: 'S', rotates: true, w: 56, h: 72, off: TIER_SHADOW.S, sh: '<rect x="-10" y="-19" width="20" height="38" rx="7"/>',
    svg: wheels([-12.5, 7.5], [[-13, 8], [6, 8]]) +
      '<rect ' + k('denim-500', O, 2.2) + ' x="-10" y="-19" width="20" height="38" rx="7"/><rect ' + k(O, '', 0, 'opacity="0.16"') + ' x="4.500" y="-15" width="4" height="30" rx="2"/>' +
      '<path ' + k('glass', 'glass-700', 1.1) + ' d="M-7 -7q7 -4 14 0l-1.500 5h-11z"/><path ' + k('glass', 'glass-700', 1.1) + ' d="M-6.500 9h13l1 4q-7.500 3 -15 0z"/><rect ' + k('denim-300') + ' x="-6.500" y="-2" width="13" height="11" rx="3"/>' +
      '<circle ' + k('beacon') + ' cx="-5.500" cy="-16.500" r="1.600"/><circle ' + k('beacon') + ' cx="5.500" cy="-16.500" r="1.600"/><path ' + k('', 'glass-shine', 1.4, 'opacity="0.8"') + ' d="M-4 -12.500h3"/>'
  });
  sprite('fuel-truck', {
    label: 'Fuel truck', group: 'Vehicles', tier: 'S', rotates: true, w: 60, h: 92, off: [4, 5], sh: '<rect x="-11" y="-28" width="22" height="56" rx="6"/>',
    svg: wheels([-13, 8], [[-22, 8], [10, 9]]) +
      '<clipPath id="fa-cp-tank"><rect x="-11" y="-11" width="22" height="39" rx="10"/></clipPath><rect ' + k('tin-300') + ' x="-11" y="-11" width="22" height="39" rx="10"/>' +
      '<g clip-path="url(#fa-cp-tank)"><rect ' + k('tin-100') + ' x="-11" y="-11" width="7" height="39"/><rect ' + k('tin-500') + ' x="5" y="-11" width="6" height="39"/><path ' + k('', 'tin-700', 1.1) + ' d="M-11 2h22M-11 15h22"/></g>' +
      '<rect ' + k('', O, 2.2) + ' x="-11" y="-11" width="22" height="39" rx="10"/>' + [-4, 9, 21].map(function (y) { return '<circle ' + k('tin-500', 'tin-900', 1.1) + ' cx="0" cy="' + y + '" r="2.600"/>'; }).join('') +
      '<rect ' + k('roof-500', O, 2.2) + ' x="-10.500" y="-28" width="21" height="15" rx="4"/><path ' + k('glass', 'glass-700', 1.1) + ' d="M-7 -24q7 -3 14 0l-1 4h-12z"/><rect ' + k('roof-300') + ' x="-6" y="-19" width="12" height="4.500" rx="1.500"/>'
  });
  sprite('baggage-cart', {
    label: 'Baggage cart', group: 'Vehicles', tier: 'S', rotates: true, w: 52, h: 96, off: TIER_SHADOW.S,
    sh: '<rect x="-8" y="-34" width="16" height="16" rx="4"/><rect x="-8" y="-12" width="16" height="20" rx="2"/><rect x="-8" y="14" width="16" height="20" rx="2"/>',
    svg: '<path ' + k('', O, 2) + ' d="M0 -18v32"/>' + wheels([-10.5, 5.5], [[-32, 6], [-9, 6], [17, 6]]) +
      '<rect ' + k('roof-500', O, 2.2) + ' x="-8" y="-34" width="16" height="16" rx="4"/><rect ' + k('glass', 'glass-700', 1.1) + ' x="-5" y="-31" width="10" height="5" rx="1.500"/><rect ' + k('roof-300') + ' x="-5" y="-24" width="10" height="4" rx="1"/>' +
      [-12, 14].map(function (y, i) {
        return '<rect ' + k('tin-300', O, 2.2) + ' x="-8" y="' + y + '" width="16" height="20" rx="2"/><rect ' + k('tin-500') + ' x="3.500" y="' + (y + 1.2) + '" width="3.300" height="17.600"/>' +
          '<rect ' + k(i ? 'wood-500' : 'wood-300', 'wood-900', 1.1) + ' x="-5.500" y="' + (y + 2.5) + '" width="7" height="6" rx="1"/><rect ' + k(i ? 'wood-300' : 'wood-700', 'wood-900', 1.1) + ' x="-4" y="' + (y + 10.5) + '" width="8" height="6.500" rx="1"/>';
      }).join('')
  });
  /* A parked aircraft: one generic light prop in metal tones, with no livery and no cockpit shine. */
  sprite('parked-aircraft', {
    label: 'Parked aircraft', group: 'Airfield', tier: 'S', rotates: true, w: 92, h: 88, off: [4, 5], ground: 'apron',
    sh: '<rect x="-30" y="-12" width="60" height="13" rx="6"/><path d="M0 -29c5 0 6 10 6 20l-2 32h-8l-2 -32c0 -10 1 -20 6 -20z"/><rect x="-12" y="22" width="24" height="8" rx="4"/>',
    svg: '<rect ' + k('tin-500', O, 2.2) + ' x="-12" y="22" width="24" height="8" rx="4"/><rect ' + k('tin-500', O, 2.2) + ' x="-30" y="-12" width="60" height="13" rx="6"/><rect ' + k('tin-300') + ' x="-28" y="-10.500" width="26" height="4"/>' +
      '<path ' + k('tin-100', O, 2.2) + ' d="M0 -29c5 0 6 10 6 20l-2 32h-8l-2 -32c0 -10 1 -20 6 -20z"/><path ' + k('tin-500') + ' d="M2.500 -27c2 3 2.300 11 2.300 18l-1.800 30h-0.500z"/>' +
      '<path ' + k('glass', 'glass-700', 1.1) + ' d="M-4.500 -17q4.500 -5 9 0v6h-9z"/><path ' + k('', O, 2.4) + ' d="M-9 -30h18"/>'
  });

  /* --- animals and people: strictly top-down, front up */
  function animalBody(id, d, coat, flank, marks, line) {
    return '<clipPath id="fa-cp-' + id + '"><path d="' + d + '"/></clipPath><path ' + k(coat, O, line || 2.2) + ' d="' + d + '"/><g clip-path="url(#fa-cp-' + id + ')">' + flank + marks + '</g><path ' + k('', O, line || 2.2) + ' d="' + d + '"/>';
  }
  (function () {
    var body = 'M-10 -8C-12 -17 12 -17 10 -8L11 10C11 21 -11 21 -11 10Z';
    function ears(c) { return '<ellipse ' + k(c, O, 1.8) + ' cx="-8.500" cy="-19" rx="4" ry="2.400" transform="rotate(-18 -8.500 -19)"/><ellipse ' + k(c, O, 1.8) + ' cx="8.500" cy="-19" rx="4" ry="2.400" transform="rotate(18 8.500 -19)"/>'; }
    var eyes = '<circle ' + k(O) + ' cx="-3" cy="-20.500" r="1"/><circle ' + k(O) + ' cx="3.200" cy="-20.500" r="1"/>';
    var shadow = '<ellipse cx="0" cy="2" rx="11.500" ry="19"/><ellipse cx="0" cy="-19" rx="7" ry="8"/>';
    sprite('cow', {
      label: 'Cow', group: 'Animals', tier: 'S', rotates: true, w: 64, h: 86, off: [4, 5], sh: shadow,
      svg: '<path ' + k('', O, 2) + ' d="M0 17q-1.500 6 3 9"/><circle ' + k('hide-900') + ' cx="3.400" cy="26.500" r="2.300"/>' +
        animalBody('cow', body, 'hide-100', '<rect ' + k('hide-300') + ' x="5.500" y="-16" width="7" height="38"/>',
          '<path ' + k('hide-900') + ' d="M-11 -3q6 -6 9 0q1 6 -4 7q-6 0 -5 -7z"/><path ' + k('hide-900') + ' d="M1 7q7 -3 9 4q-1 7 -7 6q-5 -3 -2 -10z"/><path ' + k('hide-900') + ' d="M4 -13q5 -1 7 3l-1 3q-5 0 -6 -6z"/>') +
        ears('hide-300') + '<ellipse ' + k('hide-100', O, 2.2) + ' cx="0" cy="-19" rx="6.800" ry="8"/><path ' + k('hide-900') + ' d="M0.500 -20q5 -3.500 5.500 1.500q-2 3.500 -5.500 1.500z"/>' +
        '<ellipse ' + k('muzzle', 'hide-900', 1.1) + ' cx="0" cy="-25" rx="4.600" ry="3"/><circle ' + k('hide-900') + ' cx="-1.600" cy="-25.300" r="0.700"/><circle ' + k('hide-900') + ' cx="1.600" cy="-25.300" r="0.700"/>' + eyes
    });
    var stripes = '';
    for (var y = -13; y <= 17; y += 5) stripes += '<path ' + k('', 'hide-900', 2.4) + ' d="M-12 ' + y + 'q6 ' + (y < 2 ? 3 : -3) + ' 12 0t12 0"/>';
    sprite('zebra', {
      label: 'Zebra', group: 'Animals', tier: 'S', rotates: true, w: 64, h: 90, off: [4, 5], sh: shadow,
      svg: '<path ' + k('', O, 2) + ' d="M0 17q1 6 -2 10"/><circle ' + k('hide-900') + ' cx="-2.200" cy="27.500" r="2.200"/>' +
        animalBody('zebra', body, 'hide-100', '<rect ' + k('hide-300') + ' x="5.500" y="-16" width="7" height="38"/>', stripes) +
        ears('hide-100') + '<path ' + k('hide-100', O, 2.2) + ' d="M-6 -17q0 -9 6 -14q6 5 6 14q0 5 -6 5t-6 -5z"/><path ' + k('', 'hide-900', 2) + ' d="M-5 -19h10M-4 -23.500h8"/><path ' + k('hide-900', O, 1.4) + ' d="M-3.200 -29.500q3.200 -4 6.400 0q-3.200 3 -6.400 0z"/><path ' + k('', 'hide-900', 2.6) + ' d="M0 -15v8"/>' +
        '<circle ' + k(O) + ' cx="-3.800" cy="-21.500" r="0.900"/><circle ' + k(O) + ' cx="3.800" cy="-21.500" r="0.900"/>'
    });
    var eb = 'M-17 -12C-20 -28 20 -28 17 -12L18 14C18 32 -18 32 -18 14Z';
    sprite('elephant', {
      label: 'Elephant', group: 'Animals', tier: 'S', rotates: true, w: 104, h: 132, off: [6, 7],
      sh: '<ellipse cx="0" cy="4" rx="19" ry="28"/><ellipse cx="-19" cy="-24" rx="13" ry="15"/><ellipse cx="19" cy="-24" rx="13" ry="15"/><ellipse cx="0" cy="-30" rx="11" ry="13"/>',
      svg: '<path ' + k('', O, 2.2) + ' d="M0 28q2 7 -2 12"/><circle ' + k('stone-700') + ' cx="-2.500" cy="41" r="2.400"/>' +
        animalBody('elephant', eb, 'stone-500', '<rect ' + k('stone-700') + ' x="9" y="-26" width="12" height="60"/><ellipse ' + k('stone-300') + ' cx="-8" cy="-6" rx="7" ry="13"/>', '<path ' + k('', 'stone-700', 1.2) + ' d="M-10 14q5 3 10 0M-4 22q5 3 10 0"/>', 2.5) +
        '<path ' + k('stone-500', O, 2.5) + ' d="M-9 -22C-24 -44 -36 -24 -30 -12C-26 -4 -14 -8 -9 -14Z"/><path ' + k('stone-500', O, 2.5) + ' d="M9 -22C24 -44 36 -24 30 -12C26 -4 14 -8 9 -14Z"/>' +
        '<path ' + k('stone-300') + ' d="M-12 -24C-22 -36 -30 -24 -27 -15C-22 -12 -15 -15 -12 -18Z"/><path ' + k('stone-700') + ' d="M14 -22C22 -34 30 -24 27 -15C22 -12 17 -15 14 -18Z"/>' +
        '<path ' + k('stone-500', O, 2.5) + ' d="M-10 -28q0 -14 10 -14t10 14q0 7 -5 9l-1.500 -14h-7l-1.500 14q-5 -2 -5 -9z"/><path ' + k('stone-500', O, 2.5) + ' d="M-3.500 -36q-1 -12 1.500 -19q2 -3 4 0q2.500 7 1.500 19z"/><path ' + k('', 'stone-700', 1.2) + ' d="M-2 -42h4M-1.500 -47h3.500M-1 -52h2.500"/>' +
        '<path ' + k('wall-100', O, 1.6) + ' d="M-7 -38q-3 -7 0 -12l2.500 1q-1.500 5 0.500 10z"/><path ' + k('wall-100', O, 1.6) + ' d="M7 -38q3 -7 0 -12l-2.500 1q1.500 5 -0.500 10z"/><circle ' + k(O) + ' cx="-5.500" cy="-29" r="1.100"/><circle ' + k(O) + ' cx="5.500" cy="-29" r="1.100"/>'
    });
    sprite('farmer', {
      label: 'Farmer', group: 'Farm', tier: 'S', rotates: true, w: 60, h: 64, off: TIER_SHADOW.S,
      sh: '<ellipse cx="0" cy="2" rx="12" ry="7"/><circle cx="0" cy="-1" r="8.500"/>',
      svg: '<path ' + k('', O, 4.4) + ' d="M17 -16v26"/><path ' + k('', 'wood-300', 2) + ' d="M17 -16v26"/><path ' + k('', O, 1.6) + ' d="M13.500 -22v6h7v-6M17 -22v6"/>' +
        '<ellipse ' + k('pine-500', O, 2.2) + ' cx="0" cy="2" rx="12" ry="6.500"/><path ' + k('pine-700') + ' d="M5 -3.500q6 1.500 6 5.500q0 3 -6 4.200z"/><circle ' + k('muzzle', O, 1.6) + ' cx="-12" cy="-1" r="2.800"/><circle ' + k('muzzle', O, 1.6) + ' cx="15" cy="-3" r="2.800"/>' +
        '<circle ' + k('straw-500', O, 2.2) + ' cx="0" cy="-1" r="8.500"/><circle ' + k('straw-300') + ' cx="-1.500" cy="-2.500" r="6"/><circle ' + k('straw-300', 'straw-700', 1.2) + ' cx="0" cy="-1" r="4"/>'
    });
    function bird(name, label, wing) {
      sprite(name, {
        label: label, group: 'Animals', tier: 'S', rotates: true, w: 48, h: 40, off: [7, 9],
        sh: '<path d="' + wing + '"/><ellipse cx="0" cy="0" rx="3.200" ry="6.500"/>',
        svg: '<path ' + k('hide-300', O, 1.8) + ' d="' + wing + '"/><path ' + k('hide-100', O, 1.8) + ' d="M-3 3q-1 -9 3 -11q4 2 3 11l-3 5z"/><path ' + k('straw-500', O, 1.2) + ' d="M-1.300 -8h2.600l-1.300 -3.200z"/><circle ' + k(O) + ' cx="-1.200" cy="-5.500" r="0.600"/><circle ' + k(O) + ' cx="1.200" cy="-5.500" r="0.600"/>'
      });
    }
    bird('bird-up', 'Bird, wings out', 'M0 -2q-8 -6 -15 -2q4 1 6 5q5 -1 9 2q4 -3 9 -2q2 -4 6 -5q-7 -4 -15 2z');
    bird('bird-down', 'Bird, wings in', 'M0 -2q-6 -1 -9 5q3 0 5 3q2 -3 4 -2q2 -1 4 2q2 -3 5 -3q-3 -6 -9 -5z');
  })();

  /* --- on the water */
  (function () {
    var hull = 'M0 -30C8 -22 10 -8 10 6V24q0 4 -4 4H-6q-4 0 -4 -4V6C-10 -8 -8 -22 0 -30Z';
    sprite('sailboat', {
      label: 'Sailboat', group: 'Water', tier: 'S', rotates: true, w: 60, h: 92, off: TIER_SHADOW.S, ground: 'water', sh: '<path d="' + hull + '"/>',
      svg: '<clipPath id="fa-cp-sail"><path d="' + hull + '"/></clipPath><path ' + k('wall-100', O, 2.2) + ' d="' + hull + '"/><g clip-path="url(#fa-cp-sail)"><rect ' + k('wall-300') + ' x="4" y="-32" width="8" height="62"/></g><path ' + k('', O, 2.2) + ' d="' + hull + '"/>' +
        '<path ' + k('wood-300', 'wood-900', 1.2) + ' d="M0 -22C5 -16 6.500 -6 6.500 5V21h-13V5C-6.500 -6 -5 -16 0 -22Z"/><path ' + k('', 'wood-700', 1) + ' d="M-6.500 2h13M-6.500 10h13M0 -20v40"/>' +
        '<path ' + k('wall-100', O, 1.8) + ' d="M0 -14q12 8 3 30z"/><path ' + k('wall-300') + ' d="M1.500 -9q8 8 2.500 20z"/><circle ' + k('wood-700', O, 1.4) + ' cx="0" cy="-14" r="2"/>'
    });
    var yh = 'M0 -40C11 -30 14 -12 14 6V30q0 6 -6 6H-8q-6 0 -6 -6V6C-14 -12 -11 -30 0 -40Z';
    sprite('yacht', {
      label: 'Yacht', group: 'Water', tier: 'S', rotates: true, w: 72, h: 110, off: [4, 5], ground: 'water', sh: '<path d="' + yh + '"/>',
      svg: '<clipPath id="fa-cp-yacht"><path d="' + yh + '"/></clipPath><path ' + k('wall-100', O, 2.5) + ' d="' + yh + '"/><g clip-path="url(#fa-cp-yacht)"><rect ' + k('wall-300') + ' x="7" y="-42" width="9" height="80"/><rect ' + k('wood-300') + ' x="-14" y="22" width="28" height="16"/><path ' + k('', 'wood-700', 1) + ' d="M-14 27h28M-14 32h28"/></g><path ' + k('', O, 2.5) + ' d="' + yh + '"/>' +
        '<rect ' + k('tin-300', O, 2) + ' x="-9" y="-12" width="18" height="30" rx="5"/><rect ' + k('tin-500') + ' x="4" y="-10" width="3.500" height="26" rx="1.500"/><path ' + k('glass', 'glass-700', 1.1) + ' d="M-6.500 -9q6.500 -4 13 0v5h-13z"/><rect ' + k('glass', 'glass-700', 1.1) + ' x="-6.500" y="8" width="13" height="6" rx="1.500"/><path ' + k('', 'glass-shine', 1.6) + ' d="M-4 -6l3 -2.500"/>'
    });
    var ch = 'M-22 -70q22 -16 44 0V62q0 8 -8 8H-14q-8 0 -8 -8Z', boxes = '';
    [['roof-500', 'roof-300', 'roof-700'], ['tin-500', 'tin-300', 'tin-700'], ['denim-500', 'denim-300', 'water-700'], ['tin-500', 'tin-100', 'tin-700'], ['roof-500', 'roof-300', 'roof-700'], ['denim-500', 'denim-300', 'water-700']].forEach(function (c, i) {
      var y = -44 + Math.floor(i / 2) * 26, x = i % 2 ? 1.5 : -17.5;
      boxes += '<rect ' + k(c[0], O, 1.8) + ' x="' + x + '" y="' + y + '" width="16" height="22" rx="1.500"/><rect ' + k(c[1]) + ' x="' + (x + 1.5) + '" y="' + (y + 1.5) + '" width="5" height="19"/><path ' + k('', c[2], 1) + ' d="M' + (x + 8) + ' ' + (y + 1) + 'v20M' + (x + 12) + ' ' + (y + 1) + 'v20"/>';
    });
    sprite('container-vessel', {
      label: 'Container vessel', group: 'Water', tier: 'S', rotates: true, carries: true, w: 96, h: 176, off: [6, 7], ground: 'water', sh: '<path d="' + ch + '"/>',
      svg: '<clipPath id="fa-cp-vessel"><path d="' + ch + '"/></clipPath><path ' + k('asphalt-500', O, 2.5) + ' d="' + ch + '"/><g clip-path="url(#fa-cp-vessel)"><rect ' + k('asphalt-700') + ' x="12" y="-80" width="12" height="152"/><path ' + k('asphalt-300') + ' d="M-22 -70q22 -16 44 0v6q-22 -14 -44 0z"/></g><path ' + k('', O, 2.5) + ' d="' + ch + '"/>' +
        boxes + '<rect ' + k('wall-100', O, 2.2) + ' x="-16" y="38" width="32" height="22" rx="3"/><rect ' + k('wall-300') + ' x="8" y="39.500" width="6.500" height="19"/><rect ' + k('glass', 'glass-700', 1.1) + ' x="-12" y="41" width="24" height="5" rx="1.500"/><rect ' + k('roof-500', O, 1.6) + ' x="-5" y="50" width="10" height="7" rx="1.500"/>'
    });
    sprite('jetty', {
      label: 'Jetty', group: 'Water', tier: 'S', rotates: true, w: 60, h: 120, off: TIER_SHADOW.S, ground: 'water', sh: '<rect x="-9" y="-44" width="18" height="88" rx="2"/>',
      svg: '<clipPath id="fa-cp-jetty"><rect x="-9" y="-44" width="18" height="88" rx="2"/></clipPath><rect ' + k('wood-500', O, 2.2) + ' x="-9" y="-44" width="18" height="88" rx="2"/>' +
        '<g clip-path="url(#fa-cp-jetty)"><rect ' + k('wood-300') + ' x="-9" y="-44" width="7" height="88"/><rect ' + k('wood-700') + ' x="5" y="-44" width="4" height="88"/><path ' + k('', 'wood-900', 1.1) + ' d="M-9 -33h18M-9 -22h18M-9 -11h18M-9 0h18M-9 11h18M-9 22h18M-9 33h18"/></g><rect ' + k('', O, 2.2) + ' x="-9" y="-44" width="18" height="88" rx="2"/>' +
        [[-9, -40], [9, -40], [-9, -8], [9, -8], [-9, 24], [9, 24]].map(function (p) { return '<circle ' + k('wood-700', O, 1.6) + ' cx="' + p[0] + '" cy="' + p[1] + '" r="3"/>'; }).join('')
    });
    /* Flat strokes on water: no outline, no shadow (W6). */
    sprite('wave', { label: 'Wave', group: 'Water', tier: 'flat', rotates: true, w: 48, h: 24, off: [0, 0], ground: 'water', sh: '', svg: '<path ' + k('', 'foam', 2) + ' d="M-14 2q7 -6 14 0t14 0"/>' });
    sprite('wake', { label: 'Wake', group: 'Water', tier: 'flat', rotates: true, w: 56, h: 64, off: [0, 0], ground: 'water', sh: '', svg: '<path ' + k('', 'foam', 2) + ' d="M0 -22q-10 16 -14 40M0 -22q10 16 14 40"/><path ' + k('', 'foam', 1.5, 'opacity="0.7"') + ' d="M0 -10q-4 10 -5 22M0 -10q4 10 5 22"/>' });
  })();

  /* --- crops: flat ground with rows, edged in their own darker tone (W6) */
  function crop(name, label, base, row, edge, rowWidth) {
    var rows = '';
    for (var y = -19; y <= 19; y += 9) rows += 'M-32 ' + y + 'H32';
    sprite(name, {
      label: label, group: 'Farm', tier: 'flat', rotates: true, w: 84, h: 68, off: [0, 0], sh: '',
      svg: '<rect ' + k('grass-700') + ' x="-36" y="-28" width="72" height="56" rx="12"/><clipPath id="fa-cp-' + name + '"><rect x="-32" y="-24" width="64" height="48" rx="9"/></clipPath><rect ' + k(base) + ' x="-32" y="-24" width="64" height="48" rx="9"/>' +
        '<g clip-path="url(#fa-cp-' + name + ')"><path ' + k('', row, rowWidth) + ' d="' + rows + '"/></g><rect ' + k('', edge, 2) + ' x="-32" y="-24" width="64" height="48" rx="9"/>'
    });
  }
  crop('crop-field', 'Crop field, green (one tile)', 'soil-500', 'leaf-300', 'soil-700', 4);
  crop('crop-wheat', 'Crop field, wheat (one tile)', 'straw-300', 'straw-500', 'straw-700', 4);
  crop('crop-plough', 'Crop field, ploughed (one tile)', 'soil-500', 'soil-700', 'soil-700', 2.2);

  /* --- small flat details, drawn straight on the ground without an outline or a shadow */
  var DETAILS = {
    tuft: '<path ' + k('', 'grass-700', 1.7) + ' d="M0 0l-2.500 -6M0 0l0.300 -8.500M0 0l3 -6"/>',
    'tuft-light': '<path ' + k('', 'grass-300', 1.7) + ' d="M0 0l-2.500 -6M0 0l0.300 -8.500M0 0l3 -6"/>',
    flower: '<circle ' + k('wall-100') + ' r="2.300"/><circle ' + k('straw-500') + ' r="0.900"/>',
    'flower-pink': '<circle ' + k('muzzle') + ' r="2.300"/><circle ' + k('wall-100') + ' r="0.900"/>',
    reed: '<path ' + k('', 'leaf-700', 1.6) + ' d="M0 0l-2 -11M0 0l1 -13M0 0l4 -10"/><path ' + k('', 'wood-700', 2.6) + ' d="M-2 -11l-0.400 -3M1 -13l0.200 -3.500M4 -10l0.600 -3"/>',
    lily: '<path ' + k('leaf-500', 'leaf-900', 1) + ' d="M0 0L6 -2A6.300 6.300 0 1 1 5 -4Z"/><circle ' + k('muzzle') + ' cx="-1.500" cy="1.500" r="1.800"/>',
    duck: '<path ' + k('', 'foam', 1.5) + ' d="M-9 6q9 5 18 0M-6 11q6 3 12 0"/><ellipse ' + k('wall-100', 'wall-500', 1.5) + ' cx="0" cy="1" rx="4.200" ry="6"/><circle ' + k('pine-500', 'pine-900', 1.5) + ' cx="0" cy="-5" r="3"/><path ' + k('straw-500', 'straw-700', 1) + ' d="M-1.300 -8.500h2.600l-1.300 -2.600z"/>'
  };

  /* A sprite on its own: shadow first, then the sprite, on a square that fits both. */
  function worldSvg(name, painter, opts) {
    var a = WORLD[name] || WORLD.tree, p = painter || worldCss, o = opts || {};
    var shadow = a.sh ? '<g ' + (o.shadowAttr || 'style="fill:var(--shadow-cast);opacity:var(--opacity-shadow-cast)"') + ' transform="translate(' + a.off[0] + ',' + a.off[1] + ')">' + paint(a.sh, p) + '</g>' : '';
    return '<svg xmlns="http://www.w3.org/2000/svg" viewBox="' + (-a.w / 2) + ' ' + (-a.h / 2) + ' ' + a.w + ' ' + a.h + '" stroke-linejoin="round" stroke-linecap="round" aria-hidden="true">' + shadow + paint(a.svg, p) + '</svg>';
  }

  /* ---------- Landing zones (v2) ----------
     A runway is drawn around its own centre, along the x axis, 64 units wide: the same asphalt
     footprint as v1. Around it lie a concrete shoulder and a fringe of rough grass (W7). It is,
     with the helipad, the only ground shape stroked in outline (W6), at line-zone.
     numbers = "09/27": the left end and the right end. One-way: only the right end is marked,
     and the centre line is chevrons pointing the landing way (to the left). */
  function runwayMarkup(length, numbers, oneWay, zone) {
    var L = Math.max(240, +length || 760), h = L / 2, n = String(numbers || '09/27').split('/'), s = '', x, i;
    /* A short runway keeps its threshold, number and lights and drops the marks it has no room for. */
    var full = L >= 460, inset = full ? 230 : 80;
    s += '<rect ' + k('grass-700') + ' x="' + (-h - 24) + '" y="-54" width="' + (L + 48) + '" height="108" rx="30"/>';
    s += '<rect ' + k('concrete-500', 'concrete-700', 1.6) + ' x="' + (-h - 10) + '" y="-41" width="' + (L + 20) + '" height="82" rx="12"/>';
    if (zone) s += '<rect class="fa-zone' + (zone === 'no' ? ' fa-zone--no' : '') + '" x="' + (-h - 14) + '" y="-45" width="' + (L + 28) + '" height="90" rx="16"/>';
    s += '<rect ' + k('asphalt-500', O, 3) + ' x="' + (-h) + '" y="-32" width="' + L + '" height="64" rx="6"/>';
    [-1, 1].forEach(function (sgn) {
      if (oneWay && sgn > 0) return;
      var e = -h;
      s += '<g transform="scale(' + sgn + ',1)">';
      if (full) s += '<path ' + k('', 'asphalt-700', 3) + ' d="M' + (e + 74) + ' -10h96M' + (e + 82) + ' -5h118M' + (e + 78) + ' 6h104M' + (e + 88) + ' 11h70"/>';
      for (i = 0; i < 6; i++) s += '<rect ' + k('marking') + ' x="' + (e + 8) + '" y="' + (-25 + i * 9) + '" width="22" height="5" rx="1"/>';
      if (full) s += '<rect ' + k('marking') + ' x="' + (e + 118) + '" y="-21" width="40" height="11" rx="1.500"/><rect ' + k('marking') + ' x="' + (e + 118) + '" y="10" width="40" height="11" rx="1.500"/>';
      if (L >= 560) s += '<path ' + k('', 'marking', 3, 'stroke-linecap="butt"') + ' d="M' + (e + 184) + ' -19h20M' + (e + 184) + ' -13h20M' + (e + 184) + ' 13h20M' + (e + 184) + ' 19h20"/>';
      [-19, -7, 7, 19].forEach(function (y) { s += '<circle ' + k('mint-500', 'concrete-700', 1) + ' cx="' + (e - 5) + '" cy="' + y + '" r="2.300"/>'; });
      s += '</g>';
    });
    s += '<path ' + k('', 'marking', 1.6) + ' d="M' + (-h + 8) + ' -27.500h' + (L - 16) + 'M' + (-h + 8) + ' 27.500h' + (L - 16) + '"/>';
    if (oneWay) for (x = -h + 60; x < h - inset - 6; x += 40) s += '<path ' + k('', 'marking', 3.2) + ' d="M' + (x + 12) + ' -9l-12 9l12 9"/>';
    else s += '<path ' + k('', 'marking', 3.2, 'stroke-dasharray="26 20" stroke-linecap="butt"') + ' d="M' + (-h + inset) + ' 0h' + (L - 2 * inset) + '"/>';
    if (!oneWay) s += '<text class="fa-p-num" transform="translate(' + (-h + 58) + ',0) rotate(90)" text-anchor="middle" dominant-baseline="central">' + (n[0] || '') + '</text>';
    s += '<text class="fa-p-num" transform="translate(' + (h - 58) + ',0) rotate(-90)" text-anchor="middle" dominant-baseline="central">' + (n[1] || '') + '</text>';
    for (x = -Math.floor((h - 32) / 58) * 58; x <= h - 32; x += 58) s += '<circle ' + k('beacon', 'concrete-700', 1) + ' cx="' + x + '" cy="-36.500" r="2.500"/><circle ' + k('beacon', 'concrete-700', 1) + ' cx="' + x + '" cy="36.500" r="2.500"/>';
    /* The approach lights beside the landing end: two red, two white. */
    var px = oneWay ? h - (full ? 112 : 50) - 36 : -h + (full ? 112 : 50);
    for (i = 0; i < 4; i++) s += '<rect ' + k((i < 2) !== !!oneWay ? 'cherry-500' : 'foam', O, 1) + ' x="' + (px + i * 9) + '" y="-52" width="6.500" height="5.500" rx="1"/>';
    return s;
  }
  function runwaySvg(length, numbers, oneWay, zone) {
    var L = Math.max(240, +length || 760);
    return '<svg xmlns="http://www.w3.org/2000/svg" viewBox="' + (-L / 2 - 28) + ' -58 ' + (L + 56) + ' 116" stroke-linejoin="round" stroke-linecap="round" aria-hidden="true">' + paint(runwayMarkup(L, numbers, oneWay, zone), worldCss) + '</svg>';
  }
  /* A helipad: 112 units across, as in v1, on a concrete pad with slab joints and corner lights. */
  function helipadMarkup(zone) {
    var s = '<rect ' + k('grass-700') + ' x="-76" y="-76" width="152" height="152" rx="22"/><rect ' + k('concrete-300', 'concrete-700', 2) + ' x="-69" y="-69" width="138" height="138" rx="16"/><path ' + k('', 'concrete-500', 1.2) + ' d="M-69 0h138M0 -69v138"/>';
    if (zone) s += '<circle class="fa-zone' + (zone === 'no' ? ' fa-zone--no' : '') + '" r="60"/>';
    s += '<circle ' + k('asphalt-500', O, 3) + ' r="56"/><circle ' + k('', 'asphalt-700', 5, 'opacity="0.5"') + ' r="47"/><circle ' + k('', 'marking-warn', 5) + ' r="39"/><path ' + k('', 'marking', 7.5) + ' d="M-13 -18V18M13 -18V18M-13 0H13"/>';
    [[-60, -60], [60, -60], [-60, 60], [60, 60]].forEach(function (p) { s += '<circle ' + k('beacon', 'concrete-700', 1.2) + ' cx="' + p[0] + '" cy="' + p[1] + '" r="3"/>'; });
    return s;
  }
  function helipadSvg(zone) {
    return '<svg xmlns="http://www.w3.org/2000/svg" viewBox="-80 -80 160 160" stroke-linejoin="round" stroke-linecap="round" aria-hidden="true">' + paint(helipadMarkup(zone), worldCss) + '</svg>';
  }

  /* A cloud is airborne: outlined at line-zone, three tones, and it keeps the soft shadow. */
  var CLOUD = 'M-58 20a22 22 0 0 1 4 -43a34 34 0 0 1 62 -12a26 26 0 0 1 44 16a20 20 0 0 1 4 39z';
  function cloudMarkup() {
    return '<clipPath id="fa-cp-cloud"><path d="' + CLOUD + '"/></clipPath><path ' + k('cloud') + ' d="' + CLOUD + '"/>' +
      '<g clip-path="url(#fa-cp-cloud)"><path ' + k('cloud-shade') + ' d="M-90 8q30 10 62 2q30 -8 56 2q24 8 50 -2v30h-168z"/><path ' + k('', 'glass-shine', 5) + ' d="M-30 -34q14 -14 30 -8"/></g>' +
      '<path ' + k('', O, 3) + ' d="' + CLOUD + '"/><path ' + k('', 'cloud-shade', 2.2) + ' d="M-36 -8q8 -8 18 -4M14 -18q8 -6 16 -1"/>';
  }
  function cloudSvg() {
    return '<svg xmlns="http://www.w3.org/2000/svg" viewBox="-86 -58 172 86" stroke-linejoin="round" stroke-linecap="round" aria-hidden="true">' + paint(cloudMarkup(), worldCss) + '</svg>';
  }
  function windsockSvg() { return worldSvg('windsock'); }

  /* ---------- Scenes ----------
     A scene composes ground pieces, landing zones, scenery and aircraft into one level-sized
     picture, in the fixed layer order: flat ground, one layer of hard shadows, standing scenery
     from north to south, zone glows and paths, airborne shadows, aircraft, clouds.
     Scenes are reference compositions for the showcase and the level cards; the game draws its
     levels from level data. */
  function Scene(seed, width, height) {
    var rng = rnd(seed), W = width || 1920, H = height || 1080;
    var ground = [], shadows = [], standing = [], air = [], airShadow = [], over = [], clouds = [], blocks = [], used = {}, ids = 0;
    var frame = { cx: W / 2, cy: H / 2, th: 0, deg: 0 }, local = [], fieldPts = null;
    var S = {};
    S.R = function (a, b) { return a + rng() * (b - a); };
    /* The runway frame: hard surfaces are laid out in it, with the runway on its x axis. */
    S.frame = function (cx, cy, deg) { S.flush(); frame = { cx: cx, cy: cy, deg: deg, th: deg * Math.PI / 180 }; return S; };
    S.at = function (x, y) { return [r1(frame.cx + x * Math.cos(frame.th) - y * Math.sin(frame.th)), r1(frame.cy + x * Math.sin(frame.th) + y * Math.cos(frame.th))]; };
    S.flush = function () { if (local.length) ground.push('<g transform="translate(' + frame.cx + ',' + frame.cy + ') rotate(' + frame.deg + ')">' + local.join('') + '</g>'); local = []; };
    S.block = function (x, y, r) { blocks.push([x, y, r]); };
    function inPoly(pts, x, y) { var ins = false; for (var i = 0, j = pts.length - 1; i < pts.length; j = i++) { var a = pts[i], b = pts[j]; if ((a[1] > y) !== (b[1] > y) && x < (b[0] - a[0]) * (y - a[1]) / (b[1] - a[1]) + a[0]) ins = !ins; } return ins; }
    function grown(f) { var c = centre(fieldPts); return fieldPts.map(function (p) { return [c[0] + (p[0] - c[0]) * f, c[1] + (p[1] - c[1]) * f]; }); }
    function centre(pts) { var x = 0, y = 0; pts.forEach(function (p) { x += p[0]; y += p[1]; }); return [x / pts.length, y / pts.length]; }
    S.inField = function (x, y, f) { return !!fieldPts && inPoly(grown(f || 1.08), x, y); };
    S.free = function (x, y, pad) { return !S.inField(x, y) && blocks.every(function (b) { return Math.hypot(x - b[0], y - b[1]) > b[2] + (pad || 0); }); };
    S.raw = function (markup) { ground.push(markup); return S; };
    /* Reserves the airfield before it is drawn, so patches and tracks laid first stay out of it. */
    S.claim = function (pts) { fieldPts = pts; return S; };

    /* Scenery: its shadow goes to the shadow layer, the sprite to the standing layer. */
    S.put = function (name, x, y, o) {
      o = o || {};
      var a = WORLD[name], s = o.s || 1, r = a.rotates ? (o.r || 0) : 0, tf = 'scale(' + s + ')' + (r ? ' rotate(' + r + ')' : '');
      used[name] = true;
      if (a.sh) shadows.push('<use href="#fa-h-' + name + '" transform="translate(' + r1(x + a.off[0] * s) + ',' + r1(y + a.off[1] * s) + ') ' + tf + '"/>');
      var use = '<use href="#fa-w-' + name + '" transform="translate(' + r1(x) + ',' + r1(y) + ') ' + tf + '"' + (o.style ? ' style="' + o.style + '"' : '') + '/>';
      if (a.tier === 'flat') ground.push(use); else standing.push([y + (o.z || 0), use]);
      return S;
    };
    S.detail = function (kind, x, y, tf) { used['d:' + kind] = true; ground.push('<use href="#fa-d-' + kind + '" transform="translate(' + r1(x) + ',' + r1(y) + ')' + (tf ? ' ' + tf : '') + '"/>'); return S; };

    /* --- ground pieces */
    S.lawn = function (token) { ground.push('<rect ' + k(token || 'grass-500') + ' x="-20" y="-20" width="' + (W + 40) + '" height="' + (H + 40) + '"/>'); return S; };
    S.patches = function (count, light, dark) {
      for (var i = 0; i < count; i++) {
        var x = S.R(0, W), y = S.R(0, H);
        if (S.inField(x, y, 1.2)) continue;
        ground.push('<path ' + k(i % 3 ? (dark || 'grass-600') : (light || 'grass-400'), '', 0, 'opacity="' + (i % 3 ? 0.75 : 0.6) + '"') + ' d="' + smooth(organic(x, y, S.R(60, 150), S.R(40, 90), 9, rng, 0.22)) + '"/>');
      }
      return S;
    };
    /* A public road: verge, kerb line, surface, centre dashes. */
    S.roads = function (lines) {
      var ds = lines.map(function (pts) { return smooth(pts, false); });
      [['grass-700', 40], ['road-700', 29], ['road-500', 25]].forEach(function (l) { ds.forEach(function (d) { ground.push('<path ' + k('', l[0], l[1]) + ' d="' + d + '"/>'); }); });
      ds.forEach(function (d) { ground.push('<path ' + k('', 'foam', 1.8, 'stroke-dasharray="9 10" stroke-linecap="butt"') + ' d="' + d + '"/>'); });
      lines.forEach(function (pts) { for (var i = 1; i < pts.length; i++) for (var t = 0; t <= 1; t += 0.12) S.block(pts[i - 1][0] + (pts[i][0] - pts[i - 1][0]) * t, pts[i - 1][1] + (pts[i][1] - pts[i - 1][1]) * t, 44); });
      return S;
    };
    /* A dirt track: sand with a darker edge and one rut. */
    S.track = function (pts, wide) {
      var d = smooth(pts, false), w = wide || 11;
      ground.push('<path ' + k('', 'sand-500', w + 4) + ' d="' + d + '"/><path ' + k('', 'sand-300', w) + ' d="' + d + '"/><path ' + k('', 'sand-500', 1.5, 'stroke-dasharray="6 7"') + ' d="' + d + '" transform="translate(-2.500,0)"/>');
      for (var i = 1; i < pts.length; i++) for (var t = 0; t <= 1; t += 0.2) S.block(pts[i - 1][0] + (pts[i][0] - pts[i - 1][0]) * t, pts[i - 1][1] + (pts[i][1] - pts[i - 1][1]) * t, w + 14);
      return S;
    };
    /* A crop field of any size: the crop tiles' construction, stretched. kind: wheat, green or plough. */
    S.field = function (x, y, w, h, rot, kind) {
      var P = { wheat: ['straw-300', 'straw-500', 'straw-700', 4], green: ['soil-500', 'leaf-300', 'soil-700', 4], plough: ['soil-500', 'soil-700', 'soil-700', 2.2] }[kind], rows = '', a, b, id = 'fa-cf-' + seed + '-' + (ids++);
      if (h > w) for (a = -w / 2 + 7; a < w / 2; a += 9) rows += 'M' + r1(a) + ' ' + (-h / 2) + 'V' + (h / 2);
      else for (a = -h / 2 + 7; a < h / 2; a += 9) rows += 'M' + (-w / 2) + ' ' + r1(a) + 'H' + (w / 2);
      ground.push('<g transform="translate(' + x + ',' + y + ') rotate(' + rot + ')"><rect ' + k('grass-700') + ' x="' + (-w / 2 - 6) + '" y="' + (-h / 2 - 6) + '" width="' + (w + 12) + '" height="' + (h + 12) + '" rx="16"/><clipPath id="' + id + '"><rect x="' + (-w / 2) + '" y="' + (-h / 2) + '" width="' + w + '" height="' + h + '" rx="11"/></clipPath>' +
        '<rect ' + k(P[0]) + ' x="' + (-w / 2) + '" y="' + (-h / 2) + '" width="' + w + '" height="' + h + '" rx="11"/><g clip-path="url(#' + id + ')"><path ' + k('', P[1], P[3]) + ' d="' + rows + '"/></g><rect ' + k('', P[2], 2) + ' x="' + (-w / 2) + '" y="' + (-h / 2) + '" width="' + w + '" height="' + h + '" rx="11"/></g>');
      for (a = -w / 2; a <= w / 2; a += 40) for (b = -h / 2; b <= h / 2; b += 40) S.block(x + a, y + b, 34);
      return S;
    };
    /* Still water: damp grass, sand, shallows, open water. Returns its outline points. */
    S.pond = function (cx, cy, rx, ry, pseed, extras) {
      var P = organic(cx, cy, rx, ry, 10, rnd(pseed), 0.14), d = smooth(P), d2 = smooth(P.map(function (p) { return [cx + (p[0] - cx) * 0.74, cy + (p[1] - cy) * 0.7]; }));
      ground.push('<path ' + k('', 'grass-700', 22) + ' d="' + d + '"/><path ' + k('sand-300', 'sand-300', 9) + ' d="' + d + '"/><path ' + k('water-300', 'water-700', 1.6) + ' d="' + d + '"/><path ' + k('water-500') + ' d="' + d2 + '"/>');
      ground.push('<path ' + k('', 'foam', 2) + ' d="M' + (cx - 38) + ' ' + (cy - 12) + 'q10 -6 22 0M' + (cx + 22) + ' ' + (cy + 30) + 'q12 -7 26 0M' + (cx - 8) + ' ' + (cy - 40) + 'q8 -5 18 0"/>');
      (extras || []).forEach(function (e) { S.detail(e[0], cx + e[1], cy + e[2], e[3]); });
      S.block(cx, cy, Math.max(rx, ry) + 30);
      return P;
    };
    /* The airfield lawn: mown in stripes parallel to the runway. Countryside is never striped (W8). */
    S.airfield = function (pts, base) {
      S.flush(); fieldPts = pts;
      var d = smooth(pts), id = 'fa-af-' + seed, stripes = '';
      for (var y = -936; y < 936; y += 104) stripes += '<rect ' + k('grass-600') + ' x="-1700" y="' + y + '" width="3400" height="52"/>';
      ground.push('<path ' + k('', 'grass-700', 7, 'opacity="0.7"') + ' d="' + d + '"/><clipPath id="' + id + '"><path d="' + d + '"/></clipPath><path ' + k(base || 'grass-500') + ' d="' + d + '"/><g clip-path="url(#' + id + ')"><g transform="translate(' + frame.cx + ',' + frame.cy + ') rotate(' + frame.deg + ')">' + stripes + '</g></g>');
      return S;
    };
    /* --- hard surfaces, in the runway frame */
    S.apron = function (x, y, w, h, stands) {
      var s = '<rect ' + k('grass-700') + ' x="' + (x - 6) + '" y="' + (y - 6) + '" width="' + (w + 12) + '" height="' + (h + 12) + '" rx="14"/><rect ' + k('concrete-300', 'concrete-700', 2) + ' x="' + x + '" y="' + y + '" width="' + w + '" height="' + h + '" rx="10"/>', j = '';
      for (var a = x + 45; a < x + w - 10; a += 45) j += 'M' + a + ' ' + y + 'v' + h;
      j += 'M' + x + ' ' + r1(y + h / 2) + 'h' + w;
      s += '<path ' + k('', 'concrete-500', 1.2) + ' d="' + j + '"/>';
      (stands || []).forEach(function (p) { s += '<path ' + k('', 'marking-warn', 2) + ' d="M' + p[0] + ' ' + (p[1] + 36) + 'v-22M' + (p[0] - 11) + ' ' + (p[1] + 14) + 'h22"/>'; });
      local.push(s);
      return S;
    };
    /* Taxiways: lighter asphalt with a yellow centre line, so they never look like a place to land. */
    S.taxi = function (paths, holds) {
      [['grass-700', 44], ['concrete-700', 33], ['asphalt-300', 29]].forEach(function (l) { paths.forEach(function (d) { local.push('<path ' + k('', l[0], l[1], 'stroke-linecap="butt"') + ' d="' + d + '"/>'); }); });
      paths.forEach(function (d) { local.push('<path ' + k('', 'marking-warn', 1.8, 'stroke-linecap="butt"') + ' d="' + d + '"/>'); });
      (holds || []).forEach(function (p) {
        local.push('<path ' + k('', 'marking-warn', 1.5) + ' d="M' + (p[0] - 13) + ' ' + p[1] + 'h26M' + (p[0] - 13) + ' ' + (p[1] + 4) + 'h26"/><path ' + k('', 'marking-warn', 1.5, 'stroke-dasharray="4 3" stroke-linecap="butt"') + ' d="M' + (p[0] - 13) + ' ' + (p[1] + 9) + 'h26M' + (p[0] - 13) + ' ' + (p[1] + 13) + 'h26"/>');
      });
      return S;
    };
    S.helipad = function (x, y, zone) { local.push('<g transform="translate(' + x + ',' + y + ')">' + helipadMarkup(zone) + '</g>'); return S; };
    S.runway = function (length, numbers, oneWay, zone, x, y, deg) { local.push('<g transform="translate(' + (x || 0) + ',' + (y || 0) + ') rotate(' + (deg || 0) + ')">' + runwayMarkup(length, numbers, oneWay, zone) + '</g>'); return S; };
    S.lot = function (x, y, w, h) {
      var s = '<rect ' + k('grass-700') + ' x="' + (x - 5) + '" y="' + (y - 5) + '" width="' + (w + 10) + '" height="' + (h + 10) + '" rx="10"/><rect ' + k('concrete-300', 'concrete-700', 1.6) + ' x="' + x + '" y="' + y + '" width="' + w + '" height="' + h + '" rx="7"/>', j = '';
      for (var a = x + 22; a < x + w - 6; a += 22) j += 'M' + a + ' ' + y + 'v' + r1(h * 0.42);
      ground.push(s + '<path ' + k('', 'foam', 1.5) + ' d="' + j + '"/>');
      return S;
    };
    /* --- rows and groups */
    S.hedgeRow = function (x0, y0, x1, y1) {
      var L = Math.hypot(x1 - x0, y1 - y0), n = Math.round(L / 13), o = '', f = '', hl = '', sh = '';
      for (var i = 0; i <= n; i++) {
        var x = r1(x0 + (x1 - x0) * i / n), y = r1(y0 + (y1 - y0) * i / n + (rng() - 0.5) * 3), r = r1(8.5 + rng() * 2.5);
        o += '<circle cx="' + x + '" cy="' + y + '" r="' + r + '"/>'; f += '<circle cx="' + x + '" cy="' + y + '" r="' + r + '"/>'; hl += '<circle cx="' + r1(x - 2) + '" cy="' + r1(y - 3) + '" r="' + r1(r * 0.55) + '"/>';
        S.block(x, y, 22);
      }
      shadows.push('<g transform="translate(4,5)">' + f + '</g>');
      standing.push([Math.max(y0, y1), '<g ' + k('leaf-700', O, 5) + '>' + o + '</g><g ' + k('leaf-500') + '>' + f + '</g><g ' + k('leaf-300') + '>' + hl + '</g>']);
      return S;
    };
    S.paddock = function (pts, base) {
      var d = 'M' + pts.map(function (p) { return p.join(' '); }).join('L') + 'Z', posts = '', a, b;
      ground.push('<path ' + k(base || 'grass-400', 'grass-700', 2, 'opacity="0.9"') + ' d="' + d + '"/>');
      shadows.push('<path ' + k('', 'shadow-cast', 3) + ' d="' + d + '" transform="translate(2.500,3.500)"/>');
      for (var i = 0; i < pts.length; i++) {
        a = pts[i]; b = pts[(i + 1) % pts.length];
        var n = Math.round(Math.hypot(b[0] - a[0], b[1] - a[1]) / 30);
        for (var j = 0; j < n; j++) posts += '<circle ' + k('wood-300', O, 1.6) + ' cx="' + r1(a[0] + (b[0] - a[0]) * j / n) + '" cy="' + r1(a[1] + (b[1] - a[1]) * j / n) + '" r="3"/>';
      }
      standing.push([Math.min.apply(null, pts.map(function (p) { return p[1]; })), '<path ' + k('', O, 4.6) + ' d="' + d + '"/><path ' + k('', 'wood-300', 2) + ' d="' + d + '"/>' + posts]);
      var c = centre(pts);
      S.block(c[0], c[1], Math.hypot(pts[0][0] - c[0], pts[0][1] - c[1]) + 10);
      return S;
    };
    /* A wood: sprites with a minimum spacing, never under the HUD band and never on a blocked spot. */
    S.wood = function (x0, y0, x1, y1, count, kinds, wseed, gap) {
      var wr = rnd(wseed), placed = [];
      for (var t = 0; t < count * 14 && placed.length < count; t++) {
        var x = x0 + wr() * (x1 - x0), y = y0 + wr() * (y1 - y0);
        if (y < 150 || !S.free(x, y, 26) || placed.some(function (p) { return Math.hypot(p[0] - x, p[1] - y) < (gap || 40); })) continue;
        placed.push([x, y]); S.put(kinds[Math.floor(wr() * kinds.length)], x, y, { s: 0.85 + wr() * 0.35 });
      }
      placed.forEach(function (p) { S.block(p[0], p[1], 30); });
      return S;
    };
    /* Tufts and flowers: in clusters, on open ground only (W8). */
    S.scatter = function (count, kinds) {
      kinds = kinds || ['flower', 'tuft', 'tuft-light', 'tuft', 'flower-pink', 'tuft', 'tuft-light', 'tuft'];
      for (var i = 0; i < count; i++) {
        var x = S.R(20, W - 20), y = S.R(20, H - 20);
        if (!S.free(x, y, 6)) continue;
        var kind = kinds[i % kinds.length], n = kind.indexOf('flower') === 0 ? 5 : 3;
        for (var j = 0; j < n; j++) S.detail(kind, x + S.R(-16, 16), y + S.R(-12, 12));
      }
      return S;
    };

    /* --- the air */
    function craft(type, livery) { return '<g style="--fa-livery:var(--path-' + livery + ')" transform="translate(-48,-48)">' + paint(AIRCRAFT[type].svg, cls) + '</g>'; }
    S.fly = function (type, x, y, heading, size, livery, onGround) {
      var tf = ' rotate(' + heading + ') scale(' + r1(size / 96 * 1000) / 1000 + ')', body = craft(type, livery || 1);
      if (!onGround) airShadow.push('<g transform="translate(' + (x + 15) + ',' + (y + 20) + ')' + tf + '">' + body + '</g>');
      (onGround ? over : air).push('<g transform="translate(' + x + ',' + y + ')' + tf + '">' + body + '</g>');
      return S;
    };
    /* A flight path in world units: the 3dp dash on its 7dp casing, at the reference phone's scale. */
    S.path = function (pts, livery, idle) {
      var d = smooth(pts, false), e = pts[pts.length - 1], c = 'path-' + livery;
      over.push('<g' + (idle ? ' opacity="0.75"' : '') + '><path ' + k('', O, 16.8) + ' d="' + d + '"/><path ' + k('', c, 7.2, 'stroke-dasharray="24 19"') + ' d="' + d + '"/><circle ' + k(c, O, 4.8) + ' cx="' + e[0] + '" cy="' + e[1] + '" r="14.400"/></g>');
      return S;
    };
    S.ghost = function (x0, y0, x1, y1) { over.push('<path ' + k('', O, 6, 'stroke-dasharray="1 16" opacity="0.5"') + ' d="M' + x0 + ' ' + y0 + 'L' + x1 + ' ' + y1 + '"/>'); return S; };
    S.select = function (x, y, r, callsign) {
      air.unshift('<circle ' + k('', O, 9.6) + ' cx="' + x + '" cy="' + y + '" r="' + r + '"/><circle ' + k('', 'select-ring', 4.8, 'stroke-dasharray="22 17"') + ' cx="' + x + '" cy="' + y + '" r="' + r + '"/>' +
        (callsign ? '<g transform="translate(' + x + ',' + (y + r + 22) + ')"><rect ' + k(O) + ' x="-52" y="-17" width="104" height="34" rx="10"/><text class="fa-p-callsign" text-anchor="middle" dominant-baseline="central">' + callsign + '</text></g>' : ''));
      return S;
    };
    S.cloud = function (x, y, s) { clouds.push([x, y, s || 1]); return S; };

    /* opts: { air: false leaves out paths, aircraft and clouds; viewBox: "x y w h" crops } */
    S.svg = function (opts) {
      opts = opts || {}; S.flush();
      var defs = '', body, name;
      for (name in used) {
        if (name.indexOf('d:') === 0) defs += '<g id="fa-d-' + name.slice(2) + '">' + DETAILS[name.slice(2)] + '</g>';
        else defs += '<g id="fa-w-' + name + '">' + WORLD[name].svg + '</g>' + (WORLD[name].sh ? '<g id="fa-h-' + name + '">' + WORLD[name].sh + '</g>' : '');
      }
      standing.sort(function (a, b) { return a[0] - b[0]; });
      body = '<defs>' + defs + (clouds.length ? '<g id="fa-w-cloud">' + cloudMarkup() + '</g>' : '') + '</defs>' +
        '<g>' + ground.join('') + '</g><g class="fa-shadow-cast">' + shadows.join('') + '</g><g>' + standing.map(function (s) { return s[1]; }).join('') + '</g>';
      if (opts.air !== false) {
        body += over.join('') + '<g class="fa-shadow-air">' + airShadow.join('') + '</g>' + air.join('');
        if (clouds.length) body += '<g class="fa-shadow-cloud">' + clouds.map(function (c) { return '<use href="#fa-w-cloud" transform="translate(' + (c[0] + 34) + ',' + (c[1] + 50) + ') scale(' + c[2] + ')"/>'; }).join('') + '</g><g class="fa-clouds">' + clouds.map(function (c) { return '<use href="#fa-w-cloud" transform="translate(' + c[0] + ',' + c[1] + ') scale(' + c[2] + ')"/>'; }).join('') + '</g>';
      }
      return '<svg xmlns="http://www.w3.org/2000/svg" class="fa-scene-svg" viewBox="' + (opts.viewBox || '0 0 ' + W + ' ' + H) + '" preserveAspectRatio="xMidYMid slice" stroke-linejoin="round" stroke-linecap="round" aria-hidden="true">' + paint(body, worldCss) + '</svg>';
    };
    return S;
  }

  /* Meadow Field: one runway and a helipad between a road, a farm, crop fields, a pond and woods. */
  function meadowScene() {
    var S = Scene(20261113), p;
    S.lawn().frame(930, 590, -7);
    var field = [[430, 250], [960, 215], [1480, 250], [1640, 480], [1590, 760], [960, 820], [400, 800], [315, 520]];
    S.claim(field);
    S.patches(26);
    S.track([[240, 880], [300, 905], [345, 1010], [372, 1002]]);
    S.roads([[[232, -30], [205, 170], [172, 370], [214, 600], [250, 800], [205, 1110]], [[178, 330], [300, 322], [420, 318], [500, 322]]]);
    S.field(860, 975, 330, 120, -3, 'wheat').field(1215, 985, 300, 112, 2, 'green').field(78, 560, 112, 290, -2, 'plough').field(74, 900, 104, 190, 1, 'green');
    [[760, 952], [802, 1004], [905, 960], [968, 1002]].forEach(function (q) { S.put('hay-bale', q[0], q[1]); });
    S.pond(1768, 560, 96, 68, 77, [['lily', -46, 25], ['lily', -32, 38, 'rotate(120) scale(.8)'], ['lily', 50, -25, 'rotate(200)'], ['duck', 10, -4, 'rotate(60)'], ['duck', 30, 10, 'rotate(75) scale(.85)'], ['reed', -78, -40], ['reed', -70, -30], ['reed', 78, 40], ['reed', 70, 52], ['reed', -8, 68], ['reed', -78, 40]]);
    S.airfield(field);
    S.apron(-300, -235, 360, 105, [[-205, -186], [-112, -186]]).taxi(['M-160 -132V-30', 'M58 -185H250'], [[-160, -60]]).helipad(300, -190).runway(760, '09/27', false, 'ok');
    S.lot(491, 301, 86, 48);
    S.put('car', 502, 314).put('car', 546, 315, { r: 180, style: '--denim-500:var(--wall-300);--denim-300:var(--wall-100)' }).put('car', 214, 700, { r: 172, style: '--denim-500:var(--pine-500);--denim-300:var(--pine-300)' }).put('car', 190, 250, { r: 8 });
    p = S.at(-262, -292); S.put('tower', p[0], p[1]);
    p = S.at(-118, -296); S.put('terminal', p[0], p[1]);
    p = S.at(30, -300); S.put('hangar', p[0], p[1]);
    p = S.at(-205, -186); S.put('parked-aircraft', p[0], p[1], { r: -7, s: 0.8 });
    p = S.at(-112, -186); S.put('parked-aircraft', p[0], p[1], { r: -7, s: 0.8 });
    p = S.at(-50, -178); S.put('fuel-truck', p[0], p[1], { r: 20, s: 0.9 });
    p = S.at(150, -86); S.put('windsock', p[0], p[1]);
    p = S.at(-340, 100); S.put('windsock', p[0], p[1], { s: 0.9 });
    S.put('farmhouse', 372, 958).put('barn', 520, 968).put('silo', 588, 944);
    S.block(372, 958, 80); S.block(520, 968, 80); S.block(588, 944, 40);
    S.put('tree-oak', 300, 1010).put('bush', 430, 1010).put('bush', 316, 916).put('rock', 640, 1010).put('farmer', 452, 1036, { r: 30 });
    S.paddock([[1560, 858], [1806, 866], [1800, 1010], [1552, 1002]]);
    S.put('cow', 1610, 905, { r: 40 }).put('cow', 1688, 950, { r: -70 }).put('cow', 1748, 902, { r: 150 }).put('cow', 1630, 968, { r: 100, s: 0.85 });
    S.put('rock', 1662, 648).put('rock', 1858, 470, { s: 1.2 }).put('bush', 1690, 470).put('bush', 1872, 640);
    S.hedgeRow(700, 900, 1380, 908);
    S.wood(10, 150, 140, 400, 9, ['pine', 'pine-small', 'pine'], 3).wood(1470, 150, 1910, 300, 14, ['tree', 'tree-small', 'tree-oak', 'pine'], 4).wood(1700, 300, 1910, 450, 5, ['tree', 'tree-small'], 6)
      .wood(1400, 1030, 1910, 1075, 6, ['tree', 'pine-small', 'tree-small'], 8).wood(1820, 700, 1910, 1000, 5, ['tree-small', 'pine'], 12).wood(270, 150, 400, 260, 3, ['tree', 'tree-small'], 14)
      .wood(250, 560, 300, 860, 3, ['tree-small'], 15).wood(1380, 830, 1530, 960, 3, ['tree', 'tree-oak'], 16);
    S.scatter(46);
    var rwE = S.at(366, 0), heli = S.at(300, -190);
    S.path([[1722, 742], [1650, 640], [1540, 560], [1420, 535], rwE], 3).path([[1470, 196], [1400, 250], [1300, 300], heli], 6, true).ghost(1085, 855, 1260, 750);
    S.fly('jet', 1722, 742, -36, 108, 3).fly('helicopter', 1470, 196, -128, 96, 6).fly('jumbo', 1010, 900, 59, 156, 5).fly('turboprop', 452, 486, 104, 102, 2);
    p = S.at(120, 0); S.fly('light-prop', p[0], p[1], -97, 84, 1, true);
    S.select(1722, 742, 78, 'FA 204').cloud(420, 214, 1.5).cloud(1458, 916, 1.15);
    return S;
  }


  /* Island: the airfield fills a small island; boats, a jetty and palms around it. */
  function islandScene() {
    var S = Scene(4711), p, rng = rnd(99);
    S.lawn('water-500');
    for (var i = 0; i < 14; i++) S.raw('<path ' + k('water-700', '', 0, 'opacity="0.28"') + ' d="' + smooth(organic(S.R(0, 1920), S.R(0, 1080), S.R(120, 260), S.R(60, 120), 9, rng, 0.2)) + '"/>');
    var isle = organic(960, 560, 640, 350, 14, rnd(31), 0.1), d = smooth(isle);
    function shrink(f) { return smooth(isle.map(function (q) { return [960 + (q[0] - 960) * f, 560 + (q[1] - 560) * f]; })); }
    S.raw('<path ' + k('', 'foam', 26, 'opacity="0.55"') + ' d="' + d + '"/><path ' + k('water-300', 'water-300', 44) + ' d="' + d + '"/><path ' + k('sand-300', 'sand-500', 2) + ' d="' + d + '"/><path ' + k('grass-500', 'grass-700', 7) + ' d="' + shrink(0.86) + '"/>');
    S.raw('<path ' + k('', 'foam', 2.2, 'stroke-dasharray="46 30"') + ' d="' + smooth(isle.map(function (q) { return [960 + (q[0] - 960) * 1.045, 560 + (q[1] - 560) * 1.06]; })) + '"/>');
    S.frame(960, 560, 9);
    S.airfield(isle.map(function (q) { return [960 + (q[0] - 960) * 0.8, 560 + (q[1] - 560) * 0.78]; }));
    S.apron(-250, -215, 280, 100, [[-160, -168]]).taxi(['M-130 -116V-30', 'M28 -168H190'], [[-130, -58]]).helipad(250, -168, 'ok').runway(700, '10/28', false);
    p = S.at(-205, -268); S.put('tower', p[0], p[1]);
    p = S.at(-70, -272); S.put('terminal', p[0], p[1], { s: 0.9 });
    p = S.at(-160, -168); S.put('parked-aircraft', p[0], p[1], { r: 9, s: 0.8 });
    p = S.at(-60, -160); S.put('baggage-cart', p[0], p[1], { r: 80, s: 0.8 });
    p = S.at(120, -70); S.put('windsock', p[0], p[1]);
    /* everything outside the island's grass is water or beach: keep woods and flowers off it */
    for (var a = 0; a < 1920; a += 60) for (var b = 0; b < 1080; b += 60) if (!insideIsle(a, b)) S.block(a, b, 46);
    function insideIsle(x, y) { var ins = false; for (var m = 0, n = isle.length - 1; m < isle.length; n = m++) { var u = [960 + (isle[m][0] - 960) * 0.84, 560 + (isle[m][1] - 560) * 0.84], v = [960 + (isle[n][0] - 960) * 0.84, 560 + (isle[n][1] - 560) * 0.84]; if ((u[1] > y) !== (v[1] > y) && x < (v[0] - u[0]) * (y - u[1]) / (v[1] - u[1]) + u[0]) ins = !ins; } return ins; }
    S.wood(340, 230, 1580, 900, 26, ['palm', 'palm', 'bush'], 21, 58);
    S.put('palm', 420, 520, { s: 1.1 }).put('palm', 1490, 640).put('palm', 1010, 880, { s: 0.9 }).put('rock', 372, 640).put('rock', 1560, 470, { s: 1.2 });
    S.put('jetty', 960, 968, { r: 4 }).put('sailboat', 1010, 1030, { r: 30 }).put('wake', 1022, 1052, { r: 30, s: 0.8 });
    S.put('yacht', 250, 300, { r: 124 }).put('wake', 222, 281, { r: 124 }).put('container-vessel', 1740, 220, { r: -62 }).put('wake', 1790, 247, { r: -62, s: 1.6 }).put('sailboat', 1790, 870, { r: -150 });
    [[140, 620], [210, 820], [520, 60], [1320, 70], [1660, 600], [1500, 1000], [700, 1040], [90, 130], [1850, 420]].forEach(function (q, j) { S.put('wave', q[0], q[1], { r: j % 2 ? 6 : -8 }); });
    S.scatter(24);
    var heli = S.at(250, -168), rwW = S.at(-336, 0);
    S.path([[1540, 250], [1440, 300], [1320, 330], heli], 4).path([[230, 760], [300, 660], [420, 560], [540, 520], rwW], 1, true);
    S.fly('helicopter', 1540, 250, -118, 96, 4).fly('turboprop', 230, 760, 34, 102, 1).fly('jet', 1500, 880, -60, 108, 2);
    S.select(1540, 250, 72, 'FA 118').cloud(700, 170, 1.3).cloud(1300, 960, 1.1);
    return S;
  }

  /* Savanna: dry ground, a one-way runway, acacias, a waterhole, zebras and an elephant. */
  function savannaScene() {
    var S = Scene(1903), p;
    S.lawn('grass-400').frame(960, 560, 4);
    var field = [[420, 300], [960, 270], [1520, 300], [1640, 560], [1540, 790], [960, 830], [400, 790], [300, 540]];
    S.claim(field);
    S.patches(30, 'sand-300', 'grass-500');
    S.track([[-20, 930], [300, 900], [640, 940], [1000, 925], [1400, 960], [1940, 915]], 22).track([[640, 940], [600, 860], [520, 800]], 14);
    S.pond(330, 250, 120, 74, 51, [['reed', -100, -30], ['reed', -92, -18], ['reed', 96, 44], ['reed', 104, 30], ['lily', 30, 20], ['duck', -30, -6, 'rotate(40)']]);
    S.airfield(field, 'grass-500');
    S.apron(-260, -210, 240, 96, [[-190, -164]]).taxi(['M-140 -116V-30', 'M-22 -164H170'], [[-140, -58]]).helipad(230, -164).runway(720, '09/27', true, 'ok');
    p = S.at(-290, -262); S.put('tower', p[0], p[1]);
    p = S.at(-150, -272); S.put('hangar', p[0], p[1], { s: 0.92 });
    p = S.at(-190, -164); S.put('parked-aircraft', p[0], p[1], { r: 4, s: 0.8 });
    p = S.at(-70, -160); S.put('fuel-truck', p[0], p[1], { r: 96, s: 0.9 });
    p = S.at(90, -70); S.put('windsock', p[0], p[1]);
    S.put('acacia', 1700, 300, { s: 1.2 }).put('acacia', 1560, 190).put('acacia', 150, 560).put('acacia', 240, 760, { s: 1.15 }).put('acacia', 1780, 700).put('acacia', 860, 1010, { s: 0.9 }).put('acacia', 1330, 1040).put('acacia', 620, 180, { s: 0.9 });
    [[1700, 300], [1560, 190], [150, 560], [240, 760], [1780, 700], [860, 1010], [1330, 1040], [620, 180]].forEach(function (q) { S.block(q[0], q[1], 60); });
    S.put('zebra', 1640, 880, { r: 60 }).put('zebra', 1700, 850, { r: 78 }).put('zebra', 1730, 905, { r: 40 }).put('zebra', 1590, 835, { r: 95, s: 0.85 }).put('elephant', 120, 330, { r: 100 });
    S.put('rock', 1840, 520, { s: 1.3 }).put('rock', 1210, 180).put('rock', 470, 900).put('bush', 1420, 190).put('bush', 90, 820).put('bush', 1880, 880).put('bush', 760, 880);
    S.wood(10, 150, 1910, 1070, 22, ['bush', 'bush', 'rock'], 71, 120);
    S.scatter(40, ['tuft', 'tuft-light', 'tuft']);
    var rwE = S.at(326, 0), heli = S.at(230, -164);
    S.path([[1730, 470], [1620, 520], [1480, 560], rwE], 2).path([[1380, 210], [1320, 280], [1240, 340], heli], 6, true);
    S.fly('jumbo', 1730, 470, -112, 156, 2).fly('helicopter', 1380, 210, -140, 96, 6).fly('light-prop', 560, 720, 40, 84, 4);
    S.select(1730, 470, 96, 'FA 731').cloud(980, 140, 1.3).cloud(360, 980, 1.1);
    return S;
  }
  var SCENES = { meadow: meadowScene, island: islandScene, savanna: savannaScene };
  var sceneCache = {};
  /* The picture of a level. opts.air = false leaves out paths, aircraft and clouds; opts.viewBox crops. */
  function sceneSvg(name, opts) {
    var key = SCENES[name] ? name : 'meadow';
    if (!sceneCache[key]) sceneCache[key] = SCENES[key]();
    return sceneCache[key].svg(opts);
  }

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

  /* Blobs: outlined circles first, then the same circles as fill, so only the silhouette keeps a stroke. */
  function blob(circles, edge, fill) {
    var a = '', b = '';
    circles.forEach(function (c) {
      a += '<circle ' + edge + ' cx="' + c[0] + '" cy="' + c[1] + '" r="' + c[2] + '"/>';
      b += '<circle ' + fill + ' cx="' + c[0] + '" cy="' + c[1] + '" r="' + c[2] + '"/>';
    });
    return a + b;
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
    cloudSvg: cloudSvg, windsockSvg: windsockSvg, burstSvg: burstSvg,
    WORLD: WORLD, DETAILS: DETAILS, WORLD_MAX_MATERIALS: WORLD_MAX_MATERIALS, WORLD_BANNED: WORLD_BANNED, WORLD_ACCENT: WORLD_ACCENT,
    WORLD_TRIM: WORLD_TRIM, TIER_SHADOW: TIER_SHADOW, worldPaint: worldPaint, worldSvg: worldSvg, SCENES: Object.keys(SCENES), sceneSvg: sceneSvg
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
  /* zone="ok" or zone="no" adds the landing-zone glow. */
  define('fa-runway', ['length', 'numbers', 'mode', 'zone'], function (el) {
    var L = Math.max(240, parseFloat(el.getAttribute('length')) || 760);
    el.style.setProperty('--fa-length', (L + 56) + 'px');
    el.innerHTML = runwaySvg(L, el.getAttribute('numbers'), el.getAttribute('mode') === 'one-way', el.getAttribute('zone'));
  });
  define('fa-helipad', ['zone'], function (el) { el.innerHTML = helipadSvg(el.getAttribute('zone')); });
  define('fa-cloud', [], function (el) { el.innerHTML = cloudSvg(); });
  define('fa-windsock', [], function (el) { el.innerHTML = windsockSvg(); });
  define('fa-burst', ['kind'], function (el) { el.innerHTML = burstSvg(el.getAttribute('kind')); });
  /* One scenery sprite with its cast shadow. size multiplies its world size (1 unit = 1px). */
  define('fa-scenery', ['name', 'size'], function (el) {
    var name = el.getAttribute('name'), a = WORLD[name] || WORLD.tree, s = parseFloat(el.getAttribute('size')) || 1;
    el.style.width = (a.w * s) + 'px';
    el.style.height = (a.h * s) + 'px';
    el.setAttribute('role', 'img');
    el.setAttribute('aria-label', a.label);
    el.innerHTML = worldSvg(name);
  });
  /* The picture of a level: level="meadow", "island" or "savanna". ground leaves out paths, aircraft
     and clouds; crop="x y w h" shows a part of the 1920 by 1080 world. */
  define('fa-scene', ['level', 'ground', 'crop'], function (el) {
    el.setAttribute('role', 'img');
    el.setAttribute('aria-label', (el.getAttribute('level') || 'meadow') + ' airfield');
    el.innerHTML = sceneSvg(el.getAttribute('level'), { air: !el.hasAttribute('ground'), viewBox: el.getAttribute('crop') || undefined });
  });
})();
