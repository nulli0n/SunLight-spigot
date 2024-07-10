package su.nightexpress.sunlight.utils.pos;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.util.NumberUtil;

import java.util.Objects;

public class BlockPos implements WorldPos {

    private final int x,y,z;

    public BlockPos(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @NotNull
    public static BlockPos empty() {
        return new BlockPos(0, 0, 0);
    }

    @NotNull
    public static BlockPos from(@NotNull Block block) {
        return new BlockPos(block.getX(), block.getY(), block.getZ());
    }

    @NotNull
    public static BlockPos from(@NotNull Location location) {
        return new BlockPos(location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    @NotNull
    public static BlockPos read(@NotNull FileConfig config, @NotNull String path) {
        String str = config.getString(path, "");
        return deserialize(str);
    }

    public void write(@NotNull FileConfig config, @NotNull String path) {
        config.set(path, this.serialize());
    }

    @NotNull
    public static BlockPos deserialize(@NotNull String str) {
        String[] split = str.split(",");
        if (split.length < 3) return empty();

        int x = NumberUtil.getAnyInteger(split[0], 0);
        int y = NumberUtil.getAnyInteger(split[1], 0);
        int z = NumberUtil.getAnyInteger(split[2], 0);

        return new BlockPos(x, y, z);
    }

    @Override
    @NotNull
    public String serialize() {
        return this.x + "," + this.y + "," + this.z;
    }

    @Override
    @NotNull
    public BlockPos copy() {
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

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof BlockPos other)) return false;
        return x == other.x && y == other.y && z == other.z;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, z);
    }

    @Override
    public String toString() {
        return "BlockPos{" +
            "x=" + x +
            ", y=" + y +
            ", z=" + z +
            '}';
    }
}
