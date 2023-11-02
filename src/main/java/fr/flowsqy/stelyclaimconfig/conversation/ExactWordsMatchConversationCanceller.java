package fr.flowsqy.stelyclaimconfig.conversation;

import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationCanceller;
import org.bukkit.conversations.ConversationContext;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class ExactWordsMatchConversationCanceller implements ConversationCanceller {

    private final Set<String> cancelWords;

    public ExactWordsMatchConversationCanceller(@NotNull Set<String> cancelWords) {
        this.cancelWords = cancelWords;
    }

    @Override
    public void setConversation(@NotNull Conversation conversation) {
    }

    @Override
    public boolean cancelBasedOnInput(@NotNull ConversationContext context, @NotNull String input) {
        return cancelWords.contains(input);
    }

    @Override
    public @NotNull ConversationCanceller clone() {
        return new ExactWordsMatchConversationCanceller(cancelWords);
    }
}