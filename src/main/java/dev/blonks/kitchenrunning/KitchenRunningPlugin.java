package dev.blonks.kitchenrunning;

import com.google.inject.Provides;
import dev.blonks.kitchenrunning.config.KitchenRunningConfig;
import dev.blonks.kitchenrunning.overlay.KitchenRunningOverlay;
import dev.blonks.kitchenrunning.utils.CycleState;
import dev.blonks.kitchenrunning.utils.HideMode;
import dev.blonks.kitchenrunning.utils.KitchenRunningConstants;
import dev.blonks.kitchenrunning.utils.PlayerPositionUtils;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.*;
import net.runelite.client.Notifier;
import net.runelite.client.callback.Hooks;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;

@Slf4j
@PluginDescriptor(
	name = "Kitchen Running",
    description = "Helpful utilities for getting in cycle with the Sage's Greaves",
    tags = {"greaves", "leagues", "kitchen", "fountain", "grid", "gridmaster", "grid master", "table", "run", "running", "agility"}
)
public class KitchenRunningPlugin extends Plugin
{
    @Inject
	private Client client;

	@Inject
	private KitchenRunningConfig config;

    @Inject
    private OverlayManager overlayManager;

    @Inject
    private Notifier notifier;

    @Inject
    private Hooks hooks;
    private final Hooks.RenderableDrawListener renderableDrawListener = this::shouldDraw;

    private IndexedObjectSet<? extends Player> localPlayers;

    @Inject
    private KitchenRunningOverlay overlay;

    private boolean inKitchen = false;
	private boolean wasFollowingConductor = false;
	private boolean enabled = false;

    @Provides
    KitchenRunningConfig provideConfig(ConfigManager configManager)
    {
        return configManager.getConfig(KitchenRunningConfig.class);
    }

    @Override
    protected void startUp() throws Exception
    {
        log.info("startup");
        checkLocation();
        overlayManager.add(overlay);
        hooks.registerRenderableDrawListener(renderableDrawListener);
    }

    @Override
    protected void shutDown() throws Exception {
        overlayManager.remove(overlay);
        hooks.unregisterRenderableDrawListener(renderableDrawListener);
    }

    @Subscribe
    public void onGameTick(GameTick e) {
        checkLocation();
    }

    @Subscribe
    public void onInteractingChanged(InteractingChanged e) {
        Player local = client.getLocalPlayer();
        if (local == null)
            return;

        if (!local.equals(e.getSource()))
            return;


		boolean isFollowingConductor = PlayerPositionUtils.isFollowingConductor(config, local);
		if (wasFollowingConductor && !isFollowingConductor)
		{
			wasFollowingConductor = false;
			notifier.notify(config.stoppedFollowing(), "You are no longer following the conductor!");
		} else if (isFollowingConductor)
		{
			wasFollowingConductor = true;
		}
    }

    @Subscribe
    public void onChatMessage(ChatMessage e) {
        if (!e.getType().equals(ChatMessageType.PUBLICCHAT))
            return;

        if (e.getName() == null)
            return;

        if (!inKitchen)
            return;

        String senderName = e.getName();
        senderName = senderName.substring(Math.max(senderName.indexOf('>')+1, 1));
        if (senderName.equalsIgnoreCase(config.conductorUsername())) {
            if (e.getMessage().toLowerCase().startsWith("alert:"))
                notifier.notify(config.conductorAlert(), "The conductor says: " + e.getMessage().toLowerCase().replace("alert:", ""));
        }
    }

    private void checkLocation() {
        boolean newIsKitchen = PlayerPositionUtils.isInKitchen(client);

        if (newIsKitchen == inKitchen)
            return;

        inKitchen = newIsKitchen;
    }

    private boolean shouldDraw(Renderable renderable, boolean b) {
        // always draw others outside the kitchen
        if (!inKitchen)
            return true;

        if (PlayerPositionUtils.isPvpOrNonLeagues(client)) {
            return true;
        }

        // entity hider settings are disabled
        if (config.hideOtherEntities().equals(HideMode.NEVER))
            return true;

        if (renderable instanceof NPC) {
            NPC npc = (NPC) renderable;
            if (KitchenRunningConstants.RANDOM_EVENT_NPC_IDS.contains(npc.getId()))
                return false;

            if (KitchenRunningConstants.THRALL_IDS.contains(npc.getId()))
                return false;
        }

        if (renderable instanceof Player) {
            Player player = (Player) renderable;

            // draw the local player
            if (player.equals(client.getLocalPlayer()))
                return true;

            // draw the conductor
            if (config.conductorUsername() != null && player.getName() != null
                    && player.getName().equalsIgnoreCase(config.conductorUsername()))
                return true;

            return shouldRenderOthers(config, player);
        }

        return true;
    }

    private boolean shouldRenderOthers(KitchenRunningConfig config, Player other) {
        boolean playerInCycle = PlayerPositionUtils.isInCycle(config, client.getLocalPlayer());
        boolean otherInCycle = PlayerPositionUtils.isInCycle(config, other);

        HideMode playerCycleConfig = config.hideOtherEntities();

        // Hide other entities when player is following and config is set to hide on following
        if (playerCycleConfig.equals(HideMode.FOLLOWING_CONDUCTOR) && playerInCycle) {
            return false;
        }

		// Hide other entities when player is not following and config is set to hide on not following
        if (playerCycleConfig.equals(HideMode.NOT_FOLLOWING_CONDUCTOR) && !playerInCycle) {
            return false;
        }

		// Hide other entities when config is set to always hide
        if (playerCycleConfig.equals(HideMode.ALWAYS)) {
            return false;
        }

		// Don't hide when config is set to never hide
        if (playerCycleConfig.equals(HideMode.NEVER)) {
            return true;
        }

        return true;
    }
}
