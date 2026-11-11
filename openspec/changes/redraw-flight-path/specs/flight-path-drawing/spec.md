# Spec Delta

## Purpose

Defines how the player selects an aircraft and clears, draws or redraws its flight path.

## ADDED Requirements

### Requirement: Tap removes the path
The system SHALL remove an aircraft's path when the player taps the aircraft, and the aircraft SHALL keep its current heading.

#### Scenario: Cancel path
- **WHEN** an aircraft has a path and the player taps it without dragging
- **THEN** the path disappears and the aircraft flies straight on in its current heading

### Requirement: Touch hides the old path
The system SHALL hide an aircraft's previous path as soon as the player touches the aircraft.

#### Scenario: Touch starts redraw
- **WHEN** the player touches an aircraft that has a path
- **THEN** the old path is no longer shown

### Requirement: Drag draws a replacement path
The system SHALL let the player tap and drag an aircraft to give it a new path that replaces any previous one.

#### Scenario: Redraw directly
- **WHEN** the player touches an aircraft with a path and drags
- **THEN** the aircraft follows the newly drawn path only
