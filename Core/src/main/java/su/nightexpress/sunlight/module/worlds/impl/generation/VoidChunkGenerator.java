package su.nightexpress.sunlight.module.worlds.impl.generation;

import org.bukkit.generator.ChunkGenerator;
import org.bukkit.generator.WorldInfo;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class VoidChunkGenerator extends ChunkGenerator {

    public static final String NAME = "void";

    @Override
    public void generateNoise(@NotNull WorldInfo worldInfo, @NotNull Random random, int x, int z, @NotNull ChunkGenerator.ChunkData chunkData) {

    }
}
