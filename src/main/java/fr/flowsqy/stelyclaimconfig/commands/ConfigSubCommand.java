package fr.flowsqy.stelyclaimconfig.commands;

import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import fr.flowsqy.stelyclaim.StelyClaimPlugin;
import fr.flowsqy.stelyclaim.api.ClaimHandler;
import fr.flowsqy.stelyclaim.api.ClaimOwner;
import fr.flowsqy.stelyclaim.api.FormattedMessages;
import fr.flowsqy.stelyclaim.command.subcommand.ProtocolSubCommand;
import fr.flowsqy.stelyclaim.common.ConfigurationFormattedMessages;
import fr.flowsqy.stelyclaim.internal.PlayerHandler;
import fr.flowsqy.stelyclaim.internal.PlayerOwner;
import fr.flowsqy.stelyclaim.protocol.RegionFinder;
import fr.flowsqy.stelyclaim.util.WorldName;
import fr.flowsqy.stelyclaimconfig.menu.MenuManager;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

public class ConfigSubCommand extends ProtocolSubCommand {
    private final MenuManager menuManager;

    public ConfigSubCommand(StelyClaimPlugin plugin, ConfigurationFormattedMessages messages, String name, String alias,
            String permission, boolean console, List<String> allowedWorlds, boolean statistic,
            MenuManager menuManager) {
        super(plugin.getProtocolManager(), messages, name, alias, permission, console, allowedWorlds, statistic);
        this.menuManager = menuManager;
    }

    public boolean execute(CommandSender sender, List<String> args, int size, boolean isPlayer) {
        PlayerOwner owner;
        Player player = (Player) sender;
        if (size == 1) {
            owner = new PlayerOwner((OfflinePlayer) player);
        } else if (size == 2) {
            owner = new PlayerOwner(Bukkit.getOfflinePlayer(args.get(1)));
        } else {
            return !this.messages.sendMessage((CommandSender) player, "help." +

                    getName() + (sender.hasPermission(getOtherPermission()) ? "-other" : ""), new String[0]);
        }
        boolean ownRegion = owner.own(player);
        if (!ownRegion && !player.hasPermission(getOtherPermission()))
            return !this.messages.sendMessage((CommandSender) player, "help." +

                    getName() + (sender.hasPermission(getOtherPermission()) ? "-other" : ""), new String[0]);
        org.bukkit.World world = player.getWorld();
        RegionManager regionManager = RegionFinder.getRegionManager((World) new WorldName(world.getName()), player,
                (FormattedMessages) this.messages);
        if (regionManager == null)
            return false;
        PlayerHandler handler = this.protocolManager.getHandler("player");
        String regionName = RegionFinder.getRegionName((ClaimHandler) handler, (ClaimOwner) owner);
        ProtectedRegion region = RegionFinder.mustExist(regionManager, regionName, owner.getName(), owner.own(player),
                player, (FormattedMessages) this.messages);
        if (region == null)
            return false;
        this.menuManager.open(player, region);
        return true;
    }

    public List<String> tab(CommandSender sender, List<String> args, boolean isPlayer) {
        if (args.size() == 2 && sender.hasPermission(getOtherPermission())) {
            Player player = (Player) sender;
            String arg = ((String) args.get(1)).toLowerCase(Locale.ROOT);
            Objects.requireNonNull(player);
            return (List<String>) Bukkit.getOnlinePlayers().stream().filter(player::canSee)
                    .map(HumanEntity::getName)
                    .filter(name -> name.toLowerCase(Locale.ROOT).startsWith(arg))
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }
}
