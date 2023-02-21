package fr.flowsqy.stelyclaimconfig.menu.action;

import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import fr.flowsqy.stelyclaimconfig.menu.MenuManager;
import fr.flowsqy.stelyclaimconfig.menu.session.FlagManager;
import fr.flowsqy.stelyclaimconfig.menu.session.PlayerMenuSession;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.function.Consumer;

public class FlagsAction implements Consumer<InventoryClickEvent> {

    private final MenuManager menuManager;

    public FlagsAction(MenuManager menuManager) {
        this.menuManager = menuManager;
    }

    /**
     * Handle a click on a flag item
     *
     * @param event The {@link InventoryClickEvent} that trigger the method
     */
    @Override
    public void accept(InventoryClickEvent event) {
        final Player player = (Player) event.getWhoClicked();
        // Get the session
        final PlayerMenuSession session = menuManager.getSession(player.getUniqueId());
        if (session == null) {
            return;
        }
        final FlagManager flagManager = session.getFlagManager();
        // Get the flag
        final Flag<?> flag = flagManager.getFlagSlotHandler().getAttachedFlag(event.getSlot());
        if (flag == null) {
            return;
        }
        flagManager.getFlagStateManager().toggleFlag((StateFlag) flag);
        session.refresh(player);
    }
}
