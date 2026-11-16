# Spec Delta

## Purpose

Defines how the app's UI components look and respond, following version 2 of the design system, so every screen is built from the same moulded controls, flat readouts and paper surfaces.

## ADDED Requirements

### Requirement: Pressable controls are moulded
Every pressable control in the app (text buttons, icon buttons, level tiles, the toggle knob, the selected segment of a segmented control) SHALL be drawn as a face with a highlight along its top edge, a lip below it in a darker tone of the same colour and a single 2.5dp navy outline. While pressed, the face SHALL sit lower on its lip; on release it SHALL return.

#### Scenario: Primary button
- **WHEN** a screen shows a primary button
- **THEN** its lip is a darker yellow, not navy, and its outline is 2.5dp

#### Scenario: Press
- **WHEN** the player holds a finger on a button
- **THEN** the button's face moves down and its lip gets shorter until the finger lifts

### Requirement: Readouts are flat
Elements that only show information (score, best score, spawn counter, lives, next aircraft, tags on a level tile, coach marks) SHALL have no lip and SHALL NOT move when touched.

#### Scenario: HUD during play
- **WHEN** a level is being played
- **THEN** the pause button is the only HUD element with a lip, and every HUD readout is a flat strip 40dp high

### Requirement: Disabled controls stay legible
A disabled control SHALL be drawn in paper tones without a coloured face, at full opacity, and SHALL NOT respond to touch.

#### Scenario: Disabled button
- **WHEN** a disabled button is shown
- **THEN** it is opaque, colourless and does nothing when tapped

### Requirement: Dialogs are boarding passes
A dialog SHALL have a tinted header band holding a short uppercase eyebrow and the title, a body, a dashed perforation, and a stub that holds any stats and the actions. When the space inside the system bars is wider than tall and lower than the compact breakpoint, the perforation SHALL be vertical with the stub on the right.

#### Scenario: Pause dialog
- **WHEN** the game is paused
- **THEN** the pause dialog shows a sky-tinted header with its title, a perforation and the same actions as before this change

#### Scenario: Game-ended dialog
- **WHEN** a level ends
- **THEN** the dialog shows a header tinted for the outcome, the reason as text, the same stats and the same actions as before this change

#### Scenario: Landscape phone
- **WHEN** the game-ended dialog is shown on a 640 by 360dp landscape screen with system bars and a 50dp banner
- **THEN** the whole dialog is visible, with the stats and actions right of a vertical perforation

### Requirement: Nothing is lost
Porting a screen to the v2 components SHALL NOT add, remove or rename any control, label, statistic or behaviour of that screen.

#### Scenario: Screens before and after
- **WHEN** the main page, settings page, level select, HUD, pause dialog, game-ended dialog and tutorial overlays are compared with their state before this change
- **THEN** each has the same controls with the same labels, content descriptions and behaviour

### Requirement: Type in the app
Running text in the app SHALL be set in Nunito at weight 600 or heavier. Titles, button labels and scores SHALL stay in DynaPuff; uppercase labels, readouts and callsigns SHALL stay in Inconsolata.

#### Scenario: Dialog body
- **WHEN** a dialog is shown
- **THEN** its message is in Nunito and its title in DynaPuff

### Requirement: Touch targets and motion
Every control SHALL keep a touch target of at least 48dp whatever its drawn size, and press and toggle animations SHALL be skipped when the device asks for reduced motion.

#### Scenario: Small button
- **WHEN** a small button 40dp high is shown
- **THEN** a tap up to 4dp above or below it still activates it

#### Scenario: Reduced motion
- **WHEN** reduced motion is on and a toggle is switched
- **THEN** the knob changes position without an animation
