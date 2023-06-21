package su.nightexpress.sunlight.module.spawns.editor;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.menu.AutoPaged;
import su.nexmedia.engine.api.menu.click.ItemClick;
import su.nexmedia.engine.api.menu.impl.EditorMenu;
import su.nexmedia.engine.api.menu.impl.MenuOptions;
import su.nexmedia.engine.api.menu.impl.MenuViewer;
import su.nexmedia.engine.utils.ItemUtil;
import su.nightexpress.sunlight.SunLight;
import su.nightexpress.sunlight.module.spawns.SpawnsModule;
import su.nightexpress.sunlight.module.spawns.impl.Spawn;
import su.nightexpress.sunlight.module.spawns.util.Placeholders;

import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

public class SpawnsEditor extends EditorMenu<SunLight, SpawnsModule> implements AutoPaged<Spawn> {

    private final SpawnsModule spawnsModule;

    public SpawnsEditor(@NotNull SpawnsModule module) {
        super(module.plugin(), module, Placeholders.EDITOR_TITLE, 45);
        this.spawnsModule = module;

        this.addExit(40);
        this.addNextPage(44);
        this.addPreviousPage(36);
    }

    @Override
    public void onPrepare(@NotNull MenuViewer viewer, @NotNull MenuOptions options) {
        super.onPrepare(viewer, options);
        this.getItemsForPage(viewer).forEach(this::addItem);
    }

    @Override
    public int[] getObjectSlots() {
        return IntStream.range(0, 36).toArray();
    }

    @Override
    @NotNull
    public List<Spawn> getObjects(@NotNull Player player) {
        return this.spawnsModule.getSpawns().stream()
            .sorted(Comparator.comparingInt(Spawn::getPriority).thenComparing(Spawn::getName)).toList();
    }

    @Override
    @NotNull
    public ItemStack getObjectStack(@NotNull Player player, @NotNull Spawn spawn) {
        ItemStack item = new ItemStack(Material.GRASS_BLOCK); // TODO
        ItemUtil.mapMeta(item, meta -> {
            meta.setDisplayName(EditorLocales.SPAWN_OBJECT.getLocalizedName());
            meta.setLore(EditorLocales.SPAWN_OBJECT.getLocalizedLore());
            meta.addItemFlags(ItemFlag.values());
            ItemUtil.replace(meta, spawn.replacePlaceholders());
        });
        return item;
    }

    @Override
    @NotNull
    public ItemClick getObjectClick(@NotNull Spawn spawn) {
        return (viewer, event) -> {
            if (event.isShiftClick() && event.isRightClick()) {
                spawn.getSpawnManager().deleteSpawn(spawn);
                this.openNextTick(viewer, 1);
                return;
            }
            spawn.getEditor().openNextTick(viewer, 1);
        };
    }
}
