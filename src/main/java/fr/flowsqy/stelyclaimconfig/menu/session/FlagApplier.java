package fr.flowsqy.stelyclaimconfig.menu.session;

import com.sk89q.worldguard.protection.flags.StateFlag;
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
        for (Map.Entry<StateFlag, Boolean> entry : flagManager.getFlagStateManager().getFlagsStates().entrySet()) {
            final StateFlag stateFlag = entry.getKey();
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
