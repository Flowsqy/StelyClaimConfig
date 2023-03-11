package fr.flowsqy.stelyclaimconfig.menu.item.flag.effect;

import fr.flowsqy.stelyclaimconfig.menu.item.flag.effect.state.FlagStateEffects;

public class FlagEffects {

    private final FlagStateEffects stateEffects;

    public FlagEffects(FlagStateEffects stateEffects) {
        this.stateEffects = stateEffects;
    }

    public FlagStateEffects getState() {
        return stateEffects;
    }
}
