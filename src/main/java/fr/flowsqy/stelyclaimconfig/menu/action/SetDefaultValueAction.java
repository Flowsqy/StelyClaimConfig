package fr.flowsqy.stelyclaimconfig.menu.action;

import fr.flowsqy.stelyclaimconfig.menu.MenuManager;
import fr.flowsqy.stelyclaimconfig.menu.session.PlayerMenuSession;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.function.Consumer;

public class SetDefaultValueAction implements Consumer<InventoryClickEvent> {

    private final MenuManager menuManager;

    public SetDefaultValueAction(MenuManager menuManager) {
        this.menuManager = menuManager;
    }

    @Override
    public void accept(InventoryClickEvent event) {
        final Player player = (Player) event.getWhoClicked();
        final PlayerMenuSession session = menuManager.getSession(player.getUniqueId());
        if (session == null) {
            return;
        }

        session.getFlagManager().getFlagStateManager().setDefault();

        session.refresh(player);
    }

}
