package su.nightexpress.sunlight.module.kits.editor;

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
import su.nightexpress.nightcore.ui.inventory.item.ItemState;
import su.nightexpress.nightcore.ui.inventory.item.MenuItem;
import su.nightexpress.nightcore.ui.inventory.menu.AbstractMenu;
import su.nightexpress.nightcore.ui.inventory.viewer.ViewerContext;
import su.nightexpress.nightcore.util.bukkit.NightItem;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.dialog.DialogRegistry;
import su.nightexpress.sunlight.module.kits.KitsModule;
import su.nightexpress.sunlight.module.kits.config.KitsLang;
import su.nightexpress.sunlight.module.kits.dialog.KitDialogKeys;
import su.nightexpress.sunlight.module.kits.model.Kit;

import java.util.List;
import java.util.stream.IntStream;

import static su.nightexpress.nightcore.util.text.night.wrapper.TagWrappers.*;
import static su.nightexpress.sunlight.module.kits.KitsPlaceholders.KIT_ID;
import static su.nightexpress.sunlight.module.kits.KitsPlaceholders.KIT_NAME;

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
    private final DialogRegistry     dialogRegistry;
    private final ItemPopulator<Kit> kitPopulator;

    public KitsEditorMenu(@NotNull SunLightPlugin plugin, @NotNull KitsModule module, @NotNull DialogRegistry dialogRegistry) {
        super(MenuType.GENERIC_9X5, KitsLang.EDITOR_TITLE_LIST.text());
        this.module = module;
        this.dialogRegistry = dialogRegistry;

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

        this.addNextPageItem(Material.ARROW, 44);
        this.addPreviousPageItem(Material.ARROW, 36);

        this.addDefaultButton("create", MenuItem.builder()
            .defaultState(ItemState.defaultBuilder()
                .icon(NightItem.fromType(Material.ANVIL).localized(KIT_CREATE))
                .action(context -> {
                    this.dialogRegistry.show(context.getPlayer(), KitDialogKeys.KIT_CREATION, this.module, () -> context.getViewer().refresh());
                })
                .build()
            )
            .slots(40)
            .build()
        );
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
        this.kitPopulator.populateTo(context, this.module.getKits(), items);
    }

    @Override
    public void onReady(@NotNull ViewerContext context, @NotNull InventoryView view, @NotNull Inventory inventory) {

    }

    @Override
    public void onRender(@NotNull ViewerContext context, @NotNull InventoryView view, @NotNull Inventory inventory) {

    }
}
