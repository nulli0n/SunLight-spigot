package su.nightexpress.sunlight.module.worlds;

import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;
import org.bukkit.generator.ChunkGenerator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.nightcore.util.FileUtil;
import su.nightexpress.nightcore.util.StringUtil;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.module.Module;
import su.nightexpress.sunlight.module.ModuleInfo;
import su.nightexpress.sunlight.module.worlds.command.WorldCommands;
import su.nightexpress.sunlight.module.worlds.config.WorldsConfig;
import su.nightexpress.sunlight.module.worlds.config.WorldsLang;
import su.nightexpress.sunlight.module.worlds.config.WorldsPerms;
import su.nightexpress.sunlight.module.worlds.editor.*;
import su.nightexpress.sunlight.module.worlds.impl.WorldData;
import su.nightexpress.sunlight.module.worlds.impl.WorldInventories;
import su.nightexpress.sunlight.module.worlds.impl.WrappedWorld;
import su.nightexpress.sunlight.module.worlds.impl.generation.FlatChunkGenerator;
import su.nightexpress.sunlight.module.worlds.impl.generation.PlainsChunkGenerator;
import su.nightexpress.sunlight.module.worlds.impl.generation.VoidChunkGenerator;
import su.nightexpress.sunlight.module.worlds.listener.InventoryListener;
import su.nightexpress.sunlight.module.worlds.listener.WorldsListener;
import su.nightexpress.sunlight.module.worlds.util.Placeholders;

import java.io.File;
import java.util.*;

public class WorldsModule extends Module {

    public static final String DIR_WORLDS      = "/worlds/";
    public static final String DIR_INVENTORIES = "/inventories/";

    private final Map<String, ChunkGenerator>   generatorMap;
    private final Map<String, WorldInventories> inventoryMap;
    private final Map<String, WorldData>        dataMap;

    private WorldListEditor       listEditor;
    private WorldMainEditor       mainEditor;
    private WorldRulesEditor      rulesEditor;
    private WorldGenerationEditor generationEditor;

    public WorldsModule(@NotNull SunLightPlugin plugin, @NotNull String id) {
        super(plugin, id);
        this.generatorMap = new HashMap<>();
        this.inventoryMap = new HashMap<>();
        this.dataMap = new HashMap<>();
    }

    @Override
    protected void gatherInfo(@NotNull ModuleInfo moduleInfo) {
        moduleInfo.setConfigClass(WorldsConfig.class);
        moduleInfo.setLangClass(WorldsLang.class);
        moduleInfo.setPermissionsClass(WorldsPerms.class);
    }

    @Override
    protected void onModuleLoad() {
        this.registerCommands();
        this.loadGenerators();
        this.loadWorlds();
        this.loadEditor();

        this.addListener(new WorldsListener(this.plugin, this));
        if (WorldsConfig.INVENTORY_SPLIT_ENABLED.get()) {
            this.addListener(new InventoryListener(this.plugin, this));
        }

        this.addTask(this.plugin.createTask(this::tickAutoReset).setSecondsInterval(60));

        if (WorldsConfig.AUTO_RESET_NOTIFICATION_ENABLED.get()) {
            this.addTask(this.plugin.createAsyncTask(this::notifyAutoReset).setSecondsInterval(1));
        }
    }

    @Override
    protected void onModuleUnload() {
        if (this.listEditor != null) this.listEditor.clear();
        if (this.mainEditor != null) this.mainEditor.clear();
        if (this.rulesEditor != null) this.rulesEditor.clear();
        if (this.generationEditor != null) this.generationEditor.clear();

        this.getDatas().forEach(WorldData::unloadWorld);

        this.inventoryMap.values().forEach(WorldInventories::save);
        this.inventoryMap.clear();

        this.dataMap.clear();
        this.generatorMap.clear();
    }

    private void registerCommands() {
        WorldCommands.load(this.plugin, this);
    }

    private void loadGenerators() {
        this.generatorMap.put(VoidChunkGenerator.NAME, new VoidChunkGenerator());
        this.generatorMap.put(FlatChunkGenerator.NAME, new FlatChunkGenerator());
        this.generatorMap.put(PlainsChunkGenerator.NAME, new PlainsChunkGenerator());
    }

    private void loadWorlds() {
        for (File file : FileUtil.getFiles(this.getAbsolutePath() + DIR_WORLDS, false)) {
            WorldData worldData = new WorldData(this.plugin, this, file);
            if (worldData.load()) {
                this.dataMap.put(worldData.getId(), worldData);
                if (worldData.isAutoLoad()) {
                    worldData.loadWorld();
                }
            }
            else this.error("World data not loaded: '" + worldData.getFile().getName() + "'!");
        }
    }

    private void loadEditor() {
        this.listEditor = new WorldListEditor(this.plugin, this);
        this.mainEditor = new WorldMainEditor(this.plugin, this);
        this.rulesEditor = new WorldRulesEditor(this.plugin, this);
        this.generationEditor = new WorldGenerationEditor(this.plugin, this);
    }

    public void tickAutoReset() {
        this.getDatas().forEach(WorldData::autoReset);
    }

    public void notifyAutoReset() {
        this.getDatas().forEach(WorldData::autoResetNotify);
    }

    @NotNull
    public Map<String, WorldData> getDataMap() {
        return this.dataMap;
    }

    @NotNull
    public Collection<WorldData> getDatas() {
        return this.dataMap.values();
    }

    @Nullable
    public WorldData getWorldData(@NotNull String id) {
        return this.dataMap.get(id.toLowerCase());
    }

    @NotNull
    public Map<String, WorldInventories> getInventoryMap() {
        return inventoryMap;
    }

    @NotNull
    public Map<String, ChunkGenerator> getGeneratorMap() {
        return generatorMap;
    }

    @Nullable
    public WorldData createWorldData(@NotNull String name) {
        name = StringUtil.lowerCaseUnderscore(name);
        if (this.getWorldData(name) != null) return null;
        if (this.plugin.getServer().getWorld(name) != null) return null;

        File file = new File(this.getAbsolutePath() + DIR_WORLDS, name + ".yml");
        WorldData worldData = new WorldData(this.plugin, this, file);
        worldData.save();
        worldData.load();
        this.dataMap.put(worldData.getId(), worldData);
        return worldData;
    }

    @NotNull
    public Set<WrappedWorld> getWorlds() {
        Set<WrappedWorld> set = new HashSet<>();

        Set<World> worlds = new HashSet<>(this.plugin.getServer().getWorlds());

        this.getDatas().forEach(worldConfig -> {
            World world = worldConfig.getWorld();
            if (world != null) {
                worlds.remove(world);
                set.add(wrap(world));
            }
            else set.add(new WrappedWorld(null, worldConfig));
        });

        worlds.forEach(world -> set.add(wrap(world)));

        return set;
    }

    public boolean isCustomWorld(@NotNull World world) {
        return this.getWorldData(world.getName()) != null;
    }

    @NotNull
    public WrappedWorld wrap(@NotNull World world) {
        WorldData worldData = this.getWorldData(world.getName());
        return new WrappedWorld(world, worldData);
    }

    public void openEditor(@NotNull Player player) {
        this.listEditor.open(player, this);
    }

    public void openWorldSettings(@NotNull Player player, @NotNull WrappedWorld wrappedWorld) {
        this.mainEditor.open(player, wrappedWorld);
    }

    public void openGameRules(@NotNull Player player, @NotNull WrappedWorld wrappedWorld) {
        this.rulesEditor.open(player, wrappedWorld);
    }

    public void openGenerationSettings(@NotNull Player player, @NotNull WorldData worldData) {
        this.generationEditor.open(player, worldData);
    }

    @Nullable
    public ChunkGenerator getChunkGenerator(@Nullable String id) {
        if (id == null) return null;

        return this.generatorMap.get(id.toLowerCase());
    }

    @Nullable
    public ChunkGenerator getPluginGenerator(@NotNull String world, @Nullable String name) {
        if (name == null || name.equalsIgnoreCase(Placeholders.DEFAULT)) return null;

        ChunkGenerator generator = this.getChunkGenerator(name);
        if (generator != null) return generator;

        return WorldCreator.getGeneratorForName(world, name, null);
    }

    public boolean isInventoryAffected(@NotNull Player player) {
        return this.isInventoryAffected(player.getWorld());
    }

    public boolean isInventoryAffected(@NotNull World world) {
        return this.getWorldGroup(world) != null;
    }

    @NotNull
    public WorldInventories getWorldInventory(@NotNull Player player) {
        String id = player.getUniqueId().toString();
        if (this.inventoryMap.containsKey(id)) {
            return this.inventoryMap.get(id);
        }

        File file = new File(this.getAbsolutePath() + DIR_INVENTORIES, id + ".yml");
        WorldInventories worldInventories = new WorldInventories(this.plugin, this, file);
        worldInventories.load();

        this.inventoryMap.put(worldInventories.getId(), worldInventories);
        return worldInventories;
    }

    @Nullable
    public String getWorldGroup(@NotNull World world) {
        String worldName = world.getName();

        return WorldsConfig.INVENTORY_SPLIT_WORLD_GROUPS.get().entrySet().stream()
            .filter(entry -> entry.getValue().contains(worldName))
            .map(Map.Entry::getKey).findFirst().orElse(null);
    }

    public boolean canFlyThere(@NotNull Player player) {
        if (player.hasPermission(WorldsPerms.BYPASS) || player.hasPermission(WorldsPerms.BYPASS_FLY)) return true;

        return !this.isFlyDisabled(player.getWorld());
    }

    public boolean isFlyDisabled(@NotNull World world) {
        return WorldsConfig.NO_FLY_WORLDS.get().contains(world.getName());
    }
}
