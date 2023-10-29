package fr.flowsqy.stelyclaimconfig.menu.session;

import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import fr.flowsqy.stelyclaim.common.ConfigurationFormattedMessages;
import fr.flowsqy.stelyclaimconfig.menu.FlagItem;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FlagManager {

    private final ConfigurationFormattedMessages messages;
    private final ProtectedRegion region;
    private final List<Flag<?>> availableFlags;
    private final FlagStateManager flagStateManager;
    private final FlagSlotHandler flagSlotHandler;

    public FlagManager(ConfigurationFormattedMessages messages, ProtectedRegion region, List<Integer> flagSlots, PageManager pageManager) {
        this.messages = messages;
        this.region = region;
        this.availableFlags = new ArrayList<>(0);
        flagStateManager = new FlagStateManager(messages);
        flagSlotHandler = new FlagSlotHandler(flagSlots, this, pageManager);
    }

    public void load(Player player, Map<String, FlagItem> flagItems) {
        flagStateManager.load(player, region);
        final AvailableFlagLoader availableFlagLoader = new AvailableFlagLoader();
        availableFlags.addAll(availableFlagLoader.loadAvailableFlags(flagStateManager.getFlagsStates(), flagItems));
        availableFlags.addAll(availableFlagLoader.loadAvailableFlags(flagStateManager.getFlagsString(), flagItems));
    }

    public List<Flag<?>> getAvailableFlags() {
        return availableFlags;
    }

    public FlagStateManager getFlagStateManager() {
        return flagStateManager;
    }

    public FlagSlotHandler getFlagSlotHandler() {
        return flagSlotHandler;
    }

    public void apply(Player player) {
        final FlagApplier applier = new FlagApplier(messages, this);
        applier.apply(player);
    }

    public ProtectedRegion getRegion() {
        return region;
    }

}
