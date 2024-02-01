package su.nightexpress.sunlight.module.worlds.impl;

import org.bukkit.Difficulty;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;
import org.bukkit.entity.SpawnCategory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nexmedia.engine.api.config.JYML;
import su.nexmedia.engine.api.manager.AbstractConfigHolder;
import su.nexmedia.engine.api.placeholder.Placeholder;
import su.nexmedia.engine.api.placeholder.PlaceholderMap;
import su.nexmedia.engine.lang.LangManager;
import su.nexmedia.engine.utils.*;
import su.nightexpress.sunlight.SunLight;
import su.nightexpress.sunlight.config.Config;
import su.nightexpress.sunlight.config.Lang;
import su.nightexpress.sunlight.module.spawns.SpawnsModule;
import su.nightexpress.sunlight.module.spawns.impl.Spawn;
import su.nightexpress.sunlight.module.worlds.WorldsModule;
import su.nightexpress.sunlight.module.worlds.config.WorldsConfig;
import su.nightexpress.sunlight.module.worlds.config.WorldsLang;
import su.nightexpress.sunlight.module.worlds.editor.WorldMainEditor;
import su.nightexpress.sunlight.module.worlds.editor.WorldRulesEditor;
import su.nightexpress.sunlight.module.worlds.util.Placeholders;

import java.io.File;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class WorldConfig extends AbstractConfigHolder<SunLight> implements Placeholder {

    private final WorldsModule module;
    private final WorldCreator creator;
    private final PlaceholderMap placeholderMap;

    private boolean                     autoLoad;
    private boolean                     autoSave;
    private boolean                     pvpAllowed;
    private String                      generator;
    private Difficulty                  difficulty;
    private World.Environment environment;

    private boolean autoWipe;
    private int wipeInterval;
    private long lastWipe;

    private final Map<SpawnCategory, Integer> spawnLimits;
    private final Map<SpawnCategory, Integer> ticksPerSpawns;

    private WorldMainEditor editor;
    private WorldRulesEditor rulesEditor;

    public WorldConfig(@NotNull WorldsModule module, @NotNull JYML cfg) {
        super(module.plugin(), cfg);
        this.module = module;
        this.creator = new WorldCreator(this.getId());
        this.spawnLimits = new HashMap<>();
        this.ticksPerSpawns = new HashMap<>();
        this.placeholderMap = new PlaceholderMap()
            .add(Placeholders.WORLD_ID, this::getId)
            .add(Placeholders.WORLD_IS_LOADED, () -> LangManager.getBoolean(this.isLoaded()))
            .add(Placeholders.WORLD_IS_CREATED, () -> LangManager.getBoolean(this.hasData()))
            .add(Placeholders.WORLD_AUTO_LOAD, () -> LangManager.getBoolean(this.isAutoLoad()))
            .add(Placeholders.WORLD_AUTO_SAVE, () -> LangManager.getBoolean(this.isAutoSave()))
            .add(Placeholders.WORLD_PVP_ALLOWED, () -> LangManager.getBoolean(this.isPVPAllowed()))
            .add(Placeholders.WORLD_GENERATOR, () -> this.getGenerator() == null ? Placeholders.DEFAULT : this.getGenerator())
            .add(Placeholders.WORLD_ENVIRONMENT, () -> StringUtil.capitalizeUnderscored(this.getEnvironment().name()))
            .add(Placeholders.WORLD_DIFFICULTY, () -> plugin.getLangManager().getEnum(this.getDifficulty()))
            .add(Placeholders.WORLD_STRUCTURES, () -> LangManager.getBoolean(this.getCreator().generateStructures()))
            .add(Placeholders.WORLD_SPAWN_LIMITS, () -> {
                return this.getSpawnLimits().entrySet().stream().map(e -> Colorizer.apply(Colors.GRAY + StringUtil.capitalizeUnderscored(e.getKey().name()) + ": " + Colors.YELLOW + e.getValue().toString())).collect(Collectors.joining("\n"));
            })
            .add(Placeholders.WORLD_SPAWN_TICKS, () -> {
                return this.getTicksPerSpawns().entrySet().stream().map(e -> Colorizer.apply(Colors.GRAY + StringUtil.capitalizeUnderscored(e.getKey().name()) + ": " + Colors.YELLOW + e.getValue().toString())).collect(Collectors.joining("\n"));
            })
            .add(Placeholders.WORLD_AUTO_WIPE_ENABLED, () -> LangManager.getBoolean(this.isAutoWipe()))
            .add(Placeholders.WORLD_AUTO_WIPE_INTERVAL, () -> TimeUtil.formatTime(this.getWipeInterval() * 1000L))
            .add(Placeholders.WORLD_AUTO_WIPE_LAST_WIPE, () -> {
                return this.getLastWipe() <= 0L ? LangManager.getPlain(Lang.OTHER_NEVER) : Config.GENERAL_DATE_FORMAT.get().format(this.getLastWipe());
            })
            .add(Placeholders.WORLD_AUTO_WIPE_NEXT_WIPE, () -> {
                return this.getNextWipe() <= 0L ? LangManager.getPlain(Lang.OTHER_NEVER) : Config.GENERAL_DATE_FORMAT.get().format(this.getNextWipe());
            })
        ;
    }

    @Override
    public boolean load() {
        this.setAutoLoad(cfg.getBoolean("Auto_Load"));
        this.setAutoSave(cfg.getBoolean("Auto_Save"));
        this.setPVPAllowed(cfg.getBoolean("PVP_Allowed"));
        this.setGenerator(cfg.getString("Generator"));
        this.setDifficulty(cfg.getEnum("Difficulty", Difficulty.class, Difficulty.NORMAL));
        this.setEnvironment(cfg.getEnum("Environment", World.Environment.class));
        for (SpawnCategory spawnCategory : SpawnCategory.values()) {
            if (spawnCategory == SpawnCategory.MISC) continue;

            int defLimit = -1;
            int defTick = spawnCategory == SpawnCategory.ANIMAL ? 400 : 1;

            this.spawnLimits.put(spawnCategory, cfg.getInt("SpawnLimits." + spawnCategory.name(), defLimit));
            this.ticksPerSpawns.put(spawnCategory, cfg.getInt("TicksPerSpawn." + spawnCategory.name(), defTick));
        }
        this.setAutoWipe(cfg.getBoolean("Auto_Wipe.Enabled"));
        this.setWipeInterval(cfg.getInt("Auto_Wipe.Interval"));
        this.setLastWipe(cfg.getLong("Auto_Wipe.Last_Wipe"));

        //this.setGenerationType(cfg.getEnum("Generation.Type", GenerationType.class, GenerationType.DEFAULT));
        //this.setEnvironment(cfg.getEnum("Environment", Environment.class, Environment.NORMAL));
        //this.setStructuresEnabled(cfg.getBoolean("Structures_Enabled"));
        return true;
    }

    @Override
    public void onSave() {
        cfg.set("Auto_Load", this.isAutoLoad());
        cfg.set("Auto_Save", this.isAutoSave());
        cfg.set("PVP_Allowed", this.isPVPAllowed());
        cfg.set("Generator", this.getGenerator());
        cfg.set("Difficulty", this.getDifficulty().name());
        cfg.set("Environment", this.getEnvironment().name());
        this.getSpawnLimits().forEach((cat, lim) -> cfg.set("SpawnLimits." + cat.name(), lim));
        this.getTicksPerSpawns().forEach((cat, tick) -> cfg.set("TicksPerSpawn." + cat.name(), tick));
        cfg.set("Auto_Wipe.Enabled", this.isAutoWipe());
        cfg.set("Auto_Wipe.Interval", this.getWipeInterval());
        cfg.set("Auto_Wipe.Last_Wipe", this.getLastWipe());

        //cfg.set("Generation.Type", this.getGenerationType().name());
        //cfg.set("Environment", this.getEnvironment().name());
        //cfg.set("Structures_Enabled", this.isStructuresEnabled());

        this.applyWorldSettings();
    }

    @Override
    @NotNull
    public PlaceholderMap getPlaceholders() {
        return this.placeholderMap;
    }

    public void clear() {
        if (this.rulesEditor != null) {
            this.rulesEditor.clear();
            this.rulesEditor = null;
        }
        if (this.editor != null) {
            this.editor.clear();
            this.editor = null;
        }
    }

    @NotNull
    public WorldMainEditor getEditor() {
        if (this.editor == null) {
            this.editor = new WorldMainEditor(this.module, this);
        }
        return editor;
    }

    @NotNull
    public WorldRulesEditor getRulesEditor() {
        if (this.rulesEditor == null) {
            this.rulesEditor = new WorldRulesEditor(this.plugin, this);
        }
        return rulesEditor;
    }

    public void applyWorldSettings() {
        World world = this.getWorld();
        if (world == null) return;

        world.setAutoSave(this.isAutoSave());
        world.setDifficulty(this.getDifficulty());
        world.setPVP(this.isPVPAllowed());
        this.getSpawnLimits().forEach(world::setSpawnLimit);
        this.getTicksPerSpawns().forEach(world::setTicksPerSpawns);
    }

    public boolean loadWorld() {
        if (this.isLoaded()) return false;

        this.getCreator().environment(this.getEnvironment());
        this.getCreator().generator(this.module.getPluginGenerator(this.getId(), this.getGenerator()));
        if (this.getCreator().createWorld() == null) return false;

        this.applyWorldSettings();
        return true;
    }

    public boolean deleteWorldFiles() {
        if (this.isLoaded()) return false;

        File dir = new File(plugin.getServer().getWorldContainer() + "/" + this.getId());

        return FileUtil.deleteRecursive(dir); // Delete bukkit world folder.
    }

    public boolean deleteRegionFiles() {
        if (this.isLoaded()) return false;

        File dir = new File(plugin.getServer().getWorldContainer() + "/" + this.getId());

        for (File folder : FileUtil.getFolders(dir.getAbsolutePath())) {
            if (folder.getName().equalsIgnoreCase("datapacks")) continue;

            FileUtil.deleteRecursive(folder);
        }

        for (File file : FileUtil.getFiles(dir.getAbsolutePath(), false)) {
            if (file.getName().equalsIgnoreCase("level.dat")) continue;
            if (file.getName().equalsIgnoreCase("paper-world.yml")) continue;

            file.delete();
        }

        return true;
    }

    public boolean deleteWorld(boolean withFolder) {
        if (this.isLoaded()) return false;

        if (withFolder) {
            if (!this.deleteWorldFiles()) {
                return false;
            }
        }

        this.module.deleteWorldConfig(this);
        return true;
    }

    public boolean unloadWorld() {
        World world = this.getWorld();
        if (world == null) return false;

        /*Location defSpawn = null;
        SpawnsModule spawnsModule = this.plugin.getModuleManager().getModule(SpawnsModule.class).orElse(null);
        if (spawnsModule != null) {
            Spawn spawn = spawnsModule.getSpawnByDefault().orElse(null);
            if (spawn != null) {
                defSpawn = spawn.getLocation();
            }
        }
        if (defSpawn == null) {
            defSpawn = this.plugin.getServer().getWorlds().get(0).getSpawnLocation();
        }

        for (Player player : world.getPlayers()) {
            player.teleport(defSpawn);
        }*/

        return this.plugin.getServer().unloadWorld(world, true);
    }

    public boolean autoWipe() {
        if (!this.isAutoWipe()) return false;
        if (!this.isWipeTime()) return false;

        this.module.info("Start Auto-Wipe for world '" + this.getId() + "'...");
        if (!this.movePlayersOut()) {
            this.module.warn("Auto-Wipe for world '" + this.getId() + "' failed: Unable to move players out of the world.");
            return false;
        }
        this.unloadWorld();
        this.deleteRegionFiles();
        this.loadWorld();
        this.setLatestWipeDate();
        //this.setLastWipe(System.currentTimeMillis());
        this.module.info("Auto-Wipe for world '" + this.getId() + "' completed.");
        this.save();
        return true;
    }

    public boolean autoWipeNotify() {
        if (!this.isAutoWipe()) return false;
        if (this.isWipeTime()) return false;

        long wipeDate = this.getNextWipe();
        if (wipeDate <= 0L) return false;

        long current = TimeUtil.toEpochMillis(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        long threshold = WorldsConfig.AUTO_WIPE_NOTIFICATION_THRESHOLD.get() * 1000L;
        long diff = wipeDate - current;
        if (diff > threshold) return false;

        if (TimeUnit.MILLISECONDS.toSeconds(diff) % WorldsConfig.AUTO_WIPE_NOTIFICATION_INTERVAL.get() == 0L) {
            World world = this.getWorld();
            if (world == null) return false;

            world.getPlayers().forEach(player -> {
                plugin.getMessage(WorldsLang.AUTOWIPE_NOTIFY)
                    .replace(Placeholders.GENERIC_TIME, TimeUtil.formatTimeLeft(this.getNextWipe() + 1000L)) // add 1 second for good formatiing
                    .send(player);
            });
            return true;
        }

        return false;
    }

    public boolean movePlayersOut() {
        World world = this.getWorld();
        if (world == null) return false;

        Location location = null;
        if (WorldsConfig.AUTO_WIPE_MOVE_PLAYERS_OUT_TO_SPAWN.get()) {
            SpawnsModule spawnsModule = this.plugin.getModuleManager().getModule(SpawnsModule.class).orElse(null);
            Spawn spawn = spawnsModule == null ? null : spawnsModule.getSpawnById(WorldsConfig.AUTO_WIPE_MOVE_PLAYERS_OUT_SPAWN_NAME.get()).orElse(null);
            if (spawn != null) {
                location = spawn.getLocation();
            }
        }
        if (location == null) {
            World target = this.plugin.getServer().getWorlds().stream().filter(w -> w != world).findFirst().orElse(null);
            if (target == null) return false;

            location = target.getSpawnLocation();
        }

        for (Player player : world.getPlayers()) {
            if (player.teleport(location)) {
                this.plugin.getMessage(WorldsLang.AUTOWIPE_MOVE_OUT).send(player);
            }
        }

        return world.getPlayers().isEmpty();
    }

    @Nullable
    public World getWorld() {
        return this.plugin.getServer().getWorld(this.getId());
    }

    public boolean isLoaded() {
        return this.getWorld() != null;
    }

    public boolean hasData() {
        File dir = new File(plugin.getServer().getWorldContainer() + "/" + this.getId());
        return dir.exists() && dir.listFiles() != null;
    }

    public long getNextWipe() {
        if (!this.isAutoWipe()) return -1L;
        if (this.getLastWipe() <= 0L || this.getWipeInterval() <= 0L) return -1L;

        return this.getLastWipe() + (this.getWipeInterval() * 1000L);
    }

    public boolean isWipeTime() {
        if (!this.isAutoWipe()) return false;

        long next = this.getNextWipe();
        if (next <= 0L) return false;

        long now = TimeUtil.toEpochMillis(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        return now >= next;
    }

    @NotNull
    public WorldCreator getCreator() {
        return creator;
    }

    public boolean isAutoLoad() {
        return this.autoLoad;
    }

    public void setAutoLoad(boolean autoLoad) {
        this.autoLoad = autoLoad;
    }

    public boolean isAutoSave() {
        return autoSave;
    }

    public void setAutoSave(boolean autoSave) {
        this.autoSave = autoSave;
    }

    public boolean isPVPAllowed() {
        return pvpAllowed;
    }

    public void setPVPAllowed(boolean pvpAllowed) {
        this.pvpAllowed = pvpAllowed;
    }

    @Nullable
    public String getGenerator() {
        return generator;
    }

    public void setGenerator(@Nullable String generator) {
        this.generator = generator;
    }

    @NotNull
    public Difficulty getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(@NotNull Difficulty difficulty) {
        this.difficulty = difficulty;
    }

    @NotNull
    public World.Environment getEnvironment() {
        return environment == null ? World.Environment.NORMAL : this.environment;
    }

    public void setEnvironment(@Nullable World.Environment environment) {
        this.environment = environment == null ? World.Environment.NORMAL : environment;
    }

    @NotNull
    public Map<SpawnCategory, Integer> getSpawnLimits() {
        return spawnLimits;
    }

    @NotNull
    public Map<SpawnCategory, Integer> getTicksPerSpawns() {
        return ticksPerSpawns;
    }

    public boolean isAutoWipe() {
        return autoWipe;
    }

    public void setAutoWipe(boolean autoWipe) {
        this.autoWipe = autoWipe;
    }

    public int getWipeInterval() {
        return wipeInterval;
    }

    public void setWipeInterval(int wipeInterval) {
        this.wipeInterval = wipeInterval;
    }

    public long getLastWipe() {
        return lastWipe;
    }

    public void setLatestWipeDate() {
        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        this.setLastWipe(TimeUtil.toEpochMillis(now));
    }

    public void setLastWipe(long lastWipe) {
        this.lastWipe = lastWipe;
    }
}
