The app icon and how it looks under each platform mask.

**You provide:** nothing: use the files in the AppIcon asset group as they are.

- Stores and iOS: `app-icon.svg` or its PNG exports, full bleed, no rounding of your own.
- Android: `app-icon-foreground.svg` over `app-icon-background.svg` as an adaptive icon. Everything that matters sits inside the central 66%, so circle, squircle and rounded-square masks all keep the jet and the start of its path.
- In-app, docs and web: `app-icon-rounded.svg`, which adds the `line`-weight `outline` frame the rest of the system has.

Minimum size 40px. Below that the dashes close up: use the `plane` icon instead. Keep clear space of a quarter of the icon width around it, and place it only on `bg-sky`, `surface` or a photo-free flat ground.
