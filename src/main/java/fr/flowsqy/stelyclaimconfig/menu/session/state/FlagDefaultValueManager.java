package fr.flowsqy.stelyclaimconfig.menu.session.state;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.function.Function;

public record FlagDefaultValueManager(@NotNull Map<String, Function<Player, Boolean>> state,
                                      @NotNull Map<String, Function<Player, String>> string) {

}
