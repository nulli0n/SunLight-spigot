package su.nightexpress.sunlight.api.event;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.sunlight.command.teleport.impl.TeleportRequest;

public class PlayerTeleportRequestEvent extends Event implements Cancellable {

    private static final HandlerList handlerList = new HandlerList();

    private final TeleportRequest request;

    private boolean isCancelled;

    public PlayerTeleportRequestEvent(@NotNull TeleportRequest request) {
        this.request = request;
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

    @NotNull
    public TeleportRequest getRequest() {
        return this.request;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.isCancelled = cancelled;
    }
}
