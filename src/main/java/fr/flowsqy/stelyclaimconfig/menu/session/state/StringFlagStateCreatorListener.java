package fr.flowsqy.stelyclaimconfig.menu.session.state;

import fr.flowsqy.abstractmenu.item.CreatorAdaptor;
import fr.flowsqy.stelyclaimconfig.menu.item.flag.effect.FlagEffects;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Collectors;

public class StringFlagStateCreatorListener extends CreatorAdaptor implements FlagStateCreatorListener {

    private StringFlagState flagState;

    @Override
    public void open(@Nullable FlagState flagState, @NotNull FlagEffects flagEffects) {
        this.flagState = (StringFlagState) flagState;
    }

    @Override
    public void close() {
        flagState = null;
    }

    /**
     * Replace all placeholders with their value
     *
     * @param text The text to process
     * @return The same {@link String} but with placeholders replaced by their value
     */
    private String processPlaceholders(String text) {
        final String value = flagState.getValue();
        return text.replace("%value%", value == null ? "" : value);
    }

    @Override
    public String handleName(Player player, String name) {
        final String computedValue = super.handleName(player, name);
        // Replace "%value%" placeholder in the item name
        return computedValue != null ? processPlaceholders(computedValue) : null;
    }

    @Override
    public List<String> handleLore(Player player, List<String> lore) {
        final List<String> computedValue = super.handleLore(player, lore);
        // Replace "%value%" placeholder in the item lore
        return computedValue == null ? null :
                computedValue.stream()
                        .map(this::processPlaceholders)
                        .collect(Collectors.toList());
    }

}
