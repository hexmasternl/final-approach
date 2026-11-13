// One-off migration of tokens.json to design system v2 (OpenSpec change design-system-v2).
// It only adds tokens and marks v1-only tokens as deprecated; no existing name or value changes.
// Safe to run again: tokens that already exist are left alone.
// usage, from the repository root: node docs/design-system/tools/migrate-tokens-v2.cjs
const fs = require('fs');
const path = require('path');
const file = path.join(__dirname, '..', 'tokens.json');
const t = JSON.parse(fs.readFileSync(file, 'utf8'));
t.version = 2;

const colors = t.color.tokens;
const byName = n => colors.find(c => c.name === n);
const hex = v => [1, 3, 5].map(i => parseInt(v.slice(i, i + 2), 16));
const toHex = a => '#' + a.map(x => Math.max(0, Math.min(255, Math.round(x))).toString(16).padStart(2, '0')).join('');
// Dawn is dim and warm: darken, then pull halfway to a warm grey. Used where v1 has no Dawn value to follow.
const dawnOf = day => toHex(hex(day).map((c, i) => (c * [0.86, 0.8, 0.74][i]) * 0.5 + [150, 125, 85][i] * 0.5));
const mix = (a, b) => toHex(hex(a).map((c, i) => (c + hex(b)[i]) / 2));
const dawnValue = n => { const c = byName(n); return typeof c.value === 'string' ? c.value : (c.value.dawn || c.value.light); };

function add(after, name, value, usage) {
  if (byName(name)) return;
  const tok = { name, value, usage };
  const i = after ? colors.findIndex(c => c.name === after) : -1;
  if (i >= 0) colors.splice(i + 1, 0, tok); else colors.push(tok);
}
// World colour: Day, Night and a derived Dawn value.
const world = (after, name, day, night, usage, dawn) => add(after, name, { light: day, dark: night, dawn: dawn || dawnOf(day) }, usage);

// ---- ink
add('ink-muted', 'ink-3', '#7c86a6', 'The label of a disabled control, and the quietest text on paper. Never for text a player must read to continue.');

// ---- paper: UI surfaces
const paper = [
  ['paper-0', '#fffdf8', '#fffaf0', '#fbf5e6', 'The lightest paper: the face of a ghost button, a toggle knob, a level-card lock badge.'],
  ['paper-100', '#fffaf0', '#efe9d8', '#f4ebd8', 'The default surface: panels, dialogs, flight strips, level cards. Same values as the v1 surface token.'],
  ['paper-200', '#f8eed8', '#e2dbc8', '#ebe0c8', 'A band on paper: a panel header, the label cell of a flight strip, the face of a disabled control.'],
  ['paper-300', '#eddfc2', '#d5cdb8', '#e0d3b6', 'Wells and keylines: the toggle track, the segmented trough, lines between stat cells, the lip of a ghost button.'],
  ['paper-400', '#dfcda9', '#c4bb9f', '#d0c09d', 'The lip of panels, dialogs and level cards; the inner top edge of a well.'],
  ['paper-500', '#c9b48a', '#a89f84', '#b8a67f', 'Tinted lines on paper: the perforation of a dialog, the dashed rule under a panel header, the bottom edge of a paper lip.'],
];
let prev = 'surface-sunken';
for (const [n, l, d, w, u] of paper) { add(prev, n, { light: l, dark: d, dawn: w }, u); prev = n; }

// ---- accents: ramps around the v1 colours
const accent = (base, deep, steps) => { let p = deep || base; for (const [n, v, u] of steps) { add(p, n, v, u); p = n; } };
accent('sun', 'sun-deep', [
  ['sun-200', '#ffe693', 'Highlight on a sun fill: the top edge of a primary button, the label cell of the score strip.'],
  ['sun-500', '{sun}', 'Base tone of the sun ramp. The same colour as sun.'],
  ['sun-700', '#e39a12', 'Lip of a sun control; the edge of a sun chip. Replaces sun-deep.'],
  ['sun-900', '#b87407', 'The bottom edge of a sun lip.'],
]);
accent('tangerine', 'tangerine-deep', [
  ['tangerine-200', '#ffc998', 'Highlight on a tangerine fill.'],
  ['tangerine-500', '{tangerine}', 'Base tone of the tangerine ramp. The same colour as tangerine.'],
  ['tangerine-700', '#d9641a', 'Lower edge of a tangerine sticker. Replaces tangerine-deep.'],
  ['tangerine-900', '#a94b08', 'The darkest tangerine: a tinted line on a tangerine fill.'],
]);
accent('cherry', 'cherry-deep', [
  ['cherry-200', '#ffb5ae', 'Highlight on a cherry fill; the header band of a dialog that reports a failure; the face of the control tower roof in light.'],
  ['cherry-500', '{cherry}', 'Base tone of the cherry ramp. The same colour as cherry.'],
  ['cherry-700', '#c9302c', 'Lip of a danger button; the edge of a cherry chip; the stamp on a dialog. Replaces cherry-deep.'],
  ['cherry-900', '#9a2320', 'The bottom edge of a cherry lip; text of a stamp.'],
]);
accent('success', 'success-ink', [
  ['mint-200', '#aaf1df', 'Highlight on a mint fill; the header band of a dialog that reports success.'],
  ['mint-500', '{success}', 'Base tone of the mint ramp. The same colour as success: the track of a toggle that is on, threshold lights.'],
  ['mint-700', '#17b08d', 'The inner top edge of a toggle track that is on; the edge of a mint chip.'],
  ['mint-900', '#0b6b59', 'Text and icons on mint-200; the check in a toggle that is on. The same colour as success-ink.'],
]);
add('sky-700', 'sky-900', '#0f4c80', 'The bottom edge of a sky lip. sky-700 is the lip of a secondary button; sky-100 and sky-300 are its tint and highlight.');

// ---- world materials. 300 lit, 500 base, 700 shade, 900 tinted inner line.
const g = n => byName(n).value;
add('grass-300', 'grass-400', { light: '#97d858', dark: '#4a9571', dawn: mix(g('grass-300').dawn, g('grass-500').dawn) }, 'Light patches in unmown meadow, and the ground inside a paddock.');
add('grass-500', 'grass-600', { light: '#71c544', dark: '#398060', dawn: mix(g('grass-500').dawn, g('grass-700').dawn) }, 'The darker mowing stripe of the airfield lawn, and dark patches in the meadow. One step from grass-500, so stripes stay quiet.');
add('asphalt-700', 'asphalt-900', { light: '#343b4f', dark: '#30364f', dawn: '#2b3042' }, 'Tyres and the dark inside of an open hangar door.');
add('sand-300', 'sand-500', { light: '#d9b56f', dark: '#7f7a6c', dawn: '#a8935f' }, 'Scenery only. The edge and the ruts of a dirt track; the shaded side of sand.');
add('roof-500', 'roof-900', { light: '#7a3424', dark: '#4e2e3c', dawn: '#633228' }, 'Scenery only. The ridge of a tiled roof and the trim of a barn door: the tinted inner line of the roof ramp.');
add('roof-500', 'roof-700', { light: '#a84d35', dark: '#6d4350', dawn: '#874a39' }, 'Scenery only. Tile joints on the shaded slope, the shaded end of a barn wall, a porch roof.');
add('roof-500', 'roof-300', { light: '#ec8f6c', dark: '#a96f76', dawn: '#c27f62' }, 'Scenery only. The lit slope of a tiled roof.');
add('stone-300', 'stone-700', { light: '#7d776d', dark: '#5a5d73', dawn: '#6f6a66' }, 'Scenery only. The shaded facet of a rock and its cracks.');
add('stone-300', 'stone-500', { light: '#9c968b', dark: '#74778c', dawn: '#8b857f' }, 'Scenery only. The base tone of a rock; stone-300 is its lit facet.');
const ramps = [
  ['leaf', 'Scenery only. Broadleaf canopies, bushes and hedges. Darker and cooler than the lawn, so a tree never melts into it.', [
    [300, '#8ed35c', '#4d9c7c', 'the lit lobe'], [500, '#55b04a', '#357f68', 'the canopy'], [700, '#38923f', '#276657', 'the shaded side'], [900, '#236e38', '#184a45', 'leaf scallops and reeds']]],
  ['pine', 'Scenery only. Conifers, and painted wood such as shutters.', [
    [300, '#62c092', '#3f8f86', 'the top tier'], [500, '#3da079', '#2c7473', 'the middle tier'], [700, '#2a8067', '#215c63', 'the bottom tier'], [900, '#1b5f52', '#154451', 'tier edges']]],
  ['wood', 'Scenery only. Trunks, fences, doors, jetties.', [
    [300, '#cf9f69', '#8f7a70', 'the lit face of a rail or plank'], [500, '#a8713f', '#6e5c52', 'the base tone'], [700, '#7d4f2a', '#4d403c', 'the shaded side'], [900, '#59341a', '#33292b', 'window frames and plank joints']]],
  ['straw', 'Scenery only. Wheat fields and hay bales. Paler than sun, which belongs to the UI.', [
    [300, '#f1de98', '#a39f78', 'the field and the lit side of a bale'], [500, '#dcc163', '#8a8762', 'crop rows and the shaded side of a bale'], [700, '#b3923a', '#6c6a4c', 'the edge of a field and the spiral of a bale']]],
  ['wall', 'Scenery only. Plastered walls. The cream that stands in for white on the ground: scenery never uses the fuselage white.', [
    [100, '#fff4dc', '#c9c7cf', 'the wall in light'], [300, '#ecd9b4', '#a5a4b3', 'the shaded end of a wall'], [500, '#d2ba8e', '#878799', 'the shadow under the eaves']]],
  ['tin', 'Scenery only. Metal roofs, tanks, silos, parked aircraft. A cool grey, apart from the blue-grey asphalt.', [
    [100, '#f0f4f9', '#b9c1d6', 'the brightest band of a curved roof'], [300, '#dbe2ee', '#a1aac4', 'the lit tone'], [500, '#bcc6d8', '#8791af', 'the shade tone'], [700, '#98a4bc', '#6b7595', 'the deepest shade and roof seams'], [900, '#6d7994', '#4b5474', 'tinted lines on metal']]],
  ['concrete', 'Aprons, helipad pads, runway shoulders, kerbs.', [
    [300, '#d7dbe4', '#8f97b3', 'the slab'], [500, '#c1c7d3', '#7d85a2', 'slab joints and the runway shoulder'], [700, '#a2a9ba', '#666e8c', 'the edge of a slab']]],
  ['road', 'Scenery only. Public roads: lighter than a runway, so a road never looks like a place to land.', [
    [500, '#8892a8', '#6f7999', 'the surface'], [700, '#6d778e', '#586180', 'the kerb line']]],
  ['water', 'Ponds, waterholes and the sea.', [
    [300, '#93dff8', '#3f7ac4', 'the shallows along a shore'], [500, '#4db8f5', '#2f62ad', 'open water. The same blue as sky-500'], [700, '#2b97de', '#214a8c', 'the edge between shallows and shore']]],
  ['hide', 'Scenery only. Animals.', [
    [100, '#fff6e6', '#cfcbd0', 'a pale coat'], [300, '#ead9bc', '#a9a6b3', 'the shaded flank and ears'], [900, '#4a3528', '#3a2f36', 'patches, hooves and stripes']]],
  ['denim', 'Scenery only. Car paint, a step muted so no car looks like an aircraft livery.', [
    [300, '#8db4e6', '#6f8fc0', 'the roof'], [500, '#5f8fcf', '#4a6fa8', 'the body']]],
];
prev = 'stone-700';
for (const [fam, what, steps] of ramps) for (const [step, day, night, role] of steps) {
  world(prev, `${fam}-${step}`, day, night, `${what} Step ${step}: ${role}.`);
  prev = `${fam}-${step}`;
}
add('glass', 'glass-700', { light: '#5fb2dc', dark: '#4d8fc4' }, 'Mullions and the lower rim of glass on buildings: the tinted line of glass.');
add('glass-700', 'window', { light: '{glass}', dark: '#ffe9a8', dawn: '#ffe9a8' }, 'Scenery only. A window of a building: glass by day, a warm light in Night and Dawn.');
add('window', 'beacon', '#fff3b0', 'Scenery only. Small lamps drawn as part of the ground: runway edge lights, helipad corner lights, headlights, the tower beacon at rest. The same colour as light-beacon.');
add('beacon', 'muzzle', { light: '#f4b3a8', dark: '#b98f96', dawn: '#d6a093' }, 'Scenery only. A muzzle, and pink flowers in a meadow or a flower box.');
add('shadow-ground', 'shadow-cast', { light: '#12301f', dark: '#03050f', dawn: '#14161e' }, 'The hard shadow anything standing on the ground casts down-right. Drawn as one layer at opacity-shadow-cast, so overlapping shadows never darken twice.');
add('scrim', 'contact', { light: 'rgba(29, 37, 69, 0.2)', dark: 'rgba(4, 7, 18, 0.4)' }, 'The flat contact shadow under a control, panel or card on a menu.');
add('contact', 'contact-field', { light: 'rgba(18, 48, 31, 0.28)', dark: 'rgba(3, 5, 15, 0.45)' }, 'The flat contact shadow under anything in the HUD: flight strips, stickers, the pause button, coach marks.');
add('contact-field', 'contact-overlay', { light: 'rgba(10, 14, 36, 0.3)', dark: 'rgba(3, 5, 16, 0.5)' }, 'The flat contact shadow under a dialog on the scrim.');

// ---- usage notes of kept tokens that still described v1
const note = (name, usage) => { const c = byName(name); if (c) c.usage = usage; };
note('outline', 'The outer edge of every drawn shape: aircraft at line-hero, landing zones at line-zone, controls, panels, flight strips and scenery silhouettes at line-ui. Never pure black. Lines inside a shape are a darker tone of that shape, not outline.');
note('ink', 'Primary text and icons on paper. The same in every theme: paper stays light at night.');
note('ink-muted', 'Secondary text, captions and hints on paper; the label of an unselected segment.');
note('sky-700', 'Lip of a secondary button; links and emphasised text on paper.');
note('success-ink', 'Success text on paper. The same colour as mint-900.');
note('danger-ink', 'Danger text on paper.');
note('focus', 'Inner band of the two-tone focus ring: a line-ui band of focus, then a line-ui band of outline outside it. One of the two always holds 3:1 against whatever the ring lands on.');
note('on-accent', 'Text and icons on any bright fill: sun, tangerine, cherry, mint, sky-500, star and every path colour. Dark in every theme because the fills stay bright.');
note('on-outline', 'Text and icons on an outline-coloured fill: coach marks and the callsign tag.');

// ---- deprecations: v1 tokens v2 no longer uses, each with its replacement
const dep = (fam, name, by) => { const k = (fam === 'color' ? colors : t[fam].tokens).find(x => x.name === name); if (k) k.deprecated = by; };
dep('color', 'surface', 'paper-100'); dep('color', 'surface-raised', 'paper-0'); dep('color', 'surface-sunken', 'paper-300');
dep('color', 'sun-deep', 'sun-700'); dep('color', 'tangerine-deep', 'tangerine-700'); dep('color', 'cherry-deep', 'cherry-700');
for (const [n, by] of [['shadow-lip-sm', 'lip-sm'], ['shadow-lip', 'lip-md'], ['shadow-lip-lg', 'lip-panel'], ['shadow-soft', 'contact-overlay'], ['shadow-well', 'well-inset']]) dep('shadow', n, by);
for (const [n, by] of [['line-fine', 'line-detail'], ['line-1', 'line-detail'], ['line-2', 'line-ui-sm'], ['line-3', 'line-ui'], ['line-4', 'line-ui']]) dep('line', n, by);
for (const [n, by] of [['radius-sm', 'radius-chip'], ['radius-md', 'radius-control'], ['radius-lg', 'radius-panel'], ['radius-xl', 'radius-dialog']]) dep('radius', n, by);
dep('size', 'hud-chip', 'strip-height'); dep('size', 'toggle-width', 'toggle-width-v2'); dep('size', 'toggle-height', 'toggle-height-v2'); dep('size', 'toggle-knob', 'toggle-knob-v2'); dep('opacity', 'opacity-disabled', 'the flat disabled state (U7)');

// ---- lines, radii, sizes, opacity
const push = (fam, name, value, usage, after) => { const a = t[fam].tokens; if (a.find(x => x.name === name)) return; const i = after ? a.findIndex(x => x.name === after) : -1; const k = { name, value, usage }; if (i >= 0) a.splice(i + 1, 0, k); else a.push(k); };
let p = 'line-4';
for (const [n, v, u] of [
  ['line-hero', '4px', 'Rank 1. The outline of an aircraft, and of nothing else.'],
  ['line-zone', '3px', 'Rank 2. The outline of a runway, a helipad and a cloud.'],
  ['line-ui', '2.5px', 'Rank 3. The outline of every control, panel, flight strip and dialog, and the silhouette of scenery.'],
  ['line-ui-sm', '2px', 'Rank 4. Small parts: the selected segment, a heart, a chimney, a post.'],
  ['line-detail', '1.25px', 'Rank 5. Lines inside a shape: keylines, dividers, tile joints, mullions. Always a darker tone of the surface, never outline. Scenery may go up to 1.5.'],
]) { push('line', n, v, u, p); p = n; }
p = 'radius-xl';
for (const [n, v, u] of [
  ['radius-chip', '9px', 'Chips and tags.'],
  ['radius-strip', '12px', 'Flight strips, the pause button, icon tiles, the selected segment.'],
  ['radius-control-sm', '13px', 'Small buttons.'],
  ['radius-control', '16px', 'Buttons, icon buttons, the segmented trough, coach marks.'],
  ['radius-control-lg', '21px', 'The large button.'],
  ['radius-panel', '20px', 'Panels and level cards.'],
  ['radius-dialog', '26px', 'Dialogs.'],
]) { push('radius', n, v, u, p); p = n; }
for (const [n, v, u] of [
  ['strip-height', '40px', 'Height of a flight strip and of the pause button in the HUD.'],
  ['lip-sm', '4px', 'Lip of a small button and of the HUD pause button.'],
  ['lip-md', '5px', 'Lip of a default button and of an icon button.'],
  ['lip-lg', '7px', 'Lip of the large button.'],
  ['lip-panel', '6px', 'Lip of a panel and of a level card.'],
  ['lip-dialog', '8px', 'Lip of a dialog.'],
  ['lip-pressed', '1px', 'What is left of a lip while its control is pressed. The face moves down by the difference.'],
  ['lip-inset', '3.5px', 'The lip drawn inside a small round part: the toggle knob, the selected segment.'],
  ['lip-edge', '1.5px', 'The darker line along the bottom of a lip.'],
  ['highlight', '2px', 'The lighter line along the top edge of a face.'],
  ['well-inset', '3px', 'The inner top edge of a well: toggle track, segmented trough.'],
  ['contact-offset', '4px', 'How far the flat contact shadow shows below a control or panel.'],
  ['contact-offset-field', '3px', 'How far the contact shadow shows below a flight strip, sticker or coach mark.'],
  ['contact-offset-overlay', '7.5px', 'How far the contact shadow shows below a dialog.'],
  ['shine-width', '11px', 'Width of the shine on a coloured button.'],
  ['shine-height', '4px', 'Height of the shine on a coloured button.'],
  ['shine-inset-x', '15px', 'Distance of the shine from the left edge of the face.'],
  ['shine-inset-y', '7px', 'Distance of the shine from the top edge of the face.'],
  ['sticker-rim', '3px', 'Width of the paper rim around a sticker.'],
  ['sticker-height', '32px', 'Height of a sticker: the streak badge and the score popup.'],
  ['chip-height', '26px', 'Height of a chip.'],
  ['icon-tile', '36px', 'Side of the tinted tile behind an icon in a panel row.'],
  ['icon-xs', '15px', 'Icons inside a flight-strip label and a chip.'],
  ['perforation-dot', '14px', 'Diameter of the punched half-holes where a perforation meets the edge of a dialog.'],
  ['toggle-width-v2', '60px', 'Width of the v2 toggle track.'],
  ['toggle-height-v2', '34px', 'Height of the v2 toggle track (the touch area is touch-min).'],
  ['toggle-knob-v2', '26px', 'Diameter of the v2 toggle knob.'],
  ['segment-height', '36px', 'Height of a segment of a segmented control (the touch area is touch-min).'],
]) push('size', n, v, u);
for (const [n, v, u] of [
  ['shadow-air-blur', '5px', 'Blur of the soft shadow under an aircraft, in world units of a scene.'],
  ['shadow-cloud-blur', '7px', 'Blur of the soft shadow under a cloud, in world units of a scene.'],
  ['rule-dash', '6px', 'Dash length of a dashed rule on paper: the perforation of a dialog, the rule under a panel header.'],
  ['rule-gap', '4px', 'Gap between the dashes of a dashed rule on paper.'],
]) push('size', n, v, u);
push('opacity', 'opacity-shadow-air', '0.26', 'The soft shadow under an aircraft in a scene.');
push('opacity', 'opacity-shadow-cloud', '0.2', 'The soft shadow under a cloud in a scene.');
push('opacity', 'opacity-shadow-cast', '0.32', 'The one layer that holds every hard ground shadow is drawn at this opacity.');
push('opacity', 'opacity-shine', '0.6', 'The shine on a coloured button.');
push('opacity', 'opacity-eyebrow', '0.75', 'The eyebrow label in the header band of a dialog.');

// ---- type: Nunito for running text
if (!t.type.fonts.find(f => f.family === 'Nunito')) t.type.fonts.push({ family: 'Nunito', file: 'fonts/Nunito-Variable.woff2', weight: '200 1000', style: 'normal' });
t.type.families.body = '"Nunito", "Segoe UI", "Trebuchet MS", system-ui, sans-serif';
const textGroup = t.type.groups.find(x => x.name === 'Text');
textGroup.family = 'body';
const weights = { 'body-lg': 600, body: 600, 'body-sm': 600, caption: 700 };
for (const s of textGroup.styles) s.fontWeight = weights[s.name] || Math.max(600, s.fontWeight);
const st = textGroup.styles.find(s => s.name === 'body'); st.usage = 'All running text. Nunito, never lighter than 600: thin strokes look weak beside the outlines.';
if (!textGroup.styles.find(s => s.name === 'row')) textGroup.styles.splice(1, 0, { name: 'row', fontSize: '15px', lineHeight: '20px', fontWeight: 700, sample: 'Tap an aircraft to select it.', usage: 'A row in a panel or a settings list, and the text of a coach mark.' });
const read = t.type.groups.find(x => x.name === 'Readouts');
if (!read.styles.find(s => s.name === 'label-sm')) read.styles.push({ name: 'label-sm', fontSize: '11.5px', lineHeight: '14px', fontWeight: 800, letterSpacing: '0.09em', sample: 'SCORE', usage: 'The label cell of a flight strip, the label of a stat cell, the eyebrow of a dialog and the text of a stamp. Write it in capitals.' });
const disp = t.type.groups.find(x => x.name === 'Display');
if (!disp.styles.find(s => s.name === 'strip-value')) disp.styles.push({ name: 'strip-value', fontSize: '21px', lineHeight: '24px', fontWeight: 700, fontVariantNumeric: 'tabular-nums', sample: '12 450', usage: 'The value in a flight strip and in a stat cell. Thin space as thousands separator.' });
if (!disp.styles.find(s => s.name === 'dialog-title')) disp.styles.push({ name: 'dialog-title', fontSize: '38px', lineHeight: '40px', fontWeight: 700, sample: 'Paused', usage: 'The title in the header band of a dialog.' });

fs.writeFileSync(file, JSON.stringify(t, null, 2) + '\n');
console.log('tokens.json: version 2,', colors.length, 'colour tokens');
