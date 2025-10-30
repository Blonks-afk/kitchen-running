package dev.blonks.kitchenrunning.utils;

import com.google.common.collect.ImmutableSet;
import dev.blonks.kitchenrunning.config.KitchenRunningConfig;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import net.runelite.api.Actor;
import net.runelite.api.Client;
import net.runelite.api.Player;
import net.runelite.api.WorldType;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;

import java.util.Set;

/**
 * A class that contains static properties and methods that aid in working with the player location
 * data, as well as general map location
 */
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

	public static boolean shouldProcess(Client client) {
		return isInKitchen(client) && !isPvpOrNonLeagues(client);
	}

    public static boolean isInKitchen(Client client) {
        return isInKitchen(client, client.getLocalPlayer());
    }

	public static boolean isInKitchen(Client client, Player player) {
		if (player == null)
			return false;

		WorldPoint playerLocation = player.getWorldLocation();
		return playerLocation.isInArea(LUMBRIDGE_KITCHEN);
	}

	public static boolean isConductorNearby(Client client, KitchenRunningConfig config) {
		var playerOptional = getConductorPlayer(client, config);

		if (playerOptional.isEmpty())
		{
			return false;
		}

		return isInKitchen(client, playerOptional.get());
	}

	/**
	 * Tries to grab the specified conductor from the nearby players
	 * @return An {@link Optional<? extends Player> optional} that may contain the conductor {@Player}
	 */
	public static Optional<? extends Player> getConductorPlayer(Client client, KitchenRunningConfig config) {
		String conductorName = config.activeConductor();
		if (conductorName == null)
			return Optional.empty();

		Optional<? extends Player> playerOptional = client.getTopLevelWorldView().players().stream()
			.filter(player -> conductorName.strip().equalsIgnoreCase(player.getName()))
			.findFirst();

		return playerOptional;
	}

	public static Optional<? extends Player> getAnyConductorPlayer(Client client, KitchenRunningConfig config) {
		if (config.conductorUsernames() == null || config.conductorUsernames().isBlank())
			return Optional.empty();

		/*
		Always split on line separators if present, else split on commas
		 */
		String splitOn = config.conductorUsernames().contains(System.lineSeparator()) ? System.lineSeparator() : ",";
		List<String> potentialConductors = Arrays.stream(config.conductorUsernames().split(splitOn))
			.map(String::toLowerCase)
			.map(str -> str.replace(",", "")) // make sure to remove any commas in case people mixed commas and newlines
			.map(str -> str.replace(System.lineSeparator(), "")) // same here
			.collect(Collectors.toList());


		return client.getTopLevelWorldView().players().stream()
			.filter(player -> potentialConductors.contains(player.getName().toLowerCase()))
			.min(Comparator.comparingInt((Player p) -> potentialConductors.indexOf(p.getName().toLowerCase())));
	}

    public static CycleState getPlayerCycleState(KitchenRunningConfig config, Player player) {
        boolean isOnGoodTile = isOnGoodTile(player);
        boolean isFollowingConductor = isFollowingConductor(config, player);

        if (isOnGoodTile && isFollowingConductor)
            return CycleState.FOLLOWING_CONDUCTOR;

        return CycleState.NOT_FOLLOWING_CONDUCTOR;
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

        return actorName.equalsIgnoreCase(config.activeConductor());
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
