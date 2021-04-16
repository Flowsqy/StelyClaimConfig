package fr.flowsqy.stelyclaimconfig.commands;

import fr.flowsqy.abstractmenu.inventory.EventInventory;
import fr.flowsqy.stelyclaim.StelyClaimPlugin;
import fr.flowsqy.stelyclaimconfig.StelyClaimConfigPlugin;

public class CommandManager {

    public CommandManager(StelyClaimConfigPlugin plugin, EventInventory inventory) {
        final ConfigSubCommand configSubCommand = new ConfigSubCommand(
                StelyClaimPlugin.getInstance(),
                plugin.getMessages(),
                "config",
                "c",
                "stelyclaimconfig.claim.config",
                false,
                plugin.getConfiguration().getStringList("allowed-worlds"),
                plugin.getConfiguration().getBoolean("statistic"),
                inventory
        );
        StelyClaimPlugin
                .getInstance()
                .getCommandManager()
                .getClaimCommand()
                .registerCommand(configSubCommand, true);
    }

}
