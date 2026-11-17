# Spec Delta

## ADDED Requirements

### Requirement: Every ad request is child-directed
Because the audience includes children and players are never asked their age, every player SHALL be treated as a child. Every ad request, interstitial or banner, SHALL be tagged as child-directed and as under the age of consent, SHALL be limited to ad content rated G, and SHALL NOT be personalised.

#### Scenario: Interstitial request
- **WHEN** an interstitial is requested
- **THEN** the request is child-directed, under the age of consent, capped at rating G and non-personalised

#### Scenario: Banner request
- **WHEN** a banner is requested
- **THEN** the request is child-directed, under the age of consent, capped at rating G and non-personalised

### Requirement: Consent flow treats the player as under age
The consent flow SHALL be started with the player marked as under the age of consent. The existing rules still hold: no ad is requested before the flow resolves, and a failed flow means no ads and normal play.

#### Scenario: Consent requested
- **WHEN** the app starts the consent flow
- **THEN** the request marks the player as under the age of consent

### Requirement: No advertising ID permission
The merged Android manifest SHALL NOT contain the `com.google.android.gms.permission.AD_ID` permission, even though the ads SDK declares it.

#### Scenario: Merged manifest
- **WHEN** the app is built
- **THEN** the merged manifest does not contain the `com.google.android.gms.permission.AD_ID` permission

## REMOVED Requirements

### Requirement: Advertising ID permission declared
**Reason**: The audience includes children. Google Play's Families policy forbids sending the advertising ID from children or players of unknown age, and the game treats every player as a child.
**Migration**: Replaced by "No advertising ID permission". The permission is removed from the merged manifest, and the Play Console advertising ID declaration is updated to say the app does not use it.
