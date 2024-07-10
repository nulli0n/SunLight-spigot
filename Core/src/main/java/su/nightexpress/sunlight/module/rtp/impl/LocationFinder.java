package su.nightexpress.sunlight.module.rtp.impl;

import org.bukkit.*;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.util.Version;
import su.nightexpress.nightcore.util.random.Rnd;
import su.nightexpress.sunlight.Placeholders;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.module.rtp.config.RTPConfig;
import su.nightexpress.sunlight.module.rtp.config.RTPLang;

import java.util.*;

public class LocationFinder {

    private final SunLightPlugin plugin;
    private final Player         player;

    private World  world;
    private int attempts;
    private long timeout;

    public LocationFinder(@NotNull SunLightPlugin plugin, @NotNull Player player, int attempts) {
        this.plugin = plugin;
        this.player = player;
        this.world = player.getWorld();
        this.attempts = attempts;
    }

    public boolean isFailed() {
        return this.attempts == 0;
    }

    public void tick() {
        if (this.timeout > 0) {
            this.timeout--;
            return;
        }
        if (!this.player.isOnline()) return;

        Map<String, Map<String, RangeInfo>> map = RTPConfig.LOCATION_SEARCH_RANGES.get();

        if (!map.containsKey(this.world.getName()) && RTPConfig.FORCE_TO_DEFAULT.get()) {
            World defWorld = plugin.getServer().getWorld(RTPConfig.DEFAULT_WORLD.get());
            if (defWorld != null) {
                this.world = defWorld;
            }
        }

        Map<String, RangeInfo> ranges = map.getOrDefault(this.world.getName(), Collections.emptyMap());
        RangeInfo rangeInfo = ranges.isEmpty() ? null : Rnd.get(new ArrayList<>(ranges.values()));
        if (rangeInfo == null || rangeInfo.getDirections().isEmpty()) {
            this.attempts = 0;
            this.takeAttempt();
            return;
        }

        int distanceX = Rnd.get(rangeInfo.getDistanceMin(), rangeInfo.getDistanceMax());
        int distanceZ = Rnd.get(rangeInfo.getDistanceMin(), rangeInfo.getDistanceMax());

        Set<BlockFace> directions = rangeInfo.getDirections();
        List<BlockFace> directionsX = new ArrayList<>(List.of(BlockFace.EAST, BlockFace.WEST));
        List<BlockFace> directionsZ = new ArrayList<>(List.of(BlockFace.SOUTH, BlockFace.NORTH));
        directionsX.retainAll(rangeInfo.getDirections());
        directionsZ.retainAll(rangeInfo.getDirections());

        BlockFace directionX = directionsX.isEmpty() ? BlockFace.UP : Rnd.get(directionsX);
        BlockFace directionZ = directionsZ.isEmpty() ? BlockFace.UP : Rnd.get(directionsZ);

        int locX = directionX.getModX() * distanceX;
        int locZ = directionZ.getModZ() * distanceZ;

        int chunkX = locX >> 4;
        int chunkZ = locZ >> 4;

        Chunk chunk = this.world.getChunkAt(chunkX, chunkZ, false);
        if (Version.isAtLeast(Version.V1_19_R3)) {
            if (!chunk.isGenerated() && RTPConfig.LOCATION_SEARCH_GENERATED_CHUNKS_ONLY.get()) {
                this.takeAttempt();
                return;
            }
        }
        if (!chunk.isLoaded() && RTPConfig.LOCATION_SEARCH_LOADED_CHUNKS_ONLY.get()) {
            this.takeAttempt();
            return;
        }

        ChunkSnapshot snapshot = chunk.getChunkSnapshot();

        int bX = locX & 0xF;
        int bZ = locZ & 0xF;
        int bY = snapshot.getHighestBlockYAt(bX, bZ);

        Material material = snapshot.getBlockType(bX, bY, bZ);
        if (!material.isBlock() || !material.isSolid()) {
            this.takeAttempt();
            return;
        }

        Location location = new Location(this.world, locX + 0.5D, bY + 1, locZ + 0.5D);
        this.plugin.runTaskLater(task -> {
            this.player.teleport(location);
            RTPLang.TELEPORT_NOTIFY_DONE.getMessage().replace(Placeholders.forLocation(location)).send(this.player);
        }, 5L);
        this.attempts = 0;
    }

    public void takeAttempt() {
        this.attempts = Math.max(this.attempts - 1, 0);
        if (this.attempts == 0) {
            RTPLang.TELEPORT_NOTIFY_FAILURE.getMessage().send(this.player);
        }
        else {
            this.timeout = 2;
            RTPLang.TELEPORT_NOTIFY_SEARCH.getMessage()
                .replace(Placeholders.GENERIC_CURRENT, RTPConfig.LOCATION_SEARCH_ATTEMPTS.get() - this.attempts)
                .replace(Placeholders.GENERIC_MAX, RTPConfig.LOCATION_SEARCH_ATTEMPTS.get())
                .send(this.player);
        }
    }
}
