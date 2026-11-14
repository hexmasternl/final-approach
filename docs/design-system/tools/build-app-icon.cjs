// Builds the app icon from the design system: the sun-yellow jet in close-up over a runway.
// Writes the SVGs in assets/app-icon, their PNG exports, and the Android launcher layers.
// usage, from the repository root: node docs/design-system/tools/build-app-icon.cjs [chromePath]
const fs = require('fs');
const path = require('path');
const { execFileSync } = require('child_process');
const DS = path.join(__dirname, '..');
const repo = path.join(DS, '..', '..');
const t = JSON.parse(fs.readFileSync(path.join(DS, 'tokens.json'), 'utf8'));
const FA = require(path.join(DS, 'bundle.js'));
const cmap = Object.fromEntries(t.color.tokens.map(c => [c.name, c.value]));
const col = n => { let v = cmap[n]; if (typeof v !== 'string') v = v.light; const m = /^\{(.+)\}$/.exec(v); return m ? col(m[1]) : v; };

const S = 1024, C = S / 2;
// The jet: the game's own sprite, unchanged, painted with the first livery (sun).
const HERO = 4; // line-hero, on the sprite's 96-unit grid
const paint = {
  body: `fill="${col('fuselage')}"`, liv: `fill="${col('sun')}"`, shade: `fill="${col('fuselage-shade')}"`,
  glass: `fill="${col('glass')}" stroke-width="3"`, shine: `fill="none" stroke="${col('glass-shine')}" stroke-width="2.5"`,
};
const jet = FA.paint(FA.AIRCRAFT.jet.svg, k => paint[k]);
const solid = FA.paint(FA.AIRCRAFT.jet.svg, k => k === 'shine' ? 'fill="none" stroke="none"' : `fill="${col('shadow-cast')}" stroke="${col('shadow-cast')}"`);
// Close-up: the icon is centred just behind the cockpit, so the nose tip (28 units from the focus,
// 312px with its outline) and the wing
// roots sit inside the central 66% that every launcher mask keeps; wing tips and tail bleed off.
const SCALE = 10.4, HEADING = 45, FOCUS = [48, 31];
const place = (dx, dy) => `translate(${C + dx} ${C + dy}) rotate(${HEADING}) scale(${SCALE}) translate(${-FOCUS[0]} ${-FOCUS[1]})`;
const jetLayer = `<g transform="${place(0, 0)}" stroke="${col('outline')}" stroke-width="${HERO}" stroke-linejoin="round" stroke-linecap="round">${jet}</g>`;
// Airborne: the soft shadow, thrown down-right (W1).
const shadowLayer = `<g transform="${place(54, 74)}" opacity="${0.3}" filter="url(#soft)" stroke-width="${HERO}" stroke-linejoin="round">${solid}</g>`;
const defs = `<defs><filter id="soft" x="-20%" y="-20%" width="140%" height="140%"><feGaussianBlur stdDeviation="1.6"/></filter></defs>`;

// The ground: the mown lawn in two neighbouring grass steps, and the runway the jet is lined up
// with, in the v2 construction: rough-grass fringe, concrete shoulder, outlined asphalt, edge
// lines, centre dashes and edge lights.
const RW = 5.2; // icon pixels per world unit for the runway
let stripes = '';
for (let y = -1100; y < 1100; y += 208) stripes += `<rect x="-1100" y="${y}" width="2200" height="104" fill="${col('grass-600')}"/>`;
const u = v => (v * RW).toFixed(1);
let lights = '';
for (let x = -1000; x <= 1000; x += 58 * RW) lights += `<circle cx="${x.toFixed(1)}" cy="${u(-36.5)}" r="${u(2.5)}"/><circle cx="${x.toFixed(1)}" cy="${u(36.5)}" r="${u(2.5)}"/>`;
const ground = `<rect width="${S}" height="${S}" fill="${col('grass-500')}"/>
<g transform="translate(${C} ${C}) rotate(${HEADING - 90})">${stripes}
<rect x="-1100" y="${u(-54)}" width="2200" height="${u(108)}" fill="${col('grass-700')}"/>
<rect x="-1100" y="${u(-41)}" width="2200" height="${u(82)}" fill="${col('concrete-500')}" stroke="${col('concrete-700')}" stroke-width="${u(1.6)}"/>
<rect x="-1100" y="${u(-32)}" width="2200" height="${u(64)}" fill="${col('asphalt-500')}" stroke="${col('outline')}" stroke-width="${u(3)}"/>
<path d="M-1100 ${u(-27.5)}h2200M-1100 ${u(27.5)}h2200" fill="none" stroke="${col('marking')}" stroke-width="${u(1.6)}"/>
<path d="M-1100 0h2200" fill="none" stroke="${col('marking')}" stroke-width="${u(3.2)}" stroke-dasharray="${u(26)} ${u(20)}"/>
<g fill="${col('beacon')}" stroke="${col('concrete-700')}" stroke-width="${u(1)}">${lights}</g></g>`;

const svg = inner => `<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 ${S} ${S}" width="${S}" height="${S}">${inner}</svg>\n`;
const files = {
  'app-icon.svg': svg(`${defs}${ground}${shadowLayer}${jetLayer}`),
  'app-icon-background.svg': svg(ground),
  'app-icon-foreground.svg': svg(`${defs}${shadowLayer}${jetLayer}`),
  // In-app, docs and web: rounded, inside the line-ui outline the rest of the system has.
  'app-icon-rounded.svg': svg(`${defs}<clipPath id="m"><rect x="14" y="14" width="996" height="996" rx="228"/></clipPath><g clip-path="url(#m)">${ground}${shadowLayer}${jetLayer}</g><rect x="14" y="14" width="996" height="996" rx="228" fill="none" stroke="${col('outline')}" stroke-width="28"/>`),
};
const dir = path.join(DS, 'assets', 'app-icon');
for (const [f, s] of Object.entries(files)) fs.writeFileSync(path.join(dir, f), s);

// ---- the showcase section: the icon under each platform mask
const uri = f => `data:image/svg+xml;base64,${Buffer.from(files[f]).toString('base64')}`;
const masked = (radius, label) => `<figure><span class="sc-mask" style="border-radius:${radius}"><img src="${uri('app-icon.svg')}" alt=""></span><figcaption>${label}</figcaption></figure>`;
fs.writeFileSync(path.join(DS, 'tools', 'showcase', 'parts', 'section-appicon.html'), `<section class="sc-section" id="appicon">
  <h2><small>Brand</small>App icon</h2>
  <p class="sc-lede">The sun-yellow jet in close-up, lined up over a runway. Built only from system parts: the game's jet sprite at <code>line-hero</code>, its soft shadow, and the v2 runway on the mown lawn.</p>
  <div class="sc-card sc-appicon">
    <figure><img src="${uri('app-icon-rounded.svg')}" alt="Final Approach app icon"><figcaption>In-app and web: <code>app-icon-rounded.svg</code></figcaption></figure>
    ${masked('50%', 'Android, circle mask')}
    ${masked('30%', 'Android, squircle mask')}
    ${masked('22.5%', 'iOS mask')}
    <figure><span style="display:flex;gap:16px;align-items:flex-end;height:150px"><img src="${uri('app-icon-rounded.svg')}" alt="" style="width:96px;height:96px"><img src="${uri('app-icon-rounded.svg')}" alt="" style="width:64px;height:64px"><img src="${uri('app-icon-rounded.svg')}" alt="" style="width:40px;height:40px"></span><figcaption>96, 64 and 40px: the smallest size</figcaption></figure>
  </div>
</section>
`);

// ---- PNG exports through headless Chrome
const chrome = process.argv[2] || ['C:/Program Files/Google/Chrome/Application/chrome.exe', 'C:/Program Files (x86)/Microsoft/Edge/Application/msedge.exe', '/usr/bin/google-chrome', '/usr/bin/chromium'].find(p => fs.existsSync(p));
if (!chrome) { console.log('SVGs written. No Chrome found: PNG exports skipped.'); process.exit(0); }
const tmp = fs.mkdtempSync(path.join(require('os').tmpdir(), 'fa-icon-'));
const png = (svgFile, size, out, transparent) => {
  const page = path.join(tmp, 'p.html');
  fs.writeFileSync(page, `<!doctype html><html><body style="margin:0;background:transparent"><img src="file:///${path.join(dir, svgFile).replace(/\\/g, '/')}" width="${size}" height="${size}" style="display:block"></body></html>`);
  execFileSync(chrome, ['--headless=new', '--disable-gpu', '--hide-scrollbars', `--window-size=${size},${size}`, ...(transparent ? ['--default-background-color=00000000'] : []), `--screenshot=${out}`, 'file:///' + page.replace(/\\/g, '/')], { stdio: 'ignore' });
};
png('app-icon.svg', 1024, path.join(dir, 'app-icon-1024.png'), true);
png('app-icon.svg', 512, path.join(dir, 'app-icon-512.png'), true);
// Android adaptive icon layers: 108dp at xxxhdpi is 432px.
const res = path.join(repo, 'app', 'src', 'androidApp', 'src', 'main', 'res', 'mipmap-xxxhdpi');
if (fs.existsSync(res)) {
  png('app-icon-background.svg', 432, path.join(res, 'ic_launcher_background.png'));
  png('app-icon-foreground.svg', 432, path.join(res, 'ic_launcher_foreground.png'), true);
}
fs.rmSync(tmp, { recursive: true, force: true });
console.log('app icon: 4 SVGs, 2 PNG exports' + (fs.existsSync(res) ? ', Android launcher layers' : ''));
