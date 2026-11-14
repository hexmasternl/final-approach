// Generates the Compose theme values and sprite data from the design-system sources, checks the
// v2 scenery sprites in bundle.js against the world rules, and writes them as SVG assets
// (assets/scenery).
// usage, from the repository root: node docs/design-system/tools/gen-kotlin.cjs
const fs = require('fs');
const path = require('path');
const repo = path.join(__dirname, '..', '..', '..');
const DS = path.join(__dirname, '..');
const t = JSON.parse(fs.readFileSync(path.join(DS, 'tokens.json'), 'utf8'));
const FA = require(path.join(DS, 'bundle.js'));
const base = path.join(repo, 'app', 'src', 'shared', 'src', 'commonMain', 'kotlin', 'nl', 'hexmaster', 'finalapproach', 'ui');
const w = (rel, s) => { const f = path.join(base, rel); fs.mkdirSync(path.dirname(f), { recursive: true }); fs.writeFileSync(f, s); };
const camel = n => n.replace(/[-.](\w)/g, (_, c) => c.toUpperCase());
const HEAD = '// Generated from the design system (docs/design-system/tokens.json). Do not edit by hand.\n';
// A v1 token that v2 no longer uses keeps its name and value until the app's world is ported; its
// KDoc names the replacement. (Not @Deprecated: the v1 world still reads these on purpose.)
const dep = (k, indent) => k.deprecated ? `${indent}/** v1. Use ${/^[a-z0-9-]+$/.test(k.deprecated) ? '[' + camel(k.deprecated) + ']' : k.deprecated} in new code. */\n` : '';
const ctok = Object.fromEntries(t.color.tokens.map(c => [c.name, c]));

// ---------- colours
const cmap = Object.fromEntries(t.color.tokens.map(c => [c.name, c.value]));
const resolve = (n, th) => { let v = cmap[n]; if (typeof v !== 'string') v = v[th] ?? v.light; const m = /^\{(.+)\}$/.exec(v); return m ? resolve(m[1], th) : v; };
const kColor = v => {
  if (v[0] === '#') return `Color(0xFF${v.slice(1).toUpperCase()})`;
  const m = /^rgba\((\d+),\s*(\d+),\s*(\d+),\s*([\d.]+)\)$/.exec(v);
  if (!m) throw new Error('colour ' + v);
  return `Color(${m[1]}, ${m[2]}, ${m[3]}, ${Math.round(parseFloat(m[4]) * 255)})`;
};
// The sunset palette belongs to the splash page only, so it is kept out of the themes.
const isSunset = n => n.startsWith('sunset-');
// The lights of Dawn levels are not theme colours either; they go into FaLights.
const isLight = n => n.startsWith('light-');
const names = t.color.tokens.map(c => c.name).filter(n => !isSunset(n) && !isLight(n));
const lightNames = t.color.tokens.map(c => c.name).filter(isLight);
const sunsetNames = t.color.tokens.map(c => c.name).filter(isSunset);
let colors = `${HEAD}package nl.hexmaster.finalapproach.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

/**
 * One property per colour token of the design system.
 *
 * A palette is built from a [FaColorTokens], not through a constructor with a parameter per
 * token: a method takes at most 255 argument slots and a [Color] takes two, so such a constructor
 * stops loading, on Android and on the JVM, once there are more than 126 tokens.
 */
@Immutable
class FaColors internal constructor(tokens: FaColorTokens) {
${names.map(n => `${dep(ctok[n], '    ')}    val ${camel(n)}: Color = tokens.${camel(n)}`).join('\n')}

    /** Flight path and livery colour for index 1 to 6. */
    fun path(index: Int): Color = when (index) {
        1 -> path1
        2 -> path2
        3 -> path3
        4 -> path4
        5 -> path5
        else -> path6
    }
}

/** The colours of one palette while it is being put together; [FaColors] is the fixed result. */
internal class FaColorTokens {
${names.map(n => `    var ${camel(n)}: Color = Color.Unspecified`).join('\n')}
}

`;
const themeDoc = {
  DawnColors: '/**\n * The Dawn palette, for levels set before sunrise: the ground and strips are dim and warm, while\n * panels, chips, text and aircraft stay light as in Night. It is not an inverted UI. Tokens\n * without a Dawn value take their Day value.\n */',
};
for (const [th, k] of [['light', 'DayColors'], ['dark', 'NightColors'], ['dawn', 'DawnColors']])
  colors += `${themeDoc[k] ? themeDoc[k] + '\n' : ''}val ${k} = FaColors(\n    FaColorTokens().apply {\n${names.map(n => `        ${camel(n)} = ${kColor(resolve(n, th))}`).join('\n')}\n    },\n)\n\n`;
w('theme/FaColors.kt', colors.trimEnd() + '\n');
w('theme/SunsetColors.kt', `${HEAD}package nl.hexmaster.finalapproach.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

/**
 * The palette of the splash page: four flat sky bands, the sun, the airport silhouette and
 * the lit tower window. It is not a theme, and nothing but the splash page may use it.
 */
@Immutable
data class SunsetColors(
${sunsetNames.map(n => `    val ${camel(n)}: Color,`).join('\n')}
)

val Sunset = SunsetColors(
${sunsetNames.map(n => `    ${camel(n)} = ${kColor(resolve(n, 'light'))},`).join('\n')}
)
`);

// ---------- dimensions, motion, opacity
const dp = v => { const m = /^([\d.]+)px$/.exec(v); if (!m) throw new Error('dp ' + v); return `${m[1]}.dp`; };
const group = (fam, comment) => `    // ${comment}\n` + t[fam].tokens.map(k => `${dep(k, '    ')}    val ${camel(k.name)} = ${dp(k.value)}`).join('\n');
w('theme/FaDimens.kt', `${HEAD}package nl.hexmaster.finalapproach.ui.theme

import androidx.compose.ui.unit.dp

/** Spacing, radius, line, flight-path and size tokens. */
object FaDimens {
${group('spacing', 'Spacing')}

${group('radius', 'Radius')}

${group('line', 'Lines and flight path')}

${group('size', 'Sizes')}
}
`);
const ms = v => /ms$/.test(v) ? parseInt(v, 10) : Math.round(parseFloat(v) * 1000);
w('theme/FaMotion.kt', `${HEAD}package nl.hexmaster.finalapproach.ui.theme

import androidx.compose.animation.core.CubicBezierEasing

/** Durations in milliseconds and easings. */
object FaMotion {
${t.duration.tokens.map(k => `    const val ${camel(k.name)}Ms = ${ms(k.value)}`).join('\n')}

${t.easing.tokens.map(k => { const m = /cubic-bezier\(([^)]+)\)/.exec(k.value)[1].split(',').map(s => parseFloat(s) + 'f'); return `    val ${camel(k.name)} = CubicBezierEasing(${m.join(', ')})`; }).join('\n')}
}

/** Opacity tokens. */
object FaOpacity {
${t.opacity.tokens.filter(k => !isLight(k.name)).map(k => `${dep(k, '    ')}    const val ${camel(k.name.replace(/^opacity-/, ''))} = ${k.value}f`).join('\n')}
}
`);

// ---------- lights of Dawn levels
const tok = (fam, n) => { const k = t[fam].tokens.find(x => x.name === n); if (!k) throw new Error('token ' + n); return k.value; };
const secs = n => { const s = String(ms(tok('duration', n)) / 1000); return s.includes('.') ? s : s + '.0'; };
w('theme/FaLights.kt', `${HEAD}package nl.hexmaster.finalapproach.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * Tokens for the lights of Dawn levels. Light is gentle: no light is brighter than
 * [MAX_OPACITY] and no light cycle is faster than [MIN_CYCLE_SECONDS].
 */
object FaLights {
    const val MAX_OPACITY = ${tok('opacity', 'light-max-opacity')}f
    const val MIN_CYCLE_SECONDS = ${secs('dur-light-min')}

    /** One full flash of the control tower beacon, in seconds. */
    const val BEACON_CYCLE_SECONDS = ${secs('dur-beacon')}

    /** One sweep of the running light along a runway, in seconds. */
    const val RUNNING_LIGHT_CYCLE_SECONDS = ${secs('dur-running-light')}

${lightNames.map(n => `    val ${camel(n.replace(/^light-/, ''))} = ${kColor(resolve(n, 'light'))}`).join('\n')}
}
`);

// ---------- path helpers
const num = n => { const s = (+n).toFixed(3).replace(/\.?0+$/, ''); return s === '-0' ? '0' : s; };
const norm = d => d.match(/[a-zA-Z]|-?(?:\d+\.\d+|\d+|\.\d+)(?:e-?\d+)?/g).map(x => /[a-zA-Z]/.test(x) ? x : num(x)).join(' ');
const rectPath = (x, y, wd, h, r) => { x = +x; y = +y; wd = +wd; h = +h; r = Math.min(+r || 0, wd / 2, h / 2);
  return r ? `M ${num(x + r)} ${num(y)} H ${num(x + wd - r)} A ${num(r)} ${num(r)} 0 0 1 ${num(x + wd)} ${num(y + r)} V ${num(y + h - r)} A ${num(r)} ${num(r)} 0 0 1 ${num(x + wd - r)} ${num(y + h)} H ${num(x + r)} A ${num(r)} ${num(r)} 0 0 1 ${num(x)} ${num(y + h - r)} V ${num(y + r)} A ${num(r)} ${num(r)} 0 0 1 ${num(x + r)} ${num(y)} Z`
           : `M ${num(x)} ${num(y)} H ${num(x + wd)} V ${num(y + h)} H ${num(x)} Z`; };
const ellipsePath = (cx, cy, rx, ry) => { cx = +cx; cy = +cy; rx = +rx; ry = +ry;
  return `M ${num(cx - rx)} ${num(cy)} A ${num(rx)} ${num(ry)} 0 1 0 ${num(cx + rx)} ${num(cy)} A ${num(rx)} ${num(ry)} 0 1 0 ${num(cx - rx)} ${num(cy)} Z`; };
const attr = (s, n) => { const m = new RegExp(`(?:^|\\s)${n}="([^"]*)"`).exec(s); return m ? m[1] : undefined; };
const shapePath = (tag, a) => tag === 'path' ? norm(attr(a, 'd'))
  : tag === 'rect' ? rectPath(attr(a, 'x'), attr(a, 'y'), attr(a, 'width'), attr(a, 'height'), attr(a, 'rx'))
  : tag === 'circle' ? ellipsePath(attr(a, 'cx'), attr(a, 'cy'), attr(a, 'r'), attr(a, 'r'))
  : ellipsePath(attr(a, 'cx'), attr(a, 'cy'), attr(a, 'rx'), attr(a, 'ry'));

// ---------- aircraft sprites
const paintName = { body: 'Body', liv: 'Livery', shade: 'Shade', glass: 'Glass', shine: 'Shine', dark: 'Dark', blur: 'Blur', line: 'Line' };
const typeName = { 'light-prop': 'LightProp', turboprop: 'Turboprop', jet: 'Jet', jumbo: 'Jumbo', helicopter: 'Helicopter' };
let sprites = `// Generated from the design system sprites (components/bundle.js). Do not edit by hand.
package nl.hexmaster.finalapproach.ui.sprites

import nl.hexmaster.finalapproach.domain.AircraftType

/** How a sprite part is painted. Colours come from the theme at draw time. */
internal enum class SpritePaint { Body, Livery, Shade, Glass, Shine, Dark, Blur, Line }

/** How a sprite part moves while the aircraft flies. */
internal enum class SpriteMotion { None, Propeller, PropellerSide, Rotor }

/** One shape of a sprite, as SVG path data on the 96-unit sprite square (nose up). */
internal class SpritePart(
    val paint: SpritePaint,
    val pathData: String,
    val motion: SpriteMotion = SpriteMotion.None,
    val pivotX: Float = 0f,
    val pivotY: Float = 0f,
)

/** Side length of the square every sprite is drawn on. */
internal const val SPRITE_UNITS = 96f

internal val aircraftSprites: Map<AircraftType, List<SpritePart>> = mapOf(
`;
const motionName = { prop: 'Propeller', 'prop-side': 'PropellerSide', rotor: 'Rotor' };
const partsOf = (svg, indent) => {
  const parts = [];
  let motion = 'None';
  const re = /<g class="fa-(prop-side|prop|rotor)">|<\/g>|<(rect|path|circle|ellipse) \{\{([a-z]+)\}\}([^>]*)\/>/g;
  let m;
  while ((m = re.exec(svg))) {
    if (m[0].startsWith('<g')) { motion = motionName[m[1]]; continue; }
    if (m[0] === '</g>') { motion = 'None'; continue; }
    const d = shapePath(m[2], m[4]);
    let pivot = '';
    if (motion === 'Propeller' || motion === 'PropellerSide') { const x = +attr(m[4], 'x'), y = +attr(m[4], 'y'), wd = +attr(m[4], 'width'), h = +attr(m[4], 'height'); pivot = `, SpriteMotion.${motion}, ${num(x + wd / 2)}f, ${num(y + h / 2)}f`; }
    if (motion === 'Rotor') pivot = `, SpriteMotion.Rotor, 48f, 44f`;
    parts.push(`${indent}SpritePart(SpritePaint.${paintName[m[3]]}, "${d}"${pivot}),`);
  }
  return parts.join('\n');
};
for (const [key, a] of Object.entries(FA.AIRCRAFT)) {
  sprites += `    AircraftType.${typeName[key]} to listOf(\n${partsOf(a.svg, '        ')}\n    ),\n`;
}
sprites += ')\n';
const side = FA.SIDE_AIRCRAFT['light-prop'];
sprites += `
/** Size of the grid the side-view sprite is drawn on. Its nose points right. */
internal const val SIDE_SPRITE_WIDTH = ${side.width}f
internal const val SIDE_SPRITE_HEIGHT = ${side.height}f

/** The light prop seen from the side. It tows the banner on the splash page; gameplay never uses it. */
internal val sideViewLightProp: List<SpritePart> = listOf(
${partsOf(side.svg, '    ')}
)
`;
w('sprites/AircraftSprites.kt', sprites);

// ---------- scenery sprites for the app's v1 world
// The app's world is not ported to v2 yet, so its sprite data is still generated from the v1
// sprites in tools/scenery-v1.js. Port the world, then generate this from FA.WORLD instead.
const V1 = require(path.join(__dirname, 'scenery-v1.js'));
const pascal = n => n.replace(/(^|-)(\w)/g, (_, __, c) => c.toUpperCase());
const sceneryRoles = Object.keys(V1.SCENERY_FILL);
const roleOfToken = Object.fromEntries(sceneryRoles.map(r => [V1.SCENERY_FILL[r], r]));
const strokeName = { outline: 'Outline', fine: 'Fine' };
const lineOf = n => parseFloat(tok('line', n));
const sceneryShapes = (name, a) => {
  const shapes = [];
  const re = /<(rect|path|circle|ellipse) \{\{([a-z-]+)\}\}([^>]*)\/>/g;
  let m;
  while ((m = re.exec(a.svg))) shapes.push({ tag: m[1], key: m[2], attrs: m[3], paint: V1.sceneryPaint(m[2]) });
  if (shapes.length !== (a.svg.match(/<(rect|path|circle|ellipse)\b/g) || []).length) throw new Error(`scenery ${name}: a shape has no paint key`);
  // The rules of world.md, checked here so a sprite that breaks them never reaches the game.
  const fills = new Set(shapes.map(x => x.paint.fill).filter(Boolean));
  if (fills.size > V1.SCENERY_MAX_FILLS) throw new Error(`scenery ${name}: ${fills.size} fill colours (${[...fills].join(', ')}), at most ${V1.SCENERY_MAX_FILLS}`);
  for (const x of shapes) for (const token of [x.paint.fill, x.paint.stroke && x.paint.strokeColor]) {
    if (!token) continue;
    const resolved = [token]; let v = cmap[token];
    while (typeof v === 'string' && /^\{(.+)\}$/.test(v)) { resolved.push(v.slice(1, -1)); v = cmap[v.slice(1, -1)]; }
    const pathColours = V1.SCENERY_BANNED.filter(b => resolved.includes(b) || (typeof cmap[b] === 'string' && resolved.includes(cmap[b].slice(1, -1))));
    if (pathColours.length && V1.SCENERY_ACCENT[name] !== token) throw new Error(`scenery ${name}: ${token} is not a scenery colour (${pathColours.join(', ')})`);
  }
  return shapes;
};
const sceneryEntries = Object.entries(V1.SCENERY).map(([name, a]) => ({ name, a, shapes: sceneryShapes(name, a) }));
w('sprites/ScenerySprites.kt', `// Generated from the design system scenery sprites (components/bundle.js). Do not edit by hand.
package nl.hexmaster.finalapproach.ui.sprites

import androidx.compose.ui.graphics.Color
import nl.hexmaster.finalapproach.ui.theme.FaColors

/** The colour roles scenery is painted with. Colours come from the theme at draw time. */
internal enum class SceneryPaint { ${sceneryRoles.map(pascal).join(', ')} }

/** The theme colour of a scenery paint role. */
internal fun FaColors.scenery(paint: SceneryPaint): Color = when (paint) {
${sceneryRoles.map(r => `    SceneryPaint.${pascal(r)} -> ${camel(V1.SCENERY_FILL[r])}`).join('\n')}
}

/** How a scenery shape is stroked: not at all, with the scenery outline (\`${V1.SCENERY_STROKE.outline}\`) or with the detail line (\`${V1.SCENERY_STROKE.fine}\`). */
internal enum class SceneryStroke { None, Outline, Fine }

/** One shape of a scenery sprite, as SVG path data on the sprite's grid. A null [fill] is a stroke only. */
internal class SceneryPart(
    val fill: SceneryPaint?,
    val stroke: SceneryStroke,
    val pathData: String,
    val strokePaint: SceneryPaint = SceneryPaint.Dark,
)

/**
 * The scenery sprites of the design system: top-down, front up, each on its own grid of
 * [width] by [height] units. One unit is one world unit at the size the sprite is drawn for.
 */
internal enum class ScenerySprite(val width: Float, val height: Float, val parts: List<SceneryPart>) {
${sceneryEntries.map(({ name, a, shapes }) => `    ${pascal(name)}(
        ${a.width}f, ${a.height}f,
        listOf(
${shapes.map(x => `            SceneryPart(${x.paint.fill ? 'SceneryPaint.' + pascal(roleOfToken[x.paint.fill]) : 'null'}, SceneryStroke.${x.paint.stroke ? strokeName[x.paint.stroke] : 'None'}, "${shapePath(x.tag, x.attrs)}"${x.paint.stroke && x.paint.strokeColor !== 'outline' ? `, SceneryPaint.${pascal(roleOfToken[x.paint.strokeColor])}` : ''}),`).join('\n')}
        ),
    ),`).join('\n')}
}
`);
console.log(`generated: sprites/ScenerySprites.kt (${sceneryEntries.length} v1 sprites)`);

// ---------- scenery sprites of design system v2: rule checks and SVG assets
// The world rules of world.md, checked here so a sprite that breaks them never reaches the kit.
const material = tk => { const m = /^([a-z]+)-\d+$/.exec(tk); return m ? m[1] : tk; };
const worldEntries = Object.entries(FA.WORLD);
for (const [name, a] of worldEntries) {
  const keys = [...(a.svg + a.sh).matchAll(/\{\{([a-z0-9\/.-]+)\}\}/g)].map(m => FA.worldPaint(m[1]));
  const shapes = (a.svg.match(/<(rect|path|circle|ellipse|polygon)\b/g) || []).length;
  const painted = (a.svg.match(/<(rect|path|circle|ellipse|polygon) \{\{/g) || []).length;
  const inClip = (a.svg.match(/<clipPath[^>]*>.*?<\/clipPath>/g) || []).join('').match(/<(rect|path|circle|ellipse|polygon)\b/g) || [];
  if (painted + inClip.length !== shapes) throw new Error(`scenery ${name}: a shape has no paint key`);
  const used = new Set();
  for (const p of keys) for (const tk of [p.fill, p.stroke]) {
    if (!tk) continue;
    if (!cmap[tk]) throw new Error(`scenery ${name}: unknown colour token ${tk}`);
    if (ctok[tk].deprecated) throw new Error(`scenery ${name}: ${tk} is a v1 token`);
    if (FA.WORLD_BANNED.includes(tk)) throw new Error(`scenery ${name}: ${tk} belongs to aircraft and paths (W9)`);
    if (FA.WORLD_TRIM.includes(tk)) continue;
    const mat = material(tk);
    if (FA.WORLD_ACCENT[name] === mat) continue;
    if (/^(cherry|tangerine|sun|sky|mint|paper)$/.test(mat)) throw new Error(`scenery ${name}: ${tk} is a UI colour (W9)`);
    used.add(mat);
  }
  // wood and soil, and the two leaf greens, are one material each
  const mats = new Set([...used].map(m => ({ soil: 'wood', pine: 'leaf', grass: 'leaf' })[m] || m));
  // W2: three materials; a sprite that only carries other things (a vessel with containers, a cart with luggage) may hold more
  const trim = a.trim || [];
  if (trim.length > 2 || trim.some(m => !mats.has(m))) throw new Error(`scenery ${name}: trim names at most two materials the sprite uses`);
  trim.forEach(m => mats.delete(m));
  if (mats.size > FA.WORLD_MAX_MATERIALS && !a.carries) throw new Error(`scenery ${name}: ${mats.size} materials (${[...mats].join(', ')}), at most ${FA.WORLD_MAX_MATERIALS} (W2)`);
  if (!keys.some(p => p.stroke === 'outline') && a.tier !== 'flat') throw new Error(`scenery ${name}: no outline silhouette (W3)`);
  if (a.tier === 'flat' && keys.some(p => p.stroke === 'outline')) throw new Error(`scenery ${name}: flat ground has no outline (W6)`);
  if (a.tier !== 'flat' && !a.sh) throw new Error(`scenery ${name}: no cast shadow (W1)`);
  if (!['flat', 'S', 'M', 'L'].includes(a.tier)) throw new Error(`scenery ${name}: height tier`);
  if ((a.tier === 'M') && a.rotates) throw new Error(`scenery ${name}: a standing sprite does not rotate (W5)`);
}
// The sprites as standalone SVG files, shadow included, painted with the Day values.
const sceneryDir = path.join(DS, 'assets', 'scenery');
fs.mkdirSync(sceneryDir, { recursive: true });
for (const f of fs.readdirSync(sceneryDir)) if (f.endsWith('.svg') && !FA.WORLD[f.slice(0, -4)]) fs.unlinkSync(path.join(sceneryDir, f));
const literal = key => { const p = FA.worldPaint(key);
  return `fill="${p.fill ? resolve(p.fill, 'light') : 'none'}"` + (p.stroke ? ` stroke="${resolve(p.stroke, 'light')}" stroke-width="${p.width}"` : ''); };
const castOpacity = tok('opacity', 'opacity-shadow-cast');
for (const [name, a] of worldEntries)
  fs.writeFileSync(path.join(sceneryDir, name + '.svg'), FA.worldSvg(name, literal, { shadowAttr: `fill="${resolve('shadow-cast', 'light')}" opacity="${castOpacity}"` }).replace(' aria-hidden="true"', ` width="${a.w}" height="${a.h}"`) + '\n');
console.log(`generated: assets/scenery (${worldEntries.length} v2 sprites)`);

// ---------- airport silhouette
const sil = FA.SILHOUETTE;
const sunset = FA.SUNSET;
const fl = n => (Number.isInteger(n) ? n + 'f' : n + 'f');
w('sprites/AirportSilhouette.kt', `// Generated from the design system (components/bundle.js). Do not edit by hand.
package nl.hexmaster.finalapproach.ui.sprites

/**
 * The airport silhouette of the splash page, as SVG path data on a
 * ${sil.width} by ${sil.height} grid. The buildings stand on the line y = BASE_Y; the ground
 * fills everything below it.
 */
internal object AirportSilhouette {
    const val WIDTH = ${sil.width}f
    const val HEIGHT = ${sil.height}f
    const val BASE_Y = ${sil.baseY}f

    /** Where each of the four sky bands ends, as a share of the screen height from the top. */
    val skyBandEnds: FloatArray = floatArrayOf(${sunset.bandEnds.map(fl).join(', ')})

    /** The sun on this grid: low, and partly behind the terminal. */
    const val SUN_X = ${fl(sunset.sun.x)}
    const val SUN_Y = ${fl(sunset.sun.y)}
    const val SUN_RADIUS = ${fl(sunset.sun.r)}

    /** The control tower, tip of the antenna to the ground. */
    const val TOWER_HEIGHT = ${fl(sil.baseY - sunset.tower.topY)}

    /** The tower is at least this tall and at most that tall, as a share of the screen height. */
    const val MIN_TOWER_SHARE = ${fl(sunset.tower.minShare)}
    const val MAX_TOWER_SHARE = ${fl(sunset.tower.maxShare)}

    /** Buildings, parked aircraft and trees. The control tower is the tallest shape. */
    val shapes: List<String> = listOf(
${sil.shapes.map(d => `        "${norm(d)}",`).join('\n')}
    )

    /** The lit window of the tower cab. */
    val lights: List<String> = listOf(
${sil.lights.map(d => `        "${norm(d)}",`).join('\n')}
    )
}
`);

// ---------- icons
const iconNames = Object.keys(FA.ICONS);
let icons = `// Generated from the design system icons (components/bundle.js). Do not edit by hand.
package nl.hexmaster.finalapproach.ui.sprites

/** A filled shape of an icon; strokeWidth > 0 adds a round-joined outline in the same colour. */
internal class IconFill(val pathData: String, val strokeWidth: Float = 0f)

/** An icon on the 24-unit grid: filled shapes plus one 3-unit round stroke. */
internal class IconDef(val fills: List<IconFill> = emptyList(), val stroke: String? = null)

/** The icon set of the design system. */
enum class FaIcon(internal val def: IconDef) {
`;
icons += iconNames.map(n => {
  const i = FA.ICONS[n], fills = [];
  const re = /<(rect|path|circle|ellipse)([^>]*)\/>/g; let m;
  while (i.f && (m = re.exec(i.f))) { const sw = attr(m[2], 'stroke-width'); fills.push(`IconFill("${shapePath(m[1], m[2])}"${sw ? `, ${num(sw)}f` : ''})`); }
  const args = [];
  if (fills.length) args.push(`fills = listOf(${fills.join(', ')})`);
  if (i.s) args.push(`stroke = "${norm(i.s)}"`);
  const id = n.replace(/(^|-)(\w)/g, (_, __, c) => c.toUpperCase());
  return `    ${id}(IconDef(${args.join(', ')})),`;
}).join('\n');
icons += '\n}\n';
w('sprites/FaIcons.kt', icons);
console.log('generated: theme/SunsetColors.kt, sprites/AirportSilhouette.kt, theme/FaColors.kt, theme/FaDimens.kt, theme/FaMotion.kt, theme/FaLights.kt, sprites/AircraftSprites.kt, sprites/FaIcons.kt');

// ---------- typography
const styles = t.type.groups.flatMap(g => g.styles.map(s => ({ ...s, family: s.family || g.family })));
const sp = v => `${parseFloat(v)}.sp`;
for (const s of styles) {
  if (s.family === 'body' && s.fontWeight < 600) throw new Error(`type ${s.name}: Nunito is never lighter than 600`);
  if (s.family === 'text' && s.fontWeight < 700) throw new Error(`type ${s.name}: Inconsolata is never lighter than 700`);
}
w('theme/FaTypography.kt', `${HEAD}package nl.hexmaster.finalapproach.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import nl.hexmaster.finalapproach.resources.Res
import nl.hexmaster.finalapproach.resources.dynapuff_bold
import nl.hexmaster.finalapproach.resources.dynapuff_semibold
import nl.hexmaster.finalapproach.resources.inconsolata_extrabold
import nl.hexmaster.finalapproach.resources.nunito_bold
import nl.hexmaster.finalapproach.resources.nunito_extrabold
import nl.hexmaster.finalapproach.resources.nunito_semibold
import org.jetbrains.compose.resources.Font

/** One text style per type style of the design system. */
@Immutable
class FaTypography(
${styles.map(s => `    val ${camel(s.name)}: TextStyle,`).join('\n')}
)

/**
 * DynaPuff for headers, titles, button labels and the score; Nunito for running text;
 * Inconsolata for labels, readouts and callsigns. Only the weights the app ships are bundled:
 * other weights resolve to the nearest one.
 */
@Composable
fun rememberFaTypography(): FaTypography {
    val display = FontFamily(
        Font(Res.font.dynapuff_semibold, FontWeight.SemiBold),
        Font(Res.font.dynapuff_bold, FontWeight.Bold),
    )
    val body = FontFamily(
        Font(Res.font.nunito_semibold, FontWeight.SemiBold),
        Font(Res.font.nunito_bold, FontWeight.Bold),
        Font(Res.font.nunito_extrabold, FontWeight.ExtraBold),
    )
    val text = FontFamily(
        Font(Res.font.inconsolata_extrabold, FontWeight.ExtraBold),
    )
    return remember(display, body, text) {
        FaTypography(
${styles.map(s => `            ${camel(s.name)} = TextStyle(fontFamily = ${s.family}, fontWeight = FontWeight(${s.fontWeight}), fontSize = ${sp(s.fontSize)}, lineHeight = ${sp(s.lineHeight)}${s.letterSpacing ? `, letterSpacing = ${parseFloat(s.letterSpacing)}.em` : ''}${s.fontVariantNumeric === 'tabular-nums' ? ', fontFeatureSettings = "tnum"' : ''}),`).join('\n')}
        )
    }
}
`);

// ---------- shadows (hard lips and the soft second lip)
const sh = Object.fromEntries(t.shadow.tokens.map(k => [k.name, k.value]));
const lip = n => { const m = /^0 (\d+)px 0 /.exec(sh[n].light); return `${m[1]}.dp`; };
const wellCol = th => kColor(/rgba\([^)]+\)/.exec(sh['shadow-well'][th])[0]);
const wellInset = /^inset 0 ([0-9]+)px 0 /.exec(sh['shadow-well'].light)[1] + '.dp';
const softCol = th => kColor(/rgba\([^)]+\)/.exec(sh['shadow-soft'][th])[0]);
w('theme/FaShadows.kt', `${HEAD}package nl.hexmaster.finalapproach.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * Depth tokens of v1: a lip was a hard offset straight down in the outline colour. v2 draws a
 * lip in the control's own colour, from the lip and contact tokens in [FaDimens] and [FaColors].
 * Only the float offsets below are still current; everything else here is v1 and is kept until
 * the app's world is ported.
 */
object FaShadows {
    /** v1. Use [FaDimens.lipSm] in new code. */
    val lipSm = ${lip('shadow-lip-sm')}
    /** v1. Use [FaDimens.lipMd] in new code. */
    val lip = ${lip('shadow-lip')}
    /** v1. Use [FaDimens.lipPanel] in new code. */
    val lipLg = ${lip('shadow-lip-lg')}

    /** v1: the translucent second lip under a dialog. Use [FaColors.contactOverlay] in new code. */
    val softLip = ${lip('shadow-soft')}
    val softLipDay = ${softCol('light')}
    val softLipNight = ${softCol('dark')}

    /** v1: the inner top edge of a well. Use [FaDimens.wellInset] in new code. */
    val wellInset = ${wellInset}
    val wellDay = ${wellCol('light')}
    val wellNight = ${wellCol('dark')}

    /** Ground shadow of airborne things: offset down-right. */
    val floatOffsetX = 6.dp
    val floatOffsetY = 10.dp
}
`);
console.log('generated: theme/FaTypography.kt, theme/FaShadows.kt');
