package fr.flowsqy.stelyclaimconfig.menu.item;

import fr.flowsqy.abstractmenu.item.CreatorAdaptor;
import fr.flowsqy.stelyclaimconfig.menu.MenuManager;
import fr.flowsqy.stelyclaimconfig.menu.session.PlayerMenuSession;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

public class InfoCreatorListener extends CreatorAdaptor {

    private final MenuManager menuManager;
    private String page;

    public InfoCreatorListener(MenuManager menuManager) {
        this.menuManager = menuManager;
    }

    @Override
    public void open(Player player) {
        // Replace the "%page%" placeholder in name and lore
        final PlayerMenuSession session = menuManager.getSession(player.getUniqueId());
        page = String.valueOf(session == null ? 1 : session.getPageManager().getReadableCurrentPage());
    }

    @Override
    public void close(Player player) {
        page = null;
    }

    @Override
    public String handleName(Player player, String name) {
        return name == null ? null : name.replace("%page%", page);
    }

    @Override
    public List<String> handleLore(Player player, List<String> lore) {
        if (lore == null) {
            return null;
        }
        return lore.stream()
                .map(line -> line.replace("%page%", page))
                .collect(Collectors.toList());
    }

}
