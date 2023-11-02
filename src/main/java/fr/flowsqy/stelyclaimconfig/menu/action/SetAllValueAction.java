package fr.flowsqy.stelyclaimconfig.menu.action;

import fr.flowsqy.stelyclaimconfig.menu.MenuManager;
import fr.flowsqy.stelyclaimconfig.menu.session.FlagStateManager;
import fr.flowsqy.stelyclaimconfig.menu.session.PlayerMenuSession;
import fr.flowsqy.stelyclaimconfig.menu.session.state.FlagState;
import fr.flowsqy.stelyclaimconfig.menu.session.state.StateFlagState;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class SetAllValueAction implements Consumer<InventoryClickEvent> {

    private final MenuManager menuManager;
    private final boolean state;

    public SetAllValueAction(MenuManager menuManager, boolean state) {
        this.menuManager = menuManager;
        this.state = state;
    }

    @Override
    public void accept(InventoryClickEvent event) {
        final Player player = (Player) event.getWhoClicked();
        final PlayerMenuSession session = menuManager.getSession(player.getUniqueId());
        if (session == null) {
            return;
        }
        setAll(session.getFlagManager().getFlagStateManager(), state);
        session.refresh(player);
    }

    private void setAll(@NotNull FlagStateManager flagStateManager, boolean state) {
        for (String flag : flagStateManager.getAvailableFlags()) {
            final FlagState flagState = flagStateManager.getState(flag);
            if (!(flagState instanceof StateFlagState stateFlagState)) {
                continue;
            }
            stateFlagState.setValue(state);
        }
    }

}
