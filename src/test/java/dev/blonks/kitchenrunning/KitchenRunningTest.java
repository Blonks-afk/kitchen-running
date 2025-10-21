package dev.blonks.kitchenrunning;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class KitchenRunningTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(KitchenRunningPlugin.class);
		RuneLite.main(args);
	}
}