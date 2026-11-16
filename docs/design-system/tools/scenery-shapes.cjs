// Parses the SVG markup of a design-system sprite (bundle.js: WORLD, DETAILS, the cloud) into a
// small shape tree, for gen-kotlin.cjs. It knows exactly the elements and attributes the sprites
// use and stops with the sprite's name on anything else, so nothing is dropped silently.
//
// A node is a shape { shape, d, fill, stroke, width, opacity, nonScaling, matrix } or a group
// { group, clip, matrix, children }. A matrix is [a, b, c, d, e, f] as in SVG. Paths are
// normalised to absolute-free "M x y L ..." text exactly as the aircraft pass writes them.

const num = n => { const s = (+n).toFixed(3).replace(/\.?0+$/, ''); return s === '-0' ? '0' : s; };
const norm = d => d.match(/[a-zA-Z]|-?(?:\d+\.\d+|\d+|\.\d+)(?:e-?\d+)?/g).map(x => /[a-zA-Z]/.test(x) ? x : num(x)).join(' ');

const rectPath = (x, y, wd, h, r) => {
  x = +x; y = +y; wd = +wd; h = +h; r = Math.min(+r || 0, wd / 2, h / 2);
  return r
    ? `M ${num(x + r)} ${num(y)} H ${num(x + wd - r)} A ${num(r)} ${num(r)} 0 0 1 ${num(x + wd)} ${num(y + r)} V ${num(y + h - r)} A ${num(r)} ${num(r)} 0 0 1 ${num(x + wd - r)} ${num(y + h)} H ${num(x + r)} A ${num(r)} ${num(r)} 0 0 1 ${num(x)} ${num(y + h - r)} V ${num(y + r)} A ${num(r)} ${num(r)} 0 0 1 ${num(x + r)} ${num(y)} Z`
    : `M ${num(x)} ${num(y)} H ${num(x + wd)} V ${num(y + h)} H ${num(x)} Z`;
};
const ellipsePath = (cx, cy, rx, ry) => {
  cx = +cx; cy = +cy; rx = +rx; ry = +ry;
  return `M ${num(cx - rx)} ${num(cy)} A ${num(rx)} ${num(ry)} 0 1 0 ${num(cx + rx)} ${num(cy)} A ${num(rx)} ${num(ry)} 0 1 0 ${num(cx - rx)} ${num(cy)} Z`;
};
const polygonPath = pts => {
  const p = pts.trim().split(/[\s,]+/).map(Number);
  let d = '';
  for (let i = 0; i < p.length; i += 2) d += `${i ? ' L' : 'M'} ${num(p[i])} ${num(p[i + 1])}`;
  return d + ' Z';
};

const SHAPES = {
  rect: ['x', 'y', 'width', 'height', 'rx'],
  path: ['d'],
  circle: ['cx', 'cy', 'r'],
  ellipse: ['cx', 'cy', 'rx', 'ry'],
  polygon: ['points'],
};
const COMMON = ['transform', 'opacity', 'vector-effect'];

const mul = (m, n) => [
  m[0] * n[0] + m[2] * n[1], m[1] * n[0] + m[3] * n[1],
  m[0] * n[2] + m[2] * n[3], m[1] * n[2] + m[3] * n[3],
  m[0] * n[4] + m[2] * n[5] + m[4], m[1] * n[4] + m[3] * n[5] + m[5],
];
const clean = m => m.map(v => +num(v) === 0 ? 0 : +(+v).toFixed(5));

function parseTransform(where, s) {
  let m = [1, 0, 0, 1, 0, 0];
  const re = /\s*(scale|rotate|translate)\(([^)]*)\)\s*/gy;
  let x, used = 0;
  while ((x = re.exec(s))) {
    used = re.lastIndex;
    const a = x[2].split(/[\s,]+/).filter(Boolean).map(Number);
    if (a.some(Number.isNaN)) throw new Error(`${where}: transform "${s}"`);
    let t;
    if (x[1] === 'scale') t = [a[0], 0, 0, a.length > 1 ? a[1] : a[0], 0, 0];
    else if (x[1] === 'translate') t = [1, 0, 0, 1, a[0], a.length > 1 ? a[1] : 0];
    else {
      const r = a[0] * Math.PI / 180, c = Math.cos(r), sn = Math.sin(r), cx = a[1] || 0, cy = a[2] || 0;
      t = [c, sn, -sn, c, cx - c * cx + sn * cy, cy - sn * cx - c * cy];
    }
    m = mul(m, t);
  }
  if (used !== s.length && s.trim().length) throw new Error(`${where}: unknown transform "${s}"`);
  return clean(m);
}

/**
 * @param name   sprite name, for error messages
 * @param markup the sprite's SVG markup
 * @param paintOf maps a {{key}} to { fill, stroke, width } (FA.worldPaint)
 * @param optionalPaint true for shadows, whose shapes carry a key only when they are a stroked line
 * @returns { nodes, tokens } the node tree and every colour token it uses
 */
function parseArt(name, markup, paintOf, optionalPaint) {
  const where = `scenery ${name}`;
  const clips = {};
  const root = { children: [] };
  const stack = [root];
  const tokens = new Set();
  let clip = null; // the clipPath being collected
  const re = /<(\/?)([a-zA-Z]+)((?:[^>"]|"[^"]*")*?)(\/?)>/g;
  let m, pos = 0;
  const attrsOf = (tag, s) => {
    const a = {}; let key = null;
    const rest = s.replace(/\{\{([^}]*)\}\}/, (_, k) => { key = k; return ''; });
    let left = rest;
    for (const x of rest.matchAll(/\s*([a-zA-Z-:]+)="([^"]*)"/g)) { a[x[1]] = x[2]; left = left.replace(x[0], ''); }
    if (left.trim()) throw new Error(`${where}: cannot read "${left.trim()}" in <${tag}>`);
    return { a, key };
  };
  while ((m = re.exec(markup))) {
    if (markup.slice(pos, m.index).trim()) throw new Error(`${where}: text outside an element`);
    pos = re.lastIndex;
    const [, closing, tag, rawAttrs, selfClose] = m;
    if (closing) {
      if (tag === 'clipPath') { clip = null; continue; }
      if (tag !== 'g') throw new Error(`${where}: unexpected </${tag}>`);
      stack.pop();
      continue;
    }
    const { a, key } = attrsOf(tag, rawAttrs);
    const allow = (list) => { for (const k of Object.keys(a)) if (!list.includes(k)) throw new Error(`${where}: unknown attribute ${k} on <${tag}>`); };
    if (tag === 'clipPath') {
      allow(['id']);
      if (selfClose) throw new Error(`${where}: empty clipPath`);
      clip = { id: a.id, ds: [] }; clips[a.id] = clip;
      continue;
    }
    if (tag === 'g') {
      allow(['clip-path', 'transform']);
      const g = { group: true, clip: null, matrix: null, children: [] };
      if (a['clip-path']) {
        const id = /^url\(#([^)]+)\)$/.exec(a['clip-path']);
        if (!id || !clips[id[1]]) throw new Error(`${where}: clip-path ${a['clip-path']} names no clipPath`);
        g.clip = clips[id[1]].ds.join(' ');
      }
      if (a.transform) g.matrix = parseTransform(where, a.transform);
      if (!g.clip && !g.matrix) throw new Error(`${where}: a group with neither a clip nor a transform`);
      stack[stack.length - 1].children.push(g);
      if (!selfClose) stack.push(g);
      continue;
    }
    if (!SHAPES[tag]) throw new Error(`${where}: unknown element <${tag}>`);
    allow(SHAPES[tag].concat(COMMON));
    const optional = ['rx', 'x', 'y', 'cx', 'cy']; for (const k of SHAPES[tag]) { if (a[k] === undefined && !(optional.includes(k) && !(tag === 'ellipse' && k === 'rx'))) throw new Error(`${where}: <${tag}> has no ${k}`); if (a[k] === undefined) a[k] = '0'; }
    const d = tag === 'path' ? norm(a.d) : tag === 'rect' ? rectPath(a.x, a.y, a.width, a.height, a.rx)
      : tag === 'circle' ? ellipsePath(a.cx, a.cy, a.r, a.r) : tag === 'ellipse' ? ellipsePath(a.cx, a.cy, a.rx, a.ry) : polygonPath(a.points);
    if (clip) { clip.ds.push(d); if (key || a.transform) throw new Error(`${where}: a clip shape with a paint or a transform`); continue; }
    if (a['vector-effect'] && a['vector-effect'] !== 'non-scaling-stroke') throw new Error(`${where}: vector-effect ${a['vector-effect']}`);
    const node = { shape: true, d, fill: null, stroke: null, width: 0, opacity: a.opacity === undefined ? 1 : +a.opacity, nonScaling: !!a['vector-effect'], matrix: a.transform ? parseTransform(where, a.transform) : null };
    if (paintOf) {
      if (!key && !optionalPaint) throw new Error(`${where}: a shape has no paint key`);
      const p = key ? paintOf(key) : { fill: null, stroke: null, width: 0 };
      node.fill = p.fill; node.stroke = p.stroke; node.width = p.width;
      if (p.fill) tokens.add(p.fill);
      if (p.stroke) tokens.add(p.stroke);
      if (p.stroke && !(p.width > 0)) throw new Error(`${where}: stroke without a width in {{${key}}}`);
    } else if (key) throw new Error(`${where}: a shadow shape has a paint key`);
    stack[stack.length - 1].children.push(node);
  }
  if (markup.slice(pos).trim()) throw new Error(`${where}: text outside an element`);
  if (stack.length !== 1 || clip) throw new Error(`${where}: unbalanced markup`);
  return { nodes: root.children, tokens: [...tokens] };
}

/**
 * The bounding box [minX, minY, maxX, maxY] of the geometry of a node tree: strokes, clips and
 * shadows left out, curves and arcs sampled. It is what the app fits into a scenery item's
 * footprint, so levels can give each sprite its natural size.
 */
function boundsOf(nodes) {
  let x0 = Infinity, y0 = Infinity, x1 = -Infinity, y1 = -Infinity;
  const put = (m, x, y) => {
    const px = m ? m[0] * x + m[2] * y + m[4] : x, py = m ? m[1] * x + m[3] * y + m[5] : y;
    x0 = Math.min(x0, px); y0 = Math.min(y0, py); x1 = Math.max(x1, px); y1 = Math.max(y1, py);
  };
  const walk = (list, outer) => list.forEach(n => {
    const m = n.matrix ? (outer ? mul(outer, n.matrix) : n.matrix) : outer;
    if (n.group) return walk(n.children, m);
    pathPoints(n.d).forEach(p => put(m, p[0], p[1]));
  });
  walk(nodes, null);
  return [x0, y0, x1, y1];
}

/** Points along a normalised path: every vertex, and samples of its curves and arcs. */
function pathPoints(d) {
  const t = d.match(/[a-zA-Z]|-?\d*\.?\d+/g);
  const out = [];
  let i = 0, cmd = '', x = 0, y = 0, sx = 0, sy = 0, last = null;
  const n = () => +t[i++];
  const pt = (px, py) => { out.push([px, py]); x = px; y = py; };
  while (i < t.length) {
    if (/[a-zA-Z]/.test(t[i])) cmd = t[i++];
    const rel = cmd === cmd.toLowerCase(), c = cmd.toUpperCase(), ox = rel ? x : 0, oy = rel ? y : 0;
    if (!'QCTS'.includes(c)) last = null;
    if (c === 'M') { pt(ox + n(), oy + n()); sx = x; sy = y; cmd = rel ? 'l' : 'L'; }
    else if (c === 'L') pt(ox + n(), oy + n());
    else if (c === 'H') pt(ox + n(), y);
    else if (c === 'V') pt(x, oy + n());
    else if (c === 'Z') { x = sx; y = sy; }
    else if (c === 'Q' || c === 'C' || c === 'T' || c === 'S') {
      const smooth = c === 'T' || c === 'S', k = c === 'Q' || c === 'T' ? 2 : 3, p = [[x, y]];
      // A smooth curve reflects the previous control point of its own kind.
      if (smooth) p.push(last && last.k === k ? [2 * x - last.p[0], 2 * y - last.p[1]] : [x, y]);
      for (let j = smooth ? 1 : 0; j < k; j++) p.push([ox + n(), oy + n()]);
      for (let u = 1; u <= 16; u++) {
        const f = u / 16, g = 1 - f;
        const q = k === 2
          ? [g * g * p[0][0] + 2 * g * f * p[1][0] + f * f * p[2][0], g * g * p[0][1] + 2 * g * f * p[1][1] + f * f * p[2][1]]
          : [g ** 3 * p[0][0] + 3 * g * g * f * p[1][0] + 3 * g * f * f * p[2][0] + f ** 3 * p[3][0], g ** 3 * p[0][1] + 3 * g * g * f * p[1][1] + 3 * g * f * f * p[2][1] + f ** 3 * p[3][1]];
        out.push(q);
      }
      last = { k, p: p[k - 1] };
      x = p[k][0]; y = p[k][1];
      continue;
    } else if (c === 'A') {
      let rx = Math.abs(n()), ry = Math.abs(n()); const rot = n() * Math.PI / 180, fa = n(), fs = n(), ex = ox + n(), ey = oy + n();
      arcPoints(x, y, rx, ry, rot, fa, fs, ex, ey).forEach(q => out.push(q));
      x = ex; y = ey;
    } else throw new Error('path command ' + cmd);
  }
  return out;
}

/** Samples of an SVG elliptical arc, by the endpoint-to-centre conversion of the SVG specification. */
function arcPoints(x1, y1, rx, ry, phi, fa, fs, x2, y2) {
  if (!rx || !ry) return [[x2, y2]];
  const c = Math.cos(phi), s = Math.sin(phi), dx = (x1 - x2) / 2, dy = (y1 - y2) / 2;
  const px = c * dx + s * dy, py = -s * dx + c * dy;
  const lam = px * px / (rx * rx) + py * py / (ry * ry);
  if (lam > 1) { rx *= Math.sqrt(lam); ry *= Math.sqrt(lam); }
  const num = rx * rx * ry * ry - rx * rx * py * py - ry * ry * px * px, den = rx * rx * py * py + ry * ry * px * px;
  const k = (fa === fs ? -1 : 1) * Math.sqrt(Math.max(0, num / den));
  const cxp = k * rx * py / ry, cyp = -k * ry * px / rx;
  const cx = c * cxp - s * cyp + (x1 + x2) / 2, cy = s * cxp + c * cyp + (y1 + y2) / 2;
  const ang = (ux, uy, vx, vy) => Math.atan2(ux * vy - uy * vx, ux * vx + uy * vy);
  const th1 = ang(1, 0, (px - cxp) / rx, (py - cyp) / ry);
  let dth = ang((px - cxp) / rx, (py - cyp) / ry, (-px - cxp) / rx, (-py - cyp) / ry);
  if (!fs && dth > 0) dth -= 2 * Math.PI; else if (fs && dth < 0) dth += 2 * Math.PI;
  const pts = [];
  for (let u = 1; u <= 32; u++) {
    const a = th1 + dth * u / 32;
    pts.push([cx + rx * Math.cos(a) * c - ry * Math.sin(a) * s, cy + rx * Math.cos(a) * s + ry * Math.sin(a) * c]);
  }
  return pts;
}

module.exports = { parseArt, boundsOf, norm, num };
