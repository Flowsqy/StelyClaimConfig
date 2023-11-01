package fr.flowsqy.stelyclaimconfig.conversation.impl;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;

import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.protection.flags.StringFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import fr.flowsqy.stelyclaim.api.ClaimHandler;
import fr.flowsqy.stelyclaim.api.ClaimOwner;
import fr.flowsqy.stelyclaim.api.FormattedMessages;
import fr.flowsqy.stelyclaim.api.ProtocolManager;
import fr.flowsqy.stelyclaim.common.ConfigurationFormattedMessages;
import fr.flowsqy.stelyclaim.internal.PlayerHandler;
import fr.flowsqy.stelyclaim.internal.PlayerOwner;
import fr.flowsqy.stelyclaim.protocol.RegionFinder;
import fr.flowsqy.stelyclaim.util.WorldName;
import fr.flowsqy.stelyclaimconfig.menu.MenuManager;
import fr.flowsqy.stelyclaimconfig.menu.session.FlagManager;

public class ConvGetFlagText extends StringPrompt {

    private final StringFlag flag;
    private final FlagManager flagManager;
    private final YamlConfiguration config;
    private final ConfigurationFormattedMessages messages;
    private final MenuManager menuManager;
    private final ProtocolManager protocolManager;

    public ConvGetFlagText(StringFlag flag, FlagManager flagManager, YamlConfiguration config,
            ConfigurationFormattedMessages messages, MenuManager menuManager, ProtocolManager protocolManager) {
        this.flag = flag;
        this.flagManager = flagManager;
        this.config = config;
        this.messages = messages;
        this.menuManager = menuManager;
        this.protocolManager = protocolManager;
    }

    @Override
    public Prompt acceptInput(ConversationContext con, String answer) {
        Player author = (Player) con.getForWhom();
        
        if (containsBlockedColors(answer)) {
            author.sendRawMessage(messages.getMessage("string-flags.contains-blocked-color").replace("%prefix%", messages.getMessage("prefix")));
            return this;
        }
        
        flagManager.getFlagStateManager().defineStringFlag(flag, answer);

        PlayerOwner owner = new PlayerOwner((Player) author);
        org.bukkit.World world = author.getWorld();
        RegionManager regionManager = RegionFinder.getRegionManager((World) new WorldName(world.getName()), author,
                (FormattedMessages) this.messages);
        if (regionManager == null)
            return null;
        PlayerHandler handler = this.protocolManager.getHandler("player");
        String regionName = RegionFinder.getRegionName((ClaimHandler) handler, (ClaimOwner) owner);
        ProtectedRegion region = RegionFinder.mustExist(regionManager, regionName, owner.getName(), owner.own(author),
                author, (FormattedMessages) this.messages);
        if (region == null)
            return null;
        
        this.menuManager.reopen(author, region);

        return null;
    }

    @Override
    public String getPromptText(ConversationContext arg0) {
        return messages.getMessage("string-flags.send-flag-text")
            .replace("%prefix%", messages.getMessage("prefix"));
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
