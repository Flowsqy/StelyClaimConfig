package fr.flowsqy.stelyclaimconfig.menu.session;

import java.util.HashMap;
import java.util.Map;

import fr.flowsqy.stelyclaimconfig.menu.session.state.FlagState;
import fr.flowsqy.stelyclaimconfig.menu.session.state.StateFlagState;
import fr.flowsqy.stelyclaimconfig.menu.session.state.StringStateFlag;
import org.bukkit.entity.Player;

import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.internal.permission.RegionPermissionModel;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.StringFlag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

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
            // Currently it only supports StateFlag and StringFlag, skip if the flag is not supported
            if (!(flag instanceof StateFlag || flag instanceof StringFlag)) {
                continue;
            }
            // Don't load flags that the player can't set
            if (!model.maySetFlag(region, flag)) {
                continue;
            }

            // TODO Retrieve the flag value

            /*
            final StateFlag stateFlag = (StateFlag) flag;
            // Get the flag value
            final StateFlag.State value = region.getFlag(stateFlag);

            // Register the boolean value of the StateFlag
            //flagsStates.put(flag.getName(), (value == null ? stateFlag.getDefault() : value) == StateFlag.State.ALLOW);

            */
            
            final FlagState flagState;
            if (flag instanceof StateFlag stateFlag) {
                flagState = new StateFlagState(stateFlag);
            } else {
                flagState = new StringStateFlag((StringFlag) flag);
            }

            flagsStates.put(flag.getName(), flagState);
        }
        return flagsStates;
    }


    public Map<StringFlag, String> loadFlagStrings() {
        final Map<StringFlag, String> flagsStrings = new HashMap<>();
        final RegionPermissionModel model = new RegionPermissionModel(WorldGuardPlugin.inst().wrapPlayer(player));
        for (Flag<?> flag : WorldGuard.getInstance().getFlagRegistry()) {
            // Currently it only supports StringFlag, skip if the flag is not supported
            if (!(flag instanceof StringFlag)) {
                continue;
            }
            // Don't load flags that the player can't set
            if (!model.maySetFlag(region, flag)) {
                continue;
            }
            final StringFlag stringFlag = (StringFlag) flag;
            // Get the flag value
            final String value = region.getFlag(stringFlag);
            // Register the value of the StringFlag
            flagsStrings.put(stringFlag, value == null ? stringFlag.getDefault() : value);
        }
        return flagsStrings;
    }

}
