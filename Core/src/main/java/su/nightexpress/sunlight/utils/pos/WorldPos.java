package su.nightexpress.sunlight.utils.pos;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

public interface WorldPos {

    @NotNull String serialize();

    @NotNull
    default Chunk toChunk(@NotNull World world) {
        int chunkX = this.getX() >> 4;
        int chunkZ = this.getZ() >> 4;

        return world.getChunkAt(chunkX, chunkZ);
    }

    default boolean isChunkLoaded(@NotNull World world) {
        int chunkX = this.getX() >> 4;
        int chunkZ = this.getZ() >> 4;

        return world.isChunkLoaded(chunkX, chunkZ);
    }

    @NotNull
    default Location toLocation(@NotNull World world) {
        return new Location(world, this.getX(), this.getY(), this.getZ());
    }

    @NotNull
    WorldPos copy();

    default boolean isEmpty() {
        return this.getX() == 0 && this.getY() == 0 && this.getZ() == 0;
    }

    int getX();

    int getY();

    int getZ();
}
