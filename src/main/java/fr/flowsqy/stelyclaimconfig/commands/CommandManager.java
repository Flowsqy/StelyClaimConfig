package fr.flowsqy.stelyclaimconfig.commands;

import fr.flowsqy.stelyclaim.StelyClaimPlugin;
import fr.flowsqy.stelyclaimconfig.StelyClaimConfigPlugin;
import fr.flowsqy.stelyclaimconfig.menu.MenuManager;
import org.bukkit.configuration.Configuration;
import org.bukkit.permissions.Permission;

public class CommandManager {
    private final StelyClaimPlugin stelyClaimPlugin;
    private final ConfigSubCommand configSubCommand;

    public CommandManager(StelyClaimConfigPlugin plugin, StelyClaimPlugin stelyClaimPlugin, MenuManager menuManager, Configuration configuration) {
        this.stelyClaimPlugin = stelyClaimPlugin;
        this.configSubCommand = new ConfigSubCommand(
                stelyClaimPlugin,
                plugin.getMessages(),
                "config",
                "c",
                "stelyclaimconfig.claim.config",
                false,
                configuration.getStringList("allowed-worlds"),
                configuration.getBoolean("statistic"),
                menuManager
        );
        stelyClaimPlugin
                .getCommandManager()
                .getClaimCommand()
                .registerCommand(this.configSubCommand, true, basePerm -> {
                    Permission otherPermission = new Permission(this.configSubCommand.getOtherPermission());
                    basePerm.addParent(otherPermission, true);
                    return otherPermission;
                });
    }

    public void disable() {
        this.stelyClaimPlugin
                .getCommandManager()
                .getClaimCommand()
                .unregisterCommand(this.configSubCommand);
    }
}
