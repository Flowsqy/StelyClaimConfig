package fr.flowsqy.stelyclaimconfig.menu.session;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import fr.flowsqy.stelyclaimconfig.menu.session.state.FlagState;
import org.bukkit.entity.Player;
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

    private final Map<String, FlagState> flagsStates;

    public FlagStateManager() {
        flagsStates = new HashMap<>(0);
    }

    public void load(Player player, ProtectedRegion region) {
        final FlagStateLoader loader = new FlagStateLoader(player, region);
        flagsStates.putAll(loader.loadFlagStates());
    }

    @NotNull
    public Set<String> getAvailableFlags() {
        return Collections.unmodifiableSet(flagsStates.keySet());
    }

    @Nullable
    public FlagState getState(@NotNull String flagId) {
        return flagsStates.get(flagId);
    }

    public void setDefault() {
        flagsStates.values().forEach(FlagState::setDefault);
    }

    public void apply(@NotNull ProtectedRegion region) {
        flagsStates.values().forEach(flagState -> flagState.apply(region));
    }

}
