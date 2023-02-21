package fr.flowsqy.stelyclaimconfig.menu.item;

import fr.flowsqy.abstractmenu.item.CreatorCopy;
import fr.flowsqy.abstractmenu.item.ItemBuilder;
import fr.flowsqy.stelyclaimconfig.menu.MenuManager;
import fr.flowsqy.stelyclaimconfig.menu.session.PlayerMenuSession;
import org.bukkit.entity.Player;

import java.util.function.Function;

public class ChangePageCreator extends CreatorCopy {

    private final MenuManager menuManager;
    private final Function<Integer, Integer> pageCursorFunction;
    private final ItemBuilder emptyItem = new ItemBuilder();

    public ChangePageCreator(MenuManager menuManager, Function<Integer, Integer> pageCursorFunction) {
        this.menuManager = menuManager;
        this.pageCursorFunction = pageCursorFunction;
    }

    @Override
    public void open(Player player) {
        final PlayerMenuSession session = menuManager.getSession(player.getUniqueId());
        if (session == null || !session.getPageManager().canGoTo(pageCursorFunction)) {
            original(emptyItem);
        }
    }

    @Override
    public void close(Player player) {
        original(null);
    }

}
