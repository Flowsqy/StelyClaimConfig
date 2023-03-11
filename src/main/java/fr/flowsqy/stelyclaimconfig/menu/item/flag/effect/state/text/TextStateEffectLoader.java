package fr.flowsqy.stelyclaimconfig.menu.item.flag.effect.state.text;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.configuration.ConfigurationSection;

public class TextStateEffectLoader {

    public static TextStateEffect getDefault() {
        return new TextStateEffect("", "");
    }

    public TextStateEffect load(ConfigurationSection effectStateSection) {
        final ConfigurationSection effectStateTextSection = effectStateSection.getConfigurationSection("text");
        if (effectStateTextSection == null) {
            return getDefault();
        }
        final String allow = getFormattedText(effectStateTextSection, "allow");
        final String deny = getFormattedText(effectStateTextSection, "deny");
        return new TextStateEffect(allow, deny);
    }

    private String getFormattedText(ConfigurationSection effectStateTextSection, String path) {
        return formatText(getText(effectStateTextSection, path));
    }

    private String getText(ConfigurationSection effectStateTextSection, String path) {
        return effectStateTextSection.getString(path);
    }

    private String formatText(String text) {
        return text == null ? "" : ChatColor.translateAlternateColorCodes('&', text);
    }

}
