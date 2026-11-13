## ADDED Requirements

### Requirement: The tutorial runs until it is completed
When Meadow Field starts and the tutorial has not been completed on this device, the level SHALL start with the tutorial. When it has been completed, Meadow Field SHALL start as the normal level. Completion SHALL survive restarting the app. Leaving or restarting the level before the tutorial is completed SHALL NOT count as completing it.

#### Scenario: First start
- **WHEN** a player who has never completed the tutorial starts Meadow Field
- **THEN** the tutorial's first step is shown and no aircraft is in the air

#### Scenario: Completed earlier
- **WHEN** a player who completed the tutorial starts Meadow Field, also after the app was closed and opened again
- **THEN** the normal level starts and no tutorial step is shown

#### Scenario: Quit mid-tutorial
- **WHEN** the player quits the level during the tutorial and starts Meadow Field again
- **THEN** the tutorial starts again from its first step

#### Scenario: Restart from the options panel
- **WHEN** the player chooses Restart in the options panel during the tutorial
- **THEN** the tutorial starts again from its first step

### Requirement: Steps in a fixed order
The tutorial SHALL run these steps in this order: (1) goal, (2) runway, (3) light prop, (4) turboprop, (5) helipad, (6) helicopter. Each step SHALL show one coach mark with its step number out of six.

#### Scenario: Order
- **WHEN** the player goes through the tutorial without failing or skipping
- **THEN** the steps appear as goal, runway, light prop, turboprop, helipad, helicopter, numbered 1 to 6 of 6

### Requirement: The normal spawner is off during the tutorial
While the tutorial runs, the normal spawner SHALL NOT spawn any aircraft. Only the tutorial SHALL send in aircraft: one light prop, one turboprop and one helicopter, each from an entry in the tutorial script that leaves room for an easy approach to runway 27 or to the helipad.

#### Scenario: No random traffic
- **WHEN** the tutorial is running
- **THEN** no aircraft appears other than the light prop, the turboprop and the helicopter the tutorial sends in

#### Scenario: Easy approaches
- **WHEN** the tutorial script is checked
- **THEN** from each airplane's entry there is a straight approach into runway 27's landing zone within the alignment tolerance that stays inside the viewport, and the helicopter's entry has a clear line to the helipad

### Requirement: Explanation steps hold the game until Got it
The goal, runway, helipad steps, and the explanation parts of the turboprop and helicopter steps, SHALL hold the simulation: nothing moves, spawns or scores while they are shown. Each SHALL end only when the player taps its Got it button.

#### Scenario: Goal before any aircraft
- **WHEN** the tutorial starts
- **THEN** the goal step explains that every aircraft must be guided to a safe landing and kept from getting too close to others, and no aircraft appears until the player taps Got it

#### Scenario: Runway explained
- **WHEN** the player taps Got it on the goal step
- **THEN** the runway step highlights runway 27, shows the landing end and the direction to come in from, says the approach has to be lined up with the runway, and says some runways take landings from one end only and others from both ends

#### Scenario: Held while explaining
- **WHEN** an explanation step is shown
- **THEN** aircraft positions and the score do not change until the player taps Got it

### Requirement: Light prop step teaches the drag
After the runway step, the tutorial SHALL send in the light prop, tracked by a coach mark saying it needs to land. Touch SHALL be off until the light prop is well in view, meaning its centre is at least the configured inset inside every edge of the viewport. Then the simulation SHALL hold, the coach mark SHALL say to drag a path from the plane to the runway, the drag demo SHALL play, and touch SHALL come back on. The step SHALL end when the player's path clears the light prop to land.

#### Scenario: Touch off while it flies in
- **WHEN** the light prop is flying in and is not yet well in view
- **THEN** taps and drags do not select it or draw a path

#### Scenario: Held with the drag demo
- **WHEN** the light prop becomes well in view
- **THEN** the simulation holds, the coach mark tells the player to drag a path from the plane to the runway, the drag demo plays from the plane to runway 27, and the player can select the plane and draw its path

#### Scenario: Path that does not land
- **WHEN** the player draws a path for the light prop that does not clear it to land
- **THEN** the step stays, the simulation stays held, and the player can draw again

#### Scenario: Path that lands
- **WHEN** the player draws a path that clears the light prop to land
- **THEN** the simulation resumes and the turboprop step starts

### Requirement: Turboprop step shows a faster airplane
As soon as the light prop is cleared to land, the tutorial SHALL send in the turboprop, tracked by a coach mark saying it needs to land too. Touch SHALL stay on. When the turboprop is well in view, the simulation SHALL hold and the coach mark SHALL explain that this plane needs to land as well, that there are four types of airplane each with its own speed, and that this one is faster than the light prop so it catches up with slower traffic. The explanation SHALL end with Got it.

#### Scenario: Turboprop sent in
- **WHEN** the light prop is cleared to land
- **THEN** a turboprop enters, with a coach mark following it

#### Scenario: Turboprop explained
- **WHEN** the turboprop becomes well in view
- **THEN** the simulation holds and the coach mark explains the four airplane types, their own speeds, and that the turboprop catches up with the light prop

### Requirement: Helipad step follows the routed turboprop
The helipad step SHALL start once the turboprop has been cleared to land after its explanation. It SHALL highlight the helipad and explain that helicopters land there, and only there, and SHALL end with Got it.

#### Scenario: Waits for the turboprop's path
- **WHEN** the player has tapped Got it on the turboprop explanation and has not yet cleared the turboprop to land
- **THEN** the helipad step does not start and the simulation runs

#### Scenario: Helipad explained
- **WHEN** the turboprop is cleared to land
- **THEN** the simulation holds, the helipad is highlighted, and the coach mark says helicopters land there and only there

### Requirement: Helicopter step and completion
After the helipad step, the tutorial SHALL send in the helicopter, tracked by a coach mark. When the helicopter is well in view, the simulation SHALL hold and the coach mark SHALL explain that it needs to land on the helipad. Tapping Got it on this explanation SHALL complete the tutorial, without waiting for any aircraft to land, and SHALL store the completion on the device.

#### Scenario: Helicopter explained
- **WHEN** the helicopter becomes well in view
- **THEN** the simulation holds and the coach mark says it needs to land on the helipad

#### Scenario: Completed on Got it
- **WHEN** the player taps Got it on the helicopter explanation
- **THEN** the tutorial is completed and stored, even though tutorial aircraft are still in the air

### Requirement: The level continues after the tutorial
When the tutorial is completed or skipped, Meadow Field SHALL continue as a normal level: aircraft already in the air stay with their paths, the normal spawner starts, and the difficulty curve and first spawn delay count from that moment. Landings of tutorial aircraft SHALL score like any other landing. From then on, collisions and aircraft leaving the area SHALL end the level as usual.

#### Scenario: Hand-over
- **WHEN** the tutorial is completed
- **THEN** the tutorial aircraft keep flying their paths, no coach mark is shown, and the spawner sends its first aircraft after the level's first spawn delay, starting in the first difficulty phase

#### Scenario: Tutorial landings score
- **WHEN** a tutorial aircraft lands
- **THEN** its points are added to the score as for any landing

#### Scenario: Failure after the tutorial
- **WHEN** two aircraft touch after the tutorial is completed
- **THEN** the level ends as usual

### Requirement: Failure restarts the current step
During the tutorial, a collision or an aircraft leaving the area SHALL NOT end the level. Instead the game SHALL return to the state it had when the current step began, run that step again, and its coach mark SHALL carry a short line saying what happened, without blame.

#### Scenario: Turboprop leaves the area
- **WHEN** the turboprop leaves the area during the turboprop step
- **THEN** no game-ended dialog is shown, the aircraft and score are as they were when the turboprop step began, the turboprop enters again, and the coach mark says an aircraft left the area

#### Scenario: Collision
- **WHEN** two aircraft touch during the helicopter step
- **THEN** the aircraft and score are as they were when the helicopter step began, the helicopter enters again, and the coach mark says two aircraft got too close

### Requirement: The player can skip the tutorial
While the tutorial runs, a Skip tutorial control SHALL be available. Using it SHALL end the tutorial at once, store it as completed, and continue as a normal level.

#### Scenario: Skip before any aircraft
- **WHEN** the player taps Skip tutorial on the goal step
- **THEN** the coach mark disappears, the tutorial is stored as completed, and the normal level starts

#### Scenario: Skip with aircraft in the air
- **WHEN** the player taps Skip tutorial while tutorial aircraft are in the air
- **THEN** those aircraft stay, the simulation is no longer held, and the normal spawner starts

#### Scenario: Skip is remembered
- **WHEN** the player skipped the tutorial and starts Meadow Field again
- **THEN** the normal level starts without the tutorial

### Requirement: The options panel works during the tutorial
Opening the options panel during the tutorial SHALL pause the game as usual. Continue SHALL return to the same tutorial step, still held if it was held.

#### Scenario: Continue during an explanation
- **WHEN** the player opens the options panel during an explanation step and taps Continue
- **THEN** the same explanation is shown again and the simulation is still held
