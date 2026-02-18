package su.nightexpress.sunlight.module.backlocation;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.core.config.CoreLang;
import su.nightexpress.nightcore.util.time.TimeFormatType;
import su.nightexpress.nightcore.util.time.TimeFormats;
import su.nightexpress.sunlight.config.PermissionTree;
import su.nightexpress.sunlight.hook.placeholder.PlaceholderRegistry;
import su.nightexpress.sunlight.module.Module;
import su.nightexpress.sunlight.module.ModuleContext;
import su.nightexpress.sunlight.module.backlocation.command.BackCommandProvider;
import su.nightexpress.sunlight.module.backlocation.command.DeathBackCommandProvider;
import su.nightexpress.sunlight.module.backlocation.config.BackLocationLang;
import su.nightexpress.sunlight.module.backlocation.config.BackLocationPerms;
import su.nightexpress.sunlight.module.backlocation.config.BackLocationSettings;
import su.nightexpress.sunlight.module.backlocation.data.LocationType;
import su.nightexpress.sunlight.module.backlocation.data.StoredLocation;
import su.nightexpress.sunlight.module.backlocation.listener.BackLocationListener;
import su.nightexpress.sunlight.teleport.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class BackLocationModule extends Module {

    private final TeleportManager      teleportManager;
    private final BackLocationSettings settings;

    private final Map<UUID, Map<LocationType, StoredLocation>> locationMap;

    public BackLocationModule(@NotNull ModuleContext context, @NotNull TeleportManager teleportManager) {
        super(context);
        this.teleportManager = teleportManager;
        this.settings = new BackLocationSettings();
        this.locationMap = new HashMap<>();
    }

    @Override
    protected void loadModule(@NotNull FileConfig config) {
        this.settings.load(config);
        this.plugin.injectLang(BackLocationLang.class);
        this.registerCommands();

        this.addListener(new BackLocationListener(this.plugin, this));
    }

    @Override
    protected void unloadModule() {
        this.locationMap.clear();
    }

    @Override
    protected void registerPermissions(@NotNull PermissionTree root) {
        root.merge(BackLocationPerms.ROOT);
    }

    protected void registerCommands() {
        if (this.settings.cacheTeleports.get()) {
            this.commandRegistry.addProvider("back", new BackCommandProvider(this.plugin, this, this.userManager));
        }
        if (this.settings.cacheDeaths.get()) {
            this.commandRegistry.addProvider("deathback", new DeathBackCommandProvider(this.plugin, this, this.userManager));
        }
    }

    @Override
    public void registerPlaceholders(@NotNull PlaceholderRegistry registry) {
        registry.register("backlocation_has_previous", (player, payload) -> CoreLang.STATE_YES_NO.get(this.hasLocation(player, LocationType.PREVIOUS)));
        registry.register("backlocation_has_death", (player, payload) -> CoreLang.STATE_YES_NO.get(this.hasLocation(player, LocationType.DEATH)));

        registry.register("backlocation_previous_expire_in", (player, payload) -> this.formatExpireInPlaceholder(player, LocationType.PREVIOUS));
        registry.register("backlocation_death_expire_in", (player, payload) -> this.formatExpireInPlaceholder(player, LocationType.DEATH));
    }

    @NotNull
    private String formatExpireInPlaceholder(@NotNull Player player, @NotNull LocationType type) {
        if (this.hasLocation(player, type)) {
            return TimeFormats.formatDuration(this.getExpireDate(player, type), TimeFormatType.LITERAL);
        }
        return CoreLang.OTHER_NONE.text();
    }

    public void handleTeleport(@NotNull Player player, @NotNull PlayerTeleportEvent event) {
        if (!this.settings.cacheTeleports.get()) return;

        Location destination = event.getTo();
        World world = player.getWorld();

        if (!player.hasPermission(BackLocationPerms.BYPASS_PREVIOUS_CAUSES) && this.isDisabledCause(event.getCause())) {
            return;
        }
        if (!player.hasPermission(BackLocationPerms.BYPASS_PREVIOUS_WORLDS) && this.isDisabledWorld(world, LocationType.PREVIOUS)) {
            return;
        }

        Location from = event.getFrom();
        World toWorld = destination.getWorld();
        int minDifference = this.settings.teleportMinDistanceDifference.get();
        int requiredDistance = minDifference * minDifference;

        if (world == toWorld && from.distanceSquared(destination) < requiredDistance) {
            if (this.hasLocation(player, LocationType.PREVIOUS)) return;
        }

        this.saveLocation(player, from, LocationType.PREVIOUS);
    }

    public void handleDeath(@NotNull Player player, @NotNull PlayerDeathEvent event) {
        if (!this.settings.cacheDeaths.get()) return;

        if (!player.hasPermission(BackLocationPerms.BYPASS_DEATH_WORLDS) && this.isDisabledWorld(player.getWorld(), LocationType.DEATH)) {
            return;
        }

        this.saveLocation(player, player.getLocation(), LocationType.DEATH);
    }

    public boolean isDisabledWorld(@NotNull World world, @NotNull LocationType type) {
        return this.isDisabledWorld(world.getName(), type);
    }

    public boolean isDisabledWorld(@NotNull String name, @NotNull LocationType type) {
        Set<String> disabled = type == LocationType.PREVIOUS ? this.settings.teleportWorldBlacklist.get() : this.settings.deathWorldBlacklist.get();
        return disabled.contains(name);
    }

    public boolean isDisabledCause(@NotNull PlayerTeleportEvent.TeleportCause cause) {
        return this.settings.ignoredTeleportCauses.get().contains(cause);
    }

    public int getDuration(@NotNull LocationType type) {
        return type == LocationType.PREVIOUS ? this.settings.teleportCacheExpireTime.get() : this.settings.deathCacheExpireTime.get();
    }

    public boolean hasLocation(@NotNull Player player, @NotNull LocationType type) {
        return this.hasLocation(player.getUniqueId(), type);
    }

    public boolean hasLocation(@NotNull UUID playerId, @NotNull LocationType type) {
        return this.getLocation(playerId, type) != null;
    }

    public long getExpireDate(@NotNull Player player, @NotNull LocationType type) {
        StoredLocation location = this.getLocation(player, type);
        return location == null ? 0L : location.getExpireDate();
    }

    @NotNull
    public Map<LocationType, StoredLocation> getLocationMap(@NotNull Player player) {
        return this.getLocationMap(player.getUniqueId());
    }

    @NotNull
    public Map<LocationType, StoredLocation> getLocationMap(@NotNull UUID playerId) {
        var map = this.locationMap.computeIfAbsent(playerId, k -> new HashMap<>());
        map.values().removeIf(location -> location.isExpired() || !location.isValid());

        return map;
    }

    @Nullable
    public StoredLocation getLocation(@NotNull Player player, @NotNull LocationType type) {
        return this.getLocation(player.getUniqueId(), type);
    }

    @Nullable
    public StoredLocation getLocation(@NotNull UUID playerId, @NotNull LocationType type) {
        return this.getLocationMap(playerId).get(type);
    }

    public void saveLocation(@NotNull Player player, @NotNull Location location, @NotNull LocationType type) {
        this.saveLocation(player.getUniqueId(), location, type);
    }

    public void saveLocation(@NotNull UUID playerId, @NotNull Location location, @NotNull LocationType type) {
        World world = location.getWorld();
        if (world == null) return;

        int duration = this.getDuration(type);

        StoredLocation storedLocation = new StoredLocation(world.getName(), location.getX(), location.getY(), location.getZ(), duration);
        this.getLocationMap(playerId).put(type, storedLocation);
    }

    public boolean teleportToLocation(@NotNull Player player, @NotNull LocationType type) {
        return this.teleportToLocation(player, type, false);
    }

    public boolean teleportToLocation(@NotNull Player player, @NotNull LocationType type, boolean silent) {
        boolean isPrevious = type == LocationType.PREVIOUS;
        StoredLocation storedLocation = this.getLocation(player, type);
        Location location = storedLocation == null ? null : storedLocation.toLocation();

        if (storedLocation == null || location == null) {
            if (!silent) this.sendPrefixed(isPrevious ? BackLocationLang.PREVIOUS_ERROR_NOTHING_NOTIFY : BackLocationLang.DEATH_ERROR_NOTHING_NOTIFY, player);
            return false;
        }

        TeleportType teleportType = type == LocationType.DEATH ? TeleportType.DEATH_LOCATION : TeleportType.PREVIOUS_LOCATION;
        TeleportContext teleportContext = TeleportContext.builder(this, player, location)
            .withFlag(TeleportFlag.KEEP_DIRECTION)
            .callback(() -> {
                if (!silent) this.sendPrefixed(isPrevious ? BackLocationLang.PREVIOUS_TELEPORT_NOTIFY : BackLocationLang.DEATH_TELEPORT_NOTIFY, player);
            })
            .build();

        return this.teleportManager.teleport(teleportContext, teleportType);
    }

    @NotNull
    public BackLocationSettings getSettings() {
        return this.settings;
    }
}
