package fr.flowsqy.stelyclaimconfig.menu.session;

import org.bukkit.conversations.Conversation;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

public class ConversationManager {

    private final Set<Conversation> pendingConversations;

    public ConversationManager() {
        pendingConversations = new HashSet<>();
    }

    public void registerConversation(@NotNull Conversation conversation) {
        pendingConversations.add(conversation);
        conversation.addConversationAbandonedListener(e -> unregisterConversation(conversation));
    }

    private void unregisterConversation(@NotNull Conversation conversation) {
        pendingConversations.remove(conversation);
    }

    public void closeConversations() {
        for (Conversation conversation : pendingConversations.toArray(new Conversation[0])) {
            if (conversation.getState() == Conversation.ConversationState.STARTED) {
                conversation.abandon();
            }
        }
    }

}
