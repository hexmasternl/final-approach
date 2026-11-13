// Builds the social visuals and the Play Store feature graphic from the design kit.
// Reuses the tokens, fonts, component CSS and custom elements of the design system (tools/build-showcase.cjs),
// writes one HTML file per visual next to this script, and renders each to PNG with headless Chrome.
// Usage: node build.cjs [chromePath]
const fs = require('fs');
const path = require('path');
const { execFileSync } = require('child_process');

const here = __dirname;
const outDir = path.resolve(here, '..');
// The tokens, fonts, component CSS and the kit script: the same parts the showcase is built from.
const { head, kit } = require(path.resolve(here, '../../tools/build-showcase.cjs')).parts();

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
.sky{background:repeating-conic-gradient(from 6deg at 50% 118%, var(--sky-300) 0 9deg, var(--bg-sky) 9deg 18deg);}
.sky--c{background:repeating-conic-gradient(from 3deg at 50% 50%, var(--sky-300) 0 9deg, var(--bg-sky) 9deg 18deg);}
.field{background:repeating-linear-gradient(97deg, var(--grass-500) 0 52px, var(--grass-600) 52px 104px);}
.band{position:absolute;left:0;right:0;border-bottom:var(--line-zone) solid var(--outline);}
.band--bottom{border-bottom:0;border-top:var(--line-zone) solid var(--outline);}
.h-xl{margin:0;font:700 96px/96px var(--font-display);letter-spacing:-.015em;color:var(--on-sky);}
.h-lg{margin:0;font:700 76px/80px var(--font-display);letter-spacing:-.01em;color:var(--on-sky);}
.h-md{margin:0;font:700 56px/60px var(--font-display);color:var(--ink);}
.kick{margin:0;font:800 22px/26px var(--font-text);letter-spacing:.12em;text-transform:uppercase;color:var(--on-sky-muted);}
.sub{margin:0;font:700 28px/38px var(--font-body);color:var(--ink-muted);}
.card{background:var(--paper-100);border-radius:var(--radius-dialog);box-shadow:inset 0 -8px 0 var(--paper-400),0 0 0 var(--line-zone) var(--outline),0 12px 0 3px var(--contact-field);}
.tag{display:inline-flex;align-items:center;gap:10px;padding:10px 22px;border-radius:var(--radius-pill);background:var(--sun-500);color:var(--on-accent);font:700 26px/30px var(--font-display);box-shadow:inset 0 3px 0 var(--sun-200),inset 0 -5px 0 var(--sun-700),0 0 0 var(--line-zone) var(--outline),0 0 0 7px var(--paper-0),0 7px 0 7px var(--contact);}
.tag fa-icon{--fa-icon:28px;}
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
<div class="abs center" style="top:600px;display:flex;justify-content:center"><span class="tag"><fa-icon name="play"></fa-icon>Draw. Land. Repeat.</span></div>
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
visuals.push({ name: '03-gameplay-field', w: S, h: S, bg: 'field', body: `
<div class="abs" style="left:0;top:0;width:800px;height:800px;transform:scale(1.35);transform-origin:0 0">
<fa-runway length="380" numbers="09/27" zone="ok" style="position:absolute;left:150px;top:400px;transform:rotate(-14deg)"></fa-runway>
<fa-helipad style="position:absolute;left:600px;top:470px"></fa-helipad>
<fa-windsock style="position:absolute;left:470px;top:520px"></fa-windsock>
${svg(800, 800,
  path_('M116 236C40 360 80 470 178 470', 1, 'march') +
  path_('M592 214C660 300 690 400 656 526', 6) +
  path_('M360 160C430 200 450 260 410 320', 2) +
  `<path class="fa-path-ghost" d="M716 322l-130 44"/>`)}
<fa-aircraft type="jet" livery="1" heading="200" selected callsign="FA 204" style="position:absolute;left:80px;top:190px"></fa-aircraft>
<fa-aircraft type="helicopter" livery="6" heading="150" style="position:absolute;left:560px;top:170px"></fa-aircraft>
<fa-aircraft type="light-prop" livery="2" heading="120" style="position:absolute;left:330px;top:120px"></fa-aircraft>
<fa-aircraft type="turboprop" livery="3" heading="250" style="position:absolute;left:700px;top:280px"></fa-aircraft>
<span class="fa-popup fa-popup--bonus" style="position:absolute;left:300px;top:330px">+250 <small>Streak x3</small></span>
<fa-cloud style="position:absolute;left:10px;top:520px;width:140px"></fa-cloud>
<fa-cloud style="position:absolute;left:440px;top:84px;width:90px"></fa-cloud>
${hud()}
</div>
<div class="abs card" style="left:60px;right:60px;bottom:56px;padding:30px 40px">
  <p class="h-md">Draw a path. Land it.</p>
  <p class="sub" style="margin-top:8px">Tap an aircraft, drag to a runway, and keep the sky moving.</p>
</div>
`});

// 04 The fleet
const fleet = [
  ['light-prop', 1, 'Light prop', 'Slow, turns tight'],
  ['turboprop', 2, 'Turboprop', 'Medium all round'],
  ['jet', 3, 'Jet', 'Fast, wide turns'],
  ['jumbo', 5, 'Jumbo', 'Big and stubborn'],
  ['helicopter', 6, 'Helicopter', 'Lands on the H'],
];
const fleetCell = ([t, l, n, d], size) => `<div style="display:flex;flex-direction:column;align-items:center;gap:18px">
  <div style="height:${Math.max(size, 200)}px;display:grid;place-items:center"><fa-aircraft type="${t}" livery="${l}" size="${size}"></fa-aircraft></div>
  <span class="fa-chip" style="font-size:22px;padding:8px 18px">${n}</span>
  <span style="font:800 20px/24px var(--font-body);color:var(--ink)">${d}</span></div>`;
visuals.push({ name: '04-meet-the-fleet', w: S, h: S, bg: 'field', body: `
<div class="band sky" style="top:0;height:250px"></div>
<p class="kick abs center" style="top:58px">Five flying toys</p>
<p class="h-xl abs center" style="top:100px">Meet the fleet</p>
<div class="abs" style="left:0;right:0;top:300px;display:flex;justify-content:space-evenly;align-items:flex-end">
  ${fleetCell(fleet[0], 150)}${fleetCell(fleet[1], 180)}${fleetCell(fleet[2], 190)}
</div>
<div class="abs" style="left:0;right:0;top:640px;display:flex;justify-content:space-evenly;align-items:flex-end;padding:0 120px">
  ${fleetCell(fleet[3], 290)}${fleetCell(fleet[4], 170)}
</div>
`});

// 05 How to play
visuals.push({ name: '05-how-to-play', w: S, h: S, bg: 'field', body: `
<div class="band sky" style="top:0;height:250px"></div>
<p class="kick abs center" style="top:58px">How to play</p>
<p class="h-xl abs center" style="top:100px">Tap. Drag. Land.</p>
<div class="abs sc" style="left:70px;top:310px;transform:scale(1.6)">
  <div class="fa-coach"><fa-icon name="tap"></fa-icon><span><span class="fa-coach__step">Step 1 of 3</span><b>Tap an aircraft</b>It gets a ring when it is yours.</span></div>
</div>
<fa-aircraft type="light-prop" livery="1" heading="40" selected size="120" style="position:absolute;left:800px;top:300px"></fa-aircraft>
${svg(S, S, path_('M860 420C900 520 700 560 560 600', 1, 'march'))}
<fa-runway length="420" numbers="09/27" zone="ok" style="position:absolute;left:110px;top:600px;transform:rotate(-6deg)"></fa-runway>
<div class="abs sc" style="left:520px;top:690px;transform:scale(1.6)">
  <div class="fa-coach fa-coach--up"><fa-icon name="plane"></fa-icon><span><span class="fa-coach__step">Step 2 of 3</span><b>Drag to the runway</b>End the path lined up.</span></div>
</div>
<div class="abs" style="left:90px;top:840px;display:flex;align-items:center;gap:22px">
  <fa-burst kind="star" style="width:150px;height:150px"></fa-burst>
  <span class="fa-popup fa-popup--success sc" style="transform:scale(1.5);transform-origin:left center"><fa-icon name="check"></fa-icon>Safe landing</span>
</div>
`});

// 06 Streaks
visuals.push({ name: '06-streak-combo', w: S, h: S, bg: 'field', body: `
<fa-runway length="640" numbers="18/36" zone="ok" style="position:absolute;left:200px;top:640px;transform:rotate(8deg)"></fa-runway>
<fa-burst kind="star" style="position:absolute;left:120px;top:560px;width:220px;height:220px"></fa-burst>
<fa-burst kind="star" style="position:absolute;left:760px;top:600px;width:180px;height:180px"></fa-burst>
<fa-aircraft type="turboprop" livery="2" heading="98" size="130" style="position:absolute;left:470px;top:600px"></fa-aircraft>
<span class="fa-popup sc" style="position:absolute;left:110px;top:470px;transform:scale(1.6) rotate(-6deg)">+100</span>
<span class="fa-popup fa-popup--bonus sc" style="position:absolute;left:600px;top:470px;transform:scale(1.6) rotate(5deg)">+250 <small>Streak x3</small></span>
<span class="fa-popup fa-popup--success sc" style="position:absolute;left:640px;top:860px;transform:scale(1.5)"><fa-icon name="check"></fa-icon>Safe landing</span>
<div class="abs" style="left:0;right:0;top:280px;display:flex;justify-content:center">
  <span class="fa-streak fa-streak--hot" style="transform:scale(3.4) rotate(-6deg)"><fa-icon name="bolt"></fa-icon>x5 STREAK</span>
</div>
<div class="abs card" style="left:60px;right:60px;top:56px;padding:22px 36px;text-align:center">
  <p class="h-md">Chain your landings</p>
</div>
<p class="abs center" style="bottom:46px;margin:0;font:800 28px/34px var(--font-text);letter-spacing:.08em;color:var(--on-outline);text-shadow:0 3px 0 var(--outline)">EVERY SAFE LANDING GROWS THE MULTIPLIER</p>
`});

// 07 Keep your distance
visuals.push({ name: '07-keep-your-distance', w: S, h: S, bg: 'field', body: `
<fa-runway length="600" numbers="09/27" style="position:absolute;left:240px;top:760px;transform:rotate(-4deg)"></fa-runway>
${svg(S, S, path_('M300 470C380 520 470 540 560 520', 1, 'march', false) + path_('M800 420C730 480 640 520 600 530', 3, 'idle', false))}
<div class="abs sc" style="left:210px;top:350px;transform:scale(1.7)">${ring('<fa-aircraft type="jet" livery="1" heading="118"></fa-aircraft>')}</div>
<div class="abs sc" style="left:670px;top:300px;transform:scale(1.7)">${ring('<fa-aircraft type="turboprop" livery="3" heading="235"></fa-aircraft>')}</div>
<span class="fa-popup fa-popup--danger sc" style="position:absolute;left:395px;top:600px;transform:scale(1.6)"><fa-icon name="warning"></fa-icon>Too close</span>
<fa-cloud style="position:absolute;left:820px;top:640px;width:170px"></fa-cloud>
<div class="abs card" style="left:60px;right:60px;top:56px;padding:26px 40px">
  <p class="h-md">Keep your distance</p>
  <p class="sub" style="margin-top:8px">A dashed warning ring means two aircraft are too close. Redraw a path before they touch.</p>
</div>
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
visuals.push({ name: '09-night-shift', w: S, h: S, bg: 'field', theme: 'dark', body: `
<fa-runway length="600" numbers="27/09" zone="ok" style="position:absolute;left:220px;top:600px;transform:rotate(12deg)"></fa-runway>
<fa-helipad style="position:absolute;left:840px;top:430px"></fa-helipad>
${svg(S, S, path_('M920 840C820 760 640 760 560 690', 1, 'march') + path_('M756 412C820 400 880 430 896 486', 6))}
<fa-aircraft type="jumbo" livery="5" heading="290" size="150" style="position:absolute;left:880px;top:780px"></fa-aircraft>
<fa-aircraft type="helicopter" livery="6" heading="80" style="position:absolute;left:720px;top:376px"></fa-aircraft>
<fa-aircraft type="light-prop" livery="2" heading="60" style="position:absolute;left:120px;top:880px"></fa-aircraft>
<fa-cloud style="position:absolute;left:60px;top:640px;width:200px"></fa-cloud>
<div class="band sky" style="top:0;height:330px"></div>
<span class="abs" style="left:860px;top:60px;width:110px;height:110px;border-radius:50%;background:var(--paper-100);box-shadow:inset -22px -10px 0 var(--paper-300),0 0 0 var(--line-zone) var(--outline)"></span>
<p class="kick abs" style="left:70px;top:76px">New levels in the works</p>
<p class="h-xl abs" style="left:70px;top:118px">Night shift</p>
<div class="abs" style="left:70px;top:236px"><span class="tag" style="background:var(--sky-500)"><fa-icon name="star"></fa-icon>Coming soon</span></div>
`});

// 10 Score challenge
visuals.push({ name: '10-beat-the-best', w: S, h: S, bg: 'field', body: `
<fa-runway length="560" numbers="09/27" style="position:absolute;left:260px;top:520px;transform:rotate(-14deg)"></fa-runway>
<fa-aircraft type="jet" livery="1" heading="200" style="position:absolute;left:150px;top:200px"></fa-aircraft>
<fa-aircraft type="jumbo" livery="5" heading="40" style="position:absolute;left:820px;top:760px"></fa-aircraft>
<div class="fa-scrim" style="position:absolute;inset:0"></div>
<p class="abs center" style="top:48px;margin:0;font:700 72px/76px var(--font-display);color:var(--on-outline)">Can you beat 18 200?</p>
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

// Play Store feature graphic, 1024 x 500
visuals.push({ name: 'play-store-feature-graphic', w: 1024, h: 500, bg: 'sky', body: `
<fa-cloud class="abs" style="left:-20px;top:20px;width:150px"></fa-cloud>
<fa-cloud class="abs" style="left:420px;top:400px;width:130px"></fa-cloud>
<div class="abs" style="left:40px;top:70px">
  <p class="kick" style="font-size:17px;margin:0 0 26px 8px">A top-down air traffic game</p>
  <h1 class="fa-wordmark" style="font-size:92px;line-height:88px;-webkit-text-stroke-width:15px;text-shadow:0 8px 0 var(--outline)">Final<span>Approach</span></h1>
  <div style="margin:44px 0 0 8px"><span class="tag" style="font-size:22px;padding:8px 18px"><fa-icon name="play"></fa-icon>Draw. Land. Repeat.</span></div>
</div>
<div class="abs field" style="left:620px;top:-40px;width:480px;height:600px;transform:rotate(8deg);border-radius:var(--radius-dialog);box-shadow:0 0 0 var(--line-zone) var(--outline),-10px 10px 0 3px var(--contact);overflow:hidden">
  <div style="position:absolute;inset:0;transform:rotate(-8deg) translate(-56px,10px)">
    <fa-runway length="380" numbers="09/27" zone="ok" style="position:absolute;left:90px;top:330px;transform:rotate(-14deg)"></fa-runway>
    <fa-helipad style="position:absolute;left:330px;top:120px"></fa-helipad>
    ${svg(520, 600, path_('M130 120C40 250 90 360 170 380', 1, 'march') + path_('M420 360C440 300 420 240 386 176', 6))}
    <fa-aircraft type="jet" livery="1" heading="200" selected style="position:absolute;left:96px;top:84px"></fa-aircraft>
    <fa-aircraft type="helicopter" livery="6" heading="340" style="position:absolute;left:390px;top:340px"></fa-aircraft>
    <fa-aircraft type="jumbo" livery="5" heading="300" style="position:absolute;left:300px;top:470px"></fa-aircraft>
    <span class="fa-popup fa-popup--bonus" style="position:absolute;left:220px;top:250px">+250 <small>Streak x3</small></span>
  </div>
</div>
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
