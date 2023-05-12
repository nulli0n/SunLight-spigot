package su.nightexpress.sunlight.module.spawns.impl;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.config.JYML;
import su.nexmedia.engine.api.manager.AbstractConfigHolder;
import su.nexmedia.engine.api.manager.ICleanable;
import su.nexmedia.engine.api.manager.IEditable;
import su.nexmedia.engine.api.placeholder.Placeholder;
import su.nexmedia.engine.api.placeholder.PlaceholderMap;
import su.nexmedia.engine.hooks.Hooks;
import su.nexmedia.engine.lang.LangManager;
import su.nexmedia.engine.utils.Colorizer;
import su.nexmedia.engine.utils.NumberUtil;
import su.nightexpress.sunlight.SunLight;
import su.nightexpress.sunlight.config.Lang;
import su.nightexpress.sunlight.module.spawns.SpawnsModule;
import su.nightexpress.sunlight.module.spawns.config.SpawnsLang;
import su.nightexpress.sunlight.module.spawns.editor.SpawnSettingsEditor;
import su.nightexpress.sunlight.module.spawns.util.Placeholders;
import su.nightexpress.sunlight.module.spawns.util.SpawnsPerms;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class Spawn extends AbstractConfigHolder<SunLight> implements ICleanable, IEditable, Placeholder {

    private final SpawnsModule   spawnsModule;
    private final PlaceholderMap placeholderMap;
    private final Set<String> loginTeleportGroups;
    private final Set<String> respawnTeleportGroups;

    private String   name;
    private Location location;
    private boolean  isPermission;
    private boolean  isDefault;
    private int     priority;
    private boolean loginTeleportEnabled;
    private boolean firstLoginTeleportEnabled;
    private boolean respawnTeleportEnabled;

    private SpawnSettingsEditor editor;

    public Spawn(@NotNull SpawnsModule module, @NotNull JYML cfg) {
        super(module.plugin(), cfg);
        this.spawnsModule = module;
        this.loginTeleportGroups = new HashSet<>();
        this.respawnTeleportGroups = new HashSet<>();

        this.placeholderMap = new PlaceholderMap()
            .add(Placeholders.SPAWN_ID, this::getId)
            .add(Placeholders.SPAWN_NAME, this::getName)
            .add(Placeholders.SPAWN_LOCATION_WORLD, () -> {
                Location location = this.getLocation();
                return location.getWorld() == null ? "null" : LangManager.getWorld(location.getWorld());
            })
            .add(Placeholders.SPAWN_LOCATION_X, () -> NumberUtil.format(this.getLocation().getX()))
            .add(Placeholders.SPAWN_LOCATION_Y, () -> NumberUtil.format(this.getLocation().getY()))
            .add(Placeholders.SPAWN_LOCATION_Z, () -> NumberUtil.format(this.getLocation().getZ()))
            .add(Placeholders.SPAWN_PERMISSION_REQUIRED, () -> LangManager.getBoolean(this.isPermission))
            .add(Placeholders.SPAWN_PERMISSION_NODE, () -> SpawnsPerms.PREFIX_SPAWN + this.getId())
            .add(Placeholders.SPAWN_PRIORITY, () -> String.valueOf(this.getPriority()))
            .add(Placeholders.SPAWN_IS_DEFAULT, () -> LangManager.getBoolean(this.isDefault()))
            .add(Placeholders.SPAWN_LOGIN_TELEPORT_ENABLED, () -> LangManager.getBoolean(this.isLoginTeleportEnabled()))
            .add(Placeholders.SPAWN_LOGIN_TELEPORT_NEWBIES, () -> LangManager.getBoolean(this.isFirstLoginTeleportEnabled()))
            .add(Placeholders.SPAWN_RESPAWN_TELEPORT_ENABLED, () -> LangManager.getBoolean(this.isRespawnTeleportEnabled()))
            .add(Placeholders.SPAWN_LOGIN_TELEPORT_GROUPS, () -> String.join(",", this.getLoginTeleportGroups()))
            .add(Placeholders.SPAWN_RESPAWN_TELEPORT_GROUPS, () -> String.join(",", this.getRespawnTeleportGroups()))
        ;
    }

    @Override
    public boolean load() {
        Location location = cfg.getLocation("Location");
        if (location == null) {
            this.spawnsModule.error("Invalid spawn location");
            return false;
        }
        this.setLocation(location);

        this.setName(cfg.getString("Name", this.getId()));
        this.setPermissionRequired(cfg.getBoolean("Permission_Required"));
        this.setDefault(cfg.getBoolean("Is_Default"));
        this.setPriority(cfg.getInt("Priority"));
        this.setLoginTeleportEnabled(cfg.getBoolean("Teleport_On_Login.Enabled"));
        this.setFirstLoginTeleportEnabled(cfg.getBoolean("Teleport_On_Login.For_New_Players"));
        this.setRespawnTeleportEnabled(cfg.getBoolean("Teleport_On_Death.Enabled"));
        this.loginTeleportGroups.addAll(cfg.getStringSet("Teleport_On_Login.Groups").stream()
            .map(String::toLowerCase).toList());
        this.respawnTeleportGroups.addAll(cfg.getStringSet("Teleport_On_Death.Groups").stream()
            .map(String::toLowerCase).toList());

        return true;
    }

    @Override
    @NotNull
    public PlaceholderMap getPlaceholders() {
        return this.placeholderMap;
    }

    @Override
    public void onSave() {
        cfg.set("Name", this.getName());
        cfg.set("Location", this.getLocation());
        cfg.set("Permission_Required", this.isPermissionRequired());
        cfg.set("Is_Default", this.isDefault());
        cfg.set("Priority", this.getPriority());
        cfg.set("Teleport_On_Login.Enabled", this.isLoginTeleportEnabled());
        cfg.set("Teleport_On_Login.For_New_Players", this.isFirstLoginTeleportEnabled());
        cfg.set("Teleport_On_Login.Groups", new ArrayList<>(this.getLoginTeleportGroups()));
        cfg.set("Teleport_On_Death.Enabled", this.isRespawnTeleportEnabled());
        cfg.set("Teleport_On_Death.Groups", new ArrayList<>(this.getRespawnTeleportGroups()));
    }

    @Override
    public void clear() {
        if (this.editor != null) {
            this.editor.clear();
            this.editor = null;
        }
        this.placeholderMap.clear();
    }

    @NotNull
    @Override
    public SpawnSettingsEditor getEditor() {
        if (this.editor == null) {
            this.editor = new SpawnSettingsEditor(this);
        }
        return this.editor;
    }

    public void teleport(@NotNull Player player) {
        this.teleport(player, true);
    }

    public boolean teleport(@NotNull Player player, boolean isForce) {
        if (!isForce && !this.hasPermission(player)) {
            plugin.getMessage(Lang.ERROR_PERMISSION_DENY).send(player);
            return false;
        }

        if (player.teleport(this.getLocation())) {
            this.plugin.getMessage(SpawnsLang.SPAWN_TELEPORT_DONE).replace(this.replacePlaceholders()).send(player);
            return true;
        }
        return false;
    }

    public boolean isDeathTeleportEnabled(@NotNull Player player) {
        if (!this.isRespawnTeleportEnabled()) return false;
        if (this.getRespawnTeleportGroups().contains(Placeholders.WILDCARD)) return true;

        return Hooks.getPermissionGroups(player).stream().anyMatch(this.getRespawnTeleportGroups()::contains);
    }

    public boolean isLoginTeleportEnabled(@NotNull Player player) {
        if (!this.isLoginTeleportEnabled()) return false;
        if (this.getLoginTeleportGroups().contains(Placeholders.WILDCARD)) return true;

        return Hooks.getPermissionGroups(player).stream().anyMatch(this.getLoginTeleportGroups()::contains);
    }

    public boolean hasPermission(@NotNull Player player) {
        if (!this.isPermissionRequired() || this.isDefault()) return true;
        return player.hasPermission(SpawnsPerms.SPAWN) || player.hasPermission(SpawnsPerms.PREFIX_SPAWN + this.getId());
    }

    @NotNull
    public SpawnsModule getSpawnManager() {
        return this.spawnsModule;
    }

    @NotNull
    public String getName() {
        return this.name;
    }

    public void setName(@NotNull String name) {
        this.name = Colorizer.apply(name);
    }

    @NotNull
    public Location getLocation() {
        return this.location;
    }

    public void setLocation(@NotNull Location location) {
        this.location = location;
    }

    public boolean isPermissionRequired() {
        return this.isPermission;
    }

    public void setPermissionRequired(boolean isPermission) {
        this.isPermission = isPermission;
    }

    public boolean isDefault() {
        return this.isDefault;
    }

    public void setDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }

    public int getPriority() {
        return this.priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public boolean isLoginTeleportEnabled() {
        return this.loginTeleportEnabled;
    }

    public void setLoginTeleportEnabled(boolean loginTeleportEnabled) {
        this.loginTeleportEnabled = loginTeleportEnabled;
    }

    public boolean isFirstLoginTeleportEnabled() {
        return this.firstLoginTeleportEnabled;
    }

    public void setFirstLoginTeleportEnabled(boolean firstLoginTeleportEnabled) {
        this.firstLoginTeleportEnabled = firstLoginTeleportEnabled;
    }

    @NotNull
    public Set<String> getLoginTeleportGroups() {
        return this.loginTeleportGroups;
    }

    public boolean isRespawnTeleportEnabled() {
        return this.respawnTeleportEnabled;
    }

    public void setRespawnTeleportEnabled(boolean respawnTeleportEnabled) {
        this.respawnTeleportEnabled = respawnTeleportEnabled;
    }

    @NotNull
    public Set<String> getRespawnTeleportGroups() {
        return this.respawnTeleportGroups;
    }
}