package dev.blonks.kitchenrunning;

import com.google.inject.Provides;
import dev.blonks.kitchenrunning.config.KitchenRunningConfig;
import dev.blonks.kitchenrunning.overlay.ForgottenGreavesOverlay;
import dev.blonks.kitchenrunning.overlay.KitchenRunningOverlay;
import dev.blonks.kitchenrunning.ui.MainPanel;
import dev.blonks.kitchenrunning.utils.HideMode;
import dev.blonks.kitchenrunning.utils.InventoryUtils;
import dev.blonks.kitchenrunning.utils.KitchenRunningConstants;
import dev.blonks.kitchenrunning.utils.PlayerPositionUtils;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.*;
import net.runelite.api.gameval.InventoryID;
import net.runelite.client.Notifier;
import net.runelite.client.callback.Hooks;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import net.runelite.client.util.ImageUtil;

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
	private ClientToolbar clientToolbar;
	private NavigationButton navButton;
	private MainPanel mainPanel;

	@Inject
	private KitchenRunningConfig config;

    @Inject
    private OverlayManager overlayManager;

	@Inject
	private KitchenRunningOverlay tileOverlay;

	@Inject
	private ForgottenGreavesOverlay greavesWarningOverlay;

    @Inject
    private Notifier notifier;

    @Inject
    private Hooks hooks;
    private final Hooks.RenderableDrawListener renderableDrawListener = this::shouldDraw;

    private IndexedObjectSet<? extends Player> localPlayers;

	@Getter
	private boolean inKitchen = false;
	@Getter
	private boolean wasFollowingConductor = false;
	/**
	 * Main state tracking variable for when features should be enabled.<br/>
	 * When true, this means that the player is on leagues/grid and in the kitchen.<br/>
	 * When false, this would mean that NO features of this plugin should execute.
	 */
	@Getter
	private boolean enabled = false;
	/**
	 * A more granular flag than {@link #enabled}. This will always be false when {@link #enabled} is false,
	 * but there may be cases where some features should be disabled to not disrupt players' experience,
	 * such as disabling entity hiding if they are ready to start running, but no conductor can be found.
	 */
	@Getter
	private boolean conductorNearby = false;
	@Getter
	private boolean loggedIn = false;
	@Getter
	private boolean wearingGreaves = true;
	@Getter
	private boolean forgotGreaves = false;

    @Provides
    KitchenRunningConfig provideConfig(ConfigManager configManager)
    {
        return configManager.getConfig(KitchenRunningConfig.class);
    }

    @Override
    protected void startUp() throws Exception
    {
		updateState();
        overlayManager.add(tileOverlay);
		overlayManager.add(greavesWarningOverlay);
        hooks.registerRenderableDrawListener(renderableDrawListener);
		mainPanel = new MainPanel(this);
		navButton = createNavButton(config.sidebarPriority());
		clientToolbar.addNavigation(navButton);
    }

    @Override
    protected void shutDown() throws Exception {
		updateState();
        overlayManager.remove(tileOverlay);
		overlayManager.remove(greavesWarningOverlay);
        hooks.unregisterRenderableDrawListener(renderableDrawListener);
		clientToolbar.removeNavigation(navButton);
    }

	@Subscribe
	public void onConfigChanged(ConfigChanged e) {
		if (e.getKey().equalsIgnoreCase("sidebarPriority")) {
			if (e.getNewValue() == null)
				return;

			NavigationButton newNavButton = createNavButton(Integer.valueOf(e.getNewValue()));
			clientToolbar.removeNavigation(navButton);
			navButton = newNavButton;
			clientToolbar.addNavigation(navButton);
		}
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged e) {
		if (e.getGameState() == GameState.LOGIN_SCREEN) {
			updateState();
		}
	}

    @Subscribe
    public void onGameTick(GameTick e) {
		updateState();
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

	/**
	 * Sets {@link #inKitchen} boolean based on player location<br/>
	 * Sets {@link #wasFollowingConductor}  and {@link #enabled} to false when not in kitchen<br/>
	 * Sets {@link #conductorNearby} if player is in kitchen based on whether the conductor is in the kitchen<br/>
	 */
	private void updateState() {
		// Completely wipe state variables if the user is not logged in
		if (client.getGameState() != GameState.LOGGED_IN) {
			resetStateVariables();
			return;
		}
		loggedIn = true;

		// Completely wipe state variables if the user is on a non-leagues/gridmaster world
		if (PlayerPositionUtils.isPvpOrNonLeagues(client)) {
			resetStateVariables();
			return;
		}

		/* Completely wipe state variables if the user IS logged in and on a leagues/gridmaster world, but not
		 * currently looking to train agility in the kitchen
		 */
		if (!PlayerPositionUtils.isInKitchen(client)) {
			resetStateVariables();
			return;
		}
		inKitchen = true;
		wearingGreaves = InventoryUtils.itemContainerContainsGreaves(client, InventoryID.WORN, true);
		forgotGreaves = InventoryUtils.itemContainerContainsGreaves(client, InventoryID.INV, false);
		enabled = true;
		conductorNearby = PlayerPositionUtils.isConductorNearby(client, config);
	}

	private void resetStateVariables() {
		inKitchen = false;
		conductorNearby = false;
		wasFollowingConductor = false;
		enabled = false;
		wearingGreaves = true;
		forgotGreaves = false;
	}

    private boolean shouldDraw(Renderable renderable, boolean b) {
		if (!enabled)
			return true;

		// Always render when the specified conductor is not around
        if (!conductorNearby)
			return true;

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

	private NavigationButton createNavButton(int priority) {
		return NavigationButton.builder()
			.tooltip("Kitchen Running")
			.icon(ImageUtil.loadImageResource(getClass(), "/greaves.png"))
			.panel(mainPanel)
			.priority(priority)
			.build();
	}
}
