# Yippee Blue Tears

A RuneLite plugin that plays a sound whenever a blue tear stream starts on the
weeping wall crack you're already standing at, in the Tears of Guthix
minigame.

## Why

Tears of Guthix has you watching for the crack next to you to switch from
green to blue tears so you can start collecting. This plugin gives an audio
cue the moment that happens, so you don't have to stare at the wall.

## How it works

The plugin listens for `DecorativeObjectSpawned` events matching the wall's
blue tears object (`ObjectID.TOG_WEEPING_WALL_GOOD_R` / `_GOOD_L`), checks
that the object is adjacent to the player, and plays `tear.mp3` via a
bundled MP3 decoder ([JLayer](https://www.javazoom.net/javalayer/javalayer.html)).
