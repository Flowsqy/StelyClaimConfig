package fr.flowsqy.stelyclaimconfig.menu.session.state;

import com.sk89q.worldguard.protection.flags.StringFlag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import fr.flowsqy.stelyclaim.api.FormattedMessages;
import fr.flowsqy.stelyclaimconfig.conversation.ConversationBuilder;
import fr.flowsqy.stelyclaimconfig.conversation.prompt.StringFlagValuePrompt;
import fr.flowsqy.stelyclaimconfig.menu.MenuManager;
import fr.flowsqy.stelyclaimconfig.menu.session.PlayerMenuSession;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.Prompt;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Predicate;

public class StringFlagState implements FlagState {

    private final StringInteractData stringInteractData;
    private final StringFlag flag;
    private final String defaultValue = null;
    private String value;

    public StringFlagState(@NotNull StringInteractData stringInteractData, @NotNull StringFlag flag, @Nullable String value) {
        this.stringInteractData = stringInteractData;
        this.flag = flag;
        this.value = value;
        //this.defaultValue = defaultValue;
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
        if (Objects.equals(value, flag.getDefault())) {
            region.setFlag(flag, null);
            return;
        }
        region.setFlag(flag, value);
    }

    @Override
    public void setDefault() {
        value = defaultValue;
    }

    @Override
    public void handleUserInput(@NotNull InventoryClickEvent event, @NotNull PlayerMenuSession session) {
        // TODO Maybe elaborate for RIGHT_CLICK (reset to default for example)

        final Player player = (Player) event.getWhoClicked();

        final ConversationBuilder conversationBuilder = stringInteractData.conversationBuilder();
        final FormattedMessages messages = stringInteractData.messages();
        final MenuManager menuManager = stringInteractData.menuManager();
        final Predicate<String> inputPredicate = stringInteractData.inputPredicate();

        menuManager.pause(player);
        player.closeInventory();

        final Prompt prompt = new StringFlagValuePrompt(inputPredicate, this, messages, menuManager);
        final Conversation conversation = conversationBuilder.buildConversation(player, prompt);
        session.getConversationManager().registerConversation(conversation);
        conversation.begin();
    }

    public void setValue(@Nullable String value) {
        this.value = value;
    }

}
