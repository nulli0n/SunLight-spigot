package su.nightexpress.sunlight.module.kits.editor;

import static su.nightexpress.nightcore.util.text.night.wrapper.TagWrappers.BOLD;
import static su.nightexpress.nightcore.util.text.night.wrapper.TagWrappers.GRAY;
import static su.nightexpress.nightcore.util.text.night.wrapper.TagWrappers.GREEN;
import static su.nightexpress.nightcore.util.text.night.wrapper.TagWrappers.WHITE;
import static su.nightexpress.nightcore.util.text.night.wrapper.TagWrappers.YELLOW;
import static su.nightexpress.sunlight.module.kits.KitsPlaceholders.KIT_ID;
import static su.nightexpress.sunlight.module.kits.KitsPlaceholders.KIT_NAME;

import java.util.List;
import java.util.stream.IntStream;

import org.bukkit.Material;
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
import su.nightexpress.nightcore.ui.inventory.item.ItemState;
import su.nightexpress.nightcore.ui.inventory.item.MenuItem;
import su.nightexpress.nightcore.ui.inventory.menu.AbstractMenu;
import su.nightexpress.nightcore.ui.inventory.viewer.ViewerContext;
import su.nightexpress.nightcore.util.bukkit.NightItem;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.module.kits.KitsModule;
import su.nightexpress.sunlight.module.kits.config.KitsLang;
import su.nightexpress.sunlight.module.kits.dialog.KitDialogKeys;
import su.nightexpress.sunlight.module.kits.model.Kit;

public class KitsEditorMenu extends AbstractMenu implements LangContainer {

    private static final IconLocale KIT_OBJECT = LangEntry.iconBuilder("Kits.UI.Editor.KitList.Kit")
        .rawName(YELLOW.and(BOLD).wrap("Kit: ") + WHITE.wrap(KIT_NAME) + " " + GRAY.wrap("(ID: " + KIT_ID + ")"))
        .br()
        .appendClick("Click to edit")
        .build();

    private static final IconLocale KIT_CREATE = LangEntry.iconBuilder("Kits.UI.Editor.KitList.Creation")
        .accentColor(GREEN)
        .rawName("Create a Kit")
        .br()
        .appendClick("Click to create")
        .build();

    private final KitsModule         module;
    private final ItemPopulator<Kit> kitPopulator;

    public KitsEditorMenu(@NonNull SunLightPlugin plugin, @NonNull KitsModule module) {
        super(MenuType.GENERIC_9X5, KitsLang.EDITOR_TITLE_LIST.text());
        this.module = module;

        this.kitPopulator = ItemPopulator.builder(Kit.class)
            .actionProvider(kit -> context -> this.module.openSettingsEditor(context.getPlayer(), kit))
            .itemProvider((context, kit) -> {
                return kit.definition().getIcon()
                    .hideAllComponents()
                    .localized(KIT_OBJECT)
                    .replace(builder -> builder.with(kit.placeholders()));
            })
            .slots(IntStream.range(0, 36).toArray())
            .build();

        plugin.injectLang(this);
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
        this.addBackgroundItem(Material.BLACK_STAINED_GLASS_PANE, IntStream.range(36, 45).toArray());

        this.addNextPageButton(44);
        this.addPreviousPageButton(36);

        this.addDefaultButton("create", MenuItem.button()
            .defaultState(ItemState.builder()
                .icon(NightItem.fromType(Material.ANVIL).localized(KIT_CREATE))
                .action(context -> {
                    this.plugin.showDialog(context.getPlayer(), KitDialogKeys.KIT_CREATION, this.module, () -> context
                        .getViewer().refresh());
                })
                .build()
            )
            .slots(40)
            .build()
        );
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
        this.kitPopulator.populateTo(context, this.module.getKits(), items);
    }

    @Override
    public void onReady(@NonNull ViewerContext context, @NonNull InventoryView view, @NonNull Inventory inventory) {

    }

    @Override
    public void onRender(@NonNull ViewerContext context, @NonNull InventoryView view, @NonNull Inventory inventory) {

    }
}
