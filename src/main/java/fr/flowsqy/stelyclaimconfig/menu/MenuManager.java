package fr.flowsqy.stelyclaimconfig.menu;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import fr.flowsqy.abstractmenu.inventory.EventInventory;
import fr.flowsqy.stelyclaim.StelyClaimPlugin;
import fr.flowsqy.stelyclaimconfig.StelyClaimConfigPlugin;
import fr.flowsqy.stelyclaimconfig.menu.loader.MenuLoader;
import fr.flowsqy.stelyclaimconfig.menu.session.PlayerMenuSession;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.util.*;

public class MenuManager {

    private final Map<UUID, PlayerMenuSession> playerSessions;
    private final Map<String, FlagItem> flagsItems;
    private final List<Integer> flagSlots;
    private final EventInventory inventory;

    public MenuManager(StelyClaimConfigPlugin plugin, StelyClaimPlugin stelyClaimPlugin, YamlConfiguration menuConfiguration) {
        playerSessions = new HashMap<>();
        flagSlots = new ArrayList<>(0);

        final MenuLoader loader = new MenuLoader();

        flagsItems = loader.loadFlagItemById(menuConfiguration);
        inventory = loader.loadInventory(menuConfiguration, plugin, stelyClaimPlugin, this, flagSlots);
        // inventory.setCloseCallback(this::removeSession);
    }


    /**
     * Open the GUI to a player
     *
     * @param player The player that open the GUI
     * @param region The {@link ProtectedRegion} that will be modified
     */
    public void open(Player player, ProtectedRegion region) {
        // Create a session and register it
        if (playerSessions.containsKey(player.getUniqueId())) {
            removeSession(player);
        }
        final PlayerMenuSession session = new PlayerMenuSession(inventory, region, flagSlots);
        session.load(player, flagsItems);
        playerSessions.put(player.getUniqueId(), session);
        // Open the inventory
        session.open(player);
    }

    public void reopen(Player player, ProtectedRegion region) {
        final PlayerMenuSession session = playerSessions.get(player.getUniqueId());
        if (session == null) {
            open(player, region);
            return;
        }
        session.open(player);
    }

    /**
     * Get the {@link PlayerMenuSession} of a player
     *
     * @param playerId The {@link UUID} of the player
     * @return The {@link PlayerMenuSession}. {@code null} if he does not have a session
     */
    public PlayerMenuSession getSession(UUID playerId) {
        return playerSessions.get(playerId);
    }

    public Map<String, FlagItem> getFlagsItems() {
        return flagsItems;
    }

    /**
     * Remove session
     *
     * @param player The owner of the session
     */
    private void removeSession(Player player) {
        playerSessions.remove(player.getUniqueId());
    }

    /**
     * Close all sessions
     */
    public void closeAllSessions() {
        for (UUID playerUUID : new HashSet<>(playerSessions.keySet())) {
            final Player player = Bukkit.getPlayer(playerUUID);
            if (player != null) {
                player.closeInventory();
            }
        }
    }

}
