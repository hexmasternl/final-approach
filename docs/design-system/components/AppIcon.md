The app icon: the sun-yellow jet in close-up, lined up over a runway, and how it looks under each platform mask.

**You provide:** nothing: use the files in the AppIcon asset group as they are. They are built by `tools/build-app-icon.cjs`; do not edit them by hand.

**Construction.** The icon is built only from system parts. The jet is the game's own sprite, unchanged, in the first livery (`sun`), outlined at `line-hero`, flying at 45 degrees. Under it lies its soft shadow, thrown down-right (W1). The ground is the mown lawn in `grass-500` and `grass-600` stripes and the version 2 runway the jet is lined up with: `grass-700` fringe, `concrete-500` shoulder, outlined `asphalt-500`, `marking` edge lines and centre dashes, and `beacon` edge lights.

**Close-up on purpose.** The jet is larger than the icon. Its wing tips and its tail bleed off the edges, so the icon reads as a close-up rather than as a small aircraft on a field. The nose and the wing roots stay inside the central 66% that every launcher mask keeps, so circle, squircle and rounded-square masks all show the cockpit and the start of both wings.

**Files.**
- Stores and iOS: `app-icon.svg` or its PNG exports (`app-icon-1024.png`, `app-icon-512.png`), full bleed, no rounding of your own.
- Android: an adaptive icon in two layers. `app-icon-background.svg` is the lawn and the runway; `app-icon-foreground.svg` is the jet and its soft shadow on a transparent ground. The tool renders both at 432px into the app's `mipmap-xxxhdpi` folder as `ic_launcher_background.png` and `ic_launcher_foreground.png`.
- In-app, docs and web: `app-icon-rounded.svg`, which adds the rounded `outline` frame the rest of the system has.

Minimum size 40px. Below that use the `plane` icon instead. Keep clear space of a quarter of the icon width around it, and place it only on `bg-sky`, `paper-100` or a photo-free flat ground.

**Don't** shrink the jet to fit it whole, add a path or text, or round the full-bleed files yourself.
