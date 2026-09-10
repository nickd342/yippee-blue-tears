package com.nickd342.yippeebluetears;

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
        description = "How loud the tear sound plays. Set to 0 to mute it."
    )
    default int volume()
    {
        return 50;
    }
}
