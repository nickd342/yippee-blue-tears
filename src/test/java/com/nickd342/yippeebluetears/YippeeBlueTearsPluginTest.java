package com.nickd342.yippeebluetears;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class YippeeBlueTearsPluginTest
{
    public static void main(String[] args) throws Exception
    {
        ExternalPluginManager.loadBuiltin(YippeeBlueTearsPlugin.class);
        RuneLite.main(args);
    }
}
