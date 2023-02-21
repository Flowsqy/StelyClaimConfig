package fr.flowsqy.stelyclaimconfig.menu.session;

import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import java.util.Map;

public class FlagApplier {

    private final FlagManager flagManager;

    public FlagApplier(FlagManager flagManager) {
        this.flagManager = flagManager;
    }

    public void apply() {
        // Apply all flag changes to the region
        final ProtectedRegion region = flagManager.getRegion();
        final FlagRegistry registry = WorldGuard.getInstance().getFlagRegistry();
        for (Map.Entry<String, Boolean> entry : flagManager.getFlagsStates().entrySet()) {
            final Flag<?> flag = registry.get(entry.getKey());
            // Get the StateFlag from the flag identifier
            // Skip the change if it's not a (valid) StateFlag
            if (!(flag instanceof StateFlag)) {
                continue;
            }
            final StateFlag stateFlag = (StateFlag) flag;
            final boolean value = entry.getValue();
            // Avoid changes if the flag is already set to true for a 'true' change
            if (value && region.getFlag(stateFlag) == StateFlag.State.ALLOW) {
                continue;
            }
            // Unset the flag if the 'changed value' is the same as the default one for this flag
            if (
                    (stateFlag.getDefault() == StateFlag.State.ALLOW && value)
                            || (stateFlag.getDefault() == null && !value)
            ) {
                region.setFlag(stateFlag, null);
            } else {
                // Otherwise, set the flag
                region.setFlag(stateFlag, value ? StateFlag.State.ALLOW : StateFlag.State.DENY);
            }
        }
    }

}
