package dev.blonks.kitchenrunning;

import com.google.common.collect.ImmutableSet;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;

import java.util.Set;

public class KitchenRunnerConstants {
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
}
