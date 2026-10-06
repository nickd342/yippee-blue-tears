package com.nickd342.yippeebluetears;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.util.Map;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.DecorativeObject;
import net.runelite.api.Perspective;
import net.runelite.api.Point;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayUtil;

class StreamOrderOverlay extends Overlay
{
    // Height above the tile to draw the number, so it sits on the stream
    // rather than at the base of the wall.
    private static final int TEXT_Z_OFFSET = 150;

    private final Client client;
    private final StreamOrderTracker tracker;
    private final YippeeBlueTearsConfig config;

    @Inject
    StreamOrderOverlay(Client client, StreamOrderTracker tracker, YippeeBlueTearsConfig config)
    {
        this.client = client;
        this.tracker = tracker;
        this.config = config;
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_SCENE);
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
        if (!config.showStreamOrder())
        {
            return null;
        }

        Map<DecorativeObject, TearStream> streams = tracker.getStreams();
        if (streams.isEmpty())
        {
            return null;
        }

        Font font = FontManager.getRunescapeBoldFont().deriveFont(Font.BOLD, 24f);
        graphics.setFont(font);

        int plane = client.getTopLevelWorldView().getPlane();
        for (Map.Entry<DecorativeObject, TearStream> entry : streams.entrySet())
        {
            DecorativeObject object = entry.getKey();
            if (object.getPlane() != plane)
            {
                continue;
            }

            TearStream stream = entry.getValue();
            String text = String.valueOf(stream.getOrder());
            Point point = Perspective.getCanvasTextLocation(client, graphics, object.getLocalLocation(), text, TEXT_Z_OFFSET);
            if (point != null)
            {
                OverlayUtil.renderTextLocation(graphics, point, text, stream.isBlue() ? config.blueOrderColor() : config.greenOrderColor());
            }
        }

        return null;
    }
}
