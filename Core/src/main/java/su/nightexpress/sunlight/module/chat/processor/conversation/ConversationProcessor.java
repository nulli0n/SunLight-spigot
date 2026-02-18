package su.nightexpress.sunlight.module.chat.processor.conversation;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.util.Players;
import su.nightexpress.nightcore.util.placeholder.CommonPlaceholders;
import su.nightexpress.nightcore.util.placeholder.PlaceholderContext;
import su.nightexpress.sunlight.SLPlaceholders;
import su.nightexpress.sunlight.module.chat.ChatModule;
import su.nightexpress.sunlight.module.chat.context.ConversationContext;
import su.nightexpress.sunlight.module.chat.processor.ChatProcessor;

public class ConversationProcessor implements ChatProcessor<ConversationContext> {

    @Override
    public void preProcess(@NotNull ChatModule module, @NotNull ConversationContext context) {

    }

    @Override
    public void postProcess(@NotNull ChatModule module, @NotNull ConversationContext context) {
        String rawIncoming = module.getSettings().getConversationIncomingFormat();
        String rawOutgoing = module.getSettings().getConversationOutgoingFormat();

        String inFormatted = this.format(context, context.getPlayer(), rawIncoming);
        String outFormatted = this.format(context, context.getTarget(), rawOutgoing);

        Players.sendMessage(context.getTarget(), inFormatted);
        Players.sendMessage(context.getPlayer(), outFormatted);

        if (module.getSettings().isConversationSoundsEnabled()) {
            module.getSettings().getConversationIncomingSound().play(context.getTarget());
            module.getSettings().getConversationOutgoingSound().play(context.getPlayer());
        }

        context.getCache().setLastConversationWith(context.getTarget().getUniqueId());
        module.getChatCache(context.getTarget()).setLastConversationWith(context.getPlayer().getUniqueId());
    }

    @NotNull
    private String format(@NotNull ConversationContext context, @NotNull Player player, @NotNull String rawFormat) {
        PlaceholderContext messageContext = PlaceholderContext.builder()
            .with(SLPlaceholders.GENERIC_MESSAGE, context::getMessage)
            .build();

        PlaceholderContext formatContext = PlaceholderContext.builder()
            .maxRecursion(1) // To apply player placeholders for proxy format as well.
            .with(SLPlaceholders.GENERIC_FORMAT, context::getFormat)
            .with(CommonPlaceholders.PLAYER.resolver(player))
            .andThen(CommonPlaceholders.forPlaceholderAPI(player))
            .build();

        String formatted = formatContext.apply(rawFormat);

        return messageContext.apply(formatted);
    }
}
