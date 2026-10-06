package com.nickd342.yippeebluetears;

import java.awt.Color;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Range;
import net.runelite.client.config.Units;

@ConfigGroup("yippee-blue-tears")
public interface YippeeBlueTearsConfig extends Config
{
    @Range(max = 100)
    @Units(Units.PERCENT)
    @ConfigItem(
        keyName = "volume",
        name = "Volume",
        description = "How loud the tear sound plays. Set to 0 to mute it.",
        position = 0
    )
    default int volume()
    {
        return 10;
    }

    @ConfigItem(
        keyName = "showStreamOrder",
        name = "Show stream order",
        description = "Numbers each blue and green tear stream 1-3 in the order it spawned",
        position = 1
    )
    default boolean showStreamOrder()
    {
        return true;
    }

    @ConfigItem(
        keyName = "blueOrderColor",
        name = "Blue number colour",
        description = "Colour of the order numbers drawn on blue tear streams",
        position = 2
    )
    default Color blueOrderColor()
    {
        return Color.CYAN;
    }

    @ConfigItem(
        keyName = "greenOrderColor",
        name = "Green number colour",
        description = "Colour of the order numbers drawn on green tear streams",
        position = 3
    )
    default Color greenOrderColor()
    {
        return Color.GREEN;
    }
}
