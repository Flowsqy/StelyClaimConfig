package fr.flowsqy.stelyclaimconfig.menu.item.flag;

import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.StringFlag;

import fr.flowsqy.abstractmenu.item.CreatorCopy;
import fr.flowsqy.abstractmenu.item.ItemBuilder;
import fr.flowsqy.stelyclaim.common.ConfigurationFormattedMessages;
import fr.flowsqy.stelyclaimconfig.menu.FlagItem;
import fr.flowsqy.stelyclaimconfig.menu.MenuManager;
import fr.flowsqy.stelyclaimconfig.menu.item.flag.effect.FlagEffects;
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

    private final ConfigurationFormattedMessages messages;
    private final MenuManager menuManager;
    private final ItemBuilder emptyItem;
    private final FlagEffects flagEffects;
    private String flagId;
    private boolean state;

    public FlagCreatorListener(ConfigurationFormattedMessages messages, MenuManager menuManager, ItemBuilder emptyItem, FlagEffects flagEffects) {
        this.messages = messages;
        this.menuManager = menuManager;
        this.emptyItem = emptyItem;
        this.flagEffects = flagEffects;
    }

    /**
     * Get the flag identifier and the matching item
     *
     * @param player The player which has opened the inventory
     */
    @Override
    public void open(Player player) {
        // Get the player session (should not be null)
        final String playerName = player.getName();
        final PlayerMenuSession session = menuManager.getSession(player.getUniqueId());
        if (session == null) {
            original(emptyItem);
            return;
        }
        // Get the flag id of the current item (null if we are at the end of the available flags)
        final FlagManager flagManager = session.getFlagManager();
        final Iterator<Flag<?>> pageFlagIdItr = flagManager.getFlagSlotHandler().getPageFlagsItr();
        final Flag<?> flag = pageFlagIdItr.hasNext() ? pageFlagIdItr.next() : null;
        // Get the matching item (empty item if the flag is null)
        if (flag == null) {
            original(emptyItem);
            return;
        }
        flagId = flag.getName();

        // Get the flag state
        if (flag instanceof StateFlag) {
            state = flagManager.getFlagStateManager().getFlagsStates().get((StateFlag) flag);
        }else if (flag instanceof StringFlag){
            final String value = flagManager.getFlagStateManager().getFlagsString().get((StringFlag) flag);
            state = messages.getFormattedMessage("default-string-flags." + flag.getName(), "%region%", playerName).equals(value);
        }
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
                .replace("%flag%", String.valueOf(flagId))
                .replace("%state%", flagEffects.getState().getText().get(state));
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
        if (flagEffects.getState().getEnchant().isEnchanted(state)) {
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