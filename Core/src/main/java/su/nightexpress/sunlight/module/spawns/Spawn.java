package su.nightexpress.sunlight.module.spawns;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.util.bukkit.NightItem;
import su.nightexpress.nightcore.util.geodata.pos.ExactPos;
import su.nightexpress.nightcore.util.placeholder.PlaceholderResolvable;
import su.nightexpress.nightcore.util.placeholder.PlaceholderResolver;
import su.nightexpress.sunlight.module.spawns.config.SpawnsPerms;
import su.nightexpress.sunlight.module.spawns.model.SpawnRule;

import java.nio.file.Path;
import java.util.Set;

public class Spawn implements PlaceholderResolvable {

    private final String id;
    private final Path file;

    private String    name;
    private ExactPos  blockPos;
    private String    worldName;
    private NightItem icon;
    private boolean   permissionRequired;
    private int       priority;
    private SpawnRule loginRule;
    private SpawnRule respawnRule;

    private World world;
    private boolean dirty;

    public Spawn(@NotNull String id, @NotNull Path file) {
        this.id = id;
        this.file = file;
    }

    public void load() {
        FileConfig.load(this.file).edit(this::load);
    }

    public boolean load(@NotNull FileConfig config) {
        String locationStr = config.getString("Location");
        if (locationStr != null) {
            String[] split = locationStr.split(",");
            if (split.length != 6) return false;

            String world = split[5];
            ExactPos pos = ExactPos.deserialize(locationStr);
            config.remove("Location");
            config.set("World", world);
            pos.write(config, "BlockPos");
        }

        if (config.contains("Teleport_On_Login")) {
            boolean enabled = config.getBoolean("Teleport_On_Login.Enabled");
            Set<String> ranks = config.getStringSet("Teleport_On_Login.Groups");

            SpawnRule rule = new SpawnRule(enabled, ranks);
            config.set("Rules.Login", rule);
            config.remove("Teleport_On_Login");
        }

        if (config.contains("Teleport_On_Death")) {
            boolean enabled = config.getBoolean("Teleport_On_Death.Enabled");
            Set<String> ranks = config.getStringSet("Teleport_On_Death.Groups");

            SpawnRule rule = new SpawnRule(enabled, ranks);
            config.set("Rules.Respawn", rule);
            config.remove("Teleport_On_Death");
        }

        this.blockPos = ExactPos.read(config, "BlockPos");
        this.worldName = config.getString("World");

        this.setName(config.getString("Name", this.getId()));
        this.setIcon(config.getCosmeticItem("Icon", NightItem.fromType(Material.GRASS_BLOCK)));
        this.setPermissionRequired(config.getBoolean("Permission_Required"));
        this.setPriority(config.getInt("Priority"));

        this.setLoginRule(SpawnRule.read(config, "Rules.Login"));
        this.setRespawnRule(SpawnRule.read(config, "Rules.Respawn"));
        
        return true;
    }

    public void saveIfDirty() {
        if (!this.dirty) return;

        this.save();
        this.markClean();
    }

    public void save() {
        FileConfig.load(this.file).edit(this::writeToFile);
    }

    public void writeToFile(@NotNull FileConfig config) {
        config.set("Name", this.name);
        config.set("World", this.worldName);
        config.set("Icon", this.icon);
        config.set("BlockPos", this.blockPos);
        config.set("Permission_Required", this.permissionRequired);
        config.set("Priority", this.priority);
        config.set("Rules.Login", this.loginRule);
        config.set("Rules.Respawn", this.respawnRule);
    }

    @Override
    @NotNull
    public PlaceholderResolver placeholders() {
        return SpawnsPlaceholders.SPAWN.resolver(this);
    }

    public boolean isAvailableForRespawn(@NotNull Player player) {
        return this.hasPermission(player) && this.respawnRule.isApplicable(player);
    }

    public boolean isAvailableForJoin(@NotNull Player player) {
        return this.hasPermission(player) && this.loginRule.isApplicable(player);
    }

    public boolean hasPermission(@NotNull Player player) {
        if (!this.isPermissionRequired()) return true;

        return SpawnsPerms.SPAWN.hasChildAccess(player, this.getId());
    }

    public boolean isWorld(@NotNull World world) {
        return this.worldName.equalsIgnoreCase(world.getName());
    }

    public boolean activate() {
        World world = Bukkit.getWorld(this.worldName);
        return world != null && this.activate(world);
    }

    public boolean activate(@NotNull World world) {
        if (this.isWorld(world)) {
            this.world = world;
            return true;
        }
        return false;
    }

    public void deactivate() {
        this.world = null;
    }

    public boolean isActive() {
        return this.world != null;
    }

    @NotNull
    public World getWorld() {
        if (!this.isActive()) throw new IllegalStateException("Spawn is not active!");

        return this.world;
    }

    @NotNull
    public Location getLocation() {
        if (!this.isActive()) throw new IllegalStateException("Spawn is not active!");

        return this.blockPos.toLocation(this.world);
    }

    public void setLocation(@NotNull Location location) {
        World locWorld = location.getWorld();
        if (locWorld == null) return;

        this.worldName = locWorld.getName();
        this.blockPos = ExactPos.from(location);
    }

    public boolean isDirty() {
        return this.dirty;
    }

    public void markDirty() {
        this.dirty = true;
    }

    public void markClean() {
        this.dirty = false;
    }

    @NotNull
    public String getId() {
        return this.id;
    }

    @NotNull
    public Path getFile() {
        return this.file;
    }

    @NotNull
    public String getWorldName() {
        return worldName;
    }

    @NotNull
    public ExactPos getBlockPos() {
        return this.blockPos;
    }

    @NotNull
    public String getName() {
        return this.name;
    }

    public void setName(@NotNull String name) {
        this.name = name;
    }

    @NotNull
    public NightItem getIcon() {
        return this.icon.copy();
    }

    public void setIcon(@NotNull NightItem icon) {
        this.icon = icon.copy();
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

    @NotNull
    public SpawnRule getLoginRule() {
        return this.loginRule;
    }

    public void setLoginRule(@NotNull SpawnRule loginRule) {
        this.loginRule = loginRule;
    }

    @NotNull
    public SpawnRule getRespawnRule() {
        return this.respawnRule;
    }

    public void setRespawnRule(@NotNull SpawnRule respawnRule) {
        this.respawnRule = respawnRule;
    }
}