package dev.blonks.kitchenrunning.ui;

import dev.blonks.kitchenrunning.KitchenRunningPlugin;
import dev.blonks.kitchenrunning.ui.dashboard.DashboardPanel;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.ui.PluginPanel;

@Slf4j
public class MainPanel extends PluginPanel
{
	private KitchenRunningPlugin plugin;
	private final JComboBox<String> viewSelector;
	private final JPanel contentPanel;
	private final CardLayout cardLayout;

	public MainPanel(KitchenRunningPlugin plugin) {
		super();
		setLayout(new BorderLayout());

		String[] views = { "Dashboard" };
		viewSelector = new JComboBox<>(views);
		viewSelector.setFocusable(false);
		viewSelector.addActionListener(e -> switchView());

		JPanel topPanel = new JPanel(new BorderLayout());
		topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		topPanel.add(viewSelector, BorderLayout.CENTER);

		add(topPanel, BorderLayout.NORTH);

		cardLayout = new CardLayout();
		contentPanel = new JPanel(cardLayout);

		contentPanel.add("Dashboard", new DashboardPanel(plugin));

		add(contentPanel, BorderLayout.CENTER);
	}

	private void switchView() {
		String selected = (String) viewSelector.getSelectedItem();
		if (selected == null)
			selected = "Dashboard";

		cardLayout.show(contentPanel, selected);
	}
}
