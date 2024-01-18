package su.nightexpress.sunlight.module.worlds.impl.generation;

import org.bukkit.Material;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.generator.WorldInfo;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class FlatChunkGenerator extends ChunkGenerator {

    public static final String NAME = "flat";

    @Override
    public void generateNoise(@NotNull WorldInfo worldInfo, @NotNull Random random, int chunkX, int chunkZ, @NotNull ChunkGenerator.ChunkData chunkData) {
        for (int y = chunkData.getMinHeight(); y < 6 && y < chunkData.getMaxHeight(); y++) {
            if (y < 0) continue;

            for(int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {
                    Material material = Material.BEDROCK;

                    if (y > 0) {
                        if (y == 5) material = Material.GRASS_BLOCK;
                        else material = Material.DIRT;
                    }

                    chunkData.setBlock(x, y, z, material);
                }
            }
        }
    }
}
