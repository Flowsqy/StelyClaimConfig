package fr.flowsqy.stelyclaimconfig.menu.item.flag.effect.state.enchant;

public class ReverseEnchantStateEffect implements EnchantStateEffect {
    @Override
    public boolean isEnchanted(boolean state) {
        return !state;
    }
}
