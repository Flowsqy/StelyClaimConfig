package fr.flowsqy.stelyclaimconfig.menu.session;

import com.sk89q.worldguard.protection.flags.Flag;
import fr.flowsqy.stelyclaimconfig.menu.FlagItem;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class AvailableFlagLoader {

    public <T> List<Flag<?>> loadAvailableFlags(Map<? extends Flag<?>, T> flagStates, Map<String, FlagItem> flagItems) {
        final List<Flag<?>> availableFlags = new ArrayList<>(flagStates.keySet());
        // Sort the flags by the order specified in the configuration
        availableFlags.sort(Comparator.comparingInt(flag -> {
                    final FlagItem flagItem = flagItems.get(flag.getName());
                    return flagItem == null ? Integer.MAX_VALUE : flagItem.getOrder();
                })
        );
        return availableFlags;
    }

}
