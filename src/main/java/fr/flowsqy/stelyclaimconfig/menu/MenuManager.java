package fr.flowsqy.stelyclaimconfig.menu;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import fr.flowsqy.abstractmenu.inventory.EventInventory;
import fr.flowsqy.stelyclaim.StelyClaimPlugin;
import fr.flowsqy.stelyclaimconfig.StelyClaimConfigPlugin;
import fr.flowsqy.stelyclaimconfig.menu.loader.MenuLoader;
import fr.flowsqy.stelyclaimconfig.menu.session.PlayerMenuSession;
import fr.flowsqy.stelyclaimconfig.menu.session.state.FlagStateLoader;
import org.bukkit.Bukkit;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;

public class MenuManager {

    private final FlagStateLoader flagStateLoader;
    private final Map<UUID, PlayerMenuSession> playerSessions;
    private final Map<String, FlagItem> flagsItems;
    private final Set<UUID> pauseIds;
    private final List<Integer> flagSlots;
    private final EventInventory inventory;

    public MenuManager(@NotNull StelyClaimConfigPlugin plugin, @NotNull StelyClaimPlugin stelyClaimPlugin, @NotNull Configuration menuConfiguration, @NotNull Function<MenuManager, FlagStateLoader> flagStateLoaderSupplier) {
        this.flagStateLoader = flagStateLoaderSupplier.apply(this);
        playerSessions = new HashMap<>();
        flagSlots = new ArrayList<>(0);
        pauseIds = new HashSet<>();

        final MenuLoader loader = new MenuLoader();

        flagsItems = loader.loadFlagItemById(menuConfiguration);
        inventory = loader.loadInventory(menuConfiguration, plugin, stelyClaimPlugin, this, flagSlots);
        inventory.setCloseCallback(this::onClose);
    }


    /**
     * Open the GUI to a player
     *
     * @param player The player that open the GUI
     * @param region The {@link ProtectedRegion} that will be modified
     */
    public void open(@NotNull Player player, @NotNull ProtectedRegion region) {
        final UUID playerId = player.getUniqueId();
        // Create a session and register it
        if (playerSessions.containsKey(playerId)) {
            removeSession(playerId);
        }
        final PlayerMenuSession session = new PlayerMenuSession(flagStateLoader, inventory, region, flagSlots);
        session.load(player, flagsItems);
        playerSessions.put(playerId, session);
        // Open the inventory
        session.open(player);
    }

    /**
     * Resume a paused session
     *
     * @param player The owner of the session
     */
    public void resume(@NotNull Player player) {
        final PlayerMenuSession session = playerSessions.get(player.getUniqueId());
        if (session == null) {
            throw new IllegalStateException("The session should exist to call this method");
        }
        session.open(player);
    }

    /**
     * Pause a session. Allow closing the inventory without erasing the session
     *
     * @param player The owner of the session
     */
    public void pause(@NotNull Player player) {
        pauseIds.add(player.getUniqueId());
    }

    /**
     * Get the {@link PlayerMenuSession} of a player
     *
     * @param playerId The {@link UUID} of the player
     * @return The {@link PlayerMenuSession}. {@code null} if he does not have a session
     */
    @Nullable
    public PlayerMenuSession getSession(@NotNull UUID playerId) {
        return playerSessions.get(playerId);
    }

    public Map<String, FlagItem> getFlagsItems() {
        return flagsItems;
    }

    /**
     * Remove session
     *
     * @param playerId The owner's id of the session
     */
    private void removeSession(@NotNull UUID playerId) {
        final PlayerMenuSession session = playerSessions.remove(playerId);
        if (session != null) {
            session.close();
        }
    }

    /**
     * Closing listener. Erase the session if needed
     *
     * @param player The session owner
     */
    private void onClose(@NotNull Player player) {
        final UUID playerId = player.getUniqueId();
        if (pauseIds.remove(playerId)) {
            return;
        }
        removeSession(playerId);
    }

    /**
     * Close all sessions
     */
    public void closeAllSessions() {
        pauseIds.clear();
        for (UUID playerUUID : new HashSet<>(playerSessions.keySet())) {
            final Player player = Bukkit.getPlayer(playerUUID);
            if (player != null) {
                player.closeInventory();
            }
        }
        playerSessions.clear();
    }

}
