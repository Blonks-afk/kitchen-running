package dev.blonks.kitchenrunning.utils;

import net.runelite.api.Client;
import net.runelite.api.Item;
import net.runelite.api.ItemContainer;
import net.runelite.api.ItemID;
import net.runelite.api.InventoryID;

public class InventoryUtils
{
	public static boolean itemContainerContainsGreaves(Client client, int inventoryID, boolean defaultBool) {
		ItemContainer container = client.getItemContainer(inventoryID);
		if (container == null) {
			return defaultBool;
		}

		for (Item item : container.getItems()) {
			if (item.getId() == ItemID.SAGES_GREAVES) {
				return true;
			}
		}
		return false;
	}
}
