// Writes the rear-view aircraft sprites of the approach view as SVG assets, in Day values:
// assets/aircraft/<type>-rear.svg. The sources are REAR_AIRCRAFT in bundle.js.
// usage, from the repository root: node docs/design-system/tools/build-rear-aircraft.cjs
const fs = require('fs');
const path = require('path');
const DS = path.join(__dirname, '..');
const FA = require(path.join(DS, 'bundle.js'));
const tokens = JSON.parse(fs.readFileSync(path.join(DS, 'tokens.json'), 'utf8'));

/** The Day value of a colour token, following references such as {sun}. */
function colour(name) {
  const t = tokens.color.tokens.find(c => c.name === name);
  if (!t) throw new Error(`no colour token ${name}`);
  const v = typeof t.value === 'string' ? t.value : t.value.light;
  const ref = /^\{(.+)\}$/.exec(v);
  return ref ? colour(ref[1]) : v;
}
const line = tokens.line.tokens.find(l => l.name === 'line-hero').value.replace('px', '');
// The livery each type wears in the asset set, the same as its top-down asset.
const livery = { 'light-prop': 'path-1', turboprop: 'path-2', jet: 'path-3', jumbo: 'path-5', helicopter: 'path-6' };
const outline = `stroke="${colour('outline')}" stroke-linejoin="round" stroke-linecap="round"`;
/** The paint of bundle.css (.fa-p-*), written out. */
const painter = type => key => ({
  body: `fill="${colour('fuselage')}" ${outline} stroke-width="${line}"`,
  liv: `fill="${colour(livery[type])}" ${outline} stroke-width="${line}"`,
  shade: `fill="${colour('fuselage-shade')}" ${outline} stroke-width="${line}"`,
  glass: `fill="${colour('glass')}" ${outline} stroke-width="3"`,
  shine: `fill="none" stroke="${colour('glass-shine')}" stroke-width="2.5" stroke-linejoin="round" stroke-linecap="round"`,
  dark: `fill="${colour('outline')}"`,
  blur: `fill="${colour('outline')}" fill-opacity="0.14"`,
  line: `fill="none" ${outline} stroke-width="4.5"`,
}[key]);

for (const [type, a] of Object.entries(FA.REAR_AIRCRAFT)) {
  const svg = `<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 ${a.width} ${a.height}" width="${a.width}" height="${a.height}">${FA.paint(a.svg, painter(type))}</svg>\n`;
  if (/\{\{/.test(svg)) throw new Error(`rear sprite ${type}: a paint key was not painted`);
  fs.writeFileSync(path.join(DS, 'assets', 'aircraft', `${type}-rear.svg`), svg);
  console.log('wrote', `assets/aircraft/${type}-rear.svg`);
}
