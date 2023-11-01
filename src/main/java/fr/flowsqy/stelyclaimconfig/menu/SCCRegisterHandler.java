package fr.flowsqy.stelyclaimconfig.menu;

import fr.flowsqy.abstractmenu.inventory.EventInventory;
import fr.flowsqy.abstractmenu.item.ItemBuilder;
import fr.flowsqy.stelyclaim.StelyClaimPlugin;
import fr.flowsqy.stelyclaimconfig.StelyClaimConfigPlugin;
import fr.flowsqy.stelyclaimconfig.menu.action.*;
import fr.flowsqy.stelyclaimconfig.menu.item.InfoCreatorListener;
import fr.flowsqy.stelyclaimconfig.menu.item.flag.effect.FlagEffectLoader;
import fr.flowsqy.stelyclaimconfig.menu.item.flag.effect.FlagEffects;
import fr.flowsqy.stelyclaimconfig.menu.loader.ChangePageItemLinker;
import fr.flowsqy.stelyclaimconfig.menu.loader.FlagItemLinker;
import fr.flowsqy.stelyclaimconfig.menu.loader.SlotFlagLinker;
import org.bukkit.configuration.ConfigurationSection;

import java.util.List;
import java.util.Objects;
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
    private final ConfigurationSection menuSection;
    private final MenuManager menuManager;
    private final SlotFlagLinker slotFlagLinker;
    private final FlagItemLinker flagItemLinker;
    private final ChangePageItemLinker previousItemLinker, nextItemLinker;

    public SCCRegisterHandler(MenuManager menuManager, StelyClaimConfigPlugin plugin, StelyClaimPlugin stelyClaimPlugin, ConfigurationSection menuSection) {
        this.menuManager = menuManager;
        this.plugin = plugin;
        this.stelyClaimPlugin = stelyClaimPlugin;
        this.menuSection = menuSection;
        slotFlagLinker = new SlotFlagLinker();
        flagItemLinker = new FlagItemLinker(menuManager);
        previousItemLinker = new ChangePageItemLinker(menuManager, PREVIOUS_PAGE_FUNCTION);
        nextItemLinker = new ChangePageItemLinker(menuManager, NEXT_PAGE_FUNCTION);
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
                // Load effects
                final FlagEffectLoader flagEffectLoader = new FlagEffectLoader();
                final FlagEffects flagEffects = flagEffectLoader.load(Objects.requireNonNull(menuSection.getConfigurationSection("items." + key)));

                // Add the custom creator to display the item matching the flag
                flagItemLinker.setFlagItem(builder, flagEffects);
                slotFlagLinker.setFlagSlots(slots);
                // Register the items flags
                eventInventory.register(
                        builder,
                        new FlagsAction(menuManager, plugin.getConversationBuilder(), plugin.getConfig(), plugin.getMessages(), stelyClaimPlugin.getProtocolManager()),
                        slots
                );
                break;
            // Empty flags item
            case "empty-flags":
                flagItemLinker.setEmptyItem(builder);
                break;
            // Information item
            case "info":
                builder.creatorListener(new InfoCreatorListener(menuManager));
                eventInventory.register(builder, slots);
                break;
            // Previous item
            case "previous":
                previousItemLinker.setChangePageItem(builder);
                eventInventory.register(
                        builder,
                        new ChangePageAction(menuManager, PREVIOUS_PAGE_FUNCTION),
                        slots
                );
                break;
            // Empty previous item
            case "empty-previous":
                previousItemLinker.setEmptyItem(builder);
                break;
            // Next item
            case "next":
                nextItemLinker.setChangePageItem(builder);
                eventInventory.register(
                        builder,
                        new ChangePageAction(menuManager, NEXT_PAGE_FUNCTION),
                        slots
                );
                break;
            // Empty previous item
            case "empty-next":
                nextItemLinker.setEmptyItem(builder);
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
            // Set every flag to default value item
            case "set-default":
                eventInventory.register(
                        builder,
                        new SetDefaultValueAction(menuManager),
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

    public void link(List<Integer> flagSlots) {
        slotFlagLinker.link(flagSlots);
        flagItemLinker.link(plugin.getMessages());
        previousItemLinker.link();
        nextItemLinker.link();
    }
}
