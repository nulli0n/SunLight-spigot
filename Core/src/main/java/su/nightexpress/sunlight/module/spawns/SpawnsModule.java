package su.nightexpress.sunlight.module.spawns;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.nightcore.util.FileUtil;
import su.nightexpress.nightcore.util.StringUtil;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.module.Module;
import su.nightexpress.sunlight.module.ModuleInfo;
import su.nightexpress.sunlight.module.spawns.command.SpawnCommands;
import su.nightexpress.sunlight.module.spawns.config.SpawnsConfig;
import su.nightexpress.sunlight.module.spawns.config.SpawnsLang;
import su.nightexpress.sunlight.module.spawns.config.SpawnsPerms;
import su.nightexpress.sunlight.module.spawns.editor.SpawnListEditor;
import su.nightexpress.sunlight.module.spawns.editor.SpawnSettingsEditor;
import su.nightexpress.sunlight.module.spawns.impl.Spawn;
import su.nightexpress.sunlight.module.spawns.listener.SpawnListener;

import java.io.File;
import java.util.*;

public class SpawnsModule extends Module {

    public static final String DIR_SPAWNS = "/spawns/";

    private final Map<String, Spawn> spawnMap;

    private SpawnListEditor     listEditor;
    private SpawnSettingsEditor settingsEditor;

    public SpawnsModule(@NotNull SunLightPlugin plugin, @NotNull String id) {
        super(plugin, id);
        this.spawnMap = new HashMap<>();
    }

    @Override
    protected void gatherInfo(@NotNull ModuleInfo moduleInfo) {
        moduleInfo.setConfigClass(SpawnsConfig.class);
        moduleInfo.setLangClass(SpawnsLang.class);
        moduleInfo.setPermissionsClass(SpawnsPerms.class);
    }

    @Override
    protected void onModuleLoad() {
        this.registerCommands();
        this.loadSpawns();

        this.listEditor = new SpawnListEditor(this.plugin, this);
        this.settingsEditor = new SpawnSettingsEditor(this.plugin, this);

        this.addListener(new SpawnListener(this.plugin, this));
    }

    @Override
    protected void onModuleUnload() {
        if (this.listEditor != null) this.listEditor.clear();
        if (this.settingsEditor != null) this.settingsEditor.clear();

        this.spawnMap.clear();
    }

    private void registerCommands() {
        SpawnCommands.load(this.plugin, this);
    }

    private void loadSpawns() {
        for (File file : FileUtil.getFiles(this.getAbsolutePath() + DIR_SPAWNS, false)) {
            Spawn spawn = new Spawn(this.plugin, this, file);
            this.loadSpawn(spawn);
        }
        this.info("Loaded " + this.spawnMap.size() + " spawns!");
    }

    private void loadSpawn(@NotNull Spawn spawn) {
        if (spawn.load()) {
            this.spawnMap.put(spawn.getId(), spawn);
        }
        else this.warn("Spawn not loaded: '" + spawn.getFile().getName() + "'!");
    }

    public void openEditor(@NotNull Player player) {
        this.listEditor.open(player, this);
    }

    public void openSpawnSettings(@NotNull Player player, @NotNull Spawn spawn) {
        this.settingsEditor.open(player, spawn);
    }

    @NotNull
    public Map<String, Spawn> getSpawnMap() {
        return this.spawnMap;
    }

    @NotNull
    public Collection<Spawn> getSpawns() {
        return this.spawnMap.values();
    }

    @NotNull
    public List<String> getSpawnIds() {
        return new ArrayList<>(this.spawnMap.keySet());
    }

    @Nullable
    public Spawn getSpawn(@NotNull String id) {
        return this.spawnMap.get(id.toLowerCase());
    }

    @Nullable
    public Spawn getDefaultSpawn() {
        return this.getSpawn(SpawnsConfig.DEFAULT_SPAWN.get());
        //return this.getSpawns().stream().filter(Spawn::isDefault).findFirst().orElse(null);
    }

    @Nullable
    public Spawn getSpawnOrDefault(@NotNull String id) {
        return this.spawnMap.getOrDefault(id.toLowerCase(), this.getDefaultSpawn());
    }

    @Nullable
    public Spawn getNewbieSpawn(@NotNull Player player) {
        return this.getSpawn(SpawnsConfig.NEWBIE_TELEPORT_TARGET.get());
    }

    @Nullable
    public Spawn getLoginSpawn(@NotNull Player player) {
        return this.getSpawns().stream().filter(spawn -> spawn.isLoginSpawn(player)).max(Comparator.comparingInt(Spawn::getPriority)).orElse(null);
    }

    @Nullable
    public Spawn getDeathSpawn(@NotNull Player player) {
        return this.getSpawns().stream().filter(spawn -> spawn.isDeathSpawn(player)).max(Comparator.comparingInt(Spawn::getPriority)).orElse(null);
    }

    public boolean createSpawn(@NotNull Player player, @NotNull String id) {
        Location location = player.getLocation();
        id = StringUtil.lowerCaseUnderscore(id);

        Spawn spawn = this.getSpawn(id);
        if (spawn == null) {
            File file = new File(this.getAbsolutePath() + DIR_SPAWNS, id + ".yml");
            spawn = new Spawn(this.plugin, this, file);
            spawn.setName(StringUtil.capitalizeUnderscored(id));
        }
        spawn.setLocation(location);
        spawn.save();

        this.loadSpawn(spawn);
        SpawnsLang.COMMAND_SET_SPAWN_DONE.getMessage().replace(spawn.replacePlaceholders()).send(player);
        return true;
    }

    public boolean deleteSpawn(@NotNull Spawn spawn) {
        if (spawn.getFile().delete()) {
            this.spawnMap.remove(spawn.getId());
            return true;
        }
        return false;
    }
}
