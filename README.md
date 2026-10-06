# Yippee Blue Tears

A RuneLite plugin that plays a rewarding sound when a blue tear stream starts on the same weeping wall crack you're collecting from. Also adds a toggleable counter to track the order the streams spawn.

## Why

Fun :)

## How it works

The plugin listens for `DecorativeObjectSpawned` events matching the wall's
blue tears object (`ObjectID.TOG_WEEPING_WALL_GOOD_R` / `_GOOD_L`), checks
that the object is adjacent to the player, and plays `tear.wav` through
RuneLite's built-in `AudioPlayer`.

## Settings

**Volume** - How loud the sound plays, from 0 to 100%. Defaults to 15%.

**Show stream order** - Draws a number on each tear stream showing where it falls
in the spawn order for its colour: blue streams count 1-3, and green streams count
1-3 separately. Head for blue stream 1, if the next blue stream spawns on the
same crack when it runs out, you keep collecting without moving.

The six streams change in a burst, one game tick apart, followed by a longer pause. Numbering restarts at the first stream of each burst, so the numbers match the world's stream order (e.g. `gggbbb`). If you arrive partway through a burst after logging in, hopping, or entering the cave those streams aren't numbered. Numbering starts with the next burst. On by default.

**Blue number colour / Green number colour** - Colours used for the order numbers.
