package fr.flowsqy.stelyclaimconfig.menu.session;

import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.internal.permission.RegionPermissionModel;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class FlagStateLoader {

    private final Player player;
    private final ProtectedRegion region;

    public FlagStateLoader(Player player, ProtectedRegion region) {
        this.player = player;
        this.region = region;
    }

    public Map<StateFlag, Boolean> loadFlagStates() {
        final Map<StateFlag, Boolean> flagsStates = new HashMap<>();
        final RegionPermissionModel model = new RegionPermissionModel(WorldGuardPlugin.inst().wrapPlayer(player));
        for (Flag<?> flag : WorldGuard.getInstance().getFlagRegistry()) {
            // Currently it only supports StateFlag, skip if the flag is not supported
            if (!(flag instanceof StateFlag)) {
                continue;
            }
            // Don't load flags that the player can't set
            if (!model.maySetFlag(region, flag)) {
                continue;
            }
            final StateFlag stateFlag = (StateFlag) flag;
            // Get the flag value
            final StateFlag.State value = region.getFlag(stateFlag);
            // Register the boolean value of the StateFlag
            flagsStates.put(stateFlag, (value == null ? stateFlag.getDefault() : value) == StateFlag.State.ALLOW);
        }
        return flagsStates;
    }

}
