// Checks tokens.json against the rules of design system v2.
// usage, from the repository root: node docs/design-system/tools/check-tokens.cjs [git ref to compare with]
// With a git ref (for example origin/development) it also checks that every token of that ref
// still exists with the same value, which is what keeps the app building while v1 tokens remain.
const fs = require('fs');
const path = require('path');
const { execFileSync } = require('child_process');
const rel = 'docs/design-system/tokens.json';
const now = JSON.parse(fs.readFileSync(path.join(__dirname, '..', 'tokens.json'), 'utf8'));
const fams = ['spacing', 'radius', 'shadow', 'line', 'size', 'duration', 'easing', 'opacity', 'zIndex'];
const all = t => { const m = new Map(); for (const c of t.color.tokens) m.set('color/' + c.name, c); for (const f of fams) for (const k of t[f].tokens) m.set(f + '/' + k.name, k); return m; };
const errors = [];
const cur = all(now);

// every token has a usage note; every deprecated token names a replacement that exists
const names = new Set([...cur.keys()].map(k => k.split('/')[1]));
for (const [key, k] of cur) {
  if (!k.usage) errors.push(`${key}: no usage note`);
  if (k.deprecated && /^[a-z0-9-]+$/.test(k.deprecated) && !names.has(k.deprecated)) errors.push(`${key}: replacement ${k.deprecated} does not exist`);
}
// ramps have at least three steps
const ramps = ['grass', 'leaf', 'pine', 'wood', 'roof', 'wall', 'tin', 'stone', 'concrete', 'asphalt', 'water', 'straw', 'sun', 'sky', 'cherry', 'tangerine', 'mint', 'paper'];
for (const r of ramps) {
  const steps = now.color.tokens.filter(c => new RegExp(`^${r}-\\d+$`).test(c.name));
  if (steps.length < 3) errors.push(`ramp ${r}: ${steps.length} steps, at least 3`);
}
// line ranks
for (const [n, v] of [['line-hero', '4px'], ['line-zone', '3px'], ['line-ui', '2.5px'], ['line-ui-sm', '2px'], ['line-detail', '1.25px']])
  if ((cur.get('line/' + n) || {}).value !== v) errors.push(`line rank ${n} must be ${v}`);
// type weights
for (const g of now.type.groups) for (const s of g.styles) {
  const fam = s.family || g.family;
  if (fam === 'body' && s.fontWeight < 600) errors.push(`type ${s.name}: Nunito lighter than 600`);
  if (fam === 'text' && s.fontWeight < 700) errors.push(`type ${s.name}: Inconsolata lighter than 700`);
}
// nothing that existed before is gone or changed
const ref = process.argv[2];
if (ref) {
  const before = all(JSON.parse(execFileSync('git', ['show', `${ref}:${rel}`], { encoding: 'utf8', maxBuffer: 1 << 26 })));
  for (const [key, k] of before) {
    const c = cur.get(key);
    if (!c) errors.push(`${key}: removed`);
    else if (JSON.stringify(c.value) !== JSON.stringify(k.value)) errors.push(`${key}: value changed`);
  }
  // text styles may gain weight and move family, but keep their name and size
  const styles = t => new Map(t.type.groups.flatMap(g => g.styles.map(s => [s.name, s])));
  const sNow = styles(now);
  for (const [n, s] of styles(JSON.parse(execFileSync('git', ['show', `${ref}:${rel}`], { encoding: 'utf8', maxBuffer: 1 << 26 })))) {
    const c = sNow.get(n);
    if (!c) errors.push(`type ${n}: removed`);
    else if (c.fontSize !== s.fontSize || c.lineHeight !== s.lineHeight) errors.push(`type ${n}: size changed`);
  }
  console.log(`compared with ${ref}: ${before.size} tokens`);
}
if (errors.length) { console.error(errors.join('\n')); process.exit(1); }
console.log(`tokens.json ok: ${cur.size} tokens, ${now.color.tokens.length} colours`);
