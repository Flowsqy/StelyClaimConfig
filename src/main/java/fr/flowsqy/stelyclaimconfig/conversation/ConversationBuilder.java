package fr.flowsqy.stelyclaimconfig.conversation;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.conversations.Prompt;
import org.bukkit.entity.Player;

import fr.flowsqy.stelyclaimconfig.StelyClaimConfigPlugin;

public class ConversationBuilder {

    private final StelyClaimConfigPlugin plugin;
    private final YamlConfiguration config;

    public ConversationBuilder(StelyClaimConfigPlugin plugin, YamlConfiguration config) {
        this.plugin = plugin;
        this.config = config;
    }

    public void getNameInput(Player player, Prompt prompt) {
        ConversationFactory cf = new ConversationFactory(plugin);

        cf.withFirstPrompt(prompt);
        cf.withLocalEcho(false);
        cf.withConversationCanceller(new ConversationSetCanceller(config));
        cf.withTimeout(config.getInt("conversation-timeout"));

        Conversation conv = cf.buildConversation(player);
        conv.begin();
        return;
    }
}