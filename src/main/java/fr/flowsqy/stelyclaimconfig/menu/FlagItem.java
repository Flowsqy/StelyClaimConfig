package fr.flowsqy.stelyclaimconfig.menu;

import com.sk89q.worldguard.protection.flags.Flags;

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
