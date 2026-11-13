// Builds showcase.html, the design system on one page, from its sources:
// tokens.json, bundle.css, bundle.js, the fonts in fonts/, the page styles and the kept v1
// sections in tools/showcase/, and the component examples written below.
// usage, from the repository root: node docs/design-system/tools/build-showcase.cjs
// Other tools reuse the same head and kit: require('./build-showcase.cjs').parts()
const fs = require('fs');
const path = require('path');
const DS = path.join(__dirname, '..');
const read = f => fs.readFileSync(path.join(DS, f), 'utf8').split('\r\n').join('\n');
const part = n => read(`tools/showcase/parts/${n}`).trimEnd();
const t = JSON.parse(read('tokens.json'));
const FA = require(path.join(DS, 'bundle.js'));
const esc = s => String(s).replace(/&/g, '&amp;').replace(/</g, '&lt;');

// ---------------------------------------------------------------- tokens as CSS
const live = k => !k.deprecated;
const cssValue = v => { const m = /^\{(.+)\}$/.exec(v); return m ? `var(--${m[1]})` : v; };
function tokensCss() {
  const light = [], dark = [], dawn = [];
  for (const c of t.color.tokens.filter(live)) {
    if (typeof c.value === 'string') { light.push(`--${c.name}:${cssValue(c.value)}`); continue; }
    light.push(`--${c.name}:${cssValue(c.value.light)}`);
    if (c.value.dark) dark.push(`--${c.name}:${cssValue(c.value.dark)}`);
    if (c.value.dawn) dawn.push(`--${c.name}:${cssValue(c.value.dawn)}`);
  }
  const rest = [];
  for (const fam of ['spacing', 'radius', 'line', 'size', 'duration', 'easing', 'opacity', 'zIndex'])
    for (const k of t[fam].tokens.filter(live)) rest.push(`--${k.name}:${k.value}`);
  // the float shadow is the one shadow token v2 keeps
  const float = t.shadow.tokens.find(k => k.name === 'shadow-float');
  rest.push(`--shadow-float:${float.value.light}`);
  for (const [n, v] of Object.entries(t.type.families)) rest.push(`--font-${n}:${v}`);
  let s = `:root,[data-theme="light"]{${light.join(';')}}\n[data-theme="dark"]{${dark.join(';')}}\n[data-theme="dawn"]{${dawn.join(';')}}\n:root{${rest.join(';')}}\n`;
  for (const g of t.type.groups) for (const st of g.styles)
    s += `.${st.name}{font-family:var(--font-${st.family || g.family});font-size:${st.fontSize};line-height:${st.lineHeight};font-weight:${st.fontWeight};${st.letterSpacing ? `letter-spacing:${st.letterSpacing};` : ''}${st.fontVariantNumeric ? `font-variant-numeric:${st.fontVariantNumeric};` : ''}}\n`;
  return s;
}
function fontsCss() {
  return t.type.fonts.map(f => `@font-face{font-family:"${f.family}";src:url(data:font/woff2;base64,${fs.readFileSync(path.join(DS, f.file)).toString('base64')}) format("woff2");font-weight:${f.weight};font-style:${f.style};font-display:swap}`).join('\n') + '\n';
}
/** The parts other pages (the visuals) are built from: everything for the <head>, and the kit script. */
function parts() {
  return { head: `<meta name="viewport" content="width=device-width, initial-scale=1">\n<style>\n${tokensCss()}${fontsCss()}${read('bundle.css')}`, kit: `<script>\n${read('bundle.js')}\n</script>` };
}
module.exports = { parts, tokensCss };
if (require.main !== module) return;

// ---------------------------------------------------------------- foundations
const principles = [
  ['Toy scale', 'Sizes are exaggerated, not realistic. A jumbo is nearly twice a light prop.'],
  ['Silhouette before colour', 'Types, zones and states are told apart by shape first. Colour confirms.'],
  ['The field stays clear', 'UI lives in flat strips at the edges. Nothing covers a landing zone.'],
];
const W = [
  ['One sun, top-left', 'Every object is lit from the top-left: a lit tone to the north-west, a shade tone to the south-east, a hard shadow cast down-right.'],
  ['Three tones per material', 'Light, base and shade, as flat shapes. Never a gradient. A sprite uses at most three materials.'],
  ['Navy outside, tinted inside', 'The silhouette is outline. Every line inside it is the material’s darkest tone.'],
  ['Line weight is rank', 'Aircraft 4, landing zones 3, scenery 2.5, inner detail 1.25. The thickest line is always an aircraft.'],
  ['Standing things show a south wall', 'Buildings, trees and the tower lean a little: roof on top, wall below, in one of three height tiers. They never rotate.'],
  ['Flat ground has no outline', 'Roads, fields, water and aprons are edged in their own darker tone. Only things with height, and landing zones, earn a navy line.'],
  ['Edges are transitions', 'A runway has a shoulder and rough grass, water has shallows and sand, a road has a kerb and a verge.'],
  ['Detail clusters, open ground rests', 'Tufts, flowers and props gather around objects and edges. Only the airfield is mown in stripes.'],
  ['Aircraft own the top of the range', 'The brightest white and the most saturated colours belong to aircraft and paths. Scenery whites are cream.'],
];
const U = [
  ['Moulded, not boxed', 'A control is a face, a highlight along its top edge and a lip in its own 700 tone, wrapped by one thin outline.'],
  ['A lip means pressable', 'Buttons, cards, the toggle knob and the selected segment have a lip. Readouts, chips and stickers are flat.'],
  ['Thin line, thick form', 'The UI outline is 2.5dp. Depth comes from the lip and a flat contact shadow.'],
  ['Tinted inside', 'Dividers, keylines and chip edges take a darker tone of their surface. Navy is for the outer edge and for text.'],
  ['Nested radii', 'An inner corner is the outer radius minus the gap between them.'],
  ['Tower paperwork', 'HUD readouts are flight strips, dialogs are boarding passes with a perforated stub, verdicts are stamps.'],
  ['Disabled is flat, not faded', 'A disabled control loses its colour, lip and shine and keeps full opacity.'],
];
const rule = (id, r) => `<div class="sc-rule"><i>${id}</i><b>${r[0]}</b><span>${r[1]}</span></div>`;
const secPrinciples = `<section class="sc-section sc-keep" id="principles">
  <h2><small>Foundations</small>Principles</h2>
  <div class="sc-grid">${principles.map((p, i) => `<div class="sc-card sc-prin"><i>${i + 1}</i><b>${p[0]}</b><span>${p[1]}</span></div>`).join('')}</div>
  <h3 class="sc-sub">World: nine rules for everything on the ground</h3>
  <div class="sc-card sc-rules2">${W.map((r, i) => rule('W' + (i + 1), r)).join('')}</div>
  <h3 class="sc-sub">UI: seven rules for controls and panels</h3>
  <div class="sc-card sc-rules2">${U.map((r, i) => rule('U' + (i + 1), r)).join('')}</div>
</section>`;

const colours = t.color.tokens.filter(live);
const groups = [
  ['Ink and edges', n => /^(outline|ink|ink-muted|ink-3|on-|focus)/.test(n)],
  ['Sky', n => /^(bg-sky|sky-)/.test(n)],
  ['Paper: UI surfaces', n => /^paper-/.test(n)],
  ['Accents', n => /^(sun|tangerine|cherry|mint)(-|$)/.test(n)],
  ['Status', n => /^(success|danger|warning|info)/.test(n)],
  ['Flight paths and liveries', n => /^path-/.test(n)],
  ['Ground', n => /^(bg-field|grass-|asphalt-|concrete-|road-|marking|water-|sand-|soil-|foam)/.test(n)],
  ['Scenery materials (decoration only)', n => /^(leaf|pine|wood|straw|roof|wall|tin|stone|hide|denim)-|^(glass-700|window|beacon|muzzle)$/.test(n)],
  ['Aircraft and sky objects', n => /^(fuselage|glass$|glass-shine|prop-blur|cloud|select-ring|zone-|star|poof)/.test(n)],
  ['Depth', n => /^(shadow-|contact|scrim|blackout)/.test(n)],
  ['Sunset (splash page only)', n => /^sunset-/.test(n)],
  ['Lights (Dawn levels only)', n => /^light-/.test(n)],
];
const val = c => typeof c.value === 'string' ? (/^\{/.test(c.value) ? '= ' + c.value.slice(1, -1) : c.value)
  : (/^\{/.test(c.value.light) ? '= ' + c.value.light.slice(1, -1) : c.value.light) + (c.value.dark ? `<br>night ${c.value.dark}` : '') + (c.value.dawn ? `<br>dawn ${c.value.dawn}` : '');
const swatch = c => `<div class="sc-sw"><span class="sc-sw__chip" style="background:var(--${c.name})"></span><span class="sc-sw__name">${c.name}</span><span class="sc-sw__val">${val(c)}</span><span class="sc-sw__use">${esc(c.usage)}</span></div>`;
const seen = new Set();
let colourBody = '';
for (const [title, test] of groups) {
  const list = colours.filter(c => !seen.has(c.name) && test(c.name));
  list.forEach(c => seen.add(c.name));
  if (list.length) colourBody += `<h3 class="sc-sub">${title}</h3><div class="sc-card"><div class="sc-swatches">${list.map(swatch).join('')}</div></div>\n`;
}
const left = colours.filter(c => !seen.has(c.name));
if (left.length) throw new Error('colour tokens without a group: ' + left.map(c => c.name).join(', '));
const secColour = `<section class="sc-section" id="colour">
  <h2><small>Foundations</small>Colour</h2>
  <p class="sc-lede">${colours.length} colour tokens in three themes: Day, Night and Dawn. Materials and accents are ramps with fixed roles: 300 is the lit tone, 500 the base, 700 the shade or the lip, 900 the tinted inner line, 100 and 200 a highlight. Swatches follow the theme switch; values list Day first.</p>
${colourBody}</section>`;

const fontOf = { display: 'DynaPuff', body: 'Nunito', text: 'Inconsolata' };
const secType = `<section class="sc-section" id="type">
  <h2><small>Foundations</small>Type</h2>
  <p class="sc-lede">DynaPuff for display type, titles, button labels and scores. Nunito for running text, never lighter than 600. Inconsolata for labels, readouts and callsigns, never lighter than 700: it is the voice of the tower.</p>
${t.type.groups.map(g => `  <h3 class="sc-sub">${g.name} · ${fontOf[g.family]}</h3><div class="sc-card sc-type">${g.styles.map(s => `<div class="sc-type__row"><span class="${s.name}">${esc(s.sample)}</span><span class="sc-type__meta">${s.name} · ${s.fontSize}/${s.lineHeight} · ${s.fontWeight} · ${esc(s.usage)}</span></div>`).join('')}</div>`).join('\n')}
</section>`;

const px = v => parseFloat(v);
const table = (head, rows) => `<div class="sc-card"><div class="sc-scroll"><table class="sc-table"><thead><tr>${head.map(h => `<th>${h}</th>`).join('')}</tr></thead><tbody>${rows.map(r => `<tr>${r.map(c => `<td>${c}</td>`).join('')}</tr>`).join('')}</tbody></table></div></div>`;
const fam = (name, demo) => t[name].tokens.filter(live).map(k => [`<b>${k.name}</b>`, `<span class="sc-num">${k.value}</span>`, demo(k), esc(k.usage)]);
const lipNames = ['lip-sm', 'lip-md', 'lip-lg', 'lip-panel', 'lip-dialog', 'lip-pressed', 'lip-inset', 'lip-edge', 'highlight', 'well-inset', 'contact-offset', 'contact-offset-field', 'contact-offset-overlay'];
const sizeRows = t.size.tokens.filter(live);
const secShape = `<section class="sc-section" id="shape">
  <h2><small>Foundations</small>Shape and depth</h2>
  <p class="sc-lede">A 4px spacing grid, five line ranks, nested radii, and depth from a lip and a flat contact shadow. Nothing in the UI is blurred.</p>
  <h3 class="sc-sub">Line ranks</h3>${table(['Token', 'Value', 'Weight', 'Use'], t.line.tokens.filter(live).filter(k => /^line-/.test(k.name)).map(k => [`<b>${k.name}</b>`, `<span class="sc-num">${k.value}</span>`, `<span class="sc-line" style="height:${k.value}${k.name === 'line-detail' ? ';background:var(--paper-500)' : ''}"></span>`, esc(k.usage)]))}
  <h3 class="sc-sub">Depth: lips and contact shadows</h3>${table(['Token', 'Value', 'Use'], sizeRows.filter(k => lipNames.includes(k.name)).map(k => [`<b>${k.name}</b>`, `<span class="sc-num">${k.value}</span>`, esc(k.usage)]))}
  <div class="sc-demo fa-stage fa-stage--sky"><div class="fa-item"><span class="fa-chip">1 runway</span><span class="fa-note">Flat: information</span></div><div class="fa-item"><div class="fa-strip"><span class="fa-strip__label">Best</span><span class="fa-strip__value">18&#8201;200</span></div><span class="fa-note">Flat on the field: a readout</span></div><div class="fa-item"><button class="fa-btn">Resume</button><span class="fa-note">A lip: pressable</span></div><div class="fa-item"><button class="fa-btn is-pressed">Resume</button><span class="fa-note">Pressed: on its lip</span></div></div>
  <h3 class="sc-sub">Radius</h3>${table(['Token', 'Value', 'Corner', 'Use'], fam('radius', k => `<span class="sc-rad" style="border-radius:${px(k.value) > 60 ? '20px' : k.value}"></span>`))}
  <h3 class="sc-sub">Spacing</h3>${table(['Token', 'Value', 'Scale', 'Use'], fam('spacing', k => `<span class="sc-bar-v" style="width:${k.value}"></span>`))}
  <h3 class="sc-sub">Flight path and rings</h3>${table(['Token', 'Value', 'Use'], t.line.tokens.filter(live).filter(k => !/^line-/.test(k.name)).map(k => [`<b>${k.name}</b>`, `<span class="sc-num">${k.value}</span>`, esc(k.usage)]))}
  <h3 class="sc-sub">Sizes</h3>${table(['Token', 'Value', 'Use'], sizeRows.filter(k => !lipNames.includes(k.name)).map(k => [`<b>${k.name}</b>`, `<span class="sc-num">${k.value}</span>`, esc(k.usage)]))}
  <h3 class="sc-sub">Opacity</h3>${table(['Token', 'Value', 'Use'], t.opacity.tokens.filter(live).map(k => [`<b>${k.name}</b>`, `<span class="sc-num">${k.value}</span>`, esc(k.usage)]))}
  <h3 class="sc-sub">Layers</h3>${table(['Token', 'Value', 'What is on it'], t.zIndex.tokens.map(k => [`<b>${k.name}</b>`, `<span class="sc-num">${k.value}</span>`, esc(k.usage)]))}
</section>`;

// ---------------------------------------------------------------- scenery and ground
const order = ['Plants', 'Farm', 'Airfield', 'Vehicles', 'Animals', 'Water', 'Ground'];
const groundClass = { water: ' sc-scn__tile--water', apron: ' sc-scn__tile--apron', sand: ' sc-scn__tile--sand' };
const sprites = (size) => order.map(gr => {
  const names = Object.keys(FA.WORLD).filter(n => FA.WORLD[n].group === gr);
  return names.length ? `<p class="sc-scn__group">${gr}</p><div class="sc-scn">${names.map(n => `<div class="sc-scn__item"><span class="sc-scn__tile${groundClass[FA.WORLD[n].ground] || ''}"><fa-scenery name="${n}" size="${size}"></fa-scenery></span><span class="sc-scn__name">${FA.WORLD[n].label}</span><span class="sc-scn__tier">${FA.WORLD[n].tier === 'flat' ? 'flat' : 'tier ' + FA.WORLD[n].tier}${FA.WORLD[n].rotates ? ' · rotates' : ' · fixed'}</span></div>`).join('')}</div>` : '';
}).join('');
const secScenery = `<section class="sc-section" id="scenery">
  <h2><small>Assets</small>Scenery</h2>
  <p class="sc-lede">Everything on the ground that is not a landing zone, drawn on the nine world rules. Standing things show a south wall and never rotate; things that move are strictly top-down. Each sprite carries a height tier and the silhouette of its cast shadow.</p>
  <div class="sc-card"><ul class="sc-rules">
    <li>Silhouette in <code>outline</code> at <code>line-ui</code>; inner lines in the material's 700 or 900 step at <code>line-detail</code>. An aircraft keeps <code>line-hero</code>.</li>
    <li>At most three materials in one sprite, each in flat tones of its ramp. Glass, lamps and small trims do not count as a material.</li>
    <li>Never <code>fuselage</code>, a path colour or <code>sun-500</code>. Whites are <code>wall-100</code> or <code>hide-100</code>. Only the tower cab (<code>cherry</code>) and the windsock (<code>tangerine</code>) keep an accent.</li>
    <li>Height tiers: S throws its shadow 3 by 4 units, M 12 by 14, L 11 by 13 from a raised crown. All hard shadows share one layer in <code>shadow-cast</code> at <code>opacity-shadow-cast</code>.</li>
    <li>Windows take <code>window</code>: glass by day, a warm light in Night and Dawn.</li>
    <li>Scenery never covers a runway, a helipad or a landing zone, and only flat ground lies under the HUD band.</li>
  </ul></div>
  <h3 class="sc-sub">Day, at twice world size</h3>
  <div class="sc-card">${sprites(2)}</div>
  <h3 class="sc-sub">Night and Dawn, from the same sprites</h3>
  <div class="sc-night" data-theme="dark"><p class="sc-lede">Night: only the token values change. Windows are lit.</p><div class="sc-scn">${['farmhouse', 'barn', 'tower', 'terminal', 'hangar', 'tree', 'pine', 'cow', 'car', 'fuel-truck', 'palm', 'sailboat'].map(n => `<div class="sc-scn__item"><span class="sc-scn__tile${groundClass[FA.WORLD[n].ground] || ''}"><fa-scenery name="${n}" size="1.5"></fa-scenery></span><span class="sc-scn__name">${FA.WORLD[n].label}</span></div>`).join('')}</div></div>
  <div class="sc-night" data-theme="dawn"><p class="sc-lede">Dawn: the ground is dim and warm, the windows are lit.</p><div class="sc-scn">${['hangar', 'tower', 'acacia', 'bush', 'rock', 'zebra', 'elephant', 'fuel-truck'].map(n => `<div class="sc-scn__item"><span class="sc-scn__tile"><fa-scenery name="${n}" size="1.5"></fa-scenery></span><span class="sc-scn__name">${FA.WORLD[n].label}</span></div>`).join('')}</div></div>
</section>`;
const crop = (level, box, cap) => `<figure class="sc-crop"><fa-scene level="${level}" ground crop="${box}"></fa-scene><figcaption>${cap}</figcaption></figure>`;
const secGround = `<section class="sc-section" id="ground">
  <h2><small>Assets</small>Ground and landing zones</h2>
  <p class="sc-lede">Flat ground has no navy line and never meets another material on a bare edge. Runways and helipads keep their v1 footprint and are the only ground shapes outlined, at <code>line-zone</code>.</p>
  <div class="sc-demo fa-stage fa-stage--field fa-stage--col">
    <div class="fa-item"><fa-runway length="760" numbers="09/27" zone="ok"></fa-runway><span class="fa-chip fa-chip--success"><fa-icon name="check"></fa-icon>Two-way runway, valid landing</span></div>
    <div class="fa-item"><fa-runway length="620" numbers="09/27" mode="one-way"></fa-runway><span class="fa-chip">One-way runway: only the landing end is marked</span></div>
    <div style="display:flex;flex-wrap:wrap;gap:var(--space-6);justify-content:center"><div class="fa-item"><fa-helipad zone="ok"></fa-helipad><span class="fa-chip fa-chip--success"><fa-icon name="check"></fa-icon>Helipad, valid</span></div><div class="fa-item"><fa-helipad zone="no"></fa-helipad><span class="fa-chip fa-chip--cherry"><fa-icon name="warning"></fa-icon>Wrong zone</span></div><div class="fa-item"><fa-helipad></fa-helipad><span class="fa-chip">At rest</span></div></div>
  </div>
  <div class="sc-crops">
    ${crop('meadow', '500 535 500 281', '<b>Runway.</b> Shoulder and rough-grass fringe, threshold bars, aiming blocks, touchdown marks, tyre rubber, edge lights, green threshold lights and four approach lights.')}
    ${crop('meadow', '1010 240 400 225', '<b>Helipad and taxiway.</b> A concrete pad with slab joints and corner lights. Taxiways are a lighter asphalt with a yellow centre line and a hold-short bar.')}
    ${crop('meadow', '560 225 470 264', '<b>Apron.</b> Slab joints and stand marks are tinted. Buildings stand on their south walls; parked aircraft are metal, with no livery.')}
    ${crop('meadow', '1500 420 400 225', '<b>Water and woods.</b> Open water, shallows, sand, damp grass. Trees overlap north to south. The meadow is not striped.')}
    ${crop('meadow', '180 840 500 281', '<b>Farm.</b> A road with kerb and verge, a dirt track, crop fields edged in their own tone, a hedge with one shared outline.')}
    ${crop('island', '760 780 500 281', '<b>Shore.</b> Sand, shallows and a line of foam; a jetty, a sailboat and its wake.')}
  </div>
</section>`;

// ---------------------------------------------------------------- components
const ic = n => `<fa-icon name="${n}"></fa-icon>`;
const comp = (id, title, desc, demo) => `<article class="sc-comp" id="c-${id}">
  <header><h3>${title}</h3><p>${desc}</p></header>
  ${demo}
</article>`;
const stage = (kind, inner, extra = '') => `<div class="sc-demo fa-stage fa-stage--${kind}${extra}">${inner}</div>`;
const strip = (label, value, cls = '') => `<div class="fa-strip">${label ? `<span class="fa-strip__label${cls}">${label}</span>` : ''}<span class="fa-strip__value">${value}</span></div>`;
const lives = lost => `<div class="fa-strip" role="img" aria-label="${3 - lost} of 3 lives"><span class="fa-strip__value fa-lives">${[0, 1, 2].map(i => `<fa-icon class="fa-life${i >= 3 - lost ? ' fa-life--lost' : ''}" name="heart"></fa-icon>`).join('')}</span></div>`;
const next = (type, livery, edge) => `<div class="fa-strip fa-next"><span class="fa-strip__label">Next</span><span class="fa-strip__value"><fa-aircraft type="${type}" livery="${livery}" heading="90" flat></fa-aircraft><span class="fa-next__edge">${ic(edge)}</span></span></div>`;
const hud = (o = {}) => `<div class="fa-hud" style="position:absolute;inset:0 0 auto 0">
  <div class="fa-hud__group">${strip(`${ic('star')}Score`, o.score || '12&#8201;450', ' fa-strip__label--sun')}<span class="fa-streak">${ic('bolt')}${o.streak || 'x3'}</span></div>
  <div class="fa-hud__group">${next(o.next || 'jet', o.livery || 3, o.edge || 'arrow-left')}${lives(o.lost === undefined ? 1 : o.lost)}<button class="fa-iconbtn fa-iconbtn--hud" aria-label="Pause">${ic('pause')}</button></div>
</div>`;
const stat = (k, v) => `<div class="fa-stat"><dt>${k}</dt><dd>${v}</dd></div>`;
const stats = `<dl class="fa-dialog__stats">${stat('Score', '12&#8201;450')}${stat('Landed', '37')}${stat('Best streak', 'x6')}${stat('Best', '18&#8201;200')}</dl>`;
const paused = `<div class="fa-dialog" role="dialog" aria-labelledby="d-paused"><header class="fa-dialog__head"><span class="fa-dialog__eyebrow">Tower · holding</span><h2 class="fa-dialog__title" id="d-paused">Paused</h2></header>
  <p class="fa-dialog__body">Three aircraft are holding. They will wait for you.</p><hr class="fa-perf">
  <div class="fa-dialog__actions"><button class="fa-btn fa-btn--block">${ic('play')}Resume</button><button class="fa-btn fa-btn--ghost fa-btn--block">${ic('restart')}Restart</button><button class="fa-btn fa-btn--ghost fa-btn--block">${ic('home')}Main menu</button></div></div>`;
const over = `<div class="fa-dialog" role="dialog" aria-labelledby="d-over"><header class="fa-dialog__head fa-dialog__head--danger"><span class="fa-dialog__eyebrow">Tower log · runway 27</span><h2 class="fa-dialog__title" id="d-over">Game over</h2></header>
  <p class="fa-dialog__body">Two aircraft got too close over runway 27.<br><span class="fa-stamp">Too close</span></p><hr class="fa-perf">
  ${stats}<div class="fa-dialog__actions"><button class="fa-btn fa-btn--block">${ic('restart')}Try again</button><button class="fa-btn fa-btn--ghost fa-btn--block">Choose airport</button></div></div>`;
const overCompact = `<div class="fa-dialog fa-dialog--compact" role="dialog" aria-labelledby="d-over-c"><div class="fa-dialog__lead"><header class="fa-dialog__head fa-dialog__head--danger"><span class="fa-dialog__eyebrow">Tower log · runway 27</span><h2 class="fa-dialog__title" id="d-over-c">Game over</h2></header>
  <p class="fa-dialog__body">Two aircraft got too close over runway 27.<br><span class="fa-stamp">Too close</span></p></div>
  <div class="fa-dialog__stub">${stats}<div class="fa-dialog__actions"><button class="fa-btn fa-btn--sm fa-btn--block">${ic('restart')}Try again</button><button class="fa-btn fa-btn--sm fa-btn--ghost fa-btn--block">Choose airport</button></div></div></div>`;
const pausedCompact = `<div class="fa-dialog fa-dialog--compact" role="dialog" aria-labelledby="d-paused-c"><div class="fa-dialog__lead"><header class="fa-dialog__head"><span class="fa-dialog__eyebrow">Tower · holding</span><h2 class="fa-dialog__title" id="d-paused-c">Paused</h2></header>
  <p class="fa-dialog__body">Three aircraft are holding. They will wait for you.</p></div>
  <div class="fa-dialog__stub"><div class="fa-dialog__actions"><button class="fa-btn fa-btn--sm fa-btn--block">${ic('play')}Resume</button><button class="fa-btn fa-btn--sm fa-btn--ghost fa-btn--block">${ic('restart')}Restart</button><button class="fa-btn fa-btn--sm fa-btn--ghost fa-btn--block">${ic('home')}Main menu</button></div></div></div>`;
const card = (level, name, cropBox, tags, best, locked) => `<button class="fa-levelcard"${locked ? ' aria-disabled="true"' : ''}><span class="fa-levelcard__art"><fa-scene level="${level}" ground crop="${cropBox}"></fa-scene>${locked ? `<span class="fa-levelcard__lock"><span>${ic('lock')}</span></span>` : (best ? `<span class="fa-levelcard__badge"><span class="fa-chip fa-chip--sun">${ic('star')}${best}</span></span>` : '')}</span><span class="fa-levelcard__body"><span class="fa-levelcard__title">${name}</span><span class="fa-levelcard__tags">${tags.map(x => `<span class="fa-chip${locked ? ' fa-chip--plain' : ''}">${x}</span>`).join('')}</span></span></button>`;
const cards = card('meadow', 'Meadow Field', '330 190 1330 700', ['1 runway', '1 helipad'], '18&#8201;200') + card('island', 'Island', '300 150 1330 780', ['1 runway', '1 helipad'], '9&#8201;750') + card('savanna', 'Savanna', '300 190 1330 700', ['Land 50 to unlock'], '', true);

const components = [
  comp('Button', 'Button', 'The moulded button for every text action in menus and dialogs.', stage('sky', `<div class="fa-item" style="flex-direction:row;flex-wrap:wrap;justify-content:center"><button class="fa-btn">${ic('play')}Take off</button><button class="fa-btn fa-btn--secondary">Choose airport</button><button class="fa-btn fa-btn--ghost">${ic('settings')}Settings</button><button class="fa-btn fa-btn--danger">Quit flight</button></div>
<div class="fa-item" style="flex-direction:row;flex-wrap:wrap;justify-content:center"><button class="fa-btn fa-btn--sm">${ic('restart')}Retry</button><button class="fa-btn">Resume</button><button class="fa-btn fa-btn--lg">${ic('play')}Play</button></div>
<div class="fa-item" style="flex-direction:row;flex-wrap:wrap;justify-content:center;gap:var(--space-5)"><button class="fa-btn is-pressed">Pressed</button><button class="fa-btn is-focus">Focused</button><button class="fa-btn" disabled>Disabled</button></div>`, ' fa-stage--col')),
  comp('IconButton', 'Icon Button', 'A square moulded button that carries one icon and no text: pause in the HUD, close and back in menus.', stage('field', `<button class="fa-iconbtn fa-iconbtn--hud" aria-label="Pause">${ic('pause')}</button><button class="fa-iconbtn fa-iconbtn--sm" aria-label="Sound on">${ic('sound-on')}</button><button class="fa-iconbtn fa-iconbtn--sm" aria-label="Sound off">${ic('sound-off')}</button><button class="fa-iconbtn" aria-label="Restart">${ic('restart')}</button><button class="fa-iconbtn fa-iconbtn--sun" aria-label="Play">${ic('play')}</button><button class="fa-iconbtn" aria-label="Home">${ic('home')}</button><button class="fa-iconbtn is-pressed" aria-label="Close, pressed">${ic('close')}</button><button class="fa-iconbtn" aria-label="Music" disabled>${ic('music')}</button>`)),
  comp('Toggle', 'Toggle', 'An on/off switch for settings that apply immediately.', stage('sky', `<div class="fa-panel" style="width:330px"><ul class="fa-rows" style="gap:var(--space-3)">
    <li class="fa-setting"><span id="t1">Sound effects<small>Engines, dings and boings</small></span><button class="fa-toggle" role="switch" aria-checked="true" aria-labelledby="t1"></button></li>
    <li class="fa-setting"><span id="t2">Music<small>Light and upbeat</small></span><button class="fa-toggle" role="switch" aria-checked="false" aria-labelledby="t2"></button></li>
    <li class="fa-setting"><span id="t3" style="color:var(--ink-3)">Callsign tags<small style="color:var(--ink-3)">Unlocks at 5 000 points</small></span><button class="fa-toggle" role="switch" aria-checked="false" aria-labelledby="t3" disabled></button></li>
  </ul></div>`)),
  comp('SegmentedControl', 'Segmented Control', 'A row of two to four mutually exclusive choices, one always selected.', stage('sky', `<div class="fa-panel" style="width:360px"><span class="label" id="dl" style="display:block;color:var(--ink-muted);margin-bottom:var(--space-2)">DIFFICULTY</span>
  <div class="fa-segmented" role="group" aria-labelledby="dl"><button class="fa-segment" aria-pressed="false" data-note="No lives lost on a miss. Fewer aircraft.">Cadet</button><button class="fa-segment" aria-pressed="true" data-note="A collision costs one life. More aircraft, more types.">Controller</button><button class="fa-segment" aria-pressed="false" data-note="One mistake ends the level. Full traffic.">Chief</button></div>
  <p id="dn" style="margin-top:var(--space-3)">A collision costs one life. More aircraft, more types.</p></div>`)),
  comp('Chip', 'Chip', 'A flat, tinted tag that names a property: landing zones, a best score, a lock reason, a status. Never pressable.', stage('surface', `<span class="fa-chip">1 runway</span><span class="fa-chip">1 helipad</span><span class="fa-chip fa-chip--sun">${ic('star')}Best 18&#8201;200</span><span class="fa-chip fa-chip--success">${ic('check')}Landed</span><span class="fa-chip fa-chip--cherry">${ic('warning')}Too close</span><span class="fa-chip fa-chip--tangerine">${ic('bolt')}Streak</span><span class="fa-chip fa-chip--plain">${ic('lock')}Locked</span>`)),
  comp('FlightStrip', 'Flight Strip', 'The flat HUD readout: an optional labelled cell and a value. It replaces the v1 HUD chip.', stage('field', `${strip(`${ic('star')}Score`, '12&#8201;450', ' fa-strip__label--sun')}${strip('Best', '18&#8201;200')}${strip('', '3:20')}${lives(1)}${next('jumbo', 5, 'arrow-left')}`)),
  comp('HudBar', 'Hud Bar', 'The gameplay overlay: score and streak on the left; next aircraft, lives and pause on the right. Pause is its only control.', `<div class="sc-demo fa-stage fa-stage--field" style="display:block;position:relative;height:150px;padding:0">${hud()}</div>`),
  comp('ScoreCounter', 'Score Counter', 'The live score and the best score, each in a flight strip.', stage('field', `${strip(`${ic('star')}Score`, '12&#8201;450', ' fa-strip__label--sun')}${strip('Best', '18&#8201;200')}`)),
  comp('SpawnCounter', 'Spawn Counter', 'How many aircraft of one type have spawned in the running level, in a flight strip.', stage('field', `<div class="fa-hud__counters">${[['light-prop', 12], ['turboprop', 7], ['jet', 4], ['jumbo', 1], ['helicopter', 3]].map(c => `<div class="fa-strip fa-spawncounter"><span class="fa-strip__value"><fa-aircraft type="${c[0]}" flat></fa-aircraft>${c[1]}</span></div>`).join('')}</div>`)),
  comp('LivesMeter', 'Lives Meter', 'Remaining lives as a row of hearts in a flight strip. A lost life is a paper heart.', stage('field', `${lives(0)}${lives(1)}${lives(2)}`)),
  comp('NextAircraftHint', 'Next Aircraft Hint', 'A flight strip that previews the next aircraft and the edge it will enter from.', stage('field', `${next('jet', 3, 'arrow-left')}${next('helicopter', 6, 'arrow-right')}`)),
  comp('Sticker', 'Sticker: Streak Badge and Score Popup', 'The celebratory pills: tilted, with a paper rim. They are the only tilted shapes, and they are flat.', stage('field', `<span class="fa-streak">${ic('bolt')}x3</span><span class="fa-streak fa-streak--hot">${ic('bolt')}x8</span><span class="fa-popup">+100</span><span class="fa-popup fa-popup--bonus">+250 <small>Streak x3</small></span><span class="fa-popup fa-popup--success">${ic('check')}Landed</span><span class="fa-popup fa-popup--danger">${ic('warning')}Too close</span>`)),
  part('Aircraft.html'), part('FlightPath.html'),
  comp('Runway', 'Runway', 'A landing strip for airplanes, defined by position, heading, length and its two numbers. See Ground and landing zones.', stage('field', `<fa-runway length="520" numbers="09/27"></fa-runway><fa-runway length="520" numbers="13/31" mode="one-way" zone="no"></fa-runway>`, ' fa-stage--col')),
  comp('Helipad', 'Helipad', 'A round landing spot for helicopters on a concrete pad.', stage('field', `<fa-helipad></fa-helipad><fa-helipad zone="ok"></fa-helipad>`)),
  comp('Cloud', 'Cloud', 'An ambient cloud that drifts across the field above the aircraft. Airborne: it keeps the soft shadow.', stage('field', `<fa-cloud data-loop></fa-cloud><fa-cloud style="width:120px"></fa-cloud>`)),
  comp('Windsock', 'Windsock', 'A striped windsock on a pole beside the runway.', stage('field', `<fa-scenery name="windsock" size="2"></fa-scenery>`)),
  part('Effects.html'), part('ProximityRing.html'),
  comp('Panel', 'Panel', 'A paper container for grouped content in menus, with an optional header band.', stage('sky', `<div class="fa-panel" style="width:340px"><div class="fa-panel__head"><h4 class="fa-panel__title">How to play</h4><span class="fa-chip fa-chip--plain">3 steps</span></div><ul class="fa-rows"><li><span class="fa-icontile">${ic('tap')}</span>Tap an aircraft to select it.</li><li><span class="fa-icontile">${ic('plane')}</span>Drag to a runway. Planes need to line up with it.</li><li><span class="fa-icontile">${ic('helicopter')}</span>Helicopters land on the H.</li></ul></div>
<div class="fa-panel" style="width:260px"><h4 class="fa-panel__title">Meadow Field</h4><p>One runway and a helipad between a farm and a pond.</p></div>`)),
  comp('Dialog', 'Dialog', 'A modal that stops the game, built as a boarding pass: a tinted header, the message, a perforation, and a stub with stats and actions.', `<div class="sc-demo" style="position:relative;height:680px"><fa-scene level="meadow" style="position:absolute;inset:0;height:100%;aspect-ratio:auto"></fa-scene><div class="fa-scrim" style="display:flex;inset:0;flex-wrap:wrap;gap:var(--space-6);align-items:center;justify-content:center;padding:var(--space-6) var(--space-4)">${paused}${over}</div></div>
  <div class="sc-card"><p style="max-width:70ch;margin-bottom:var(--space-4)">Compact variant, on a 640 by 360 landscape phone with its system bars (24px top and bottom) and a 50px banner under the game-over dialog. The pass tears along a vertical perforation.</p>
  <div style="display:flex;flex-wrap:wrap;gap:var(--space-5)">${[[pausedCompact, 0], [overCompact, 50]].map(([d, banner]) => `<div style="position:relative;width:640px;max-width:100%;height:360px;border-radius:var(--radius-panel);overflow:hidden;box-shadow:0 0 0 var(--line-ui) var(--outline)"><fa-scene level="meadow" style="position:absolute;inset:0;height:100%;aspect-ratio:auto"></fa-scene><div class="fa-scrim" style="position:absolute;inset:24px 0 ${24 + banner}px 0;padding:var(--space-2) var(--space-4)">${d}</div>${banner ? `<div style="position:absolute;left:0;right:0;bottom:24px;height:50px;display:grid;place-items:center;background:var(--paper-300);color:var(--ink-muted)" class="label">AD BANNER 320 × 50</div>` : ''}</div>`).join('')}</div></div>`),
  comp('LevelCard', 'Level Card', 'A pressable card for one airport. Its art is the level itself, cropped, so a card always matches its airfield.', stage('sky', cards)),
  part('CoachMark.html'), part('Wordmark.html'), part('Banner.html'),
];
const secComponents = `<section class="sc-section" id="components">
  <h2><small>Components</small>Components</h2>
  <p class="sc-lede">Every component lives in <code>bundle.css</code> under an <code>fa-</code> class and reads only tokens. Guidelines are in <code>components/</code>.</p>
${components.join('\n')}
</section>`;

// ---------------------------------------------------------------- screens
const screen = (inner, theme) => `<div class="sc-demo sc-demo--screen"${theme ? ` data-theme="${theme}"` : ''}><div class="fa-stage" style="position:relative;display:block;width:960px;height:540px;min-height:0;padding:0;overflow:hidden">${inner}</div></div>`;
const game = (level, o) => `<fa-scene level="${level}" style="position:absolute;inset:0;height:100%;aspect-ratio:auto"></fa-scene>${hud(o)}`;
const secScreens = `<section class="sc-section" id="screens">
  <h2><small>Reference</small>Screens</h2>
  <p class="sc-lede">Reference compositions at 960 by 540: how the world and the UI layer in each level.</p>
  ${comp('GameScreen', 'Game Screen: Meadow Field', 'Day. A jet is selected and cleared onto runway 27; a helicopter is routed to the helipad.', screen(game('meadow')))}
  ${comp('GameIsland', 'Game Screen: Island', 'Day. The airfield fills the island; the helipad shows the valid-landing halo.', screen(game('island', { score: '4&#8201;300', streak: 'x2', next: 'turboprop', livery: 1, edge: 'arrow-right', lost: 0 })))}
  ${comp('GameSavanna', 'Game Screen: Savanna', 'Dawn. A one-way runway: only the landing end is marked.', screen(game('savanna', { score: '7&#8201;150', streak: 'x4', next: 'light-prop', livery: 4, lost: 2 }), 'dawn'))}
  ${comp('PauseScreen', 'Pause', 'The pause dialog over the dimmed field.', screen(`${game('meadow')}<div class="fa-scrim" style="position:absolute;inset:0">${pausedCompact}</div>`))}
  ${comp('GameOverScreen', 'Game over', 'The game-ended dialog over the dimmed field, in its landscape layout.', screen(`${game('meadow')}<div class="fa-scrim" style="position:absolute;inset:0">${overCompact}</div>`))}
  ${comp('LevelSelect', 'Level Select', 'One card per airport on the menu sky.', `<div class="sc-demo sc-demo--screen"><div class="fa-stage fa-stage--sky" style="position:relative;width:960px;height:540px;min-height:0;flex-direction:column;gap:var(--space-6)"><h2 class="display-md" style="margin:0">Choose an airport</h2><div style="display:flex;gap:var(--space-5)">${cards}</div></div></div>`)}
  ${part('MainMenu.html')}
  ${part('SplashScreen.html')}
</section>`;
const secNight = `<section class="sc-section" id="night">
  <h2><small>Themes</small>Night</h2>
  <p class="sc-lede">Night darkens the sky and moonlights the field. Paper, strips and aircraft stay light and accents stay bright, so outlines and text hold. The UI is never inverted.</p>
  <div class="sc-night" data-theme="dark">
    ${comp('NightGame', 'Game Screen', 'Meadow Field at night, from the same sprites.', screen(game('meadow')))}
    ${comp('NightButton', 'Controls', 'The same controls on the night sky.', stage('sky', `<button class="fa-btn">${ic('play')}Take off</button><button class="fa-btn fa-btn--secondary">Choose airport</button><button class="fa-btn fa-btn--ghost">${ic('settings')}Settings</button><button class="fa-btn fa-btn--danger">Quit flight</button><button class="fa-btn" disabled>Disabled</button><button class="fa-toggle" role="switch" aria-checked="true" aria-label="Sound"></button><span class="fa-chip">1 runway</span>`))}
    ${comp('NightDialog', 'Dialog', 'Paper stays light at night.', stage('sky', paused))}
  </div>
</section>`;

// ---------------------------------------------------------------- page
const nav = [['principles', 'Principles'], ['colour', 'Colour'], ['type', 'Type'], ['shape', 'Shape and depth'], ['motion', 'Motion'], ['appicon', 'App icon'], ['icons', 'Icons'], ['aircraft', 'Aircraft'], ['scenery', 'Scenery'], ['ground', 'Ground and zones'], ['components', 'Components'], ['screens', 'Screens'], ['night', 'Night'], ['voice', 'Voice']];
const hero = part('hero.html')
  .replace(/<nav class="sc-nav"[\s\S]*?<\/nav>/, `<nav class="sc-nav" aria-label="Sections">${nav.map(n => `<a class="fa-chip" href="#${n[0]}">${n[1]}</a>`).join('')}</nav>`)
  .replace(/<p class="sc-hero__lead">[^<]*<\/p>/, '<p class="sc-hero__lead">The design system, version 2, for a top-down air traffic game that looks like a box of toys tipped onto a lawn.</p>');
const p = parts();
const pageCss = read('tools/showcase/page.css') + `
.sc-rules2{display:grid;grid-template-columns:repeat(auto-fill,minmax(270px,1fr));gap:var(--space-4) var(--space-5)}
.sc-rule{display:grid;grid-template-columns:auto 1fr;gap:2px var(--space-2);align-content:start;min-width:0}
.sc-rule i{grid-row:span 2;font:800 13px/22px var(--font-text);font-style:normal;color:var(--sky-900);min-width:26px}
.sc-rule b{font:600 17px/22px var(--font-display)}
.sc-rule span{font:600 14px/20px var(--font-body);color:var(--ink-muted)}
.sc-scn__tier{font:800 10.5px/12px var(--font-text);letter-spacing:.06em;text-transform:uppercase;color:var(--ink-muted)}
.sc-scn__tile{box-shadow:none;border:0;border-radius:var(--radius-control);padding:0}
.sc-scn__tile--water{background:var(--water-500)}.sc-scn__tile--apron{background:var(--concrete-300)}
.sc-crops{display:grid;gap:var(--space-5);grid-template-columns:repeat(auto-fill,minmax(min(100%,420px),1fr))}
.sc-crop{margin:0;display:grid;gap:var(--space-2);min-width:0}
.sc-crop fa-scene{border-radius:var(--radius-panel)}
.sc-crop figcaption{font:600 14px/20px var(--font-body);color:var(--on-sky)}
.sc-demo{border:0;box-shadow:0 0 0 var(--line-ui) var(--outline);margin:var(--line-ui)}
.sc-card{border:0;box-shadow:0 0 0 var(--line-ui) var(--outline),0 6px 0 2.5px var(--contact);margin:var(--line-ui)}
.sc-hero{border:0;box-shadow:0 0 0 var(--line-ui) var(--outline),0 8px 0 2.5px var(--contact)}
.sc-night{border:0;box-shadow:0 0 0 var(--line-ui) var(--outline)}
.sc-sw__chip{border:0;box-shadow:inset 0 0 0 1.5px var(--paper-500)}
.sc-icon{border:0;box-shadow:inset 0 0 0 1.5px var(--paper-400)}
`;
const html = `<!doctype html>
<html lang="en" data-theme="light">
<head>
<meta charset="utf-8">
<title>Final Approach Design System</title>
${p.head}
${pageCss}</style>
</head>
<body>
<main class="sc-wrap">

${hero}

${secPrinciples}

${secColour}

${secType}

${secShape}

${part('section-motion.html')}

${part('section-appicon.html')}

${part('section-icons.html')}

${part('section-aircraft.html')}

${secScenery}

${secGround}

${secComponents}

${secScreens}

${secNight}

${part('section-voice.html')}

<p class="sc-foot">Final Approach design system, version ${t.version}. Generated by tools/build-showcase.cjs from tokens.json, bundle.css and bundle.js.</p>
</main>
${p.kit}
<script>
${part('theme.js')}
</script>
</body>
</html>
`;
// nothing deprecated may be used or shown
const dead = [...t.color.tokens, ...['spacing', 'radius', 'line', 'size', 'opacity'].flatMap(f => t[f].tokens)].filter(k => k.deprecated).map(k => k.name);
const usedDead = dead.filter(n => new RegExp(`var\\(--${n}\\)`).test(html));
if (usedDead.length) throw new Error('deprecated tokens used in the showcase: ' + usedDead.join(', '));
fs.writeFileSync(path.join(DS, 'showcase.html'), html);
console.log(`showcase.html: ${Math.round(html.length / 1024)}KB, ${colours.length} colours, ${Object.keys(FA.WORLD).length} scenery sprites, ${components.length} components`);
