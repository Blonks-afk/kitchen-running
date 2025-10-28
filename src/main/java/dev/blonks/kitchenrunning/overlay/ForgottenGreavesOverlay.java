package dev.blonks.kitchenrunning.overlay;

import dev.blonks.kitchenrunning.KitchenRunningPlugin;
import dev.blonks.kitchenrunning.config.KitchenRunningConfig;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.TitleComponent;

@Slf4j
public class ForgottenGreavesOverlay extends OverlayPanel
{
	private final KitchenRunningPlugin plugin;

	@Inject
	private ForgottenGreavesOverlay(KitchenRunningPlugin plugin) {
		super(plugin);
		setPosition(OverlayPosition.ABOVE_CHATBOX_RIGHT);
		this.plugin = plugin;
	}

	@Override
	public Dimension render(Graphics2D graphics) {
		boolean enabled = plugin.isEnabled();
		if (!enabled || plugin.isWearingGreaves()) {
			panelComponent.getChildren().clear();
			return super.render(graphics);
		}

		final String missingGreavesMessage = "WARNING: You are not currently wearing Sage's greaves!";
		panelComponent.getChildren().add(TitleComponent.builder()
			.text(missingGreavesMessage)
			.color(Color.RED)
			.build()
		);

		panelComponent.setPreferredSize(new Dimension(
			graphics.getFontMetrics().stringWidth(missingGreavesMessage) + 5,
			0
		));

		return super.render(graphics);
	}
}
