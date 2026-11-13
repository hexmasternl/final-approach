## Context

The repository today has 1280 x 720 worlds (factor 1). `detailed-scenery` (not yet applied) plans factor 1.25 with `LevelDefaults.WORLD_ZOOM` and scales aircraft-sized values with it, so aircraft keep their on-screen size and speed. Issue #48 asks for factor 2 and issue #56 says that is too far and wants the middle between the size before and the size after.

## Decisions

### 1. One factor, 1.5

The two issues are one decision: the author's end state is the midpoint of factor 1 and factor 2, which is 1.5 (1920 x 1080). Both issues therefore share this change. The `WORLD_ZOOM` constant and the scaling table from `detailed-scenery` are reused; only its value and the dependent world size change.

Rejected: applying factor 2 and then a second correction (two steps for one result); a midpoint between 1.25 and 2 (1.625), because 1.25 is a plan in an unapplied change, not a size the author has seen.

### 2. Aircraft keep their on-screen size and motion

Issue #48 wants the airports "significantly smaller" and more space for planes to fly. Aircraft are not mentioned, so the scaling table of `detailed-scenery` stays: aircraft-sized values scale with the factor, airfield values do not. Zoom-1 aircraft speeds come from `aircraft-speed-tuning`.

Rejected: keeping aircraft at their world size (they would shrink to unreadable on a phone and fall below the 72dp pick area).

### 3. Layouts and the larger border

Each level keeps its layout centred, clear of the `HUD_BAND`. The border around the airfield grows with the factor; the backdrop fill (ground, water, savanna) must cover the whole world, and the scenery of `detailed-scenery` must be spread over the border or left plain there, not left as empty background.

Rejected: leaving the border empty (looks unfinished).

### Assumptions (author can overrule)

- "Previous size" is the 1280 x 720 world in the repository; "current size" is the doubled world from issue #48; the middle is 1.5.
- The change is applied after `detailed-scenery`; if that change is dropped, this one needs the `WORLD_ZOOM` constant added itself.
- Worlds stay 16:9.

### Open questions (non-blocking)

- Is 1.5 the intended value? It is one constant, so changing it is cheap.
- Should `detailed-scenery` be edited to 1.5 directly instead? This change keeps its text untouched.
