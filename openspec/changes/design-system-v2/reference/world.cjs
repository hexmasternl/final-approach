// World kit for the Final Approach v2 proposal: sprites and the Meadow Field scene.
const fs = require('fs');
const path = require('path');
const DS = (process.argv[2] && !process.argv[2].startsWith('--')) ? process.argv[2] : path.join(__dirname, '..', '..', '..', '..', 'docs', 'design-system');
const rd = p => fs.readFileSync(path.join(DS, p), 'utf8');
const inner = svg => svg.replace(/^<svg[^>]*>/, '').replace(/<\/svg>\s*$/, '');

function mulberry(a) { return () => { a |= 0; a = a + 0x6D2B79F5 | 0; let t = Math.imul(a ^ a >>> 15, 1 | a); t = t + Math.imul(t ^ t >>> 7, 61 | t) ^ t; return ((t ^ t >>> 14) >>> 0) / 4294967296; }; }
const n = v => Math.round(v * 10) / 10;
const c = t => (!t || t === 'none') ? 'none' : (t[0] === '#' || t.startsWith('rgb') || t.startsWith('var') ? t : `var(--${t})`);
const st = (fill, stroke, sw, extra = '') => ` style="fill:${c(fill)};${stroke ? `stroke:${c(stroke)};stroke-width:${sw};` : ''}${extra}"`;
const O = 'outline';

function lobes(cx, cy, r, k, rng, jit = 0.1, b = 0.62) {
  const pts = [];
  for (let i = 0; i < k; i++) {
    const a = (i + (rng() - 0.5) * 0.4) / k * Math.PI * 2 - Math.PI / 2;
    const rr = r * (1 + (rng() - 0.5) * 2 * jit);
    pts.push([cx + Math.cos(a) * rr, cy + Math.sin(a) * rr]);
  }
  let d = `M${n(pts[0][0])} ${n(pts[0][1])}`;
  for (let i = 1; i <= k; i++) {
    const p = pts[i % k], q = pts[i - 1];
    const ar = n(Math.hypot(p[0] - q[0], p[1] - q[1]) * b);
    d += `A${ar} ${ar} 0 0 1 ${n(p[0])} ${n(p[1])}`;
  }
  return d + 'Z';
}
function smooth(pts, closed = true) {
  const L = pts.length;
  const g = i => closed ? pts[(i + L) % L] : pts[Math.max(0, Math.min(L - 1, i))];
  let d = `M${n(pts[0][0])} ${n(pts[0][1])}`;
  const m = closed ? L : L - 1;
  for (let i = 0; i < m; i++) {
    const p0 = g(i - 1), p1 = g(i), p2 = g(i + 1), p3 = g(i + 2);
    d += `C${n(p1[0] + (p2[0] - p0[0]) / 6)} ${n(p1[1] + (p2[1] - p0[1]) / 6)} ${n(p2[0] - (p3[0] - p1[0]) / 6)} ${n(p2[1] - (p3[1] - p1[1]) / 6)} ${n(p2[0])} ${n(p2[1])}`;
  }
  return d + (closed ? 'Z' : '');
}
function organic(cx, cy, rx, ry, k, rng, jit = 0.16) {
  const pts = [];
  for (let i = 0; i < k; i++) {
    const a = i / k * Math.PI * 2;
    const j = 1 + (rng() - 0.5) * 2 * jit;
    pts.push([cx + Math.cos(a) * rx * j, cy + Math.sin(a) * ry * j]);
  }
  return pts;
}
function star(cx, cy, r, k, inr, rot = 0) {
  let d = '';
  for (let i = 0; i < k * 2; i++) {
    const a = i / (k * 2) * Math.PI * 2 + rot;
    const rr = i % 2 ? r * inr : r;
    d += (i ? 'L' : 'M') + n(cx + Math.cos(a) * rr) + ' ' + n(cy + Math.sin(a) * rr);
  }
  return d + 'Z';
}
const oct = (cx, cy, r) => Array.from({ length: 8 }, (_, i) => { const a = (22.5 + 45 * i) * Math.PI / 180; return [n(cx + Math.cos(a) * r), n(cy + Math.sin(a) * r)]; });
const poly = pts => pts.map(p => p.join(',')).join(' ');
// Cast shadow of a box with footprint x0..x1, y0..y1 and height offset (dx, dy).
const boxShadow = (x0, y0, x1, y1, dx, dy) => `<polygon points="${poly([[x1, y0], [x1 + dx, y0 + dy], [x1 + dx, y1 + dy], [x0 + dx, y1 + dy], [x0, y1]])}"/>`;

// ---------------------------------------------------------------- sprites
// Each sprite: body markup, shadow silhouette, shadow offset [dx, dy] (0 when baked), tile size for the sheet.
const SPR = {};
const add = (id, o) => { SPR[id] = o; };

function tree(id, r, k, seed) {
  const rng = mulberry(seed);
  const C = lobes(0, -4, r, k, rng);
  const H = lobes(-r * 0.3, -4 - r * 0.36, r * 0.42, 5, rng, 0.08);
  add(id, {
    off: [r * 0.42, r * 0.5], tile: [r * 2 + 34, r * 2 + 34],
    sh: `<ellipse cx="0" cy="-2" rx="${r}" ry="${n(r * 0.94)}"/>`,
    body: `<rect x="-4" y="${r - 12}" width="8" height="16" rx="3"${st('wood-500', O, 2.2)}/><rect x="0.5" y="${r - 11}" width="2.5" height="14" rx="1.2"${st('wood-700')}/>
<clipPath id="cp-${id}"><path d="${C}"/></clipPath><path d="${C}"${st('leaf-700')}/>
<g clip-path="url(#cp-${id})"><path d="${C}" transform="translate(${n(-r * 0.17)},${n(-r * 0.2)})"${st('leaf-500')}/><path d="${H}"${st('leaf-300')}/></g>
<path d="M${n(-r * 0.12)} ${n(r * 0.1)}q${n(r * 0.14)} ${n(-r * 0.2)} ${n(r * 0.36)} ${n(-r * 0.04)}M${n(r * 0.18)} ${n(-r * 0.44)}q${n(r * 0.12)} ${n(-r * 0.16)} ${n(r * 0.3)} ${n(-r * 0.02)}M${n(-r * 0.56)} ${n(r * 0.02)}q${n(r * 0.1)} ${n(-r * 0.16)} ${n(r * 0.26)} ${n(-r * 0.03)}"${st('none', 'leaf-900', 1.3)}/>
<path d="${C}"${st('none', O, 2.5)}/>`,
  });
}
tree('tree-a', 26, 9, 11);
tree('tree-b', 21, 8, 29);
tree('tree-c', 30, 10, 47);

function pine(id, r, seed) {
  const rot = seed * 0.37;
  add(id, {
    off: [r * 0.5, r * 0.6], tile: [r * 2 + 36, r * 2 + 36],
    sh: `<path d="${star(0, 0, r, 9, 0.74, rot)}"/>`,
    body: `<path d="${star(0, 0, r, 9, 0.72, rot)}"${st('pine-700', O, 2.5)}/><path d="${star(-r * 0.1, -r * 0.13, r * 0.68, 9, 0.7, rot + 0.3)}"${st('pine-500', 'pine-900', 1.2)}/><path d="${star(-r * 0.19, -r * 0.26, r * 0.36, 7, 0.66, rot + 0.6)}"${st('pine-300', 'pine-900', 1.1)}/>`,
  });
}
pine('pine-a', 23, 1); pine('pine-b', 18, 2);

(function bush() {
  const rng = mulberry(5);
  const C = lobes(0, 0, 11, 6, rng, 0.12);
  add('bush', {
    off: [3, 4], tile: [56, 56], sh: `<circle r="11"/>`,
    body: `<clipPath id="cp-bush"><path d="${C}"/></clipPath><path d="${C}"${st('leaf-700')}/><g clip-path="url(#cp-bush)"><path d="${C}" transform="translate(-2.5,-3)"${st('leaf-500')}/><circle cx="-4" cy="-5" r="4"${st('leaf-300')}/></g><path d="${C}"${st('none', O, 2.2)}/><circle cx="3" cy="-1" r="1.5"${st('wall-100')}/><circle cx="-2" cy="4" r="1.5"${st('wall-100')}/><circle cx="6" cy="5" r="1.3"${st('wall-100')}/>`,
  });
})();

add('rock', {
  off: [3, 4], tile: [64, 56],
  sh: `<polygon points="-13,2 -9,-9 3,-12 12,-5 14,6 5,11 -7,10"/><circle cx="14" cy="9" r="5"/>`,
  body: `<polygon points="-13,2 -9,-9 3,-12 12,-5 14,6 5,11 -7,10"${st('stone-500', O, 2.2)}/><polygon points="-9,-9 3,-12 5,-3 -6,-1"${st('stone-300')}/><polygon points="5,-3 12,-5 14,6 5,11 3,3"${st('stone-700')}/><polygon points="-13,2 -9,-9 3,-12 12,-5 14,6 5,11 -7,10"${st('none', O, 2.2)}/><path d="M-6 -1l-3 6"${st('none', 'stone-700', 1.2)}/><circle cx="14" cy="9" r="4.5"${st('stone-300', O, 2)}/>`,
});

add('farmhouse', {
  off: [0, 0], tile: [150, 126],
  sh: boxShadow(-46, -38, 46, 30, 12, 14),
  body: `<rect x="-11" y="30" width="22" height="5" rx="1.5"${st('stone-300', 'stone-700', 1.2)}/>
<rect x="-42" y="10" width="84" height="20"${st('wall-100', O, 2.5)}/><rect x="28" y="11.5" width="12.5" height="17"${st('wall-300')}/><rect x="-40.7" y="14" width="81.4" height="3.5"${st('wall-500')}/>
<rect x="-6" y="17" width="12" height="13" rx="2"${st('wood-500', 'wood-900', 1.3)}/><circle cx="3" cy="24" r="1"${st('straw-300')}/>
<rect x="-36" y="17" width="3" height="9"${st('pine-500', 'pine-900', 1)}/><rect x="-21" y="17" width="3" height="9"${st('pine-500', 'pine-900', 1)}/><rect x="-33" y="17" width="12" height="9" rx="1"${st('window', 'wood-900', 1.2)}/><path d="M-27 17v9M-33 21.500h12"${st('none', 'wood-900', 1)}/>
<rect x="18" y="17" width="3" height="9"${st('pine-500', 'pine-900', 1)}/><rect x="33" y="17" width="3" height="9"${st('pine-500', 'pine-900', 1)}/><rect x="21" y="17" width="12" height="9" rx="1"${st('window', 'wood-900', 1.2)}/><path d="M27 17v9M21 21.500h12"${st('none', 'wood-900', 1)}/>
<rect x="-34" y="26.500" width="14" height="2.6" rx="1"${st('wood-700')}/><circle cx="-31" cy="26" r="1.3"${st('muzzle')}/><circle cx="-27" cy="25.600" r="1.3"${st('wall-100')}/><circle cx="-23" cy="26" r="1.3"${st('muzzle')}/>
<rect x="-46" y="-38" width="92" height="52" rx="3"${st('roof-500')}/><path d="M-46 -12v-23a3 3 0 0 1 3-3h86a3 3 0 0 1 3 3v23z"${st('roof-300')}/>
<path d="M-46 -30h92M-46 -21h92"${st('none', 'roof-500', 1.2)}/><path d="M-46 -3h92M-46 6h92"${st('none', 'roof-700', 1.2)}/>
<path d="M-34 -38v8M-12 -38v8M10 -38v8M32 -38v8M-23 -30v9M-1 -30v9M21 -30v9M-34 -21v9M-12 -21v9M10 -21v9M32 -21v9"${st('none', 'roof-500', 1)}/><path d="M-23 -12v9M-1 -12v9M21 -12v9M-34 -3v9M-12 -3v9M10 -3v9M32 -3v9M-23 6v8M-1 6v8M21 6v8"${st('none', 'roof-700', 1)}/>
<path d="M-46 -12h92"${st('none', 'roof-900', 1.8)}/>
<path d="M30 -24l9 10h-11l-9 -10z"${st('roof-700')}/><rect x="19" y="-35" width="11" height="11" rx="1.500"${st('stone-300', O, 2)}/><rect x="22" y="-32" width="5" height="5" rx="1"${st('asphalt-900')}/>
<rect x="-46" y="-38" width="92" height="52" rx="3"${st('none', O, 2.5)}/>
<rect x="-12" y="9" width="24" height="7" rx="2"${st('roof-700', O, 2)}/>`,
});

add('barn', {
  off: [0, 0], tile: [138, 126],
  sh: boxShadow(-40, -44, 40, 30, 14, 16),
  body: `<rect x="-36" y="8" width="72" height="22"${st('roof-500', O, 2.5)}/><rect x="23" y="9.5" width="11.500" height="19"${st('roof-700')}/><rect x="-34.700" y="12" width="69.400" height="3"${st('roof-700')}/>
<rect x="-13" y="14" width="26" height="16" rx="1"${st('wall-100', 'roof-900', 1.3)}/><path d="M0 14v16M-13 14l13 16M0 14l-13 16M0 14l13 16M13 14l-13 16"${st('none', 'roof-700', 1.3)}/>
<clipPath id="cp-barn"><rect x="-40" y="-44" width="80" height="56" rx="3"/></clipPath>
<g clip-path="url(#cp-barn)"><rect x="-40" y="-44" width="80" height="13"${st('tin-500')}/><rect x="-40" y="-31" width="80" height="17"${st('tin-100')}/><rect x="-40" y="-14" width="80" height="16"${st('tin-300')}/><rect x="-40" y="2" width="80" height="10"${st('tin-700')}/>
<path d="M-28 -44v56M-16 -44v56M-4 -44v56M8 -44v56M20 -44v56M32 -44v56"${st('none', 'tin-700', 1)} opacity="0.55"/></g>
<path d="M-40 -31h80M-40 2h80"${st('none', 'tin-700', 1.2)}/><path d="M-40 -14h80"${st('none', 'tin-900', 1.8)}/>
<rect x="-40" y="-44" width="80" height="56" rx="3"${st('none', O, 2.5)}/>`,
});

add('silo', {
  off: [13, 15], tile: [76, 76], sh: `<circle r="14"/>`,
  body: `<clipPath id="cp-silo"><circle r="14"/></clipPath><circle r="14"${st('tin-700')}/><g clip-path="url(#cp-silo)"><circle cx="-3" cy="-3.500" r="14"${st('tin-300')}/><circle cx="-6" cy="-7" r="9"${st('tin-100')}/></g><path d="M0 -14v28M-14 0h28M-10 -10l20 20M10 -10l-20 20"${st('none', 'tin-700', 0.9)} opacity="0.6"/><circle r="14"${st('none', O, 2.5)}/><circle r="4.500"${st('tin-500', 'tin-900', 1.3)}/>`,
});

(function tower() {
  const o1 = oct(0, -30, 22), o2 = oct(0, -37, 22), o3 = oct(0, -37, 14);
  const mull = o2.map((p, i) => `M${o3[i][0]} ${o3[i][1]}L${p[0]} ${p[1]}`).join('');
  add('tower', {
    off: [0, 0], tile: [132, 150],
    sh: `${boxShadow(-24, -6, 24, 42, 12, 14)}<polygon points="${poly(oct(30, 0, 22))}"/><polygon points="9,-30 39,0 21,18 -9,-12"/>`,
    body: `<rect x="-22" y="28" width="44" height="14"${st('wall-100', O, 2.5)}/><rect x="10" y="29.500" width="10.500" height="11"${st('wall-300')}/><rect x="-5" y="31" width="10" height="11" rx="1.500"${st('wood-500', 'wood-900', 1.3)}/><rect x="-17" y="32" width="7" height="5" rx="1"${st('window', 'wood-900', 1.1)}/>
<rect x="-24" y="-6" width="48" height="36" rx="3"${st('tin-300', O, 2.5)}/><rect x="12" y="-4.700" width="10.700" height="33.400"${st('tin-500')}/><rect x="-18" y="14" width="9" height="9" rx="1.500"${st('tin-500', 'tin-900', 1.2)}/><circle cx="-13.500" cy="18.500" r="2.500"${st('tin-700')}/>
<rect x="-9" y="-28" width="18" height="34"${st('wall-100', O, 2.5)}/><rect x="2" y="-26.700" width="5.700" height="31.400"${st('wall-300')}/><rect x="-5.500" y="-10" width="4" height="6" rx="1"${st('window', 'wood-900', 1)}/>
<polygon points="${poly(o1)}"${st('glass-700', O, 2.5)}/><polygon points="${poly(o2)}"${st('glass', O, 2.5)}/><path d="${mull}"${st('none', 'glass-700', 1.3)}/><path d="M-17 -43l5 -8"${st('none', '#ffffff', 2.2)}/>
<polygon points="${poly(o3)}"${st('cherry-500', O, 2.2)}/><polygon points="${poly([o3[4], o3[5], o3[6], [0, -37]])}"${st('cherry-200')}/><polygon points="${poly(o3)}"${st('none', O, 2.2)}/>
<path d="M0 -37l8 -14"${st('none', O, 2)}/><circle cx="8" cy="-51" r="1.800"${st(O)}/><circle cx="0" cy="-37" r="3.400"${st('beacon', O, 1.500)}/>`,
  });
})();

add('terminal', {
  off: [0, 0], tile: [206, 126],
  sh: boxShadow(-76, -34, 76, 36, 12, 14),
  body: `<rect x="-82" y="32" width="164" height="10" rx="3"${st('concrete-300', 'concrete-700', 1.2)}/>
<rect x="-72" y="12" width="144" height="22"${st('glass', O, 2.5)}/><path d="M-60 12v22M-48 12v22M-36 12v22M-24 12v22M24 12v22M36 12v22M48 12v22M60 12v22"${st('none', 'glass-700', 1.3)}/><path d="M-70 23h52M18 23h52"${st('none', 'glass-700', 1.1)}/><path d="M-56 30l8 -12M-51 30l8 -12M40 30l8 -12"${st('none', '#ffffff', 1.800)} opacity="0.7"/>
<rect x="-70.700" y="30" width="141.400" height="3"${st('tin-700')}/>
<rect x="-9" y="20" width="18" height="14" rx="1"${st('glass-700', O, 1.800)}/><path d="M0 20v14"${st('none', O, 1.300)}/>
<rect x="-17" y="13" width="34" height="8" rx="2"${st('roof-500', O, 2)}/><path d="M-10 13v8M-3 13v8M4 13v8M11 13v8"${st('none', 'wall-100', 2.200)}/><rect x="-17" y="13" width="34" height="8" rx="2"${st('none', O, 2)}/>
<rect x="-76" y="-34" width="152" height="48" rx="4"${st('tin-300')}/><rect x="60" y="-34" width="16" height="48"${st('tin-500')}/><rect x="-76" y="5" width="152" height="9"${st('tin-500')}/><path d="M-38 -34v39M0 -34v39M38 -34v39"${st('none', 'tin-500', 1.200)}/>
<rect x="-76" y="-34" width="152" height="48" rx="4"${st('none', O, 2.5)}/>
<rect x="-52" y="-27" width="104" height="13" rx="6.500"${st('glass', O, 2)}/><path d="M-39 -27v13M-26 -27v13M-13 -27v13M0 -27v13M13 -27v13M26 -27v13M39 -27v13"${st('none', 'glass-700', 1.200)}/><path d="M-46 -17l5 -7"${st('none', '#ffffff', 1.800)}/>
<rect x="-68" y="-8" width="12" height="10" rx="2"${st('tin-500', 'tin-900', 1.200)}/><circle cx="-62" cy="-3" r="3"${st('tin-700')}/><rect x="52" y="-9" width="12" height="10" rx="2"${st('tin-700', 'tin-900', 1.200)}/>
<rect x="-27" y="-8" width="54" height="10" rx="3"${st(O)}/><path d="M-21 -3h5M-13 -3h3M-7 -3h6M2 -3h4M9 -3h5M17 -3h4"${st('none', 'wall-100', 2.400)}/>`,
});

add('hangar', {
  off: [0, 0], tile: [166, 140],
  sh: boxShadow(-56, -40, 56, 44, 13, 15),
  body: `<rect x="-54" y="22" width="108" height="22"${st('tin-500', O, 2.5)}/><rect x="38" y="23.500" width="14.500" height="19"${st('tin-700')}/>
<rect x="-34" y="27" width="68" height="17"${st('asphalt-900')}/><path d="M-3 44C-3 33 15 33 15 44Z"${st('tin-100', O, 1.600)}/><path d="M2 39q4 -3.500 8 0"${st('none', 'glass', 2.200)}/>
<rect x="-34" y="27" width="27" height="17"${st('tin-300', 'tin-900', 1.200)}/><path d="M-27 27v17M-20 27v17M-13 27v17"${st('none', 'tin-700', 1)}/><rect x="-34" y="27" width="68" height="17"${st('none', O, 1.800)}/>
<clipPath id="cp-hangar"><rect x="-56" y="-40" width="112" height="66" rx="10"/></clipPath>
<g clip-path="url(#cp-hangar)"><rect x="-56" y="-40" width="112" height="66"${st('tin-300')}/><rect x="-56" y="-40" width="26" height="66"${st('tin-100')}/><rect x="24" y="-40" width="32" height="66"${st('tin-500')}/><rect x="-56" y="17" width="112" height="9"${st('tin-700')} opacity="0.5"/>
<path d="M-56 -29h112M-56 -18h112M-56 -7h112M-56 4h112M-56 15h112"${st('none', 'tin-700', 1.100)} opacity="0.7"/></g>
<rect x="-56" y="-40" width="112" height="66" rx="10"${st('none', O, 2.5)}/>
<circle cx="-4" cy="-24" r="3.500"${st('tin-500', 'tin-900', 1.200)}/><circle cx="-4" cy="2" r="3.500"${st('tin-500', 'tin-900', 1.200)}/>`,
});

add('cow', {
  off: [4, 5], tile: [64, 86],
  sh: `<ellipse cx="0" cy="2" rx="11.500" ry="19"/><ellipse cx="0" cy="-19" rx="7" ry="8"/>`,
  body: `<path d="M0 17q-1.500 6 3 9"${st('none', O, 2)}/><circle cx="3.400" cy="26.500" r="2.300"${st('hide-900')}/>
<clipPath id="cp-cow"><path d="M-10 -8C-12 -17 12 -17 10 -8L11 10C11 21 -11 21 -11 10Z"/></clipPath>
<path d="M-10 -8C-12 -17 12 -17 10 -8L11 10C11 21 -11 21 -11 10Z"${st('hide-100', O, 2.200)}/>
<g clip-path="url(#cp-cow)"><rect x="5.500" y="-16" width="7" height="38"${st('hide-300')}/><path d="M-11 -3q6 -6 9 0q1 6 -4 7q-6 0 -5 -7z"${st('hide-900')}/><path d="M1 7q7 -3 9 4q-1 7 -7 6q-5 -3 -2 -10z"${st('hide-900')}/><path d="M4 -13q5 -1 7 3l-1 3q-5 0 -6 -6z"${st('hide-900')}/></g>
<path d="M-10 -8C-12 -17 12 -17 10 -8L11 10C11 21 -11 21 -11 10Z"${st('none', O, 2.200)}/>
<ellipse cx="-8.500" cy="-19" rx="4" ry="2.400" transform="rotate(-18 -8.500 -19)"${st('hide-300', O, 1.800)}/><ellipse cx="8.500" cy="-19" rx="4" ry="2.400" transform="rotate(18 8.500 -19)"${st('hide-300', O, 1.800)}/>
<ellipse cx="0" cy="-19" rx="6.800" ry="8"${st('hide-100', O, 2.200)}/><path d="M0.500 -20q5 -3.500 5.500 1.500q-2 3.500 -5.500 1.500z"${st('hide-900')}/>
<ellipse cx="0" cy="-25" rx="4.600" ry="3"${st('muzzle', 'hide-900', 1.100)}/><circle cx="-1.600" cy="-25.300" r="0.700"${st('hide-900')}/><circle cx="1.600" cy="-25.300" r="0.700"${st('hide-900')}/>
<circle cx="-3" cy="-20.500" r="1"${st(O)}/><circle cx="3.200" cy="-20.500" r="1"${st(O)}/>`,
});

add('car', {
  off: [3, 4], tile: [56, 72],
  sh: `<rect x="-10" y="-19" width="20" height="38" rx="7"/>`,
  body: `<rect x="-12.500" y="-13" width="5" height="8" rx="2"${st('asphalt-900')}/><rect x="7.500" y="-13" width="5" height="8" rx="2"${st('asphalt-900')}/><rect x="-12.500" y="6" width="5" height="8" rx="2"${st('asphalt-900')}/><rect x="7.500" y="6" width="5" height="8" rx="2"${st('asphalt-900')}/>
<rect x="-10" y="-19" width="20" height="38" rx="7"${st('var(--car, #5f8fcf)', O, 2.200)}/><rect x="4.500" y="-15" width="4" height="30" rx="2" style="fill:#1d2545;opacity:0.16"/>
<path d="M-7 -7q7 -4 14 0l-1.500 5h-11z"${st('glass', 'glass-700', 1.100)}/><path d="M-6.500 9h13l1 4q-7.500 3 -15 0z"${st('glass', 'glass-700', 1.100)}/>
<rect x="-6.500" y="-2" width="13" height="11" rx="3"${st('var(--car-l, #8db4e6)')}/>
<circle cx="-5.500" cy="-16.500" r="1.600"${st('beacon')}/><circle cx="5.500" cy="-16.500" r="1.600"${st('beacon')}/><path d="M-4 -12.500h3"${st('none', '#ffffff', 1.400)} opacity="0.8"/>`,
});

add('fueltruck', {
  off: [4, 5], tile: [60, 92],
  sh: `<rect x="-11" y="-28" width="22" height="56" rx="6"/>`,
  body: `<rect x="-13" y="-22" width="5" height="8" rx="2"${st('asphalt-900')}/><rect x="8" y="-22" width="5" height="8" rx="2"${st('asphalt-900')}/><rect x="-13" y="10" width="5" height="9" rx="2"${st('asphalt-900')}/><rect x="8" y="10" width="5" height="9" rx="2"${st('asphalt-900')}/>
<clipPath id="cp-tank"><rect x="-11" y="-11" width="22" height="39" rx="10"/></clipPath><rect x="-11" y="-11" width="22" height="39" rx="10"${st('tin-300')}/>
<g clip-path="url(#cp-tank)"><rect x="-11" y="-11" width="7" height="39"${st('tin-100')}/><rect x="5" y="-11" width="6" height="39"${st('tin-500')}/><path d="M-11 2h22M-11 15h22"${st('none', 'tin-700', 1.100)}/></g>
<rect x="-11" y="-11" width="22" height="39" rx="10"${st('none', O, 2.200)}/><circle cx="0" cy="-4" r="2.600"${st('tin-500', 'tin-900', 1.100)}/><circle cx="0" cy="9" r="2.600"${st('tin-500', 'tin-900', 1.100)}/><circle cx="0" cy="21" r="2.600"${st('tin-500', 'tin-900', 1.100)}/>
<rect x="-10.500" y="-28" width="21" height="15" rx="4"${st('roof-500', O, 2.200)}/><path d="M-7 -24q7 -3 14 0l-1 4h-12z"${st('glass', 'glass-700', 1.100)}/><rect x="-6" y="-19" width="12" height="4.500" rx="1.500"${st('roof-300')}/>`,
});

add('haybale', {
  off: [3, 4], tile: [48, 48], sh: `<circle r="9"/>`,
  body: `<clipPath id="cp-hay"><circle r="9"/></clipPath><circle r="9"${st('straw-500')}/><g clip-path="url(#cp-hay)"><circle cx="-2" cy="-2.500" r="9"${st('straw-300')}/></g><path d="M0.500 0.500a2 2 0 1 1 -2.500 -2a4.500 4.500 0 1 1 5 6a6.500 6.500 0 0 1 -9 -5"${st('none', 'straw-700', 1.200)}/><circle r="9"${st('none', O, 2)}/>`,
});

add('windsock', {
  off: [9, 11], tile: [84, 76],
  sh: `<path d="M0 0v-26" style="stroke:#000;stroke-width:3"/><polygon points="0,-31 34,-27.500 34,-22.500 0,-21" transform="rotate(14 0 -26)"/>`,
  body: `<circle r="4.500"${st('concrete-300', O, 1.800)}/><path d="M0 0v-27"${st('none', O, 3.200)}/><path d="M-0.500 -2v-22"${st('none', 'tin-300', 1)}/>
<g transform="rotate(14 0 -26)"><clipPath id="cp-sock"><polygon points="0,-31.500 34,-28 34,-23 0,-20.500"/></clipPath><polygon points="0,-31.500 34,-28 34,-23 0,-20.500"${st('tangerine-500')}/>
<g clip-path="url(#cp-sock)"><rect x="7" y="-33" width="7" height="14"${st('foam')}/><rect x="21" y="-33" width="7" height="14"${st('foam')}/><rect x="0" y="-25" width="34" height="6" style="fill:#1d2545;opacity:0.14"/></g>
<polygon points="0,-31.500 34,-28 34,-23 0,-20.500"${st('none', O, 2)}/></g><circle cx="0" cy="-26" r="2.200"${st('tin-300', O, 1.400)}/>`,
});

add('cloud', {
  off: [0, 0], tile: [190, 120],
  sh: `<path d="M-58 20a22 22 0 0 1 4 -43a34 34 0 0 1 62 -12a26 26 0 0 1 44 16a20 20 0 0 1 4 39z"/>`,
  body: `<clipPath id="cp-cloud"><path d="M-58 20a22 22 0 0 1 4 -43a34 34 0 0 1 62 -12a26 26 0 0 1 44 16a20 20 0 0 1 4 39z"/></clipPath>
<path d="M-58 20a22 22 0 0 1 4 -43a34 34 0 0 1 62 -12a26 26 0 0 1 44 16a20 20 0 0 1 4 39z"${st('cloud')}/>
<g clip-path="url(#cp-cloud)"><path d="M-90 8q30 10 62 2q30 -8 56 2q24 8 50 -2v30h-168z"${st('cloud-shade')}/><path d="M-30 -34q14 -14 30 -8"${st('none', '#ffffff', 5)}/></g>
<path d="M-58 20a22 22 0 0 1 4 -43a34 34 0 0 1 62 -12a26 26 0 0 1 44 16a20 20 0 0 1 4 39z"${st('none', O, 3)}/><path d="M-36 -8q8 -8 18 -4M14 -18q8 -6 16 -1"${st('none', 'cloud-shade', 2.200)}/>`,
});

// Aircraft: the existing sprites, unchanged, re-centred on their pivot.
const AC = {};
for (const a of ['light-prop', 'turboprop', 'jet', 'jumbo', 'helicopter']) AC[a] = `<g transform="translate(-48,-48)">${inner(rd(`assets/aircraft/${a}.svg`))}</g>`;
const parked = AC['light-prop'].replace(/#ff[0-9a-f]{4}|#ffcb3d|#ff8f33|#ff5d52|#ff7ac8|#b48cff|#4fe0e6/gi, m => (m.toLowerCase() === '#ffffff' ? m : '#bcc6d8'));

function defs() {
  let s = '';
  for (const [id, o] of Object.entries(SPR)) s += `<g id="s-${id}">${o.body}</g><g id="h-${id}">${o.sh}</g>\n`;
  for (const [id, m] of Object.entries(AC)) s += `<g id="ac-${id}">${m}</g>\n`;
  s += `<g id="ac-parked">${parked}</g>\n`;
  s += `<g id="tuft"><path d="M0 0l-2.500 -6M0 0l0.300 -8.500M0 0l3 -6" style="fill:none;stroke:var(--grass-700);stroke-width:1.700"/></g>`;
  s += `<g id="tuft-l"><path d="M0 0l-2.500 -6M0 0l0.300 -8.500M0 0l3 -6" style="fill:none;stroke:var(--grass-300);stroke-width:1.700"/></g>`;
  s += `<g id="flower"><circle r="2.300" style="fill:var(--wall-100)"/><circle r="0.900" style="fill:var(--straw-500)"/></g>`;
  s += `<g id="flower-p"><circle r="2.300" style="fill:var(--muzzle)"/><circle r="0.900" style="fill:var(--wall-100)"/></g>`;
  s += `<g id="reed"><path d="M0 0l-2 -11M0 0l1 -13M0 0l4 -10" style="fill:none;stroke:var(--leaf-700);stroke-width:1.600"/><path d="M-2 -11l-0.400 -3M1 -13l0.200 -3.500M4 -10l0.600 -3" style="fill:none;stroke:var(--wood-700);stroke-width:2.600"/></g>`;
  s += `<g id="lily"><path d="M0 0L6 -2A6.300 6.300 0 1 1 5 -4Z" style="fill:var(--leaf-500);stroke:var(--leaf-900);stroke-width:1"/><circle cx="-1.500" cy="1.500" r="1.800" style="fill:var(--muzzle)"/></g>`;
  s += `<g id="duck"><path d="M-9 6q9 5 18 0M-6 11q6 3 12 0" style="fill:none;stroke:var(--foam);stroke-width:1.500"/><ellipse cx="0" cy="1" rx="4.200" ry="6" style="fill:var(--wall-100);stroke:var(--outline);stroke-width:1.500"/><circle cx="0" cy="-5" r="3" style="fill:var(--pine-500);stroke:var(--outline);stroke-width:1.500"/><path d="M-1.300 -8.500h2.600l-1.300 -2.600z" style="fill:var(--straw-500);stroke:var(--outline);stroke-width:1"/></g>`;
  return s;
}

// ---------------------------------------------------------------- scene
function scene() {
  const rng = mulberry(20261113);
  const R = (a, b) => a + rng() * (b - a);
  const ground = [], shadows = [], standing = [], air = [], airShadow = [];
  const TH = -7 * Math.PI / 180, CX = 930, CY = 590;
  const Lw = (x, y) => [n(CX + x * Math.cos(TH) - y * Math.sin(TH)), n(CY + x * Math.sin(TH) + y * Math.cos(TH))];
  const put = (id, x, y, o = {}) => {
    const s = o.s || 1, r = o.r || 0, sp = SPR[id];
    const tf = `scale(${s})${r ? ` rotate(${r})` : ''}`;
    shadows.push(`<use href="#h-${id}" transform="translate(${n(x + sp.off[0] * s)},${n(y + sp.off[1] * s)}) ${tf}"/>`);
    standing.push([y + (o.z || 0), `<use href="#s-${id}" transform="translate(${n(x)},${n(y)}) ${tf}"${o.style ? ` style="${o.style}"` : ''}/>`]);
  };
  const airfieldPts = [[430, 250], [960, 215], [1480, 250], [1640, 480], [1590, 760], [960, 820], [400, 800], [315, 520]];
  const inPoly = (pts, x, y) => { let ins = false; for (let i = 0, j = pts.length - 1; i < pts.length; j = i++) { const [xi, yi] = pts[i], [xj, yj] = pts[j]; if ((yi > y) !== (yj > y) && x < (xj - xi) * (y - yi) / (yj - yi) + xi) ins = !ins; } return ins; };
  const grow = (pts, f) => pts.map(([x, y]) => [960 + (x - 960) * f, 530 + (y - 530) * f]);
  const inAirfield = (x, y, f = 1.08) => inPoly(grow(airfieldPts, f), x, y);
  const blocks = [];
  const block = (x, y, r) => blocks.push([x, y, r]);
  const free = (x, y, pad = 0) => !inAirfield(x, y) && blocks.every(([bx, by, br]) => Math.hypot(x - bx, y - by) > br + pad);

  // --- lawn, meadow patches
  ground.push(`<rect x="-20" y="-20" width="1960" height="1120"${st('grass-500')}/>`);
  for (let i = 0; i < 26; i++) {
    const x = R(0, 1920), y = R(0, 1080);
    if (inAirfield(x, y, 1.2)) continue;
    ground.push(`<path d="${smooth(organic(x, y, R(60, 150), R(40, 90), 9, rng, 0.22))}"${st(i % 3 ? 'grass-600' : 'grass-400')} opacity="${i % 3 ? 0.75 : 0.6}"/>`);
  }

  // --- roads
  const roadMain = smooth([[232, -30], [205, 170], [172, 370], [214, 600], [250, 800], [205, 1110]], false);
  const roadAcc = smooth([[178, 330], [300, 322], [420, 318], [500, 322]], false);
  const track = smooth([[240, 880], [300, 905], [345, 1010], [372, 1002]], false);
  ground.push(`<path d="${track}"${st('none', 'sand-500', 15)}/><path d="${track}"${st('none', 'sand-300', 11)}/><path d="${track}"${st('none', 'sand-500', 1.500, 'stroke-dasharray:6 7')} transform="translate(-2.500,0)"/>`);
  for (const d of [roadMain, roadAcc]) ground.push(`<path d="${d}"${st('none', 'grass-700', 40)}/>`);
  for (const d of [roadMain, roadAcc]) ground.push(`<path d="${d}"${st('none', 'road-700', 29)}/>`);
  for (const d of [roadMain, roadAcc]) ground.push(`<path d="${d}"${st('none', 'road-500', 25)}/>`);
  for (const d of [roadMain, roadAcc]) ground.push(`<path d="${d}"${st('none', 'foam', 1.800, 'stroke-dasharray:9 10;stroke-linecap:butt')}/>`);
  for (let y = 0; y <= 1080; y += 40) block(210, y, 46);
  for (let x = 200; x <= 520; x += 40) block(x, 322, 40);

  // --- crop fields
  const field = (x, y, w, h, rot, kind, id) => {
    const P = { wheat: ['straw-300', 'straw-500', 'straw-700'], green: ['soil-500', 'leaf-300', 'soil-700'], plough: ['soil-500', 'soil-700', 'soil-700'] }[kind];
    let rows = '';
    if (h > w) for (let xx = -w / 2 + 7; xx < w / 2; xx += 9) rows += `M${n(xx)} ${-h / 2}V${h / 2}`;
    else for (let yy = -h / 2 + 7; yy < h / 2; yy += 9) rows += `M${-w / 2} ${n(yy)}H${w / 2}`;
    ground.push(`<g transform="translate(${x},${y}) rotate(${rot})"><rect x="${-w / 2 - 6}" y="${-h / 2 - 6}" width="${w + 12}" height="${h + 12}" rx="16"${st('grass-700')}/><clipPath id="cf-${id}"><rect x="${-w / 2}" y="${-h / 2}" width="${w}" height="${h}" rx="11"/></clipPath><rect x="${-w / 2}" y="${-h / 2}" width="${w}" height="${h}" rx="11"${st(P[0])}/><g clip-path="url(#cf-${id})"><path d="${rows}"${st('none', P[1], kind === 'plough' ? 2.200 : 4)}/></g><rect x="${-w / 2}" y="${-h / 2}" width="${w}" height="${h}" rx="11"${st('none', P[2], 2)}/></g>`);
    for (let a = -w / 2; a <= w / 2; a += 40) for (let b = -h / 2; b <= h / 2; b += 40) block(x + a, y + b, 34);
  };
  field(860, 975, 330, 120, -3, 'wheat', 1);
  field(1215, 985, 300, 112, 2, 'green', 2);
  field(78, 560, 112, 290, -2, 'plough', 3);
  field(74, 900, 104, 190, 1, 'green', 4);
  for (const [x, y] of [[760, 952], [802, 1004], [905, 960], [968, 1002]]) put('haybale', x, y);

  // --- pond
  {
    const pr = mulberry(77);
    const P = organic(1768, 560, 96, 68, 10, pr, 0.14);
    const d = smooth(P), d2 = smooth(P.map(([x, y]) => [1768 + (x - 1768) * 0.74, 560 + (y - 560) * 0.7]));
    ground.push(`<path d="${d}"${st('none', 'grass-700', 22)}/><path d="${d}"${st('sand-300', 'sand-300', 9)}/><path d="${d}"${st('water-300', 'water-700', 1.600)}/><path d="${d2}"${st('water-500')}/>`);
    ground.push(`<path d="M1730 548q10 -6 22 0M1790 590q12 -7 26 0M1760 520q8 -5 18 0"${st('none', 'foam', 2)}/>`);
    ground.push(`<use href="#lily" transform="translate(1722,585)"/><use href="#lily" transform="translate(1736,598) rotate(120) scale(.8)"/><use href="#lily" transform="translate(1818,535) rotate(200)"/><use href="#duck" transform="translate(1778,556) rotate(60)"/><use href="#duck" transform="translate(1798,570) rotate(75) scale(.85)"/>`);
    for (const [x, y] of [[1690, 520], [1698, 530], [1846, 600], [1838, 612], [1760, 628], [1690, 600]]) ground.push(`<use href="#reed" transform="translate(${x},${y})"/>`);
    block(1768, 560, 125);
  }

  // --- airfield lawn: mown in stripes parallel to the runway
  {
    const d = smooth(airfieldPts);
    let stripes = '';
    for (let y = -620; y < 620; y += 104) stripes += `<rect x="-1100" y="${y}" width="2200" height="52"${st('grass-600')}/>`;
    ground.push(`<path d="${d}"${st('none', 'grass-700', 7)} opacity="0.7"/><clipPath id="cp-airfield"><path d="${d}"/></clipPath><path d="${d}"${st('grass-500')}/><g clip-path="url(#cp-airfield)"><g transform="translate(${CX},${CY}) rotate(-7)">${stripes}</g></g>`);
  }

  // --- airfield hard surfaces (local frame, runway on the x axis)
  {
    let g = '';
    // apron
    g += `<rect x="-306" y="-241" width="372" height="117" rx="14"${st('grass-700')}/><rect x="-300" y="-235" width="360" height="105" rx="10"${st('concrete-300', 'concrete-700', 2)}/>`;
    g += `<path d="M-255 -235v105M-210 -235v105M-165 -235v105M-120 -235v105M-75 -235v105M-30 -235v105M15 -235v105M-300 -182h360"${st('none', 'concrete-500', 1.200)}/>`;
    g += `<path d="M-205 -150v-22M-216 -172h22M-112 -150v-22M-123 -172h22"${st('none', 'marking-warn', 2)}/>`;
    // taxiways
    const tx = ['M-160 -132V-30', 'M58 -185H250'];
    for (const d of tx) g += `<path d="${d}"${st('none', 'grass-700', 44, 'stroke-linecap:butt')}/>`;
    for (const d of tx) g += `<path d="${d}"${st('none', 'concrete-700', 33, 'stroke-linecap:butt')}/>`;
    for (const d of tx) g += `<path d="${d}"${st('none', 'asphalt-300', 29, 'stroke-linecap:butt')}/>`;
    g += `<path d="M-160 -150V-34M20 -185H246"${st('none', 'marking-warn', 1.800)}/><path d="M-173 -60h26M-173 -56h26"${st('none', 'marking-warn', 1.500)}/><path d="M-173 -51h26M-173 -47h26"${st('none', 'marking-warn', 1.500, 'stroke-dasharray:4 3;stroke-linecap:butt')}/>`;
    // helipad
    g += `<g transform="translate(300,-190)"><rect x="-76" y="-76" width="152" height="152" rx="22"${st('grass-700')}/><rect x="-69" y="-69" width="138" height="138" rx="16"${st('concrete-300', 'concrete-700', 2)}/><path d="M-69 0h138M0 -69v138"${st('none', 'concrete-500', 1.200)}/>
<circle r="55"${st('asphalt-500', O, 3)}/><circle r="47" style="fill:none;stroke:var(--asphalt-700);stroke-width:5;opacity:.5"/><circle r="39"${st('none', 'marking-warn', 5)}/><path d="M-13 -18V18M13 -18V18M-13 0H13"${st('none', 'marking', 7.500)}/>
${[[-60, -60], [60, -60], [-60, 60], [60, 60]].map(([x, y]) => `<circle cx="${x}" cy="${y}" r="3"${st('beacon', 'concrete-700', 1.200)}/>`).join('')}</g>`;
    // runway
    g += `<rect x="-404" y="-54" width="808" height="108" rx="30"${st('grass-700')}/><rect x="-390" y="-41" width="780" height="82" rx="12"${st('concrete-500', 'concrete-700', 1.600)}/>`;
    g += `<rect x="-380" y="-32" width="760" height="64" rx="6"${st('asphalt-500', O, 3)}/>`;
    for (const sgn of [-1, 1]) {
      g += `<g transform="scale(${sgn},1)">`;
      g += `<path d="M-306 -10h96M-298 -5h118M-302 6h104M-292 11h70"${st('none', 'asphalt-700', 3)}/>`;
      for (let i = 0; i < 6; i++) g += `<rect x="-372" y="${-25 + i * 9}" width="22" height="5" rx="1"${st('marking')}/>`;
      g += `<rect x="-262" y="-21" width="40" height="11" rx="1.500"${st('marking')}/><rect x="-262" y="10" width="40" height="11" rx="1.500"${st('marking')}/>`;
      g += `<path d="M-196 -19h20M-196 -13h20M-196 13h20M-196 19h20"${st('none', 'marking', 3, 'stroke-linecap:butt')}/>`;
      for (const y of [-19, -7, 7, 19]) g += `<circle cx="-385" cy="${y}" r="2.300"${st('mint-500', 'concrete-700', 1)}/>`;
      g += `</g>`;
    }
    g += `<path d="M-372 -27.500h744M-372 27.500h744"${st('none', 'marking', 1.600)}/><path d="M-150 0h300"${st('none', 'marking', 3.200, 'stroke-dasharray:26 20;stroke-linecap:butt')}/>`;
    g += `<text transform="translate(-322,0) rotate(90)" text-anchor="middle" dominant-baseline="central" style="font:700 27px var(--fd);fill:var(--marking)">09</text><text transform="translate(322,0) rotate(-90)" text-anchor="middle" dominant-baseline="central" style="font:700 27px var(--fd);fill:var(--marking)">27</text>`;
    for (let x = -348; x <= 348; x += 58) g += `<circle cx="${x}" cy="-36.500" r="2.500"${st('beacon', 'concrete-700', 1)}/><circle cx="${x}" cy="36.500" r="2.500"${st('beacon', 'concrete-700', 1)}/>`;
    for (let i = 0; i < 4; i++) g += `<rect x="${-268 + i * 9}" y="-52" width="6.500" height="5.500" rx="1"${st(i < 2 ? 'cherry-500' : 'foam', O, 1)}/>`;
    ground.push(`<g transform="translate(${CX},${CY}) rotate(-7)">${g}</g>`);
  }
  // car park
  ground.push(`<rect x="486" y="296" width="96" height="58" rx="10"${st('grass-700')}/><rect x="491" y="301" width="86" height="48" rx="7"${st('concrete-300', 'concrete-700', 1.600)}/><path d="M513 301v20M535 301v20M557 301v20"${st('none', 'foam', 1.500)}/>`);
  put('car', 502, 314, { r: 0, style: '--car:#e9dcc2;--car-l:#fff4dc' }); put('car', 546, 315, { r: 180 }); put('car', 214, 700, { r: 172, style: '--car:#8d7fc0;--car-l:#b3a8dc' }); put('car', 190, 250, { r: 8, style: '--car:#6fb39a;--car-l:#9bd3bd' });

  // --- airfield buildings and equipment (axis aligned; only their position follows the runway frame)
  put('tower', ...Lw(-262, -292)); put('terminal', ...Lw(-118, -296)); put('hangar', ...Lw(30, -300));
  { const [x, y] = Lw(-205, -186); ground.push(`<use href="#ac-parked" transform="translate(${x},${y}) rotate(-7) scale(.62)"/>`); }
  { const [x, y] = Lw(-112, -186); ground.push(`<use href="#ac-parked" transform="translate(${x},${y}) rotate(-7) scale(.62)"/>`); }
  put('fueltruck', ...Lw(-50, -178), { r: 20, s: 0.9 });
  put('windsock', ...Lw(150, -86));
  put('windsock', ...Lw(-340, 100), { s: 0.9 });

  // --- farm
  put('farmhouse', 372, 958); put('barn', 520, 968); put('silo', 588, 944);
  block(372, 958, 80); block(520, 968, 80); block(588, 944, 40);
  put('tree-c', 300, 1010); put('bush', 430, 1010); put('bush', 316, 916); put('rock', 640, 1010);
  // paddock
  {
    const pts = [[1560, 858], [1806, 866], [1800, 1010], [1552, 1002]];
    const d = `M${pts.map(p => p.join(' ')).join('L')}Z`;
    ground.push(`<path d="${d}"${st('grass-400', 'grass-700', 2)} opacity="0.9"/>`);
    shadows.push(`<path d="${d}" transform="translate(2.500,3.500)" style="fill:none;stroke:#000;stroke-width:3"/>`);
    let posts = '';
    for (let i = 0; i < 4; i++) { const a = pts[i], b = pts[(i + 1) % 4]; const L = Math.hypot(b[0] - a[0], b[1] - a[1]), k = Math.round(L / 30); for (let j = 0; j < k; j++) posts += `<circle cx="${n(a[0] + (b[0] - a[0]) * j / k)}" cy="${n(a[1] + (b[1] - a[1]) * j / k)}" r="3"${st('wood-300', O, 1.600)}/>`; }
    standing.push([850, `<path d="${d}"${st('none', O, 4.600)}/><path d="${d}"${st('none', 'wood-300', 2)}/>${posts}`]);
    put('cow', 1610, 905, { r: 40 }); put('cow', 1688, 950, { r: -70 }); put('cow', 1748, 902, { r: 150 }); put('cow', 1630, 968, { r: 100, s: 0.85 });
    for (let a = 1560; a <= 1800; a += 40) for (let b = 860; b <= 1000; b += 40) block(a, b, 30);
  }
  put('rock', 1662, 648); put('rock', 1858, 470, { s: 1.2 }); put('bush', 1690, 470); put('bush', 1872, 640);

  // --- hedge between the airfield and the crop fields (one merged silhouette)
  {
    const hr = mulberry(9); let o = '', f = '', h = '';
    for (let x = 700; x <= 1380; x += 13) { const y = 900 + (x - 700) * 0.012 + (hr() - 0.5) * 3, r = 8.500 + hr() * 2.500; o += `<circle cx="${x}" cy="${n(y)}" r="${n(r)}"/>`; f += `<circle cx="${x}" cy="${n(y)}" r="${n(r)}"/>`; h += `<circle cx="${n(x - 2)}" cy="${n(y - 3)}" r="${n(r * 0.55)}"/>`; block(x, y, 22); }
    shadows.push(`<g transform="translate(4,5)">${f}</g>`);
    standing.push([900, `<g${st('leaf-700', O, 5)}>${o}</g><g${st('leaf-500')}>${f}</g><g${st('leaf-300')}>${h}</g>`]);
  }

  // --- woods: clusters with a minimum spacing, drawn north to south
  const wood = (x0, y0, x1, y1, count, kinds, seed) => {
    const wr = mulberry(seed), placed = [];
    for (let t = 0; t < count * 14 && placed.length < count; t++) {
      const x = x0 + wr() * (x1 - x0), y = y0 + wr() * (y1 - y0);
      if (y < 150 || !free(x, y, 26) || placed.some(p => Math.hypot(p[0] - x, p[1] - y) < 40)) continue;
      placed.push([x, y]); put(kinds[Math.floor(wr() * kinds.length)], x, y, { s: 0.85 + wr() * 0.35 });
    }
    placed.forEach(p => block(p[0], p[1], 30));
  };
  wood(10, 150, 140, 400, 9, ['pine-a', 'pine-b', 'pine-a'], 3);
  wood(1470, 150, 1910, 300, 14, ['tree-a', 'tree-b', 'tree-c', 'pine-a'], 4);
  wood(1700, 300, 1910, 450, 5, ['tree-a', 'tree-b'], 6);
  wood(1400, 1030, 1910, 1075, 6, ['tree-a', 'pine-b', 'tree-b'], 8);
  wood(1820, 700, 1910, 1000, 5, ['tree-b', 'pine-a'], 12);
  wood(270, 150, 400, 260, 3, ['tree-a', 'tree-b'], 14);
  wood(250, 560, 300, 860, 3, ['tree-b'], 15);
  wood(1380, 830, 1530, 960, 3, ['tree-a', 'tree-c'], 16);

  // --- tufts and flowers: in clusters, on open ground only
  for (let i = 0; i < 46; i++) {
    const x = R(20, 1900), y = R(20, 1060);
    if (!free(x, y, 6)) continue;
    const kind = i % 4 === 0 ? (i % 8 ? 'flower' : 'flower-p') : (i % 3 ? 'tuft' : 'tuft-l');
    const k = kind.startsWith('flower') ? 5 : 3;
    for (let j = 0; j < k; j++) ground.push(`<use href="#${kind}" transform="translate(${n(x + R(-16, 16))},${n(y + R(-12, 12))})"/>`);
  }

  standing.sort((a, b) => a[0] - b[0]);
  const groundLayer = `<g>${ground.join('\n')}</g><g style="fill:var(--shadow);opacity:var(--shadow-o)">${shadows.join('')}</g><g>${standing.map(s => s[1]).join('\n')}</g>`;

  // --- air layer: zone glow, paths, aircraft shadows, aircraft, clouds
  const fly = (id, x, y, hdg, size, alt = 1) => {
    const s = size / 96;
    if (alt) airShadow.push(`<use href="#ac-${id}" transform="translate(${x + 15},${y + 20}) rotate(${hdg}) scale(${s})"/>`);
    air.push(`<use href="#ac-${id}" transform="translate(${x},${y}) rotate(${hdg}) scale(${s})"/>`);
  };
  const pathLine = (d, col, end) => `<path d="${d}"${st('none', O, 16.800)}/><path d="${d}"${st('none', col, 7.200, 'stroke-dasharray:24 19')}/><circle cx="${end[0]}" cy="${end[1]}" r="14.400"${st(col, O, 4.800)}/>`;
  const rwE = Lw(366, 0), heli = Lw(300, -190);
  let zone = `<g transform="translate(${CX},${CY}) rotate(-7)"><rect x="-396" y="-47" width="792" height="94" rx="16"${st('none', 'mint-500', 9)} opacity="0.85"/><rect x="-404" y="-55" width="808" height="110" rx="22"${st('none', 'mint-500', 6)} opacity="0.35"/></g>`;
  const pJet = smooth([[1722, 742], [1650, 640], [1540, 560], [1420, 535], rwE], false);
  const pHeli = smooth([[1470, 196], [1400, 250], [1300, 300], heli], false);
  let paths = pathLine(pJet, 'cherry-500', rwE) + `<g opacity="0.75">${pathLine(pHeli, 'lagoon', heli)}</g>`;
  paths += `<path d="M1085 855L1260 750"${st('none', O, 6, 'stroke-dasharray:1 16;opacity:.5')}/>`;
  fly('jet', 1722, 742, -36, 108); fly('helicopter', 1470, 196, -128, 96); fly('jumbo', 1010, 900, 59, 156); fly('turboprop', 452, 486, 104, 102);
  { const [x, y] = Lw(120, 0); air.unshift(`<use href="#ac-light-prop" transform="translate(${x},${y}) rotate(-97) scale(${84 / 96})"/>`); }
  const ring = `<circle cx="1722" cy="742" r="78"${st('none', O, 9.600)}/><circle cx="1722" cy="742" r="78"${st('none', '#ffffff', 4.800, 'stroke-dasharray:22 17')}/><g transform="translate(1722,842)"><rect x="-52" y="-17" width="104" height="34" rx="10"${st(O)}/><text text-anchor="middle" dominant-baseline="central" style="font:800 23px var(--fm);fill:#fffaf0;letter-spacing:.04em">FA 204</text></g>`;
  const clouds = `<g style="filter:brightness(0) blur(7px);opacity:.2"><use href="#s-cloud" transform="translate(456,266) scale(1.5)"/><use href="#s-cloud" transform="translate(1486,956) scale(1.15)"/></g><g opacity="0.9"><use href="#s-cloud" transform="translate(420,214) scale(1.5)"/><use href="#s-cloud" transform="translate(1458,916) scale(1.15)"/></g>`;
  const airLayer = `${zone}${paths}<g style="filter:brightness(0) blur(5px);opacity:.26">${airShadow.join('')}</g>${ring}${air.join('')}${clouds}`;
  return { groundLayer, airLayer };
}

module.exports = { defs, scene, SPR, st, inner, rd, n };
