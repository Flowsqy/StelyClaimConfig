package fr.flowsqy.stelyclaimconfig.menu;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import fr.flowsqy.abstractmenu.factory.MenuFactory;
import fr.flowsqy.abstractmenu.inventory.EventInventory;
import fr.flowsqy.abstractmenu.item.CreatorListener;
import fr.flowsqy.abstractmenu.item.ItemBuilder;
import fr.flowsqy.stelyclaim.io.Messages;
import fr.flowsqy.stelyclaimconfig.StelyClaimConfigPlugin;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;

import java.util.*;

public class MenuManager {

    private final Messages messages;
    private final Map<String, PlayerSession> playerSessions;
    private final Map<String, List<String>> regionPlayers;
    private final List<Integer> slots;
    private final EventInventory inventory;

    public MenuManager(StelyClaimConfigPlugin plugin, YamlConfiguration menuConfiguration) {
        messages = plugin.getMessages();
        playerSessions = new HashMap<>();
        regionPlayers = new HashMap<>();
        slots = new ArrayList<>(0);
        final MenuFactory factory = new MenuFactory(plugin);
        final ConfigurationSection section = menuConfiguration.getConfigurationSection("menu");
        if (section == null)
            inventory = new EventInventory(factory, "", 1);
        else
            inventory = EventInventory.deserialize(
                    section,
                    factory,
                    new CustomRegisterHandle()
            );
        inventory.setCloseCallback(this::close);
    }

    public void open(Player player, ProtectedRegion region) {
        inventory.open(player, player.getName());
        playerSessions.put(player.getName(), new PlayerSession(calculateFlags(player), region.getId(), 1));
        final List<String> inventories = regionPlayers.computeIfAbsent(region.getId(), k -> new ArrayList<>());
        inventories.add(player.getName());
    }

    private List<String> calculateFlags(Player player) {
        // TODO Fill with allowed flags
        return new ArrayList<>();
    }

    private void applySession(InventoryClickEvent event) {
        messages.sendMessage(event.getWhoClicked(), "menu.success");
    }

    private void close(Player player) {
        final PlayerSession session = playerSessions.remove(player.getName());
        if (session != null) {
            final List<String> players = regionPlayers.get(session.getSessionId());
            players.remove(player.getName());
            if (players.size() == 0) {
                regionPlayers.remove(session.getSessionId());
            }
        }
    }

    private void handleFlagClick(InventoryClickEvent event) {

    }

    private final static class PlayerSession {

        private final List<String> flags;
        private final String sessionId;
        private final Map<String, Boolean> changes;
        private int page;

        public PlayerSession(List<String> flags, String sessionId, int page) {
            this.flags = flags;
            this.sessionId = sessionId;
            this.changes = new HashMap<>();
            this.page = page;
        }

        public List<String> getFlags() {
            return flags;
        }

        public String getSessionId() {
            return sessionId;
        }

        public Map<String, Boolean> getChanges() {
            return changes;
        }

        public int getPage() {
            return page;
        }

        public void setPage(int page) {
            this.page = page;
        }
    }

    private final class FlagCreatorListener implements CreatorListener {

        @Override
        public String handleName(String name) {
            return null;
        }

        @Override
        public List<String> handleLore(List<String> lore) {
            return null;
        }

        @Override
        public boolean handleUnbreakable(boolean unbreakable) {
            return false;
        }

        @Override
        public Material handleMaterial(Material material) {
            return null;
        }

        @Override
        public int handleAmount(int amount) {
            return 0;
        }

        @Override
        public Map<Enchantment, Integer> handleEnchants(Map<Enchantment, Integer> enchants) {
            return null;
        }

        @Override
        public Set<ItemFlag> handleFlags(Set<ItemFlag> flags) {
            return null;
        }

        @Override
        public Map<Attribute, AttributeModifier> handleAttributes(Map<Attribute, AttributeModifier> attributes) {
            return null;
        }
    }

    private final class CustomRegisterHandle implements EventInventory.RegisterHandler {

        @Override
        public void handle(EventInventory eventInventory, String key, ItemBuilder builder, List<Integer> slots) {
            switch (key) {
                case "valid":
                    eventInventory.register(
                            builder,
                            e -> {
                                applySession(e);
                                e.getWhoClicked().closeInventory();
                            },
                            slots
                    );
                    break;
                case "leave":
                    eventInventory.register(
                            builder,
                            e -> e.getWhoClicked().closeInventory(),
                            slots
                    );
                    break;
                case "flags":
                    builder.creatorListener(new FlagCreatorListener());
                    eventInventory.register(
                            builder,
                            MenuManager.this::handleFlagClick,
                            slots
                    );
                    MenuManager.this.slots.addAll(slots);
                    break;
                case "info":
                default:
                    eventInventory.register(
                            builder,
                            slots
                    );
            }
        }

    }

}
