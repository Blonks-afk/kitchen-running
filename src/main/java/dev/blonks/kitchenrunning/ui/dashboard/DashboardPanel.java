package dev.blonks.kitchenrunning.ui.dashboard;

import dev.blonks.kitchenrunning.KitchenRunningPlugin;
import dev.blonks.kitchenrunning.ui.stats.StatsPanel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class DashboardPanel extends JPanel
{
	private KitchenRunningPlugin plugin;
	private StatsPanel statsPanel;

	public DashboardPanel(KitchenRunningPlugin plugin) {
		super();
		this.plugin = plugin;

		statsPanel = new StatsPanel(plugin);
		add(statsPanel, BorderLayout.CENTER);
	}
}
