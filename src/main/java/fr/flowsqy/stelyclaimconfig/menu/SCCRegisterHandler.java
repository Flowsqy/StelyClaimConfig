package fr.flowsqy.stelyclaimconfig.menu;

import fr.flowsqy.abstractmenu.inventory.EventInventory;
import fr.flowsqy.abstractmenu.item.ItemBuilder;
import fr.flowsqy.stelyclaim.StelyClaimPlugin;
import fr.flowsqy.stelyclaimconfig.StelyClaimConfigPlugin;
import fr.flowsqy.stelyclaimconfig.menu.action.*;
import fr.flowsqy.stelyclaimconfig.menu.item.ChangePageCreator;
import fr.flowsqy.stelyclaimconfig.menu.item.FlagCreatorListener;
import fr.flowsqy.stelyclaimconfig.menu.item.InfoCreatorListener;

import java.util.List;
import java.util.function.Function;

/**
 * Custom handler for Inventory deserialization
 * <p>
 * Maps every item to its function
 */
public class SCCRegisterHandler implements EventInventory.RegisterHandler {

    private final Function<Integer, Integer> PREVIOUS_PAGE_FUNCTION = currentPage -> currentPage - 1;
    private final Function<Integer, Integer> NEXT_PAGE_FUNCTION = currentPage -> currentPage + 1;
    private final StelyClaimConfigPlugin plugin;
    private final StelyClaimPlugin stelyClaimPlugin;
    private final MenuManager menuManager;
    private final StateText stateText;

    public SCCRegisterHandler(MenuManager menuManager, StelyClaimConfigPlugin plugin, StelyClaimPlugin stelyClaimPlugin, StateText stateText) {
        this.menuManager = menuManager;
        this.plugin = plugin;
        this.stelyClaimPlugin = stelyClaimPlugin;
        this.stateText = stateText;
    }

    @Override
    public void handle(EventInventory eventInventory, String key, ItemBuilder builder, List<Integer> slots) {
        switch (key) {
            // Valid item
            case "valid":
                eventInventory.register(
                        builder,
                        new ValidAction(menuManager, stelyClaimPlugin.getProtocolManager(), plugin.getMessages()),
                        slots
                );
                break;
            // Leave item
            case "leave":
                eventInventory.register(
                        builder,
                        new LeaveAction(),
                        slots
                );
                break;
            // Flags items
            case "flags":
                // Add the custom creator to display the item matching the flag
                builder.creatorListener(new FlagCreatorListener(menuManager, stateText));
                menuManager.registerSlotsFlags(slots);
                // Register the items flags
                eventInventory.register(
                        builder,
                        new FlagsAction(menuManager),
                        slots
                );
                break;
            // Information item
            case "info":
                builder.creatorListener(new InfoCreatorListener(menuManager));
                eventInventory.register(builder, slots);
                break;
            // Previous item
            case "previous":
                builder.creatorListener(new ChangePageCreator(menuManager, PREVIOUS_PAGE_FUNCTION));
                eventInventory.register(
                        builder,
                        new ChangePageAction(menuManager, PREVIOUS_PAGE_FUNCTION),
                        slots
                );
                break;
            // Next item
            case "next":
                builder.creatorListener(new ChangePageCreator(menuManager, NEXT_PAGE_FUNCTION));
                eventInventory.register(
                        builder,
                        new ChangePageAction(menuManager, NEXT_PAGE_FUNCTION),
                        slots
                );
                break;
            // Toggle off everything item
            case "toggle-all-off":
                eventInventory.register(
                        builder,
                        new SetAllValueAction(menuManager, false),
                        slots
                );
                break;
            // Toggle on everything item
            case "toggle-all-on":
                eventInventory.register(
                        builder,
                        new SetAllValueAction(menuManager, true),
                        slots
                );
                break;
            // Any other item
            default:
                eventInventory.register(
                        builder,
                        slots
                );
        }
    }

}
