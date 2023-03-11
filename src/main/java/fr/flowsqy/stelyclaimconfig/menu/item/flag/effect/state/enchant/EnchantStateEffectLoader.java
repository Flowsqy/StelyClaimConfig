package fr.flowsqy.stelyclaimconfig.menu.item.flag.effect.state.enchant;

import org.bukkit.configuration.ConfigurationSection;

public class EnchantStateEffectLoader {

    public static EnchantStateEffect getDefault() {
        return new NoneEnchantStateEffect();
    }

    public EnchantStateEffect load(ConfigurationSection effectStateSection) {
        final String effectStateEnchant = effectStateSection.getString("enchant", "");
        if (effectStateEnchant.equalsIgnoreCase("DIRECT")) {
            return new DirectEnchantStateEffect();
        }
        if (effectStateEnchant.equalsIgnoreCase("REVERSE")) {
            return new ReverseEnchantStateEffect();
        }
        return new NoneEnchantStateEffect();
    }

}
