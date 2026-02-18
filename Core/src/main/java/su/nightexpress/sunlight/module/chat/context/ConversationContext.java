package su.nightexpress.sunlight.module.chat.context;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.sunlight.module.chat.cache.CachedContent;
import su.nightexpress.sunlight.module.chat.cache.UserChatCache;

public class ConversationContext extends FormattedContext {

    protected final Player target;

    public ConversationContext(@NotNull Player player,
                               @NotNull UserChatCache cache,
                               @NotNull String originalMessage,
                               @NotNull String proxyFormat,
                               @NotNull Player target) {
        super(player, cache, originalMessage);
        this.target = target;
        this.setFormat(proxyFormat);
    }

    @Override
    @Nullable
    public CachedContent getLastContent() {
        return null;
    }

    @Override
    public void setLastContent(@NotNull String message, long lifeTime) {

    }

    @NotNull
    public Player getTarget() {
        return this.target;
    }
}
