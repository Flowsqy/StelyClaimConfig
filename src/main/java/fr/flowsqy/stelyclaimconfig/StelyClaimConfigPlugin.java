package fr.flowsqy.stelyclaimconfig;

import fr.flowsqy.stelyclaim.StelyClaimPlugin;
import fr.flowsqy.stelyclaim.io.Messages;
import fr.flowsqy.stelyclaimconfig.commands.CommandManager;
import fr.flowsqy.stelyclaimconfig.menu.MenuManager;
import org.bukkit.Bukkit;
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

    private YamlConfiguration configuration;
    private Messages messages;
    private MenuManager menuManager;
    private CommandManager commandManager;

    @Override
    public void onEnable() {
        final Plugin rawSCPlugin = getServer().getPluginManager().getPlugin("StelyClaim");
        if (!(rawSCPlugin instanceof StelyClaimPlugin)) {
            throw new RuntimeException("Wrong StelyClaim plugin, install the correct one");
        }
        final StelyClaimPlugin stelyClaimPlugin = (StelyClaimPlugin) rawSCPlugin;

        final Logger logger = getLogger();
        final File dataFolder = getDataFolder();

        if (!checkDataFolder(dataFolder)) {
            logger.log(Level.WARNING, "Can not write in the directory : " + dataFolder.getAbsolutePath());
            logger.log(Level.WARNING, "Disable the plugin");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        this.configuration = initFile(dataFolder, "config.yml");
        this.messages = new Messages(initFile(dataFolder, "messages.yml"), "&7[&5StelyClaimConfig&7]&f");

        menuManager = new MenuManager(this, stelyClaimPlugin, initFile(dataFolder, "menu.yml"));

        commandManager = new CommandManager(this, stelyClaimPlugin, menuManager);
    }

    @Override
    public void onDisable() {
        menuManager.disable();
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

    public YamlConfiguration getConfiguration() {
        return configuration;
    }

    public Messages getMessages() {
        return messages;
    }
}
