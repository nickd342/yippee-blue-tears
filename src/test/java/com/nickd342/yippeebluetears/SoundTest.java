package com.nickd342.yippeebluetears;

import net.runelite.client.audio.AudioPlayer;

public class SoundTest
{
    public static void main(String[] args) throws Exception
    {
        System.out.println("Playing tear.wav...");
        new AudioPlayer().play(SoundTest.class, "/tear.wav", 0f);
        Thread.sleep(4000);
        System.out.println("Done.");
    }
}
