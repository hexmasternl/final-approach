/* The v1 scenery sprites. Design system v2 replaced them in bundle.js; this copy only feeds
   tools/gen-kotlin.cjs, which still generates ui/sprites/ScenerySprites.kt from it so the app's
   world keeps its v1 look until it is ported. Do not use it for anything new, and delete it with
   that port. */
(function () {
  'use strict';
  function paint(svg, painter) {
    return svg.replace(/\{\{([a-z-]+)\}\}/g, function (_, k) { return painter(k); });
  }

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

  module.exports = {
    SCENERY: SCENERY, SCENERY_FILL: SCENERY_FILL, SCENERY_STROKE: SCENERY_STROKE, SCENERY_MAX_FILLS: SCENERY_MAX_FILLS,
    SCENERY_BANNED: SCENERY_BANNED, SCENERY_ACCENT: SCENERY_ACCENT, sceneryPaint: sceneryPaint, scenerySvg: scenerySvg
  };
})();
