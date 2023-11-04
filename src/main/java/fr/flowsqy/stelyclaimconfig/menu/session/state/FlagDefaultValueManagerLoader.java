package fr.flowsqy.stelyclaimconfig.menu.session.state;

import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;

public class FlagDefaultValueManagerLoader {

    @NotNull
    public FlagDefaultValueManager load(@NotNull Configuration configuration) {
        final ConfigurationSection defaultValueSection = configuration.getConfigurationSection("default-value");
        if (defaultValueSection == null) {
            return new FlagDefaultValueManager(Collections.emptyMap(), Collections.emptyMap());
        }
        final StateFlagDefaultValuesLoader stateFlagDefaultValuesLoader = new StateFlagDefaultValuesLoader();
        final StringFlagDefaultValuesLoader stringFlagDefaultValuesLoader = new StringFlagDefaultValuesLoader();

        return new FlagDefaultValueManager(
                stateFlagDefaultValuesLoader.load(defaultValueSection),
                stringFlagDefaultValuesLoader.load(defaultValueSection)
        );
    }

}
