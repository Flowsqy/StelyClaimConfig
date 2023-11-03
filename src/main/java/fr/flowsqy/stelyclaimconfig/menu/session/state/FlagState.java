package fr.flowsqy.stelyclaimconfig.menu.session.state;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import fr.flowsqy.stelyclaimconfig.menu.session.PlayerMenuSession;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;

public interface FlagState {

    @NotNull
    FlagStateCreatorListener getCreatorListener();

    void apply(@NotNull ProtectedRegion region);

    void setDefault();

    void handleUserInput(@NotNull InventoryClickEvent event, @NotNull PlayerMenuSession session);

}
