package su.nightexpress.sunlight.module.chat.context;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.nightcore.util.placeholder.PlaceholderContext;
import su.nightexpress.sunlight.SLPlaceholders;
import su.nightexpress.sunlight.module.chat.cache.CachedContent;
import su.nightexpress.sunlight.module.chat.cache.UserChatCache;
import su.nightexpress.sunlight.module.chat.channel.ChatChannel;

import java.util.Set;

public class MessageContext extends FormattedContext {

    private final Set<CommandSender> viewers;

    private ChatChannel channel;
    //private String      format;

    public MessageContext(@NotNull Player player,
                          @NotNull UserChatCache cache,
                          @NotNull String originalMessage,
                          @NotNull String userFormat,
                          @NotNull ChatChannel channel,
                          @NotNull Set<CommandSender> viewers) {
        super(player, cache, originalMessage);
        this.viewers = viewers;

        this.setChannel(channel);
        this.setFormat(PlaceholderContext.builder()
            .with(SLPlaceholders.GENERIC_FORMAT, () -> userFormat)
            .with(SLPlaceholders.GENERIC_MESSAGE, () -> "") // old
            .with(channel.placeholders())
            .build()
            .apply(channel.getDisplay().format())
        );
    }

    @NotNull
    public Set<CommandSender> getViewers() {
        return this.viewers;
    }

    @Override
    @Nullable
    public CachedContent getLastContent() {
        return this.cache.getLastMessage();
    }

    @Override
    public void setLastContent(@NotNull String message, long lifeTime) {
        this.cache.setLastMessage(message, lifeTime);
    }

    @NotNull
    public ChatChannel getChannel() {
        return this.channel;
    }

    public void setChannel(@NotNull ChatChannel channel) {
        this.channel = channel;
    }

    /*@NotNull
    public String getFormat() {
        return this.format;
    }

    public void setFormat(@NotNull String format) {
        this.format = format;
    }*/
}
