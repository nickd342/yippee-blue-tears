# Yippee Blue Tears

A RuneLite plugin that plays a rewarding sound when a blue tear stream starts on the
same weeping wall crack you're collecting from.

## Why

Fun :)

## How it works

The plugin listens for `DecorativeObjectSpawned` events matching the wall's
blue tears object (`ObjectID.TOG_WEEPING_WALL_GOOD_R` / `_GOOD_L`), checks
that the object is adjacent to the player, and plays `tear.wav` through
RuneLite's built-in `AudioPlayer`.

## Settings

**Volume** — how loud the sound plays, from 0 to 100%. Defaults to 50%.
Set it to 0 to mute the plugin without disabling it.