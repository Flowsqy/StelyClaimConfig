package fr.flowsqy.stelyclaimconfig.conversation;

import org.bukkit.configuration.Configuration;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

public class ConversationBuilderLoader {

    public ConversationBuilder load(@NotNull Plugin plugin, @NotNull Configuration configuration) {
        final Set<String> cancelWords = new HashSet<>(configuration.getStringList("conversation-cancel-words"));
        final int timeout = configuration.getInt("conversation-timeout");
        return new ConversationBuilder(plugin, cancelWords, timeout);
    }

}
