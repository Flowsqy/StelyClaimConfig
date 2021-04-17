package fr.flowsqy.stelyclaimconfig.menu;

import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.internal.permission.RegionPermissionModel;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import fr.flowsqy.abstractmenu.factory.MenuFactory;
import fr.flowsqy.abstractmenu.inventory.EventInventory;
import fr.flowsqy.abstractmenu.item.CreatorAdaptor;
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
import java.util.function.Function;
import java.util.stream.Collectors;

public class MenuManager {

    private final Messages messages;
    private final Map<String, PlayerSession> playerSessions;
    private final Map<String, List<String>> regionPlayers;
    private final Map<String, ItemBuilder> flagsItems;
    private final List<Integer> slots;
    private final EventInventory inventory;

    public MenuManager(StelyClaimConfigPlugin plugin, YamlConfiguration menuConfiguration) {
        messages = plugin.getMessages();
        playerSessions = new HashMap<>();
        regionPlayers = new HashMap<>();
        flagsItems = new HashMap<>();
        final ConfigurationSection itemSection = menuConfiguration.getConfigurationSection("items");
        if (itemSection != null)
            fillFlagsItems(itemSection);
        slots = new ArrayList<>(0);
        final MenuFactory factory = new MenuFactory(plugin);
        final ConfigurationSection menuSection = menuConfiguration.getConfigurationSection("menu");
        if (menuSection == null)
            inventory = new EventInventory(factory, "", 1);
        else
            inventory = EventInventory.deserialize(
                    menuSection,
                    factory,
                    new CustomRegisterHandle()
            );
        inventory.setCloseCallback(this::close);
    }

    private void fillFlagsItems(ConfigurationSection section) {
        for (String sectionKey : section.getKeys(false)) {
            final ConfigurationSection itemSection = section.getConfigurationSection(sectionKey);
            assert itemSection != null;
            final ItemBuilder itemBuilder = ItemBuilder.deserialize(itemSection);
            flagsItems.put(sectionKey, itemBuilder);
        }
    }

    public void open(Player player, ProtectedRegion region) {
        final PlayerSession session = new PlayerSession(calculateFlags(player, region), region.getId(), 1);
        playerSessions.put(player.getName(), session);
        final List<String> inventories = regionPlayers.computeIfAbsent(region.getId(), k -> new ArrayList<>());
        inventories.add(player.getName());
        session.generatePageItem();
        inventory.open(player, player.getName());
        session.clearPageItem();
    }

    private List<String> calculateFlags(Player player, ProtectedRegion region) {
        final List<String> flags = new ArrayList<>();
        final RegionPermissionModel model = new RegionPermissionModel(WorldGuardPlugin.inst().wrapPlayer(player));
        for (Flag<?> flag : WorldGuard.getInstance().getFlagRegistry()) {
            if (flag instanceof StateFlag && model.maySetFlag(region, flag)) {
                flags.add(flag.getName());
            }
        }
        return flags;
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

    private void changePage(InventoryClickEvent event, Function<Integer, Integer> modifier) {
        if (event.getCurrentItem() == null)
            return;
        final Player player = (Player) event.getWhoClicked();
        final PlayerSession session = playerSessions.get(player.getName());
        if (session == null)
            return;
        session.setPage(modifier.apply(session.getPage()));
        session.generatePageItem();
        inventory.refresh(player.getName(), player);
        session.clearPageItem();
    }

    private void handleFlagClick(InventoryClickEvent event) {

    }

    private final class PlayerSession {

        private final List<String> flags;
        private final String sessionId;
        private final Map<String, Boolean> changes;
        private int page;
        private Iterator<String> pageItems;

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

        public Iterator<String> getPageItems() {
            return pageItems;
        }

        public int numberOfPage() {
            final int modulo = flags.size() % slots.size();
            return (flags.size() - modulo) / slots.size() + (modulo > 0 ? 1 : 0);
        }

        public void generatePageItem() {
            final List<String> items = new ArrayList<>();
            if (!flags.isEmpty()) {
                // Fill the temp item list
                for (int index = (page - 1) * slots.size(), i = 0; index < flags.size() && i < slots.size(); index++, i++) {
                    items.add(flags.get(index));
                }
            }
            pageItems = items.iterator();
        }

        public void clearPageItem() {
            pageItems = null;
        }

    }

    private final class FlagCreatorListener implements CreatorListener {

        final Map<String, ItemBuilder> createSession;

        public FlagCreatorListener() {
            createSession = new HashMap<>();
        }

        @Override
        public void open(Player player) {
            final PlayerSession session = playerSessions.get(player.getName());
            if (session == null)
                return;
            final String nextId = session.getPageItems().hasNext() ? session.getPageItems().next() : null;
            final ItemBuilder item = nextId == null ? null : flagsItems.get(nextId);
            createSession.put(player.getName(), item);
        }

        @Override
        public void close(Player player) {
            createSession.remove(player.getName());
        }

        @Override
        public String handleName(Player player, String name) {
            final ItemBuilder builder = createSession.get(player.getName());
            return builder != null ? builder.name() : name;
        }

        @Override
        public List<String> handleLore(Player player, List<String> lore) {
            final ItemBuilder builder = createSession.get(player.getName());
            return builder != null ? builder.lore() : lore;
        }

        @Override
        public boolean handleUnbreakable(Player player, boolean unbreakable) {
            final ItemBuilder builder = createSession.get(player.getName());
            return builder != null ? builder.unbreakable() : unbreakable;
        }

        @Override
        public Material handleMaterial(Player player, Material material) {
            final ItemBuilder builder = createSession.get(player.getName());
            return builder != null ? builder.material() : material;
        }

        @Override
        public int handleAmount(Player player, int amount) {
            final ItemBuilder builder = createSession.get(player.getName());
            return builder != null ? builder.amount() : amount;
        }

        @Override
        public Map<Enchantment, Integer> handleEnchants(Player player, Map<Enchantment, Integer> enchants) {
            final ItemBuilder builder = createSession.get(player.getName());
            return builder != null ? builder.enchants() : enchants;
        }

        @Override
        public Set<ItemFlag> handleFlags(Player player, Set<ItemFlag> flags) {
            final ItemBuilder builder = createSession.get(player.getName());
            return builder != null ? builder.flags() : flags;
        }

        @Override
        public Map<Attribute, AttributeModifier> handleAttributes(Player player, Map<Attribute, AttributeModifier> attributes) {
            final ItemBuilder builder = createSession.get(player.getName());
            return builder != null ? builder.attributes() : attributes;
        }

        @Override
        public String handleHeadDataTextures(Player player, String textures) {
            final ItemBuilder builder = createSession.get(player.getName());
            return builder != null ? builder.headDataTexture() : textures;
        }

        @Override
        public String handleHeadDataSignature(Player player, String signature) {
            final ItemBuilder builder = createSession.get(player.getName());
            return builder != null ? builder.headDataSignature() : signature;
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
                    builder.creatorListener(new CreatorAdaptor() {
                        @Override
                        public List<String> handleLore(Player player, List<String> lore) {
                            if (lore == null)
                                return null;
                            final PlayerSession session = playerSessions.get(player.getName());
                            final String page = String.valueOf(session == null ? 1 : session.getPage());
                            return lore.stream()
                                    .map(line -> line.replace("%page%", page))
                                    .collect(Collectors.toList());
                        }
                    });
                    eventInventory.register(builder, slots);
                    break;
                case "previous":
                    builder.creatorListener(new CreatorAdaptor() {
                        @Override
                        public Material handleMaterial(Player player, Material material) {
                            final PlayerSession session = playerSessions.get(player.getName());
                            if (session == null)
                                return null;
                            return session.getPage() < 2 ? null : material;
                        }
                    });
                    eventInventory.register(
                            builder,
                            event -> changePage(event, page -> page - 1),
                            slots
                    );
                    break;
                case "next":
                    builder.creatorListener(new CreatorAdaptor() {
                        @Override
                        public Material handleMaterial(Player player, Material material) {
                            final PlayerSession session = playerSessions.get(player.getName());
                            if (session == null)
                                return null;
                            return session.getPage() >= session.numberOfPage() ? null : material;
                        }
                    });
                    eventInventory.register(
                            builder,
                            event -> changePage(event, page -> page + 1),
                            slots
                    );
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
