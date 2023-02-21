package fr.flowsqy.stelyclaimconfig.menu.loader;

import fr.flowsqy.abstractmenu.item.ItemBuilder;
import fr.flowsqy.stelyclaimconfig.menu.MenuManager;
import fr.flowsqy.stelyclaimconfig.menu.item.ChangePageCreator;

import java.util.function.Function;

public class ChangePageItemLinker {

    private final MenuManager menuManager;
    private final Function<Integer, Integer> pageCursorFunction;
    private ItemBuilder changePageItem;
    private ItemBuilder emptyItem;

    public ChangePageItemLinker(MenuManager menuManager, Function<Integer, Integer> pageCursorFunction) {
        this.menuManager = menuManager;
        this.pageCursorFunction = pageCursorFunction;
    }

    public void setChangePageItem(ItemBuilder changePageItem) {
        this.changePageItem = changePageItem;
    }

    public void setEmptyItem(ItemBuilder emptyItem) {
        this.emptyItem = emptyItem;
    }

    public void link() {
        if (changePageItem != null) {
            changePageItem.creatorListener(
                    new ChangePageCreator(
                            menuManager,
                            pageCursorFunction,
                            emptyItem == null ? new ItemBuilder() : emptyItem
                    )
            );
        }
    }

}
