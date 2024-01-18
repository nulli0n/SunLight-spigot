package su.nightexpress.sunlight.module.homes.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.sunlight.module.homes.impl.Home;

public class PlayerHomeTeleportEvent extends Event implements Cancellable {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    private final Player player;
    private final Home   home;

    private boolean cancelled;

    public PlayerHomeTeleportEvent(@NotNull Player player, @NotNull Home home) {
        this.player = player;
        this.home = home;
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
    public Home getHome() {
        return home;
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
