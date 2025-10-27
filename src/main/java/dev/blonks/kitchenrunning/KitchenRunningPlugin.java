package dev.blonks.kitchenrunning;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.*;
import net.runelite.api.gameval.NpcID;
import net.runelite.client.Notifier;
import net.runelite.client.callback.Hooks;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.util.Set;

@Slf4j
@PluginDescriptor(
	name = "Kitchen Runner",
    description = "Helpful utilities for getting in cycle with the Sage's Greaves",
    tags = {"greaves", "leagues", "kitchen", "fountain", "grid", "gridmaster", "grid master"}
)
public class KitchenRunningPlugin extends Plugin
{
    private boolean overlayEnabled = false;

    private static final Set<Integer> THRALL_IDS = ImmutableSet.of(
            NpcID.ARCEUUS_THRALL_GHOST_LESSER, NpcID.ARCEUUS_THRALL_SKELETON_LESSER, NpcID.ARCEUUS_THRALL_ZOMBIE_LESSER,  // Lesser Thrall (ghost, skeleton, zombie)
            NpcID.ARCEUUS_THRALL_GHOST_SUPERIOR, NpcID.ARCEUUS_THRALL_SKELETON_SUPERIOR, NpcID.ARCEUUS_THRALL_ZOMBIE_SUPERIOR,  // Superior Thrall (ghost, skeleton, zombie)
            NpcID.ARCEUUS_THRALL_GHOST_GREATER, NpcID.ARCEUUS_THRALL_SKELETON_GREATER, NpcID.ARCEUUS_THRALL_ZOMBIE_GREATER   // Greater Thrall (ghost, skeleton, zombie)
    );
    private static final Set<Integer> RANDOM_EVENT_NPC_IDS = ImmutableSet.of(
            NpcID.MACRO_BEEKEEPER_INVITATION,
            NpcID.MACRO_COMBILOCK_PIRATE,
            NpcID.MACRO_JEKYLL, NpcID.MACRO_JEKYLL_UNDERWATER,
            NpcID.MACRO_DWARF,
            NpcID.PATTERN_INVITATION,
            NpcID.MACRO_EVIL_BOB_OUTSIDE, NpcID.MACRO_EVIL_BOB_PRISON,
            NpcID.PINBALL_INVITATION,
            NpcID.MACRO_FORESTER_INVITATION,
            NpcID.MACRO_FROG_CRIER, NpcID.MACRO_FROG_GENERIC, NpcID.MACRO_FROG_SULKING, NpcID.MACRO_FROG_NONCOMBAT, NpcID.MACRO_FROG_NOHAT, NpcID.MACRO_FROG_PRIN_HE, NpcID.MACRO_FROG_PRIN_SHE, NpcID.MACRO_FROG_PRIN_A, NpcID.MACRO_FROG_PRIN_B,
            NpcID.MACRO_GENI, NpcID.MACRO_GENI_UNDERWATER,
            NpcID.MACRO_GILES, NpcID.MACRO_GILES_UNDERWATER,
            NpcID.MACRO_GRAVEDIGGER_INVITATION,
            NpcID.MACRO_MILES, NpcID.MACRO_MILES_UNDERWATER,
            NpcID.MACRO_MYSTERIOUS_OLD_MAN, NpcID.MACRO_MYSTERIOUS_OLD_MAN_UNDERWATER,
            NpcID.MACRO_MAZE_INVITATION, NpcID.MACRO_MIME_INVITATION,
            NpcID.MACRO_NILES, NpcID.MACRO_NILES_UNDERWATER,
            NpcID.MACRO_PILLORY_GUARD,
            NpcID.GRAB_POSTMAN,
            NpcID.MACRO_MAGNESON_INVITATION,
            NpcID.MACRO_HIGHWAYMAN, NpcID.MACRO_HIGHWAYMAN_UNDERWATER,
            NpcID.MACRO_SANDWICH_LADY_NPC,
            NpcID.MACRO_DRILLDEMON_INVITATION,
            NpcID.MACRO_COUNTCHECK_SURFACE, NpcID.MACRO_COUNTCHECK_UNDERWATER
    );

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

        if (inKitchen) {
            // have moved into the kitchen

        } else {
            // have left the kitchen
        }


    }

    private boolean shouldDraw(Renderable renderable, boolean b) {
        // always draw others outside of the kitchen
        if (!inKitchen)
            return true;

        if (PlayerPositionUtils.isPvpOrNonLeagues(client)) {
            return true;
        }

        // entity hider settings are disabled
        if (config.hideOtherEntities().equals(CycleState.NONE))
            return true;

        if (renderable instanceof NPC) {
            NPC npc = (NPC) renderable;
            if (RANDOM_EVENT_NPC_IDS.contains(npc.getId()))
                return false;

            if (THRALL_IDS.contains(npc.getId()))
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

            return shouldRender(config, player);
        }

        return true;
    }

    private boolean shouldRender(KitchenRunningConfig config, Player other) {
        boolean playerInCycle = PlayerPositionUtils.isInCycle(config, client.getLocalPlayer());
        boolean otherInCycle = PlayerPositionUtils.isInCycle(config, other);

        CycleState playerCycleConfig = config.hideOtherEntities();

        // guard statement that returns when the current player state is not in line with when entities should hide
        if (playerCycleConfig.equals(CycleState.IN_CYCLE) && playerInCycle) {
            return false;
        }

        if (playerCycleConfig.equals(CycleState.OUT_OF_CYCLE) && !playerInCycle) {
            return false;
        }

        if (playerCycleConfig.equals(CycleState.BOTH)) {
            return false;
        }

        if (playerCycleConfig.equals(CycleState.NONE)) {
            return true;
        }

        return true;
    }



    private void disableOverlay() {
        if (overlayEnabled)
            overlayEnabled = false;
    }


}
