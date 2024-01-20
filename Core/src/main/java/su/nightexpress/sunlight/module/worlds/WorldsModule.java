package su.nightexpress.sunlight.module.worlds;

import org.bukkit.Difficulty;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;
import org.bukkit.generator.ChunkGenerator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nexmedia.engine.api.config.JYML;
import su.nexmedia.engine.utils.StringUtil;
import su.nexmedia.engine.utils.values.UniTask;
import su.nightexpress.sunlight.SunLight;
import su.nightexpress.sunlight.module.Module;
import su.nightexpress.sunlight.module.worlds.commands.main.WorldsCommand;
import su.nightexpress.sunlight.module.worlds.config.WorldsConfig;
import su.nightexpress.sunlight.module.worlds.config.WorldsLang;
import su.nightexpress.sunlight.module.worlds.config.WorldsPerms;
import su.nightexpress.sunlight.module.worlds.editor.EditorLocales;
import su.nightexpress.sunlight.module.worlds.editor.WorldListEditor;
import su.nightexpress.sunlight.module.worlds.impl.WorldConfig;
import su.nightexpress.sunlight.module.worlds.impl.WorldInventory;
import su.nightexpress.sunlight.module.worlds.impl.generation.FlatChunkGenerator;
import su.nightexpress.sunlight.module.worlds.impl.generation.PlainsChunkGenerator;
import su.nightexpress.sunlight.module.worlds.impl.generation.VoidChunkGenerator;
import su.nightexpress.sunlight.module.worlds.listener.InventoryListener;
import su.nightexpress.sunlight.module.worlds.listener.WorldsListener;
import su.nightexpress.sunlight.module.worlds.task.WorldWipeTask;
import su.nightexpress.sunlight.module.worlds.util.Placeholders;

import java.util.*;

public class WorldsModule extends Module {

    public static final String DIR_WORLDS = "/worlds/";
    public static final String DIR_INVENTORIES = "/inventories/";

    private final Map<String, ChunkGenerator> generatorMap;
    private final Map<String, WorldInventory> inventoryMap;
    private final Map<String, WorldConfig>    configMap;

    private WorldListEditor editor;
    private WorldWipeTask wipeTask;
    private UniTask wipeNotifyTask;

    public WorldsModule(@NotNull SunLight plugin, @NotNull String id) {
        super(plugin, id);
        this.generatorMap = new HashMap<>();
        this.inventoryMap = new HashMap<>();
        this.configMap = new HashMap<>();
    }

    @Override
    public void onLoad() {
        this.plugin.registerPermissions(WorldsPerms.class);
        this.plugin.getLangManager().loadMissing(WorldsLang.class);
        this.plugin.getLangManager().loadEditor(EditorLocales.class);
        this.plugin.getLangManager().loadEnum(Difficulty.class);
        this.plugin.getLang().saveChanges();
        this.getConfig().initializeOptions(WorldsConfig.class);

        this.getGeneratorMap().put(VoidChunkGenerator.NAME, new VoidChunkGenerator());
        this.getGeneratorMap().put(FlatChunkGenerator.NAME, new FlatChunkGenerator());
        this.getGeneratorMap().put(PlainsChunkGenerator.NAME, new PlainsChunkGenerator());

        for (JYML cfg : JYML.loadAll(this.getAbsolutePath() + DIR_WORLDS, false)) {
            WorldConfig worldConfig = new WorldConfig(this, cfg);
            if (worldConfig.load()) {
                this.getConfigsMap().put(worldConfig.getId(), worldConfig);
                if (worldConfig.isAutoLoad()) {
                    worldConfig.loadWorld();
                }
            }
            else this.error("World Config not loaded: '" + cfg.getFile().getName() + "' !");
        }

        this.plugin.getCommandRegulator().register(WorldsCommand.NAME, (cfg1, aliases) -> new WorldsCommand(this, aliases), "worldmanager");

        this.addListener(new WorldsListener(this));
        if (WorldsConfig.INVENTORY_SPLIT_ENABLED.get()) {
            this.addListener(new InventoryListener(this));
        }

        this.wipeTask = new WorldWipeTask(this);
        this.wipeTask.start();

        if (WorldsConfig.AUTO_WIPE_NOTIFICATION_ENABLED.get()) {
            this.wipeNotifyTask = UniTask.builder(this.plugin)
                .async()
                .withSeconds(1)
                .withRunnable(() -> this.getWorldConfigs().forEach(WorldConfig::autoWipeNotify))
                .buildAndRun();
        }
    }

    @Override
    public void onShutdown() {
        if (this.wipeTask != null) {
            this.wipeTask.stop();
            this.wipeTask = null;
        }
        if (this.wipeNotifyTask != null) this.wipeNotifyTask.stop();
        this.getWorldConfigs().forEach(worldConfig -> {
            worldConfig.clear();
            worldConfig.unloadWorld();
        });

        this.inventoryMap.values().forEach(WorldInventory::save);
        this.inventoryMap.clear();

        this.getWorldConfigs().clear();
        this.getGeneratorMap().clear();
    }

    @NotNull
    public WorldListEditor getEditor() {
        if (this.editor == null) {
            this.editor = new WorldListEditor(this);
        }
        return editor;
    }

    public boolean createWorldConfig(@NotNull String name) {
        name = StringUtil.lowerCaseUnderscore(name);
        if (this.getWorldById(name) != null) return false;

        JYML cfg = new JYML(this.getAbsolutePath() + DIR_WORLDS, name + ".yml");
        WorldConfig worldConfig = new WorldConfig(this, cfg);
        worldConfig.setAutoSave(true);
        worldConfig.setPVPAllowed(true);
        worldConfig.setDifficulty(Difficulty.NORMAL);
        worldConfig.save();
        worldConfig.load();
        this.getConfigsMap().put(worldConfig.getId(), worldConfig);
        return true;
    }

    public boolean deleteWorldConfig(@NotNull WorldConfig worldConfig) {
        if (worldConfig.isLoaded()) return false;
        if (worldConfig.hasData()) return false;

        if (worldConfig.getFile().delete()) {
            this.getConfigsMap().remove(worldConfig.getId());
        }
        return true;
    }

    @NotNull
    public Map<String, ChunkGenerator> getGeneratorMap() {
        return generatorMap;
    }

    @Nullable
    public ChunkGenerator getChunkGenerator(@Nullable String id) {
        if (id == null) return null;

        return this.getGeneratorMap().get(id.toLowerCase());
    }

    @Nullable
    public ChunkGenerator getPluginGenerator(@NotNull String world, @Nullable String name) {
        if (name == null || name.equalsIgnoreCase(Placeholders.DEFAULT)) return null;

        ChunkGenerator generator = this.getChunkGenerator(name);
        if (generator != null) return generator;

        return WorldCreator.getGeneratorForName(world, name, null);
    }

    @NotNull
    public Map<String, WorldConfig> getConfigsMap() {
        return this.configMap;
    }

    @NotNull
    public Collection<WorldConfig> getWorldConfigs() {
        return this.getConfigsMap().values();
    }

    @Nullable
    public WorldConfig getWorldById(@NotNull String id) {
        return this.getConfigsMap().get(id.toLowerCase());
    }

    public boolean isCustomWorld(@NotNull World world) {
        return this.getWorldById(world.getName()) != null;
    }

    @NotNull
    public Map<String, WorldInventory> getInventoryMap() {
        return inventoryMap;
    }

    @NotNull
    public WorldInventory getWorldInventory(@NotNull Player player) {
        String id = player.getUniqueId().toString();
        if (this.inventoryMap.containsKey(id)) {
            return this.inventoryMap.get(id);
        }

        JYML file = new JYML(this.getAbsolutePath() + DIR_INVENTORIES, id + ".yml");
        WorldInventory worldInventory = new WorldInventory(this, file);
        worldInventory.load();

        this.inventoryMap.put(worldInventory.getId(), worldInventory);
        return worldInventory;
    }

    public boolean isInventoryAffected(@NotNull Player player) {
        return this.isInventoryAffected(player.getWorld());
    }

    public boolean isInventoryAffected(@NotNull World world) {
        return this.getWorldGroup(world) != null;
    }

    @Nullable
    public String getWorldGroup(@NotNull World world) {
        String worldName = world.getName();

        return WorldsConfig.INVENTORY_SPLIT_WORLD_GROUPS.get().entrySet().stream()
            .filter(entry -> entry.getValue().contains(worldName))
            .map(Map.Entry::getKey).findFirst().orElse(null);
    }
}
