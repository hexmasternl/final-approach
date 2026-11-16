# Proposal

GitHub issue: #64

## Why

The privacy policy (`docs/privacy-policy.md`) still describes a game with no ads, no advertising ID and no network connections, but since `level-start-ads` the game shows AdMob interstitials and banners behind Google's consent message. The game's audience includes children, so Google Play's Families policy also applies: ads to children, or to players of unknown age, must be child-directed and must not use the advertising ID. Both must be right before the next release with real ads.

## What Changes

- Rewrite the privacy policy so it matches the app: the ads and where they appear, the consent message, what Google may collect, internet use and permissions, data kept on the device, a children section, the only third party (Google), and how to get in touch.
- Treat every player as a child. There is no age screen. Every ad request is tagged child-directed and under the age of consent, ad content is capped at rating G, and ads are never personalised.
- **BREAKING** (store-facing): the app no longer declares the `com.google.android.gms.permission.AD_ID` permission. The ads SDK merges it in, so it is removed explicitly. This replaces the `level-start-ads` requirement that declared it.
- Publish the policy at one fixed public URL, the GitHub page of `docs/privacy-policy.md` on `main`. That is the URL for the Play listing and for the future Settings link.
- Clean-up: remove the doubled `AD_ID` permission and `APPLICATION_ID` meta-data entries in the Android manifest.
- Human tasks in the Play Console: target audience includes children, the Ads declaration, the Data safety form, and the privacy policy URL.

## Capabilities

### New Capabilities
- `privacy-policy`: the published privacy policy, meaning what it must state, that it matches the app's actual data handling, and where it is published.

### Modified Capabilities
- `level-start-ads`: ad requests become child-directed, under the age of consent, rated G and non-personalised. The consent flow is run with the player treated as under the age of consent. The "Advertising ID permission declared" requirement is replaced by a requirement that the advertising ID permission is absent.

## Impact

- `docs/privacy-policy.md` is rewritten.
- `app/src/androidApp/src/main/kotlin/nl/hexmaster/finalapproach/AdMobAds.kt`: the consent request parameters and a global ad request configuration change.
- `app/src/androidApp/src/main/AndroidManifest.xml`: the duplicate entries go, and `AD_ID` is removed with a manifest-merger `tools:node="remove"`.
- `docs/architecture.md`: the ads paragraph describes the child-directed configuration.
- Ad revenue per view drops, because ads are never personalised. This was accepted when choosing "treat everyone as a child".
- No change to `commonMain`, the `Ads` interface or gameplay.
- Out of scope: the settings page and its link to the policy, and a neutral age screen.
