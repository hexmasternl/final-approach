## Context

`FlightPathDrawing.kt` draws the casing (`FaDimens.pathCasing`, 14dp) and dash (`pathStroke`, 8dp) with `pathDash` 18dp, `pathGap` 16dp, an end dot `pathDot` 22dp with a `line3` outline, and a march phase of one dash-plus-gap period per `FaMotion.durMarchMs` (900). The values come from the design system tokens (`tokens.json`, `components/FlightPath.md`).

## Decisions

### 1. Change the tokens, not the drawing code

The design system rules say to add or change a token first. `path-stroke` 3px, `path-casing` 7px (stroke plus a `line-1`, 2px, border on each side), `path-dash` 10px, `path-gap` 8px, `path-dot` 12px and `dur-march` 1800ms are set in `tokens.json`, in `components/FlightPath.md` and in the showcase, then regenerated into `FaDimens` and `FaMotion`. `FlightPathDrawing.kt` already reads them; only the end dot outline moves from `line3` to `line2`.

Rejected: scaling the drawn stroke by a factor in the composable (a literal in disguise).

### 2. Border width is the thinnest line token

A 2px border on each side is `line-1`, the thinnest allowed stroke of the brand book (no hairlines). Thinner would break the "outline everything" principle.

Rejected: a 1px border (hairline, forbidden).

### 3. Half speed means double the period

`dur-march` is the time of one dash-and-gap period; doubling it halves the animation speed. Because the period itself is shorter now (18px instead of 34px), the marching distance per second is lower still; the author asked for the animation speed to halve, so the duration is what is halved.

Rejected: keeping the on-screen march speed in px per second at half the old value (would give about 950ms; harder to verify against the issue wording).

### Assumptions (author can overrule)

- Exact sizes (3, 7, 10, 8) are my choice for "thin and fine"; they keep the old dash-to-gap ratio roughly and the old ratio of border to stroke is tightened.
- The end dot shrinks with the line; the issue does not mention it.
- The pick area, the straight-on projection and the cleared-to-land flash keep their look and logic.

### Open questions (non-blocking)

- Are the sizes thin enough on a phone? They are tokens, so tuning is cheap.
