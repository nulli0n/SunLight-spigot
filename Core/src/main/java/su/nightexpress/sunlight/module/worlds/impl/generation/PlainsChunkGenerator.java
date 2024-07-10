package su.nightexpress.sunlight.module.worlds.impl.generation;

import org.bukkit.Material;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.generator.WorldInfo;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.util.Lists;
import su.nightexpress.nightcore.util.random.Rnd;

import java.util.*;

public class PlainsChunkGenerator extends ChunkGenerator {

    public static final String NAME = "plains";
    private static final int Y_MAX = 90;
    private static final int Y_HALF = 45;

    private final FastNoiseLite terrainNoise = new FastNoiseLite();
    private final FastNoiseLite detailNoise = new FastNoiseLite();

    private static final List<Material> LAYER_SURFACE     = Lists.newList(Material.GRASS_BLOCK);
    private static final List<Material> LAYER_SUB_SUFFACE = Lists.newList(Material.DIRT);
    private static final List<Material> LAYER_ORES        = Lists.newList(Material.COAL_ORE, Material.IRON_ORE);
    private static final List<Material> LAYER_BOTTOM      = Lists.newList(Material.BEDROCK);

    public PlainsChunkGenerator() {
        // Set frequencies, lower frequency = slower change.
        terrainNoise.SetFrequency(0.0005f);
        detailNoise.SetFrequency(0.05f);

        // Fractal pattern (optional).
        terrainNoise.SetFractalType(FastNoiseLite.FractalType.Ridged);
        terrainNoise.SetFractalOctaves(3);
    }

    @Override
    public void generateNoise(@NotNull WorldInfo worldInfo, @NotNull Random random, int chunkX, int chunkZ, @NotNull ChunkData chunkData) {
        for (int y = chunkData.getMinHeight(); y < Y_MAX && y < chunkData.getMaxHeight(); y++) {
            for (int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {

                    if (y == 0) {
                        chunkData.setBlock(x, y, z, Rnd.get(LAYER_BOTTOM));
                        continue;
                    }

                    float terNoise = (terrainNoise.GetNoise(x + (chunkX * 16), z + (chunkZ * 16)) * 2) + (detailNoise.GetNoise(x + (chunkX * 16), z + (chunkZ * 16)) / 10);
                    float detNoise = detailNoise.GetNoise(x + (chunkX * 16), y, z + (chunkZ * 16));
                    float currentY = (Y_HALF + (terNoise * 10));

                    if (y < currentY) {
                        float distanceToSurface = Math.abs(y - currentY); // The absolute y distance to the world surface.
                        double function = .1 * Math.pow(distanceToSurface, 2) - 1; // A second grade polynomial offset to the noise max and min (1, -1).

                        if (detNoise > Math.min(function, -.3)) {
                            // Set grass if the block closest to the surface.
                            if (distanceToSurface < 1 && y > Y_HALF - 2) {
                                chunkData.setBlock(x, y, z, Rnd.get(LAYER_SURFACE));
                            }
                            // It is not the closest block to the surface but still very close.
                            else if (distanceToSurface < 5) {
                                chunkData.setBlock(x, y, z, Rnd.get(LAYER_SUB_SUFFACE));
                            }

                            // Not close to the surface at all.
                            else {
                                Material neighbour = Material.STONE;
                                List<Material> neighbourBlocks = new ArrayList<>(Arrays.asList(
                                    chunkData.getType(Math.max(x - 1, 0), y, z),
                                    chunkData.getType(x, Math.max(y - 1, 0), z),
                                    chunkData.getType(x, y, Math.max(z - 1, 0))
                                )); // A list of all neighbour blocks.

                                // Randomly place vein anchors.
                                if (random.nextFloat() < 0.002) {
                                    neighbour = Rnd.get(LAYER_ORES); // A basic way to shift probability to lower values.
                                }

                                // If the current block has an ore block as neighbour, try the current block.
                                if ((!Collections.disjoint(neighbourBlocks, LAYER_ORES))) {
                                    for (Material neighbourBlock : neighbourBlocks) {
                                        if (LAYER_ORES.contains(neighbourBlock) && random.nextFloat() < -0.01 * LAYER_ORES.indexOf(neighbourBlock) + 0.4) {
                                            neighbour = neighbourBlock;
                                        }
                                    }
                                }

                                chunkData.setBlock(x, y, z, neighbour);
                            }
                        }
                    }
                    else if (y < Y_HALF) {
                        chunkData.setBlock(x, y, z, Material.WATER);
                    }
                }
            }
        }
    }
}
