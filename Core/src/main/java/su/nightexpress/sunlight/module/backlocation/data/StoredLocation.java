package su.nightexpress.sunlight.module.backlocation.data;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.TimeUnit;

public class StoredLocation {

    private final String   worldName;
    //private final BlockPos blockPos;
    private final double x;
    private final double y;
    private final double z;
    private final long expireDate;

    public StoredLocation(@NotNull String worldName, double x, double y, double z, int duration) {
        this.worldName = worldName;
        this.x = x;
        this.y = y;
        this.z = z;
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

        return new Location(world, x, y, z);

        //return this.blockPos.toLocation(world);
    }

    @NotNull
    public String getWorldName() {
        return worldName;
    }

//    @NotNull
//    public BlockPos getBlockPos() {
//        return blockPos;
//    }

    public long getExpireDate() {
        return expireDate;
    }
}
