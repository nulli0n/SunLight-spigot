package su.nightexpress.sunlight.module.worlds.impl;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.manager.AbstractFileData;
import su.nightexpress.nightcore.util.FileUtil;
import su.nightexpress.nightcore.util.TimeUtil;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.module.spawns.SpawnsModule;
import su.nightexpress.sunlight.module.spawns.impl.Spawn;
import su.nightexpress.sunlight.module.worlds.WorldsModule;
import su.nightexpress.sunlight.module.worlds.config.WorldsConfig;
import su.nightexpress.sunlight.module.worlds.config.WorldsLang;
import su.nightexpress.sunlight.module.worlds.util.DeletionType;
import su.nightexpress.sunlight.module.worlds.util.Placeholders;
import su.nightexpress.sunlight.utils.Teleporter;

import java.io.File;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

public class WorldData extends AbstractFileData<SunLightPlugin> {

    private final WorldsModule module;

    private boolean           autoLoad;
    private String            generator;
    private World.Environment environment;
    private boolean           generateStructures;

    private boolean autoReset;
    private int  resetInterval;
    private long lastResetDate;

    public WorldData(@NotNull SunLightPlugin plugin, @NotNull WorldsModule module, @NotNull File file) {
        super(plugin, file);
        this.module = module;
    }

    @Override
    protected boolean onLoad(@NotNull FileConfig config) {
        this.setAutoLoad(config.getBoolean("Auto_Load"));
        this.setGenerator(config.getString("Generator"));
        this.setEnvironment(config.getEnum("Environment", World.Environment.class));
        this.setGenerateStructures(config.getBoolean("GenerateStructures"));
        this.setAutoReset(config.getBoolean("Auto_Wipe.Enabled"));
        this.setResetInterval(config.getInt("Auto_Wipe.Interval"));
        this.setLastResetDate(config.getLong("Auto_Wipe.Last_Wipe"));
        return true;
    }

    @Override
    protected void onSave(@NotNull FileConfig config) {
        config.set("Auto_Load", this.isAutoLoad());
        config.set("Generator", this.getGenerator());
        config.set("Environment", this.getEnvironment().name());
        config.set("GenerateStructures", this.isGenerateStructures());
        config.set("Auto_Wipe.Enabled", this.isAutoReset());
        config.set("Auto_Wipe.Interval", this.getResetInterval());
        config.set("Auto_Wipe.Last_Wipe", this.getLastResetDate());
    }

    @Nullable
    public World loadWorld() {
        if (this.isLoaded()) return this.getWorld();

        WorldCreator creator = new WorldCreator(this.getId());
        creator.environment(this.getEnvironment());
        creator.generator(this.module.getPluginGenerator(this.getId(), this.getGenerator()));
        return creator.createWorld();
    }

    public boolean hasWorldFiles() {
        return this.getDirectory() != null;
    }

    @Nullable
    public File getDirectory() {
        File dir = new File(plugin.getServer().getWorldContainer() + "/" + this.getId());
        return dir.exists() && dir.listFiles() != null ? dir : null;
    }

    private boolean deleteWorldFiles() {
        //if (this.isLoaded()) return false;

        File dir = this.getDirectory();
        if (dir == null) return false;

        return FileUtil.deleteRecursive(dir); // Delete bukkit world folder.
    }

    private boolean deleteRegionFiles() {
        //if (this.isLoaded()) return false;

        File dir = this.getDirectory();
        if (dir == null) return false;

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

    public boolean delete(@NotNull DeletionType type) {
        if (this.isLoaded()) {
            if (!this.unloadWorld()) return false;
        }

        if (type == DeletionType.DATA || type == DeletionType.FULL) {
            if (!this.getFile().delete()) return false;

            this.module.getDataMap().remove(this.getId());
        }

        if (type == DeletionType.REGION) {
            this.deleteRegionFiles();
        }
        else if (type == DeletionType.DIRECTORY || type == DeletionType.FULL) {
            this.deleteWorldFiles();
        }

        return true;
    }

    public boolean unloadWorld() {
        World world = this.getWorld();
        if (world == null) return false;

        this.movePlayersOut();

        return this.plugin.getServer().unloadWorld(world, true);
    }

    public boolean autoReset() {
        if (!this.isAutoReset()) return false;
        if (!this.isResetTime()) return false;

        this.module.info("Start Auto-Reset for world '" + this.getId() + "'...");

        if (!this.unloadWorld()) {
            this.module.warn("Auto-Reset for world '" + this.getId() + "' failed: Unable to unload the world.");
            return false;
        }

        this.deleteRegionFiles();
        this.loadWorld();
        this.setLatestWipeDate();
        this.module.info("Auto-Reset for world '" + this.getId() + "' completed.");
        this.save();
        return true;
    }

    public boolean autoResetNotify() {
        if (!this.isAutoReset()) return false;
        if (this.isResetTime()) return false;

        long wipeDate = this.getNextWipe();
        if (wipeDate <= 0L) return false;

        long current = TimeUtil.toEpochMillis(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        long threshold = WorldsConfig.AUTO_RESET_NOTIFICATION_THRESHOLD.get() * 1000L;
        long diff = wipeDate - current;
        if (diff > threshold) return false;

        if (TimeUnit.MILLISECONDS.toSeconds(diff) % WorldsConfig.AUTO_RESET_NOTIFICATION_INTERVAL.get() == 0L) {
            World world = this.getWorld();
            if (world == null) return false;

            world.getPlayers().forEach(player -> {
                WorldsLang.AUTO_RESET_NOTIFY.getMessage()
                    .replace(Placeholders.GENERIC_TIME, TimeUtil.formatDuration(this.getNextWipe() + 1000L)) // add 1 second for good formatiing
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
        if (WorldsConfig.UNLOAD_MOVE_PLAYERS_TO_SPAWN_ENABLED.get()) {
            SpawnsModule spawnsModule = this.plugin.getModuleManager().getModule(SpawnsModule.class).orElse(null);
            Spawn spawn = spawnsModule == null ? null : spawnsModule.getSpawn(WorldsConfig.UNLOAD_MOVE_PLAYERS_TO_SPAWN_NAME.get());
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
            Teleporter teleporter = new Teleporter(player, location).centered();

            if (teleporter.teleport()) {
                WorldsLang.UNLOAD_MOVE_OUT_INFO.getMessage().send(player);
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

    public long getNextWipe() {
        if (!this.isAutoReset()) return -1L;
        if (this.getLastResetDate() <= 0L || this.getResetInterval() <= 0L) return -1L;

        return this.getLastResetDate() + (this.getResetInterval() * 1000L);
    }

    public boolean isResetTime() {
        if (!this.isAutoReset()) return false;

        long next = this.getNextWipe();
        if (next <= 0L) return false;

        long now = TimeUtil.toEpochMillis(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        return now >= next;
    }

    public boolean isAutoLoad() {
        return this.autoLoad;
    }

    public void setAutoLoad(boolean autoLoad) {
        this.autoLoad = autoLoad;
    }

    @Nullable
    public String getGenerator() {
        return generator;
    }

    public void setGenerator(@Nullable String generator) {
        this.generator = generator;
    }

    @NotNull
    public World.Environment getEnvironment() {
        return environment == null ? World.Environment.NORMAL : this.environment;
    }

    public void setEnvironment(@Nullable World.Environment environment) {
        this.environment = environment == null ? World.Environment.NORMAL : environment;
    }

    public boolean isGenerateStructures() {
        return generateStructures;
    }

    public void setGenerateStructures(boolean generateStructures) {
        this.generateStructures = generateStructures;
    }

    public boolean isAutoReset() {
        return autoReset;
    }

    public void setAutoReset(boolean autoReset) {
        this.autoReset = autoReset;
    }

    public int getResetInterval() {
        return resetInterval;
    }

    public void setResetInterval(int resetInterval) {
        this.resetInterval = resetInterval;
    }

    public long getLastResetDate() {
        return lastResetDate;
    }

    public void setLatestWipeDate() {
        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        this.setLastResetDate(TimeUtil.toEpochMillis(now));
    }

    public void setLastResetDate(long lastResetDate) {
        this.lastResetDate = lastResetDate;
    }
}
