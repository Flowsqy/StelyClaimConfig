package fr.flowsqy.stelyclaimconfig.menu;

import fr.flowsqy.abstractmenu.factory.MenuFactory;
import fr.flowsqy.abstractmenu.inventory.EventInventory;
import fr.flowsqy.stelyclaimconfig.StelyClaimConfigPlugin;
import org.bukkit.configuration.file.YamlConfiguration;

public class MenuManager {

    private final EventInventory inventory;

    public MenuManager(StelyClaimConfigPlugin plugin, YamlConfiguration menuConfiguration) {
        final MenuFactory factory = new MenuFactory(plugin);
        this.inventory = EventInventory.deserialize(menuConfiguration.getConfigurationSection("menu"), factory, EventInventory.REGISTER);
    }

    public EventInventory getInventory() {
        return inventory;
    }

}
