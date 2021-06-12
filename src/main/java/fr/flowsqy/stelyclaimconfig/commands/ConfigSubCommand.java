package fr.flowsqy.stelyclaimconfig.commands;

import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import fr.flowsqy.stelyclaim.StelyClaimPlugin;
import fr.flowsqy.stelyclaim.api.ClaimMessage;
import fr.flowsqy.stelyclaim.command.subcommand.SubCommand;
import fr.flowsqy.stelyclaim.internal.DefaultClaimMessages;
import fr.flowsqy.stelyclaim.internal.PlayerHandler;
import fr.flowsqy.stelyclaim.internal.PlayerOwner;
import fr.flowsqy.stelyclaim.io.Messages;
import fr.flowsqy.stelyclaim.protocol.RegionFinder;
import fr.flowsqy.stelyclaim.util.WorldName;
import fr.flowsqy.stelyclaimconfig.menu.MenuManager;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class ConfigSubCommand extends SubCommand {

    private final MenuManager menuManager;

    public ConfigSubCommand(StelyClaimPlugin plugin, Messages messages, String name, String alias, String permission, boolean console, List<String> allowedWorlds, boolean statistic, MenuManager menuManager) {
        super(plugin, messages, name, alias, permission, console, allowedWorlds, statistic);
        this.menuManager = menuManager;
    }

    @Override
    public boolean execute(CommandSender sender, List<String> args, int size, boolean isPlayer) {
        final Player player = (Player) sender;
        final PlayerOwner owner;
        if (size == 1) {
            owner = new PlayerOwner(player);
        } else if (size == 2) {
            owner = new PlayerOwner(Bukkit.getOfflinePlayer(args.get(1)));
        } else {
            return !messages.sendMessage(
                    player,
                    "help." + getName() + (sender.hasPermission(getOtherPermission()) ? "-other" : "")
            );
        }

        final boolean ownRegion = owner.own(player);
        if (!ownRegion && !player.hasPermission(getOtherPermission())) {
            return !messages.sendMessage(
                    player,
                    "help." + getName() + (sender.hasPermission(getOtherPermission()) ? "-other" : "")
            );
        }

        final World world = player.getWorld();

        final ClaimMessage claimMessages = new DefaultClaimMessages(plugin.getMessages());

        final RegionManager regionManager = RegionFinder.getRegionManager(new WorldName(world.getName()), player, claimMessages);
        if (regionManager == null)
            return false;

        final PlayerHandler handler = plugin.getProtocolManager().getHandler("player");

        final String regionName = RegionFinder.getRegionName(handler, owner);

        final ProtectedRegion region = RegionFinder.mustExist(regionManager, regionName, owner.getName(), owner.own(player), player, claimMessages);
        if (region == null) {
            return false;
        }

        menuManager.open(player, region);

        return true;
    }

    @Override
    public List<String> tab(CommandSender sender, List<String> args, boolean isPlayer) {
        if (args.size() == 2 && sender.hasPermission(getOtherPermission())) {
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
