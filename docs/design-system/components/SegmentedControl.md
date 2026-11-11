A row of two to four mutually exclusive choices, one always selected.

**You provide:** the options (one word each), which one is pressed (`aria-pressed="true"`), and a group label.

Trough: `surface-sunken`, `line-2` `outline`, `radius-md`, `shadow-well`, `space-1` padding and gap. The selected segment is a raised `sun` tile with a `line-2` outline and a 2px lip; the others are plain `ink` text in `button-label-sm`.

Used for difficulty. The game design ties difficulty to what a collision costs, so show the consequence under the control in `body`, and update it when the selection changes.

**Don't** exceed four segments or wrap them to two lines.
