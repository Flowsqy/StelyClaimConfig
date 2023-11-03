package fr.flowsqy.stelyclaimconfig.menu.session.state;

import fr.flowsqy.abstractmenu.item.CreatorListener;
import fr.flowsqy.stelyclaimconfig.menu.item.flag.effect.FlagEffects;
import org.jetbrains.annotations.NotNull;

public interface FlagStateCreatorListener extends CreatorListener {

    void open(@NotNull FlagState flagState, @NotNull FlagEffects flagEffects);

    void close();

}
