package dev.blonks.kitchenrunning;

import com.google.common.collect.ImmutableSet;
import net.runelite.api.Actor;
import net.runelite.api.Client;
import net.runelite.api.Player;
import net.runelite.api.WorldType;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;

import java.util.Set;

public class PlayerPositionUtils {
    public static final WorldArea LUMBRIDGE_KITCHEN = new WorldArea(3205, 3212, 8, 6, 0);
    public static final Set<WorldPoint> GOOD_TILES = ImmutableSet.of(
            new WorldPoint(3207, 3213, 0),
            new WorldPoint(3209, 3213, 0),
            new WorldPoint(3211, 3213, 0),
            new WorldPoint(3207, 3215, 0),
            new WorldPoint(3209, 3215, 0),
            new WorldPoint(3211, 3215, 0)
    );
    public static final Set<WorldPoint> BAD_TILES = ImmutableSet.of(
            new WorldPoint(3208, 3215, 0),
            new WorldPoint(3210, 3215, 0),
            new WorldPoint(3211, 3214, 0),
            new WorldPoint(3210, 3213, 0),
            new WorldPoint(3208, 3213, 0),
            new WorldPoint(3207, 3214, 0)
    );

    public static boolean isInKitchen(Client client) {
        Player localPlayer = client.getLocalPlayer();
        if (localPlayer != null) {
            WorldPoint playerLocation = localPlayer.getWorldLocation();
            return playerLocation.isInArea(LUMBRIDGE_KITCHEN);
        }
        return false;
    }

    public static CycleState getPlayerCycleState(KitchenRunningConfig config, Player player) {
        boolean isOnGoodTile = isOnGoodTile(player);
        boolean isFollowingConductor = isFollowingConductor(config, player);

        if (isOnGoodTile && isFollowingConductor)
            return CycleState.IN_CYCLE;

        return CycleState.OUT_OF_CYCLE;
    }

    public static boolean isInCycle(KitchenRunningConfig config, Player player) {
        return isOnTileSet(player, GOOD_TILES) && isFollowingConductor(config, player);
    }

    public static boolean isOnGoodTile(Player player) {
        return isOnTileSet(player, GOOD_TILES);
    }

    public static boolean isOnBadTile(Player player) {
        return isOnTileSet(player, BAD_TILES);
    }

    private static boolean isOnTileSet(Player player, Set<WorldPoint> tileSet) {
        if (player == null)
            return false;

        WorldPoint worldPoint = player.getWorldLocation();
        if (worldPoint == null)
            return false;

        for (WorldPoint point : tileSet) {
            if (point.distanceTo(worldPoint) == 0)
                return true;
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

    public static boolean isFollowingConductor(KitchenRunningConfig config, Player player) {
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

    public static boolean isPvpOrNonLeagues(Client client) {
        // render everyone on pvp worlds
        if (WorldType.isPvpWorld(client.getWorldType())) {
            return true;
        }

        // render everyone when its not a seasonal or tournament world (i.e. leagues or grid master)
        if (!client.getWorldType().contains(WorldType.SEASONAL) && !client.getWorldType().contains(WorldType.TOURNAMENT_WORLD)) {
            return true;
        }

        return false;
    }
}
