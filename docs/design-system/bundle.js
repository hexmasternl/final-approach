/* Final Approach world kit. Vanilla custom elements, no dependencies.
   window.FinalApproach = { AIRCRAFT, ICONS, SCENERY, aircraftSvg, iconSvg, runwaySvg, helipadSvg, cloudSvg, windsockSvg, burstSvg, scenerySvg }
   Elements: <fa-aircraft> <fa-side-aircraft> <fa-silhouette> <fa-icon> <fa-runway> <fa-helipad> <fa-cloud> <fa-windsock> <fa-burst> <fa-scenery>
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

  /* ---------- Scenery ----------
     Scenery sprites are drawn top-down, front up, on a grid whose unit is one world unit at the
     size the sprite is meant to be placed at. A paint key in a sprite is one of:
       {{name}}       fill SCENERY_FILL[name], outline in line-2
       {{name-fine}}  the same fill, outline in line-fine
       {{name-flat}}  the fill only, no stroke
       {{line}}       no fill, outline-coloured stroke in line-2;   {{line-name}} the same in SCENERY_FILL[name]
       {{fine}}       no fill, outline-coloured stroke in line-fine; {{fine-name}} the same in SCENERY_FILL[name]
     A sprite uses at most SCENERY_MAX_FILLS fill tokens and none of SCENERY_BANNED. `ground` is what a
     sprite stands on when it is shown (grass unless stated). */
  var SCENERY_FILL = {
    white: 'surface-raised', shade: 'fuselage-shade', pane: 'sky-300', glass: 'glass', blue: 'sky-500',
    leaf: 'grass-300', bush: 'grass-700', deep: 'grass-900',
    wood: 'soil-500', furrow: 'soil-700', roof: 'roof-500', sand: 'sand-300', stone: 'stone-300', foam: 'foam',
    hull: 'asphalt-700', cab: 'cherry', sock: 'tangerine', dark: 'outline'
  };
  var SCENERY_STROKE = { outline: 'line-2', fine: 'line-fine' };
  var SCENERY_MAX_FILLS = 4;
  var SCENERY_BANNED = ['path-1', 'path-2', 'path-3', 'path-4', 'path-5', 'path-6', 'sun', 'fuselage', 'shadow-ground'];
  /* The two path colours the older components already use; only these sprites may keep them. */
  var SCENERY_ACCENT = { tower: 'cherry', windsock: 'tangerine' };

  /* What a paint key means: { fill, stroke, strokeColor } as token names, null where absent. */
  function sceneryPaint(key) {
    var m = /^(line|fine)(?:-([a-z]+))?$/.exec(key);
    if (m) {
      if (m[2] && !SCENERY_FILL[m[2]]) throw new Error('scenery paint ' + key);
      return { fill: null, stroke: m[1] === 'line' ? 'outline' : 'fine', strokeColor: m[2] ? SCENERY_FILL[m[2]] : 'outline' };
    }
    m = /^([a-z]+)(?:-(fine|flat))?$/.exec(key);
    if (!m || !SCENERY_FILL[m[1]]) throw new Error('scenery paint ' + key);
    return { fill: SCENERY_FILL[m[1]], stroke: m[2] === 'flat' ? null : (m[2] || 'outline'), strokeColor: 'outline' };
  }

  function n1(v) { return String(Math.round(v * 10) / 10); }
  /* A lobed blob with one outline around the whole: dark discs first, then the fill on top. */
  function sblob(circles, fill) {
    var a = '', b = '';
    circles.forEach(function (c) {
      a += '<circle {{dark}} cx="' + c[0] + '" cy="' + c[1] + '" r="' + c[2] + '"/>';
      b += '<circle {{' + fill + '-flat}} cx="' + c[0] + '" cy="' + c[1] + '" r="' + n1(c[2] - 1.5) + '"/>';
    });
    return a + b;
  }
  /* A leaf from (cx, cy) pointing at `deg` (clockwise from up), `len` long and `wide` across, with a midrib. */
  function frond(paintKey, cx, cy, deg, len, wide) {
    var r = deg * Math.PI / 180, ux = Math.sin(r), uy = -Math.cos(r), vx = -uy, vy = ux;
    function p(along, across) { return n1(cx + ux * along + vx * across) + ' ' + n1(cy + uy * along + vy * across); }
    return '<path {{' + paintKey + '}} d="M' + p(0, 0) + 'Q' + p(len * 0.45, -wide) + ' ' + p(len, 0) + 'Q' + p(len * 0.45, wide) + ' ' + p(0, 0) + 'z"/>' +
      '<path {{fine}} d="M' + p(len * 0.12, 0) + 'L' + p(len * 0.8, 0) + '"/>';
  }
  /* A regular octagon around (cx, cy) with a flat side up; r is the distance to a corner. */
  function octagon(cx, cy, r) {
    var d = '', i, t;
    for (i = 0; i < 8; i++) { t = (22.5 + 45 * i) * Math.PI / 180; d += (i ? 'L' : 'M') + n1(cx + r * Math.cos(t)) + ' ' + n1(cy + r * Math.sin(t)); }
    return d + 'z';
  }
  function palmSvg() {
    var s = '', i;
    for (i = 0; i < 4; i++) s += frond('bush', 32, 32, 45 + i * 90, 27, 8);
    for (i = 0; i < 4; i++) s += frond('leaf', 32, 32, i * 90 + 8, 30, 8.5);
    return s + '<circle {{wood-fine}} cx="29.5" cy="30.5" r="3.5"/><circle {{wood-fine}} cx="34.5" cy="30.5" r="3.5"/><circle {{wood-fine}} cx="32" cy="35" r="3.5"/>';
  }
  function containers() {
    var s = '', k = 0, row, col;
    for (row = 0; row < 5; row++) for (col = 0; col < 2; col++, k++)
      s += '<rect {{' + ((row + col) % 2 ? 'sand' : 'roof') + '-fine}} x="' + (9 + col * 16) + '" y="' + (44 + row * 19) + '" width="14" height="16" rx="1.5"/>';
    return s;
  }

  var SCENERY = {
    /* ----- Meadow ----- */
    cow: {
      label: 'Cow', group: 'Meadow', width: 36, height: 60,
      svg:
        '<path {{line}} d="M18 52q0 4 3 6"/><circle {{dark-flat}} cx="21.5" cy="57.5" r="2"/>' +
        '<rect {{white}} x="3" y="24" width="9" height="6" rx="3"/><rect {{white}} x="24" y="24" width="9" height="6" rx="3"/>' +
        '<rect {{white}} x="3" y="42" width="9" height="6" rx="3"/><rect {{white}} x="24" y="42" width="9" height="6" rx="3"/>' +
        '<ellipse {{white}} cx="18" cy="36" rx="11" ry="17"/>' +
        '<path {{dark-flat}} d="M9 31q4-7 9-3 3 4-2 7-5 2-7-4z"/><path {{dark-flat}} d="M20 40q6-3 8 3 0 6-5 6-5-2-3-9z"/><path {{dark-flat}} d="M22 23q4 0 5 4-3 2-6-1z"/>' +
        '<ellipse {{white}} cx="8.500" cy="12" rx="4.500" ry="2.500"/><ellipse {{white}} cx="27.500" cy="12" rx="4.500" ry="2.500"/>' +
        '<ellipse {{white}} cx="18" cy="12" rx="7.500" ry="9"/>' +
        '<path {{dark-flat}} d="M13 13q2-5 5-3 0 5-5 3z"/>' +
        '<ellipse {{sand-fine}} cx="18" cy="5.500" rx="5" ry="3.500"/>' +
        '<circle {{dark-flat}} cx="14" cy="15" r="1.300"/><circle {{dark-flat}} cx="22" cy="15" r="1.300"/>'
    },
    farmer: {
      label: 'Farmer', group: 'Meadow', width: 42, height: 46,
      svg:
        '<rect {{wood-fine}} x="33.300" y="6" width="3.400" height="20" rx="1.700"/>' +
        '<path {{line}} d="M29.500 7h11M29.500 7V1.500M35 7V1.500M40.500 7V1.500"/>' +
        '<rect {{blue}} x="2.500" y="16" width="7" height="16" rx="3.500"/><rect {{blue}} x="31.500" y="16" width="7" height="16" rx="3.500"/>' +
        '<ellipse {{blue}} cx="20.500" cy="31" rx="18" ry="9.500"/>' +
        '<circle {{sand-fine}} cx="6" cy="16.500" r="3"/><circle {{sand-fine}} cx="35" cy="16.500" r="3"/>' +
        '<circle {{sand}} cx="20.500" cy="31" r="9"/><circle {{sand-fine}} cx="20.500" cy="31" r="4.500"/>'
    },
    'bird-up': {
      label: 'Bird, wings out', group: 'Meadow', width: 44, height: 30,
      svg:
        '<path {{white}} d="M19 9Q10 2 3 8q-1 6 5 10l11-1z"/><path {{white}} d="M25 9q9-7 16-1 1 6-5 10l-11-1z"/>' +
        '<path {{dark-flat}} d="M4 8.500q-.5 4.500 4 8l1.500-3q-3-2-2.500-6z"/><path {{dark-flat}} d="M40 8.500q.5 4.500-4 8l-1.500-3q3-2 2.500-6z"/>' +
        '<path {{white}} d="M22 20l-5 8h10z"/>' +
        '<ellipse {{white}} cx="22" cy="14" rx="5" ry="10"/>' +
        '<path {{sand-fine}} d="M19.500 5.500L22 1l2.500 4.500z"/>' +
        '<circle {{dark-flat}} cx="20" cy="9" r="1.100"/><circle {{dark-flat}} cx="24" cy="9" r="1.100"/>'
    },
    'bird-down': {
      label: 'Bird, wings in', group: 'Meadow', width: 44, height: 30,
      svg:
        '<path {{white}} d="M19 10l-8 2q-5 6-3 14l11-8z"/><path {{white}} d="M25 10l8 2q5 6 3 14l-11-8z"/>' +
        '<path {{dark-flat}} d="M9 17q-2 4-.5 8l4-3-.5-5.500z"/><path {{dark-flat}} d="M35 17q2 4 .5 8l-4-3 .5-5.500z"/>' +
        '<path {{white}} d="M22 20l-5 8h10z"/>' +
        '<ellipse {{white}} cx="22" cy="14" rx="5" ry="10"/>' +
        '<path {{sand-fine}} d="M19.500 5.500L22 1l2.500 4.500z"/>' +
        '<circle {{dark-flat}} cx="20" cy="9" r="1.100"/><circle {{dark-flat}} cx="24" cy="9" r="1.100"/>'
    },
    tree: {
      label: 'Tree', group: 'Meadow', width: 64, height: 64,
      svg:
        '<ellipse {{deep-flat}} cx="37" cy="40" rx="20" ry="17"/>' +
        sblob([[25, 22, 13], [38, 24, 12], [19, 34, 11], [31, 37, 13], [43, 35, 10]], 'leaf') +
        '<circle {{bush-flat}} cx="35" cy="38" r="5"/><circle {{bush-flat}} cx="26" cy="42" r="3.500"/><circle {{bush-flat}} cx="42" cy="32" r="3"/>' +
        '<path {{fine}} d="M19 22q3-4 7-1M32 21q3-4 7-1M15 34q3-4 7-1M26 32q3-4 7-1"/>'
    },
    hedge: {
      label: 'Hedge (one piece of a row)', group: 'Meadow', width: 28, height: 28,
      svg:
        sblob([[14, 14, 11], [9, 8, 6], [20, 9, 6], [8, 19, 6], [20, 20, 6]], 'bush') +
        '<circle {{deep-flat}} cx="18.500" cy="19" r="3.500"/>' +
        '<path {{fine}} d="M7 9.500q2.500-3 5-.5M15 13.500q2.500-3 5-.5M8 18.500q2.500-3 5-.5"/>'
    },
    fence: {
      label: 'Fence (one piece of a row)', group: 'Meadow', width: 12, height: 48,
      svg:
        '<rect {{wood-fine}} x="4.500" y="0" width="3" height="48"/>' +
        '<rect {{wood}} x="2" y="5.500" width="8" height="5" rx="1.500"/><rect {{wood}} x="2" y="21.500" width="8" height="5" rx="1.500"/><rect {{wood}} x="2" y="37.500" width="8" height="5" rx="1.500"/>'
    },
    farmhouse: {
      label: 'Farmhouse', group: 'Meadow', width: 84, height: 72,
      svg:
        '<rect {{roof}} x="54" y="28" width="26" height="26" rx="3"/>' +
        '<path {{line}} d="M60 41h12"/><path {{fine}} d="M72 41l8-13M72 41l8 13"/>' +
        '<rect {{roof}} x="4" y="8" width="56" height="56" rx="3"/>' +
        '<path {{line}} d="M32 24v24"/><path {{fine}} d="M32 24L4 8M32 24L60 8M32 48L4 64M32 48l28 16"/>' +
        '<rect {{stone}} x="11" y="31" width="10" height="10" rx="2"/><path {{fine}} d="M14 36h4"/>' +
        '<rect {{white-fine}} x="24" y="64" width="16" height="5" rx="1.500"/>'
    },
    barn: {
      label: 'Barn', group: 'Meadow', width: 104, height: 72,
      svg:
        '<rect {{wood}} x="20" y="56" width="32" height="12" rx="2"/><path {{fine}} d="M36 60v8"/>' +
        '<rect {{roof}} x="4" y="6" width="64" height="54" rx="3"/>' +
        '<path {{line}} d="M4 33h64"/>' +
        '<path {{fine}} d="M10 19.500h52M10 46.500h52"/>' +
        '<circle {{stone}} cx="85" cy="25" r="15"/><circle {{stone-fine}} cx="85" cy="25" r="8.500"/><circle {{dark-flat}} cx="85" cy="25" r="2.200"/>'
    },
    'crop-field': {
      label: 'Crop field (one tile)', group: 'Meadow', width: 48, height: 48,
      svg:
        '<rect {{wood-flat}} x="0" y="0" width="48" height="48"/>' +
        '<path {{fine-furrow}} d="M0 0v48M12 0v48M24 0v48M36 0v48M48 0v48"/>' +
        '<path {{line-leaf}} d="M6 3v42M18 3v42M30 3v42M42 3v42"/>'
    },

    /* ----- Airfield ----- */
    terminal: {
      label: 'Terminal', group: 'Airfield', width: 140, height: 96,
      svg:
        '<rect {{shade}} x="34" y="8" width="11" height="26" rx="2"/><circle {{shade}} cx="39.500" cy="9" r="6.500"/>' +
        '<rect {{shade}} x="95" y="8" width="11" height="26" rx="2"/><circle {{shade}} cx="100.500" cy="9" r="6.500"/>' +
        '<rect {{pane}} x="14" y="30" width="112" height="18" rx="4"/>' +
        '<path {{fine}} d="M28 30v12M42 30v12M56 30v12M70 30v12M84 30v12M98 30v12M112 30v12"/>' +
        '<rect {{white}} x="4" y="42" width="132" height="50" rx="4"/>' +
        '<rect {{white-fine}} x="10" y="48" width="120" height="38" rx="2.500"/>' +
        '<rect {{shade-fine}} x="24" y="60" width="14" height="14" rx="2"/><rect {{shade-fine}} x="63" y="60" width="14" height="14" rx="2"/><rect {{shade-fine}} x="102" y="60" width="14" height="14" rx="2"/>' +
        '<path {{fine}} d="M27 67h8M66 67h8M105 67h8"/>'
    },
    tower: {
      label: 'Control tower', group: 'Airfield', width: 56, height: 56,
      svg:
        '<rect {{white}} x="32" y="34" width="21" height="19" rx="3"/><path {{fine}} d="M38 47h9"/>' +
        '<rect {{white}} x="3" y="3" width="44" height="44" rx="5"/>' +
        '<path {{pane}} d="' + octagon(25, 25, 18) + '"/>' +
        '<path {{fine}} d="M25 8.500v4.500M25 37v4.500M8.500 25h4.500M37 25h4.500M12.300 12.300l3.900 3.900M33.800 33.800l3.900 3.900M37.700 12.300l-3.900 3.900M16.200 33.800l-3.900 3.900"/>' +
        '<path {{cab}} d="' + octagon(25, 25, 12.500) + '"/>' +
        '<path {{fine}} d="M25 25l7-7"/><circle {{dark-flat}} cx="25" cy="25" r="2.200"/>'
    },
    hangar: {
      label: 'Hangar', group: 'Airfield', width: 120, height: 88,
      svg:
        '<rect {{dark}} x="28" y="4" width="64" height="22" rx="2"/>' +
        '<rect {{white}} x="10" y="4" width="26" height="22" rx="2"/><rect {{white}} x="84" y="4" width="26" height="22" rx="2"/>' +
        '<path {{fine}} d="M23 4v15M97 4v15"/>' +
        '<rect {{shade}} x="4" y="20" width="112" height="64" rx="8"/>' +
        '<path {{fine}} d="M4 36q56-9 112 0M4 52q56-9 112 0M4 68q56-9 112 0"/>'
    },
    windsock: {
      label: 'Windsock', group: 'Airfield', width: 28, height: 64,
      svg:
        '<path {{sock}} d="M6.500 54L10.500 5h7l4 49z"/>' +
        '<path {{white-flat}} d="M7.300 44h13.400l-.8-10H8.100zM8.900 24h10.200l-.8-10H9.700z"/>' +
        '<path {{line}} d="M6.500 54L10.500 5h7l4 49z"/>' +
        '<path {{fine}} d="M7.300 44h13.400M8.100 34h11.800M8.900 24h10.200M9.700 14h8.600"/>' +
        '<circle {{white}} cx="14" cy="56" r="6"/><circle {{dark-flat}} cx="14" cy="56" r="2.500"/>'
    },
    car: {
      label: 'Car', group: 'Airfield', ground: 'apron', width: 28, height: 52,
      svg:
        '<rect {{dark-flat}} x="1.500" y="10" width="5" height="9" rx="2"/><rect {{dark-flat}} x="21.500" y="10" width="5" height="9" rx="2"/>' +
        '<rect {{dark-flat}} x="1.500" y="34" width="5" height="9" rx="2"/><rect {{dark-flat}} x="21.500" y="34" width="5" height="9" rx="2"/>' +
        '<rect {{blue}} x="4" y="3" width="20" height="46" rx="7"/>' +
        '<path {{pane-fine}} d="M7.500 19q6.500-4 13 0l-1.500 5h-10z"/><path {{pane-fine}} d="M9 36h10l1.500 5q-6.500 3-13 0z"/>' +
        '<rect {{blue-fine}} x="8" y="25" width="12" height="10" rx="2"/>' +
        '<path {{fine}} d="M9 7.500h10"/>'
    },
    'fuel-truck': {
      label: 'Fuel truck', group: 'Airfield', ground: 'apron', width: 32, height: 76,
      svg:
        '<rect {{dark-flat}} x="1.500" y="8" width="5" height="10" rx="2"/><rect {{dark-flat}} x="25.500" y="8" width="5" height="10" rx="2"/>' +
        '<rect {{dark-flat}} x="1.500" y="44" width="5" height="10" rx="2"/><rect {{dark-flat}} x="25.500" y="44" width="5" height="10" rx="2"/>' +
        '<rect {{dark-flat}} x="1.500" y="57" width="5" height="10" rx="2"/><rect {{dark-flat}} x="25.500" y="57" width="5" height="10" rx="2"/>' +
        '<rect {{blue}} x="5" y="3" width="22" height="19" rx="5"/>' +
        '<path {{pane-fine}} d="M8.500 12q7.500-4.500 15 0v4.500h-15z"/>' +
        '<rect {{shade}} x="4" y="25" width="24" height="48" rx="10"/>' +
        '<path {{fine}} d="M4 41h24M4 57h24"/>' +
        '<circle {{shade-fine}} cx="16" cy="33" r="3.500"/><circle {{shade-fine}} cx="16" cy="49" r="3.500"/><circle {{shade-fine}} cx="16" cy="65" r="3.500"/>'
    },
    'baggage-cart': {
      label: 'Baggage cart', group: 'Airfield', ground: 'apron', width: 26, height: 80,
      svg:
        '<path {{line}} d="M13 18v40"/>' +
        '<rect {{blue}} x="5" y="3" width="16" height="18" rx="4"/><rect {{white-fine}} x="8.500" y="10" width="9" height="7" rx="2"/><path {{fine}} d="M9 6.500h8"/>' +
        '<rect {{white}} x="3" y="27" width="20" height="22" rx="2.500"/>' +
        '<rect {{wood-fine}} x="6" y="30" width="14" height="7.500" rx="1.500"/><rect {{roof-fine}} x="6" y="39" width="14" height="7.500" rx="1.500"/>' +
        '<rect {{white}} x="3" y="55" width="20" height="22" rx="2.500"/>' +
        '<rect {{roof-fine}} x="6" y="58" width="7" height="16" rx="1.500"/><rect {{wood-fine}} x="14" y="58" width="6" height="16" rx="1.500"/>'
    },
    'parked-aircraft': {
      label: 'Parked aircraft', group: 'Airfield', ground: 'apron', width: 72, height: 72,
      svg:
        '<rect {{shade}} x="20" y="54" width="32" height="11" rx="3"/>' +
        '<rect {{shade}} x="5" y="23" width="62" height="17" rx="3.500"/>' +
        '<path {{shade}} d="M36 7c7 0 9.500 9 9.500 20l-1.500 24c-1 8-4 13-8 15-4-2-7-7-8-15l-1.500-24c0-11 2.500-20 9.500-20z"/>' +
        '<path {{glass-fine}} d="M29.500 23q6.500-7 13 0v-5q-6.500-6-13 0z"/>' +
        '<path {{fine}} d="M14 23v17M58 23v17M36 44v14"/>' +
        '<rect {{dark-flat}} x="24" y="4" width="24" height="4" rx="2"/><circle {{dark-flat}} cx="36" cy="7.500" r="3.200"/>'
    },

    /* ----- Island ----- */
    palm: { label: 'Palm', group: 'Island', ground: 'sand', width: 64, height: 64, svg: palmSvg() },
    jetty: {
      label: 'Jetty', group: 'Island', ground: 'water', width: 28, height: 96,
      svg:
        '<rect {{wood}} x="5" y="3" width="18" height="90" rx="2"/>' +
        '<path {{fine}} d="M5 13h18M5 23h18M5 33h18M5 43h18M5 53h18M5 63h18M5 73h18M5 83h18"/>' +
        '<circle {{furrow-fine}} cx="5" cy="8" r="3"/><circle {{furrow-fine}} cx="23" cy="8" r="3"/><circle {{furrow-fine}} cx="5" cy="38" r="3"/><circle {{furrow-fine}} cx="23" cy="38" r="3"/><circle {{furrow-fine}} cx="5" cy="68" r="3"/><circle {{furrow-fine}} cx="23" cy="68" r="3"/>'
    },
    sailboat: {
      label: 'Sailboat', group: 'Island', ground: 'water', width: 38, height: 76,
      svg:
        '<path {{wood}} d="M14 3q10 14 10 34v28q0 8-10 8t-10-8V37q0-20 10-34z"/>' +
        '<path {{sand-fine}} d="M14 13q6 11 6 24v26q0 5-6 5t-6-5V37q0-13 6-24z"/>' +
        '<path {{fine}} d="M10 62h8"/>' +
        '<path {{white}} d="M14 20q22 12 21 38H14z"/>' +
        '<path {{fine}} d="M14 58V20"/>' +
        '<circle {{dark-flat}} cx="14" cy="20" r="2.500"/>'
    },
    yacht: {
      label: 'Yacht', group: 'Island', ground: 'water', width: 32, height: 92,
      svg:
        '<path {{white}} d="M16 3q12 16 12 40v36q0 10-12 10T4 79V43Q4 19 16 3z"/>' +
        '<path {{sand-fine}} d="M16 15q7 12 7 28v35q0 6-7 6t-7-6V43q0-16 7-28z"/>' +
        '<path {{fine}} d="M11 68h10M11 73h10M11 78h10"/>' +
        '<rect {{blue}} x="9" y="36" width="14" height="26" rx="4"/>' +
        '<path {{pane-fine}} d="M10.500 35q5.500-5 11 0l-1 6h-9z"/>' +
        '<path {{fine}} d="M12 50h8M12 56h8"/>'
    },
    'container-vessel': {
      label: 'Container vessel', group: 'Island', ground: 'water', width: 48, height: 176,
      svg:
        '<path {{hull}} d="M24 3q20 18 20 42v118q0 10-10 10H14q-10 0-10-10V45Q4 21 24 3z"/>' +
        '<path {{fine}} d="M24 12v22"/>' +
        containers() +
        '<rect {{white}} x="9" y="142" width="30" height="22" rx="3"/>' +
        '<path {{fine}} d="M12 147.500h24M18 156h12"/>'
    },
    wake: {
      label: 'Wake', group: 'Island', ground: 'water', width: 48, height: 64,
      svg:
        '<path {{line-foam}} d="M19 4Q12 30 4 60M29 4q7 26 15 56"/>' +
        '<path {{fine-foam}} d="M24 10v10M17 34q7 5 14 0M13 50q11 6 22 0"/>' +
        '<circle {{foam-flat}} cx="24" cy="26" r="2.500"/><circle {{foam-flat}} cx="18" cy="43" r="2.500"/><circle {{foam-flat}} cx="30" cy="43" r="2.500"/>'
    },
    wave: {
      label: 'Wave', group: 'Island', ground: 'water', width: 44, height: 20,
      svg: '<path {{line-foam}} d="M3 8q6.500-9 13 0t13 0 12 0"/><path {{fine-foam}} d="M12 17q5-6 10 0t10 0"/>'
    },

    /* ----- Savanna ----- */
    acacia: {
      label: 'Acacia', group: 'Savanna', width: 92, height: 64,
      svg:
        sblob([[17, 35, 13], [33, 28, 18], [52, 26, 20], [70, 30, 16], [79, 38, 10], [45, 42, 16], [27, 44, 11], [64, 44, 12]], 'deep') +
        '<circle {{bush-flat}} cx="26" cy="27" r="7"/><circle {{bush-flat}} cx="55" cy="20" r="8"/><circle {{bush-flat}} cx="72" cy="33" r="6"/><circle {{bush-flat}} cx="36" cy="46" r="6"/><circle {{bush-flat}} cx="60" cy="45" r="6"/>' +
        '<path {{fine}} d="M46 34L30 30M46 34l-7 9M46 34l5-11M46 34l18-2M46 34l11 8"/>' +
        '<circle {{wood-fine}} cx="46" cy="34" r="4.500"/>'
    },
    bush: {
      label: 'Bush', group: 'Savanna', width: 40, height: 36,
      svg:
        sblob([[13, 21, 10], [23, 13, 10], [28, 24, 9]], 'leaf') +
        '<circle {{bush-flat}} cx="22" cy="12" r="3"/><circle {{bush-flat}} cx="12" cy="20" r="3"/><circle {{bush-flat}} cx="29" cy="24" r="3"/>' +
        '<path {{fine}} d="M15 15q2.500-3 5-.5M19 24q2.500-3 5-.5"/>'
    },
    rock: {
      label: 'Rock', group: 'Savanna', width: 48, height: 36,
      svg:
        '<path {{stone}} d="M6 22q-3-11 8-15l10-3q11-1 13 9 3 11-8 14l-12 2q-9 1-11-7z"/>' +
        '<path {{fine}} d="M15 11l6 6-2 7M27 8l2 7"/>' +
        '<path {{stone}} d="M33 28q0-6 6-6t6 5-6 6-6-5z"/>'
    },
    zebra: {
      label: 'Zebra', group: 'Savanna', width: 32, height: 68,
      svg:
        '<path {{line}} d="M16 59q0 4-2 6"/><circle {{dark-flat}} cx="13.500" cy="65" r="1.800"/>' +
        '<rect {{white}} x="1.500" y="32" width="10" height="6" rx="3"/><rect {{white}} x="20.500" y="32" width="10" height="6" rx="3"/>' +
        '<rect {{white}} x="1.500" y="50" width="10" height="6" rx="3"/><rect {{white}} x="20.500" y="50" width="10" height="6" rx="3"/>' +
        '<ellipse {{white}} cx="16" cy="44" rx="9.500" ry="16"/>' +
        '<path {{line}} d="M9.500 36q6.500 3 13 0M7.500 42q8.500 3 17 0M7.500 48q8.500 3 17 0M9.500 54q6.500 3 13 0"/>' +
        '<rect {{white}} x="11" y="14" width="10" height="18" rx="4"/>' +
        '<ellipse {{white-fine}} cx="10" cy="14" rx="2.200" ry="3.500"/><ellipse {{white-fine}} cx="22" cy="14" rx="2.200" ry="3.500"/>' +
        '<ellipse {{white}} cx="16" cy="11" rx="5.500" ry="9"/>' +
        '<rect {{dark-flat}} x="14.500" y="12" width="3" height="18" rx="1.500"/>' +
        '<ellipse {{dark-flat}} cx="16" cy="4.500" rx="3.800" ry="2.800"/>'
    },
    elephant: {
      label: 'Elephant', group: 'Savanna', width: 64, height: 84,
      svg:
        '<path {{line}} d="M32 74q0 5 3 7"/>' +
        '<circle {{stone}} cx="15" cy="44" r="6"/><circle {{stone}} cx="49" cy="44" r="6"/><circle {{stone}} cx="16" cy="64" r="6"/><circle {{stone}} cx="48" cy="64" r="6"/>' +
        '<ellipse {{stone}} cx="32" cy="54" rx="18" ry="23"/>' +
        '<path {{fine}} d="M22 60q10 4 20 0M24 68q8 3 16 0"/>' +
        '<ellipse {{stone}} cx="14" cy="30" rx="11" ry="14"/><ellipse {{stone}} cx="50" cy="30" rx="11" ry="14"/>' +
        '<path {{fine}} d="M10 24q5 6 2 13M54 24q-5 6-2 13"/>' +
        '<rect {{stone}} x="28" y="3" width="8" height="22" rx="4"/>' +
        '<ellipse {{white-fine}} cx="24.500" cy="14" rx="2.200" ry="6"/><ellipse {{white-fine}} cx="39.500" cy="14" rx="2.200" ry="6"/>' +
        '<circle {{stone}} cx="32" cy="28" r="13"/>' +
        '<path {{fine}} d="M29 8h6M29 12.500h6M29 17h6"/>' +
        '<circle {{dark-flat}} cx="26" cy="25" r="1.500"/><circle {{dark-flat}} cx="38" cy="25" r="1.500"/>'
    }
  };

  /* Paint for a page: fills and strokes read the token variables, so the theme applies. */
  function sceneryCss(key) {
    var p = sceneryPaint(key), s = 'fill:' + (p.fill ? 'var(--' + p.fill + ')' : 'none') + ';';
    s += p.stroke ? 'stroke:var(--' + p.strokeColor + ');stroke-width:var(--' + SCENERY_STROKE[p.stroke] + ');stroke-linejoin:round;stroke-linecap:round' : 'stroke:none';
    return 'style="' + s + '"';
  }
  function scenerySvg(name, painter) {
    var a = SCENERY[name] || SCENERY.tree;
    return '<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 ' + a.width + ' ' + a.height + '" aria-hidden="true">' + paint(a.svg, painter || sceneryCss) + '</svg>';
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

  /* Runway: drawn left to right, length x 64. numbers = "09/27". */
  function runwaySvg(length, numbers, oneWay) {
    var L = Math.max(200, +length || 360), W = 64, n = String(numbers || '09/27').split('/'), s = '';
    s += '<rect class="fa-zone" x="5" y="5" width="' + (L + 6) + '" height="' + (W + 6) + '" rx="13"/>';
    s += '<rect {{asphalt}} x="8" y="8" width="' + L + '" height="' + W + '" rx="10"/>';
    if (!oneWay) s += '<rect {{tyre}} x="74" y="26" width="46" height="28" rx="8"/>';
    s += '<rect {{tyre}} x="' + (L - 104) + '" y="26" width="46" height="28" rx="8"/>';
    for (var e = oneWay ? 1 : 0; e < 2; e++) for (var b = 0; b < 4; b++)
      s += '<rect {{mark}} x="' + (e ? L - 8 : 18) + '" y="' + (16 + b * 13) + '" width="6" height="9" rx="2"/>';
    if (oneWay) for (var c = 134; c < L - 132; c += 40) s += '<path {{chev}} d="M' + (c + 22) + ' 31l-12 9l12 9"/>';
    else for (var x = 134; x < L - 132; x += 40) s += '<rect {{mark}} x="' + x + '" y="37" width="24" height="6" rx="3"/>';
    if (!oneWay) s += '<text class="fa-p-num" transform="translate(48 40) rotate(90)" text-anchor="middle" dominant-baseline="central">' + (n[0] || '') + '</text>';
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
    cloudSvg: cloudSvg, windsockSvg: windsockSvg, burstSvg: burstSvg,
    SCENERY: SCENERY, SCENERY_FILL: SCENERY_FILL, SCENERY_STROKE: SCENERY_STROKE, SCENERY_MAX_FILLS: SCENERY_MAX_FILLS,
    SCENERY_BANNED: SCENERY_BANNED, SCENERY_ACCENT: SCENERY_ACCENT, sceneryPaint: sceneryPaint, scenerySvg: scenerySvg
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
  define('fa-runway', ['length', 'numbers', 'mode'], function (el) {
    var L = Math.max(200, parseFloat(el.getAttribute('length')) || 360);
    el.style.setProperty('--fa-length', (L + 16) + 'px');
    el.innerHTML = runwaySvg(L, el.getAttribute('numbers'), el.getAttribute('mode') === 'one-way');
  });
  define('fa-helipad', [], function (el) { el.innerHTML = helipadSvg(); });
  define('fa-cloud', [], function (el) { el.innerHTML = cloudSvg(); });
  define('fa-windsock', [], function (el) { el.innerHTML = windsockSvg(); });
  define('fa-burst', ['kind'], function (el) { el.innerHTML = burstSvg(el.getAttribute('kind')); });
  define('fa-scenery', ['name', 'size'], function (el) {
    var name = el.getAttribute('name'), a = SCENERY[name] || SCENERY.tree, k = parseFloat(el.getAttribute('size')) || 1;
    el.style.width = (a.width * k) + 'px';
    el.style.height = (a.height * k) + 'px';
    el.setAttribute('role', 'img');
    el.setAttribute('aria-label', a.label);
    el.innerHTML = scenerySvg(name);
  });
})();
