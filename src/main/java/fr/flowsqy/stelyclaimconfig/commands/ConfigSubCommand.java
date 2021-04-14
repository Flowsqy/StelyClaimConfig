package fr.flowsqy.stelyclaimconfig.commands;

import fr.flowsqy.stelyclaim.StelyClaimPlugin;
import fr.flowsqy.stelyclaim.command.subcommand.SubCommand;
import fr.flowsqy.stelyclaim.io.Messages;
import org.bukkit.command.CommandSender;

import java.util.List;

public class ConfigSubCommand extends SubCommand {

    public ConfigSubCommand(StelyClaimPlugin plugin, Messages messages, String name, String alias, String permission, boolean console, List<String> allowedWorlds, boolean statistic) {
        super(plugin, messages, name, alias, permission, console, allowedWorlds, statistic);
    }

    @Override
    public boolean execute(CommandSender commandSender, List<String> list, int i, boolean b) {
        return false;
    }

    @Override
    public List<String> tab(CommandSender commandSender, List<String> list, boolean b) {
        return null;
    }
}
