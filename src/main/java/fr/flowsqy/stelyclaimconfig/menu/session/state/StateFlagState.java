package fr.flowsqy.stelyclaimconfig.menu.session.state;

import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import fr.flowsqy.stelyclaimconfig.menu.session.PlayerMenuSession;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;

public class StateFlagState implements FlagState {

    private final StateFlag flag;
    private final boolean defaultValue;
    private boolean value;

    public StateFlagState(@NotNull StateFlag flag, boolean value, boolean defaultValue) {
        this.flag = flag;
        this.value = value;
        this.defaultValue = defaultValue;
    }

    @Override
    public @NotNull FlagStateCreatorListener getCreatorListener() {
        return new StateFlagStateCreatorListener();
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
        value = defaultValue;
    }

    @Override
    public void handleUserInput(@NotNull InventoryClickEvent event, @NotNull PlayerMenuSession session) {
        toggleValue();
        session.refresh((Player) event.getWhoClicked());
    }

    public boolean getValue() {
        return value;
    }

    public void setValue(boolean value) {
        this.value = value;
    }

    public void toggleValue() {
        value = !value;
    }

}
