package fr.flowsqy.stelyclaimconfig.menu.session;

import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.internal.permission.RegionPermissionModel;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import fr.flowsqy.stelyclaimconfig.menu.FlagItem;
import org.bukkit.entity.Player;

import java.util.*;

public class FlagLoader {

    private final Player player;
    private final ProtectedRegion region;

    public FlagLoader(Player player, ProtectedRegion region) {
        this.player = player;
        this.region = region;
    }

    /**
     * Get all flag that the player can set in the region
     *
     * @param player The {@link Player}
     * @param region The {@link ProtectedRegion}
     * @return A {@link List} of flag identifier
     */
    private List<String> getAvailableFlag(Player player, ProtectedRegion region) {
        final List<String> flags = new LinkedList<>();
        final RegionPermissionModel model = new RegionPermissionModel(WorldGuardPlugin.inst().wrapPlayer(player));
        for (Flag<?> flag : WorldGuard.getInstance().getFlagRegistry()) {
            if (flag instanceof StateFlag && model.maySetFlag(region, flag)) {
                flags.add(flag.getName());
            }
        }
        return flags;
    }

    public List<String> loadAvailableFlags(Map<String, FlagItem> flagItems) {
        final List<String> availableFlags = getAvailableFlag(player, region);
        // Sort the flags by the order specified in the configuration
        availableFlags.sort(Comparator.comparingInt(flagName -> {
                    final FlagItem flagItem = flagItems.get(flagName);
                    return flagItem == null ? Integer.MAX_VALUE : flagItem.getOrder();
                })
        );
        return availableFlags;
    }

    /**
     * Load every flag value of the targeted region
     */
    public Map<String, Boolean> loadFlagStates(List<String> availableFlags) {
        final Map<String, Boolean> flagsStates = new HashMap<>();
        final FlagRegistry flagRegistry = WorldGuard.getInstance().getFlagRegistry();
        for (String flagName : availableFlags) {
            // Get the flag from its identifier
            final Flag<?> flag = flagRegistry.get(flagName);
            // Currently it only supports StateFlag, skip if the flag is not supported
            if (!(flag instanceof StateFlag)) {
                continue;
            }
            final StateFlag stateFlag = (StateFlag) flag;
            // Get the flag value
            final StateFlag.State value = region.getFlag(stateFlag);
            // Register the boolean value of the StateFlag
            flagsStates.put(flagName, (value == null ? stateFlag.getDefault() : value) == StateFlag.State.ALLOW);
        }
        return flagsStates;
    }

}
