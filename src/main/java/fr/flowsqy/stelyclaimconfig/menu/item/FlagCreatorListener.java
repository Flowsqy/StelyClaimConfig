package fr.flowsqy.stelyclaimconfig.menu.item;

import fr.flowsqy.abstractmenu.item.CreatorCopy;
import fr.flowsqy.abstractmenu.item.ItemBuilder;
import fr.flowsqy.stelyclaimconfig.menu.FlagItem;
import fr.flowsqy.stelyclaimconfig.menu.MenuManager;
import fr.flowsqy.stelyclaimconfig.menu.session.FlagManager;
import fr.flowsqy.stelyclaimconfig.menu.session.PlayerMenuSession;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Use to display the item that matches the specified flag
 */
public class FlagCreatorListener extends CreatorCopy {

    private final MenuManager menuManager;
    private final ItemBuilder emptyItem = new ItemBuilder();
    private String flagId;
    private boolean state;

    public FlagCreatorListener(MenuManager menuManager) {
        this.menuManager = menuManager;
    }

    /**
     * Get the flag identifier and the matching item
     *
     * @param player The player which has opened the inventory
     */
    @Override
    public void open(Player player) {
        // Get the player session (should not be null)
        final PlayerMenuSession session = menuManager.getSession(player.getUniqueId());
        if (session == null) {
            original(emptyItem);
            return;
        }
        // Get the flag id of the current item (null if we are at the end of the available flags)
        final FlagManager flagManager = session.getFlagManager();
        final Iterator<String> pageFlagIdItr = flagManager.getFlagSlotHandler().getPageFlagIdItr();
        flagId = pageFlagIdItr.hasNext() ? pageFlagIdItr.next() : null;
        // Get the matching item (empty item if the identifier is null)
        if (flagId == null) {
            original(emptyItem);
            return;
        }
        state = flagManager.getFlagsStates().get(flagId);
        final FlagItem flagItem = menuManager.getFlagsItems().get(flagId);
        original(flagItem == null ? null : flagItem.getBuilder());
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
        state = false;
        original(null);
    }

    /**
     * Replace all placeholders with their value
     *
     * @param text The text to process
     * @return The same {@link String} but with placeholders replaced by their value
     */
    private String processPlaceholders(String text) {
        return text
                .replace("%flag%", flagId);
    }

    @Override
    public String handleName(Player player, String name) {
        final String finalName = super.handleName(player, name);
        // Replace "%flag%" placeholder in the item name
        return finalName != null ? processPlaceholders(finalName) : null;
    }

    @Override
    public List<String> handleLore(Player player, List<String> lore) {
        // Get the specific flag item's lore or keep the generic one
        final List<String> finalLore = super.handleLore(player, lore);
        // Replace "%flag%" placeholder in the item lore
        return finalLore == null ? null :
                finalLore.stream()
                        .map(this::processPlaceholders)
                        .collect(Collectors.toList());
    }

    @Override
    public Map<Enchantment, Integer> handleEnchants(Player player, Map<Enchantment, Integer> enchants) {
        final Map<Enchantment, Integer> usedEnchants = new HashMap<>(super.handleEnchants(player, enchants));
        // Add a glowing effect on allowed flag
        if (state) {
            usedEnchants.put(Enchantment.LUCK, 1);
        }
        return usedEnchants;
    }

    @Override
    public Set<ItemFlag> handleFlags(Player player, Set<ItemFlag> flags) {
        final Set<ItemFlag> itemFlags = new HashSet<>(super.handleFlags(player, flags));
        itemFlags.add(ItemFlag.HIDE_ENCHANTS); // Hide enchants as they will be used to show if the flag is active
        return itemFlags;
    }

}