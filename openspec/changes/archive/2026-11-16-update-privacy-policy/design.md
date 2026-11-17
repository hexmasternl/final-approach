# Design

## Context

See proposal.md (Why). Current state:

- `AdMobAds.kt` asks for consent with a default `ConsentRequestParameters`, then calls `MobileAds.initialize` once `canRequestAds()` allows it. It builds a plain `AdRequest` for the interstitial and for the adaptive banner below the play field in `GameScreen`. No request configuration is set.
- `AndroidManifest.xml` declares `AD_ID` and the `APPLICATION_ID` meta-data twice each. The duplicates are left over from a merge.
- Android host code has no unit tests. The ads code lives only in `androidApp`, behind `platform/Ads.kt`, and none of it reaches `commonMain`.
- Decisions already made with the owner: the audience includes children, every player is treated as a child with no age screen, and the policy lives at the GitHub page of `docs/privacy-policy.md` on `main`.

## Goals / Non-Goals

**Goals:**
- One place configures all ad requests as child-directed, so a new ad format cannot miss it.
- The merged manifest provably has no `AD_ID`.
- A policy that a reviewer can check line by line against the code.

**Non-Goals:**
- Changing the ads SDK, the ad formats or when ads appear.
- An in-app policy screen or the Settings link. Both belong to the settings change.
- Mediation or other ad networks.

## Decisions

### 1. A global request configuration, set before the SDK starts
Call `MobileAds.setRequestConfiguration(...)` once, before `MobileAds.initialize`, with:
- `TAG_FOR_CHILD_DIRECTED_TREATMENT_TRUE`
- `TAG_FOR_UNDER_AGE_OF_CONSENT_TRUE`
- `MAX_AD_CONTENT_RATING_G`

Child-directed treatment already makes Google serve only non-personalised ads. No per-request `npa` extra is needed.

*Alternative:* tag each `AdRequest`. Rejected, because the interstitial and the banner build their own requests, and a third format would have to remember to do the same. The global configuration covers every request the SDK sends.

### 2. The consent request marks the player as under age
Build `ConsentRequestParameters` with `setTagForUnderAgeOfConsent(true)`, so UMP handles the player as a minor and does not ask for consent to personalisation. The rest of the flow stays as it is: an ad is requested only when `canRequestAds()` is true, and an error means no ads.

*Alternative:* skip UMP entirely for child-directed traffic. Rejected, because the `level-start-ads` spec requires the consent flow to resolve before any request. UMP also still covers regulated regions and keeps one code path.

### 3. Remove `AD_ID` with the manifest merger
Add the `tools` namespace and `<uses-permission android:name="com.google.android.gms.permission.AD_ID" tools:node="remove" />`. This is the approach Google documents for apps that must not use the advertising ID. Deleting our own line is not enough, because `play-services-ads` merges the permission in. Verify it in the merged manifest under `androidApp/build/intermediates/merged_manifest/<variant>/`, for both debug and release.

### 4. The policy URL is the `main` blob page
`main` only changes on a release merge, so the page always shows the policy of the released app. The policy's own "Changes" section points to that URL instead of the repository path.

*Alternative:* GitHub Pages. Rejected for now, because it needs setup and publishing. The spec names one URL, so switching later is a spec change.

### 5. No new automated tests
The work is SDK configuration in the Android host, which has no test harness, plus a document. Verification is a build, an inspection of the merged manifest, a run on a device with test ads, and a requirement-by-requirement read of the policy. Shared tests must still pass.

## Risks / Trade-offs

- [Lower ad revenue, because ads are never personalised] → Accepted by the owner when choosing to treat everyone as a child.
- [With the player marked under age, UMP may leave `canRequestAds()` false in some regions, so no ads show there] → Check on a device with UMP's EEA debug geography and test ads, and write down the result in this design. If ads never show, raise it with the owner before release, because the spec already makes "no ads" a valid outcome.
- [Families policy limits on ad formats: full-screen ads must be closable, and ads must not get in the way of play. The banner sits below the play field.] → The play-policy-critic reviews the change. Any finding about the banner placement becomes its own issue rather than growing this change.
- [The Data safety form or Play Console declarations disagree with the policy] → Human tasks list each declaration, with the policy text as the reference.
- [An SDK update re-adds a permission or a data type] → The `privacy-policy` spec requires the policy to be updated in the same release, and the play-policy-critic runs on dependency changes.

## Migration Plan

Ship it in the next release with real ads. Make the Play Console changes (target audience, Ads and advertising ID declarations, Data safety, policy URL) in the same release, before it is sent for review. Rollback means reverting the commit, but the Families declarations would then be wrong, so a rollback must also undo the target audience change.
