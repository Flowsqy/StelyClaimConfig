package fr.flowsqy.stelyclaimconfig.menu.session;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import fr.flowsqy.stelyclaimconfig.menu.FlagItem;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FlagManager {

    private final ProtectedRegion region;
    private final List<String> availableFlags;
    private final FlagStateManager flagStateManager;
    private final FlagSlotHandler flagSlotHandler;

    public FlagManager(ProtectedRegion region, List<Integer> flagSlots, PageManager pageManager) {
        this.region = region;
        this.availableFlags = new ArrayList<>(0);
        flagStateManager = new FlagStateManager();
        flagSlotHandler = new FlagSlotHandler(flagSlots, this, pageManager);
    }

    public void load(Player player, Map<String, FlagItem> flagItems) {
        flagStateManager.load(player, region);
        final AvailableFlagLoader availableFlagLoader = new AvailableFlagLoader();
        availableFlags.addAll(availableFlagLoader.loadAvailableFlags(flagStateManager.getAvailableFlags(), flagItems));
    }

    // TODO Check deeply if Flag type is needed (try with string, consider revert)
    public List<String> getAvailableFlags() {
        return availableFlags;
    }

    public FlagStateManager getFlagStateManager() {
        return flagStateManager;
    }

    public FlagSlotHandler getFlagSlotHandler() {
        return flagSlotHandler;
    }

    public void apply(/*Player player*/) {
        /*
        final FlagApplier applier = new FlagApplier(messages, this);
        applier.apply(player);*/
        flagStateManager.apply(region);
    }

    public ProtectedRegion getRegion() {
        return region;
    }

}
