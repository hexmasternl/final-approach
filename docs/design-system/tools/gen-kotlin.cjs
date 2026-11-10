// Generates the Compose theme values and sprite data from the design-system sources.
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
const names = t.color.tokens.map(c => c.name).filter(n => !isSunset(n));
const sunsetNames = t.color.tokens.map(c => c.name).filter(isSunset);
let colors = `${HEAD}package nl.hexmaster.finalapproach.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

/** One property per colour token of the design system. */
@Immutable
data class FaColors(
${names.map(n => `    val ${camel(n)}: Color,`).join('\n')}
) {
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

`;
for (const [th, k] of [['light', 'DayColors'], ['dark', 'NightColors']])
  colors += `val ${k} = FaColors(\n${names.map(n => `    ${camel(n)} = ${kColor(resolve(n, th))},`).join('\n')}\n)\n\n`;
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
const group = (fam, comment) => `    // ${comment}\n` + t[fam].tokens.map(k => `    val ${camel(k.name)} = ${dp(k.value)}`).join('\n');
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
${t.opacity.tokens.map(k => `    const val ${camel(k.name.replace(/^opacity-/, ''))} = ${k.value}f`).join('\n')}
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
console.log('generated: theme/SunsetColors.kt, sprites/AirportSilhouette.kt, theme/FaColors.kt, theme/FaDimens.kt, theme/FaMotion.kt, sprites/AircraftSprites.kt, sprites/FaIcons.kt');

// ---------- typography
const styles = t.type.groups.flatMap(g => g.styles.map(s => ({ ...s, family: s.family || g.family })));
const sp = v => `${parseFloat(v)}.sp`;
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
import nl.hexmaster.finalapproach.resources.inconsolata_medium
import org.jetbrains.compose.resources.Font

/** One text style per type style of the design system. */
@Immutable
class FaTypography(
${styles.map(s => `    val ${camel(s.name)}: TextStyle,`).join('\n')}
)

/**
 * DynaPuff for headers, titles, button labels and the score; Inconsolata for regular text,
 * labels and readouts. Only the weights the app ships are bundled: other weights resolve to
 * the nearest one.
 */
@Composable
fun rememberFaTypography(): FaTypography {
    val display = FontFamily(
        Font(Res.font.dynapuff_semibold, FontWeight.SemiBold),
        Font(Res.font.dynapuff_bold, FontWeight.Bold),
    )
    val text = FontFamily(
        Font(Res.font.inconsolata_medium, FontWeight.Medium),
        Font(Res.font.inconsolata_extrabold, FontWeight.ExtraBold),
    )
    return remember(display, text) {
        FaTypography(
${styles.map(s => `            ${camel(s.name)} = TextStyle(fontFamily = ${s.family}, fontWeight = FontWeight(${s.fontWeight}), fontSize = ${sp(s.fontSize)}, lineHeight = ${sp(s.lineHeight)}${s.letterSpacing ? `, letterSpacing = ${parseFloat(s.letterSpacing)}.em` : ''}),`).join('\n')}
        )
    }
}
`);

// ---------- shadows (hard lips and the soft second lip)
const sh = Object.fromEntries(t.shadow.tokens.map(k => [k.name, k.value]));
const lip = n => { const m = /^0 (\d+)px 0 /.exec(sh[n].light); return `${m[1]}.dp`; };
const softCol = th => kColor(/rgba\([^)]+\)/.exec(sh['shadow-soft'][th])[0]);
w('theme/FaShadows.kt', `${HEAD}package nl.hexmaster.finalapproach.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/** Depth tokens. A lip is a hard, unblurred offset straight down in the outline colour. */
object FaShadows {
    val lipSm = ${lip('shadow-lip-sm')}
    val lip = ${lip('shadow-lip')}
    val lipLg = ${lip('shadow-lip-lg')}

    /** The translucent second lip that grounds a dialog on the scrim. */
    val softLip = ${lip('shadow-soft')}
    val softLipDay = ${softCol('light')}
    val softLipNight = ${softCol('dark')}

    /** Ground shadow of airborne things: offset down-right. */
    val floatOffsetX = 6.dp
    val floatOffsetY = 10.dp
}
`);
console.log('generated: theme/FaTypography.kt, theme/FaShadows.kt');
