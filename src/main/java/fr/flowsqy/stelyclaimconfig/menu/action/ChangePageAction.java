package fr.flowsqy.stelyclaimconfig.menu.action;

import fr.flowsqy.stelyclaimconfig.menu.MenuManager;
import fr.flowsqy.stelyclaimconfig.menu.session.PageManager;
import fr.flowsqy.stelyclaimconfig.menu.session.PlayerMenuSession;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.function.Consumer;
import java.util.function.Function;

public class ChangePageAction implements Consumer<InventoryClickEvent> {

    private final MenuManager menuManager;
    private final Function<Integer, Integer> pageCursorFunction;

    public ChangePageAction(MenuManager menuManager, Function<Integer, Integer> pageCursorFunction) {
        this.menuManager = menuManager;
        this.pageCursorFunction = pageCursorFunction;
    }

    @Override
    public void accept(InventoryClickEvent event) {
        final Player player = (Player) event.getWhoClicked();
        final PlayerMenuSession session = menuManager.getSession(player.getUniqueId());
        if (session == null) {
            return;
        }
        final PageManager pageManager = session.getPageManager();
        final int newPage = pageCursorFunction.apply(pageManager.getCurrentPage());
        if (!pageManager.canGoTo(newPage)) {
            return;
        }

        pageManager.setCurrentPage(newPage);
        session.refresh(player);
    }

}
