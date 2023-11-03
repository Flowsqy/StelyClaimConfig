package fr.flowsqy.stelyclaimconfig.menu.session.state;

import org.bukkit.configuration.Configuration;
import org.jetbrains.annotations.NotNull;

public class BlockedColorsInputPredicateLoader {

    public BlockedColorsInputPredicate load(@NotNull Configuration configuration) {
        final String[] blockedColors = configuration.getStringList("blocked-colors").toArray(new String[0]);
        return new BlockedColorsInputPredicate(blockedColors);
    }

}
