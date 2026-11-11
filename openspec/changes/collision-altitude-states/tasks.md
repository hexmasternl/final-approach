# Tasks

## 1. Altitude state

- [x] 1.1 Add `AltitudeState` (`InFlight`, `Landed`) in `domain` and a state field on the aircraft, defaulting to `InFlight`; verify with a `commonTest` unit test that a newly spawned aircraft is In Flight
- [x] 1.2 Set `Landed` in the engine when the landing is registered at the touchdown point of the runway (and when a helicopter lands on its pad); verify with unit tests that the state is In Flight while the aircraft approaches or is merely cleared to land, Landed after touchdown, and still Landed during rollout

## 2. Collision filter

- [x] 2.1 Make `Collisions` skip pairs with different altitude states before the broad phase; verify with unit tests that two In Flight and two Landed overlapping aircraft collide, and that an In Flight aircraft overlapping a Landed one does not
- [x] 2.2 Verify with an engine-level test that an In Flight aircraft crossing a rolling-out airplane does not end the game, and that two airplanes landing from opposite ends of a two-way runway still collide mid-runway

## 3. Docs

- [x] 3.1 Update the failure rules in `docs/game-design.md` with the altitude states and the equal-state rule; verify the text matches the specs of this change
