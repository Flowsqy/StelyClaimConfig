package fr.flowsqy.stelyclaimconfig.menu.loader;

import fr.flowsqy.abstractmenu.item.ItemBuilder;
import fr.flowsqy.stelyclaimconfig.menu.MenuManager;
import fr.flowsqy.stelyclaimconfig.menu.item.flag.FlagCreatorListener;
import fr.flowsqy.stelyclaimconfig.menu.item.flag.effect.FlagEffects;

public class FlagItemLinker {

    private final MenuManager menuManager;
    private ItemBuilder flagItem;
    private FlagEffects flagEffects;
    private ItemBuilder emptyItem;

    public FlagItemLinker(MenuManager menuManager) {
        this.menuManager = menuManager;
    }

    public void setFlagItem(ItemBuilder flagItem, FlagEffects flagEffects) {
        this.flagItem = flagItem;
        this.flagEffects = flagEffects;
    }

    public void setEmptyItem(ItemBuilder emptyItem) {
        this.emptyItem = emptyItem;
    }

    public void link() {
        if (flagItem != null) {
            flagItem.creatorListener(
                    new FlagCreatorListener(
                            menuManager,
                            emptyItem == null ? new ItemBuilder() : emptyItem,
                            flagEffects)
            );
        }
    }

}
