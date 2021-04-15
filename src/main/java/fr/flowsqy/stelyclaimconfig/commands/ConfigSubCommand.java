package fr.flowsqy.stelyclaimconfig.commands;

import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import fr.flowsqy.stelyclaim.StelyClaimPlugin;
import fr.flowsqy.stelyclaim.command.subcommand.RegionSubCommand;
import fr.flowsqy.stelyclaim.io.Messages;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class ConfigSubCommand extends RegionSubCommand {

    public ConfigSubCommand(StelyClaimPlugin plugin, Messages messages, String name, String alias, String permission, boolean console, List<String> allowedWorlds, boolean statistic) {
        super(plugin, messages, name, alias, permission, console, allowedWorlds, statistic, StelyClaimPlugin.getInstance().getRegionContainer());
    }

    @Override
    public boolean execute(CommandSender sender, List<String> args, int size, boolean isPlayer) {
        final Player player = (Player) sender;
        final String regionName;
        final boolean own;
        if (size == 1) {
            regionName = player.getName();
            own = true;
        } else if (size == 2 && sender.hasPermission(getPermission() + "-other")) {
            regionName = args.get(1);
            own = regionName.equalsIgnoreCase(player.getName());
        } else {
            return !messages.sendMessage(
                    player,
                    "help." + getName() + (sender.hasPermission(getPermission() + "-other") ? "-other" : "")
            );
        }

        final World world = player.getWorld();

        final RegionManager regionManager = getRegionManager(world);
        if (regionManager == null) {
            plugin.getMessages().sendMessage(player,
                    "claim.world.nothandle",
                    "%world%", world.getName());
            return false;
        }

        final ProtectedRegion region = regionManager.getRegion(regionName);
        if (region == null) {
            plugin.getMessages().sendMessage(player, "claim.exist.not" + (own ? "" : "-other"), "%region%", regionName);
            return false;
        }

        // TODO Open inventory

        return true;
    }

    @Override
    public List<String> tab(CommandSender sender, List<String> args, boolean isPlayer) {
        if (args.size() == 2 && sender.hasPermission(getPermission() + "-other")) {
            final Player player = (Player) sender;
            final String arg = args.get(1).toLowerCase(Locale.ROOT);
            return Bukkit.getOnlinePlayers().stream()
                    .filter(player::canSee)
                    .map(HumanEntity::getName)
                    .filter(name -> name.toLowerCase(Locale.ROOT).startsWith(arg))
                    .collect(Collectors.toList());
        }

        return Collections.emptyList();
    }
}
