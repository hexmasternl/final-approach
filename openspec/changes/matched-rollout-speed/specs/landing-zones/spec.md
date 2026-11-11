# Spec Delta

## MODIFIED Requirements

### Requirement: Airplanes roll out after touchdown
After touchdown an airplane SHALL keep moving along the runway in its landing direction for two thirds of the runway length, then fade out and be removed. The rollout fraction and the fade duration SHALL come from configuration. The rollout speed SHALL follow the shared landing speed rules. In a two-way runway, the zone that was hit SHALL decide the rollout direction.

#### Scenario: Rollout distance
- **WHEN** an airplane touches down on a runway
- **THEN** it travels two thirds of the runway length along the landing direction

#### Scenario: Fade and removal
- **WHEN** the rollout distance is covered
- **THEN** the airplane fades out over the configured duration and is then removed

#### Scenario: Two-way rollout direction
- **WHEN** airplanes land on the two ends of a two-way runway
- **THEN** each rolls out in the landing direction of the zone it landed in

## ADDED Requirements

### Requirement: All airplanes share one landing speed
Every airplane SHALL roll out at one shared landing speed: the effective flight speed, in the current run, of a reference aircraft type that comes from configuration. The reference type SHALL default to the light prop.

#### Scenario: Light prop keeps its speed
- **WHEN** a light prop touches down with the default configuration
- **THEN** it rolls out at its own flight speed for the whole rollout

#### Scenario: Faster airplanes roll out at the light prop's speed
- **WHEN** a turboprop, a jet or a jumbo has been rolling out for more than the deceleration time, with the default configuration
- **THEN** it moves at the light prop's flight speed and keeps that speed until the rollout distance is covered

#### Scenario: Reference type from configuration
- **WHEN** a level configures the turboprop as the landing speed reference
- **THEN** every airplane rolls out at the turboprop's flight speed once the deceleration time has passed

### Requirement: Speed changes to the landing speed over the deceleration time
At touchdown an airplane SHALL move at its flight speed, and its speed SHALL change steadily to the landing speed over the deceleration time, which comes from configuration and defaults to 1 second.

#### Scenario: Speed at touchdown
- **WHEN** a jet touches down
- **THEN** its speed at that moment is its flight speed

#### Scenario: Landing speed reached after 1 second
- **WHEN** a turboprop, a jet or a jumbo has been rolling out for 1 second, with the default configuration
- **THEN** its speed equals the light prop's flight speed

#### Scenario: Rollout ends during the slowdown
- **WHEN** the rollout distance is covered before the deceleration time has passed
- **THEN** the airplane stops moving and fades out as usual
