package su.nightexpress.sunlight.module.homes.event;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class PlayerHomeCreateEvent extends Event implements Cancellable {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    private final Player player;
    private final String homeId;
    private final boolean isNewHome;
    private Location location;

    private boolean cancelled;

    public PlayerHomeCreateEvent(@NotNull Player player, @NotNull String homeId, @NotNull Location location, boolean isNewHome) {
        this.player = player;
        this.homeId = homeId;
        this.isNewHome = isNewHome;
        this.setLocation(location);
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
    public String getHomeId() {
        return homeId;
    }

    @NotNull
    public Location getLocation() {
        return location;
    }

    public void setLocation(@NotNull Location location) {
        this.location = location;
    }

    public boolean isNewHome() {
        return isNewHome;
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
