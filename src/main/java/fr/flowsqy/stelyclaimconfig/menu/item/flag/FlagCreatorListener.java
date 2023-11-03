package fr.flowsqy.stelyclaimconfig.menu.item.flag;

import fr.flowsqy.abstractmenu.item.CreatorCopy;
import fr.flowsqy.abstractmenu.item.ItemBuilder;
import fr.flowsqy.stelyclaimconfig.menu.FlagItem;
import fr.flowsqy.stelyclaimconfig.menu.MenuManager;
import fr.flowsqy.stelyclaimconfig.menu.item.flag.effect.FlagEffects;
import fr.flowsqy.stelyclaimconfig.menu.session.FlagManager;
import fr.flowsqy.stelyclaimconfig.menu.session.PlayerMenuSession;
import fr.flowsqy.stelyclaimconfig.menu.session.state.FlagState;
import fr.flowsqy.stelyclaimconfig.menu.session.state.FlagStateCreatorListener;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
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
    private final ItemBuilder emptyItem;
    private final FlagEffects flagEffects;
    private String flagId;
    private FlagStateCreatorListener specificListener;

    public FlagCreatorListener(MenuManager menuManager, ItemBuilder emptyItem, FlagEffects flagEffects) {
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
        final PlayerMenuSession session = menuManager.getSession(player.getUniqueId());
        if (session == null) {
            original(emptyItem);
            return;
        }
        // Get the flag id of the current item (null if we are at the end of the available flags)
        final FlagManager flagManager = session.getFlagManager();
        final Iterator<String> pageFlagIdItr = flagManager.getFlagSlotHandler().getPageFlagsItr();
        flagId = pageFlagIdItr.hasNext() ? pageFlagIdItr.next() : null;
        // Get the matching item (empty item if the flag is null)
        if (flagId == null) {
            original(emptyItem);
            return;
        }

        final FlagState flagState = Objects.requireNonNull(flagManager.getFlagStateManager().getState(flagId));
        specificListener = flagState.getCreatorListener();
        specificListener.open(flagState, flagEffects);

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
        if (specificListener != null) {
            specificListener.close();
        }
        specificListener = null;
        original(null);
    }

    /**
     * Replace all placeholders with their value
     *
     * @param text The text to process
     * @return The same {@link String} but with placeholders replaced by their value
     */
    private String processPlaceholders(String text) {
        return text.replace("%flag%", String.valueOf(flagId));
    }

    @Override
    public String handleName(Player player, String name) {
        final String computedValue = super.handleName(player, name);
        final String finalName = specificListener == null ? computedValue : specificListener.handleName(player, computedValue);
        // Replace "%flag%" placeholder in the item name
        return finalName != null ? processPlaceholders(finalName) : null;
    }

    @Override
    public List<String> handleLore(Player player, List<String> lore) {
        // Get the specific flag item's lore or keep the generic one
        final List<String> computedValue = super.handleLore(player, lore);
        final List<String> finalLore = specificListener == null ? computedValue : specificListener.handleLore(player, computedValue);
        // Replace "%flag%" placeholder in the item lore
        return finalLore == null ? null :
                finalLore.stream()
                        .map(this::processPlaceholders)
                        .collect(Collectors.toList());
    }

    @Override
    public boolean handleUnbreakable(Player player, boolean unbreakable) {
        final boolean computedValue = super.handleUnbreakable(player, unbreakable);
        return specificListener == null ? computedValue : specificListener.handleUnbreakable(player, computedValue);
    }

    @Override
    public Material handleMaterial(Player player, Material material) {
        final Material computedValue = super.handleMaterial(player, material);
        return specificListener == null ? computedValue : specificListener.handleMaterial(player, computedValue);
    }

    @Override
    public int handleAmount(Player player, int amount) {
        final int computedValue = super.handleAmount(player, amount);
        return specificListener == null ? computedValue : specificListener.handleAmount(player, computedValue);
    }

    @Override
    public Map<Enchantment, Integer> handleEnchants(Player player, Map<Enchantment, Integer> enchants) {
        final Map<Enchantment, Integer> computedValue = new HashMap<>(super.handleEnchants(player, enchants));
        return specificListener == null ? computedValue : specificListener.handleEnchants(player, computedValue);
    }

    @Override
    public Set<ItemFlag> handleFlags(Player player, Set<ItemFlag> flags) {
        final Set<ItemFlag> computedValue = new HashSet<>(super.handleFlags(player, flags));
        return specificListener == null ? computedValue : specificListener.handleFlags(player, computedValue);
    }

    @Override
    public Map<Attribute, AttributeModifier> handleAttributes(Player player, Map<Attribute, AttributeModifier> attributes) {
        final Map<Attribute, AttributeModifier> computedValue = super.handleAttributes(player, attributes);
        return specificListener == null ? computedValue : specificListener.handleAttributes(player, computedValue);
    }

    @Override
    public String handleHeadDataTextures(Player player, String textures) {
        final String computedValue = super.handleHeadDataTextures(player, textures);
        return specificListener == null ? computedValue : specificListener.handleHeadDataTextures(player, computedValue);
    }

    @Override
    public String handleHeadDataSignature(Player player, String signature) {
        final String computedValue = super.handleHeadDataSignature(player, signature);
        return specificListener == null ? computedValue : specificListener.handleHeadDataSignature(player, computedValue);
    }
}