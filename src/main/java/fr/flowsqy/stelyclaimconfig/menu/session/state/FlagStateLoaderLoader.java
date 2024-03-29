package fr.flowsqy.stelyclaimconfig.menu.session.state;

import fr.flowsqy.stelyclaim.api.FormattedMessages;
import fr.flowsqy.stelyclaimconfig.StelyClaimConfigPlugin;
import fr.flowsqy.stelyclaimconfig.conversation.ConversationBuilder;
import fr.flowsqy.stelyclaimconfig.conversation.ConversationBuilderLoader;
import fr.flowsqy.stelyclaimconfig.menu.MenuManager;
import org.bukkit.configuration.Configuration;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;
import java.util.function.Predicate;

public class FlagStateLoaderLoader implements Function<MenuManager, FlagStateLoader> {

    private final StelyClaimConfigPlugin plugin;
    private final Configuration configuration;

    public FlagStateLoaderLoader(@NotNull StelyClaimConfigPlugin plugin, @NotNull Configuration configuration) {
        this.plugin = plugin;
        this.configuration = configuration;
    }

    @Override
    public FlagStateLoader apply(MenuManager menuManager) {
        final FormattedMessages messages = plugin.getMessages();
        final ConversationBuilderLoader conversationBuilderLoader = new ConversationBuilderLoader();
        final ConversationBuilder conversationBuilder = conversationBuilderLoader.load(plugin, configuration);
        final BlockedColorsInputPredicateLoader blockedColorsInputPredicateLoader = new BlockedColorsInputPredicateLoader();
        final Predicate<String> blockedColorsInputPredicate = blockedColorsInputPredicateLoader.load(configuration);

        final StringFlagStateData stringFlagStateData = new StringFlagStateData(
                conversationBuilder,
                messages,
                menuManager,
                blockedColorsInputPredicate
        );
        final FlagStateData flagStateData = new FlagStateData(stringFlagStateData);

        final FlagDefaultValueManagerLoader flagDefaultValueManagerLoader = new FlagDefaultValueManagerLoader();
        final FlagDefaultValueManager flagDefaultValueManager = flagDefaultValueManagerLoader.load(configuration);

        return new FlagStateLoader(flagStateData, flagDefaultValueManager);
    }

}
