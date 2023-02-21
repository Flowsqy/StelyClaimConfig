package fr.flowsqy.stelyclaimconfig.menu.session;

import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class FlagStateManager {

    private final Map<StateFlag, Boolean> flagsStates;

    public FlagStateManager() {
        flagsStates = new HashMap<>(0);
    }

    public void load(Player player, ProtectedRegion region) {
        final FlagStateLoader loader = new FlagStateLoader(player, region);
        flagsStates.putAll(loader.loadFlagStates());
    }

    public Map<StateFlag, Boolean> getFlagsStates() {
        return flagsStates;
    }

    public void toggleFlag(StateFlag flag) {
        flagsStates.computeIfPresent(flag, (k, v) -> !v);
    }

    public void setDefault() {
        final Set<StateFlag> availableStateFlags = flagsStates.keySet();
        for (StateFlag flag : availableStateFlags.toArray(new StateFlag[availableStateFlags.size()])) {
            flagsStates.put(flag, flag.getDefault() == StateFlag.State.ALLOW);
        }
    }

    public void setAllValue(boolean state) {
        final Set<StateFlag> availableStateFlags = flagsStates.keySet();
        for (StateFlag flag : availableStateFlags.toArray(new StateFlag[availableStateFlags.size()])) {
            flagsStates.put(flag, state);
        }
    }
}
