package com.nickd342.yippeebluetears;

import com.google.inject.Provides;
import javax.inject.Inject;

import net.runelite.api.Client;
import net.runelite.api.DecorativeObject;
import net.runelite.api.GameState;
import net.runelite.api.MenuAction;
import net.runelite.api.Player;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.DecorativeObjectSpawned;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.api.gameval.ObjectID;
import net.runelite.client.audio.AudioPlayer;
import net.runelite.client.config.ConfigManager;
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

    @Inject
    private YippeeBlueTearsConfig config;

    // The wall tile the player last clicked to collect from, or null if they
    // haven't clicked one or have since clicked something else in the world.
    private WorldPoint targetWall;

    // The last tears object id seen on the target wall. Used so a blue stream
    // only counts once, even if the game re-sends the same object (e.g. on a
    // scene reload).
    private int targetWallId = -1;

    @Provides
    YippeeBlueTearsConfig provideConfig(ConfigManager configManager)
    {
        return configManager.getConfig(YippeeBlueTearsConfig.class);
    }

    @Override
    protected void shutDown()
    {
        clearTarget();
    }

    @Subscribe
    public void onMenuOptionClicked(MenuOptionClicked event)
    {
        MenuAction action = event.getMenuAction();
        switch (action)
        {
            case GAME_OBJECT_FIRST_OPTION:
            case GAME_OBJECT_SECOND_OPTION:
            case GAME_OBJECT_THIRD_OPTION:
            case GAME_OBJECT_FOURTH_OPTION:
            case GAME_OBJECT_FIFTH_OPTION:
                if (isWeepingWall(event.getId()))
                {
                    targetWall = WorldPoint.fromScene(client.getTopLevelWorldView(),
                        event.getParam0(), event.getParam1(), client.getTopLevelWorldView().getPlane());
                    targetWallId = event.getId();
                    log.debug("Collecting from wall {} (object {})", targetWall, targetWallId);
                }
                else
                {
                    clearTarget();
                }
                break;
            case WALK:
            case ITEM_USE_ON_GAME_OBJECT:
            case WIDGET_TARGET_ON_GAME_OBJECT:
            case ITEM_USE_ON_NPC:
            case WIDGET_TARGET_ON_NPC:
            case NPC_FIRST_OPTION:
            case NPC_SECOND_OPTION:
            case NPC_THIRD_OPTION:
            case NPC_FOURTH_OPTION:
            case NPC_FIFTH_OPTION:
            case ITEM_USE_ON_PLAYER:
            case WIDGET_TARGET_ON_PLAYER:
            case PLAYER_FIRST_OPTION:
            case PLAYER_SECOND_OPTION:
            case PLAYER_THIRD_OPTION:
            case PLAYER_FOURTH_OPTION:
            case PLAYER_FIFTH_OPTION:
            case PLAYER_SIXTH_OPTION:
            case PLAYER_SEVENTH_OPTION:
            case PLAYER_EIGHTH_OPTION:
            case ITEM_USE_ON_GROUND_ITEM:
            case WIDGET_TARGET_ON_GROUND_ITEM:
            case GROUND_ITEM_FIRST_OPTION:
            case GROUND_ITEM_SECOND_OPTION:
            case GROUND_ITEM_THIRD_OPTION:
            case GROUND_ITEM_FOURTH_OPTION:
            case GROUND_ITEM_FIFTH_OPTION:
                // Any other world interaction stops the player collecting.
                clearTarget();
                break;
            default:
                break;
        }
    }

    @Subscribe
    public void onDecorativeObjectSpawned(DecorativeObjectSpawned event)
    {
        if (targetWall == null)
        {
            return;
        }

        DecorativeObject decorativeObject = event.getDecorativeObject();
        int id = decorativeObject.getId();
        if (!isWeepingWall(id) || !targetWall.equals(decorativeObject.getWorldLocation()))
        {
            return;
        }

        int previousId = targetWallId;
        targetWallId = id;

        // Only a change into blue counts - blue replacing blue is the same
        // stream being re-sent, not a new one.
        if (!isBlueTears(id) || isBlueTears(previousId))
        {
            return;
        }

        // Objects are re-sent while a scene loads; those aren't new streams.
        if (client.getGameState() != GameState.LOGGED_IN)
        {
            return;
        }

        Player player = client.getLocalPlayer();
        if (player == null)
        {
            return;
        }

        // The tears object sits on the wall's own tile, next to the player.
        // If the player has wandered off they're no longer collecting.
        if (targetWall.distanceTo(player.getWorldLocation()) > 1)
        {
            clearTarget();
            return;
        }

        playSound();
    }

    private void clearTarget()
    {
        targetWall = null;
        targetWallId = -1;
    }

    private static boolean isBlueTears(int id)
    {
        return id == ObjectID.TOG_WEEPING_WALL_GOOD_R || id == ObjectID.TOG_WEEPING_WALL_GOOD_L;
    }

    private static boolean isWeepingWall(int id)
    {
        switch (id)
        {
            case ObjectID.TOG_WEEPING_WALL_GOOD_R:
            case ObjectID.TOG_WEEPING_WALL_BAD_R:
            case ObjectID.TOG_WEEPING_WALL_OFF_R:
            case ObjectID.TOG_WEEPING_WALL_GOOD_L:
            case ObjectID.TOG_WEEPING_WALL_BAD_L:
            case ObjectID.TOG_WEEPING_WALL_OFF_L:
                return true;
            default:
                return false;
        }
    }

    private void playSound()
    {
        int volume = config.volume();
        if (volume <= 0)
        {
            return;
        }

        // AudioPlayer's gain is in decibels, not a linear scale, so convert the
        // percentage to an attenuation: 100% is untouched, 50% is -6dB.
        float gain = 20f * (float) Math.log10(volume / 100f);

        // AudioPlayer.play() opens the clip and returns immediately - playback
        // runs on the Java Sound system's own thread, not the client thread.
        try
        {
            audioPlayer.play(YippeeBlueTearsPlugin.class, "/tear.wav", gain);
        }
        catch (Exception e)
        {
            log.warn("Failed to play tear sound", e);
        }
    }
}
