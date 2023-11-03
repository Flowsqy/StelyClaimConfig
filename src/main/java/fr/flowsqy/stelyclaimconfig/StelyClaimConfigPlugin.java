package fr.flowsqy.stelyclaimconfig;

import fr.flowsqy.stelyclaim.StelyClaimPlugin;
import fr.flowsqy.stelyclaim.common.ConfigurationFormattedMessages;
import fr.flowsqy.stelyclaim.common.PrefixedConfigurationFormattedMessages;
import fr.flowsqy.stelyclaimconfig.commands.CommandManager;
import fr.flowsqy.stelyclaimconfig.menu.MenuManager;
import fr.flowsqy.stelyclaimconfig.menu.session.state.FlagStateLoaderCreator;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class StelyClaimConfigPlugin extends JavaPlugin {

    private ConfigurationFormattedMessages messages;
    private MenuManager menuManager;
    private CommandManager commandManager;

    @Override
    public void onEnable() {
        final Plugin rawSCPlugin = getServer().getPluginManager().getPlugin("StelyClaim");
        if (!(rawSCPlugin instanceof StelyClaimPlugin stelyClaimPlugin)) {
            throw new RuntimeException("Wrong StelyClaim plugin, install the correct one");
        }

        final Logger logger = getLogger();
        final File dataFolder = getDataFolder();

        if (!checkDataFolder(dataFolder)) {
            logger.log(Level.WARNING, "Can not write in the directory : " + dataFolder.getAbsolutePath());
            logger.log(Level.WARNING, "Disable the plugin");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        final Configuration configuration = initFile(dataFolder, "config.yml");
        this.messages = PrefixedConfigurationFormattedMessages.create(
                initFile(dataFolder, "messages.yml"),
                ChatColor.GRAY + "[" + ChatColor.DARK_PURPLE + "StelyClaimConfig" + ChatColor.GRAY + "]" + ChatColor.WHITE
        );

        final FlagStateLoaderCreator flagStateLoaderCreator = new FlagStateLoaderCreator(this, configuration);

        menuManager = new MenuManager(this, stelyClaimPlugin, initFile(dataFolder, "menu.yml"), flagStateLoaderCreator);

        commandManager = new CommandManager(this, stelyClaimPlugin, menuManager, configuration);

    }

    @Override
    public void onDisable() {
        menuManager.closeAllSessions();
        commandManager.disable();
    }

    private boolean checkDataFolder(File dataFolder) {
        if (dataFolder.exists())
            return dataFolder.canWrite();
        return dataFolder.mkdirs();
    }

    private YamlConfiguration initFile(File dataFolder, String fileName) {
        final File file = new File(dataFolder, fileName);
        if (!file.exists()) {
            try {
                Files.copy(Objects.requireNonNull(getResource(fileName)), file.toPath());
            } catch (IOException ignored) {
            }
        }

        return YamlConfiguration.loadConfiguration(file);
    }

    public ConfigurationFormattedMessages getMessages() {
        return messages;
    }

}
