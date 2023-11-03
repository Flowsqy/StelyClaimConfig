package fr.flowsqy.stelyclaimconfig.menu.session.state;

import fr.flowsqy.abstractmenu.item.CreatorAdaptor;
import fr.flowsqy.stelyclaimconfig.menu.item.flag.effect.FlagEffects;
import fr.flowsqy.stelyclaimconfig.menu.item.flag.effect.state.FlagStateEffects;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

public class StateFlagStateCreatorListener extends CreatorAdaptor implements FlagStateCreatorListener {

    private StateFlagState flagState;
    private FlagStateEffects flagStateEffects;

    @Override
    public void open(@Nullable FlagState flagState, @NotNull FlagEffects flagEffects) {
        this.flagState = (StateFlagState) flagState;
        flagStateEffects = flagEffects.getState();
    }

    @Override
    public void close() {
        flagState = null;
        flagStateEffects = null;
    }

    /**
     * Replace all placeholders with their value
     *
     * @param text The text to process
     * @return The same {@link String} but with placeholders replaced by their value
     */
    private String processPlaceholders(String text) {
        return text.replace("%state%", flagStateEffects.getText().get(flagState.getValue()));
    }

    @Override
    public String handleName(Player player, String name) {
        final String computedValue = super.handleName(player, name);
        // Replace "%state%" placeholder in the item name
        return computedValue != null ? processPlaceholders(computedValue) : null;
    }

    @Override
    public List<String> handleLore(Player player, List<String> lore) {
        final List<String> computedValue = super.handleLore(player, lore);
        // Replace "%state%" placeholder in the item lore
        return computedValue == null ? null :
                computedValue.stream()
                        .map(this::processPlaceholders)
                        .collect(Collectors.toList());
    }

    @Override
    public Map<Enchantment, Integer> handleEnchants(Player player, Map<Enchantment, Integer> enchants) {
        final Map<Enchantment, Integer> computedValue = new HashMap<>(super.handleEnchants(player, enchants));
        // Add a glowing effect on allowed flag
        if (flagStateEffects.getEnchant().isEnchanted(flagState.getValue())) {
            computedValue.put(Enchantment.LUCK, 1);
        }
        return computedValue;
    }

    @Override
    public Set<ItemFlag> handleFlags(Player player, Set<ItemFlag> flags) {
        final Set<ItemFlag> computedValue = new HashSet<>(super.handleFlags(player, flags));
        if (flagStateEffects.getEnchant().isEnchanted(flagState.getValue())) {
            computedValue.add(ItemFlag.HIDE_ENCHANTS);
        }
        return computedValue;
    }

}
