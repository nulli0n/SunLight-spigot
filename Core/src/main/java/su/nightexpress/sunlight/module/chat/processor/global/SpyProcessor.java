package su.nightexpress.sunlight.module.chat.processor.global;

import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;

import su.nightexpress.nightcore.util.placeholder.PlaceholderContext;
import su.nightexpress.sunlight.SLPlaceholders;
import su.nightexpress.sunlight.module.chat.ChatModule;
import su.nightexpress.sunlight.module.chat.context.ChatContext;
import su.nightexpress.sunlight.module.chat.context.CommandContext;
import su.nightexpress.sunlight.module.chat.context.ConversationContext;
import su.nightexpress.sunlight.module.chat.context.MessageContext;
import su.nightexpress.sunlight.module.chat.processor.ChatProcessor;
import su.nightexpress.sunlight.module.chat.spy.SpyType;

public class SpyProcessor implements ChatProcessor<ChatContext> {

    @Override
    public void preProcess(@NonNull ChatModule module, @NonNull ChatContext context) {

    }

    @Override
    public void postProcess(@NonNull ChatModule module, @NonNull ChatContext context) {
        Player player = context.getPlayer();
        String message = context.getMessage();
        SpyType type;
        PlaceholderContext placeholderContext;

        switch (context) {
            case MessageContext messageContext -> {
                placeholderContext = PlaceholderContext.builder().with(messageContext.getChannel().placeholders())
                    .build();
                type = SpyType.CHAT;
            }
            case ConversationContext conversationContext -> {
                placeholderContext = PlaceholderContext.builder().with(SLPlaceholders.GENERIC_TARGET,
                    () -> conversationContext.getTarget().getName()).build();
                type = SpyType.SOCIAL;
            }
            case CommandContext ignored -> {
                placeholderContext = PlaceholderContext.builder().build();
                type = SpyType.COMMAND;
            }
            default -> {
                return;
            }
        }

        String format = module.getSettings().getSpyModeFormat(type);
        if (format == null) return;

        String formatted = placeholderContext.apply(format);
        module.sendSpyInfo(player, message, formatted, type);
    }
}
