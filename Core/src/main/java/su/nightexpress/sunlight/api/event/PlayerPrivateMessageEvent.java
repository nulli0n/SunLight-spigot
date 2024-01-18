package su.nightexpress.sunlight.api.event;

import org.bukkit.command.CommandSender;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class PlayerPrivateMessageEvent extends Event implements Cancellable {

    private static final HandlerList handlerList = new HandlerList();

    private final CommandSender sender;
    private final CommandSender target;
    private final String        message;

    private boolean isCancelled;

    public PlayerPrivateMessageEvent(
        @NotNull CommandSender sender,
        @NotNull CommandSender target,
        @NotNull String message
    ) {
        this.sender = sender;
        this.target = target;
        this.message = message;
    }

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
    public CommandSender getSender() {
        return this.sender;
    }

    @NotNull
    public CommandSender getTarget() {
        return this.target;
    }

    @NotNull
    public String getMessage() {
        return this.message;
    }
}
