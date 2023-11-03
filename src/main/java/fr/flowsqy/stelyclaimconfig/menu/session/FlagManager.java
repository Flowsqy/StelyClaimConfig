package fr.flowsqy.stelyclaimconfig.menu.session;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import fr.flowsqy.stelyclaimconfig.menu.FlagItem;
import fr.flowsqy.stelyclaimconfig.menu.session.state.FlagStateLoader;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FlagManager {

    private final ProtectedRegion region;
    private final List<String> availableFlags;
    private final FlagStateManager flagStateManager;
    private final FlagSlotHandler flagSlotHandler;

    public FlagManager(@NotNull FlagStateLoader flagStateLoader, @NotNull ProtectedRegion region, @NotNull List<Integer> flagSlots, @NotNull PageManager pageManager) {
        this.region = region;
        this.availableFlags = new ArrayList<>(0);
        flagStateManager = new FlagStateManager(flagStateLoader);
        flagSlotHandler = new FlagSlotHandler(flagSlots, this, pageManager);
    }

    public void load(Player player, Map<String, FlagItem> flagItems) {
        flagStateManager.load(player, region);
        final AvailableFlagLoader availableFlagLoader = new AvailableFlagLoader();
        availableFlags.addAll(availableFlagLoader.loadAvailableFlags(flagStateManager.getAvailableFlags(), flagItems));
    }

    public List<String> getAvailableFlags() {
        return availableFlags;
    }

    public FlagStateManager getFlagStateManager() {
        return flagStateManager;
    }

    public FlagSlotHandler getFlagSlotHandler() {
        return flagSlotHandler;
    }

    public void apply() {
        flagStateManager.apply(region);
    }

    public ProtectedRegion getRegion() {
        return region;
    }

}
