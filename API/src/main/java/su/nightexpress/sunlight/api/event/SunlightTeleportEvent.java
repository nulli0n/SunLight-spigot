package su.nightexpress.sunlight.api.event;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.sunlight.api.type.TeleportType;

public class SunlightTeleportEvent extends Event implements Cancellable {

    public static final HandlerList HANDLER_LIST = new HandlerList();

    private final Player       player;
    private final TeleportType cause;
    private final Runnable     callback;

    private Location destination;
    private boolean  forced;
    private boolean  cancelled;
    private boolean  handled;

    public SunlightTeleportEvent(@NotNull Player player,
                                 @NotNull Location destination,
                                 @NotNull TeleportType cause,
                                 @Nullable Runnable callback,
                                 boolean forced) {
        this.player = player;
        this.setDestination(destination);
        this.cause = cause;
        this.callback = callback;
        this.setForced(forced);
    }

    public void handle() {
        if (this.isHandled()) throw new IllegalStateException("The teleport event has already been handled!");

        this.handled = true;
    }

    @NotNull
    public Player getPlayer() {
        return this.player;
    }

    @NotNull
    public Location getFrom() {
        return this.player.getLocation();
    }

    @NotNull
    public Location getDestination() {
        return this.destination.clone();
    }

    public void setDestination(@NotNull Location destination) {
        this.destination = destination.clone();
    }

    @NotNull
    public TeleportType getCause() {
        return this.cause;
    }

    @Nullable
    public Runnable getCallback() {
        return this.callback;
    }

    public boolean isForced() {
        return this.forced;
    }

    public void setForced(boolean forced) {
        this.forced = forced;
    }

    public boolean isHandled() {
        return this.handled;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }
}
