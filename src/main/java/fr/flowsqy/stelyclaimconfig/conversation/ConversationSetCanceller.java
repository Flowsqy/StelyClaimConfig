package fr.flowsqy.stelyclaimconfig.conversation;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationCanceller;
import org.bukkit.conversations.ConversationContext;

public class ConversationSetCanceller implements ConversationCanceller {

    private final YamlConfiguration config;

    public ConversationSetCanceller(YamlConfiguration config) {
        this.config = config;
    }

    @Override
    public void setConversation(Conversation conversation) {
        return;
    }

    @Override
    public boolean cancelBasedOnInput(ConversationContext context, String input) {
        return config.getStringList("conversation-cancel-words").contains(input);
    }

    @Override
    public ConversationCanceller clone() {
        return this;
    }
}