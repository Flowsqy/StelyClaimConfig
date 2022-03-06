package fr.flowsqy.stelyclaimconfig.commands;

import fr.flowsqy.stelyclaim.StelyClaimPlugin;
import fr.flowsqy.stelyclaimconfig.StelyClaimConfigPlugin;
import fr.flowsqy.stelyclaimconfig.menu.MenuManager;

public class CommandManager {

    private final StelyClaimPlugin stelyClaimPlugin;
    private final ConfigSubCommand configSubCommand;

    public CommandManager(StelyClaimConfigPlugin plugin, StelyClaimPlugin stelyClaimPlugin, MenuManager menuManager) {
        this.stelyClaimPlugin = stelyClaimPlugin;
        configSubCommand = new ConfigSubCommand(
                stelyClaimPlugin,
                plugin.getMessages(),
                "config",
                "c",
                "stelyclaimconfig.claim.config",
                false,
                plugin.getConfiguration().getStringList("allowed-worlds"),
                plugin.getConfiguration().getBoolean("statistic"),
                menuManager
        );
        stelyClaimPlugin
                .getCommandManager()
                .getClaimCommand()
                .registerCommand(configSubCommand, true);
    }

    public void disable() {
        stelyClaimPlugin
                .getCommandManager()
                .getClaimCommand()
                .unregisterCommand(configSubCommand);
    }
}
