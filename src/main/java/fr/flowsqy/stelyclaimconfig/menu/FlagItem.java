package fr.flowsqy.stelyclaimconfig.menu;

import fr.flowsqy.abstractmenu.item.ItemBuilder;

public class FlagItem {

    private final int order;
    private final ItemBuilder builder;
    
    public FlagItem(int order, ItemBuilder builder) {
        this.order = order;
        this.builder = builder;
    }

    public int getOrder() {
        return order;
    }

    public ItemBuilder getBuilder() {
        return builder;
    }
}
