package su.nightexpress.sunlight.module.warps.menu;

import static su.nightexpress.nightcore.util.text.night.wrapper.TagWrappers.GOLD;
import static su.nightexpress.nightcore.util.text.night.wrapper.TagWrappers.UNDERLINED;
import static su.nightexpress.sunlight.module.warps.WarpsPlaceholders.WARP_DESCRIPTION;
import static su.nightexpress.sunlight.module.warps.WarpsPlaceholders.WARP_NAME;

import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

import org.bukkit.Material;
import org.bukkit.entity.Player;
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
import su.nightexpress.nightcore.ui.inventory.item.ItemState;
import su.nightexpress.nightcore.ui.inventory.item.MenuItem;
import su.nightexpress.nightcore.ui.inventory.menu.AbstractMenu;
import su.nightexpress.nightcore.ui.inventory.viewer.MenuViewer;
import su.nightexpress.nightcore.ui.inventory.viewer.ViewerContext;
import su.nightexpress.nightcore.util.Placeholders;
import su.nightexpress.sunlight.module.warps.Warp;
import su.nightexpress.sunlight.module.warps.WarpsModule;

public class WarpListMenu extends AbstractMenu implements LangContainer {

    private static final IconLocale ICON_WARP_DEFAULT = LangEntry.iconBuilder("Warps.UI.WarpsMenu.WarpDefault")
        .rawName(WARP_NAME)
        .rawLore(
            WARP_DESCRIPTION,
            Placeholders.EMPTY_IF_ABOVE,
            GOLD.wrap("→ " + UNDERLINED.wrap("Click to teleport"))
        )
        .build();

    private static final IconLocale ICON_WARP_MODERATE = LangEntry.iconBuilder("Warps.UI.WarpsMenu.WarpModerate")
        .rawName(WARP_NAME)
        .rawLore(
            WARP_DESCRIPTION,
            Placeholders.EMPTY_IF_ABOVE,
            GOLD.wrap("→ " + UNDERLINED.wrap("Left-Click to teleport")),
            GOLD.wrap("→ " + UNDERLINED.wrap("Right-Click to edit"))
        )
        .build();

    private final WarpsModule module;

    public WarpListMenu(@NonNull WarpsModule module) {
        super(MenuType.GENERIC_9X6, "Server Warps");
        this.module = module;
    }

    @Override
    public void registerActions() {

    }

    @Override
    public void registerConditions() {

    }

    @Override
    public void defineDefaultLayout() {
        this.addBackgroundItem(Material.BLACK_STAINED_GLASS_PANE, IntStream.range(0, 9).toArray());
        this.addBackgroundItem(Material.BLACK_STAINED_GLASS_PANE, IntStream.range(45, 54).toArray());
        this.addBackgroundItem(Material.GRAY_STAINED_GLASS_PANE, IntStream.range(9, 45).toArray());

        this.addNextPageButton(53);
        this.addPreviousPageButton(45);
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
        Player player = context.getPlayer();
        MenuViewer viewer = context.getViewer();

        Set<Warp> warps = this.module.getAvailableWarps(player);

        int maxPages = warps.stream().mapToInt(Warp::getMenuPage).max().orElse(1);
        viewer.setTotalPages(maxPages);

        warps.stream().filter(warp -> warp.getMenuPage() == viewer.getCurrentPage()).forEach(warp -> {
            items.add(MenuItem.custom()
                .defaultState(ItemState.builder()
                    .icon(warp.getIcon()
                        .localized(warp.canEdit(player) ? ICON_WARP_MODERATE : ICON_WARP_DEFAULT)
                        .replace(builder -> builder.with(warp.placeholders()))
                    )
                    .action(ctx -> this.module.clickWarp(ctx, warp))
                    .build()
                )
                .slots(warp.getMenuSlots())
                .build());
        });
    }

    @Override
    public void onReady(@NonNull ViewerContext context, @NonNull InventoryView view, @NonNull Inventory inventory) {

    }

    @Override
    public void onRender(@NonNull ViewerContext context, @NonNull InventoryView view, @NonNull Inventory inventory) {

    }
}
