package su.nightexpress.sunlight.module.worlds.editor;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.menu.MenuOptions;
import su.nightexpress.nightcore.menu.MenuSize;
import su.nightexpress.nightcore.menu.MenuViewer;
import su.nightexpress.nightcore.menu.api.AutoFill;
import su.nightexpress.nightcore.menu.api.AutoFilled;
import su.nightexpress.nightcore.menu.impl.EditorMenu;
import su.nightexpress.nightcore.util.ItemReplacer;
import su.nightexpress.nightcore.util.ItemUtil;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.module.worlds.WorldsModule;
import su.nightexpress.sunlight.module.worlds.config.WorldsLang;
import su.nightexpress.sunlight.module.worlds.impl.WrappedWorld;

import java.util.Comparator;
import java.util.stream.IntStream;

public class WorldListEditor extends EditorMenu<SunLightPlugin, WorldsModule> implements AutoFilled<WrappedWorld> {

    private static final String TEXTURE_CUSTOM_WORLD = "597e4e27a04afa5f06108265a9bfb797630391c7f3d880d244f610bb1ff393d8";

    private final WorldsModule module;

    public WorldListEditor(@NotNull SunLightPlugin plugin, @NotNull WorldsModule module) {
        super(plugin, WorldsLang.EDITOR_TITLE_LIST.getString(), MenuSize.CHEST_45);
        this.module = module;

        this.addNextPage(44);
        this.addPreviousPage(36);
        this.addExit(40);
    }

    @Override
    public void onPrepare(@NotNull MenuViewer viewer, @NotNull MenuOptions options) {
        this.autoFill(viewer);
    }

    @Override
    protected void onReady(@NotNull MenuViewer viewer, @NotNull Inventory inventory) {

    }

    @Override
    public void onAutoFill(@NotNull MenuViewer viewer, @NotNull AutoFill<WrappedWorld> autoFill) {
        autoFill.setSlots(IntStream.range(0, 36).toArray());
        autoFill.setItems(this.module.getWorlds().stream().sorted(Comparator.comparing(WrappedWorld::isCustom)
            .thenComparing(world -> world.isCustom() ? world.getData().getId() : world.getWorld().getName()))
            .toList()
        );
        autoFill.setItemCreator(wrappedWorld -> {
            ItemStack item;
            if (wrappedWorld.isCustom()) {
                item = ItemUtil.getSkinHead(TEXTURE_CUSTOM_WORLD);
            }
            else {
                Material material = switch (wrappedWorld.getWorld().getEnvironment()) {
                    case NETHER -> Material.NETHERRACK;
                    case THE_END -> Material.END_STONE;
                    default -> Material.GRASS_BLOCK;
                };
                item = new ItemStack(material);
            }

            ItemReplacer.create(item).hideFlags().trimmed()
                .readLocale(WorldsLang.EDITOR_WORLD_OBJECT)
                .replace(wrappedWorld.getPlaceholders())
                .writeMeta();
            return item;
        });
        autoFill.setClickAction(wrappedWorld -> (viewer1, event) -> {
            if (wrappedWorld.isCustom() && !wrappedWorld.isPresent()) {
                this.runNextTick(() -> this.module.openGenerationSettings(viewer.getPlayer(), wrappedWorld.getData()));
            }
            else this.runNextTick(() -> this.module.openWorldSettings(viewer.getPlayer(), wrappedWorld));
        });
    }
}
