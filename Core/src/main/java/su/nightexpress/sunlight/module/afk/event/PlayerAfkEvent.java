package su.nightexpress.sunlight.module.afk.event;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.sunlight.data.impl.SunUser;

public class PlayerAfkEvent extends Event {

    private static final HandlerList handlerList = new HandlerList();

    private final Player  player;
    private final SunUser user;
    private final boolean isAfk;

    public PlayerAfkEvent(
        @NotNull Player player,
        @NotNull SunUser user,
        boolean isAfk
    ) {
        super(!Bukkit.isPrimaryThread());
        this.player = player;
        this.user = user;
        this.isAfk = isAfk;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

    @NotNull
    public Player getPlayer() {
        return this.player;
    }

    @NotNull
    public SunUser getUser() {
        return this.user;
    }

    public boolean isAfk() {
        return this.isAfk;
    }
}
