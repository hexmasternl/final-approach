## MODIFIED Requirements

### Requirement: Dialog appears when the game has ended
The game screen SHALL show the game ended dialog as a modal when the game has ended because two aircraft collided or because an aircraft left the viewport without landing. After a collision it SHALL appear once the collision sequence has finished; after an aircraft left the viewport it SHALL appear at once. It SHALL NOT be shown while the game is running.

#### Scenario: Collision
- **WHEN** two aircraft collide
- **THEN** the game ended dialog is shown when the collision sequence has finished, and not before

#### Scenario: Aircraft not landed
- **WHEN** an aircraft leaves the viewport without having landed
- **THEN** the game ended dialog is shown at once

#### Scenario: Game running
- **WHEN** the game is running and no failure has happened
- **THEN** no game ended dialog is shown
