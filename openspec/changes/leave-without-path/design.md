# Design

## Context

`GameEngine.hasLeft` counts an aircraft as left when `hasEnteredViewport` is true and its position is outside `playArea` (the world viewport, 0,0 to width,height, expanded by `WorldConfig.leaveMargin`). `failureOf` runs after every sub-step, after `AircraftMotion.step` has moved the aircraft, and ends the level with `GameOverReason.LeftArea`.

`PathFollower` drops each point the aircraft reaches; with no points left the path is empty and the aircraft flies straight on. So "has a path" is exactly `!aircraft.path.isEmpty` after the step.

"The level" in the engine is the world: `WorldConfig.viewport` is the world rectangle, and `WorldViewport` fits the whole world on screen and centres it. On a screen wider than 16:9 there are bands beside the world that are on screen but outside it, and drawn points are not clamped to the world, so a player can draw a path out past the leave margin and back. That is the main way an aircraft ends up outside while following a path.

## Goals / Non-Goals

**Goals:** only unguided aircraft are lost by leaving; a path that ends outside loses the level at that moment; tests for every row of the rule table.

**Non-Goals:** edge markers for off-screen aircraft, path clipping, input changes, changing the margin.

## Decisions

### The leave check adds one condition: no path
`hasLeft` becomes: entered the viewport, outside `playArea`, and `aircraft.path.isEmpty`. Because `failureOf` already runs after the motion of each sub-step, a path that ends outside is caught in the sub-step in which its last point is consumed, and a path removed by a tap is caught in the next sub-step. No new state, timer or event is needed.

- Rejected: a separate "was outside when the path ended" flag on `Aircraft`. It duplicates what the empty path already says and would need clearing when a new path is drawn.
- Rejected: a grace period after the path ends. The issue asks for the loss at the moment the path ends.

### Status does not matter, only the path
A cleared-to-land aircraft has a locked path ending in a zone inside the world, a landing helicopter and a rolling-out airplane stand inside the world, and landed aircraft are removed. The single "no path" condition therefore covers every status; no status-specific branch is added.

### Same edge and margin as today
The check keeps using `playArea`, derived from `level.world` (width, height, `leaveMargin`). It follows any world zoom and adds no new tunable.

### Paths outside the level are drawn as they are
The renderer already draws every path point it gets; parts outside the screen are simply not visible. No clipping or styling change is made.

- Rejected: clamping drawn points to the world. That would stop the deliberate wide routes the issue wants to allow.

### No edge marker
An aircraft beyond the screen while on a path is not marked. With the leave margin at 150 world units and path points drawn on screen, such stretches are short.

- Rejected for this change: an edge marker. It is new UI that needs a design-system component first; it can be its own issue.

## Assumptions

- "The level" means the world bounds, which are what the engine calls the viewport today, with the same leave margin. (Assumption.)
- Removing a path by tapping an aircraft that is outside counts as its path ending: the level ends at the next step. (Assumption.)
- An aircraft whose path ends outside loses the level even if its last heading points back into the level. (From the issue's rule table.)
- The game-ended copy "An aircraft left without landing." stays; it is still accurate. (Assumption.)
- No edge marker and no path clipping in this change. (Assumption.)

## Risks / Trade-offs

- An aircraft that has never entered the level can still be given a path that keeps it outside, and then never counts as lost. This is today's behaviour and is left as is.
- Tests must place aircraft with explicit paths outside `playArea`; `GameEngine.place` already allows this.
