package su.nightexpress.sunlight.utils.pos;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.util.NumberUtil;

import java.util.Objects;

@Deprecated
public class BlockEyedPos implements WorldPos {

    private final int x,y,z;
    private final float yaw, pitch;

    public BlockEyedPos(int x, int y, int z, float yaw, float pitch) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
    }

    @NotNull
    public static BlockEyedPos empty() {
        return new BlockEyedPos(0, 0, 0, 0F, 0F);
    }

    @NotNull
    public static BlockEyedPos from(@NotNull Block block) {
        return new BlockEyedPos(block.getX(), block.getY(), block.getZ(), 0F, 0F);
    }

    @NotNull
    public static BlockEyedPos from(@NotNull Location location) {
        return new BlockEyedPos(location.getBlockX(), location.getBlockY(), location.getBlockZ(), location.getYaw(), location.getPitch());
    }

    @NotNull
    public static BlockEyedPos read(@NotNull FileConfig cfg, @NotNull String path) {
        String str = cfg.getString(path, "");
        return deserialize(str);
    }

    public void write(@NotNull FileConfig config, @NotNull String path) {
        config.set(path, this.serialize());
    }

    @NotNull
    public static BlockEyedPos deserialize(@NotNull String str) {
        String[] split = str.split(",");
        if (split.length < 3) return empty();

        int x = (int) NumberUtil.getAnyDouble(split[0], 0);
        int y = (int) NumberUtil.getAnyDouble(split[1], 0);
        int z = (int) NumberUtil.getAnyDouble(split[2], 0);
        float pitch = (float) NumberUtil.getAnyDouble(split[3], 0);
        float yaw = (float) NumberUtil.getAnyDouble(split[4], 0);

        return new BlockEyedPos(x, y, z, yaw, pitch);
    }

    @Override
    @NotNull
    public String serialize() {
        return this.x + "," + this.y + "," + this.z + "," + this.pitch + "," + this.yaw;
    }

    @Override
    @NotNull
    public Location toLocation(@NotNull World world) {
        Location location = WorldPos.super.toLocation(world);
        location.setPitch(this.pitch);
        location.setYaw(this.yaw);
        return location;
    }

    @Override
    @NotNull
    public BlockEyedPos copy() {
        return new BlockEyedPos(this.x, this.y, this.z, this.yaw, this.pitch);
    }

    @NotNull
    public BlockPos toBlockPos() {
        return new BlockPos(this.x, this.y, this.z);
    }

    @Override
    public int getX() {
        return x;
    }

    @Override
    public int getY() {
        return y;
    }

    @Override
    public int getZ() {
        return z;
    }

    public float getYaw() {
        return yaw;
    }

    public float getPitch() {
        return pitch;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof BlockEyedPos other)) return false;
        return x == other.x && y == other.y && z == other.z && Float.compare(yaw, other.yaw) == 0 && Float.compare(pitch, other.pitch) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, z, yaw, pitch);
    }

    @Override
    public String toString() {
        return "BlockEyePos{" +
            "x=" + x +
            ", y=" + y +
            ", z=" + z +
            ", yaw=" + yaw +
            ", pitch=" + pitch +
            '}';
    }
}
