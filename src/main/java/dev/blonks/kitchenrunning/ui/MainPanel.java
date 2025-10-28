package dev.blonks.kitchenrunning.ui;

import dev.blonks.kitchenrunning.KitchenRunningPlugin;
import dev.blonks.kitchenrunning.config.KitchenRunningConfig;
import java.awt.BorderLayout;
import javax.swing.JPanel;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.ui.PluginPanel;

@Slf4j
public class MainPanel extends PluginPanel
{
	private KitchenRunningPlugin plugin;

	public MainPanel(KitchenRunningPlugin plugin) {
		this.plugin = plugin;

		JPanel panel = new JPanel();

		add(panel, BorderLayout.CENTER);
	}
}
