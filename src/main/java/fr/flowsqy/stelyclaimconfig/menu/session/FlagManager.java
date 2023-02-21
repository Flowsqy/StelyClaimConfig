package fr.flowsqy.stelyclaimconfig.menu.session;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import fr.flowsqy.stelyclaimconfig.menu.FlagItem;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FlagManager {

    private final ProtectedRegion region;
    private final List<String> availableFlags;
    private final Map<String, Boolean> flagsStates;
    private final FlagSlotHandler flagSlotHandler;

    public FlagManager(ProtectedRegion region, List<Integer> flagSlots, PageManager pageManager) {
        this.region = region;
        this.availableFlags = new ArrayList<>(0);
        this.flagsStates = new HashMap<>(0);
        flagSlotHandler = new FlagSlotHandler(flagSlots, this, pageManager);
    }

    public void load(Player player, Map<String, FlagItem> flagItems) {
        final FlagLoader loader = new FlagLoader(player, region);
        availableFlags.addAll(loader.loadAvailableFlags(flagItems));
        flagsStates.putAll(loader.loadFlagStates(availableFlags));
    }

    public List<String> getAvailableFlags() {
        return availableFlags;
    }

    public Map<String, Boolean> getFlagsStates() {
        return flagsStates;
    }

    public FlagSlotHandler getFlagSlotHandler() {
        return flagSlotHandler;
    }

    public void toggleFlag(String flagId) {
        flagsStates.computeIfPresent(flagId, (k, v) -> !v);
    }

    public void apply() {
        final FlagApplier applier = new FlagApplier(this);
        applier.apply();
    }

    public ProtectedRegion getRegion() {
        return region;
    }

}
