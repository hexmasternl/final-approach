# Tasks

## 1. Zones and configuration

- [x] 1.1 Add `LandingConfig` (helipad zone radius with per-helipad override, runway zone depth and width, alignment tolerance in radians, rollout fraction, fade duration) and a rollout speed fraction on the aircraft type config; verify no such number appears as a literal in engine code
- [x] 1.2 Derive landing zones from the level config (helipad circle; one runway zone for one-way, two with opposite directions for two-way); verify with unit tests for zone count, positions and directions

## 2. Landing rules

- [x] 2.1 Implement `LandingRules` for a path segment, zone and aircraft kind (helicopter vs helipad, airplane vs runway); verify with unit tests: path through the pad zone lands, a path that misses does not, wrong aircraft type does nothing, a fast segment that skips over a zone still lands
- [x] 2.2 Add direction alignment for runway zones; verify with unit tests: aligned lands, opposite direction on a one-way runway does not, steep angle does not, both directions land at their own end on a two-way runway, mid-runway crossing does nothing

## 3. Engine behaviour

- [ ] 3.1 Truncate and lock the path at the zone entry and set `ClearedToLand`; verify with unit tests that the path ends at the zone and later points are ignored
- [ ] 3.2 Land helicopters at the end of the path and remove them; verify with a unit test
- [ ] 3.3 Implement airplane rollout (two thirds of the runway length in the zone's direction), fade and removal; verify with unit tests for rollout length, direction per zone, and removal after the fade
- [ ] 3.4 Include rolling airplanes in collision detection; verify with a unit test that two airplanes landing from opposite ends of a two-way runway collide mid-runway

## 4. Feedback

- [ ] 4.1 Draw the white path flash from the locked-path state using the `fuselage` colour and `dur-pop` tokens; verify in a preview or on a device that the path flashes and stops
- [ ] 4.2 Draw the steady variant when reduced motion is on; verify with animations disabled that nothing flashes and the state change is visible

## 5. Docs

- [ ] 5.1 Update the landing rules in `docs/game-design.md`, `docs/architecture.md` and the core rules in `CLAUDE.md` (and `.github/copilot-instructions.md`) so a landing is triggered by a path entering a zone, with direction and rollout; add the flash to the FlightPath guideline; verify the docs no longer say a landing needs the path to end on the zone

## 6. Integration check

- [ ] 6.1 Human check on a device: land a helicopter and airplanes on one-way and two-way runways, confirm the flash, the stop, the rollout and fade, and a mid-runway collision
