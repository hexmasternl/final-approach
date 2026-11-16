// Builds the Final Approach v2 proposal page (final-approach-v2.html, next to this script).
// Usage: node build.cjs [design-system dir] [--preview]
// --preview also writes preview.html, the page wrapped in a document skeleton for a local browser.
const fs = require('fs');
const path = require('path');
const W = require('./world.cjs');
const DS = (process.argv[2] && !process.argv[2].startsWith('--')) ? process.argv[2] : path.join(__dirname, '..', '..', '..', '..', 'docs', 'design-system');
const { SPR, rd, inner } = W;
const sc = W.scene();
const tokens = fs.readFileSync(path.join(__dirname, 'tokens.css'), 'utf8');

// ---- icons (existing set, unchanged), recoloured to currentColor
const ICONS = ['arrow-left', 'bolt', 'check', 'close', 'finger', 'heart', 'helicopter', 'home', 'lock', 'music', 'pause', 'plane', 'play', 'restart', 'settings', 'sound-on', 'star', 'tap', 'warning'];
const iconDefs = ICONS.map(i => `<symbol id="i-${i}" viewBox="0 0 24 24">${inner(rd(`assets/icons/${i}.svg`)).replace(/#1d2545/gi, 'currentColor')}</symbol>`).join('');
const ic = (name, cls = '') => `<svg class="ic ${cls}" aria-hidden="true"><use href="#i-${name}"/></svg>`;
const old = name => `data:image/svg+xml;base64,${Buffer.from(rd(`assets/scenery/${name}.svg`)).toString('base64')}`;

// ---- helpers
const tile = (id, label, k = 2, extra = '') => {
  const s = SPR[id], [w, h] = s.tile;
  return `<figure class="tile"><svg viewBox="${-w / 2} ${-h / 2 + (s.cy || 0)} ${w} ${h}" width="${w * k}" height="${h * k}" style="max-width:100%;height:auto" role="img" aria-label="${label}"><rect x="${-w}" y="${-h}" width="${w * 2}" height="${h * 2}" style="fill:var(--grass-500)"/><g style="fill:var(--shadow);opacity:var(--shadow-o)"><use href="#h-${id}" transform="translate(${s.off[0]},${s.off[1]})"/></g><use href="#s-${id}"${extra}/></svg><figcaption>${label}</figcaption></figure>`;
};
const oldTile = (name, label, w, h, k = 2) => `<figure class="tile old"><div class="oldbg" style="width:${(w + 28) * k}px;height:${(h + 28) * k}px"><img src="${old(name)}" width="${w * k}" height="${h * k}" alt="${label}"></div><figcaption>${label}</figcaption></figure>`;
const ramp = (name, steps, note) => `<div class="ramp"><div class="sw">${steps.map(s => `<i style="background:var(--${name}-${s})" title="${name}-${s}"></i>`).join('')}</div><b>${name}</b><span>${steps.join(' · ')}${note ? ` · ${note}` : ''}</span></div>`;
const heart = on => `<svg width="22" height="22" viewBox="0 0 24 24" aria-hidden="true"><path d="M12 20.500S3.500 14.800 3.500 9a4.500 4.500 0 0 1 8.500 -2a4.500 4.500 0 0 1 8.500 2c0 5.800 -8.500 11.500 -8.500 11.500z" style="fill:var(--${on ? 'cherry-500' : 'paper-300'});stroke:var(--${on ? 'outline' : 'paper-500'});stroke-width:2"/>${on ? '<path d="M7.200 8.800q0.400 -2.200 2.400 -2.600" style="fill:none;stroke:#fff;stroke-width:1.800"/>' : ''}</svg>`;
const btn = (label, cls = '', icon = '', attr = '') => `<button class="b ${cls}" ${attr}><span>${icon ? ic(icon) : ''}${label}</span></button>`;
const sceneSvg = (vb, cls = '', air = true, attr = '') => `<svg class="${cls}" viewBox="${vb}" preserveAspectRatio="xMidYMid slice" ${attr}><use href="#scene-ground"/>${air ? '<use href="#scene-air"/>' : ''}</svg>`;

const hud = `<div class="hud"><div class="hud-l">
<div class="strip"><span class="k sun">${ic('star', 'xs')}SCORE</span><span class="v">12 450</span></div>
<div class="sticker">${ic('bolt', 'xs')}x3</div></div>
<div class="hud-r">
<div class="strip"><span class="k">NEXT</span><span class="v"><svg width="30" height="30" viewBox="-52 -52 104 104" aria-label="Jet"><use href="#ac-jet" transform="rotate(90)"/></svg><span class="from">${ic('arrow-left', 'xs')}</span></span></div>
<div class="strip"><span class="v hearts">${heart(1)}${heart(1)}${heart(0)}</span></div>
<button class="b ghost icon hudbtn" aria-label="Pause"><span>${ic('pause')}</span></button></div></div>`;

const css = `
${tokens}
/* Layout: one column of sections on a pale tower-window blue; specimens sit on sky, field or paper stages. */
:root{color-scheme:light;--page:#eef6fc;--rule:#cddfee}
*{box-sizing:border-box}
body{margin:0;background:var(--page);color:var(--ink);font:600 16px/1.55 var(--ft)}
svg{stroke-linejoin:round;stroke-linecap:round}
.wrap{max-width:1120px;margin:0 auto;padding-block:40px 96px;padding-left:max(16px,env(safe-area-inset-left,0px));padding-right:max(16px,env(safe-area-inset-right,0px));display:flex;flex-direction:column;gap:80px}
h1{font:700 clamp(36px,6.4vw,64px)/1 var(--fd);margin:0;text-wrap:balance;letter-spacing:-.01em}
h2{font:700 clamp(27px,4vw,38px)/1.1 var(--fd);margin:0;text-wrap:balance}
h3{font:600 20px/1.2 var(--fd);margin:0}
p{margin:0;max-width:68ch}
.eyebrow{font:800 12.5px/1.2 var(--fm);letter-spacing:.1em;text-transform:uppercase;color:var(--sky-900)}
.lead{font:600 18px/1.55 var(--ft);color:var(--ink-2);max-width:64ch}
section{display:flex;flex-direction:column;gap:26px}
.head{display:flex;flex-direction:column;gap:10px}
.sub{display:flex;flex-direction:column;gap:14px}
.grid{display:grid;gap:16px}
.g2{grid-template-columns:repeat(auto-fit,minmax(min(100%,400px),1fr))}
.g3{grid-template-columns:repeat(auto-fit,minmax(min(100%,290px),1fr))}
code{font:700 .86em var(--fm);background:var(--sky-100);padding:1px 5px;border-radius:5px;color:var(--sky-900)}
.note{font:600 14px/1.5 var(--ft);color:var(--ink-2)}
/* principles: a ruled list, not cards */
.rules{display:grid;grid-template-columns:repeat(auto-fit,minmax(min(100%,330px),1fr));gap:0 36px;margin:0;padding:0;list-style:none}
.rules li{display:grid;grid-template-columns:42px 1fr;gap:2px 10px;padding:14px 0;border-top:1.5px solid var(--rule);min-width:0}
.rules li>i{grid-row:span 2;font:800 13px/22px var(--fm);font-style:normal;color:var(--sky-900)}
.rules li>b{font:600 17.500px/22px var(--fd)}
.rules li>span{font:600 14.500px/1.5 var(--ft);color:var(--ink-2)}
/* stages */
.stage{border-radius:22px;padding:26px;display:flex;flex-wrap:wrap;gap:22px;align-items:center;justify-content:center;min-width:0}
.stage.sky{background:var(--sky-300)}.stage.field{background:var(--grass-500)}.stage.paper{background:var(--paper-0);box-shadow:inset 0 0 0 1.5px var(--rule)}
.stage.col{flex-direction:column}.stage.start{justify-content:flex-start;align-items:flex-end}.stage.field .tile figcaption{color:var(--ink)}
.cap{font:800 11.500px/1.2 var(--fm);letter-spacing:.09em;text-transform:uppercase;color:var(--ink-2)}
.stage.field .cap,.stage.sky .cap{color:var(--ink)}
.spec{display:flex;flex-direction:column;gap:10px;min-width:0}
/* tiles */
.tile{margin:0;display:flex;flex-direction:column;gap:6px;align-items:center;min-width:0}
.tile svg,.tile .oldbg{border-radius:14px;display:block}
.tile figcaption{font:800 11.500px/1.2 var(--fm);letter-spacing:.06em;color:var(--ink-2);text-align:center}
.oldbg{display:grid;place-items:center;background:repeating-linear-gradient(102deg,#7fcf4b 0 32px,#5cb337 32px 64px);max-width:100%}
.oldbg img{max-width:90%;height:auto}
.pair{display:grid;grid-template-columns:1fr 1fr;gap:10px;align-items:end;justify-items:center}.pair .tile{width:100%}.pair .tile svg{width:100%;height:auto}.pair .oldbg{width:100%!important;height:auto!important;aspect-ratio:1}.pair .oldbg img{width:62%;height:auto}
.cmp{display:flex;flex-direction:column;gap:12px;padding:16px;border-radius:20px;background:var(--paper-0);box-shadow:inset 0 0 0 1.5px var(--rule);min-width:0}
.cmp p{font:600 14px/1.45 var(--ft);color:var(--ink-2)}
.cmp p b{color:var(--ink)}
.cmp .ui-pair{display:grid;grid-template-columns:auto 1fr;gap:20px 14px;align-items:center;justify-items:start;background:var(--sky-300);border-radius:14px;padding:22px 16px 26px}.cmp .ui-pair .tag{color:var(--ink)}
.cmp .ui-pair.fieldbg{background:var(--grass-500)}
.tag{font:800 10.500px/1 var(--fm);letter-spacing:.1em;text-transform:uppercase;color:var(--ink-2)}
/* ramps */
.ramps{display:grid;grid-template-columns:repeat(auto-fit,minmax(min(100%,250px),1fr));gap:14px 22px}
.ramp{display:grid;grid-template-columns:auto 1fr;gap:1px 10px;align-items:center;min-width:0}
.ramp .sw{grid-row:span 2;display:flex;border-radius:9px;overflow:hidden;box-shadow:0 0 0 1.5px var(--outline)}
.ramp .sw i{width:22px;height:34px}
.ramp b{font:800 13.500px/1.1 var(--fm)}.ramp span{font:600 11.500px/1.2 var(--fm);color:var(--ink-2)}
/* line ranks */
.ranks{display:grid;gap:10px;margin:0;padding:0;list-style:none;width:100%}
.ranks li{display:grid;grid-template-columns:minmax(70px,150px) 1fr;gap:14px;align-items:center}
.ranks li>i{display:block;border-radius:9px;background:var(--outline)}
.ranks li>span{font:600 14px/1.35 var(--ft);color:var(--ink-2)}.ranks b{color:var(--ink);font:800 13px var(--fm)}
/* device */
.dev{position:relative;aspect-ratio:16/9;border-radius:clamp(10px,2.4vw,26px);overflow:hidden;box-shadow:0 0 0 clamp(4px,.8vw,9px) var(--outline);margin:clamp(4px,.8vw,9px);background:var(--grass-500)}
.dev>svg{position:absolute;inset:0;width:100%;height:100%}
.dev>.ui{position:absolute;left:0;top:0;width:800px;height:450px;transform-origin:0 0;transform:scale(var(--s,1))}
.crop{border-radius:18px;overflow:hidden;aspect-ratio:16/9;background:var(--grass-500);min-width:0}
.crop svg{width:100%;height:100%;display:block}
.hud{position:absolute;left:16px;right:16px;top:14px;display:flex;justify-content:space-between;align-items:flex-start}
.hud-l,.hud-r{display:flex;gap:10px;align-items:center}
.scrim{position:absolute;inset:0;background:rgba(17,24,58,.56);display:grid;place-items:center}
/* icons */
.ic{width:22px;height:22px;flex:none}.ic.xs{width:15px;height:15px}.ic.lg{width:30px;height:30px}
/* ---------- UI kit v2 ---------- */
.b{--f:var(--sun-500);--fl:var(--sun-200);--fd2:var(--sun-700);--fe:var(--sun-900);--lip:5px;position:relative;display:inline-flex;border:0;margin:0;padding:0 0 var(--lip);border-radius:16px;background:var(--fd2);color:var(--ink);font:600 18px/1 var(--fd);cursor:pointer;-webkit-tap-highlight-color:transparent;box-shadow:0 0 0 2.5px var(--outline),0 6.500px 0 0 rgba(29,37,69,.2),inset 0 -1.500px 0 var(--fe);transition:transform .09s cubic-bezier(.2,.9,.3,1),padding .09s cubic-bezier(.2,.9,.3,1)}
.b>span{display:flex;align-items:center;justify-content:center;gap:8px;flex:1;min-height:45px;padding:0 22px;border-radius:16px 16px 12px 12px;background:var(--f);box-shadow:inset 0 2px 0 var(--fl);white-space:nowrap}
.b::before{content:"";position:absolute;left:15px;top:7px;width:11px;height:4px;border-radius:9px;background:#fff;opacity:.6}
.b:active,.b.is-pressed{--lip:1px;transform:translateY(4px);box-shadow:0 0 0 2.5px var(--outline),0 2.500px 0 0 rgba(29,37,69,.2),inset 0 -1px 0 var(--fe)}
.b:focus-visible,.b.is-focus{outline:2.5px solid var(--outline);outline-offset:5.500px;box-shadow:0 0 0 2.5px var(--outline),0 0 0 5.500px #fff,inset 0 -1.500px 0 var(--fe)}
.b.sky{--f:var(--sky-500);--fl:var(--sky-300);--fd2:var(--sky-700);--fe:var(--sky-900)}
.b.cherry{--f:var(--cherry-500);--fl:var(--cherry-200);--fd2:var(--cherry-700);--fe:var(--cherry-900)}
.b.ghost{--f:var(--paper-0);--fl:#fff;--fd2:var(--paper-300);--fe:var(--paper-500)}.b.ghost::before,.b.icon::before{display:none}
.b.sm{font-size:15px;border-radius:13px;--lip:4px}.b.sm>span{min-height:34px;padding:0 15px;border-radius:13px 13px 10px 10px;gap:6px}.b.sm::before{left:12px;top:6px;width:8px;height:3.500px}
.b.lg{font-size:24px;font-weight:700;border-radius:21px;--lip:7px}.b.lg>span{min-height:56px;padding:0 32px;border-radius:21px 21px 16px 16px;gap:10px}.b.lg::before{left:20px;top:9px;width:15px;height:5px}
.b.icon>span{padding:0;width:45px}.b.block{display:flex;width:100%}
.b:disabled{--f:var(--paper-200);--fl:var(--paper-100);--fd2:var(--paper-300);--fe:var(--paper-400);--lip:2px;color:var(--ink-3);cursor:not-allowed;box-shadow:0 0 0 2.5px var(--ink-3)}.b:disabled::before{display:none}
.b.hudbtn{border-radius:12px;--lip:4px;box-shadow:0 0 0 2.5px var(--outline),0 5.500px 0 0 rgba(18,48,31,.28),inset 0 -1.500px 0 var(--fe)}.b.hudbtn>span{min-height:36px;width:40px;border-radius:12px 12px 9px 9px}
.tg{position:relative;flex:none;width:60px;height:34px;border:0;padding:0;border-radius:99px;background:var(--paper-300);box-shadow:0 0 0 2.5px var(--outline),inset 0 3px 0 var(--paper-400);cursor:pointer;color:var(--mint-900)}
.tg::after{content:"";position:absolute;left:4px;top:4px;width:26px;height:26px;border-radius:50%;background:var(--paper-0);box-shadow:0 0 0 2.5px var(--outline),inset 0 -3.500px 0 var(--paper-400),inset 0 2px 0 #fff;transition:left .16s cubic-bezier(.34,1.56,.64,1)}
.tg .ic{position:absolute;left:8px;top:8px;width:18px;height:18px;opacity:0}
.tg[aria-checked="true"]{background:var(--mint-500);box-shadow:0 0 0 2.5px var(--outline),inset 0 3px 0 var(--mint-700)}
.tg[aria-checked="true"]::after{left:30px}.tg[aria-checked="true"] .ic{opacity:1}
.tg:disabled{background:var(--paper-200);box-shadow:0 0 0 2.5px var(--ink-3);cursor:not-allowed}.tg:disabled::after{box-shadow:0 0 0 2.5px var(--ink-3)}
.seg{display:inline-flex;gap:4px;padding:4px 4px 5px;border-radius:15px;background:var(--paper-300);box-shadow:0 0 0 2.5px var(--outline),inset 0 3px 0 var(--paper-400)}
.seg button{border:0;background:none;font:600 15px/1 var(--fd);color:var(--ink-2);min-height:36px;padding:0 15px;border-radius:11px;cursor:pointer}
.seg button[aria-pressed="true"]{background:var(--sun-500);color:var(--ink);box-shadow:0 0 0 2px var(--outline),inset 0 2px 0 var(--sun-200),inset 0 -3.500px 0 var(--sun-700)}
.chip{display:inline-flex;align-items:center;gap:5px;height:26px;padding:0 10px;border-radius:9px;font:800 13px/1 var(--ft);background:var(--sky-100);color:var(--sky-900);box-shadow:inset 0 0 0 1.500px var(--sky-300);white-space:nowrap}
.chip.sun{background:var(--sun-200);color:#7a4d00;box-shadow:inset 0 0 0 1.500px var(--sun-700)}
.chip.mint{background:var(--mint-200);color:var(--mint-900);box-shadow:inset 0 0 0 1.500px var(--mint-700)}
.chip.cherry{background:var(--cherry-200);color:var(--cherry-900);box-shadow:inset 0 0 0 1.500px var(--cherry-700)}
.chip.plain{background:var(--paper-200);color:var(--ink-2);box-shadow:inset 0 0 0 1.500px var(--paper-400)}
.strip{display:inline-flex;align-items:stretch;height:40px;border-radius:12px;background:var(--paper-100);color:var(--ink);overflow:hidden;box-shadow:0 0 0 2.5px var(--outline),0 5.500px 0 0 rgba(18,48,31,.28)}
.strip .k{display:flex;align-items:center;gap:5px;padding:0 9px;background:var(--paper-200);border-right:1.500px solid var(--paper-400);font:800 11.500px/1 var(--fm);letter-spacing:.09em;color:var(--ink-2)}
.strip .k.sun{background:var(--sun-200);border-color:var(--sun-700);color:var(--ink)}
.strip .v{white-space:nowrap;display:flex;align-items:center;gap:6px;padding:0 12px;font:700 21px/1 var(--fd);font-variant-numeric:tabular-nums}
.strip .v.hearts{gap:3px;padding:0 10px}
.strip .from{display:grid;place-items:center;width:22px;height:22px;border-radius:50%;background:var(--sky-500);box-shadow:0 0 0 2px var(--outline)}
.sticker{display:inline-flex;align-items:center;gap:5px;height:32px;padding:0 13px;border-radius:99px;background:var(--tangerine-500);font:700 17px/1 var(--fd);color:var(--ink);transform:rotate(-4deg);box-shadow:inset 0 2px 0 var(--tangerine-200),inset 0 -3px 0 var(--tangerine-700),0 0 0 2.5px var(--outline),0 0 0 5.500px var(--paper-0),0 4.500px 0 5.500px rgba(18,48,31,.26)}
.sticker.sun{background:var(--sun-500);box-shadow:inset 0 2px 0 var(--sun-200),inset 0 -3px 0 var(--sun-700),0 0 0 2.5px var(--outline),0 0 0 5.500px var(--paper-0),0 4.500px 0 5.500px rgba(18,48,31,.26);transform:rotate(3deg)}
.sticker small{font:800 11.500px/1 var(--fm);letter-spacing:.08em}
.panel{border-radius:20px;background:var(--paper-400);padding-bottom:6px;color:var(--ink);box-shadow:0 0 0 2.5px var(--outline),0 7.500px 0 0 rgba(29,37,69,.18),inset 0 -1.500px 0 var(--paper-500);min-width:0}
.panel>.face{border-radius:20px 20px 15px 15px;background:var(--paper-100);box-shadow:inset 0 2px 0 #fff;overflow:hidden}
.panel .hd{display:flex;align-items:center;justify-content:space-between;gap:10px;padding:12px 18px;background:var(--paper-200);border-bottom:1.500px dashed var(--paper-500)}
.panel .bd{padding:16px 18px 18px;display:flex;flex-direction:column;gap:13px}
.row{display:flex;gap:12px;align-items:center;font:700 15px/1.35 var(--ft)}
.row small{display:block;font:600 13px/1.3 var(--ft);color:var(--ink-2)}
.row .grow{flex:1;min-width:0}
.itile{flex:none;width:36px;height:36px;border-radius:11px;display:grid;place-items:center;background:var(--sky-100);box-shadow:inset 0 0 0 1.500px var(--sky-300);color:var(--sky-900)}
.keyline{border:0;border-top:1.500px solid var(--paper-300);margin:0}
.pass{width:330px;max-width:100%;border-radius:26px;background:var(--paper-400);padding-bottom:8px;color:var(--ink);box-shadow:0 0 0 2.5px var(--outline),0 10px 0 0 rgba(10,14,36,.3),inset 0 -1.500px 0 var(--paper-500)}
.pass>.face{border-radius:26px 26px 20px 20px;background:var(--paper-100);overflow:hidden;box-shadow:inset 0 2px 0 rgba(255,255,255,.7)}
.pass .band{padding:16px 22px 15px;background:var(--sky-300);border-bottom:1.500px solid var(--sky-500);display:flex;flex-direction:column;gap:6px}
.pass .band.cherry{background:var(--cherry-200);border-color:var(--cherry-500)}
.pass .band .eyebrow{color:var(--ink);opacity:.75}
.pass .ttl{font:700 38px/1 var(--fd)}
.pass .body{padding:14px 22px 16px;font:600 16px/1.45 var(--ft);color:var(--ink-2);display:flex;flex-direction:column;gap:12px;align-items:flex-start}
.perf{position:relative;height:0;border-top:2px dashed var(--paper-500);margin:0 18px}
.perf::before,.perf::after{content:"";position:absolute;top:-8px;width:14px;height:14px;border-radius:50%;background:var(--paper-400);box-shadow:inset 0 2.500px 0 var(--paper-500)}
.perf::before{left:-25px}.perf::after{right:-25px}
.stats{display:grid;grid-template-columns:1fr 1fr}
.stat{padding:10px 22px;display:flex;flex-direction:column;gap:3px;border-bottom:1.500px solid var(--paper-300)}
.stat:nth-child(odd){border-right:1.500px solid var(--paper-300)}
.stat span{font:800 11px/1 var(--fm);letter-spacing:.1em;color:var(--ink-2)}.stat b{font:700 22px/1 var(--fd);font-variant-numeric:tabular-nums}
.pass .acts{padding:16px 22px 18px;display:grid;gap:13px}
.stamp{display:inline-block;padding:5px 9px;border:2px solid var(--cherry-700);border-radius:8px;outline:1.500px solid var(--cherry-700);outline-offset:2px;color:var(--cherry-900);font:800 12px/1 var(--fm);letter-spacing:.1em;transform:rotate(-4deg);margin:4px 2px}
.pass.wide{width:610px;display:block}
.pass.wide>.face{display:grid;grid-template-columns:1.05fr 1fr}
.pass.wide .left{display:flex;flex-direction:column;border-right:2px dashed var(--paper-500)}
.pass.wide .band{flex:none}.pass.wide .body{flex:1}
.pass.wide .stat{padding:9px 18px}.pass.wide .acts{padding:14px 18px 16px}
.card{display:block;width:216px;max-width:100%;border:0;margin:0;text-align:left;font:inherit;border-radius:20px;background:var(--paper-400);padding:0 0 6px;color:var(--ink);cursor:pointer;box-shadow:0 0 0 2.5px var(--outline),0 7.500px 0 0 rgba(29,37,69,.2),inset 0 -1.500px 0 var(--paper-500)}
.card>.face{display:block;border-radius:20px 20px 15px 15px;background:var(--paper-100);overflow:hidden}
.card .art{position:relative;display:block;height:118px;border-bottom:2.5px solid var(--outline);background:var(--grass-500)}
.card .art>svg{width:100%;height:100%;display:block}
.card .info{display:flex;flex-direction:column;gap:7px;padding:11px 14px 13px}
.card .nm{font:600 20px/1.1 var(--fd)}
.card .meta{display:flex;flex-wrap:wrap;gap:6px}
.card .best{position:absolute;right:8px;top:8px}
.card.locked .art>svg{filter:grayscale(.85) brightness(1.12) contrast(.8);opacity:.6}
.card .lock{position:absolute;inset:0;display:grid;place-items:center}
.card .lock i{display:grid;place-items:center;width:40px;height:40px;border-radius:50%;background:var(--paper-0);box-shadow:0 0 0 2.5px var(--outline),inset 0 -3px 0 var(--paper-300)}
.coach{position:relative;max-width:250px;background:var(--outline);color:#fffaf0;border-radius:14px;padding:11px 14px 12px;font:700 14.500px/1.4 var(--ft);box-shadow:0 5.500px 0 rgba(18,48,31,.3);display:flex;gap:10px;align-items:center}
.coach::after{content:"";position:absolute;left:28px;bottom:-9px;width:18px;height:18px;background:var(--outline);border-radius:4px;transform:rotate(45deg)}
.coach .dots{display:flex;gap:4px;margin-top:6px}.coach .dots i{width:6px;height:6px;border-radius:50%;background:#fffaf0;opacity:.35}.coach .dots i.on{opacity:1;background:var(--sun-500)}
/* type specimens */
.types{display:grid;gap:14px;width:100%}
.types>div{display:grid;grid-template-columns:minmax(0,150px) minmax(0,1fr);gap:16px;align-items:baseline;border-top:1.500px solid var(--rule);padding-top:12px}
.types .cap{line-height:1.5}
/* old look, for comparison only */
.o-btn{white-space:nowrap;display:inline-flex;align-items:center;gap:8px;min-height:52px;padding:0 24px 3px;border:4px solid #1d2545;border-radius:14px;background:#ffcb3d;color:#1d2545;font:600 18px/20px var(--fd);box-shadow:inset 0 -5px 0 #e39a12,0 5px 0 #1d2545}
.o-chip{white-space:nowrap;display:inline-flex;flex-direction:column;justify-content:center;min-height:56px;padding:0 16px 3px;border:4px solid #1d2545;border-radius:14px;background:#fffaf0;box-shadow:inset 0 -4px 0 #e2eefa,0 5px 0 #1d2545;font:700 26px/1 var(--fd)}
.o-chip small{font:800 12px/1.3 var(--fm);letter-spacing:.08em;color:#4f5a7c}
.o-panel{padding:20px;border:4px solid #1d2545;border-radius:22px;background:#fffaf0;box-shadow:0 8px 0 #1d2545;font:500 14px/1.5 var(--fm);color:#4f5a7c;max-width:190px}
.o-panel b{display:block;font:600 19px/1.2 var(--fd);color:#1d2545;margin-bottom:6px}
/* tables */
.tbl{overflow-x:auto;border-radius:16px;box-shadow:inset 0 0 0 1.500px var(--rule);background:var(--paper-0)}
table{border-collapse:collapse;width:100%;min-width:640px;font:600 14.500px/1.45 var(--ft)}
th{font:800 11.500px/1.2 var(--fm);letter-spacing:.09em;text-transform:uppercase;color:var(--ink-2);text-align:left;padding:12px 16px}
td{padding:11px 16px;border-top:1.500px solid var(--rule);vertical-align:top}td:first-child{font-weight:800;white-space:nowrap}td:nth-child(2){color:var(--ink-2)}
.calls{display:grid;grid-template-columns:repeat(auto-fit,minmax(min(100%,460px),1fr));gap:16px;margin:0;padding:0;list-style:none}
.calls li{padding:18px 20px;border-radius:18px;background:var(--paper-0);box-shadow:inset 0 0 0 1.500px var(--rule);display:flex;flex-direction:column;gap:6px}
.calls b{font:600 18px/1.2 var(--fd)}.calls span{font:600 14.500px/1.5 var(--ft);color:var(--ink-2)}
.legend{margin:0;padding:0;list-style:none;display:grid;gap:8px;font:600 14.500px/1.45 var(--ft);color:var(--ink-2)}
.legend li{display:grid;grid-template-columns:24px 1fr;gap:8px}.legend i{font:800 12px/22px var(--fm);font-style:normal;width:22px;height:22px;border-radius:50%;background:var(--outline);color:#fffaf0;text-align:center}
.legend b{color:var(--ink)}
@media (prefers-reduced-motion:reduce){.b,.tg::after{transition:none}}
`;

const farmAnatomy = (() => {
  const s = SPR.farmhouse;
  const dot = (x, y, k) => `<g transform="translate(${x},${y})"><circle r="5.500" style="fill:var(--outline);stroke:#fffaf0;stroke-width:1.200"/><text y="0.400" text-anchor="middle" dominant-baseline="central" style="font:800 7px var(--fm);fill:#fffaf0">${k}</text></g>`;
  return `<svg viewBox="-70 -56 150 116" style="width:100%;max-width:520px;height:auto;border-radius:16px" role="img" aria-label="Farmhouse sprite with numbered callouts"><rect x="-80" y="-60" width="170" height="130" style="fill:var(--grass-500)"/><g style="fill:var(--shadow);opacity:var(--shadow-o)"><use href="#h-farmhouse"/></g><use href="#s-farmhouse"/>${dot(-30, -26, 1)}${dot(-30, 2, 2)}${dot(-46, 22, 3)}${dot(52, 36, 4)}${dot(2, -12, 5)}${dot(46, -38, 6)}</svg>`;
})();

const lightDiagram = `<svg viewBox="0 0 420 170" style="width:100%;max-width:560px;height:auto;border-radius:16px" role="img" aria-label="Three blocks of rising height, each with a taller south wall and a longer cast shadow"><rect width="420" height="170" style="fill:var(--grass-500)"/>
<g transform="translate(38,34)"><circle r="15" style="fill:var(--sun-500);stroke:var(--outline);stroke-width:2.500"/><path d="M0 -24v-7M0 24v7M-24 0h-7M24 0h7M-17 -17l-5 -5M17 17l5 5M17 -17l5 -5M-17 17l-5 5" style="stroke:var(--outline);stroke-width:2.500"/><path d="M22 22l34 30" style="stroke:var(--outline);stroke-width:2;stroke-dasharray:1 6"/></g>
${[[120, 6, 'S', 'cow, car, bale'], [220, 12, 'M', 'house, barn, hangar'], [326, 22, 'L', 'tree, tower, silo']].map(([x, h, t, ex]) => `<g transform="translate(${x},92)"><polygon points="24,${-22 - h} ${24 + h * 0.8},${-22 - h * 0.1} ${24 + h * 0.8},${18 + h * 0.9} ${-24 + h * 0.8},${18 + h * 0.9} -24,18" style="fill:var(--shadow);opacity:var(--shadow-o)"/><rect x="-24" y="${18 - h - 4}" width="48" height="${h + 4}" style="fill:var(--wall-100);stroke:var(--outline);stroke-width:2.500"/><rect x="-24" y="${-22 - h}" width="48" height="40" rx="3" style="fill:var(--roof-500);stroke:var(--outline);stroke-width:2.500"/><path d="M-22.700 ${-2 - h}V${-20.700 - h}h45.400" style="fill:none;stroke:var(--roof-300);stroke-width:2.500"/><text y="58" text-anchor="middle" style="font:800 12px var(--fm);fill:var(--ink)">${t} · wall ${h}u</text><text y="71" text-anchor="middle" style="font:700 9.500px var(--ft);fill:var(--ink)" opacity="0.8">${ex}</text></g>`).join('')}</svg>`;

const edges = `<svg viewBox="0 0 420 150" style="width:100%;max-width:560px;height:auto;border-radius:16px" role="img" aria-label="Three ground transitions: runway to lawn, water to lawn, road to lawn"><rect width="420" height="150" style="fill:var(--grass-500)"/>
<g><rect x="-10" y="-10" width="150" height="108" style="fill:var(--grass-700)"/><rect x="-10" y="-10" width="150" height="96" style="fill:var(--concrete-500);stroke:var(--concrete-700);stroke-width:1.600"/><rect x="-10" y="-10" width="150" height="84" style="fill:var(--asphalt-500);stroke:var(--outline);stroke-width:3"/><path d="M0 66h140" style="stroke:var(--marking);stroke-width:1.600"/><circle cx="40" cy="80" r="2.500" style="fill:var(--beacon);stroke:var(--concrete-700);stroke-width:1"/><circle cx="98" cy="80" r="2.500" style="fill:var(--beacon);stroke:var(--concrete-700);stroke-width:1"/></g>
<g transform="translate(140,0)"><rect width="140" height="150" style="fill:var(--grass-500)"/><path d="M-10 96q40 16 80 4t80 6V-10h-160z" style="fill:var(--sand-300);stroke:var(--grass-700);stroke-width:12"/><path d="M-10 96q40 16 80 4t80 6V-10h-160z" style="fill:var(--sand-300)"/><path d="M-10 86q40 16 80 4t80 6V-10h-160z" style="fill:var(--water-300);stroke:var(--water-700);stroke-width:1.600"/><path d="M-10 62q40 16 80 4t80 6V-10h-160z" style="fill:var(--water-500)"/><path d="M30 40q10 -6 22 0M84 52q10 -6 22 0" style="fill:none;stroke:var(--foam);stroke-width:2"/><use href="#reed" transform="translate(34,104)"/><use href="#reed" transform="translate(104,106)"/></g>
<g transform="translate(280,0)"><rect width="140" height="150" style="fill:var(--grass-500)"/><path d="M-10 40h160" style="stroke:var(--grass-700);stroke-width:92"/><path d="M-10 40h160" style="stroke:var(--road-700);stroke-width:68"/><path d="M-10 40h160" style="stroke:var(--road-500);stroke-width:62"/><path d="M0 40h140" style="stroke:var(--foam);stroke-width:2.400;stroke-dasharray:14 14;stroke-linecap:butt"/><use href="#tuft" transform="translate(40,112)"/><use href="#tuft-l" transform="translate(96,120)"/></g>
<path d="M140 0v150M280 0v150" style="stroke:var(--page);stroke-width:4"/>
<g style="font:800 10.500px var(--fm);fill:var(--ink)"><text x="8" y="140">SHOULDER + FRINGE</text><text x="148" y="140">SHALLOWS + SAND</text><text x="288" y="140">KERB + VERGE</text></g></svg>`;

const html = `<title>Final Approach v2 Proposal</title>
<meta name="claude:viewport-fit-sides" content="cover">
<link rel="stylesheet" href="https://fonts.googleapis.com/css2?family=DynaPuff:wght@400..700&family=Inconsolata:wght@500..900&family=Nunito:wght@500..900&display=swap">
<style>${css}</style>
<svg width="0" height="0" style="position:absolute" aria-hidden="true"><defs>${iconDefs}${W.defs()}<g id="scene-ground">${sc.groundLayer}</g><g id="scene-air">${sc.airLayer}</g></defs></svg>
<main class="wrap">

<section>
  <div class="head">
    <span class="eyebrow">Design system v2 · proposal for approval</span>
    <h1>Same toys, finer craft</h1>
    <p class="lead">The aircraft stay exactly as they are. Everything around them is redrawn to one set of rules about light, line and material, and the UI is rebuilt from thinner lines and moulded shapes. Nothing in the repo has changed yet.</p>
  </div>
  <div class="dev">${sceneSvg('0 0 1920 1080')}<div class="ui">${hud}</div></div>
  <p class="note">Meadow Field in the proposed style, at the game's real proportions (1920 by 1080 world units). The five aircraft, the flight path, the selection ring and the path colours are the current ones, untouched.</p>
</section>

<section>
  <div class="head">
    <span class="eyebrow">Diagnosis</span>
    <h2>Why the current system reads as primitive</h2>
    <p class="lead">It is not a lack of assets. Four rules in the current system produce the look, and each has a direct replacement.</p>
  </div>
  <div class="grid g3">
    <div class="cmp"><div class="pair">${oldTile('tree', 'Now', 64, 64)}${tile('tree-a', 'Proposed')}</div><p><b>Shapes are built from primitives.</b> The tree is five circles, paler than the lawn it stands on. Proposed: a lobed canopy in three tones, a trunk, and a shadow that plants it on the ground.</p></div>
    <div class="cmp"><div class="pair">${oldTile('farmhouse', 'Now', 84, 72, 1.5)}${tile('farmhouse', 'Proposed', 1.5)}</div><p><b>Strict top-down hides what things are.</b> A house seen from straight above is a roof diagram. Proposed: standing things lean slightly, so the south wall, door and windows show.</p></div>
    <div class="cmp"><div class="pair">${oldTile('tower', 'Now', 56, 56)}${tile('tower', 'Proposed', 1.4)}</div><p><b>One light, nowhere.</b> Nothing on the ground has a lit side, a shaded side or a shadow, so nothing has volume. Proposed: one sun, top-left, for every object.</p></div>
    <div class="cmp"><div class="pair">${oldTile('cow', 'Now', 36, 60)}${tile('cow', 'Proposed', 1.9)}</div><p><b>Every inner line is navy.</b> Details drawn in the outline colour turn into noise at small sizes. Proposed: navy for the silhouette only, inner lines in a darker tone of the fill.</p></div>
    <div class="cmp"><div class="ui-pair"><span class="tag">Now</span><span class="o-btn">${ic('play')}Take off</span><span class="tag">Proposed</span>${btn('Take off', '', 'play')}</div><p><b>The outline does the work of depth.</b> A 4dp outline plus a 5dp navy lip makes a quarter of the button dark edge. Proposed: a 2.5dp outline, and a lip in the button's own colour.</p></div>
    <div class="cmp"><div class="ui-pair fieldbg"><span class="tag">Now</span><span class="o-chip"><small>SCORE</small>12 450</span><span class="tag">Proposed</span><div class="strip"><span class="k sun">${ic('star', 'xs')}SCORE</span><span class="v">12 450</span></div></div><p><b>Everything looks pressable.</b> Readouts carry the same lip as buttons, and at 56dp they are heavy on the field. Proposed: readouts are flat 40dp strips; only controls get a lip.</p></div>
  </div>
</section>

<section>
  <div class="head">
    <span class="eyebrow">World · principles</span>
    <h2>Nine rules for everything on the ground</h2>
    <p class="lead">Each rule is checkable on a single sprite. Together they replace "flat and bright" and the four-fill budget.</p>
  </div>
  <ol class="rules">
    <li><i>W1</i><b>One sun, top-left</b><span>Every object is lit from the top-left: a light tone on the north-west side, a shade tone on the south-east, a hard shadow cast down-right.</span></li>
    <li><i>W2</i><b>Three tones per material</b><span>Light, base and shade, as flat cel shapes. Never a gradient. A sprite uses at most three materials.</span></li>
    <li><i>W3</i><b>Navy outside, tinted inside</b><span>The silhouette is <code>outline</code>. Every line inside it is the material's darkest tone, so detail adds form without adding noise.</span></li>
    <li><i>W4</i><b>Line weight is rank</b><span>Aircraft 4, landing zones 3, scenery 2.5, inner detail 1.25. The eye goes to the thickest line first, and that is always an aircraft.</span></li>
    <li><i>W5</i><b>Standing things show a south wall</b><span>Anything with height leans a little towards the camera: roof on top, wall below. Three height tiers set the wall and the shadow length.</span></li>
    <li><i>W6</i><b>Flat ground has no outline</b><span>Roads, fields, water and aprons are edged in their own darker tone. Only things with height, and landing zones, earn a navy line.</span></li>
    <li><i>W7</i><b>Edges are transitions</b><span>Two ground materials never meet on a bare line. A runway has a shoulder and rough grass, water has shallows and sand, a road has a verge.</span></li>
    <li><i>W8</i><b>Detail clusters, open ground rests</b><span>Tufts, flowers and props gather around objects and edges. The mown airfield and the approach corridors stay calm.</span></li>
    <li><i>W9</i><b>Aircraft own the top of the range</b><span>The brightest white and the most saturated colours belong to aircraft and paths. Scenery whites are cream, scenery colours are a step muted.</span></li>
  </ol>
  <div class="grid g2">
    <div class="spec"><span class="cap">W1 and W5 · light and height tiers</span>${lightDiagram}</div>
    <div class="spec"><span class="cap">W6 and W7 · ground transitions</span>${edges}</div>
  </div>
  <div class="grid g2" style="align-items:center">
    <div class="spec"><span class="cap">The rules on one sprite</span>${farmAnatomy}</div>
    <ol class="legend">
      <li><i>1</i><span><b>Lit slope</b> in <code>roof-300</code>: the side facing the sun (W1).</span></li>
      <li><i>2</i><span><b>Shaded slope</b> in <code>roof-500</code>, tile joints in <code>roof-700</code>: three tones, no gradient (W2).</span></li>
      <li><i>3</i><span><b>South wall</b>, 16 units tall: height tier M (W5). Door, shutters and a flower box live here.</span></li>
      <li><i>4</i><span><b>Cast shadow</b>: hard, down-right, as long as the wall is tall (W1).</span></li>
      <li><i>5</i><span><b>Ridge and joints</b> in the roof's own dark tones, never navy (W3).</span></li>
      <li><i>6</i><span><b>Silhouette</b> in <code>outline</code> at 2.5 units, thinner than any aircraft (W4).</span></li>
    </ol>
  </div>
</section>

<section>
  <div class="head">
    <span class="eyebrow">World · kit</span>
    <h2>Scenery, redrawn</h2>
    <p class="lead">Shown twice their world size. Every sprite follows W1 to W9; trees and pines come in sizes, cars in muted colours.</p>
  </div>
  <div class="stage field start">
    ${tile('tree-a', 'Tree')}${tile('tree-c', 'Old oak')}${tile('pine-a', 'Pine')}${tile('bush', 'Bush')}${tile('rock', 'Rock')}${tile('haybale', 'Hay bale')}${tile('cow', 'Cow')}${tile('car', 'Car')}${tile('fueltruck', 'Fuel truck')}${tile('windsock', 'Windsock')}${tile('silo', 'Silo')}
  </div>
  <div class="stage field start">
    ${tile('farmhouse', 'Farmhouse', 1.7)}${tile('barn', 'Barn', 1.7)}${tile('tower', 'Control tower', 1.7)}${tile('hangar', 'Hangar', 1.7)}${tile('terminal', 'Terminal', 1.7)}
  </div>
  <div class="sub">
    <h3>Landing zones and ground, in place</h3>
    <div class="grid g2">
      <div class="spec"><div class="crop">${sceneSvg('500 535 500 281', '', false)}</div><span class="note"><b>Runway 09.</b> Shoulder and rough-grass fringe, threshold bars, aiming blocks, touchdown marks, tyre rubber, edge lights, green threshold lights and a four-box approach indicator. The zone still reads first: it is the only ground shape with a navy line.</span></div>
      <div class="spec"><div class="crop">${sceneSvg('1010 240 400 225', '', false)}</div><span class="note"><b>Helipad and taxiway.</b> A concrete pad with slab joints and corner lights, the yellow ring and H as today. Taxiways are a lighter asphalt with a yellow centre line, so they never look like a place to land.</span></div>
      <div class="spec"><div class="crop">${sceneSvg('560 225 470 264', '', false)}</div><span class="note"><b>Apron.</b> Tower, terminal and hangar stand on their south walls. Parked aircraft keep the current sprite in grey. Slab joints and stand marks are tinted, not navy.</span></div>
      <div class="spec"><div class="crop">${sceneSvg('1500 420 400 225', '', false)}</div><span class="note"><b>Countryside.</b> Unmown meadow with patches, a pond with shallows, sand, reeds and ducks, and woods whose trees overlap north to south. Mowing stripes exist only inside the airfield.</span></div>
    </div>
  </div>
  <div class="sub">
    <h3>Night, from the same sprites</h3>
    <p class="note">Night redefines the tones and nothing else. Windows switch to <code>window</code> light, shadows deepen, and aircraft and UI stay bright as they do today.</p>
    <div class="dev night">${sceneSvg('0 0 1920 1080', 'night')}<div class="ui">${hud}</div></div>
  </div>
</section>

<section>
  <div class="head">
    <span class="eyebrow">Foundations</span>
    <h2>Tokens that make the rules possible</h2>
    <p class="lead">Single colours become short ramps, one outline weight becomes five ranks, and depth gets a scale of its own.</p>
  </div>
  <div class="sub"><h3>Material ramps</h3>
    <div class="stage paper start"><div class="ramps" style="width:100%">
      ${ramp('grass', [300, 400, 500, 600, 700, 900])}${ramp('leaf', [300, 500, 700, 900])}${ramp('pine', [300, 500, 700, 900])}${ramp('wood', [300, 500, 700, 900])}
      ${ramp('roof', [300, 500, 700, 900])}${ramp('wall', [100, 300, 500])}${ramp('tin', [100, 300, 500, 700, 900])}${ramp('stone', [300, 500, 700])}
      ${ramp('concrete', [300, 500, 700])}${ramp('asphalt', [300, 500, 700, 900])}${ramp('water', [300, 500, 700])}${ramp('straw', [300, 500, 700])}
      ${ramp('sun', [200, 500, 700, 900])}${ramp('sky', [100, 300, 500, 700, 900])}${ramp('cherry', [200, 500, 700, 900])}${ramp('mint', [200, 500, 700, 900])}
      ${ramp('tangerine', [200, 500, 700, 900])}${ramp('paper', [0, 100, 200, 300, 400, 500])}
    </div></div>
    <p class="note">In each ramp, 500 is today's colour where one exists. 300 is the lit tone, 700 the shade or lip, 900 the tinted inner line. <code>paper</code> replaces the three <code>surface</code> tokens.</p>
  </div>
  <div class="grid g2">
    <div class="sub"><h3>Line ranks</h3><div class="stage paper"><ul class="ranks">
      <li><i style="height:4px"></i><span><b>line-hero · 4</b><br>Aircraft only.</span></li>
      <li><i style="height:3px"></i><span><b>line-zone · 3</b><br>Runways, helipads, clouds.</span></li>
      <li><i style="height:2.5px"></i><span><b>line-ui · 2.5</b><br>Controls, panels, HUD strips, scenery silhouettes.</span></li>
      <li><i style="height:2px"></i><span><b>line-ui-sm · 2</b><br>Small parts: a selected segment, a heart, a chimney.</span></li>
      <li><i style="height:1.5px;background:var(--roof-700)"></i><span><b>line-detail · 1.25 to 1.5</b><br>Inner lines and keylines, always tinted, never navy.</span></li>
    </ul></div></div>
    <div class="sub"><h3>Type</h3><div class="stage paper"><div class="types">
      <div><span class="cap">Display · DynaPuff</span><span style="font:700 34px/1 var(--fd)">Cleared to land</span></div>
      <div><span class="cap">Title · DynaPuff</span><span style="font:600 20px/1.2 var(--fd)">Meadow Field</span></div>
      <div><span class="cap">Body · Nunito, new</span><span style="font:600 16px/1.5 var(--ft)">Planes land on runways. Helicopters land on helipads.</span></div>
      <div><span class="cap">Readout · Inconsolata</span><span style="font:800 15px/1.3 var(--fm);letter-spacing:.08em">NEXT UP · FA 204 · RWY 27</span></div>
    </div></div></div>
  </div>
</section>

<section>
  <div class="head">
    <span class="eyebrow">UI · principles</span>
    <h2>Seven rules for controls and panels</h2>
  </div>
  <ol class="rules">
    <li><i>U1</i><b>Moulded, not boxed</b><span>A control is a face, a highlight along its top edge and a lip in its own 700 tone, wrapped by one thin outline.</span></li>
    <li><i>U2</i><b>A lip means pressable</b><span>Buttons, cards and toggles have a lip and sink when pressed. Readouts, chips and tags are flat. Nothing else may look like a button.</span></li>
    <li><i>U3</i><b>Thin line, thick form</b><span>The outline is 2.5dp everywhere in the UI. Depth comes from the lip and a flat contact shadow, not from a heavier edge.</span></li>
    <li><i>U4</i><b>Tinted inside</b><span>Dividers, keylines and chip edges take a darker tone of their surface. Navy is for the outer edge and for text.</span></li>
    <li><i>U5</i><b>Nested radii</b><span>An inner corner is the outer radius minus the gap between them, so shapes sit inside each other without pinched corners.</span></li>
    <li><i>U6</i><b>Tower paperwork</b><span>Surfaces borrow from the control tower: HUD readouts are flight strips, dialogs are boarding passes with a perforated stub, verdicts are rubber stamps.</span></li>
    <li><i>U7</i><b>Disabled is flat, not faded</b><span>A disabled control loses its colour, lip and shine and keeps full opacity, so it stays legible on grass and sky.</span></li>
  </ol>
</section>

<section>
  <div class="head">
    <span class="eyebrow">UI · kit</span>
    <h2>Components, rebuilt</h2>
    <p class="lead">All live HTML. Press the buttons, flip the toggles.</p>
  </div>
  <div class="grid g2">
    <div class="sub"><h3>Button</h3><div class="stage sky col">
      <div style="display:flex;flex-wrap:wrap;gap:16px;justify-content:center">${btn('Take off', '', 'play')}${btn('Choose airport', 'sky')}${btn('Settings', 'ghost', 'settings')}${btn('Quit flight', 'cherry')}</div>
      <div style="display:flex;flex-wrap:wrap;gap:18px;justify-content:center;align-items:center">${btn('Retry', 'sm', 'restart')}${btn('Resume', '')}${btn('Play', 'lg', 'play')}</div>
      <div style="display:flex;flex-wrap:wrap;gap:20px;justify-content:center;align-items:flex-start">${btn('Pressed', 'is-pressed')}${btn('Focused', 'is-focus')}${btn('Disabled', '', '', 'disabled')}</div>
    </div></div>
    <div class="sub"><h3>Icon button, toggle, segmented control</h3><div class="stage sky col">
      <div style="display:flex;flex-wrap:wrap;gap:14px;justify-content:center">${['pause', 'sound-on', 'restart', 'home', 'close'].map(i => `<button class="b ghost icon" aria-label="${i}"><span>${ic(i)}</span></button>`).join('')}<button class="b icon" aria-label="play"><span>${ic('play')}</span></button></div>
      <div class="panel" style="width:300px;max-width:100%"><div class="face"><div class="bd">
        <div class="row"><span class="grow">Sound effects<small>Engines, dings and boings</small></span><button class="tg" id="tg-sfx" role="switch" aria-checked="true" aria-label="Sound effects">${ic('check')}</button></div><hr class="keyline">
        <div class="row"><span class="grow">Music<small>Light and upbeat</small></span><button class="tg" id="tg-music" role="switch" aria-checked="false" aria-label="Music">${ic('check')}</button></div><hr class="keyline">
        <div class="row"><span class="grow" style="color:var(--ink-3)">Callsign tags<small style="color:var(--ink-3)">Unlocks at 5 000 points</small></span><button class="tg" id="tg-tags" role="switch" aria-checked="false" aria-label="Callsign tags" disabled>${ic('check')}</button></div>
      </div></div></div>
      <div class="seg" role="group" aria-label="Difficulty"><button id="sg-1" aria-pressed="false">Cadet</button><button id="sg-2" aria-pressed="true">Controller</button><button id="sg-3" aria-pressed="false">Chief</button></div>
    </div></div>
  </div>
  <div class="grid g2">
    <div class="sub"><h3>HUD: flight strips and stickers</h3><div class="stage field col">
      <div style="display:flex;flex-wrap:wrap;gap:14px;justify-content:center;align-items:center"><div class="strip"><span class="k sun">${ic('star', 'xs')}SCORE</span><span class="v">12 450</span></div><div class="strip"><span class="k">BEST</span><span class="v">18 200</span></div><div class="strip"><span class="v hearts">${heart(1)}${heart(1)}${heart(0)}</span></div></div>
      <div style="display:flex;flex-wrap:wrap;gap:22px;justify-content:center;align-items:center"><div class="strip"><span class="k">NEXT</span><span class="v"><svg width="30" height="30" viewBox="-52 -52 104 104" aria-label="Jet"><use href="#ac-jet" transform="rotate(90)"/></svg><span class="from">${ic('arrow-left', 'xs')}</span></span></div><div class="sticker">${ic('bolt', 'xs')}x3</div><div class="sticker sun">+250 <small>STREAK X3</small></div><button class="b ghost icon hudbtn" aria-label="Pause"><span>${ic('pause')}</span></button></div>
      <div class="coach">${ic('finger', 'lg')}<span>Drag from the aircraft to the runway end.<span class="dots"><i></i><i class="on"></i><i></i></span></span></div>
    </div><p class="note">Readouts are flat strips with a labelled cell: 40dp instead of 56dp, so more field stays visible. Stickers (streak, score popup) are the only tilted shapes and carry a paper rim.</p></div>
    <div class="sub"><h3>Panel and chips</h3><div class="stage sky col">
      <div class="panel" style="width:330px;max-width:100%"><div class="face"><div class="hd"><h3>How to play</h3><span class="chip plain">3 steps</span></div><div class="bd">
        <div class="row"><span class="itile">${ic('tap')}</span><span>Tap an aircraft to select it.</span></div>
        <div class="row"><span class="itile">${ic('plane')}</span><span>Drag to a runway. Planes need to line up with it.</span></div>
        <div class="row"><span class="itile">${ic('helicopter')}</span><span>Helicopters land on the H.</span></div>
      </div></div></div>
      <div style="display:flex;flex-wrap:wrap;gap:8px;justify-content:center"><span class="chip">1 runway</span><span class="chip">1 helipad</span><span class="chip sun">${ic('star', 'xs')}Best 18 200</span><span class="chip mint">${ic('check', 'xs')}Landed</span><span class="chip cherry">${ic('warning', 'xs')}Too close</span><span class="chip plain">${ic('lock', 'xs')}Locked</span></div>
    </div></div>
  </div>
  <div class="grid g2">
    <div class="sub"><h3>Dialog: the boarding pass</h3><div class="stage field" style="background:#3d6a45">
      <div class="pass"><div class="face"><div class="band"><span class="eyebrow">Tower · holding</span><span class="ttl">Paused</span></div>
        <div class="body">Three aircraft are holding. They will wait for you.</div><div class="perf"></div>
        <div class="acts">${btn('Resume', 'block', 'play')}${btn('Restart', 'ghost block', 'restart')}${btn('Main menu', 'ghost block', 'home')}</div></div></div>
    </div></div>
    <div class="sub"><h3>Level card</h3><div class="stage sky">
      <button class="card" id="card-meadow"><span class="face"><span class="art">${sceneSvg('330 190 1330 700', '', false)}<span class="best"><span class="chip sun">${ic('star', 'xs')}18 200</span></span></span><span class="info"><span class="nm">Meadow Field</span><span class="meta"><span class="chip">1 runway</span><span class="chip">1 helipad</span></span></span></span></button>
      <button class="card locked" id="card-harbour" aria-disabled="true"><span class="face"><span class="art">${sceneSvg('1150 640 760 400', '', false)}<span class="lock"><i>${ic('lock')}</i></span></span><span class="info"><span class="nm" style="color:var(--ink-2)">Harbour Cross</span><span class="meta"><span class="chip plain">Land 50 to unlock</span></span></span></span></button>
    </div><p class="note">The miniature is the level itself, cropped, so a card always matches its airfield.</p></div>
  </div>
</section>

<section>
  <div class="head">
    <span class="eyebrow">Screens</span>
    <h2>Together on a phone</h2>
  </div>
  <div class="dev">${sceneSvg('0 0 1920 1080')}<div class="ui"><div class="scrim">
    <div class="pass wide"><div class="face"><div class="left"><div class="band cherry"><span class="eyebrow">Tower log · runway 27</span><span class="ttl">Game over</span></div>
      <div class="body">Two aircraft got too close over runway 27.<span class="stamp">${'TOO CLOSE'}</span></div></div>
      <div><div class="stats"><div class="stat"><span>SCORE</span><b>12 450</b></div><div class="stat"><span>LANDED</span><b>37</b></div><div class="stat"><span>BEST STREAK</span><b>x6</b></div><div class="stat"><span>BEST</span><b>18 200</b></div></div>
      <div class="acts">${btn('Try again', 'block', 'restart')}${btn('Choose airport', 'ghost block')}</div></div></div></div>
  </div></div></div>
  <p class="note">The landscape game-over dialog at 800 by 450dp. The pass tears along a vertical perforation: what happened on the left, the numbers and the next step on the stub.</p>
</section>

<section>
  <div class="head">
    <span class="eyebrow">Scope</span>
    <h2>What changes, what stays</h2>
  </div>
  <div class="tbl"><table>
    <thead><tr><th>Area</th><th>Now</th><th>Proposed</th></tr></thead>
    <tbody>
      <tr><td>Principles</td><td>5 principles; "outline everything", "flat and bright"</td><td>Toy scale, silhouette before colour and the clear field stay. The other two become W1 to W9 and U1 to U7.</td></tr>
      <tr><td>Colour</td><td>71 tokens, mostly single values</td><td>About 130: every material and accent is a ramp of 3 to 6 tones, with Day, Night and Dawn values.</td></tr>
      <tr><td>Outline</td><td><code>line-3</code> 4dp on nearly everything</td><td>Five ranks; 4dp is reserved for aircraft.</td></tr>
      <tr><td>Scenery</td><td>Strict top-down, 4 fills, navy details, no shadow</td><td>South walls, 3 tones per material, tinted details, cast shadow by height tier. All 32 sprites redrawn.</td></tr>
      <tr><td>Ground</td><td>Lawn with 64px stripes, navy-edged shapes</td><td>Mown airfield, unmown countryside, transitions on every edge, no navy on flat ground.</td></tr>
      <tr><td>Landing zones</td><td>Slab with threshold bars, patch and number</td><td>Same footprint and world size; shoulder, fringe, markings, lights added.</td></tr>
      <tr><td>Depth in UI</td><td>Navy lip under everything</td><td>Lip in the fill's 700 tone on controls only, plus a flat contact shadow.</td></tr>
      <tr><td>HUD</td><td>56dp chips with a lip</td><td>40dp flat flight strips; pause is the only button.</td></tr>
      <tr><td>Type</td><td>DynaPuff, Inconsolata for all text</td><td>DynaPuff, Nunito for body text, Inconsolata for readouts, labels and callsigns.</td></tr>
      <tr><td>Unchanged</td><td colspan="2" style="color:var(--ink)">Aircraft sprites, liveries and path colours, the flight path and selection ring, the navy outline colour, sun as the signature, the icon set, motion and easing tokens, layer order, touch sizes, voice and copy.</td></tr>
    </tbody>
  </table></div>
  <div class="sub"><h3>Four calls that are yours to overrule</h3>
  <ul class="calls">
    <li><b>Leaning top-down for standing scenery</b><span>Buildings, trees and the tower show a south wall and never rotate. Things that move or turn (aircraft, vehicles, animals, boats) stay strictly top-down. It is the biggest single gain in recognisability, and the biggest break with today's rule.</span></li>
    <li><b>Nunito replaces Inconsolata for body text</b><span>Monospace paragraphs are a large part of the coarse feel. Inconsolata keeps the tower voice where it earns it: labels, readouts, callsigns.</span></li>
    <li><b>Readouts lose their lip</b><span>Score, lives and next-up become flat strips. The HUD gets lighter, and "a lip means pressable" becomes a rule players can trust.</span></li>
    <li><b>No navy outline on flat ground</b><span>Roads, fields and ponds are edged in their own tone. This is what lets runways and aircraft stand out against a much richer field.</span></li>
  </ul></div>
  <div class="sub"><h3>After approval</h3>
  <p>The design system comes first, as one OpenSpec change: <code>tokens.json</code> with the ramps and ranks, the brand book and world rules rewritten around W1 to W9 and U1 to U7, every scenery SVG redrawn, the component guidelines and <code>showcase.html</code> regenerated, and the published design system updated. Porting it to the Compose theme and sprites is a second change. Island and Savanna get the same treatment as Meadow Field here.</p></div>
</section>

</main>
<script>
(function(){
  var fit=function(){document.querySelectorAll('.dev').forEach(function(d){var u=d.querySelector('.ui');if(u)u.style.setProperty('--s',d.clientWidth/800);});};
  fit();addEventListener('resize',fit);if(window.ResizeObserver){new ResizeObserver(fit).observe(document.body);}
  document.querySelectorAll('.tg:not(:disabled)').forEach(function(t){t.addEventListener('click',function(){t.setAttribute('aria-checked',t.getAttribute('aria-checked')==='true'?'false':'true');});});
  document.querySelectorAll('.seg').forEach(function(s){s.addEventListener('click',function(e){var b=e.target.closest('button');if(!b)return;s.querySelectorAll('button').forEach(function(x){x.setAttribute('aria-pressed',x===b?'true':'false');});});});
})();
</script>
`;
const out = path.join(__dirname, 'final-approach-v2.html');
fs.writeFileSync(out, html);
if (process.argv.includes('--preview')) fs.writeFileSync(path.join(__dirname, 'preview.html'), `<!doctype html><html><head><meta charset="utf-8"><meta name="viewport" content="width=device-width,initial-scale=1"></head><body>${html}</body></html>`);
console.log('written', path.basename(out), Math.round(html.length / 1024) + 'KB');
