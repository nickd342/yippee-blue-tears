package com.nickd342.yippeebluetears;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.DecorativeObject;
import net.runelite.api.GameState;
import net.runelite.api.Player;
import net.runelite.api.gameval.ObjectID;

// Works out where each tear stream falls in its world's stream order.
//
// The six streams change in a burst, one game tick apart, followed by a
// pause of around eleven ticks. A world's order (e.g. gggbbb) is the colour
// sequence within a burst, so the first stream after the pause is always the
// first in the order. Blue and green streams are each numbered 1-3 from there.
//
// We only number a burst we saw start. If we arrive (log in, hop, enter the
// cave) partway through one, we can't tell how many streams we missed, so
// those streams are left unnumbered until the next burst begins.
@Slf4j
@Singleton
class StreamOrderTracker
{
    private static final int TOG_REGION = 12948;
    private static final int STREAMS_PER_COLOUR = 3;

    private final Client client;

    // Streams currently on the walls that we've numbered.
    private final Map<DecorativeObject, TearStream> streams = new HashMap<>();

    // Streams numbered in the burst in progress, so they can be un-numbered
    // if the burst turns out to be bad data.
    private final List<DecorativeObject> currentBurst = new ArrayList<>();

    // First tick we were logged in and in the cave, or -1 if we aren't.
    private int watchStartTick = -1;

    // Tick the last stream spawned on, or -1 if none have since watching began.
    private int lastSpawnTick = -1;

    // Whether the burst in progress started while we were watching, so its
    // order is known.
    private boolean numbering;
    private int blueInBurst;
    private int greenInBurst;

    @Inject
    StreamOrderTracker(Client client)
    {
        this.client = client;
    }

    Map<DecorativeObject, TearStream> getStreams()
    {
        return Collections.unmodifiableMap(streams);
    }

    void onGameStateChanged(GameState gameState)
    {
        // Entering or leaving the cave, hopping, and logging out all load a
        // new scene, so we have to wait for a fresh burst.
        if (gameState != GameState.LOGGED_IN)
        {
            reset();
        }
    }

    void onGameTick()
    {
        if (!inCave())
        {
            if (watchStartTick != -1)
            {
                reset();
            }
            return;
        }

        if (watchStartTick == -1)
        {
            watchStartTick = client.getTickCount();
        }
    }

    void onSpawned(DecorativeObject object)
    {
        int id = object.getId();
        boolean blue = isBlueTears(id);
        if (!blue && !isGreenTears(id))
        {
            return;
        }

        // Streams already running when the scene loads are re-sent without
        // us having seen them start.
        if (client.getGameState() != GameState.LOGGED_IN || !inCave())
        {
            return;
        }

        int tick = client.getTickCount();
        if (tick == lastSpawnTick)
        {
            // Bursts spawn one stream per tick. More than one in a tick is
            // the client reloading the scene, not real spawns.
            log.debug("Multiple tear streams spawned on tick {}, discarding burst", tick);
            discardBurst();
            return;
        }

        boolean continuesBurst = lastSpawnTick != -1 && tick == lastSpawnTick + 1;
        if (!continuesBurst)
        {
            // A new burst only has a known start if we saw the quiet gap
            // before it. The first spawn after arriving, without a quiet
            // tick watched first, may be partway through a burst.
            boolean sawGap = lastSpawnTick != -1 || (watchStartTick != -1 && tick - watchStartTick > 1);
            if (sawGap)
            {
                startBurst();
            }
            else
            {
                numbering = false;
            }
        }
        lastSpawnTick = tick;

        if (!numbering)
        {
            return;
        }

        int order = blue ? ++blueInBurst : ++greenInBurst;
        if (order > STREAMS_PER_COLOUR)
        {
            log.debug("More than {} {} streams in one burst, discarding burst", STREAMS_PER_COLOUR, blue ? "blue" : "green");
            discardBurst();
            return;
        }

        streams.put(object, new TearStream(blue, order));
        currentBurst.add(object);
        log.debug("{} tears #{} spawned at {} on tick {}", blue ? "Blue" : "Green", order, object.getWorldLocation(), tick);
    }

    void onDespawned(DecorativeObject object)
    {
        streams.remove(object);
        currentBurst.remove(object);
    }

    void reset()
    {
        streams.clear();
        currentBurst.clear();
        watchStartTick = -1;
        lastSpawnTick = -1;
        numbering = false;
        blueInBurst = 0;
        greenInBurst = 0;
    }

    private void startBurst()
    {
        currentBurst.clear();
        numbering = true;
        blueInBurst = 0;
        greenInBurst = 0;
    }

    private void discardBurst()
    {
        for (DecorativeObject object : currentBurst)
        {
            streams.remove(object);
        }
        currentBurst.clear();
        numbering = false;
    }

    private boolean inCave()
    {
        Player player = client.getLocalPlayer();
        return player != null && player.getWorldLocation().getRegionID() == TOG_REGION;
    }

    private static boolean isBlueTears(int id)
    {
        return id == ObjectID.TOG_WEEPING_WALL_GOOD_R || id == ObjectID.TOG_WEEPING_WALL_GOOD_L;
    }

    private static boolean isGreenTears(int id)
    {
        return id == ObjectID.TOG_WEEPING_WALL_BAD_R || id == ObjectID.TOG_WEEPING_WALL_BAD_L;
    }
}
