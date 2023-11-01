package fr.flowsqy.stelyclaimconfig.menu.session;

import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.StringFlag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import fr.flowsqy.stelyclaim.common.ConfigurationFormattedMessages;

import fr.flowsqy.stelyclaimconfig.menu.session.state.FlagState;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Handle the state of each flag in a session
 */
public class FlagStateManager {

    // TODO Generalize the map

    // private final ConfigurationFormattedMessages messages;
    //private final Map<StateFlag, Boolean> flagsStates;
    //private final Map<StringFlag, String> flagsString;
    private final Map<String, FlagState> flagsStates;

    public FlagStateManager(/*ConfigurationFormattedMessages messages*/) {
        //this.messages = messages;
        flagsStates = new HashMap<>(0);
        //flagsString = new HashMap<>(0);
    }

    public void load(Player player, ProtectedRegion region) {
        final FlagStateLoader loader = new FlagStateLoader(player, region);
        flagsStates.putAll(loader.loadFlagStates());
        //flagsString.putAll(loader.loadFlagStrings());
    }

    @NotNull
    public Set<String> getAvailableFlags() {
        return Collections.unmodifiableSet(flagsStates.keySet());
    }

    @Nullable
    public FlagState getState(@NotNull String flagId) {
        return flagsStates.get(flagId);
    }

    /*
    public Map<StateFlag, Boolean> getFlagsStates() {
        return flagsStates;
    }*/

    /*
    public Map<StringFlag, String> getFlagsString() {
        return flagsString;
    }*/

    /*
    public void toggleFlag(StateFlag flag) {
        flagsStates.computeIfPresent(flag, (k, v) -> !v);
    }

    public void defineStringFlag(StringFlag flag, String value) {
        flagsString.computeIfPresent(flag, (k, v) -> value);
    }*/

    public void setDefault(/*String regionName*/) {
        flagsStates.values().forEach(FlagState::setDefault);
    }

    public void setAllValue(boolean state) {

        // TODO Decide if it's still relevant
        /*
        final Set<StateFlag> availableStateFlags = flagsStates.keySet();
        for (StateFlag flag : availableStateFlags.toArray(new StateFlag[availableStateFlags.size()])) {
            flagsStates.put(flag, state);
        }*/
    }

    public void handleClick(@NotNull InventoryClickEvent event, @NotNull String flag) {
    }

    public void apply(@NotNull ProtectedRegion region) {
        flagsStates.values().forEach(flagState -> flagState.apply(region));
    }

}
