# sound-effects Specification

## Purpose
TBD - created by archiving change path-cleared-sound. Update Purpose after archive.

## Requirements

### Requirement: A success sound plays when a path is cleared to land
The game SHALL play the success sound once each time a drawn path enters a valid landing zone and is cut off: an airplane path entering a runway threshold zone within the alignment tolerance of that end's landing direction, or a helicopter path entering a helipad zone. The sound SHALL start at the moment the path is cut off and flashes white.

#### Scenario: Airplane cleared to a runway
- **WHEN** the player draws an airplane path that enters a runway threshold zone within the alignment tolerance
- **THEN** the success sound plays once as the path is cut off

#### Scenario: Helicopter cleared to a helipad
- **WHEN** the player draws a helicopter path that enters a helipad zone
- **THEN** the success sound plays once as the path is cut off

#### Scenario: Dragging on after the cut-off
- **WHEN** a path has been cut off at a landing zone and the player keeps dragging
- **THEN** no further success sound plays for that path

### Requirement: No success sound for a path that does not clear
The game SHALL NOT play the success sound for a path that does not end in a valid landing zone.

#### Scenario: Path ends in open air
- **WHEN** the player draws a path that enters no landing zone
- **THEN** no success sound plays

#### Scenario: Misaligned runway approach
- **WHEN** the player draws an airplane path that enters a runway threshold zone outside the alignment tolerance
- **THEN** no success sound plays

#### Scenario: Wrong end of a one-way runway
- **WHEN** the player draws an airplane path into the end of a one-way runway that is not its landing end
- **THEN** no success sound plays

#### Scenario: Wrong zone type
- **WHEN** the player draws an airplane path into a helipad zone, or a helicopter path into a runway threshold zone
- **THEN** no success sound plays

### Requirement: One sound per clearance
The game SHALL play the success sound once for every clearance, including clearances of different aircraft shortly after each other and a new clearance of an aircraft that was re-routed.

#### Scenario: Several aircraft cleared in quick succession
- **WHEN** two aircraft are cleared to land within a fraction of a second of each other
- **THEN** the success sound plays twice, once for each clearance, and the second does not cut off or replace the first

#### Scenario: Re-routed aircraft cleared again
- **WHEN** an aircraft that was cleared to land is given a new path that is cleared to land again
- **THEN** the success sound plays again for the new clearance

### Requirement: Sounds have a visual twin
Every sound effect SHALL accompany a visible cue, so the game is fully playable muted. The success sound's visual twin SHALL be the path's cut-off and white flash. Reduced motion SHALL NOT mute sound effects.

#### Scenario: Reduced motion keeps the sound
- **WHEN** reduced motion is on and a path is cleared to land
- **THEN** the path shows its steady reduced-motion state and the success sound still plays

#### Scenario: Playing with the device muted
- **WHEN** the device's media volume is off and a path is cleared to land
- **THEN** the path is still cut off and changes its look, so the clearance is visible without sound

### Requirement: The success sound is short and starts without delay
The success sound SHALL be a short mono MP3 with no audible silence at its start, so it lands with the flash.

#### Scenario: Sound lines up with the flash
- **WHEN** a path is cleared to land on a device
- **THEN** the sound is heard together with the white flash, with no noticeable gap before it
