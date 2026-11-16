## Decisions

- **Find the source first.** Run `dependencyInsight --dependency androidx.fragment:fragment` on `releaseRuntimeClasspath`; the fix depends on the parent. Assumption: it is transitive, via `activity-compose` or a Compose/Play dependency.
- **Prefer bumping the parent.** If a newer parent release brings fragment 1.2.1+, bump that version in `libs.versions.toml`. Rejected: a blanket constraint, which hides the stale parent.
- **Fallback: catalog entry plus constraint.** If no parent bump works, add `androidx-fragment` (pinned exactly, like every catalog version) to the version catalog and declare it in `androidApp` as a `constraints { implementation(...) }` entry. Rejected: a `resolutionStrategy.force`, which is global and opaque.
- **Android host only.** The change stays in `androidApp` and the catalog; `commonMain` is untouched.
- **Pick a current stable fragment version**, not just the 1.2.1 minimum. Assumption: the latest stable one compatible with the AGP and compileSdk in use.
- **Review other SDKs** from the same classpath output; only raise ones that are clearly flagged or outdated, and note the rest.

## Assumptions

- The dependency is transitive (the issue says "most likely").
- Play's warning is judged by the resolved release version.
