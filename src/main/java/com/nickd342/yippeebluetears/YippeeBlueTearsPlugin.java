package com.nickd342.yippeebluetears;

import javax.inject.Inject;

import net.runelite.api.Client;
import net.runelite.api.DecorativeObject;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.DecorativeObjectSpawned;
import net.runelite.api.gameval.ObjectID;
import net.runelite.client.audio.AudioPlayer;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@PluginDescriptor(
    name = "Yippee Blue Tears",
    description = "Plays a rewarding sound everytime a consecutive blue tear stream appears on a wall you're already collecting from",
    tags = {"tears", "guthix", "sound", "yippee"}
)
public class YippeeBlueTearsPlugin extends Plugin
{
    @Inject
    private Client client;

    @Inject
    private AudioPlayer audioPlayer;

    @Subscribe
    public void onDecorativeObjectSpawned(DecorativeObjectSpawned event)
    {
        DecorativeObject decorativeObject = event.getDecorativeObject();
        int id = decorativeObject.getId();

        if (id != ObjectID.TOG_WEEPING_WALL_GOOD_R && id != ObjectID.TOG_WEEPING_WALL_GOOD_L)
        {
            return;
        }

        WorldPoint tearLocation = WorldPoint.fromLocal(client, decorativeObject.getLocalLocation());
        WorldPoint playerLocation = client.getLocalPlayer().getWorldLocation();

        // The tears object sits on the wall's own tile, not the tile the
        // player is standing on, so check adjacency instead of an exact
        // match - i.e. the crack right next to you just turned blue.
        if (tearLocation.distanceTo(playerLocation) <= 1)
        {
            playSound();
        }
    }

    private void playSound()
    {
        // AudioPlayer.play() opens the clip and returns immediately - playback
        // runs on the Java Sound system's own thread, not the client thread.
        try
        {
            audioPlayer.play(YippeeBlueTearsPlugin.class, "/tear.wav", 0f);
        }
        catch (Exception e)
        {
            log.warn("Failed to play tear sound", e);
        }
    }
}
