package dev.blonks.kitchenrunning.ui.stats;

import dev.blonks.kitchenrunning.KitchenRunningPlugin;
import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class StatsPanel extends JPanel
{
	private KitchenRunningPlugin plugin;
	private List<JPanel> statPanes;
	private JPanel statPanel;

	public StatsPanel(KitchenRunningPlugin plugin) {
		this.plugin = plugin;
		statPanes = new ArrayList<>();

		statPanel = new JPanel();
		JLabel tempLabel = new JLabel();
		tempLabel.setText("Temp");
		statPanel.add(tempLabel);
		statPanes.add(statPanel);
		add(statPanel, BorderLayout.CENTER);
	}
}
