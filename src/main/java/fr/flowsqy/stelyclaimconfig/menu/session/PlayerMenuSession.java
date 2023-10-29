package fr.flowsqy.stelyclaimconfig.menu.session;

import java.util.List;
import java.util.Map;

import org.bukkit.entity.Player;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import fr.flowsqy.abstractmenu.inventory.EventInventory;
import fr.flowsqy.stelyclaim.common.ConfigurationFormattedMessages;
import fr.flowsqy.stelyclaimconfig.menu.FlagItem;

public class PlayerMenuSession {

    private final ConfigurationFormattedMessages messages;
    private final EventInventory eventInventory;
    private final FlagManager flagManager;
    private final PageManager pageManager;

    public PlayerMenuSession(ConfigurationFormattedMessages messages, EventInventory eventInventory, ProtectedRegion region, List<Integer> flagSlots) {
        this.messages = messages;
        this.eventInventory = eventInventory;
        pageManager = new PageManager();
        flagManager = new FlagManager(messages, region, flagSlots, pageManager);
    }

    public FlagManager getFlagManager() {
        return flagManager;
    }

    public PageManager getPageManager() {
        return pageManager;
    }

    public void load(Player player, Map<String, FlagItem> flagItems) {
        flagManager.load(player, flagItems);
        pageManager.load(flagManager.getFlagSlotHandler().getFlagSlots().size(), flagManager.getAvailableFlags().size());
    }

    public void open(Player player) {
        final FlagSlotHandler flagSlotHandler = flagManager.getFlagSlotHandler();
        flagSlotHandler.createPageFlagsItr();
        eventInventory.open(player, player.getUniqueId());
        flagSlotHandler.clearPageFlagsItr();
    }

    public void refresh(Player player) {
        final FlagSlotHandler flagSlotHandler = flagManager.getFlagSlotHandler();
        flagSlotHandler.createPageFlagsItr();
        eventInventory.refresh(player.getUniqueId(), player);
        flagSlotHandler.clearPageFlagsItr();
    }

}
