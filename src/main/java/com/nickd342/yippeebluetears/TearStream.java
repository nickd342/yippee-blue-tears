package com.nickd342.yippeebluetears;

import lombok.Value;

// A tear stream that spawned while the player was in the cave, and where it
// falls in the spawn order for its colour (1-3).
@Value
class TearStream
{
    boolean blue;
    int order;
}
