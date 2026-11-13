## REMOVED Requirements

### Requirement: All airplanes share one landing speed
**Reason**: The landing speed is no longer the reference type's full flight speed, and the light prop no longer keeps its flight speed during rollout.
**Migration**: Replaced by "All airplanes share one landing speed, scaled by a factor", which keeps the configurable reference type and adds a configurable factor that defaults to one half.

## ADDED Requirements

### Requirement: All airplanes share one landing speed, scaled by a factor
Every airplane SHALL roll out at one shared landing speed: the effective flight speed, in the current run, of a reference aircraft type, multiplied by a landing speed factor. Both the reference type and the factor SHALL come from configuration. The reference type SHALL default to the light prop and the factor SHALL default to one half. The rollout length SHALL NOT depend on the landing speed.

#### Scenario: Light prop rolls out at half its flight speed
- **WHEN** a light prop has been rolling out for more than the deceleration time, with the default configuration
- **THEN** it moves at half of the light prop's flight speed and keeps that speed until the rollout distance is covered

#### Scenario: Faster airplanes roll out at half the light prop's speed
- **WHEN** a turboprop, a jet or a jumbo has been rolling out for more than the deceleration time, with the default configuration
- **THEN** it moves at half of the light prop's flight speed and keeps that speed until the rollout distance is covered

#### Scenario: Reference type from configuration
- **WHEN** a level configures the turboprop as the landing speed reference
- **THEN** every airplane rolls out at half of the turboprop's flight speed once the deceleration time has passed

#### Scenario: Factor from configuration
- **WHEN** a level configures a landing speed factor of 1
- **THEN** every airplane rolls out at the reference type's full flight speed once the deceleration time has passed

#### Scenario: Rollout length unchanged
- **WHEN** an airplane touches down with the default configuration
- **THEN** it still rolls two thirds of the runway length before it fades out, and takes longer to do so than at the reference type's full flight speed

## MODIFIED Requirements

### Requirement: Speed changes to the landing speed over the deceleration time
At touchdown an airplane SHALL move at its flight speed, and its speed SHALL change steadily to the landing speed over the deceleration time, which comes from configuration and defaults to 1 second. This SHALL hold for every airplane type, the light prop included.

#### Scenario: Speed at touchdown
- **WHEN** a jet touches down
- **THEN** its speed at that moment is its flight speed

#### Scenario: Landing speed reached after 1 second
- **WHEN** a light prop, a turboprop, a jet or a jumbo has been rolling out for 1 second, with the default configuration
- **THEN** its speed equals half of the light prop's flight speed

#### Scenario: Rollout ends during the slowdown
- **WHEN** the rollout distance is covered before the deceleration time has passed
- **THEN** the airplane stops moving and fades out as usual
