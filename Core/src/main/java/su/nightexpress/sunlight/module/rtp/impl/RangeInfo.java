package su.nightexpress.sunlight.module.rtp.impl;

import org.bukkit.block.BlockFace;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.config.JYML;
import su.nexmedia.engine.utils.StringUtil;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class RangeInfo {

    private final int            startX;
    private final int            startZ;
    private final int            distanceMin;
    private final int distanceMax;
    private final Set<BlockFace> directions;

    public RangeInfo(int startX, int startZ, int distanceMin, int distanceMax, @NotNull Set<BlockFace> directions) {
        this.startX = startX;
        this.startZ = startZ;
        this.distanceMin = Math.abs(distanceMin);
        this.distanceMax = Math.abs(distanceMax);
        this.directions = directions;
    }

    @NotNull
    public static RangeInfo read(@NotNull JYML cfg, @NotNull String path) {
        int startX = cfg.getInt(path + ".Start_X");
        int startZ = cfg.getInt(path + ".Start_Z");
        int distanceMin = cfg.getInt(path + ".Distance_Min");
        int distanceMax = cfg.getInt(path + ".Distance_Max");
        Set<BlockFace> directions = cfg.getStringSet(path + ".Directions").stream()
            .map(str -> StringUtil.getEnum(str, BlockFace.class).orElse(null))
            .filter(Objects::nonNull).filter(BlockFace::isCartesian).collect(Collectors.toSet());

        return new RangeInfo(startX, startZ, distanceMin, distanceMax, directions);
    }

    public void write(@NotNull JYML cfg, @NotNull String path) {
        cfg.set(path + ".Start_X", this.getStartX());
        cfg.set(path + ".Start_Z", this.getStartZ());
        cfg.set(path + ".Distance_Min", this.getDistanceMin());
        cfg.set(path + ".Distance_Max", this.getDistanceMax());
        cfg.set(path + ".Directions", this.getDirections().stream().map(Enum::name).toList());
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
