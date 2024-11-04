package su.nightexpress.sunlight.module.spawns.impl;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.manager.AbstractFileData;
import su.nightexpress.nightcore.util.Players;
import su.nightexpress.nightcore.util.placeholder.Placeholder;
import su.nightexpress.nightcore.util.placeholder.PlaceholderMap;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.module.spawns.SpawnsModule;
import su.nightexpress.sunlight.module.spawns.config.SpawnsLang;
import su.nightexpress.sunlight.module.spawns.config.SpawnsPerms;
import su.nightexpress.sunlight.module.spawns.event.PlayerSpawnTeleportEvent;
import su.nightexpress.sunlight.module.spawns.util.Placeholders;
import su.nightexpress.sunlight.utils.Teleporter;
import su.nightexpress.sunlight.utils.pos.BlockEyedPos;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class Spawn extends AbstractFileData<SunLightPlugin> implements Placeholder {

    private final SpawnsModule   module;
    private final PlaceholderMap placeholders;
    private final PlaceholderMap editorPlaceholders;

    private final Set<String>    loginGroups;
    private final Set<String>    respawnGroups;

    private String       name;
    private BlockEyedPos blockPos;
    private String       worldName;
    private boolean      permissionRequired;
    private int          priority;
    private boolean      loginTeleport;
    private boolean      deathTeleport;

    public Spawn(@NotNull SunLightPlugin plugin, @NotNull SpawnsModule module, @NotNull File file) {
        super(plugin, file);
        this.module = module;
        this.loginGroups = new HashSet<>();
        this.respawnGroups = new HashSet<>();

        this.placeholders = Placeholders.forSpawn(this);
        this.editorPlaceholders = PlaceholderMap.fusion(this.placeholders, Placeholders.forSpawnEditor(this));
    }

    @Override
    protected boolean onLoad(@NotNull FileConfig config) {
        String locationStr = config.getString("Location");
        if (locationStr != null) {
            String[] split = locationStr.split(",");
            if (split.length != 6) return false;

            String world = split[5];
            BlockEyedPos pos = BlockEyedPos.deserialize(locationStr);
            config.remove("Location");
            config.set("World", world);
            pos.write(config, "BlockPos");
        }

        this.blockPos = BlockEyedPos.read(config, "BlockPos");
        this.worldName = config.getString("World");

        this.setName(config.getString("Name", this.getId()));
        this.setPermissionRequired(config.getBoolean("Permission_Required"));
        this.setPriority(config.getInt("Priority"));
        this.setLoginTeleport(config.getBoolean("Teleport_On_Login.Enabled"));
        this.setDeathTeleport(config.getBoolean("Teleport_On_Death.Enabled"));
        this.loginGroups.addAll(config.getStringSet("Teleport_On_Login.Groups").stream()
            .map(String::toLowerCase).toList());
        this.respawnGroups.addAll(config.getStringSet("Teleport_On_Death.Groups").stream()
            .map(String::toLowerCase).toList());
        
        return true;
    }

    @Override
    protected void onSave(@NotNull FileConfig config) {
        config.set("Name", this.getName());
        config.set("World", this.worldName);
        this.blockPos.write(config, "BlockPos");
        config.set("Permission_Required", this.isPermissionRequired());
        config.set("Priority", this.getPriority());
        config.set("Teleport_On_Login.Enabled", this.isLoginTeleport());
        config.set("Teleport_On_Login.Groups", new ArrayList<>(this.getLoginGroups()));
        config.set("Teleport_On_Death.Enabled", this.isDeathTeleport());
        config.set("Teleport_On_Death.Groups", new ArrayList<>(this.getRespawnGroups()));
    }

    @Override
    @NotNull
    public PlaceholderMap getPlaceholders() {
        return this.placeholders;
    }

    @NotNull
    public PlaceholderMap getEditorPlaceholders() {
        return this.editorPlaceholders;
    }

    public void teleport(@NotNull Player player) {
        this.teleport(player, true, false);
    }

    public boolean teleport(@NotNull Player player, boolean isForce, boolean silent) {
        if (!this.isValid()) {
            if (!silent) SpawnsLang.SPAWN_TELEPORT_ERROR_WORLD.getMessage().send(player);
            return false;
        }

        if (!isForce) {
            if (!this.hasPermission(player)) {
                SpawnsLang.ERROR_NO_PERMISSION.getMessage().send(player);
                return false;
            }
        }

        PlayerSpawnTeleportEvent event = new PlayerSpawnTeleportEvent(player, this);
        plugin.getPluginManager().callEvent(event);
        if (event.isCancelled()) return false;

        Teleporter teleporter = new Teleporter(player, this.getLocation()).centered().validateFloor();
        if (!teleporter.teleport()) return false;

        if (!silent) SpawnsLang.SPAWN_TELEPORT_DONE.getMessage().replace(this.replacePlaceholders()).send(player);
        return true;
    }

    public boolean isDeathSpawn(@NotNull Player player) {
        if (!this.isDeathTeleport()) return false;
        if (!this.hasPermission(player)) return false;

        if (this.respawnGroups.contains(Placeholders.WILDCARD)) return true;

        Set<String> groups = Players.getPermissionGroups(player);

        return this.respawnGroups.stream().anyMatch(groups::contains);
    }

    public boolean isLoginSpawn(@NotNull Player player) {
        if (!this.isLoginTeleport()) return false;
        if (!this.hasPermission(player)) return false;

        if (this.loginGroups.contains(Placeholders.WILDCARD)) return true;

        return Players.getPermissionGroups(player).stream().anyMatch(this.loginGroups::contains);
    }

    public boolean hasPermission(@NotNull Player player) {
        if (!this.isPermissionRequired()) return true;

        return player.hasPermission(SpawnsPerms.SPAWN) || player.hasPermission(SpawnsPerms.PREFIX_SPAWN + this.getId());
    }

    public boolean isValid() {
        return this.getWorld() != null;
    }

    public World getWorld() {
        return this.worldName == null ? null : this.plugin.getServer().getWorld(this.worldName);
    }

    public Location getLocation() {
        World world = this.getWorld();
        if (world == null) return null;

        return this.blockPos.toLocation(world);
    }

    public void setLocation(@NotNull Location location) {
        World locWorld = location.getWorld();
        if (locWorld == null) return;

        this.worldName = locWorld.getName();
        this.blockPos = BlockEyedPos.from(location);
    }

    @NotNull
    public SpawnsModule getSpawnManager() {
        return this.module;
    }

    @NotNull
    public String getWorldName() {
        return worldName;
    }

    @NotNull
    public BlockEyedPos getBlockPos() {
        return blockPos;
    }

    @NotNull
    public String getName() {
        return this.name;
    }

    public void setName(@NotNull String name) {
        this.name = name;
    }

    public boolean isPermissionRequired() {
        return this.permissionRequired;
    }

    public void setPermissionRequired(boolean isPermission) {
        this.permissionRequired = isPermission;
    }

    public int getPriority() {
        return this.priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public boolean isLoginTeleport() {
        return this.loginTeleport;
    }

    public void setLoginTeleport(boolean loginTeleport) {
        this.loginTeleport = loginTeleport;
    }

    @NotNull
    public Set<String> getLoginGroups() {
        return this.loginGroups;
    }

    public boolean isDeathTeleport() {
        return this.deathTeleport;
    }

    public void setDeathTeleport(boolean deathTeleport) {
        this.deathTeleport = deathTeleport;
    }

    @NotNull
    public Set<String> getRespawnGroups() {
        return this.respawnGroups;
    }
}