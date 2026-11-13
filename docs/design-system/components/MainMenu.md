A reference composition of the main page: the wordmark and two moulded buttons in front of the animated sky.

**You provide:** nothing: this is a showcase, not a component.

- **Actions:** one large primary Button, Take off, and one ghost Button, Settings, in a 280px column at the centre of the screen. There is no Quit button: the system back button opens a quit Dialog with a danger Quit and a ghost Cancel.
- **Version:** the app version in `label` style and `on-sky-muted`, `space-4` from the bottom and right edges, written as `V 0.1.0`.
- **Backdrop:** the sky sunburst (`bg-sky` and `sky-300` rays, 9 degrees each) with its centre at the centre of the screen, turning once every four minutes.
- **Clouds:** exactly three, at three sizes, each drifting up to 10px around its own place on a slow loop of 11 to 17 seconds, never in step. A cloud's soft shadow falls 1.6 times as far as an aircraft's, because clouds fly higher.
- **Passing aircraft:** one at a time, any of the five types in any path colour, drawn at 1.5 times its gameplay size with its soft shadow. It flies a straight line across the middle of the screen, under the clouds and behind the wordmark and the buttons, and cannot be tapped. The next one appears 3 seconds after the previous one has left.

- **Wordmark:** the regular Wordmark, "Final Approach", centred above the menu with `space-7` between them; the wordmark and the menu are centred on the screen as one group. When the stacked layout does not fit the height inside the system bars (a landscape phone, about 360px tall), the `--sm` Wordmark sits to the left of the menu instead, `space-7` between them, both vertically centred. The choice is made by measuring, not by orientation. It draws in front of passing aircraft and clouds.

The page carries no score. With reduced motion on, stripes and clouds stand still and no aircraft pass.
