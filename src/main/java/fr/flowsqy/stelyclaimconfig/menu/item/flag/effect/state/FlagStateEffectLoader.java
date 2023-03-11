package fr.flowsqy.stelyclaimconfig.menu.item.flag.effect.state;

import fr.flowsqy.stelyclaimconfig.menu.item.flag.effect.state.enchant.EnchantStateEffect;
import fr.flowsqy.stelyclaimconfig.menu.item.flag.effect.state.enchant.EnchantStateEffectLoader;
import fr.flowsqy.stelyclaimconfig.menu.item.flag.effect.state.text.TextStateEffect;
import fr.flowsqy.stelyclaimconfig.menu.item.flag.effect.state.text.TextStateEffectLoader;
import org.bukkit.configuration.ConfigurationSection;

public class FlagStateEffectLoader {
    public static FlagStateEffects getDefault() {
        return new FlagStateEffects(TextStateEffectLoader.getDefault(), EnchantStateEffectLoader.getDefault());
    }

    public FlagStateEffects load(ConfigurationSection effectSection) {
        final ConfigurationSection effectStateSection = effectSection.getConfigurationSection("state");
        if (effectStateSection == null) {
            return getDefault();
        }
        final TextStateEffectLoader textStateEffectLoader = new TextStateEffectLoader();
        final TextStateEffect textStateEffect = textStateEffectLoader.load(effectStateSection);
        final EnchantStateEffectLoader enchantStateEffectLoader = new EnchantStateEffectLoader();
        final EnchantStateEffect enchantStateEffect = enchantStateEffectLoader.load(effectStateSection);
        return new FlagStateEffects(
                textStateEffect,
                enchantStateEffect
        );
    }
}
