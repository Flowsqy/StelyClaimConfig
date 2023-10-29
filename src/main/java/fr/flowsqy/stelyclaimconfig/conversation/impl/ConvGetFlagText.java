package fr.flowsqy.stelyclaimconfig.conversation.impl;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;

import com.sk89q.worldguard.protection.flags.StringFlag;

import fr.flowsqy.stelyclaim.common.ConfigurationFormattedMessages;
import fr.flowsqy.stelyclaimconfig.menu.session.FlagManager;

public class ConvGetFlagText extends StringPrompt {

    private final StringFlag flag;
    private final FlagManager flagManager;
    private final YamlConfiguration config;
    private final ConfigurationFormattedMessages messages;

    public ConvGetFlagText(StringFlag flag, FlagManager flagManager, YamlConfiguration config, ConfigurationFormattedMessages messages) {
        this.flag = flag;
        this.flagManager = flagManager;
        this.config = config;
        this.messages = messages;
    }

    @Override
    public Prompt acceptInput(ConversationContext con, String answer) {
        Player author = (Player) con.getForWhom();
        
        if (containsBlockedColors(answer)) {
            messages.sendMessage(author, "string-flags.contains-blocked-color");
            return this;
        }
        
        flagManager.getFlagStateManager().defineStringFlag(flag, answer);

        return null;
    }

    @Override
    public String getPromptText(ConversationContext arg0) {
        return messages.getMessage("string-flags.send-flag-text");
    }

    private boolean containsBlockedColors(String input){
        for (String color : config.getStringList("blocked-colors")) {
            if (input.contains(color)) {
                return true;
            }
        }
        return false;
    }
}
