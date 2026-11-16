## Why

The world is version 2 (#77), but the flight path end dot, proximity ring, tutorial highlight and drag demo, selection ring, splash banner and `FaShadows` still read version 1 tokens, so the deprecated tokens cannot go yet. Moving them and deleting the tokens ends the two-version period and the rule about v1 tokens in new code.

GitHub issue: #85

## What Changes

- Move the listed readers to the version 2 tokens named in each deprecated token's replacement.
- Remove every token marked `deprecated` from `tokens.json` and from the generated theme (`FaColors`, `FaDimens`, `FaMotion`, `FaShadows`, the Day, Night and Dawn palettes).
- Delete `docs/design-system/tools/migrate-tokens-v2.cjs`.
- Remove the deprecated-token handling from the generator and checker once nothing uses it.
- Update `CLAUDE.md` and `.github/copilot-instructions.md`: drop the "version 1 tokens" paragraph and rule.

## Capabilities

### New Capabilities

None.

### Modified Capabilities

- `design-system`: the "The app keeps building" requirement changes from "deprecated tokens stay" to "no deprecated tokens remain".

## Out of Scope

- Any visible change. Replacements are chosen to look the same in Day, Night and Dawn; where a v2 value differs from v1, design.md says how it is decided.
- New components, the full level card (#86), or other design-system additions.

## Impact

- `docs/design-system/tokens.json`, `tools/gen-kotlin.cjs`, `tools/check-tokens.cjs`, the showcase if it lists deprecated tokens.
- Generated `ui/theme/*` and the readers in `ui/game` (`FlightPathDrawing`, `ProximityRing`, `TutorialDrawing`, `GameScreen`) and `ui/splash` (`SplashScene`, `SplashPage`).
- Depends on: `world-v2-port` (archived).
