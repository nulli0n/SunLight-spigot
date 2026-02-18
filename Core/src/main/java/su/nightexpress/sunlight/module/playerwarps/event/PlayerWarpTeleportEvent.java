package su.nightexpress.sunlight.module.playerwarps.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.sunlight.module.playerwarps.PlayerWarp;

public class PlayerWarpTeleportEvent extends Event implements Cancellable {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    private final Player player;
    private final PlayerWarp   warp;

    private boolean cancelled;

    public PlayerWarpTeleportEvent(@NotNull Player player, @NotNull PlayerWarp warp) {
        this.player = player;
        this.warp = warp;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    @NotNull
    public Player getPlayer() {
        return player;
    }

    @NotNull
    public PlayerWarp getWarp() {
        return warp;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}
