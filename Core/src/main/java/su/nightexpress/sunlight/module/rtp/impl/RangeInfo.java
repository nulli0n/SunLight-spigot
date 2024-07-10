package su.nightexpress.sunlight.module.rtp.impl;

import org.bukkit.block.BlockFace;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.util.StringUtil;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class RangeInfo {

    private final int            startX;
    private final int            startZ;
    private final int            distanceMin;
    private final int            distanceMax;
    private final Set<BlockFace> directions;

    public RangeInfo(int startX, int startZ, int distanceMin, int distanceMax, @NotNull Set<BlockFace> directions) {
        this.startX = startX;
        this.startZ = startZ;
        this.distanceMin = Math.abs(distanceMin);
        this.distanceMax = Math.abs(distanceMax);
        this.directions = directions;
    }

    @NotNull
    public static RangeInfo read(@NotNull FileConfig config, @NotNull String path) {
        int startX = config.getInt(path + ".Start_X");
        int startZ = config.getInt(path + ".Start_Z");
        int distanceMin = config.getInt(path + ".Distance_Min");
        int distanceMax = config.getInt(path + ".Distance_Max");
        Set<BlockFace> directions = config.getStringSet(path + ".Directions").stream()
            .map(str -> StringUtil.getEnum(str, BlockFace.class).orElse(null))
            .filter(Objects::nonNull).filter(BlockFace::isCartesian).collect(Collectors.toSet());

        return new RangeInfo(startX, startZ, distanceMin, distanceMax, directions);
    }

    public void write(@NotNull FileConfig config, @NotNull String path) {
        config.set(path + ".Start_X", this.getStartX());
        config.set(path + ".Start_Z", this.getStartZ());
        config.set(path + ".Distance_Min", this.getDistanceMin());
        config.set(path + ".Distance_Max", this.getDistanceMax());
        config.set(path + ".Directions", this.getDirections().stream().map(Enum::name).toList());
    }

    public int getStartX() {
        return startX;
    }

    public int getStartZ() {
        return startZ;
    }

    public int getDistanceMin() {
        return distanceMin;
    }

    public int getDistanceMax() {
        return distanceMax;
    }

    @NotNull
    public Set<BlockFace> getDirections() {
        return directions;
    }
}
