package fr.flowsqy.stelyclaimconfig.menu;

import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.internal.permission.RegionPermissionModel;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
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
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class MenuManager {

    private final Messages messages;
    private final Map<String, PlayerSession> playerSessions;
    private final Map<String, ItemBuilder> flagsItems;
    private final List<Integer> slots;
    private final EventInventory inventory;

    public MenuManager(StelyClaimConfigPlugin plugin, YamlConfiguration menuConfiguration) {
        messages = plugin.getMessages();
        playerSessions = new HashMap<>();
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
        final PlayerSession session = new PlayerSession(region, calculateFlags(player, region), 1);
        playerSessions.put(player.getName(), session);
        session.initFlagStates();
        session.generatePageItem();
        final Inventory bukkitInventory = inventory.open(player, player.getName());
        session.clearPageItem();
        session.applyStates(bukkitInventory, true);
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
        final Player player = (Player) event.getWhoClicked();
        messages.sendMessage(player, "menu.success");
        final PlayerSession session = playerSessions.get(player.getName());
        if (session == null)
            return;
        final ProtectedRegion region = session.getRegion();
        final FlagRegistry registry = WorldGuard.getInstance().getFlagRegistry();
        for (Map.Entry<String, Boolean> entry : session.getFlagsStates().entrySet()) {
            final Flag<?> flag = registry.get(entry.getKey());
            if (!(flag instanceof StateFlag))
                continue;
            final StateFlag stateFlag = (StateFlag) flag;
            final boolean value = entry.getValue();
            if (value && region.getFlag(stateFlag) == StateFlag.State.ALLOW)
                continue;
            if (
                    (stateFlag.getDefault() == StateFlag.State.ALLOW && value)
                            || (stateFlag.getDefault() == null && !value)
            ) {
                region.setFlag(stateFlag, null);
            } else {
                region.setFlag(stateFlag, value ? StateFlag.State.ALLOW : StateFlag.State.DENY);
            }
        }
    }

    private void close(Player player) {
        playerSessions.remove(player.getName());
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
        session.applyStates(event.getClickedInventory(), false);
    }

    private void handleFlagClick(InventoryClickEvent event) {
        if (event.getCurrentItem() == null)
            return;
        final PlayerSession session = playerSessions.get(event.getWhoClicked().getName());
        if (session == null)
            return;
        final String flagId = session.flagId(event.getSlot());
        if (flagId == null)
            return;
        final Map<String, Boolean> states = session.getFlagsStates();
        final Boolean value = states.computeIfPresent(flagId, (k, v) -> !v);
        applyState(event.getCurrentItem(), value, false);
    }

    private void toggleAll(InventoryClickEvent event, boolean state) {
        final PlayerSession session = playerSessions.get(event.getWhoClicked().getName());
        if (session == null)
            return;
        final Map<String, Boolean> flagStates = session.getFlagsStates();
        for (String key : flagStates.keySet().toArray(new String[0])) {
            flagStates.put(key, state);
        }
        session.applyStates(event.getInventory(), false);
    }

    private void applyState(ItemStack itemStack, Boolean state, boolean init) {
        if (state == null || !state) {
            if (!init)
                itemStack.removeEnchantment(Enchantment.LUCK);
        } else {
            itemStack.addUnsafeEnchantment(Enchantment.LUCK, 1);
        }
    }

    private final class PlayerSession {

        private final ProtectedRegion region;
        private final List<String> flags;
        private final Map<String, Boolean> flagsStates;
        private int page;
        private Iterator<String> pageItems;

        public PlayerSession(ProtectedRegion region, List<String> flags, int page) {
            this.region = region;
            this.flags = flags;
            this.flagsStates = new HashMap<>();
            this.page = page;
        }

        public ProtectedRegion getRegion() {
            return region;
        }

        public Map<String, Boolean> getFlagsStates() {
            return flagsStates;
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

        public void initFlagStates() {
            for (String flagName : flags) {
                final Flag<?> flag = WorldGuard.getInstance().getFlagRegistry().get(flagName);
                if (!(flag instanceof StateFlag))
                    return;
                final StateFlag stateFlag = (StateFlag) flag;
                final StateFlag.State value = region.getFlag(stateFlag);
                flagsStates.put(flagName, (value == null ? stateFlag.getDefault() : value) == StateFlag.State.ALLOW);
            }
        }

        public void applyStates(Inventory inventory, boolean init) {
            for (int index = (page - 1) * slots.size(), i = 0; index < flags.size() && i < slots.size(); index++, i++) {
                final ItemStack item = inventory.getItem(slots.get(i));
                if (item == null)
                    return;
                final Boolean value = flagsStates.get(flags.get(index));
                applyState(item, value, init);
            }
        }

        public String flagId(int slot) {
            return flags.get((page - 1) * slots.size() + slots.indexOf(slot));
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

        private final ItemBuilder EMPTY = new ItemBuilder();
        private String flagId;
        private ItemBuilder builder;

        public FlagCreatorListener() {
        }

        @Override
        public void open(Player player) {
            final PlayerSession session = playerSessions.get(player.getName());
            if (session == null) {
                builder = EMPTY;
                return;
            }
            flagId = session.getPageItems().hasNext() ? session.getPageItems().next() : null;
            builder = flagId == null ? EMPTY : flagsItems.get(flagId);
        }

        @Override
        public void close(Player player) {
            flagId = null;
            builder = null;
        }

        @Override
        public String handleName(Player player, String name) {
            final String finalName = builder != null ? builder.name() : name;
            return finalName != null ? finalName.replace("%flag%", flagId) : null;
        }

        @Override
        public List<String> handleLore(Player player, List<String> lore) {
            final List<String> finalLore = builder != null ? builder.lore() : lore;
            return finalLore == null ? null :
                    finalLore.stream()
                            .map(line -> line.replace("%flag%", flagId))
                            .collect(Collectors.toList());
        }

        @Override
        public boolean handleUnbreakable(Player player, boolean unbreakable) {
            return builder != null ? builder.unbreakable() : unbreakable;
        }

        @Override
        public Material handleMaterial(Player player, Material material) {
            return builder != null ? builder.material() : material;
        }

        @Override
        public int handleAmount(Player player, int amount) {
            return builder != null ? builder.amount() : amount;
        }

        @Override
        public Map<Enchantment, Integer> handleEnchants(Player player, Map<Enchantment, Integer> enchants) {
            return builder != null ? builder.enchants() : enchants;
        }

        @Override
        public Set<ItemFlag> handleFlags(Player player, Set<ItemFlag> flags) {
            final Set<ItemFlag> itemFlags = new HashSet<>(builder != null ? builder.flags() : flags);
            itemFlags.add(ItemFlag.HIDE_ENCHANTS);
            return itemFlags;
        }

        @Override
        public Map<Attribute, AttributeModifier> handleAttributes(Player player, Map<Attribute, AttributeModifier> attributes) {
            return builder != null ? builder.attributes() : attributes;
        }

        @Override
        public String handleHeadDataTextures(Player player, String textures) {
            return builder != null ? builder.headDataTexture() : textures;
        }

        @Override
        public String handleHeadDataSignature(Player player, String signature) {
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
                    final List<Integer> sanitizedSlots = new ArrayList<>(new HashSet<>(slots));
                    Collections.sort(sanitizedSlots);
                    MenuManager.this.slots.addAll(sanitizedSlots);
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
                case "toggle-all-off":
                    eventInventory.register(
                            builder,
                            event -> toggleAll(event, false),
                            slots
                    );
                    break;
                case "toggle-all-on":
                    eventInventory.register(
                            builder,
                            event -> toggleAll(event, true),
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
