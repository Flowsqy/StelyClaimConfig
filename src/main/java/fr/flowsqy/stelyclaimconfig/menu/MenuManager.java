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
import fr.flowsqy.stelyclaim.StelyClaimPlugin;
import fr.flowsqy.stelyclaim.api.ClaimHandler;
import fr.flowsqy.stelyclaim.api.ClaimOwner;
import fr.flowsqy.stelyclaim.api.ProtocolManager;
import fr.flowsqy.stelyclaim.io.Messages;
import fr.flowsqy.stelyclaim.protocol.RegionFinder;
import fr.flowsqy.stelyclaimconfig.StelyClaimConfigPlugin;
import org.bukkit.Bukkit;
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

    private final ProtocolManager protocolManager;
    private final Messages messages;
    private final Map<UUID, PlayerSession> playerSessions;
    private final Map<String, FlagItem> flagsItems;
    private final List<Integer> slots;
    private final EventInventory inventory;

    public MenuManager(StelyClaimConfigPlugin plugin, StelyClaimPlugin stelyClaimPlugin, YamlConfiguration menuConfiguration) {
        protocolManager = stelyClaimPlugin.getProtocolManager();
        messages = plugin.getMessages();
        playerSessions = new HashMap<>();
        flagsItems = new HashMap<>();

        // Setup flags -> items mapping
        final ConfigurationSection itemSection = menuConfiguration.getConfigurationSection("items");
        if (itemSection != null) {
            fillFlagsItems(itemSection);
        }

        // Create the GUI
        slots = new ArrayList<>(0);
        final MenuFactory factory = new MenuFactory(plugin);
        final ConfigurationSection menuSection = menuConfiguration.getConfigurationSection("menu");
        // Add a default GUI if none is specified in the configuration
        if (menuSection == null) {
            inventory = new EventInventory(factory, "", 1);
        }
        // Deserialize the GUI
        else {
            inventory = EventInventory.deserialize(
                    menuSection,
                    factory,
                    new SCConfigRegisterHandler()
            );
        }
        // Remove the session when the inventory is closed
        inventory.setCloseCallback(this::removeSession);
    }

    /**
     * Deserialize items mappings
     *
     * @param section The {@link ConfigurationSection} that contains all item specifications
     */
    private void fillFlagsItems(ConfigurationSection section) {
        for (String sectionKey : section.getKeys(false)) {
            final ConfigurationSection itemSection = section.getConfigurationSection(sectionKey);
            // Skip if it's not a section
            if (itemSection == null) {
                continue;
            }
            // Get the item and its order in the list
            final ItemBuilder itemBuilder = ItemBuilder.deserialize(itemSection);
            final int order = itemSection.getInt("order", -1);
            // Register the FlagItem
            flagsItems.put(sectionKey, new FlagItem(order > -1 ? order : Integer.MAX_VALUE, itemBuilder));
        }
    }

    /**
     * Open the GUI to a player
     *
     * @param player The player that open the GUI
     * @param region The {@link ProtectedRegion} that will be modified
     */
    public void open(Player player, ProtectedRegion region) {
        // Create a session and register it
        final PlayerSession session = new PlayerSession(region, calculateFlags(player, region));
        playerSessions.put(player.getUniqueId(), session);
        // Load and cache the flag values
        session.initFlagStates();
        // Open the inventory
        session.createPageFlagIdItr();
        final Inventory bukkitInventory = inventory.open(player, player.getUniqueId());
        session.clearPageFlagIdItr();
        // Actualise flag items visual state
        session.applyVisualStates(bukkitInventory, true);
    }

    /**
     * Calculate which flag that should be used by the session and their order
     *
     * @param player The {@link Player} that use the session
     * @param region The {@link ProtectedRegion} targeted by this session
     * @return A {@link List} of flag identifier
     */
    private List<String> calculateFlags(Player player, ProtectedRegion region) {
        // Check which flag the player has the permission to set
        final List<String> flags = new ArrayList<>();
        final RegionPermissionModel model = new RegionPermissionModel(WorldGuardPlugin.inst().wrapPlayer(player));
        for (Flag<?> flag : WorldGuard.getInstance().getFlagRegistry()) {
            if (flag instanceof StateFlag && model.maySetFlag(region, flag)) {
                flags.add(flag.getName());
            }
        }
        // Sort the flags by the order specified in the configuration
        flags.sort(Comparator.comparingInt(flagName -> {
                    final FlagItem flagItem = flagsItems.get(flagName);
                    return flagItem == null ? Integer.MAX_VALUE : flagItem.getOrder();
                })
        );
        return flags;
    }

    private void applySession(InventoryClickEvent event) {
        // Get the player session
        final Player player = (Player) event.getWhoClicked();
        final PlayerSession session = playerSessions.get(player.getUniqueId());
        if (session == null) {
            messages.sendMessage(player, "menu.fail");
            return;
        }
        // Apply all flag changes to the region
        final ProtectedRegion region = session.getRegion();
        final FlagRegistry registry = WorldGuard.getInstance().getFlagRegistry();
        for (Map.Entry<String, Boolean> entry : session.getFlagsStates().entrySet()) {
            final Flag<?> flag = registry.get(entry.getKey());
            // Get the StateFlag from the flag identifier
            // Skip the change if it's not a (valid) StateFlag
            if (!(flag instanceof StateFlag)) {
                continue;
            }
            final StateFlag stateFlag = (StateFlag) flag;
            final boolean value = entry.getValue();
            // Avoid changes if the flag is already set to true for a 'true' change
            if (value && region.getFlag(stateFlag) == StateFlag.State.ALLOW) {
                continue;
            }
            // Unset the flag if the 'changed value' is the same as the default one for this flag
            if (
                    (stateFlag.getDefault() == StateFlag.State.ALLOW && value)
                            || (stateFlag.getDefault() == null && !value)
            ) {
                region.setFlag(stateFlag, null);
            } else {
                // Otherwise, set the flag
                region.setFlag(stateFlag, value ? StateFlag.State.ALLOW : StateFlag.State.DENY);
            }
        }

        String regionName = region.getId();
        boolean ownRegion = false;
        // Load StelyClaim name and owner property
        if (RegionFinder.isCorrectId(regionName)) {
            final String[] partId = regionName.split("_", 3);
            // Get ClaimHandler
            final ClaimHandler<?> handler = protocolManager.getHandler(partId[1]);
            if (handler != null) {
                // Get Owner
                final ClaimOwner owner = handler.getOwner(partId[2]);
                if (owner != null) {
                    // Get owner name and own property
                    regionName = owner.getName();
                    ownRegion = owner.own(player);
                }
            }
        }

        messages.sendMessage(player, "menu.success" + (ownRegion ? "" : "-other"), "%region%", regionName);
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
     * Change the current page of the session
     *
     * @param viewer           The viewer of this session
     * @param sessionInventory The {@link Inventory} of the session
     * @param modifier         The modifier to apply to the page index
     */
    private void changePage(Player viewer, Inventory sessionInventory, Function<Integer, Integer> modifier) {
        // Get the session
        final PlayerSession session = playerSessions.get(viewer.getUniqueId());
        if (session == null) {
            return;
        }
        // Set the current page
        session.setCurrentPage(modifier.apply(session.getCurrentPage()));
        // Refresh items
        session.createPageFlagIdItr();
        inventory.refresh(viewer.getUniqueId(), viewer);
        session.clearPageFlagIdItr();
        // Actualise flag items visual state
        session.applyVisualStates(sessionInventory, true);
    }

    /**
     * Handle a click on a flag item
     *
     * @param event The {@link InventoryClickEvent} that trigger the method
     */
    private void handleFlagClick(InventoryClickEvent event) {
        // This method is only triggered when the flag ItemBuild is not null and registered
        // So if the clicked item is null, it means that this slot does not represent a flag (not enough flag, last page)
        if (event.getCurrentItem() == null) {
            return;
        }
        // Get the session
        final PlayerSession session = playerSessions.get(event.getWhoClicked().getUniqueId());
        if (session == null) {
            return;
        }
        // Get the flag id
        final String flagId = session.getAttachedFlagId(event.getSlot());
        if (flagId == null) {
            return;
        }
        // Toggle the value in the session
        final Map<String, Boolean> states = session.getFlagsStates();
        final Boolean value = states.computeIfPresent(flagId, (k, v) -> !v);
        // Actualise the flag item
        applyVisualState(event.getCurrentItem(), value, false);
    }

    /**
     * Set the value of every flag
     *
     * @param sessionId        The {@link UUID} of the session
     * @param sessionInventory The {@link Inventory} of the session
     * @param state            The boolean value to set to every StateFlag
     */
    private void setAllValue(UUID sessionId, Inventory sessionInventory, boolean state) {
        // Get the session
        final PlayerSession session = playerSessions.get(sessionId);
        if (session == null) {
            return;
        }
        // Set every flag value to 'state'
        final Map<String, Boolean> flagStates = session.getFlagsStates();
        for (String key : flagStates.keySet().toArray(new String[0])) {
            flagStates.put(key, state);
        }
        // Actualise flag items
        session.applyVisualStates(sessionInventory, false);
    }

    /**
     * Toggle the visual state of a flag item
     *
     * @param itemStack The targeted flag item
     * @param state     The state that should be displayed
     * @param init      Whether this method is triggered at item creation time
     */
    private void applyVisualState(ItemStack itemStack, Boolean state, boolean init) {
        // Remove the visual state
        if (state == null || !state) {
            // Skip the removing if it's an initialisation (don't remove something that was not added)
            if (!init) {
                itemStack.removeEnchantment(Enchantment.LUCK);
            }
        } else {
            // Add the visual state
            itemStack.addUnsafeEnchantment(Enchantment.LUCK, 1);
        }
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

    private final static class FlagItem {

        private final int order;
        private final ItemBuilder builder;

        private FlagItem(int order, ItemBuilder builder) {
            this.order = order;
            this.builder = builder;
        }

        public int getOrder() {
            return order;
        }

        public ItemBuilder getBuilder() {
            return builder;
        }
    }

    private final class PlayerSession {

        private final ProtectedRegion region;
        private final List<String> flags;
        private final Map<String, Boolean> flagsStates;
        private int currentPage;
        private Iterator<String> pageFlagIdItr;

        public PlayerSession(ProtectedRegion region, List<String> flags) {
            this.region = region;
            this.flags = flags;
            this.flagsStates = new HashMap<>();
            this.currentPage = 0; // Start at first page
        }

        public ProtectedRegion getRegion() {
            return region;
        }

        public Map<String, Boolean> getFlagsStates() {
            return flagsStates;
        }

        public int getCurrentPage() {
            return currentPage;
        }

        public void setCurrentPage(int currentPage) {
            this.currentPage = currentPage;
        }

        /**
         * The current page index starting from 1
         *
         * @return The current page starting from 1
         */
        public int getReadableCurrentPage() {
            return currentPage + 1;
        }

        /**
         * Get the {@link Iterator} of the flag identifiers of this page
         *
         * @return A {@link String} {@link Iterator}
         */
        public Iterator<String> getPageFlagIdItr() {
            return pageFlagIdItr;
        }

        /**
         * Load every flag value of the targeted region
         */
        public void initFlagStates() {
            for (String flagName : flags) {
                // Get the flag from its identifier
                final Flag<?> flag = WorldGuard.getInstance().getFlagRegistry().get(flagName);
                // Currently it only supports StateFlag, skip if the flag is not supported
                if (!(flag instanceof StateFlag)) {
                    return;
                }
                final StateFlag stateFlag = (StateFlag) flag;
                // Get the flag value
                final StateFlag.State value = region.getFlag(stateFlag);
                // Register the boolean value of the StateFlag
                flagsStates.put(flagName, (value == null ? stateFlag.getDefault() : value) == StateFlag.State.ALLOW);
            }
        }

        /**
         * Apply the visual state of each visible flag item in the session
         *
         * @param inventory The viewed inventory
         * @param init      Whether this method is triggered at item creation time
         */
        public void applyVisualStates(Inventory inventory, boolean init) {
            for (
                    int flagIndex = (currentPage - 1) * slots.size(), slotIndex = 0;
                    flagIndex < flags.size() && slotIndex < slots.size();
                    flagIndex++, slotIndex++
            ) {
                final ItemStack item = inventory.getItem(slots.get(slotIndex));
                if (item == null)
                    return;
                final Boolean value = flagsStates.get(flags.get(flagIndex));
                applyVisualState(item, value, init);
            }
        }

        /**
         * Get the flag identifier mapped to a slot on the current page
         *
         * @param slot The slot of the item
         * @return A flag {@link String} identifier
         */
        public String getAttachedFlagId(int slot) {
            return flags.get((currentPage - 1) * slots.size() + slots.indexOf(slot));
        }

        /**
         * Get the number of page this session have
         *
         * @return The page number of the session
         */
        public int numberOfPage() {
            final int remainder = flags.size() % slots.size();
            return (flags.size() - remainder) / slots.size() + (remainder > 0 ? 1 : 0);
        }

        /**
         * Initialize the flag identifier iterator of this page
         */
        public void createPageFlagIdItr() {
            final List<String> flagIdentifiers = new ArrayList<>();
            if (!flags.isEmpty()) {
                // Get the flag identifier mapped to this slot index on this page
                for (
                        int flagIndex = (currentPage - 1) * slots.size(), slotIndex = 0;
                        flagIndex < flags.size() && slotIndex < slots.size();
                        flagIndex++, slotIndex++
                ) {
                    flagIdentifiers.add(flags.get(flagIndex));
                }
            }
            pageFlagIdItr = flagIdentifiers.iterator();
        }

        /**
         * Clear the flag identifier iterator of this page
         */
        public void clearPageFlagIdItr() {
            pageFlagIdItr = null;
        }

    }

    /**
     * Use to display the item that matches the specified flag
     */
    private final class FlagCreatorListener implements CreatorListener {

        private final ItemBuilder EMPTY = new ItemBuilder();
        private String flagId;
        private ItemBuilder builder;

        public FlagCreatorListener() {
        }

        /**
         * Get the flag identifier and the matching item
         *
         * @param player The player which has opened the inventory
         */
        @Override
        public void open(Player player) {
            // Get the player session (should not be null)
            final PlayerSession session = playerSessions.get(player.getUniqueId());
            if (session == null) {
                builder = EMPTY;
                return;
            }
            // Get the flag id of the current item (null if we are at the end of the available flags)
            flagId = session.getPageFlagIdItr().hasNext() ? session.getPageFlagIdItr().next() : null;
            // Get the matching item (null if the identifier is null)
            if (flagId == null) {
                builder = EMPTY;
            } else {
                final FlagItem flagItem = flagsItems.get(flagId);
                builder = flagItem == null ? null : flagItem.getBuilder();
            }
        }

        /**
         * Reset the creator when the item is created
         *
         * @param player The player that is using the inventory
         */
        @Override
        public void close(Player player) {
            // Reset flags id and item
            flagId = null;
            builder = null;
        }

        @Override
        public String handleName(Player player, String name) {
            // Get the specific flag item's custom name or keep the generic one
            final String finalName = builder != null ? builder.name() : name;
            // Replace "%flag%" placeholder in the item name
            return finalName != null ? finalName.replace("%flag%", flagId) : null;
        }

        @Override
        public List<String> handleLore(Player player, List<String> lore) {
            // Get the specific flag item's lore or keep the generic one
            final List<String> finalLore = builder != null ? builder.lore() : lore;
            // Replace "%flag%" placeholder in the item lore
            return finalLore == null ? null :
                    finalLore.stream()
                            .map(line -> line.replace("%flag%", flagId))
                            .collect(Collectors.toList());
        }

        @Override
        public boolean handleUnbreakable(Player player, boolean unbreakable) {
            // Get the specific flag item's unbreakable property or keep the generic one
            return builder != null ? builder.unbreakable() : unbreakable;
        }

        @Override
        public Material handleMaterial(Player player, Material material) {
            // Get the specific flag item's Material or keep the generic one
            return builder != null ? builder.material() : material;
        }

        @Override
        public int handleAmount(Player player, int amount) {
            // Get the specific flag item's amount or keep the generic one
            return builder != null ? builder.amount() : amount;
        }

        @Override
        public Map<Enchantment, Integer> handleEnchants(Player player, Map<Enchantment, Integer> enchants) {
            // Get the specific flag item's enchants or keep the generic one
            return builder != null ? builder.enchants() : enchants;
        }

        @Override
        public Set<ItemFlag> handleFlags(Player player, Set<ItemFlag> flags) {
            // Get the specific flag item's custom item flag list or keep the generic one
            final Set<ItemFlag> itemFlags = new HashSet<>(builder != null ? builder.flags() : flags);
            itemFlags.add(ItemFlag.HIDE_ENCHANTS); // Hide enchants as they will be used to show if the flag is active
            return itemFlags;
        }

        @Override
        public Map<Attribute, AttributeModifier> handleAttributes(Player player, Map<Attribute, AttributeModifier> attributes) {
            // Get the specific flag item's custom item attribute map or keep the generic one
            return builder != null ? builder.attributes() : attributes;
        }

        @Override
        public String handleHeadDataTextures(Player player, String textures) {
            // Get the specific flag item's custom skull texture data or keep the generic one
            return builder != null ? builder.headDataTexture() : textures;
        }

        @Override
        public String handleHeadDataSignature(Player player, String signature) {
            // Get the specific flag item's custom skull signature data or keep the generic one
            return builder != null ? builder.headDataSignature() : signature;
        }
    }

    /***
     * Custom handler for Inventory deserialization
     * <p>
     * Maps every item to its function
     */
    private final class SCConfigRegisterHandler implements EventInventory.RegisterHandler {

        private final FlagCreatorListener FCL_INSTANCE = new FlagCreatorListener();

        @Override
        public void handle(EventInventory eventInventory, String key, ItemBuilder builder, List<Integer> slots) {
            switch (key) {
                // Valid item
                case "valid":
                    eventInventory.register(
                            builder,
                            e -> {
                                // Valid the session and close the inventory
                                applySession(e);
                                e.getWhoClicked().closeInventory();
                            },
                            slots
                    );
                    break;
                // Leave item
                case "leave":
                    eventInventory.register(
                            builder,
                            // Close the inventory
                            e -> e.getWhoClicked().closeInventory(),
                            slots
                    );
                    break;
                // Flags items
                case "flags":
                    // Add the custom creator to display the item matching the flag
                    builder.creatorListener(FCL_INSTANCE);
                    // Register sorted slots to map each slot to its flag
                    final List<Integer> sanitizedSlots = new ArrayList<>(new HashSet<>(slots));
                    Collections.sort(sanitizedSlots);
                    MenuManager.this.slots.addAll(sanitizedSlots);
                    // Register the items flags
                    eventInventory.register(
                            builder,
                            // Detect each click on the flag to modify cached flag value
                            MenuManager.this::handleFlagClick,
                            slots
                    );
                    break;
                // Information item
                case "info":
                    builder.creatorListener(new CreatorAdaptor() {
                        @Override
                        public List<String> handleLore(Player player, List<String> lore) {
                            if (lore == null)
                                return null;
                            // Replace the "%page%" placeholder in the lore
                            final PlayerSession session = playerSessions.get(player.getUniqueId());
                            final String page = String.valueOf(session == null ? 1 : session.getReadableCurrentPage());
                            return lore.stream()
                                    .map(line -> line.replace("%page%", page))
                                    .collect(Collectors.toList());
                        }
                    });
                    eventInventory.register(builder, slots);
                    break;
                // Previous item
                case "previous":
                    builder.creatorListener(new CreatorAdaptor() {
                        @Override
                        public Material handleMaterial(Player player, Material material) {
                            final PlayerSession session = playerSessions.get(player.getUniqueId());
                            if (session == null)
                                return null;
                            // Only display this item if there is a previous page
                            return session.getCurrentPage() < 1 ? null : material;
                        }
                    });
                    eventInventory.register(
                            builder,
                            // Change to the previous page
                            event -> {
                                // The item is only displayed if the action is possible
                                // So if there is no item, it means that it's impossible to change the page this way
                                if (event.getCurrentItem() == null) {
                                    return;
                                }
                                changePage((Player) event.getWhoClicked(), event.getClickedInventory(), page -> page - 1);
                            },
                            slots
                    );
                    break;
                // Next item
                case "next":
                    builder.creatorListener(new CreatorAdaptor() {
                        @Override
                        public Material handleMaterial(Player player, Material material) {
                            final PlayerSession session = playerSessions.get(player.getUniqueId());
                            if (session == null)
                                return null;
                            // Only display this item if there is a next page
                            return session.getCurrentPage() < session.numberOfPage() - 1 ? material : null;
                        }
                    });
                    eventInventory.register(
                            builder,
                            // Change to next page
                            event -> {
                                // The item is only displayed if the action is possible
                                // So if there is no item, it means that it's impossible to change the page this way
                                if (event.getCurrentItem() == null) {
                                    return;
                                }
                                changePage((Player) event.getWhoClicked(), event.getClickedInventory(), page -> page + 1);
                            },
                            slots
                    );
                    break;
                // Toggle off everything item
                case "toggle-all-off":
                    eventInventory.register(
                            builder,
                            // Set every flag value to false
                            event -> setAllValue(event.getWhoClicked().getUniqueId(), event.getClickedInventory(), false),
                            slots
                    );
                    break;
                // Toggle on everything item
                case "toggle-all-on":
                    eventInventory.register(
                            builder,
                            // Set every flag value to true
                            event -> setAllValue(event.getWhoClicked().getUniqueId(), event.getClickedInventory(), true),
                            slots
                    );
                    break;
                // Any other item
                default:
                    eventInventory.register(
                            builder,
                            slots
                    );
            }
        }

    }

}
