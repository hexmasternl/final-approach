# Design

## Context

See proposal.md for the why.

Today, in `engine/`:

- `Collisions.find` skips a pair when (1) the altitude states differ, (2) either aircraft has status `Landing` (a helicopter in its landing sequence), or (3) `Collisions.rollingTogether(a, b)` holds: both `RollingOut` on the same `runwayId` with directions within `SAME_DIRECTION_TOLERANCE`. A fading airplane is still `RollingOut`, so it is covered.
- `GameEngine.warned` filters out `Landing` aircraft and then warns every pair whose centres are closer than `level.world.warningDistance`. It applies rule 2 only, which is why rules 1 and 3 produce false warnings.
- The UI (`ProximityRings`, `drawProximityRings` in `ui/game`) draws rings purely from `GameState.warnedAircraft` and already fades a ring out when an id leaves the set.

So this is an engine-only change: narrow the set, and the rings follow.

## Goals / Non-Goals

**Goals:**

- One definition of "this pair can collide", used by both collision detection and the warning, so the two cannot drift apart.
- No change to which pairs collide.

**Non-Goals:**

- Predicting collisions from heading, path or closing speed.
- Any UI, theme or design-system work.

## Decisions

### 1. One shared pair rule in `Collisions`

Add `Collisions.canCollide(a: Aircraft, b: Aircraft): Boolean`, holding the three exclusions that `find` applies inline today (equal altitude state, neither in status `Landing`, not `rollingTogether`). `find` calls it instead of the three `continue` lines; `GameEngine.warned` calls it per pair before the distance test and drops its own `Landing` filter, which `canCollide` now covers.

Rejected: repeating the three conditions in `warned`. It works today, but the next collision exception (for example a new ground state) would then silently bring the false warnings back.

Rejected: deciding it in the UI by filtering `warnedAircraft` while drawing. That puts a game rule in a composable, against the architecture.

### 2. The rule is per pair, the result is per aircraft

`warnedAircraft` stays a `Set<Int>`: an aircraft is in it when at least one pair it belongs to is close and can collide. A third aircraft near a warned pair is only added when it forms such a pair itself.

Rejected: exposing warned pairs (`Set<Pair<Int, Int>>`). The ring is drawn per aircraft and nothing needs to know the partner; it would change `GameState` and the UI for no visible gain.

### 3. Same-direction tolerance is reused, not redefined

"Same direction" for the warning is exactly `rollingTogether`, with its existing `SAME_DIRECTION_TOLERANCE`. No new tunable value.

### 4. No design-system change

The ring keeps its tokens (`warning`, `warnRingDash`, `warnRingGap`, `warnRingMargin`) and its transition. The design system lacks nothing for this change.

## Assumptions

Each is a default chosen during triage; the issue author can overrule it.

- **The warning follows the collision rules exactly.** The issue names three cases; they are the same three the collision rules treat specially, so "can collide" is taken as the collision rules' own pair rule rather than a separate list.
- **A helicopter in its landing sequence gets no ring and causes none.** This is today's behaviour (`docs/game-design.md`) and follows from the same rule; the spec now states it.
- **Fading airplanes count as rolling out**, as they do for collisions.
- **An aircraft cleared to land but not yet touched down is In Flight.** It is warned against other In Flight aircraft and not against Landed ones, until its own touchdown.
- **No prediction.** Two In Flight aircraft flying away from each other are still warned while within the warning distance; "potential collision" means a pair the rules allow to collide, not a forecast.

## Risks / Trade-offs

- [Late warning on a head-on landing] An airplane on final towards a two-way runway on which another airplane rolls out towards it gets no ring until it touches down, because until then the pair cannot collide. From touchdown the pair is warned and can collide almost at once. → Accepted for this change: the issue asks for no ring between a landed and a flying aircraft without exception. An earlier warning for this case would need a new rule (a pair that will be able to collide) and is listed as an open question on the issue.
- [A ring disappears at touchdown] An In Flight pair that is warned loses its rings when the first of the two touches down. The existing ring transition makes this a fade, not a pop. → No action.
- [Existing tests assume distance only] `closeAircraftAreWarnedUntilTheySeparate` and `everyAircraftOfACloseGroupIsWarned` use In Flight jets, so they keep passing unchanged. → Verify, do not rewrite.
