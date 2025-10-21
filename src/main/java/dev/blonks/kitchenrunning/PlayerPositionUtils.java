package dev.blonks.kitchenrunning;

import com.google.common.collect.ImmutableSet;
import net.runelite.api.Actor;
import net.runelite.api.Client;
import net.runelite.api.Player;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;

import java.util.Set;

public class PlayerPositionUtils {
    private static final WorldArea LUMBRIDGE_KITCHEN = new WorldArea(3205, 3212, 8, 6, 0);
    private static final Set<WorldPoint> GOOD_TILES = ImmutableSet.of(
            new WorldPoint(3207, 3213, 0),
            new WorldPoint(3209, 3213, 0),
            new WorldPoint(3211, 3213, 0),
            new WorldPoint(3207, 3215, 0),
            new WorldPoint(3209, 3215, 0),
            new WorldPoint(3211, 3215, 0)
    );

    public static boolean isInKitchen(Client client) {
        Player localPlayer = client.getLocalPlayer();
        if (localPlayer != null) {
            WorldPoint playerLocation = localPlayer.getWorldLocation();
            return playerLocation.isInArea(LUMBRIDGE_KITCHEN);
        }
        return false;
    }

    public static boolean isOnGoodTile(Client client) {
        Player localPlayer = client.getLocalPlayer();
        if (localPlayer == null)
            return false;

        WorldPoint worldPoint = localPlayer.getWorldLocation();
        if (worldPoint == null)
            return false;

        for (WorldPoint point : GOOD_TILES) {
            if (worldPoint.distanceTo(point) == 0) {
                return true;
            }
        }
        return false;
    }

    public static LocalPoint getLocalPoint(Client client, Player player) {
        if (player == null)
            return null;

        WorldPoint worldPoint = player.getWorldLocation();
        if (worldPoint == null)
            return null;

        return LocalPoint.fromWorld(client, worldPoint);
    }

    public static boolean isFollowingConductor(KitchenRunningConfig config, Client client) {
        Player player = client.getLocalPlayer();
        if (player == null)
            return false;

        Actor interacting = player.getInteracting();
        if (interacting == null)
            return false;

        String actorName = interacting.getName();
        if (actorName == null)
            return false;

        return actorName.equalsIgnoreCase(config.conductorUsername());
    }
}
