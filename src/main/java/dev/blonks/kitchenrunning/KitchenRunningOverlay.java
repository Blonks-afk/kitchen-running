package dev.blonks.kitchenrunning;

import com.google.common.collect.ImmutableSet;
import net.runelite.api.Client;
import net.runelite.api.Perspective;
import net.runelite.api.Player;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayUtil;

import javax.inject.Inject;
import java.awt.*;
import java.util.Optional;
import java.util.Set;

public class KitchenRunningOverlay extends Overlay {
    private final Client client;
    private final KitchenRunningConfig config;


    @Inject
    private KitchenRunningOverlay(Client client, KitchenRunningConfig config) {
        this.client = client;
        this.config = config;
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_SCENE);
        setPriority(PRIORITY_MED);
    }

    @Override
    public Dimension render(Graphics2D graphics2D) {
        if (!PlayerPositionUtils.isInKitchen(client)) {
            return null;
        }

        if (PlayerPositionUtils.isPvpOrNonLeagues(client)) {
            return null;
        }

        // renders good tile starting point markers
        boolean isOnGoodTile = PlayerPositionUtils.isOnGoodTile(client.getLocalPlayer());
        if (!isOnGoodTile) {
            for (WorldPoint point : PlayerPositionUtils.GOOD_TILES) {
                renderPlayerTile(graphics2D, LocalPoint.fromWorld(client, point), config.startingTilesBorder(), config.startingTilesFill());
            }
        }

        // renders nothing if the player is in cycle
        if (PlayerPositionUtils.isFollowingConductor(config, client.getLocalPlayer()) && isOnGoodTile)
            return null;

        final LocalPoint playerLocalPoint = PlayerPositionUtils.getLocalPoint(client, client.getLocalPlayer());
        if (playerLocalPoint == null)
            return null;


        Optional<? extends Player> conductorPlayer = client.getTopLevelWorldView().players().stream()
                .filter(player -> player.getName().equalsIgnoreCase(config.conductorUsername()))
                .findFirst();

        if (conductorPlayer.isEmpty())
            return null;

        WorldPoint conductorWorldPoint = conductorPlayer.get().getWorldLocation();
        if (conductorWorldPoint == null)
            return null;

        final LocalPoint conductorLocalPoint = LocalPoint.fromWorld(client, conductorWorldPoint);

        renderPlayerTile(graphics2D, playerLocalPoint, config.playerTileBorder(), config.playerTileFill());
        renderPlayerTile(graphics2D, conductorLocalPoint, config.conductorPlayerTileBorder(), config.conductorPlayerTileFill());

        return null;
    }

    private void renderPlayerTile(Graphics2D graphics2D, final LocalPoint tile, Color borderColor, Color fillColor) {
        if (tile == null)
            return;

        final Polygon polygon = Perspective.getCanvasTilePoly(client, tile);

        if (polygon == null)
            return;

        OverlayUtil.renderPolygon(graphics2D, polygon, borderColor, fillColor, new BasicStroke((float) 2));
    }
}
