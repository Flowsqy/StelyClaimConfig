package fr.flowsqy.stelyclaimconfig.menu.session;

import fr.flowsqy.stelyclaimconfig.menu.FlagItem;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class AvailableFlagLoader {

    public List<String> loadAvailableFlags(@NotNull Set<String> flagNames, Map<String, FlagItem> flagItems) {
        final List<String> availableFlags = new ArrayList<>(flagNames);
        // Sort the flags by the order specified in the configuration
        availableFlags.sort(Comparator.comparingInt(flagName -> {
                    final FlagItem flagItem = flagItems.get(flagName);
                    return flagItem == null ? Integer.MAX_VALUE : flagItem.getOrder();
                })
        );
        return availableFlags;
    }

}
