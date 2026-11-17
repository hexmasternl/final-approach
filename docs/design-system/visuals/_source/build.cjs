// Builds the social visuals and the Play Store feature graphics from the design kit.
// Reuses the tokens, fonts, component CSS and custom elements of the design system (tools/build-showcase.cjs),
// writes one HTML file per visual next to this script, and renders each to PNG with headless Chrome.
// Every ground is a version 2 scene, composed with the kit's Scene builder; the approach-view visuals
// are drawn by the kit's approachSvg. Nothing here draws scenery, a runway or an aircraft of its own.
// Usage: node build.cjs [chromePath]      ONLY=<part of a name> renders a subset.
const fs = require('fs');
const path = require('path');
const { execFileSync } = require('child_process');

const here = __dirname;
const outDir = path.resolve(here, '..');
// The tokens, fonts, component CSS and the kit script: the same parts the showcase is built from.
const { head, kit } = require(path.resolve(here, '../../tools/build-showcase.cjs')).parts();
const FA = require(path.resolve(here, '../../bundle.js'));

const chrome = process.argv[2] || [
  'C:/Program Files/Google/Chrome/Application/chrome.exe',
  'C:/Program Files (x86)/Microsoft/Edge/Application/msedge.exe',
].find(p => fs.existsSync(p));

const extraCss = `
html,body{margin:0;padding:0;overflow:hidden;}
body{position:relative;font:500 16px/24px var(--font-text);color:var(--ink);}
.cv{position:absolute;inset:0;overflow:hidden;}
.cv *,.cv *::before,.cv *::after{box-sizing:border-box;}
.abs{position:absolute;}
.fill{position:absolute;inset:0;}
.fill>svg{display:block;width:100%;height:100%;}
.sky{background:repeating-conic-gradient(from 6deg at 50% 118%, var(--sky-300) 0 9deg, var(--bg-sky) 9deg 18deg);}
.sky--c{background:repeating-conic-gradient(from 3deg at 50% 50%, var(--sky-300) 0 9deg, var(--bg-sky) 9deg 18deg);}
.band{position:absolute;left:0;right:0;border-bottom:var(--line-zone) solid var(--outline);}
.h-xl{margin:0;font:700 96px/96px var(--font-display);letter-spacing:-.015em;color:var(--on-sky);}
.h-lg{margin:0;font:700 76px/80px var(--font-display);letter-spacing:-.01em;color:var(--on-sky);}
.kick{margin:0;font:800 22px/26px var(--font-text);letter-spacing:.12em;text-transform:uppercase;color:var(--on-sky-muted);}
.on-dark{color:var(--on-outline);}
.cap .fa-panel__title{margin:0;font-size:35px;line-height:38px;}
.cap p{margin-top:5px;font:700 17.5px/24px var(--font-body);}
.center{left:0;right:0;text-align:center;}
.sc{transform-origin:top left;}
`;

function page(v) {
  return `<!doctype html>
<html lang="en" data-theme="${v.theme || 'light'}">
<head>
<meta charset="utf-8">
<title>${v.name}</title>
${head}
${extraCss}
${v.css || ''}
</style>
</head>
<body style="width:${v.w}px;height:${v.h}px">
<div class="cv ${v.bg || ''}">
${v.body}
</div>
${kit}
</body>
</html>`;
}

const r1 = n => Math.round(n * 10) / 10;
const path_ = (d, livery, kind = 'idle', dot = true) => {
  const end = d.trim().split(/[\sA-Za-z,]+/).filter(Boolean).slice(-2);
  return `<g class="fa-path--${kind}" style="--fa-livery:var(--path-${livery})"><path class="fa-path-casing" d="${d}"/><path class="fa-path-dash" d="${d}"/>${dot ? `<circle class="fa-path-dot" cx="${end[0]}" cy="${end[1]}" r="11"/>` : ''}</g>`;
};
const svg = (w, h, inner) => `<svg viewBox="0 0 ${w} ${h}" width="${w}" height="${h}" style="position:absolute;inset:0" aria-hidden="true">${inner}</svg>`;
const hud = (score = '12&#8201;450', streak = 'x3', next = { type: 'jumbo', livery: 5, edge: 'arrow-left' }, lost = 1) => `
<div class="fa-hud" style="position:absolute;inset:0 0 auto 0">
  <div class="fa-hud__group">
    <div class="fa-strip"><span class="fa-strip__label fa-strip__label--sun">Score</span><span class="fa-strip__value">${score}</span></div>
    <span class="fa-streak"><fa-icon name="bolt"></fa-icon>${streak}</span>
  </div>
  <div class="fa-hud__group">
    <div class="fa-strip fa-next"><span class="fa-strip__label">Next</span><span class="fa-strip__value"><fa-aircraft type="${next.type}" livery="${next.livery}" heading="270" flat></fa-aircraft><span class="fa-next__edge"><fa-icon name="${next.edge}"></fa-icon></span></span></div>
    <div class="fa-strip" role="img" aria-label="lives"><span class="fa-strip__value fa-lives">${[0, 1, 2].map(i => `<fa-icon class="fa-life${i >= 3 - lost ? ' fa-life--lost' : ''}" name="heart"></fa-icon>`).join('')}</span></div>
    <button class="fa-iconbtn fa-iconbtn--hud" aria-label="Pause"><fa-icon name="pause"></fa-icon></button>
  </div>
</div>`;
const ring = (inner, size = 110) => `<span style="position:relative;display:grid;place-items:center;width:${size}px;height:${size}px">${inner}<svg class="fa-warn-ring" viewBox="0 0 100 100" aria-hidden="true"><circle class="fa-warn-ring__casing" cx="50" cy="50" r="46"/><circle class="fa-warn-ring__dash" cx="50" cy="50" r="46"/></svg></span>`;

// ---------------------------------------------------------------- kit pieces at poster size
/** A caption on a Panel, the kit's paper surface, 1.6 times its size. */
const caption = (pos, title, text = '') => `<div class="abs" style="${pos}"><div class="fa-panel cap" style="zoom:1.6;margin:0"><h2 class="fa-panel__title">${title}</h2>${text ? `<p>${text}</p>` : ''}</div></div>`;
/** A tagline on a Sticker. */
const tag = (icon, text, tone = 'bonus', zoom = 1.7) => `<span class="fa-popup fa-popup--${tone}" style="zoom:${zoom}"><fa-icon name="${icon}"></fa-icon>${text}</span>`;
/** A Flight Strip readout. */
const strip = (label, value) => `<div class="fa-strip"><span class="fa-strip__label">${label}</span><span class="fa-strip__value">${value}</span></div>`;

// ---------------------------------------------------------------- version 2 grounds
/** A runway frame: the same maths as the kit's Scene, so overlays land on what the scene draws. */
const frame = (cx, cy, deg) => {
  const t = deg * Math.PI / 180, c = Math.cos(t), s = Math.sin(t);
  return {
    cx, cy, deg,
    at: (x, y) => [r1(cx + x * c - y * s), r1(cy + x * s + y * c)],
    loc: (X, Y) => [r1((X - cx) * c + (Y - cy) * s), r1(-(X - cx) * s + (Y - cy) * c)],
  };
};
/** The mown airfield around a runway frame: an eight-sided lawn, `a` along the runway and `b` across. */
const lawn8 = (F, a, b) => [[-a, -b * 0.82], [0, -b], [a, -b * 0.82], [a * 1.1, 0], [a, b * 0.82], [0, b], [-a, b * 0.82], [-a * 1.1, 0]].map(p => F.at(p[0], p[1]));
/**
 * A ground: a kit Scene of the canvas divided by `zoom`, filled by `build(scene)`. What build returns
 * is laid over it in the same world coordinates: paths, aircraft, stickers.
 */
function world(seed, zoom, build, w = 1080, h = 1080) {
  const sc = FA.Scene(seed, Math.round(w / zoom), Math.round(h / zoom));
  const over = build(sc) || '';
  return `<div class="fill">${sc.svg({ air: false })}</div><div class="abs" style="left:0;top:0;width:${r1(w / zoom)}px;height:${r1(h / zoom)}px;transform:scale(${zoom});transform-origin:0 0">${over}</div>`;
}
const put = (sc, F, name, x, y, o) => { const p = F.at(x, y); sc.put(name, p[0], p[1], o); };
/** An aircraft centred on a point of the ground, at the size the kit's reference scenes fly it: 1.5 times its sprite token. */
const craft = (type, livery, x, y, heading, extra = '', size) => {
  const s = size || FA.AIRCRAFT[type].size * 1.5;
  return `<fa-aircraft type="${type}" livery="${livery}" heading="${heading}" size="${s}" ${extra} style="position:absolute;left:${r1(x - s / 2)}px;top:${r1(y - s / 2)}px"></fa-aircraft>`;
};
/** The approach view, filling the canvas. */
const approach = o => `<div class="fill">${FA.approachSvg(Object.assign({ width: 1080, height: 1080 }, o))}</div>`;

const S = 1080;
const visuals = [];

// 01 Hero: wordmark in a striped sky
visuals.push({ name: '01-hero-wordmark', w: S, h: S, bg: 'sky', body: `
<fa-cloud class="abs" style="left:-30px;top:70px;width:260px"></fa-cloud>
<fa-cloud class="abs" style="left:800px;top:40px;width:220px"></fa-cloud>
<fa-cloud class="abs" style="left:860px;top:640px;width:180px"></fa-cloud>
<p class="kick abs center" style="top:150px">A top-down air traffic game</p>
<div class="abs" style="left:0;right:0;top:250px;display:flex;justify-content:center">
  <h1 class="fa-wordmark" style="font-size:150px;line-height:140px;-webkit-text-stroke-width:22px;text-shadow:0 12px 0 var(--outline)">Final<span>Approach</span></h1>
</div>
${svg(S, S, path_('M300 860C430 760 560 820 720 700', 1, 'march', false))}
<fa-aircraft class="abs" type="jumbo" livery="5" heading="64" size="250" style="left:40px;top:700px"></fa-aircraft>
<fa-aircraft class="abs" type="jet" livery="1" heading="52" size="170" style="left:700px;top:560px"></fa-aircraft>
<fa-aircraft class="abs" type="helicopter" livery="6" heading="320" size="130" style="left:770px;top:860px"></fa-aircraft>
<div class="abs center" style="top:596px;display:flex;justify-content:center">${tag('play', 'Draw. Land. Repeat.')}</div>
`});

// 02 Splash: the sunset scene with the towed banner
visuals.push({ name: '02-sunset-splash', w: S, h: S, bg: 'fa-sunset', body: `
<span class="fa-sunset__sun" style="left:250px;top:560px;width:300px;height:300px"></span>
<fa-silhouette style="position:absolute;left:-200px;right:-200px;bottom:0"></fa-silhouette>
<div class="abs" style="left:150px;top:300px;transform:scale(1.55);transform-origin:left top">
  <span class="fa-banner" style="position:absolute;left:0;top:4px">Final Approach</span>
  <svg viewBox="0 0 520 120" width="520" height="120" style="position:absolute;left:0;top:0;overflow:visible" aria-hidden="true"><path class="fa-towline" d="M344 36q22 9 44 0"/></svg>
  <fa-side-aircraft type="light-prop" livery="1" style="position:absolute;left:383px;top:-2px"></fa-side-aircraft>
</div>
<p class="abs center" style="top:120px;margin:0;font:700 52px/58px var(--font-display);color:var(--on-outline)">Cleared for final approach.</p>
`});

// 03 Gameplay: a busy field
visuals.push({ name: '03-gameplay-field', w: S, h: S, body: world(3, 1.35, sc => {
  const F = frame(368, 452, -14), field = lawn8(F, 350, 205), hp = F.loc(672, 520);
  sc.lawn().frame(F.cx, F.cy, F.deg).claim(field).patches(12);
  sc.roads([[[830, 190], [700, 150], [610, 70], [580, -30]]]);
  sc.field(96, 84, 150, 84, -8, 'wheat');
  sc.airfield(field);
  sc.apron(-60, -182, 180, 74, [[-12, -142], [62, -142]]).taxi(['M30 -108V-30'], [[30, -56]]).helipad(hp[0], hp[1]).runway(380, '09/27', false, 'ok');
  put(sc, F, 'tower', -118, -176);
  put(sc, F, 'parked-aircraft', -12, -142, { r: -14, s: 0.8 });
  put(sc, F, 'fuel-truck', 86, -150, { r: 60, s: 0.85 });
  put(sc, F, 'windsock', 130, 66);
  sc.put('car', 690, 138, { r: 76 });
  sc.wood(640, 200, 800, 330, 5, ['tree', 'tree-small', 'pine'], 4).wood(0, 500, 110, 640, 3, ['tree', 'bush'], 5).wood(700, 0, 800, 110, 3, ['pine', 'pine-small'], 6);
  sc.scatter(14);
  const e = F.at(-190, 0), h = F.at(hp[0], hp[1]);
  return `${svg(800, 800,
    path_(`M116 236C40 360 80 470 ${e[0]} ${e[1]}`, 1, 'march') +
    path_(`M592 214C660 300 690 400 ${h[0]} ${h[1]}`, 6) +
    path_('M360 160C430 200 450 260 410 320', 2) +
    `<path class="fa-path-ghost" d="M716 322l-130 44"/>`)}
${craft('jet', 1, 116, 226, 200, 'selected callsign="FA 204"')}
${craft('helicopter', 6, 592, 202, 150)}
${craft('light-prop', 2, 358, 148, 120)}
${craft('turboprop', 3, 734, 314, 250)}
<span class="fa-popup fa-popup--bonus" style="position:absolute;left:300px;top:330px">+250 <small>Streak x3</small></span>
<fa-cloud style="position:absolute;left:10px;top:520px;width:140px"></fa-cloud>
<fa-cloud style="position:absolute;left:440px;top:84px;width:90px"></fa-cloud>
${hud()}`;
}) + caption('left:60px;right:60px;bottom:56px', 'Draw a path. Land it.', 'Tap an aircraft, drag to a runway, and keep the sky moving.') });

// 04 The fleet, parked on the apron
const fleet = [
  ['light-prop', 1, 'Light prop', 'Slow, turns tight'],
  ['turboprop', 2, 'Turboprop', 'Medium all round'],
  ['jet', 3, 'Jet', 'Fast, wide turns'],
  ['jumbo', 5, 'Jumbo', 'Big and stubborn'],
  ['helicopter', 6, 'Helicopter', 'Lands on the H'],
];
const fleetCell = ([t, l, n, d], size) => `<div style="display:flex;flex-direction:column;align-items:center;gap:18px">
  <div style="height:${Math.max(size, 200)}px;display:grid;place-items:center"><fa-aircraft type="${t}" livery="${l}" size="${size}"></fa-aircraft></div>
  <div style="zoom:1.05;white-space:nowrap">${strip(n, d)}</div></div>`;
visuals.push({ name: '04-meet-the-fleet', w: S, h: S, body: world(4, 1, sc => {
  const F = frame(540, 660, 0);
  sc.lawn().frame(F.cx, F.cy, F.deg).airfield([[-60, 200], [1140, 200], [1140, 1140], [-60, 1140]]);
  sc.apron(-476, -372, 952, 738);
  sc.put('fuel-truck', 968, 660, { r: 18 }).put('baggage-cart', 128, 676, { r: -72 }).put('windsock', 1010, 318);
  return '';
}) + `
<div class="band sky" style="top:0;height:250px"></div>
<p class="kick abs center" style="top:58px">Five flying toys</p>
<p class="h-xl abs center" style="top:100px">Meet the fleet</p>
<div class="abs" style="left:0;right:0;top:316px;display:flex;justify-content:space-evenly;align-items:flex-end;padding:0 60px">
  ${fleetCell(fleet[0], 150)}${fleetCell(fleet[1], 180)}${fleetCell(fleet[2], 190)}
</div>
<div class="abs" style="left:0;right:0;top:640px;display:flex;justify-content:space-evenly;align-items:flex-end;padding:0 150px">
  ${fleetCell(fleet[3], 290)}${fleetCell(fleet[4], 170)}
</div>
`});

// 05 How to play
visuals.push({ name: '05-how-to-play', w: S, h: S, body: world(5, 1, sc => {
  const F = frame(348, 658, -6), field = lawn8(F, 430, 190);
  sc.lawn().frame(F.cx, F.cy, F.deg).claim(field).patches(14);
  sc.pond(930, 560, 74, 50, 77, [['reed', -60, -30], ['reed', -52, -20], ['lily', 20, 14], ['duck', -14, -4, 'rotate(60)']]);
  sc.airfield(field);
  sc.taxi(['M-120 -150V-30'], [[-120, -58]]).runway(420, '09/27', false, 'ok');
  put(sc, F, 'windsock', -150, 84);
  sc.wood(660, 880, 1080, 1070, 6, ['tree', 'tree-small', 'pine', 'tree-oak'], 4, 62).wood(0, 470, 110, 600, 3, ['pine', 'pine-small'], 8).wood(0, 930, 60, 1070, 2, ['bush'], 9);
  sc.put('rock', 1010, 640).put('bush', 846, 610);
  sc.scatter(20);
  const e = F.at(210, 0);
  return `${craft('light-prop', 1, 860, 360, 40, 'selected', 120)}
${svg(S, S, path_(`M860 420C900 520 700 560 ${e[0]} ${e[1]}`, 1, 'march'))}`;
}) + `
<div class="band sky" style="top:0;height:250px"></div>
<p class="kick abs center" style="top:58px">How to play</p>
<p class="h-xl abs center" style="top:100px">Tap. Drag. Land.</p>
<div class="abs sc" style="left:70px;top:310px;transform:scale(1.6)">
  <div class="fa-coach"><fa-icon name="tap"></fa-icon><span><span class="fa-coach__step">Step 1 of 3</span><b>Tap an aircraft</b>It gets a ring when it is yours.</span></div>
</div>
<div class="abs sc" style="left:520px;top:690px;transform:scale(1.6)">
  <div class="fa-coach fa-coach--up"><fa-icon name="plane"></fa-icon><span><span class="fa-coach__step">Step 2 of 3</span><b>Drag to the runway</b>End the path lined up.</span></div>
</div>
<div class="abs" style="left:90px;top:840px;display:flex;align-items:center;gap:22px">
  <fa-burst kind="star" style="width:150px;height:150px"></fa-burst>
  <span class="fa-popup fa-popup--success sc" style="transform:scale(1.5);transform-origin:left center"><fa-icon name="check"></fa-icon>Safe landing</span>
</div>
`});

// 06 Streaks
visuals.push({ name: '06-streak-combo', w: S, h: S, body: world(6, 1, sc => {
  const F = frame(548, 698, 8), field = lawn8(F, 520, 215);
  sc.lawn().frame(F.cx, F.cy, F.deg).claim(field).patches(14);
  sc.field(900, 250, 250, 120, 6, 'wheat').field(130, 300, 190, 110, -4, 'green');
  sc.airfield(field);
  sc.taxi(['M-240 -170V-30'], [[-240, -58]]).runway(640, '09/27');
  put(sc, F, 'windsock', 250, -86);
  sc.put('hay-bale', 846, 236).put('hay-bale', 950, 262);
  sc.wood(0, 960, 300, 1070, 4, ['tree', 'pine', 'tree-small'], 3, 60).wood(880, 940, 1080, 1070, 3, ['tree-oak', 'tree'], 5, 70);
  sc.scatter(18);
  return craft('turboprop', 2, 535, 665, 98, '', 190);
}) + `
<fa-burst kind="star" style="position:absolute;left:120px;top:560px;width:220px;height:220px"></fa-burst>
<fa-burst kind="star" style="position:absolute;left:760px;top:600px;width:180px;height:180px"></fa-burst>
<span class="fa-popup sc" style="position:absolute;left:110px;top:470px;transform:scale(1.6) rotate(-6deg)">+100</span>
<span class="fa-popup fa-popup--bonus sc" style="position:absolute;left:600px;top:470px;transform:scale(1.6) rotate(5deg)">+250 <small>Streak x3</small></span>
<span class="fa-popup fa-popup--success sc" style="position:absolute;left:640px;top:860px;transform:scale(1.5)"><fa-icon name="check"></fa-icon>Safe landing</span>
<div class="abs" style="left:0;right:0;top:280px;display:flex;justify-content:center">
  <span class="fa-streak fa-streak--hot" style="transform:scale(2.8) rotate(-6deg)"><fa-icon name="bolt"></fa-icon>x5 streak</span>
</div>
${caption('left:60px;right:60px;top:56px;text-align:center', 'Chain your landings', 'Every safe landing grows the multiplier.')}
`});

// 07 Keep your distance
visuals.push({ name: '07-keep-your-distance', w: S, h: S, body: world(7, 1, sc => {
  const F = frame(568, 818, -4), field = lawn8(F, 480, 200);
  sc.lawn().frame(F.cx, F.cy, F.deg).claim(field).patches(14);
  sc.roads([[[-30, 330], [150, 372], [330, 350], [520, 300], [760, 292], [1110, 330]]]);
  sc.airfield(field);
  sc.taxi(['M150 -160V-30'], [[150, -58]]).runway(600, '09/27');
  put(sc, F, 'windsock', -200, 78);
  sc.put('car', 300, 356, { r: -84 }).put('car', 700, 291, { r: 92, style: '--denim-500:var(--pine-500);--denim-300:var(--pine-300)' });
  sc.wood(0, 430, 150, 620, 4, ['tree', 'tree-small', 'pine'], 4, 56).wood(930, 420, 1080, 600, 3, ['pine', 'tree'], 6, 60);
  sc.scatter(18);
  return svg(S, S, path_('M300 470C380 520 470 540 560 520', 1, 'march', false) + path_('M800 420C730 480 640 520 600 530', 3, 'idle', false));
}) + `
<div class="abs sc" style="left:210px;top:350px;transform:scale(1.7)">${ring('<fa-aircraft type="jet" livery="1" heading="118"></fa-aircraft>')}</div>
<div class="abs sc" style="left:670px;top:300px;transform:scale(1.7)">${ring('<fa-aircraft type="turboprop" livery="3" heading="235"></fa-aircraft>')}</div>
<span class="fa-popup fa-popup--danger sc" style="position:absolute;left:395px;top:600px;transform:scale(1.6)"><fa-icon name="warning"></fa-icon>Too close</span>
<fa-cloud style="position:absolute;left:820px;top:640px;width:170px"></fa-cloud>
${caption('left:60px;right:60px;top:56px', 'Keep your distance', 'A dashed warning ring means two aircraft are too close. Redraw a path before they touch.')}
`});

// 08 Airports
visuals.push({ name: '08-choose-your-airport', w: S, h: S, bg: 'sky--c', body: `
<p class="kick abs center" style="top:70px">Three airfields to master</p>
<p class="h-xl abs center" style="top:112px">Choose your airport</p>
<div class="abs" style="left:0;right:0;top:330px;display:flex;justify-content:center;gap:28px;transform:scale(1.3);transform-origin:center top">
<button class="fa-levelcard" style="transform:rotate(-4deg) translateY(16px)">
  <span class="fa-levelcard__art"><fa-scene level="meadow" ground crop="330 190 1330 700"></fa-scene><span class="fa-levelcard__badge"><span class="fa-chip fa-chip--sun"><fa-icon name="star"></fa-icon>18&#8201;200</span></span></span>
  <span class="fa-levelcard__body"><span class="fa-levelcard__title">Meadow Field</span><span class="fa-levelcard__tags"><span class="fa-chip">1 runway</span><span class="fa-chip">1 helipad</span></span></span>
</button>
<button class="fa-levelcard is-focus">
  <span class="fa-levelcard__art"><fa-scene level="island" ground crop="300 150 1330 780"></fa-scene><span class="fa-levelcard__badge"><span class="fa-chip fa-chip--sun"><fa-icon name="star"></fa-icon>9&#8201;750</span></span></span>
  <span class="fa-levelcard__body"><span class="fa-levelcard__title">Island</span><span class="fa-levelcard__tags"><span class="fa-chip">1 runway</span><span class="fa-chip">1 helipad</span></span></span>
</button>
<button class="fa-levelcard" aria-disabled="true" style="transform:rotate(4deg) translateY(16px)">
  <span class="fa-levelcard__art"><fa-scene level="savanna" ground crop="300 190 1330 700"></fa-scene><span class="fa-levelcard__lock"><span><fa-icon name="lock"></fa-icon></span></span></span>
  <span class="fa-levelcard__body"><span class="fa-levelcard__title">Savanna</span><span class="fa-levelcard__tags"><span class="fa-chip fa-chip--plain">Land 50 to unlock</span></span></span>
</button>
</div>
<fa-aircraft type="jet" livery="4" heading="70" size="140" style="position:absolute;left:60px;top:860px"></fa-aircraft>
<fa-cloud style="position:absolute;left:780px;top:880px;width:220px"></fa-cloud>
`});

// 09 Night shift
visuals.push({ name: '09-night-shift', w: S, h: S, theme: 'dark', body: world(9, 1, sc => {
  const F = frame(548, 668, 12), field = lawn8(F, 520, 250), hp = F.loc(905, 500);
  sc.lawn().frame(F.cx, F.cy, F.deg).claim(field).patches(12);
  sc.airfield(field);
  sc.apron(-330, 96, 230, 84, [[-270, 138], [-190, 138]]).taxi(['M-215 96V30'], [[-215, 58]]).helipad(hp[0], hp[1]).runway(600, '09/27');
  put(sc, F, 'terminal', -260, 244, { s: 0.9 });
  put(sc, F, 'tower', -420, 226);
  put(sc, F, 'parked-aircraft', -270, 138, { r: 12, s: 0.8 });
  put(sc, F, 'windsock', 120, 78);
  sc.wood(820, 880, 1080, 1070, 4, ['pine', 'tree', 'pine-small'], 4, 60).wood(0, 380, 120, 520, 3, ['pine', 'pine-small'], 6, 50).wood(520, 950, 760, 1070, 3, ['tree', 'tree-small'], 9, 60);
  sc.scatter(12, ['tuft', 'tuft-light', 'tuft']);
  const e = F.at(300, 0), h = F.at(hp[0], hp[1]);
  return `${svg(S, S, path_(`M960 880C900 780 880 760 ${e[0]} ${e[1]}`, 5, 'march') + path_(`M756 412C820 400 870 430 ${h[0]} ${h[1]}`, 6))}
${craft('jumbo', 5, 965, 880, 330)}
${craft('helicopter', 6, 752, 408, 80)}
${craft('light-prop', 2, 150, 620, 60)}
<fa-cloud style="position:absolute;left:330px;top:400px;width:200px"></fa-cloud>`;
}) + `
<div class="band sky" style="top:0;height:330px"></div>
<span class="abs" style="left:860px;top:60px;width:110px;height:110px;border-radius:50%;background:var(--paper-100);box-shadow:inset -22px -10px 0 var(--paper-300),0 0 0 var(--line-zone) var(--outline)"></span>
<p class="kick abs" style="left:70px;top:76px">New levels in the works</p>
<p class="h-xl abs" style="left:70px;top:118px">Night shift</p>
<div class="abs" style="left:64px;top:226px">${tag('star', 'Coming soon', 'bonus', 1.3)}</div>
`});

// 10 Score challenge
visuals.push({ name: '10-beat-the-best', w: S, h: S, body: world(10, 1, sc => {
  const F = frame(568, 578, -14), field = lawn8(F, 470, 230);
  sc.lawn().frame(F.cx, F.cy, F.deg).claim(field).patches(14);
  sc.pond(130, 930, 84, 56, 51, [['reed', -70, -30], ['reed', 66, 40], ['lily', 18, 12]]);
  sc.airfield(field);
  sc.taxi(['M-170 -180V-30'], [[-170, -58]]).runway(560, '09/27');
  put(sc, F, 'windsock', 240, 80);
  sc.wood(820, 80, 1080, 300, 5, ['tree', 'pine', 'tree-small'], 4, 60).wood(0, 180, 120, 420, 3, ['pine', 'tree'], 7, 60).wood(760, 930, 1080, 1070, 4, ['tree-oak', 'tree', 'bush'], 8, 64);
  sc.scatter(18);
  return `${craft('jet', 1, 186, 236, 200)}${craft('jumbo', 5, 872, 812, 40)}`;
}) + `
<div class="fa-scrim" style="position:absolute;inset:0"></div>
<p class="abs center" style="top:48px;margin:0;font:700 72px/76px var(--font-display);color:var(--on-outline)">Can you beat 18&#8201;200?</p>
<div class="abs" style="left:0;right:0;top:172px;display:flex;justify-content:center">
  <div class="fa-dialog" role="dialog" style="width:420px;transform:scale(1.65);transform-origin:center top">
    <header class="fa-dialog__head fa-dialog__head--danger"><span class="fa-dialog__eyebrow">Tower log · runway 27</span><h2 class="fa-dialog__title">Game over</h2></header>
    <p class="fa-dialog__body">Two aircraft got too close over runway 27.<br><span class="fa-stamp">Too close</span></p>
    <hr class="fa-perf">
    <dl class="fa-dialog__stats">
      <div class="fa-stat"><dt>Score</dt><dd>12&#8201;450</dd></div>
      <div class="fa-stat"><dt>Landed</dt><dd>37</dd></div>
      <div class="fa-stat"><dt>Best streak</dt><dd>x6</dd></div>
      <div class="fa-stat"><dt>Best</dt><dd>18&#8201;200</dd></div>
    </dl>
    <div class="fa-dialog__actions">
      <button class="fa-btn fa-btn--lg fa-btn--block"><fa-icon name="restart"></fa-icon>Try again</button>
    </div>
  </div>
</div>
`});

// ---------------------------------------------------------------- the approach view: on final, seen from the aircraft
const povTitle = (kick, title, dark) => `<p class="kick abs center${dark ? ' on-dark' : ''}" style="top:64px">${kick}</p><p class="h-xl abs center${dark ? ' on-dark' : ''}" style="top:104px">${title}</p>`;
const lowClouds = [[0.1, 0.3, 1.25], [0.9, 0.27, 1], [0.7, 0.33, 0.6]];

// 11 Cleared to land: a jumbo on final over Meadow Field
visuals.push({ name: '11-pov-cleared-to-land', w: S, h: S, body: `${approach({ landscape: 'meadow', type: 'jumbo', livery: 5, number: '27', zone: 'ok', clouds: lowClouds })}
${povTitle('Runway 27 in sight', 'Cleared to land')}
<div class="abs" style="right:36px;bottom:40px;zoom:1.5">${strip('FA 204', 'On final')}</div>
`});

// 12 From the cockpit of the light prop
visuals.push({ name: '12-pov-from-the-cockpit', w: S, h: S, body: `${approach({ landscape: 'meadow', view: 'cockpit', type: 'light-prop', livery: 1, number: '27', clouds: [[0.16, 0.3, 1.1], [0.86, 0.26, 0.9]] })}
<p class="kick abs center" style="top:96px">The view from the front seat</p>
<p class="h-xl abs center" style="top:136px">Line it up</p>
<div class="abs" style="left:0;right:0;bottom:36px;display:flex;justify-content:center;gap:14px;zoom:1.7">${strip('Runway', '27')}${strip('Light prop', 'Slow and steady')}</div>
`});

// 13 An island runway from far out
visuals.push({ name: '13-pov-island-in-sight', w: S, h: S, body: `${approach({ landscape: 'island', type: 'turboprop', livery: 2, number: '10', threshold: 300, eye: 90, clouds: lowClouds })}
${povTitle('Runway 10, wind calm', 'Island in sight')}
<div class="abs" style="right:36px;bottom:40px;zoom:1.5">${strip('FA 118', 'On final')}</div>
`});

// 14 The helicopter over the helipad
visuals.push({ name: '14-pov-land-on-the-h', w: S, h: S, body: `${approach({ landscape: 'meadow', target: 'helipad', type: 'helicopter', livery: 6, zone: 'ok', clouds: lowClouds })}
${povTitle('Helicopters skip the runway', 'Land on the H')}
<div class="abs" style="right:36px;bottom:40px;zoom:1.5">${strip('FA 731', 'Helipad')}</div>
`});

// 15 Night: the runway lights lead in
visuals.push({ name: '15-pov-follow-the-lights', w: S, h: S, theme: 'dark', body: `${approach({ landscape: 'meadow', type: 'jet', livery: 3, number: '09', zone: 'ok', clouds: lowClouds })}
${povTitle('Night shift', 'Follow the lights')}
<div class="abs" style="right:36px;bottom:40px;zoom:1.5">${strip('FA 090', 'On final')}</div>
`});

// 16 Dawn on the savanna: the one-way runway
visuals.push({ name: '16-pov-one-way-in', w: S, h: S, theme: 'dawn', body: `${approach({ landscape: 'savanna', type: 'jet', livery: 4, number: '27', oneWay: true, clouds: lowClouds })}
${povTitle('Savanna at dawn', 'One way in', true)}
<div class="abs" style="right:36px;bottom:40px;zoom:1.5">${strip('FA 452', 'On final')}</div>
`});

// Play Store feature graphic, 1024 x 500
visuals.push({ name: 'play-store-feature-graphic', w: 1024, h: 500, bg: 'sky', body: `
<fa-cloud class="abs" style="left:400px;top:-22px;width:150px"></fa-cloud>
<fa-cloud class="abs" style="left:420px;top:400px;width:130px"></fa-cloud>
<div class="abs" style="left:40px;top:70px">
  <p class="kick" style="font-size:17px;margin:0 0 26px 8px">A top-down air traffic game</p>
  <h1 class="fa-wordmark" style="font-size:92px;line-height:88px;-webkit-text-stroke-width:15px;text-shadow:0 8px 0 var(--outline)">Final<span>Approach</span></h1>
  <div style="margin:36px 0 0 2px">${tag('play', 'Draw. Land. Repeat.', 'bonus', 1.35)}</div>
</div>
<div class="abs" style="left:620px;top:-40px;width:480px;height:600px;transform:rotate(8deg);border-radius:var(--radius-dialog);box-shadow:0 0 0 var(--line-zone) var(--outline),-10px 10px 0 3px var(--contact);overflow:hidden">
  <div style="position:absolute;left:0;top:0;width:520px;height:600px;transform:rotate(-8deg) translate(-92px,6px)">
    ${world(12, 1, sc => {
      const F = frame(308, 388, -14), field = lawn8(F, 330, 200), hp = F.loc(410, 196);
      sc.lawn().frame(F.cx, F.cy, F.deg).claim(field).patches(8);
      sc.airfield(field);
      sc.taxi(['M-60 -150V-30'], [[-60, -58]]).helipad(hp[0], hp[1]).runway(380, '09/27', false, 'ok');
      put(sc, F, 'windsock', 150, 70);
      put(sc, F, 'tower', -170, -150);
      sc.wood(0, 480, 220, 600, 3, ['tree', 'pine'], 4, 56).wood(380, 0, 520, 90, 2, ['pine', 'tree-small'], 5, 50);
      sc.scatter(8);
      const e = F.at(-190, 0), h = F.at(hp[0], hp[1]);
      return `${svg(520, 600, path_(`M130 120C40 250 90 360 ${e[0]} ${e[1]}`, 1, 'march') + path_(`M420 360C440 300 430 250 ${h[0]} ${h[1]}`, 6))}
${craft('jet', 1, 132, 120, 200, 'selected')}${craft('helicopter', 6, 422, 372, 340)}${craft('jumbo', 5, 352, 522, 300)}
<span class="fa-popup fa-popup--bonus" style="position:absolute;left:220px;top:250px">+250 <small>Streak x3</small></span>`;
    }, 520, 600)}
  </div>
</div>
`});

// Play Store feature graphic, the approach view, 1024 x 500
visuals.push({ name: 'play-store-feature-graphic-approach', w: 1024, h: 500, body: `<div class="fill">${FA.approachSvg({ width: 1024, height: 500, landscape: 'meadow', type: 'jet', livery: 1, number: '27', zone: 'ok', centre: 0.74, horizon: 0.6, craft: { x: 0.74, y: 0.84, w: 0.62 }, clouds: [[0.6, 0.1, 1.5], [0.93, 0.3, 1.1]] })}</div>
<div class="abs" style="left:36px;top:26px">
  <p class="kick" style="font-size:16px;margin:0 0 20px 8px">Bring them in to land</p>
  <h1 class="fa-wordmark" style="font-size:80px;line-height:76px;-webkit-text-stroke-width:13px;text-shadow:0 7px 0 var(--outline)">Final<span>Approach</span></h1>
</div>
<div class="abs" style="left:34px;bottom:26px">${tag('play', 'Draw. Land. Repeat.', 'bonus', 1.25)}</div>
`});

const only = process.env.ONLY;
for (const v of visuals) {
  if (only && !v.name.includes(only)) continue;
  const htmlPath = path.join(here, v.name + '.html');
  fs.writeFileSync(htmlPath, page(v));
  const png = path.join(outDir, v.name + '.png');
  execFileSync(chrome, ['--headless=new', '--disable-gpu', '--hide-scrollbars', '--force-device-scale-factor=1',
    `--window-size=${v.w},${v.h}`, '--virtual-time-budget=2500', `--screenshot=${png}`,
    'file:///' + htmlPath.replace(/\\/g, '/')], { stdio: 'ignore' });
  console.log('rendered', path.relative(outDir, png));
}
