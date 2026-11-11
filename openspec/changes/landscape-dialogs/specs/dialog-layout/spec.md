# Spec Delta

## Purpose

Defines how the game's modal dialogs are laid out for the shape of the screen, so that their content and buttons are always reachable without scrolling on common phones in both orientations.

## ADDED Requirements

### Requirement: Game ended dialog fits a landscape phone
On a common phone in landscape, the game ended dialog SHALL show its title, its reason, every per-type well, Total landed, Points, Try again and Main page at once, without scrolling, with the ad banner shown under it. Common phones are 360×640dp and 412×915dp, with the system bars shown and the default font size.

#### Scenario: Small phone in landscape
- **WHEN** a level that spawns all five aircraft types ends on a 640×360dp landscape screen with system bars and a banner ad under the dialog
- **THEN** the title, reason, five type wells, Total landed, Points, Try again and Main page are all fully visible and the dialog does not scroll

#### Scenario: Large phone in landscape
- **WHEN** a level that spawns all five aircraft types ends on a 915×412dp landscape screen with system bars and a banner ad under the dialog
- **THEN** all content and both buttons are fully visible and the dialog does not scroll

### Requirement: Options panel fits a landscape phone
On a common phone in landscape, the options panel SHALL show its title, its body, and Continue, Restart and Quit at once, without scrolling.

#### Scenario: Small phone in landscape
- **WHEN** the player opens the options panel on a 640×360dp landscape screen with system bars
- **THEN** the title, body, Continue, Restart and Quit are all fully visible and the panel does not scroll

#### Scenario: Large phone in landscape
- **WHEN** the player opens the options panel on a 915×412dp landscape screen with system bars
- **THEN** all content and the three buttons are fully visible and the panel does not scroll

### Requirement: Portrait keeps the design-system Dialog
In portrait, the game ended dialog and the options panel SHALL keep the layout of the design-system Dialog: at most 400dp wide, title in the large display style, body under it, stats in a two-column grid, and full-width buttons stacked with the primary first.

#### Scenario: Game ended dialog in portrait
- **WHEN** the game ends on a 360×640dp portrait screen
- **THEN** the dialog is laid out as the design-system Dialog, with stacked full-width Try again and Main page buttons

#### Scenario: Options panel in portrait
- **WHEN** the options panel opens on a 412×915dp portrait screen
- **THEN** the panel is laid out as the design-system Dialog, with stacked full-width Continue, Restart and Quit buttons

### Requirement: Landscape layout keeps the Dialog look
In the landscape layout, both dialogs SHALL keep the Dialog look: thick outline, extra-large radius, lip and soft shadow on the surface colour over the scrim, a DynaPuff title, Inconsolata body text, toy-block buttons with the primary action first, and the drop-in arrival.

#### Scenario: Look in landscape
- **WHEN** either dialog is shown in landscape
- **THEN** it has the outlined, rounded card with lip and soft shadow, a DynaPuff title, Inconsolata body, toy-block buttons in the order primary first, and it drops in when it opens

#### Scenario: Day and Night
- **WHEN** either dialog is shown in landscape in the Day theme and in the Night theme
- **THEN** in both themes the card and its buttons stay light and readable, as the design-system Dialog does

### Requirement: Landscape layout stays accessible
In the landscape layout, every button SHALL be at least 48dp tall, Inconsolata text SHALL NOT be lighter than weight 500, and the dialog SHALL appear without the drop-in animation when reduced motion is on.

#### Scenario: Button height
- **WHEN** either dialog is shown on a 640×360dp landscape screen
- **THEN** every button is at least 48dp tall

#### Scenario: Text weight
- **WHEN** either dialog is shown in landscape
- **THEN** no Inconsolata text in it is lighter than weight 500

#### Scenario: Reduced motion in landscape
- **WHEN** reduced motion is enabled on the device and either dialog opens in landscape
- **THEN** the dialog is in place at once, without the drop-in

### Requirement: Scrolling is a last resort
A dialog SHALL scroll only when its content cannot fit, such as on a screen smaller than the common phones or with a large font size. Its content SHALL then stay reachable by scrolling rather than being cut off.

#### Scenario: No scrolling on common phones
- **WHEN** either dialog is shown on a common phone in landscape with the default font size
- **THEN** the dialog cannot be scrolled

#### Scenario: Very large font size
- **WHEN** either dialog is shown in landscape on a 640×360dp screen with the largest system font size
- **THEN** every button can still be reached by scrolling the dialog

### Requirement: Compact Dialog variant is part of the design system
The design system SHALL define the landscape Dialog layout as a variant of the Dialog component, and show it in the showcase for both the paused and the game over example.

#### Scenario: Showcase shows the variant
- **WHEN** the Dialog section of the design system showcase is opened
- **THEN** it shows the paused and game over dialogs in the landscape variant next to the portrait ones, and the Dialog guideline says when the variant is used
