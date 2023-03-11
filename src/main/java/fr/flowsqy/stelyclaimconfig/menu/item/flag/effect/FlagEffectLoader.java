package fr.flowsqy.stelyclaimconfig.menu.item.flag.effect;

import fr.flowsqy.stelyclaimconfig.menu.item.flag.effect.state.FlagStateEffectLoader;
import fr.flowsqy.stelyclaimconfig.menu.item.flag.effect.state.FlagStateEffects;
import org.bukkit.configuration.ConfigurationSection;

public class FlagEffectLoader {

    public static FlagEffects getDefault() {
        return new FlagEffects(FlagStateEffectLoader.getDefault());
    }

    public FlagEffects load(ConfigurationSection menuSection) {
        final ConfigurationSection effectSection = menuSection.getConfigurationSection("effect");
        if (effectSection == null) {
            return getDefault();
        }
        final FlagStateEffectLoader flagStateEffectLoader = new FlagStateEffectLoader();
        final FlagStateEffects flagStateEffects = flagStateEffectLoader.load(effectSection);
        return new FlagEffects(flagStateEffects);
    }
}
