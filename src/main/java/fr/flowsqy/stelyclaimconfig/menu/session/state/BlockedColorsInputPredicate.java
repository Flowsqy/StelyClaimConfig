package fr.flowsqy.stelyclaimconfig.menu.session.state;

import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

public class BlockedColorsInputPredicate implements Predicate<String> {

    private final String[] blockedColors;

    public BlockedColorsInputPredicate(@NotNull String[] blockedColors) {
        this.blockedColors = blockedColors;
    }

    @Override
    public boolean test(String input) {
        for (String color : blockedColors) {
            if (input.contains(color)) {
                return false;
            }
        }
        return true;
    }

}
