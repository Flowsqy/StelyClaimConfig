package fr.flowsqy.stelyclaimconfig.menu.session.state;

import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.jetbrains.annotations.NotNull;

public class StateFlagState implements FlagState {

    private final StateFlag flag;
    private boolean value;

    public StateFlagState(@NotNull StateFlag flag) {
        this.flag = flag;
    }

    @Override
    public boolean isActive() {
        return value;
    }

    @Override
    public void apply(@NotNull ProtectedRegion region) {
        // Avoid changes if the flag is already set to true for a 'true' change
        if (value && region.getFlag(flag) == StateFlag.State.ALLOW) {
            return;
        }
        // Unset the flag if the 'changed value' is the same as the default one for this flag
        if (
                (flag.getDefault() == StateFlag.State.ALLOW && value)
                        || (flag.getDefault() == null && !value)
        ) {
            region.setFlag(flag, null);
        } else {
            // Otherwise, set the flag
            region.setFlag(flag, value ? StateFlag.State.ALLOW : StateFlag.State.DENY);
        }
    }

    @Override
    public void setDefault() {
        value = flag.getDefault() == StateFlag.State.ALLOW;
    }
}
