package fr.flowsqy.stelyclaimconfig.menu.loader;

import fr.flowsqy.stelyclaimconfig.menu.StateText;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.configuration.ConfigurationSection;

public class StateTextLoader {

    public StateText load(ConfigurationSection menuSection) {
        final String allow = getFormattedText(menuSection, "allow");
        final String deny = getFormattedText(menuSection, "deny");
        return new StateText(allow, deny);
    }

    private String getFormattedText(ConfigurationSection menuSection, String path) {
        return formatText(getText(menuSection, path));
    }

    private String getText(ConfigurationSection menuSection, String path) {
        return menuSection.getString("items.flags.state." + path);
    }

    private String formatText(String text) {
        return text == null ? "" : ChatColor.translateAlternateColorCodes('&', text);
    }

}
