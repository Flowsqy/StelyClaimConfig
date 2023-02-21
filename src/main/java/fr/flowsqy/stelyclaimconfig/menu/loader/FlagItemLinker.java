package fr.flowsqy.stelyclaimconfig.menu.loader;

import fr.flowsqy.abstractmenu.item.ItemBuilder;
import fr.flowsqy.stelyclaimconfig.menu.MenuManager;
import fr.flowsqy.stelyclaimconfig.menu.StateText;
import fr.flowsqy.stelyclaimconfig.menu.item.FlagCreatorListener;

public class FlagItemLinker {

    private final MenuManager menuManager;
    private final StateText stateText;
    private ItemBuilder flagItem;
    private ItemBuilder emptyItem;

    public FlagItemLinker(MenuManager menuManager, StateText stateText) {
        this.menuManager = menuManager;
        this.stateText = stateText;
    }

    public void setFlagItem(ItemBuilder flagItem) {
        this.flagItem = flagItem;
    }

    public void setEmptyItem(ItemBuilder emptyItem) {
        this.emptyItem = emptyItem;
    }

    public void link() {
        if (flagItem != null) {
            flagItem.creatorListener(
                    new FlagCreatorListener(
                            menuManager,
                            stateText,
                            emptyItem == null ? new ItemBuilder() : emptyItem
                    )
            );
        }
    }

}
