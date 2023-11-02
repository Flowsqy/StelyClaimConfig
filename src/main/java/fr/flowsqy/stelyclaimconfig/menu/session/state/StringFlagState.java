package fr.flowsqy.stelyclaimconfig.menu.session.state;

import com.sk89q.worldguard.protection.flags.StringFlag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import fr.flowsqy.stelyclaim.api.FormattedMessages;
import fr.flowsqy.stelyclaimconfig.StelyClaimConfigPlugin;
import fr.flowsqy.stelyclaimconfig.conversation.ConversationBuilder;
import fr.flowsqy.stelyclaimconfig.conversation.prompt.StringFlagValuePrompt;
import fr.flowsqy.stelyclaimconfig.menu.MenuManager;
import fr.flowsqy.stelyclaimconfig.menu.session.PlayerMenuSession;
import org.bukkit.conversations.Prompt;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class StringFlagState implements FlagState {

    private final StringFlag flag;
    private final String defaultValue = null;
    private String value;

    public StringFlagState(@NotNull StringFlag flag, @Nullable String value) {
        this.flag = flag;
        this.value = value;
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

        // TODO Use dependency injection pattern
        final StelyClaimConfigPlugin plugin = JavaPlugin.getPlugin(StelyClaimConfigPlugin.class);
        final ConversationBuilder conversationBuilder = plugin.getConversationBuilder();
        final FormattedMessages messages = plugin.getMessages();
        final MenuManager menuManager = plugin.getMenuManager();

        menuManager.pause(player);
        player.closeInventory();
        // TODO Check for cancelled colors
        final Prompt prompt = new StringFlagValuePrompt(s -> true, this, messages, menuManager);
        conversationBuilder.buildConversation(player, prompt).begin();
    }

    public void setValue(@Nullable String value) {
        this.value = value;
    }

}
