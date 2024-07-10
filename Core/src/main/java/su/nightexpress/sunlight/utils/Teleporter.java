package su.nightexpress.sunlight.utils;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.util.LocationUtil;

public class Teleporter {

    private final Player   player;
    private final Location destination;

    private boolean  useOriginalDirection;

    public Teleporter(@NotNull Player player, @NotNull Entity target) {
        this(player, target.getLocation());
    }

    public Teleporter(@NotNull Player player, @NotNull Location destination) {
        this.player = player;
        this.destination = destination.clone();
    }

    public boolean teleport() {
        Location location = this.destination;

        if (this.useOriginalDirection) {
            Location source = this.player.getLocation();
            location.setYaw(source.getYaw());
            location.setPitch(source.getPitch());
        }

        return SunUtils.teleport(this.player, location);
    }

    @NotNull
    public Teleporter useOriginalDirection() {
        this.useOriginalDirection = true;
        return this;
    }

    @NotNull
    public Teleporter centered() {
        LocationUtil.setCenter2D(this.destination);
        return this;
    }
}
