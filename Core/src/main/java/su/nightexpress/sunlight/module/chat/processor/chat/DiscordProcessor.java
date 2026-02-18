package su.nightexpress.sunlight.module.chat.processor.chat;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.sunlight.module.chat.ChatModule;
import su.nightexpress.sunlight.module.chat.context.MessageContext;
import su.nightexpress.sunlight.module.chat.discord.DiscordHandler;
import su.nightexpress.sunlight.module.chat.processor.MessageProcessor;

public class DiscordProcessor implements MessageProcessor {

    private final DiscordHandler discordHandler;

    public DiscordProcessor(@NotNull DiscordHandler discordHandler) {
        this.discordHandler = discordHandler;
    }

    @Override
    public void preProcess(@NotNull ChatModule module, @NotNull MessageContext context) {

    }

    @Override
    public void postProcess(@NotNull ChatModule module, @NotNull MessageContext context) {
        this.discordHandler.sendToChannel(context.getChannel(), context.getMessage());
    }
}
