package fr.flowsqy.stelyclaimconfig.menu.session.state;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class StringFlagDefaultValuesLoader {

    @NotNull
    public Map<String, Function<Player, String>> load(@NotNull ConfigurationSection section) {
        final ConfigurationSection stringSection = section.getConfigurationSection("string");
        if (stringSection == null) {
            return Collections.emptyMap();
        }
        final Map<String, Function<Player, String>> providers = new HashMap<>();
        for (Map.Entry<String, Object> entry : stringSection.getValues(false).entrySet()) {
            if (!(entry.getValue() instanceof String value)) {
                continue;
            }
            providers.put(entry.getKey(), player -> ChatColor.translateAlternateColorCodes('&', value).replace("%player%", player.getName()));
        }
        return Map.copyOf(providers);
    }

}
