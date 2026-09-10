package com.nickd342.yippeebluetears;

import java.io.InputStream;
import javazoom.jl.player.Player;

/**
 * Plays tear.mp3 directly, with no RuneLite client involved - just to prove
 * the file and the JLayer decoding path actually work on this machine.
 */
public class SoundTest
{
    public static void main(String[] args) throws Exception
    {
        try (InputStream audioStream = SoundTest.class.getResourceAsStream("/tear.mp3"))
        {
            if (audioStream == null)
            {
                throw new IllegalStateException("tear.mp3 not found on classpath");
            }

            System.out.println("Playing tear.mp3...");
            new Player(audioStream).play();
            System.out.println("Done.");
        }
    }
}
