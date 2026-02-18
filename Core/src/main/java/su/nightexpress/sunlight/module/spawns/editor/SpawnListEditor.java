package su.nightexpress.sunlight.module.spawns.editor;

import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.MenuType;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.locale.LangContainer;
import su.nightexpress.nightcore.locale.LangEntry;
import su.nightexpress.nightcore.locale.entry.IconLocale;
import su.nightexpress.nightcore.ui.inventory.item.ItemPopulator;
import su.nightexpress.nightcore.ui.inventory.item.MenuItem;
import su.nightexpress.nightcore.ui.inventory.menu.AbstractMenu;
import su.nightexpress.nightcore.ui.inventory.viewer.ViewerContext;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.module.spawns.SpawnsModule;
import su.nightexpress.sunlight.module.spawns.config.SpawnsLang;
import su.nightexpress.sunlight.module.spawns.Spawn;

import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

import static su.nightexpress.nightcore.util.text.night.wrapper.TagWrappers.*;
import static su.nightexpress.sunlight.module.spawns.SpawnsPlaceholders.SPAWN_ID;
import static su.nightexpress.sunlight.module.spawns.SpawnsPlaceholders.SPAWN_NAME;

public class SpawnListEditor extends AbstractMenu implements LangContainer {

    private static final IconLocale SPAWN_OBJECT = LangEntry.iconBuilder("Spawns.Editor.SpawnsList.Spawn")
        .accentColor(YELLOW)
        .name(SPAWN_NAME)
        .appendCurrent("ID", SPAWN_ID).br()
        .appendClick("Click to edit")
        .build();

    private final SpawnsModule         module;
    private final ItemPopulator<Spawn> spawnPopulator;

    public SpawnListEditor(@NotNull SunLightPlugin plugin, @NotNull SpawnsModule module) {
        super(MenuType.GENERIC_9X5, SpawnsLang.EDITOR_TITLE_LIST.text());
        this.module = module;

        plugin.injectLang(this);

        this.spawnPopulator = ItemPopulator.builder(Spawn.class)
            .slots(IntStream.range(0, 36).toArray())
            .itemProvider((context, spawn) -> {
                return spawn.getIcon()
                    .hideAllComponents()
                    .localized(SPAWN_OBJECT)
                    .replace(builder -> builder.with(spawn.placeholders()));
            })
            .actionProvider(spawn -> context -> {
                this.module.openSpawnSettings(context.getPlayer(), spawn);
            })
            .build();

        this.load(plugin);
    }

    @Override
    public void registerActions() {

    }

    @Override
    public void registerConditions() {

    }

    @Override
    public void defineDefaultLayout() {
        this.addNextPageItem(Material.ARROW, 44);
        this.addPreviousPageItem(Material.ARROW, 36);
    }

    @Override
    protected void onLoad(@NotNull FileConfig config) {

    }

    @Override
    protected void onClick(@NotNull ViewerContext context, @NotNull InventoryClickEvent event) {

    }

    @Override
    protected void onDrag(@NotNull ViewerContext context, @NotNull InventoryDragEvent event) {

    }

    @Override
    protected void onClose(@NotNull ViewerContext context, @NotNull InventoryCloseEvent event) {

    }

    @Override
    public void onPrepare(@NotNull ViewerContext context, @NotNull InventoryView view, @NotNull Inventory inventory, @NotNull List<MenuItem> items) {
        List<Spawn> spawns = this.module.getSpawns().stream()
            .sorted(Comparator.comparingInt(Spawn::getPriority).reversed().thenComparing(Spawn::getName))
            .toList();

        this.spawnPopulator.populateTo(context, spawns, items);
    }

    @Override
    public void onReady(@NotNull ViewerContext context, @NotNull InventoryView view, @NotNull Inventory inventory) {

    }

    @Override
    public void onRender(@NotNull ViewerContext context, @NotNull InventoryView view, @NotNull Inventory inventory) {

    }
}
