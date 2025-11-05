package dev.blonks.kitchenrunning.config;

import dev.blonks.kitchenrunning.utils.HideMode;
import net.runelite.client.config.*;

import java.awt.*;

@ConfigGroup(KitchenRunningConfig.KITCHEN_GROUP)
public interface KitchenRunningConfig extends Config
{
    String KITCHEN_GROUP = "kitchenrunning";

    @ConfigSection(
            name = "Instructions",
            description = "Look here for instructions",
            position = -1
    )
    String instructions = "instructions";

    @ConfigItem(
            keyName = "kitchenRunningInstructions",
            name = "Instructions",
            description = "A set of instructions on how to use this plugin",
            section = instructions
    )
    default String kitchenRunningInstructions() {
        return "1. Stand on one of the 6 tiles that are highlighted in the lumbridge kitchen\n\n" +
                "2. Enter the username of the player who is conducting the agility circle\n\n" +
                "3. Wait for their highlighted tile to pass yours, and then follow them\n\n" +
                "4. Enjoy the xp";
    }

    @ConfigSection(
            name = "General Settings",
            description = "General settings/config options.",
            position = 0
    )
    String generalConfig = "generalConfig";

    @ConfigItem(
            keyName = "conductorUsernames",
            name = "Conductor usernames",
            description = "The username(s) of the player that is leading the kitchen loop (commas or new lines)",
            section = generalConfig
    )
    default String conductorUsernames()
    {
        return "";
    }


	@ConfigItem(
		keyName = "activeConductor",
		name = "",
		description = "",
		hidden = true
	)
	default String activeConductor() {
		return "";
	}
	@ConfigItem(
		keyName = "activeConductor",
		name = "",
		description = "",
		hidden = true
	)
	void activeConductor(String conductorUsername);

	@ConfigItem(
		keyName = "sidebarPriority",
		name = "Sidebar Priority",
		description = "Customize the priority of the panel in the sidebar",
		hidden = true,
		section = generalConfig
	)
	default int sidebarPriority() {
		return 25;
	}


    @ConfigSection(
            name = "Conductor Tile",
            description = "Conductor Tiles Configuration.",
            position = 1
    )
    String conductorTile = "conductorTile";

//    @ConfigItem(
//            keyName = "conductorTile",
//            name = "Conductor tile",
//            description = "Configures when the conductor tile should be drawn",
//            section = conductorTile,
//            position = 0
//    )
//    default CycleState conductorTile() {
//        return CycleState.OUT_OF_CYCLE;
//    }

    @Alpha
    @ConfigItem(
            keyName = "conductorPlayerTileBorder",
            name = "Conductor player tile border",
            description = "Highlights the tile of the conductor",
            section = conductorTile,
            position = 1
    )
    default Color conductorPlayerTileBorder() {
        return Color.CYAN;
    }

    @Alpha
    @ConfigItem(
            keyName = "conductorPlayerTileFill",
            name = "Conductor player tile fill",
            description = "Highlights the tile of the conductor",
            section = conductorTile,
            position = 2
    )
    default Color conductorPlayerTileFill() {
        return new Color(0, 255, 255, 50);
    }


    @ConfigSection(
            name = "Player Tile",
            description = "Player Tile Configuration.",
            position = 2
    )
    String playerTile = "playerTile";

//    @ConfigItem(
//            keyName = "playerTile",
//            name = "Player tile",
//            description = "Configures when the player tile should be drawn",
//            section = playerTile,
//            position = 0
//    )
//    default CycleState playerTile() {
//        return CycleState.OUT_OF_CYCLE;
//    }

    @Alpha
    @ConfigItem(
            keyName = "playerTileBorder",
            name = "Player tile border",
            description = "Highlights the player tile",
            section = playerTile,
            position = 1
    )
    default Color playerTileBorder() {
        return new Color(0, 255, 0, 255);
    }

    @Alpha
    @ConfigItem(
            keyName = "playerTileFill",
            name = "Player tile fill",
            description = "Highlights the player tile",
            section = playerTile,
            position = 2
    )
    default Color playerTileFill() {
        return new Color(0, 255, 0, 50);
    }


    @ConfigSection(
            name = "Starting Tiles",
            description = "Starting Tiles Configuration.",
            position = 3
    )
    String startingTiles = "startingTiles";

//    @ConfigItem(
//            keyName = "startingTiles",
//            name = "Starting tiles",
//            description = "Configures when the starting tiles should be drawn",
//            section = startingTiles,
//            position = 0
//    )
//    default CycleState startingTiles() {
//        return CycleState.OUT_OF_CYCLE;
//    }

    @Alpha
    @ConfigItem(
            keyName = "startingTilesBorder",
            name = "Starting tiles border",
            description = "Highlights the correct starting tiles.",
            section = startingTiles,
            position = 1
    )
    default Color startingTilesBorder() {
        return new Color(255, 255, 255, 255);
    }

    @Alpha
    @ConfigItem(
            keyName = "startingTilesFill",
            name = "Starting tiles fill",
            description = "Highlights the correct starting tiles.",
            section = startingTiles,
            position = 2
    )
    default Color startingTilesFill() {
        return new Color(255, 255, 255, 100);
    }

    @ConfigSection(
            name = "Entity Hider Settings",
            description = "General settings for hiding entities in the area",
            position = 4
    )
    String entityHiderSection = "entityHiderSection";

    @ConfigItem(
            keyName = "hidingMode",
            name = "Hiding mode",
            description = "Configure when other player entities should be hidden so you can find the conductor",
            section = entityHiderSection,
            position = 0
    )
    default HideMode hideOtherEntities() {
        return HideMode.NOT_FOLLOWING_CONDUCTOR;
    }


    @ConfigSection(
            name = "Notification Settings",
            description = "Configuration of different notifications",
            position = 5
    )
    String notificationSettings = "notificationSettings";

    @ConfigItem(
            keyName = "stoppedFollowing",
            name = "Stopped following notification",
            description = "Configures notifications for when you have stopped following the conductor",
            section = notificationSettings,
            position = 0
    )
    default Notification stoppedFollowing() {
        return Notification.ON;
    }

    @ConfigItem(
            keyName = "conductorAlert",
            name = "Conductor alert notification",
            description = "Configures notifications for when the conductor sends out an alert message",
            section =  notificationSettings,
            position = 1
    )
    default Notification conductorAlert() {
        return Notification.OFF;
    }


}
