# Yippee Blue Tears

A RuneLite plugin that plays a rewarding sound when a blue tear stream starts on the
same weeping wall crack you're collecting from.

## Why

Fun :)

## How it works

The plugin listens for `DecorativeObjectSpawned` events matching the wall's
blue tears object (`ObjectID.TOG_WEEPING_WALL_GOOD_R` / `_GOOD_L`), checks
that the object is adjacent to the player, and plays `tear.mp3` via a
bundled MP3 decoder ([JLayer](https://www.javazoom.net/javalayer/javalayer.html)).