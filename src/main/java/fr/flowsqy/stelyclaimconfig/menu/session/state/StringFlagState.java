package fr.flowsqy.stelyclaimconfig.menu.session.state;

import com.sk89q.worldguard.protection.flags.StringFlag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import fr.flowsqy.stelyclaim.api.FormattedMessages;
import fr.flowsqy.stelyclaimconfig.conversation.ConversationBuilder;
import fr.flowsqy.stelyclaimconfig.conversation.prompt.StringFlagValuePrompt;
import fr.flowsqy.stelyclaimconfig.menu.MenuManager;
import fr.flowsqy.stelyclaimconfig.menu.session.PlayerMenuSession;
import org.bukkit.conversations.Prompt;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class StringFlagState implements FlagState {

    private final StringFlag flag;
    private final String defaultValue = null;
    private String value;

    public StringFlagState(@NotNull StringFlag flag) {
        this.flag = flag;
    }

    @Override
    public boolean isActive() {
        //final String defaultMessage = messages.getFormattedMessage("default-string-flags." + flagName.getName(), "%region%", playerName);
        if (defaultValue == null)
            return false;
        return !defaultValue.equals(value);
    }

    @Override
    public void apply(@NotNull ProtectedRegion region) {
        if (defaultValue == null) {
            region.setFlag(flag, null);
        } else {
            // Otherwise, set the flag
            region.setFlag(flag, value);
        }
    }

    @Override
    public void setDefault() {
        if (defaultValue == null) return;
        value = defaultValue;
    }

    @Override
    public void handleUserInput(@NotNull InventoryClickEvent event, @NotNull PlayerMenuSession session) {
        // TODO Maybe elaborate for RIGHT_CLICK (reset to default for example)

        final Player player = (Player) event.getWhoClicked();

        final ConversationBuilder conversationBuilder = null;
        final FormattedMessages messages = null;
        final MenuManager menuManager = null;

        menuManager.pause(player);
        player.closeInventory();
        final Prompt prompt = new StringFlagValuePrompt(null, this, messages, menuManager);
        conversationBuilder.buildConversation(player, prompt).begin();
    }

    public void setValue(@Nullable String value) {
        this.value = value;
    }

}
