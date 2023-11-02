package fr.flowsqy.stelyclaimconfig.menu.action;

import fr.flowsqy.stelyclaimconfig.menu.MenuManager;
import fr.flowsqy.stelyclaimconfig.menu.session.FlagManager;
import fr.flowsqy.stelyclaimconfig.menu.session.PlayerMenuSession;
import fr.flowsqy.stelyclaimconfig.menu.session.state.FlagState;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;

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
    public void accept(@NotNull InventoryClickEvent event) {
        // Get the session
        final PlayerMenuSession session = menuManager.getSession(event.getWhoClicked().getUniqueId());
        if (session == null) {
            return;
        }
        final FlagManager flagManager = session.getFlagManager();
        // Get the flag
        final String flagId = flagManager.getFlagSlotHandler().getAttachedFlag(event.getSlot());
        if (flagId == null) {
            return;
        }
        // Get the state
        final FlagState flagState = flagManager.getFlagStateManager().getState(flagId);
        if (flagState == null) {
            // Maybe throw an error; it should not happen
            return;
        }
        flagState.handleUserInput(event, session);
    }
}
