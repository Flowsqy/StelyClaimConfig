package fr.flowsqy.stelyclaimconfig.menu.action;

import fr.flowsqy.stelyclaim.api.ClaimHandler;
import fr.flowsqy.stelyclaim.api.ClaimOwner;
import fr.flowsqy.stelyclaim.api.ProtocolManager;
import fr.flowsqy.stelyclaim.common.ConfigurationFormattedMessages;
import fr.flowsqy.stelyclaim.protocol.RegionFinder;
import fr.flowsqy.stelyclaimconfig.menu.MenuManager;
import fr.flowsqy.stelyclaimconfig.menu.session.FlagManager;
import fr.flowsqy.stelyclaimconfig.menu.session.PlayerMenuSession;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.function.Consumer;

public class ValidAction implements Consumer<InventoryClickEvent> {

    private final MenuManager menuManager;
    private final ProtocolManager protocolManager;
    private final ConfigurationFormattedMessages messages;

    public ValidAction(MenuManager menuManager, ProtocolManager protocolManager, ConfigurationFormattedMessages messages) {
        this.menuManager = menuManager;
        this.protocolManager = protocolManager;
        this.messages = messages;
    }

    @Override
    public void accept(InventoryClickEvent event) {
        final Player player = (Player) event.getWhoClicked();
        applySession(player);
        player.closeInventory();
    }

    public void applySession(Player player) {
        // Get the player session
        final PlayerMenuSession session = menuManager.getSession(player.getUniqueId());
        if (session == null) {
            messages.sendMessage(player, "menu.fail");
            return;
        }
        final FlagManager flagManager = session.getFlagManager();
        flagManager.apply();

        String regionName = flagManager.getRegion().getId();
        boolean ownRegion = false;
        // Load StelyClaim name and owner property
        if (RegionFinder.isCorrectId(regionName)) {
            final String[] partId = regionName.split("_", 3);
            // Get ClaimHandler
            final ClaimHandler<?> handler = protocolManager.getHandler(partId[1]);
            if (handler != null) {
                // Get Owner
                final ClaimOwner owner = handler.getOwner(partId[2]);
                if (owner != null) {
                    // Get owner name and own property
                    regionName = owner.getName();
                    ownRegion = owner.own(player);
                }
            }
        }

        messages.sendMessage(player, "menu.success" + (ownRegion ? "" : "-other"), "%region%", regionName);
    }

}
