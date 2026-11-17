## Context

`tokens.json` marks 25 tokens `deprecated`, each naming a replacement (for example `line-2` to `line-ui-sm`, `radius-md` to `radius-control`). The generator emits them with a "v1" KDoc. Readers found by search: `FlightPathDrawing` (`line2`), `ProximityRing` (`line2`, `line4`), `TutorialDrawing` (`line1`, `line3`, `line4`, `radiusMd`), `GameScreen` (`line3`), `SplashScene` and `SplashPage` (`surface`, `line2`, `line3`, `radiusMd`). `FaShadows` is read only for `floatOffsetX/Y`, which are not deprecated.

## Decisions

- **The replacement comes from the token's `deprecated` field.** Rejected: picking the closest v2 value by eye. The field is the agreed mapping and keeps review simple.
- **Where a replacement differs in value from v1, the design review decides.** For example the proximity ring at `line4` (6dp) has no v2 rank of that width. If the nearest rank does not look the same, add a v2 token to the design system first (CLAUDE.md: no one-offs), then use it. Rejected: a literal in the composable.
- **Splash banner fill** uses the `paper` ramp step named by the deprecated `surface` token, not a literal.
- **`FaShadows` keeps only `floatOffsetX/Y`**, which are current. Rejected: moving them to `FaDimens`, which is churn with no gain; call sites stay unchanged.
- **Order: readers first, then tokens**, so every commit builds. The generator's "is a v1 token" guards and the `dep` KDoc helper go with the tokens.
- **Delete `migrate-tokens-v2.cjs`**: it only adds and deprecates v1 tokens and has no use afterwards.
- Engine and domain are untouched.

## Assumptions

- "Looks the same" is judged by the design review, not pixel equality; a small width difference from a nearest-rank mapping is acceptable if the reviewer agrees.
- The v1 toggle sizes are deprecated tokens and are removed with the rest; `FaToggle` already reads the V2 ones.

## Risks

- A removed name still used somewhere unsearched (previews, tests, androidApp): the build catches it; search all of `app/src`.
