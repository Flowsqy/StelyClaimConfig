package fr.flowsqy.stelyclaimconfig.conversation;

import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.conversations.Prompt;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class ConversationBuilder {

    private final Plugin plugin;
    private final Set<String> cancelWords;
    private final int timeout;

    public ConversationBuilder(@NotNull Plugin plugin, @NotNull Set<String> cancelWords, int timeout) {
        this.plugin = plugin;
        this.cancelWords = cancelWords;
        this.timeout = timeout;
    }

    /**
     * Build a conversation
     *
     * @param player The target player
     * @param prompt The first {@link Prompt}
     * @return A {@link Conversation} with plugin specific cancellers
     */
    public Conversation buildConversation(@NotNull Player player, @NotNull Prompt prompt) {
        final ConversationFactory factory = new ConversationFactory(plugin);

        factory.withFirstPrompt(prompt);
        factory.withLocalEcho(false);
        factory.withConversationCanceller(new ExactWordsMatchConversationCanceller(cancelWords));
        factory.withTimeout(timeout);

        return factory.buildConversation(player);
    }

}