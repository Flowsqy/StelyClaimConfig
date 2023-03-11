package fr.flowsqy.stelyclaimconfig.menu.item.flag.effect.state.enchant;

public interface EnchantStateEffect {

    /**
     * Whether the flag item should be enchanted regarding of the state
     *
     * @param state The state of the flag
     * @return {@code true} if the flag should be displayed. {@code false} otherwise
     */
    boolean isEnchanted(boolean state);

}
