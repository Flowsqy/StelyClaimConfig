package fr.flowsqy.stelyclaimconfig.menu;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import fr.flowsqy.abstractmenu.factory.MenuFactory;
import fr.flowsqy.abstractmenu.inventory.EventInventory;
import fr.flowsqy.abstractmenu.item.ItemBuilder;
import fr.flowsqy.stelyclaim.io.Messages;
import fr.flowsqy.stelyclaimconfig.StelyClaimConfigPlugin;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MenuManager {

    private final Messages messages;
    private final Map<String, PlayerSession> playerSessions;
    private final List<Integer> slots;
    private final EventInventory inventory;

    public MenuManager(StelyClaimConfigPlugin plugin, YamlConfiguration menuConfiguration) {
        messages = plugin.getMessages();
        playerSessions = new HashMap<>();
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
    }

    private void applySession(InventoryClickEvent event) {
        messages.sendMessage(event.getWhoClicked(), "menu.success");
    }

    private void close(Player player) {
        playerSessions.remove(player.getName());
    }

    private final static class PlayerSession {

        private final List<String> flags;
        private int page;

        public PlayerSession(List<String> flags, int page) {
            this.flags = flags;
            this.page = page;
        }

        public List<String> getFlags() {
            return flags;
        }

        public int getPage() {
            return page;
        }

        public void setPage(int page) {
            this.page = page;
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
                    eventInventory.register(
                            builder,
                            e -> {
                            },// TODO Store somewhere the changes
                            slots
                    );
                    MenuManager.this.slots.addAll(slots);
                    break;
                default:
                    eventInventory.register(
                            builder,
                            slots
                    );
            }
        }

    }

}
