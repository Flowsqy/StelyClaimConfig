package fr.flowsqy.stelyclaimconfig.menu.session;

import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.internal.permission.RegionPermissionModel;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.StringFlag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import fr.flowsqy.stelyclaimconfig.menu.session.state.FlagState;
import fr.flowsqy.stelyclaimconfig.menu.session.state.StateFlagState;
import fr.flowsqy.stelyclaimconfig.menu.session.state.StringFlagState;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class FlagStateLoader {

    private final Player player;
    private final ProtectedRegion region;

    public FlagStateLoader(Player player, ProtectedRegion region) {
        this.player = player;
        this.region = region;
    }

    public Map<String, FlagState> loadFlagStates() {
        final Map<String, FlagState> flagsStates = new HashMap<>();
        final RegionPermissionModel model = new RegionPermissionModel(WorldGuardPlugin.inst().wrapPlayer(player));
        for (Flag<?> flag : WorldGuard.getInstance().getFlagRegistry()) {
            // Don't load flags that the player can't set
            if (!model.maySetFlag(region, flag)) {
                continue;
            }
            final FlagState flagState;
            if (flag instanceof StateFlag stateFlag) {
                flagState = loadStateFlag(stateFlag);
            } else if (flag instanceof StringFlag stringFlag) {
                flagState = loadStringFlag(stringFlag);
            } else {
                continue;
            }

            flagsStates.put(flag.getName(), flagState);
        }
        return flagsStates;
    }

    @NotNull
    private FlagState loadStateFlag(@NotNull StateFlag stateFlag) {
        final StateFlag.State value = region.getFlag(stateFlag);
        return new StateFlagState(stateFlag, (value == null ? stateFlag.getDefault() : value) == StateFlag.State.ALLOW);
    }

    @NotNull
    private FlagState loadStringFlag(@NotNull StringFlag stringFlag) {
        // Get the flag value
        final String value = region.getFlag(stringFlag);
        // Register the value of the StringFlag
        return new StringFlagState(stringFlag, value == null ? stringFlag.getDefault() : value);
    }

}
