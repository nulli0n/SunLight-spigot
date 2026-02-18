package su.nightexpress.sunlight.module.warps.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jspecify.annotations.NonNull;
import su.nightexpress.sunlight.module.warps.Warp;

public class WarpTeleportEvent extends Event implements Cancellable {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    private final Player player;
    private final Warp   warp;

    private boolean cancelled;

    public WarpTeleportEvent(@NonNull Player player, @NonNull Warp warp) {
        this.player = player;
        this.warp = warp;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    @NonNull
    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    @NonNull
    public Player getPlayer() {
        return this.player;
    }

    @NonNull
    public Warp getWarp() {
        return this.warp;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}
