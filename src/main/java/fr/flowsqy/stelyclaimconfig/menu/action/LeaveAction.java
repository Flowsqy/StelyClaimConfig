package fr.flowsqy.stelyclaimconfig.menu.action;

import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.function.Consumer;

public class LeaveAction implements Consumer<InventoryClickEvent> {

    @Override
    public void accept(InventoryClickEvent event) {
        event.getWhoClicked().closeInventory();
    }
}
