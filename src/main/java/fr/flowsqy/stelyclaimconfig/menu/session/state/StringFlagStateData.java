package fr.flowsqy.stelyclaimconfig.menu.session.state;

import fr.flowsqy.stelyclaim.api.FormattedMessages;
import fr.flowsqy.stelyclaimconfig.conversation.ConversationBuilder;
import fr.flowsqy.stelyclaimconfig.menu.MenuManager;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

public record StringFlagStateData(@NotNull ConversationBuilder conversationBuilder, @NotNull FormattedMessages messages,
                                  @NotNull MenuManager menuManager, @NotNull Predicate<String> inputPredicate) {

}
