package su.nightexpress.sunlight.module.rtp.impl;

import org.bukkit.*;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.nightcore.util.Lists;
import su.nightexpress.nightcore.util.Version;
import su.nightexpress.nightcore.util.random.Rnd;
import su.nightexpress.sunlight.Placeholders;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.module.rtp.config.RTPConfig;
import su.nightexpress.sunlight.module.rtp.config.RTPLang;
import su.nightexpress.sunlight.utils.Teleporter;

import java.util.*;

public class LocationFinder {

    private final SunLightPlugin plugin;
    private final Player         player;
    private final World          world;
    private final RangeInfo      rangeInfo;
    private final Set<BlockFace> directionsX;
    private final Set<BlockFace> directionsZ;

    private int     attempts;
    private boolean completed;
    private long    timeout;

    public LocationFinder(@NotNull SunLightPlugin plugin,
                          @NotNull Player player,
                          @NotNull World world,
                          @NotNull RangeInfo rangeInfo,
                          @NotNull Set<BlockFace> directionsX,
                          @NotNull Set<BlockFace> directionsZ) {
        this.plugin = plugin;
        this.player = player;
        this.world = world;
        this.rangeInfo = rangeInfo;
        this.directionsX = directionsX;
        this.directionsZ = directionsZ;
        this.attempts = 0;
    }

    @Nullable
    public static LocationFinder create(@NotNull SunLightPlugin plugin, @NotNull Player player) {
        World world = player.getWorld();

        Map<String, Map<String, RangeInfo>> rangeMap = RTPConfig.LOCATION_SEARCH_RANGES.get();
        if (!rangeMap.containsKey(world.getName()) && RTPConfig.FORCE_TO_DEFAULT.get()) {
            World defWorld = plugin.getServer().getWorld(RTPConfig.DEFAULT_WORLD.get());
            if (defWorld != null) {
                world = defWorld;
            }
        }

        Map<String, RangeInfo> ranges = rangeMap.getOrDefault(world.getName(), Collections.emptyMap());
        RangeInfo rangeInfo = ranges.isEmpty() ? null : Rnd.get(new ArrayList<>(ranges.values()));
        if (rangeInfo == null) return null;

        Set<BlockFace> directions = rangeInfo.getDirections();
        Set<BlockFace> directionsX = Lists.newSet(BlockFace.EAST, BlockFace.WEST);
        Set<BlockFace> directionsZ = Lists.newSet(BlockFace.SOUTH, BlockFace.NORTH);
        directionsX.retainAll(rangeInfo.getDirections());
        directionsZ.retainAll(rangeInfo.getDirections());
        if (directionsX.isEmpty() && directionsZ.isEmpty()) return null;

        return new LocationFinder(plugin, player, world, rangeInfo, directionsX, directionsZ);
    }

    public boolean isCompleted() {
        return completed;
    }

    public boolean isAllAttemptsMade() {
        return this.attempts >= RTPConfig.LOCATION_SEARCH_ATTEMPTS.get();
    }

    public void tick() {
        if (this.completed) return;

        if (this.timeout > 0) {
            this.timeout--;
            return;
        }

        if (!this.player.isOnline() || this.isAllAttemptsMade()) {
            this.completed = true;
            return;
        }

        this.attempts++;

        int distanceX = Rnd.get(rangeInfo.getDistanceMin(), rangeInfo.getDistanceMax());
        int distanceZ = Rnd.get(rangeInfo.getDistanceMin(), rangeInfo.getDistanceMax());

        BlockFace directionX = directionsX.isEmpty() ? BlockFace.UP : Rnd.get(directionsX); // UP for zero modifier
        BlockFace directionZ = directionsZ.isEmpty() ? BlockFace.UP : Rnd.get(directionsZ); // UP for zero modifier

        int locX = directionX.getModX() * distanceX;
        int locZ = directionZ.getModZ() * distanceZ;

        int chunkX = locX >> 4;
        int chunkZ = locZ >> 4;

        Chunk chunk = this.world.getChunkAt(chunkX, chunkZ, false);
        if (Version.isAtLeast(Version.V1_19_R3)) {
            if (!chunk.isGenerated() && RTPConfig.LOCATION_SEARCH_GENERATED_CHUNKS_ONLY.get()) {
                this.timeout();
                return;
            }
        }
        if (!chunk.isLoaded() && RTPConfig.LOCATION_SEARCH_LOADED_CHUNKS_ONLY.get()) {
            this.timeout();
            return;
        }

        ChunkSnapshot snapshot = chunk.getChunkSnapshot();

        int bX = locX & 0xF;
        int bZ = locZ & 0xF;
        int bY = snapshot.getHighestBlockYAt(bX, bZ);

        Material material = snapshot.getBlockType(bX, bY, bZ);
        if (!material.isBlock() || !material.isSolid()) {
            this.timeout();
            return;
        }

        Location location = new Location(this.world, locX, bY + 1, locZ);
        Teleporter teleporter = new Teleporter(this.player, location).centered().useOriginalDirection();

        this.plugin.runTaskLater(task -> {
            teleporter.teleport();
            RTPLang.TELEPORT_NOTIFY_DONE.getMessage().replace(Placeholders.forLocation(location)).send(this.player);
        }, 5L);

        this.completed = true;
    }

    public void timeout() {
        if (this.isAllAttemptsMade()) {
            RTPLang.TELEPORT_NOTIFY_FAILURE.getMessage().send(this.player);
            return;
        }

        this.timeout = 2;

        RTPLang.TELEPORT_NOTIFY_SEARCH.getMessage()
            .replace(Placeholders.GENERIC_CURRENT, this.attempts + 1)
            .replace(Placeholders.GENERIC_MAX, RTPConfig.LOCATION_SEARCH_ATTEMPTS.get())
            .send(this.player);
    }
}
