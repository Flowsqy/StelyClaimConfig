package fr.flowsqy.stelyclaimconfig.menu.action;

import fr.flowsqy.stelyclaimconfig.menu.MenuManager;
import fr.flowsqy.stelyclaimconfig.menu.session.PlayerMenuSession;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

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
        session.getFlagManager().getFlagStateManager().setAllValue(state);

        session.refresh(player);
    }

}
