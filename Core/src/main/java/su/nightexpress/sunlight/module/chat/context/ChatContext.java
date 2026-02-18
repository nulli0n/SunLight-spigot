package su.nightexpress.sunlight.module.chat.context;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.sunlight.module.chat.cache.CachedContent;
import su.nightexpress.sunlight.module.chat.cache.UserChatCache;

public abstract class ChatContext {

    protected final Player        player;
    protected final UserChatCache cache;
    protected final String        originalMessage;

    protected String  message;
    protected boolean cancelled;

    public ChatContext(@NotNull Player player, @NotNull UserChatCache cache, @NotNull String originalMessage) {
        this.player = player;
        this.cache = cache;
        this.originalMessage = originalMessage;

        this.setMessage(originalMessage);
    }

    public boolean isCancelled() {
        return this.cancelled;
    }

    public void cancel() {
        this.cancelled = true;
    }

    @Nullable
    public abstract CachedContent getLastContent();

    public abstract void setLastContent(@NotNull String message, long lifeTime);

    @NotNull
    public Player getPlayer() {
        return this.player;
    }

    @NotNull
    public UserChatCache getCache() {
        return this.cache;
    }

    @NotNull
    public String getOriginalMessage() {
        return this.originalMessage;
    }

    @NotNull
    public String getMessage() {
        return this.message;
    }

    public void setMessage(@NotNull String message) {
        this.message = message;
    }
}
