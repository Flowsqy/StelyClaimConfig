package fr.flowsqy.stelyclaimconfig.menu.session;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import fr.flowsqy.abstractmenu.inventory.EventInventory;
import fr.flowsqy.stelyclaimconfig.menu.FlagItem;
import fr.flowsqy.stelyclaimconfig.menu.session.state.FlagStateLoader;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public class PlayerMenuSession {

    private final EventInventory eventInventory;
    private final FlagManager flagManager;
    private final PageManager pageManager;
    private final ConversationManager conversationManager;

    public PlayerMenuSession(@NotNull FlagStateLoader flagStateLoader, @NotNull EventInventory eventInventory, @NotNull ProtectedRegion region, @NotNull List<Integer> flagSlots) {
        this.eventInventory = eventInventory;
        pageManager = new PageManager();
        flagManager = new FlagManager(flagStateLoader, region, flagSlots, pageManager);
        conversationManager = new ConversationManager();
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

    public ConversationManager getConversationManager() {
        return conversationManager;
    }

    public void close() {
        conversationManager.closeConversations();
    }

}
