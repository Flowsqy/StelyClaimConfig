package fr.flowsqy.stelyclaimconfig.menu.item.flag.effect.state;

import fr.flowsqy.stelyclaimconfig.menu.item.flag.effect.state.enchant.EnchantStateEffect;
import fr.flowsqy.stelyclaimconfig.menu.item.flag.effect.state.text.TextStateEffect;

public class FlagStateEffects {

    private final TextStateEffect textStateEffect;
    private final EnchantStateEffect enchantStateEffect;

    public FlagStateEffects(TextStateEffect textStateEffect, EnchantStateEffect enchantStateEffect) {
        this.textStateEffect = textStateEffect;
        this.enchantStateEffect = enchantStateEffect;
    }

    public TextStateEffect getText() {
        return textStateEffect;
    }

    public EnchantStateEffect getEnchant() {
        return enchantStateEffect;
    }
}
