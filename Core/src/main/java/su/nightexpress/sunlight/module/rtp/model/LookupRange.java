package su.nightexpress.sunlight.module.rtp.model;

import org.bukkit.block.BlockFace;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.config.Writeable;
import su.nightexpress.nightcore.util.Enums;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class LookupRange implements Writeable {

    private final int            startX;
    private final int            startZ;
    private final int            distanceMin;
    private final int            distanceMax;
    private final Set<BlockFace> directions;

    public LookupRange(int startX, int startZ, int distanceMin, int distanceMax, @NotNull Set<BlockFace> directions) {
        this.startX = startX;
        this.startZ = startZ;
        this.distanceMin = Math.abs(distanceMin);
        this.distanceMax = Math.abs(distanceMax);
        this.directions = directions;
    }

    @NotNull
    public static LookupRange read(@NotNull FileConfig config, @NotNull String path) {
        int startX = config.getInt(path + ".Start_X");
        int startZ = config.getInt(path + ".Start_Z");
        int distanceMin = config.getInt(path + ".Distance_Min");
        int distanceMax = config.getInt(path + ".Distance_Max");
        Set<BlockFace> directions = config.getStringSet(path + ".Directions").stream()
            .map(string -> Enums.get(string, BlockFace.class))
            .filter(Objects::nonNull).filter(BlockFace::isCartesian).collect(Collectors.toSet());

        return new LookupRange(startX, startZ, distanceMin, distanceMax, directions);
    }

    @Override
    public void write(@NotNull FileConfig config, @NotNull String path) {
        config.set(path + ".Start_X", this.startX);
        config.set(path + ".Start_Z", this.startZ);
        config.set(path + ".Distance_Min", this.getDistanceMin());
        config.set(path + ".Distance_Max", this.getDistanceMax());
        config.set(path + ".Directions", this.getDirections().stream().map(Enum::name).toList());
    }

    public int getStartX() {
        return this.startX;
    }

    public int getStartZ() {
        return this.startZ;
    }

    public int getDistanceMin() {
        return this.distanceMin;
    }

    public int getDistanceMax() {
        return this.distanceMax;
    }

    @NotNull
    public Set<BlockFace> getDirections() {
        return this.directions;
    }
}
