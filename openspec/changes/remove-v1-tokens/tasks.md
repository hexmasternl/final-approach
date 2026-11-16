## 1. Move the readers

- [x] 1.1 `FlightPathDrawing`, `ProximityRing`, `GameScreen` and `SplashPage`: replace v1 line widths with the v2 ranks named in `tokens.json`. Verify: search finds no `FaDimens.line1` to `line4` or `lineFine` there; `:shared:allTests` passes.
- [x] 1.2 `TutorialDrawing` (highlight and drag demo): replace `line1`, `line3`, `line4` and `radiusMd`. Verify: search clean; preview unchanged.
- [x] 1.3 `SplashScene`: replace `surface`, `line2`, `line3`, `radiusMd`. Verify: search clean; splash preview in Day, Night and Dawn unchanged.
- [x] 1.4 Selection ring and any other reader the search of `app/src` (previews and tests included) turns up. Verify: a search for every deprecated name returns only theme files.
- [x] 1.5 Where a replacement does not keep the look, add the v2 token to the design system first, then use it. Verify: design review accepts Day, Night and Dawn.

## 2. Remove the tokens

- [ ] 2.1 Remove all `deprecated` tokens from `tokens.json`; remove the `dep` helper and the "is a v1 token" guards from `gen-kotlin.cjs`; adjust `check-tokens.cjs`; update the showcase if it lists them. Verify: `node docs/design-system/tools/check-tokens.cjs` passes.
- [ ] 2.2 Regenerate the theme; reduce `FaShadows` to the float offsets. Verify: no `v1` or `deprecated` in `ui/theme`; `./gradlew :shared:allTests :androidApp:assembleDebug` passes from `app/src`.
- [ ] 2.3 Delete `docs/design-system/tools/migrate-tokens-v2.cjs`. Verify: no reference left in `docs/`.

## 3. Docs and review

- [ ] 3.1 `CLAUDE.md` and `.github/copilot-instructions.md`: remove the "A few readers outside the world ..." sentence and the "No version 1 tokens in new code" rule, keeping both files consistent. Verify: neither mentions version 1 tokens or `deprecated` tokens.
- [ ] 3.2 Human: design review of Day, Night and Dawn (game screen, tutorial, splash, menu). Verify: the review names no violation.
