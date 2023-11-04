package fr.flowsqy.stelyclaimconfig.menu.session.state;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class StateFlagDefaultValuesLoader {

    private final Function<Player, Boolean> TRUE = p -> true;
    private final Function<Player, Boolean> FALSE = p -> false;

    @NotNull
    public Map<String, Function<Player, Boolean>> load(@NotNull ConfigurationSection section) {
        final ConfigurationSection stateSection = section.getConfigurationSection("state");
        if (stateSection == null) {
            return Collections.emptyMap();
        }
        final Map<String, Function<Player, Boolean>> providers = new HashMap<>();
        for (Map.Entry<String, Object> entry : stateSection.getValues(false).entrySet()) {
            if (!(entry.getValue() instanceof Boolean value)) {
                continue;
            }
            providers.put(entry.getKey(), value ? TRUE : FALSE);
        }
        return Map.copyOf(providers);
    }

}
