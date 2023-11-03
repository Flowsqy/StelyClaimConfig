package fr.flowsqy.stelyclaimconfig.menu.session.state;

import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.internal.permission.RegionPermissionModel;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.StringFlag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class FlagStateLoader {

    private final FlagStateInteractData flagStateInteractData;

    public FlagStateLoader(@NotNull FlagStateInteractData flagStateInteractData) {
        this.flagStateInteractData = flagStateInteractData;
    }

    public Map<String, FlagState> loadFlagStates(@NotNull Player player, @NotNull ProtectedRegion region) {
        final Map<String, FlagState> flagsStates = new HashMap<>();
        final RegionPermissionModel model = new RegionPermissionModel(WorldGuardPlugin.inst().wrapPlayer(player));
        for (Flag<?> flag : WorldGuard.getInstance().getFlagRegistry()) {
            // Don't load flags that the player can't set
            if (!model.maySetFlag(region, flag)) {
                continue;
            }
            final FlagState flagState;
            if (flag instanceof StateFlag stateFlag) {
                flagState = loadStateFlag(player, region, stateFlag);
            } else if (flag instanceof StringFlag stringFlag) {
                flagState = loadStringFlag(player, region, stringFlag);
            } else {
                continue;
            }

            flagsStates.put(flag.getName(), flagState);
        }
        return flagsStates;
    }

    @NotNull
    private FlagState loadStateFlag(@NotNull Player player, @NotNull ProtectedRegion region, @NotNull StateFlag stateFlag) {
        final StateFlag.State value = region.getFlag(stateFlag);
        return new StateFlagState(stateFlag, (value == null ? stateFlag.getDefault() : value) == StateFlag.State.ALLOW);
    }

    @NotNull
    private FlagState loadStringFlag(@NotNull Player player, @NotNull ProtectedRegion region, @NotNull StringFlag stringFlag) {
        // Get the flag value
        final String value = region.getFlag(stringFlag);
        // Register the value of the StringFlag
        return new StringFlagState(flagStateInteractData.string(), stringFlag, value == null ? stringFlag.getDefault() : value);
    }

}
