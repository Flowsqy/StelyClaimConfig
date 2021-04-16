package fr.flowsqy.stelyclaimconfig.commands;

import fr.flowsqy.stelyclaim.StelyClaimPlugin;
import fr.flowsqy.stelyclaimconfig.StelyClaimConfigPlugin;
import fr.flowsqy.stelyclaimconfig.menu.MenuManager;

public class CommandManager {

    public CommandManager(StelyClaimConfigPlugin plugin, MenuManager menuManager) {
        final ConfigSubCommand configSubCommand = new ConfigSubCommand(
                StelyClaimPlugin.getInstance(),
                plugin.getMessages(),
                "config",
                "c",
                "stelyclaimconfig.claim.config",
                false,
                plugin.getConfiguration().getStringList("allowed-worlds"),
                plugin.getConfiguration().getBoolean("statistic"),
                menuManager
        );
        StelyClaimPlugin
                .getInstance()
                .getCommandManager()
                .getClaimCommand()
                .registerCommand(configSubCommand, true);
    }

}
