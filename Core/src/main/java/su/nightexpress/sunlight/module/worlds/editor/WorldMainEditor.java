package su.nightexpress.sunlight.module.worlds.editor;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.SpawnCategory;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.menu.impl.EditorMenu;
import su.nexmedia.engine.api.menu.impl.MenuViewer;
import su.nexmedia.engine.editor.EditorManager;
import su.nexmedia.engine.utils.CollectionsUtil;
import su.nexmedia.engine.utils.Colorizer;
import su.nexmedia.engine.utils.ItemUtil;
import su.nexmedia.engine.utils.StringUtil;
import su.nightexpress.sunlight.SunLight;
import su.nightexpress.sunlight.module.worlds.WorldsModule;
import su.nightexpress.sunlight.module.worlds.config.WorldsLang;
import su.nightexpress.sunlight.module.worlds.impl.WorldConfig;
import su.nightexpress.sunlight.module.worlds.util.WorldUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public class WorldMainEditor extends EditorMenu<SunLight, WorldConfig> {

    private static final String TEXTURE_TRASH = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjQ2NWY4MGJmMDJiNDA4ODg1OTg3YjAwOTU3Y2E1ZTllYjg3NGMzZmE4ODMwNTA5OTU5N2EzMzNhMzM2ZWUxNSJ9fX0=";
    private static final String TEXTURE_ARROW_YELLOW = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvM2U0ZjJmOTY5OGMzZjE4NmZlNDRjYzYzZDJmM2M0ZjlhMjQxMjIzYWNmMDU4MTc3NWQ5Y2VjZDcwNzUifX19";
    private static final String TEXTURE_ARROW_GREEN = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvM2I4M2JiY2NmNGYwYzg2YjEyZjZmNzk5ODlkMTU5NDU0YmY5MjgxOTU1ZDdlMjQxMWNlOThjMWI4YWEzOGQ4In19fQ==";
    private static final String TEXTURE_EYE_GRAY = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTFhODA1ZDBhZTJiNTYyNzM2N2E1OTVhOGNjZjVmMDAyMTMzYjc3N2QxOTgzZGU3MmYxZDZiM2U2ZmI1YmQ5NyJ9fX0=";
    private static final String TEXTURE_EYE_BLUE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZmQwNjMzMDE4ZDA5ZmFlYTU5NWYxYWMxMDc1YTNiMDRhNDAwYzFmMDM2ZTJhOTY5ZWNkN2Y5NWM1ZmZlNjVmMCJ9fX0=";

    public WorldMainEditor(@NotNull WorldsModule worldsModule, @NotNull WorldConfig worldConfig) {
        super(worldConfig.plugin(), worldConfig, "World Editor: " + worldConfig.getId(), 54);

        this.addReturn(49).setClick((viewer, event) -> {
            this.plugin.runTask(task -> worldsModule.getEditor().open(viewer.getPlayer(), 1));
        });

        this.addItem(ItemUtil.createCustomHead(TEXTURE_EYE_GRAY), EditorLocales.WORLD_AUTO_LOAD, 2).setClick((viewer, event) -> {
            worldConfig.setAutoLoad(!worldConfig.isAutoLoad());
            this.save(viewer);
        }).getOptions().setDisplayModifier((viewer, item) -> {
            if (worldConfig.isAutoLoad()) {
                ItemUtil.setSkullTexture(item, TEXTURE_EYE_BLUE);
            }
        }).setVisibilityPolicy(viewer -> worldConfig.hasData());

        this.addItem(ItemUtil.createCustomHead(TEXTURE_ARROW_GREEN), EditorLocales.WORLD_LOAD, 4).setClick((viewer, event) -> {
            if (!worldConfig.isLoaded()) {
                worldConfig.loadWorld();
                this.plugin.runTask(task -> this.open(viewer.getPlayer(), viewer.getPage()));
            }
        }).getOptions().setVisibilityPolicy(viewer -> !worldConfig.isLoaded());

        this.addItem(ItemUtil.createCustomHead(TEXTURE_ARROW_YELLOW), EditorLocales.WORLD_UNLOAD, 6).setClick((viewer, event) -> {
            if (worldConfig.isLoaded()) {
                worldConfig.unloadWorld();
                this.plugin.runTask(task -> this.open(viewer.getPlayer(), viewer.getPage()));
            }
        }).getOptions().setVisibilityPolicy(viewer -> worldConfig.isLoaded());

        this.addItem(ItemUtil.createCustomHead(TEXTURE_TRASH), EditorLocales.WORLD_DELETE, 6).setClick((viewer, event) -> {
            if (worldConfig.isLoaded()) return;

            if (event.getClick() == ClickType.DROP) {
                worldConfig.deleteWorldFiles();
                this.plugin.runTask(task -> this.open(viewer.getPlayer(), 1));
                return;
            }
            if (event.isRightClick()) {
                worldConfig.deleteWorld(event.isShiftClick());
                this.plugin.runTask(task -> worldsModule.getEditor().open(viewer.getPlayer(), 1));
            }
        }).getOptions().setVisibilityPolicy(viewer -> !worldConfig.isLoaded() && worldConfig.hasData());


        this.addItem(Material.BUCKET, EditorLocales.WORLD_AUTO_WIPE, 8).setClick((viewer, event) -> {
            if (event.isLeftClick()) {
                worldConfig.setAutoWipe(!worldConfig.isAutoWipe());
                this.save(viewer);
                return;
            }
            if (event.getClick() == ClickType.DROP) {
                worldConfig.setLatestWipeDate();
                //worldConfig.setLastWipe(System.currentTimeMillis());
                this.save(viewer);
                return;
            }
            if (event.isRightClick()) {
                this.handleInput(viewer, WorldsLang.EDITOR_ENTER_SECONDS, wrapper -> {
                    worldConfig.setWipeInterval(wrapper.asAnyInt(-1));
                    worldConfig.save();
                    return true;
                });
            }
        }).getOptions().setVisibilityPolicy(viewer -> worldConfig.hasData()).setDisplayModifier((viewer, item) -> {
            if (worldConfig.isAutoWipe()) item.setType(Material.LAVA_BUCKET);
        });


        this.addItem(Material.OAK_SAPLING, EditorLocales.WORLD_GENERATOR, 20).setClick((viewer, event) -> {
            if (event.getClick() == ClickType.DROP) {
                worldConfig.getCreator().generator((ChunkGenerator) null);
                this.plugin.runTask(task -> this.open(viewer.getPlayer(), viewer.getPage()));
                return;
            }

            List<String> generators = new ArrayList<>();
            generators.addAll(worldsModule.getGeneratorMap().keySet());
            generators.addAll(WorldUtils.getGeneratorPlugins(worldConfig.getId()).stream().map(Plugin::getName).toList());
            EditorManager.suggestValues(viewer.getPlayer(), generators, true);

            this.handleInput(viewer, WorldsLang.EDITOR_ENTER_GENERATOR, wrapper -> {
                worldConfig.setGenerator(Colorizer.restrip(wrapper.getTextRaw()));
                worldConfig.save();
                return true;
            });
        }).getOptions().setVisibilityPolicy(viewer -> !worldConfig.hasData());

        this.addItem(Material.DEAD_BUSH, EditorLocales.WORLD_ENVIRONMENT, 22).setClick((viewer, event) -> {
            worldConfig.setEnvironment(CollectionsUtil.next(worldConfig.getEnvironment(), e -> e != World.Environment.CUSTOM));
            this.plugin.runTask(task -> this.open(viewer.getPlayer(), viewer.getPage()));
        }).getOptions().setVisibilityPolicy(viewer -> !worldConfig.hasData());

        this.addItem(Material.OAK_FENCE, EditorLocales.WORLD_STRUCTURES, 24).setClick((viewer, event) -> {
            worldConfig.getCreator().generateStructures(!worldConfig.getCreator().generateStructures());
            this.plugin.runTask(task -> this.open(viewer.getPlayer(), viewer.getPage()));
        }).getOptions().setVisibilityPolicy(viewer -> !worldConfig.hasData());


        this.addItem(Material.MOJANG_BANNER_PATTERN, EditorLocales.WORLD_GAME_RULES, 22).setClick((viewer, event) -> {
            this.plugin.runTask(task -> worldConfig.getRulesEditor().open(viewer.getPlayer(), 1));
        }).getOptions().setVisibilityPolicy(viewer -> worldConfig.isLoaded());


        this.addItem(Material.CAMPFIRE, EditorLocales.WORLD_AUTO_SAVE, 29).setClick((viewer, event) -> {
            worldConfig.setAutoSave(!worldConfig.isAutoSave());
            this.save(viewer);
        }).getOptions().setVisibilityPolicy(viewer -> worldConfig.hasData());

        this.addItem(Material.ROTTEN_FLESH, EditorLocales.WORLD_DIFFICULTY, 30).setClick((viewer, event) -> {
            worldConfig.setDifficulty(CollectionsUtil.next(worldConfig.getDifficulty()));
            this.save(viewer);
        }).getOptions().setVisibilityPolicy(viewer -> worldConfig.hasData());

        this.addItem(Material.NETHERITE_SWORD, EditorLocales.WORLD_PVP_ALLOWED, 31).setClick((viewer, event) -> {
            worldConfig.setPVPAllowed(!worldConfig.isPVPAllowed());
            this.save(viewer);
        }).getOptions().setVisibilityPolicy(viewer -> worldConfig.hasData());

        this.addItem(Material.TROPICAL_FISH_SPAWN_EGG, EditorLocales.WORLD_SPAWN_LIMITS, 32).setClick((viewer, event) -> {
            EditorManager.suggestValues(viewer.getPlayer(), CollectionsUtil.getEnumsList(SpawnCategory.class), false);
            this.handleInput(viewer, WorldsLang.EDITOR_ENTER_SPAWNS, wrapper -> {
                String[] split = Colorizer.restrip(wrapper.getTextRaw()).split(" ");
                SpawnCategory category = StringUtil.getEnum(split[0], SpawnCategory.class).orElse(null);
                if (category == null || category == SpawnCategory.MISC) return true;

                int limit = split.length >= 2 ? StringUtil.getInteger(split[1], -1, true) : -1;
                worldConfig.getSpawnLimits().put(category, limit);
                worldConfig.save();
                return true;
            });
        }).getOptions().setVisibilityPolicy(viewer -> worldConfig.hasData());

        this.addItem(Material.EGG, EditorLocales.WORLD_SPAWN_TICKS, 33).setClick((viewer, event) -> {
            EditorManager.suggestValues(viewer.getPlayer(), CollectionsUtil.getEnumsList(SpawnCategory.class), false);
            this.handleInput(viewer, WorldsLang.EDITOR_ENTER_SPAWNS, wrapper -> {
                String[] split = Colorizer.restrip(wrapper.getTextRaw()).split(" ");
                SpawnCategory category = StringUtil.getEnum(split[0], SpawnCategory.class).orElse(null);
                if (category == null || category == SpawnCategory.MISC) return true;

                int limit = split.length >= 2 ? StringUtil.getInteger(split[1], -1, true) : -1;
                worldConfig.getTicksPerSpawns().put(category, limit);
                worldConfig.save();
                return true;
            });
        }).getOptions().setVisibilityPolicy(viewer -> worldConfig.hasData());


        this.getItems().forEach(menuItem -> {
            BiConsumer<MenuViewer, ItemStack> display = menuItem.getOptions().getDisplayModifier();
            if (display == null) {
                display = ((viewer, item) -> ItemUtil.replace(item, worldConfig.replacePlaceholders()));
            }
            else {
                display = display.andThen((viewer, item) -> ItemUtil.replace(item, worldConfig.replacePlaceholders()));
            }
            menuItem.getOptions().setDisplayModifier(display);
        });
    }

    private void save(@NotNull MenuViewer viewer) {
        this.object.save();
        this.plugin.runTask(task -> this.open(viewer.getPlayer(), 1));
    }
}
