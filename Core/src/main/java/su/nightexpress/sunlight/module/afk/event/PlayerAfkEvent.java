package su.nightexpress.sunlight.module.afk.event;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.sunlight.module.afk.AfkState;

public class PlayerAfkEvent extends Event {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    private final Player   player;
    private final AfkState state;

    public PlayerAfkEvent(@NotNull Player player, @NotNull AfkState state) {
        super(!Bukkit.isPrimaryThread());
        this.player = player;
        this.state = state;
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
        return this.player;
    }

    @NotNull
    public AfkState getState() {
        return state;
    }
}
