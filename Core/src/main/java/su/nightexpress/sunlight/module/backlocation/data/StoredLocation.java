package su.nightexpress.sunlight.module.backlocation.data;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.sunlight.utils.pos.BlockPos;

import java.util.concurrent.TimeUnit;

public class StoredLocation {

    private final String   worldName;
    private final BlockPos blockPos;
    private final long expireDate;

    /*public StoredLocation(@NotNull World world, @NotNull Location location, int duration) {

    }*/

    public StoredLocation(@NotNull String worldName, @NotNull BlockPos blockPos, int duration) {
        this.worldName = worldName;
        this.blockPos = blockPos;
        this.expireDate = System.currentTimeMillis() + TimeUnit.MILLISECONDS.convert(duration, TimeUnit.SECONDS);
    }

    public boolean isExpired() {
        return System.currentTimeMillis() >= this.expireDate;
    }

    public boolean isValid() {
        return this.getWorld() != null;
    }

    @Nullable
    public World getWorld() {
        return Bukkit.getWorld(this.worldName);
    }

    @Nullable
    public Location toLocation() {
        World world = this.getWorld();
        if (world == null) return null;

        return this.blockPos.toLocation(world);
    }

    @NotNull
    public String getWorldName() {
        return worldName;
    }

    @NotNull
    public BlockPos getBlockPos() {
        return blockPos;
    }

    public long getExpireDate() {
        return expireDate;
    }
}
