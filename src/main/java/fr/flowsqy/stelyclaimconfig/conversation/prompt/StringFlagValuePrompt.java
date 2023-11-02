package fr.flowsqy.stelyclaimconfig.conversation.prompt;

import fr.flowsqy.stelyclaim.api.FormattedMessages;
import fr.flowsqy.stelyclaimconfig.menu.MenuManager;
import fr.flowsqy.stelyclaimconfig.menu.session.state.StringFlagState;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

public class StringFlagValuePrompt extends StringPrompt {

    //private final String[] blockedColors; // config.getStringList("blocked-colors")
    private final Predicate<String> inputPredicate;
    private final StringFlagState flagState;
    private final FormattedMessages messages;
    private final MenuManager menuManager;

    public StringFlagValuePrompt(@NotNull Predicate<String> inputPredicate, @NotNull StringFlagState flagState, @NotNull FormattedMessages messages, @NotNull MenuManager menuManager) {
        this.inputPredicate = inputPredicate;
        this.flagState = flagState;
        this.messages = messages;
        this.menuManager = menuManager;
    }

    @Override
    public Prompt acceptInput(@NotNull ConversationContext context, String input) {
        final Player author = (Player) context.getForWhom();

        if (!inputPredicate.test(input)) {
            author.sendRawMessage(messages.getFormattedMessage("string-flags.contains-blocked-color"));
            return this;
        }

        final String value = input == null ? null : ChatColor.translateAlternateColorCodes('&', input);
        flagState.setValue(value);
        menuManager.resume(author);

        return END_OF_CONVERSATION;
    }

    @Override
    public @NotNull String getPromptText(@NotNull ConversationContext context) {
        return messages.getFormattedMessage("string-flags.send-flag-text");
    }

    /*
    private boolean containsBlockedColors(String input){
        for (String color : blockedColors) {
            if (input.contains(color)) {
                return true;
            }
        }
        return false;
    }*/

}
