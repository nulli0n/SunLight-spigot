package su.nightexpress.sunlight.module.chat.event;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.sunlight.module.chat.ChatChannel;
import su.nightexpress.sunlight.module.chat.util.Placeholders;

import java.util.Set;

public class AsyncSunlightPlayerChatEvent extends Event implements Cancellable {

    public static final HandlerList handlerList = new HandlerList();

    private final Player player;
    private final ChatChannel channel;
    private final Set<Player> recipients;

    private String      message;
    private String      format;

    private boolean isCancelled;

    public AsyncSunlightPlayerChatEvent(@NotNull Player player, @NotNull ChatChannel channel, @NotNull Set<Player> recipients,
                                        @NotNull String message, @NotNull String format) {
        super(!Bukkit.isPrimaryThread());
        this.player = player;
        this.channel = channel;
        this.recipients = recipients;
        this.setMessage(message);
        this.setFormat(format);
    }

    @NotNull
    public static HandlerList getHandlerList() {
        return handlerList;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

    @Override
    public boolean isCancelled() {
        return isCancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        isCancelled = cancelled;
    }

    @NotNull
    public Player getPlayer() {
        return player;
    }

    @NotNull
    public ChatChannel getChannel() {
        return channel;
    }

    @NotNull
    public Set<Player> getRecipients() {
        return recipients;
    }

    @NotNull
    public String getFormat() {
        return format;
    }

    public void setFormat(@NotNull String format) {
        this.format = format;
    }

    @NotNull
    public String getFinalFormat() {
        return this.getFormat().replace(Placeholders.GENERIC_MESSAGE, this.getMessage());
    }

    @NotNull
    public String getMessage() {
        return message;
    }

    public void setMessage(@NotNull String message) {
        this.message = message;
    }
}
