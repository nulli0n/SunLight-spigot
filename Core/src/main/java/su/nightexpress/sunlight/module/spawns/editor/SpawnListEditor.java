package su.nightexpress.sunlight.module.spawns.editor;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
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
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.module.spawns.SpawnsModule;
import su.nightexpress.sunlight.module.spawns.config.SpawnsLang;
import su.nightexpress.sunlight.module.spawns.impl.Spawn;

import java.util.Comparator;
import java.util.stream.IntStream;

public class SpawnListEditor extends EditorMenu<SunLightPlugin, SpawnsModule> implements AutoFilled<Spawn> {

    private final SpawnsModule module;

    public SpawnListEditor(@NotNull SunLightPlugin plugin, @NotNull SpawnsModule module) {
        super(plugin, SpawnsLang.EDITOR_TITLE_LIST.getString(), MenuSize.CHEST_45);
        this.module = module;

        this.addExit(40);
        this.addNextPage(44);
        this.addPreviousPage(36);
    }

    @Override
    public void onPrepare(@NotNull MenuViewer viewer, @NotNull MenuOptions options) {
        this.autoFill(viewer);
    }

    @Override
    protected void onReady(@NotNull MenuViewer viewer, @NotNull Inventory inventory) {

    }

    @Override
    public void onAutoFill(@NotNull MenuViewer viewer, @NotNull AutoFill<Spawn> autoFill) {
        autoFill.setSlots(IntStream.range(0, 36).toArray());
        autoFill.setItems(this.module.getSpawns().stream()
            .sorted(Comparator.comparingInt(Spawn::getPriority).reversed().thenComparing(Spawn::getName)).toList());
        autoFill.setItemCreator(spawn -> {
            ItemStack item = new ItemStack(Material.GRASS_BLOCK);
            if (spawn.isValid()) {
                Block block = spawn.getLocation().getBlock().getRelative(BlockFace.DOWN);
                if (!block.isEmpty()) item.setType(block.getType());
            }

            ItemReplacer.create(item).hideFlags().trimmed()
                .readLocale(SpawnsLang.EDITOR_SPAWN_OBJECT)
                .replace(spawn.getEditorPlaceholders())
                .writeMeta();
            return item;
        });
        autoFill.setClickAction(spawn -> (viewer1, event) -> {
            if (event.isShiftClick() && event.isRightClick()) {
                this.module.deleteSpawn(spawn);
                this.runNextTick(() -> this.flush(viewer));
                return;
            }
            this.module.openSpawnSettings(viewer.getPlayer(), spawn);
        });
    }
}
