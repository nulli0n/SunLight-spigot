package su.nightexpress.sunlight.module.worlds.editor;

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
import su.nightexpress.sunlight.module.worlds.WorldsModule;
import su.nightexpress.sunlight.module.worlds.impl.WorldConfig;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

public class WorldListEditor extends EditorMenu<SunLight, WorldsModule> implements AutoPaged<WorldConfig> {

    private static final String TEXTURE_CUSTOM_WORLD = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjliMjg4OTAyMDU4MzU2NWY4OGQ1MDUzNzg3MGM1OWFhZDgwMjU5NGZhYmQ4MzdlMWQxNGY1YTA2YWUzNDUwOSJ9fX0=";

    private final WorldsModule worldsModule;

    public WorldListEditor(@NotNull WorldsModule worldsModule) {
        super(worldsModule.plugin(), worldsModule, EditorLocales.TITLE_WORLDS_EDITOR, 45);
        this.worldsModule = worldsModule;

        this.addNextPage(44);
        this.addPreviousPage(36);
        this.addExit(40);
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
    public List<WorldConfig> getObjects(@NotNull Player player) {
        return new ArrayList<>(this.worldsModule.getWorldConfigs().stream().sorted(Comparator.comparing(WorldConfig::getId)).toList());
    }

    @Override
    @NotNull
    public ItemStack getObjectStack(@NotNull Player player, @NotNull WorldConfig world) {
        ItemStack item = ItemUtil.createCustomHead(TEXTURE_CUSTOM_WORLD);
        ItemUtil.mapMeta(item, meta -> {
            meta.setDisplayName(EditorLocales.WORLD_OBJECT.getLocalizedName());
            meta.setLore(EditorLocales.WORLD_OBJECT.getLocalizedLore());
            meta.addItemFlags(ItemFlag.values());
            ItemUtil.replace(meta, world.replacePlaceholders());
        });
        return item;
    }

    @Override
    @NotNull
    public ItemClick getObjectClick(@NotNull WorldConfig world) {
        return (viewer, event) -> {
            this.plugin.runTask(task -> world.getEditor().open(viewer.getPlayer(), 1));
        };
    }
}
