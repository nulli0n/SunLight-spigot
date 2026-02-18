package su.nightexpress.sunlight.utils;

import org.bukkit.block.BlockFace;
import org.jetbrains.annotations.NotNull;

public enum Direction {

    EAST(BlockFace.EAST),
    NORTH_EAST(BlockFace.NORTH_EAST),
    NORTH(BlockFace.NORTH),
    NORTH_WEST(BlockFace.NORTH_WEST),
    WEST(BlockFace.WEST),
    SOUTH_WEST(BlockFace.SOUTH_WEST),
    SOUTH(BlockFace.SOUTH),
    SOUTH_EAST(BlockFace.SOUTH_EAST),
    HERE(BlockFace.SELF);

    private final BlockFace bukkit;

    Direction(@NotNull BlockFace bukkit) {
        this.bukkit = bukkit;
    }

    @NotNull
    public BlockFace toBukkit() {
        return this.bukkit;
    }
}
