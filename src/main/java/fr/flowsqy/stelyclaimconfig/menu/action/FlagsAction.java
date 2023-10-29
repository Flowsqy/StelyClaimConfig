package fr.flowsqy.stelyclaimconfig.menu.action;

import java.util.function.Consumer;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.StringFlag;

import fr.flowsqy.stelyclaim.common.ConfigurationFormattedMessages;
import fr.flowsqy.stelyclaimconfig.conversation.ConversationBuilder;
import fr.flowsqy.stelyclaimconfig.conversation.impl.ConvGetFlagText;
import fr.flowsqy.stelyclaimconfig.menu.MenuManager;
import fr.flowsqy.stelyclaimconfig.menu.session.FlagManager;
import fr.flowsqy.stelyclaimconfig.menu.session.PlayerMenuSession;

public class FlagsAction implements Consumer<InventoryClickEvent> {

    private final MenuManager menuManager;
    private final ConversationBuilder conv;
    private final YamlConfiguration config;
    private final ConfigurationFormattedMessages messages;

    public FlagsAction(MenuManager menuManager, ConversationBuilder conv, YamlConfiguration config, ConfigurationFormattedMessages messages) {
        this.menuManager = menuManager;
        this.conv = conv;
        this.config = config;
        this.messages = messages;
    }

    /**
     * Handle a click on a flag item
     *
     * @param event The {@link InventoryClickEvent} that trigger the method
     */
    @Override
    public void accept(InventoryClickEvent event) {
        final Player player = (Player) event.getWhoClicked();
        // Get the session
        final PlayerMenuSession session = menuManager.getSession(player.getUniqueId());
        if (session == null) {
            return;
        }
        final FlagManager flagManager = session.getFlagManager();
        // Get the flag
        final Flag<?> flag = flagManager.getFlagSlotHandler().getAttachedFlag(event.getSlot());
        if (flag == null) {
            return;
        }
        
        if (flag instanceof StateFlag) {
            flagManager.getFlagStateManager().toggleFlag((StateFlag) flag);
        }else if (flag instanceof StringFlag){
            player.closeInventory();
            // flagManager.getFlagStateManager().defineStringFlag((StringFlag) flag, null);
            conv.getNameInput(player, new ConvGetFlagText((StringFlag) flag, flagManager, config, messages));
        }
        session.refresh(player);
    }
}
