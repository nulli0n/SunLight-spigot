package su.nightexpress.sunlight.module.worlds.editor;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.menu.MenuOptions;
import su.nightexpress.nightcore.menu.MenuSize;
import su.nightexpress.nightcore.menu.MenuViewer;
import su.nightexpress.nightcore.menu.impl.EditorMenu;
import su.nightexpress.nightcore.util.ItemReplacer;
import su.nightexpress.nightcore.util.ItemUtil;
import su.nightexpress.nightcore.util.Lists;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.module.worlds.WorldsModule;
import su.nightexpress.sunlight.module.worlds.config.WorldsLang;
import su.nightexpress.sunlight.module.worlds.impl.WorldData;
import su.nightexpress.sunlight.module.worlds.util.DeletionType;
import su.nightexpress.sunlight.module.worlds.util.Placeholders;
import su.nightexpress.sunlight.module.worlds.util.WorldUtils;

import java.util.ArrayList;
import java.util.List;

public class WorldGenerationEditor extends EditorMenu<SunLightPlugin, WorldData> {

    private static final String TEXTURE_LOAD   = "ff7416ce9e826e4899b284bb0ab94843a8f7586e52b71fc3125e0286f926a";
    private static final String TEXTURE_DELETE = "b465f80bf02b408885987b00957ca5e9eb874c3fa88305099597a333a336ee15";

    public WorldGenerationEditor(@NotNull SunLightPlugin plugin, @NotNull WorldsModule module) {
        super(plugin, WorldsLang.EDITOR_TITLE_GENERATION.getString(), MenuSize.CHEST_18);

        this.addReturn(13, (viewer, event, data) -> {
            this.runNextTick(() -> module.openEditor(viewer.getPlayer()));
        });

        this.addItem(ItemUtil.getSkinHead(TEXTURE_LOAD), WorldsLang.EDITOR_WORLD_LOAD, 7, (viewer, event, data) -> {
            if (data.isLoaded()) return;

            World world = data.loadWorld();
            if (world != null) {
                this.runNextTick(() -> module.openWorldSettings(viewer.getPlayer(), module.wrap(world)));
            }
        });


        this.addItem(ItemUtil.getSkinHead(TEXTURE_DELETE), WorldsLang.EDITOR_WORLD_DELETE, 8, (viewer, event, data) -> {
            if (data.isLoaded()) return;

            if (event.getClick() == ClickType.DROP) {
                data.delete(DeletionType.DIRECTORY);
                this.runNextTick(() -> module.openEditor(viewer.getPlayer()));
                return;
            }
            if (event.isRightClick()) {
                data.delete(event.isShiftClick() ? DeletionType.FULL : DeletionType.DATA);
                this.runNextTick(() -> module.openEditor(viewer.getPlayer()));
            }
        });


        this.addItem(Material.OAK_SAPLING, WorldsLang.EDITOR_WORLD_SET_GENERATOR, 0, (viewer, event, data) -> {
            if (event.getClick() == ClickType.DROP) {
                data.setGenerator(null);
                this.save(viewer);
                return;
            }

            List<String> generators = new ArrayList<>();
            generators.addAll(module.getGeneratorMap().keySet());
            generators.addAll(WorldUtils.getGeneratorPlugins(data.getId()).stream().map(Plugin::getName).toList());

            this.handleInput(viewer, WorldsLang.EDITOR_INPUT_GENERIC_NAME, (dialog, input) -> {
                data.setGenerator(input.getTextRaw());
                data.save();
                return true;
            }).setSuggestions(generators, true);
        });

        this.addItem(Material.DEAD_BUSH, WorldsLang.EDITOR_WORLD_SET_ENVIRONMENT, 1, (viewer, event, data) -> {
            data.setEnvironment(Lists.next(data.getEnvironment(), environment -> environment != World.Environment.CUSTOM));
            this.save(viewer);
        });

        this.addItem(Material.OAK_FENCE, WorldsLang.EDITOR_WORLD_SET_STRUCTURES, 2, (viewer, event, data) -> {
            data.setGenerateStructures(!data.isGenerateStructures());
            this.save(viewer);
        });


        this.getItems().forEach(menuItem -> menuItem.getOptions().addDisplayModifier((viewer, itemStack) -> {
            ItemReplacer.replace(itemStack, Placeholders.forGeneration(this.getLink(viewer)));
        }));
    }

    private void save(@NotNull MenuViewer viewer) {
        this.getLink(viewer).save();
        this.runNextTick(() -> this.flush(viewer));
    }

    @Override
    protected void onPrepare(@NotNull MenuViewer viewer, @NotNull MenuOptions options) {

    }

    @Override
    protected void onReady(@NotNull MenuViewer viewer, @NotNull Inventory inventory) {

    }
}
