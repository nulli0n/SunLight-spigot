package su.nightexpress.sunlight.module.backlocation;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.module.Module;
import su.nightexpress.sunlight.module.ModuleInfo;
import su.nightexpress.sunlight.module.backlocation.command.BackCommand;
import su.nightexpress.sunlight.module.backlocation.command.DeathBackCommand;
import su.nightexpress.sunlight.module.backlocation.config.BackLocationConfig;
import su.nightexpress.sunlight.module.backlocation.config.BackLocationLang;
import su.nightexpress.sunlight.module.backlocation.config.BackLocationPerms;
import su.nightexpress.sunlight.module.backlocation.data.LocationType;
import su.nightexpress.sunlight.module.backlocation.data.StoredLocation;
import su.nightexpress.sunlight.module.backlocation.listener.BackLocationListener;
import su.nightexpress.sunlight.utils.pos.BlockPos;
import su.nightexpress.sunlight.utils.Teleporter;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class BackLocationModule extends Module {

    private final Map<UUID, Map<LocationType, StoredLocation>> locationMap;

    public BackLocationModule(@NotNull SunLightPlugin plugin, @NotNull String id) {
        super(plugin, id);
        this.locationMap = new HashMap<>();
    }

    @Override
    protected void gatherInfo(@NotNull ModuleInfo moduleInfo) {
        moduleInfo.setConfigClass(BackLocationConfig.class);
        moduleInfo.setLangClass(BackLocationLang.class);
        moduleInfo.setPermissionsClass(BackLocationPerms.class);
    }

    @Override
    protected void onModuleLoad() {
        this.registerCommands();

        this.addListener(new BackLocationListener(this.plugin, this));
    }

    @Override
    protected void onModuleUnload() {
        this.locationMap.clear();
    }

    private void registerCommands() {
        if (BackLocationConfig.PREVIOUS_ENABLED.get()) {
            BackCommand.load(this.plugin, this);
        }
        if (BackLocationConfig.DEATH_ENABLED.get()) {
            DeathBackCommand.load(this.plugin, this);
        }
    }

    public boolean isDisabledWorld(@NotNull World world, @NotNull LocationType type) {
        return this.isDisabledWorld(world.getName(), type);
    }

    public boolean isDisabledWorld(@NotNull String name, @NotNull LocationType type) {
        Set<String> disabled = type == LocationType.PREVIOUS ? BackLocationConfig.PREVIOUS_DISABLED_WORLDS.get() : BackLocationConfig.DEATH_DISABLED_WORLDS.get();
        return disabled.contains(name);
    }

    public boolean isDisabledCause(@NotNull PlayerTeleportEvent.TeleportCause cause) {
        return BackLocationConfig.PREVIOUS_DISABLED_CAUSES.get().contains(cause);
    }

    public int getDuration(@NotNull LocationType type) {
        return type == LocationType.PREVIOUS ? BackLocationConfig.PREVIOUS_EXPIRE_TIME.get() : BackLocationConfig.DEATH_EXPIRE_TIME.get();
    }

    public boolean hasLocation(@NotNull Player player, @NotNull LocationType type) {
        return this.hasLocation(player.getUniqueId(), type);
    }

    public boolean hasLocation(@NotNull UUID playerId, @NotNull LocationType type) {
        return this.getLocation(playerId, type) != null;
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

        BlockPos blockPos = BlockPos.from(location);
        int duration = this.getDuration(type);

        StoredLocation storedLocation = new StoredLocation(world.getName(), blockPos, duration);
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
            if (!silent) (isPrevious ? BackLocationLang.PREVIOUS_TELEPORT_NOTHING : BackLocationLang.DEATH_TELEPORT_NOTHING).getMessage().send(player);
            return false;
        }

        new Teleporter(player, location).centered().useOriginalDirection().teleport();

        if (!silent) (isPrevious ? BackLocationLang.PREVIOUS_TELEPORT_NOTIFY : BackLocationLang.DEATH_TELEPORT_NOTIFY).getMessage().send(player);
        return true;
    }
}
