package fr.flowsqy.stelyclaimconfig.menu.session;

import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.StringFlag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import fr.flowsqy.stelyclaim.common.ConfigurationFormattedMessages;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class FlagStateManager {

    private final ConfigurationFormattedMessages messages;
    private final Map<StateFlag, Boolean> flagsStates;
    private final Map<StringFlag, String> flagsString;

    public FlagStateManager(ConfigurationFormattedMessages messages) {
        this.messages = messages;
        flagsStates = new HashMap<>(0);
        flagsString = new HashMap<>(0);
    }

    public void load(Player player, ProtectedRegion region) {
        final FlagStateLoader loader = new FlagStateLoader(player, region);
        flagsStates.putAll(loader.loadFlagStates());
        flagsString.putAll(loader.loadFlagStrings());
    }

    public Map<StateFlag, Boolean> getFlagsStates() {
        return flagsStates;
    }

    public Map<StringFlag, String> getFlagsString() {
        return flagsString;
    }

    public void toggleFlag(StateFlag flag) {
        flagsStates.computeIfPresent(flag, (k, v) -> !v);
    }

    public void defineStringFlag(StringFlag flag, String value) {
        flagsString.computeIfPresent(flag, (k, v) -> value);
    }

    public void setDefault(String regionName) {
        final Set<StateFlag> availableStateFlags = flagsStates.keySet();
        for (StateFlag flag : availableStateFlags.toArray(new StateFlag[availableStateFlags.size()])) {
            flagsStates.put(flag, flag.getDefault() == StateFlag.State.ALLOW);
        }

        final Set<StringFlag> availableStringFlags = flagsString.keySet();
        for (StringFlag flag : availableStringFlags.toArray(new StringFlag[availableStringFlags.size()])) {
            flagsString.put(flag, messages.getFormattedMessage("default-string-flags." + flag.getName(), "%region%", regionName));
        }
    }

    public void setAllValue(boolean state) {
        final Set<StateFlag> availableStateFlags = flagsStates.keySet();
        for (StateFlag flag : availableStateFlags.toArray(new StateFlag[availableStateFlags.size()])) {
            flagsStates.put(flag, state);
        }
    }
}
