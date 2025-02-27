package su.nightexpress.sunlight.utils.teleport;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.nightcore.util.LocationUtil;
import su.nightexpress.sunlight.api.event.SunlightTeleportEvent;
import su.nightexpress.sunlight.api.type.TeleportType;
import su.nightexpress.sunlight.utils.SunUtils;

public class Teleporter {

    private final Player   player;
    private final Location destination;

    private boolean useOriginalDirection;
    private boolean validateFloor;
    private boolean forced;

    public Teleporter(@NotNull Player player, @NotNull Entity target) {
        this(player, target.getLocation());
    }

    public Teleporter(@NotNull Player player, @NotNull Location destination) {
        this.player = player;
        this.destination = destination.clone();
    }

    @NotNull
    public static Teleporter create(@NotNull Player player, @NotNull Entity target) {
        return new Teleporter(player, target.getLocation());
    }

    @NotNull
    public static Teleporter create(@NotNull Player player, @NotNull Location destination) {
        return new Teleporter(player, destination);
    }

    public void teleport() {
        this.teleport(TeleportType.OTHER, null);
    }

    public void teleport(@NotNull Runnable onSuccess) {
        this.teleport(TeleportType.OTHER, onSuccess);
    }

    public void teleport(@NotNull TeleportType cause, @Nullable Runnable onSuccess) {
        SunlightTeleportEvent event = new SunlightTeleportEvent(this.player, this.getDestination(), cause, onSuccess, this.forced);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled() || event.isHandled()) return;

        SunUtils.teleport(this.player, event.getDestination(), onSuccess);
    }

    @NotNull
    public Location getDesination() {
        Location location = this.destination.clone();

        if (this.validateFloor) {
            Block block = location.getBlock();
            if (!block.isEmpty()) {
                location = block.getRelative(BlockFace.UP).getLocation();
            }
        }

        if (this.useOriginalDirection) {
            Location source = this.player.getLocation();
            location.setYaw(source.getYaw());
            location.setPitch(source.getPitch());
        }

        return location;
    }

    @NotNull
    public Teleporter validateFloor() {
        this.validateFloor = true;
        return this;
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

    @NotNull
    public Player getPlayer() {
        return this.player;
    }

    @NotNull
    public Location getDestination() {
        return this.destination;
    }

    public boolean isForced() {
        return this.forced;
    }

    @NotNull
    public Teleporter setForced(boolean forced) {
        this.forced = forced;
        return this;
    }
}
