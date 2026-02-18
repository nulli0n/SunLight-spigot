package su.nightexpress.sunlight.module.rtp;

import org.bukkit.*;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.util.Lists;
import su.nightexpress.nightcore.util.LowerCase;
import su.nightexpress.nightcore.util.placeholder.CommonPlaceholders;
import su.nightexpress.nightcore.util.random.Rnd;
import su.nightexpress.sunlight.config.PermissionTree;
import su.nightexpress.sunlight.hook.placeholder.PlaceholderRegistry;
import su.nightexpress.sunlight.module.Module;
import su.nightexpress.sunlight.module.ModuleContext;
import su.nightexpress.sunlight.module.rtp.command.RTPCommandProvider;
import su.nightexpress.sunlight.module.rtp.config.RTPLang;
import su.nightexpress.sunlight.module.rtp.config.RTPPerms;
import su.nightexpress.sunlight.module.rtp.config.RTPSettings;
import su.nightexpress.sunlight.module.rtp.model.LookupRange;
import su.nightexpress.sunlight.teleport.*;

import java.util.Optional;
import java.util.Set;

public class RTPModule extends Module {

    private final TeleportManager teleportManager;
    private final RTPSettings settings;

    public RTPModule(@NotNull ModuleContext context, @NotNull TeleportManager teleportManager) {
        super(context);
        this.teleportManager = teleportManager;
        this.settings = new RTPSettings();
    }

    @Override
    protected void loadModule(@NotNull FileConfig config) {
        this.settings.load(config);
        this.plugin.injectLang(RTPLang.class);
    }

    @Override
    protected void unloadModule() {

    }

    @Override
    protected void registerPermissions(@NotNull PermissionTree root) {
        root.merge(RTPPerms.MODULE);
    }

    @Override
    protected void registerCommands() {
        this.commandRegistry.addProvider("rtp", new RTPCommandProvider(this.plugin, this));
    }

    @Override
    public void registerPlaceholders(@NotNull PlaceholderRegistry registry) {

    }

    public boolean teleportToRandomPlace(@NotNull Player player) {
        World world = player.getWorld();
        LookupRange lookupRange = this.getWorldRange(world.getName());

        if (lookupRange == null && this.settings.isFallbackEnabled()) {
            World fallbackWorld = this.plugin.getServer().getWorld(this.settings.getFallbackWorld());
            if (fallbackWorld != null) {
                world = fallbackWorld;
                lookupRange = this.getWorldRange(fallbackWorld.getName());
            }
        }

        if (lookupRange == null) {
            this.sendPrefixed(RTPLang.TELEPORT_ERROR_INVALID_RANGE, player);
            return false;
        }

        int maxErrors = this.settings.getLookupMaxAttempts();
        int errorCount = 0;
        Location location = null;

        while (errorCount < maxErrors) {
            location = this.pickLocation(world, lookupRange).orElse(null);
            if (location != null) {
                break;
            }
            errorCount++;
        }

        if (location == null) {
            this.sendPrefixed(RTPLang.RANDOM_LOCATION_TELEPORT_FAILURE, player);
            return false;
        }

        Location destination = location.clone();

        TeleportContext teleportContext = TeleportContext.builder(this, player, destination)
            .withFlag(TeleportFlag.CENTERED)
            .withFlag(TeleportFlag.KEEP_DIRECTION)
            .withFlag(TeleportFlag.AVOID_LAVA)
            .callback(() -> {
                this.sendPrefixed(RTPLang.RANDOM_LOCATION_TELEPORT_SUCCESS, player, builder -> builder.with(CommonPlaceholders.LOCATION.resolver(destination)));
            })
            .build();

        return this.teleportManager.teleport(teleportContext, TeleportType.RTP);
    }

    @Nullable
    public LookupRange getWorldRange(@NotNull String name) {
        return this.settings.getLookupRangesMap().get(LowerCase.INTERNAL.apply(name));
    }

    @NotNull
    public Optional<Location> pickLocation(@NotNull World world, @NotNull LookupRange lookupRange) {
        Set<BlockFace> directions = lookupRange.getDirections();
        Set<BlockFace> directionsX = Lists.newSet(BlockFace.EAST, BlockFace.WEST);
        Set<BlockFace> directionsZ = Lists.newSet(BlockFace.SOUTH, BlockFace.NORTH);
        directionsX.retainAll(directions);
        directionsZ.retainAll(directions);
        if (directionsX.isEmpty() && directionsZ.isEmpty()) return Optional.empty();

        int distanceX = Rnd.get(lookupRange.getDistanceMin(), lookupRange.getDistanceMax());
        int distanceZ = Rnd.get(lookupRange.getDistanceMin(), lookupRange.getDistanceMax());

        BlockFace directionX = directionsX.isEmpty() ? BlockFace.UP : Rnd.get(directionsX); // UP for zero modifier
        BlockFace directionZ = directionsZ.isEmpty() ? BlockFace.UP : Rnd.get(directionsZ); // UP for zero modifier

        int locX = lookupRange.getStartX() + (directionX.getModX() * distanceX);
        int locZ = lookupRange.getStartZ() + (directionZ.getModZ() * distanceZ);

        int chunkX = locX >> 4;
        int chunkZ = locZ >> 4;

        Chunk chunk = world.getChunkAt(chunkX, chunkZ, false);

        if (!chunk.isGenerated() && this.settings.isLookupGeneratedChunksOnly()) {
            return Optional.empty();
        }

        // TODO Select from loaded chunks
        if (!chunk.isLoaded() && this.settings.isLookupLoadedChunksOnly()) {
            return Optional.empty();
        }

        ChunkSnapshot snapshot = chunk.getChunkSnapshot();

        int bX = locX & 0xF;
        int bZ = locZ & 0xF;
        int bY;

        if (world.getEnvironment() == World.Environment.NETHER) {
            int start = world.getMinHeight();
            int end = snapshot.getHighestBlockYAt(bX, bZ);
            Integer found = null;
            boolean wasAir = false;

            for (int y = end; y > start; y--) {
                Material blockType = snapshot.getBlockType(bX, y, bZ);
                if (wasAir && blockType.isBlock() && blockType.isSolid() && blockType != Material.BEDROCK) {
                    found = y;
                    break;
                }
                wasAir = blockType.isAir();
            }

            if (found == null) return Optional.empty();

            bY = found;
        }
        else {
            bY = snapshot.getHighestBlockYAt(bX, bZ);
        }

        Material material = snapshot.getBlockType(bX, bY, bZ);
        if (!material.isBlock() || !material.isSolid()) {
            return Optional.empty();
        }

        return Optional.of(new Location(world, locX, bY + 1, locZ));
    }
}
