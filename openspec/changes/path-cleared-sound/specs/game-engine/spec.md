## ADDED Requirements

### Requirement: The engine reports each landing clearance
The engine SHALL report a clearance event, naming the aircraft and the kind of zone it was cleared to, exactly once each time a path is cut off at a valid landing zone. The event SHALL appear in the events of the next update after the clearance, and in no later update. The engine SHALL NOT report a clearance for a path that does not enter a valid landing zone.

#### Scenario: One event per clearance
- **WHEN** a path is extended into a valid landing zone and the engine is then updated twice
- **THEN** the first update's events hold one clearance event for that aircraft and the second update's events hold none

#### Scenario: Points after the cut-off report nothing
- **WHEN** further points are added to a path that is already cut off at a landing zone
- **THEN** no further clearance event is reported

#### Scenario: Two aircraft cleared before one update
- **WHEN** two aircraft are cleared to land between two updates
- **THEN** the next update's events hold one clearance event for each of them

#### Scenario: Re-routed aircraft cleared again
- **WHEN** a cleared aircraft gets a new path that is cleared to land again
- **THEN** a second clearance event is reported for it

#### Scenario: No event without a valid zone
- **WHEN** a path ends in open air, enters a runway threshold zone outside the alignment tolerance, enters the non-landing end of a one-way runway, or enters a zone of the wrong type
- **THEN** no clearance event is reported
