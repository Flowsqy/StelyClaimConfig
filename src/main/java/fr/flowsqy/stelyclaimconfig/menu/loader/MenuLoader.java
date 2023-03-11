package fr.flowsqy.stelyclaimconfig.menu.loader;

import fr.flowsqy.abstractmenu.factory.MenuFactory;
import fr.flowsqy.abstractmenu.inventory.EventInventory;
import fr.flowsqy.abstractmenu.item.ItemBuilder;
import fr.flowsqy.stelyclaim.StelyClaimPlugin;
import fr.flowsqy.stelyclaimconfig.StelyClaimConfigPlugin;
import fr.flowsqy.stelyclaimconfig.menu.FlagItem;
import fr.flowsqy.stelyclaimconfig.menu.MenuManager;
import fr.flowsqy.stelyclaimconfig.menu.SCCRegisterHandler;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MenuLoader {

    /**
     * Deserialize items mappings
     *
     * @param menuConfiguration The {@link ConfigurationSection} that contains the menu
     */
    public Map<String, FlagItem> loadFlagItemById(ConfigurationSection menuConfiguration) {
        final ConfigurationSection section = menuConfiguration.getConfigurationSection("items");
        if (section == null) {
            return Collections.emptyMap();
        }

        final Map<String, FlagItem> flagItemById = new HashMap<>();
        for (String sectionKey : section.getKeys(false)) {
            final ConfigurationSection itemSection = section.getConfigurationSection(sectionKey);
            // Skip if it's not a section
            if (itemSection == null) {
                continue;
            }
            // Get the item and its order in the list
            final ItemBuilder itemBuilder = ItemBuilder.deserialize(itemSection);
            final int order = itemSection.getInt("order", -1);
            // Register the FlagItem
            flagItemById.put(sectionKey, new FlagItem(order > -1 ? order : Integer.MAX_VALUE, itemBuilder));
        }
        return flagItemById;
    }

    /**
     * Deserialize the inventory
     *
     * @param menuConfiguration The {@link ConfigurationSection} that contains the menu
     */
    public EventInventory loadInventory(YamlConfiguration menuConfiguration, StelyClaimConfigPlugin plugin, StelyClaimPlugin stelyClaimPlugin, MenuManager menuManager, List<Integer> flagSlots) {
        final MenuFactory factory = new MenuFactory(plugin);
        // Create the GUI
        final ConfigurationSection menuSection = menuConfiguration.getConfigurationSection("menu");
        // Add a default GUI if none is specified in the configuration
        if (menuSection == null) {
            return new EventInventory(factory, "", 1);
        }

        final SCCRegisterHandler registerHandler = new SCCRegisterHandler(
                menuManager,
                plugin,
                stelyClaimPlugin,
                menuSection
        );

        // Deserialize the GUI
        final EventInventory eventInventory = EventInventory.deserialize(
                menuSection,
                factory,
                registerHandler
        );

        registerHandler.link(flagSlots);

        return eventInventory;
    }
}
