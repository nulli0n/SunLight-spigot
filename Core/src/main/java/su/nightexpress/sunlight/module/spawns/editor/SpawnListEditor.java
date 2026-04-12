package su.nightexpress.sunlight.module.spawns.editor;

import static su.nightexpress.nightcore.util.text.night.wrapper.TagWrappers.YELLOW;
import static su.nightexpress.sunlight.module.spawns.SpawnsPlaceholders.SPAWN_ID;
import static su.nightexpress.sunlight.module.spawns.SpawnsPlaceholders.SPAWN_NAME;

import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.MenuType;
import org.jspecify.annotations.NonNull;

import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.locale.LangContainer;
import su.nightexpress.nightcore.locale.LangEntry;
import su.nightexpress.nightcore.locale.entry.IconLocale;
import su.nightexpress.nightcore.ui.inventory.item.ItemPopulator;
import su.nightexpress.nightcore.ui.inventory.item.MenuItem;
import su.nightexpress.nightcore.ui.inventory.menu.AbstractMenu;
import su.nightexpress.nightcore.ui.inventory.viewer.ViewerContext;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.module.spawns.Spawn;
import su.nightexpress.sunlight.module.spawns.SpawnsModule;
import su.nightexpress.sunlight.module.spawns.config.SpawnsLang;

public class SpawnListEditor extends AbstractMenu implements LangContainer {

    private static final IconLocale SPAWN_OBJECT = LangEntry.iconBuilder("Spawns.Editor.SpawnsList.Spawn")
        .accentColor(YELLOW)
        .name(SPAWN_NAME)
        .appendCurrent("ID", SPAWN_ID).br()
        .appendClick("Click to edit")
        .build();

    private final SpawnsModule         module;
    private final ItemPopulator<Spawn> spawnPopulator;

    public SpawnListEditor(@NonNull SunLightPlugin plugin, @NonNull SpawnsModule module) {
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
        this.addNextPageButton(44);
        this.addPreviousPageButton(36);
    }

    @Override
    protected void onLoad(@NonNull FileConfig config) {

    }

    @Override
    protected void onClick(@NonNull ViewerContext context, @NonNull InventoryClickEvent event) {

    }

    @Override
    protected void onDrag(@NonNull ViewerContext context, @NonNull InventoryDragEvent event) {

    }

    @Override
    protected void onClose(@NonNull ViewerContext context, @NonNull InventoryCloseEvent event) {

    }

    @Override
    public void onPrepare(@NonNull ViewerContext context, @NonNull InventoryView view, @NonNull Inventory inventory, @NonNull List<MenuItem> items) {
        List<Spawn> spawns = this.module.getSpawns().stream()
            .sorted(Comparator.comparingInt(Spawn::getPriority).reversed().thenComparing(Spawn::getName))
            .toList();

        this.spawnPopulator.populateTo(context, spawns, items);
    }

    @Override
    public void onReady(@NonNull ViewerContext context, @NonNull InventoryView view, @NonNull Inventory inventory) {

    }

    @Override
    public void onRender(@NonNull ViewerContext context, @NonNull InventoryView view, @NonNull Inventory inventory) {

    }
}
