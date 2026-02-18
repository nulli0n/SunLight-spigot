package su.nightexpress.sunlight.module.playerwarps.menu;

import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MenuType;
import org.jspecify.annotations.NonNull;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.integration.currency.EconomyBridge;
import su.nightexpress.nightcore.ui.inventory.action.MenuItemAction;
import su.nightexpress.nightcore.ui.inventory.action.ObjectActionContext;
import su.nightexpress.nightcore.ui.inventory.item.ItemState;
import su.nightexpress.nightcore.ui.inventory.item.MenuItem;
import su.nightexpress.nightcore.ui.inventory.menu.AbstractObjectMenu;
import su.nightexpress.nightcore.ui.inventory.viewer.ViewerContext;
import su.nightexpress.nightcore.util.Lists;
import su.nightexpress.nightcore.util.bukkit.NightItem;
import su.nightexpress.nightcore.util.placeholder.PlaceholderContext;
import su.nightexpress.sunlight.module.playerwarps.PlayerWarp;
import su.nightexpress.sunlight.module.playerwarps.PlayerWarpsModule;
import su.nightexpress.sunlight.module.playerwarps.PlayerWarpsPlaceholders;
import su.nightexpress.sunlight.module.playerwarps.core.PlayerWarpsPerms;

import java.util.List;
import java.util.stream.IntStream;

import static su.nightexpress.nightcore.util.text.night.wrapper.TagWrappers.*;
import static su.nightexpress.sunlight.module.playerwarps.PlayerWarpsPlaceholders.*;

public class PlayerWarpOptionsMenu extends AbstractObjectMenu<PlayerWarp> {

    private final PlayerWarpsModule module;

    private final MenuItemAction backAction;
    private final MenuItemAction nameAction;
    private final MenuItemAction descriptionAction;
    private final MenuItemAction priceAction;
    private final MenuItemAction categoryAction;

    public PlayerWarpOptionsMenu(@NonNull PlayerWarpsModule module) {
        super(MenuType.GENERIC_9X5, "[%s] Settings".formatted(WARP_NAME), PlayerWarp.class);
        this.module = module;

        this.backAction = this.createObjectAction(this::backToWarps);
        this.nameAction = this.createObjectAction(this::editName);
        this.descriptionAction = this.createObjectAction(this::editDescription);
        this.priceAction = this.createObjectAction(this::editPrice);
        this.categoryAction = this.createObjectAction(this::editCategory);
    }

    @Override
    @NonNull
    protected String getRawTitle(@NonNull ViewerContext context) {
        return PlaceholderContext.builder().with(this.getObject(context).placeholders()).build().apply(super.getRawTitle(context));
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
        this.addBackgroundItem(Material.BLACK_STAINED_GLASS_PANE, IntStream.range(36, 45).toArray());

        this.addDefaultButton("back", MenuItem.builder()
            .defaultState(ItemState.defaultBuilder()
                .icon(NightItem.fromType(Material.COMPASS).setDisplayName(WHITE.wrap("Go Back")))
                .action(this.backAction)
                .build()
            )
            .slots(40)
            .build()
        );

        this.addDefaultButton("name", MenuItem.builder()
            .defaultState(ItemState.defaultBuilder()
                .icon(NightItem.fromType(Material.NAME_TAG)
                    .setDisplayName(GOLD.and(BOLD).wrap("Name"))
                    .setLore(Lists.newList(
                        DARK_GRAY.wrap("» " + GRAY.wrap("Current: ") + WHITE.wrap(WARP_NAME)),
                        "",
                        GOLD.wrap("→ " + UNDERLINED.wrap("Click to edit"))
                    ))
                    .hideAllComponents()
                )
                .displayModifier((context, item) -> item.replace(builder -> builder.with(this.getObject(context).placeholders())))
                .action(this.nameAction)
                .build()
            )
            .slots(19)
            .build()
        );

        this.addDefaultButton("description", MenuItem.builder()
            .defaultState(ItemState.defaultBuilder()
                .icon(NightItem.fromType(Material.WRITABLE_BOOK)
                    .setDisplayName(GOLD.and(BOLD).wrap("Description"))
                    .setLore(Lists.newList(
                        WARP_DESCRIPTION,
                        "",
                        GOLD.wrap("→ " + UNDERLINED.wrap("Click to edit"))
                    ))
                    .hideAllComponents()
                )
                .displayModifier((context, item) -> item.replace(builder -> builder.with(this.getObject(context).placeholders())))
                .action(this.descriptionAction)
                .build()
            )
            .slots(21)
            .build()
        );

        this.addDefaultButton("icon", MenuItem.builder()
            .defaultState(ItemState.defaultBuilder()
                .icon(NightItem.fromType(Material.ITEM_FRAME)
                    .setDisplayName(GREEN.and(BOLD).wrap("Icon"))
                    .setLore(Lists.newList(
                        GRAY.wrap("Right click an item in your"),
                        GRAY.wrap("inventory to replace the icon.")
                    ))
                    .hideAllComponents()
                )
                .displayModifier((context, item) -> item.setMaterial(this.getObject(context).getIcon().getMaterial()))
                .build()
            )
            .slots(23)
            .build()
        );

        this.addDefaultButton("category", MenuItem.builder()
            .defaultState(ItemState.defaultBuilder()
                .icon(NightItem.fromType(Material.ENCHANTED_BOOK)
                    .setDisplayName(GOLD.and(BOLD).wrap("Category"))
                    .setLore(Lists.newList(
                        DARK_GRAY.wrap("» " + GRAY.wrap("Current: ") + WHITE.wrap(PlayerWarpsPlaceholders.WARP_CATEGORY)),
                        "",
                        GOLD.wrap("→ " + UNDERLINED.wrap("Click to edit"))
                    ))
                    .hideAllComponents()
                )
                .displayModifier((context, item) -> item.replace(builder -> builder.with(this.getObject(context).placeholders())))
                .action(this.categoryAction)
                .build()
            )
            .slots(25)
            .build()
        );

        this.addDefaultButton("price", MenuItem.builder()
            .defaultState(ItemState.defaultBuilder()
                .icon(NightItem.fromType(Material.EMERALD)
                    .setDisplayName(GREEN.and(BOLD).wrap("Price"))
                    .setLore(Lists.newList(
                        DARK_GRAY.wrap("» " + GRAY.wrap("Current: ") + WHITE.wrap(WARP_PRICE)),
                        "",
                        GREEN.wrap("→ " + UNDERLINED.wrap("Click to edit"))
                    ))
                    .hideAllComponents()
                )
                .condition(context -> EconomyBridge.hasCurrency() && context.getPlayer().hasPermission(PlayerWarpsPerms.OPTION_PRICE))
                .displayModifier((context, item) -> item.replace(builder -> builder.with(this.getObject(context).placeholders())))
                .action(this.priceAction)
                .build()
            )
            .slots(4)
            .build()
        );
    }

    protected void backToWarps(@NonNull ObjectActionContext<PlayerWarp> context) {
        this.module.openWarpsMenu(context.getPlayer());
    }

    private void editCategory(@NonNull ObjectActionContext<PlayerWarp> context) {
        this.module.openCategoryDialog(context.getPlayer(), context.getObject(), () -> context.getViewer().refresh());
    }

    private void editName(@NonNull ObjectActionContext<PlayerWarp> context) {
        this.module.openNameDialog(context.getPlayer(), context.getObject(), () -> context.getViewer().refresh());
    }

    private void editDescription(@NonNull ObjectActionContext<PlayerWarp> context) {
        this.module.openDescriptionDialog(context.getPlayer(), context.getObject(), () -> context.getViewer().refresh());
    }

    private void editPrice(@NonNull ObjectActionContext<PlayerWarp> context) {
        this.module.openPriceDialog(context.getPlayer(), context.getObject(), () -> context.getViewer().refresh());
    }

    @Override
    protected void onLoad(@NonNull FileConfig config) {

    }

    @Override
    protected void onClick(@NonNull ViewerContext context, @NonNull InventoryClickEvent event) {
        if (event.isRightClick() && event.getRawSlot() >= event.getInventory().getSize()) {
            ItemStack itemStack = event.getCurrentItem();
            if (itemStack == null || itemStack.getType().isAir()) return;

            PlayerWarp warp = this.getObject(context);
            warp.setIcon(NightItem.fromItemStack(itemStack).hideAllComponents().ignoreNameAndLore());
            warp.markDirty();
            context.getViewer().refresh();
        }
    }

    @Override
    protected void onDrag(@NonNull ViewerContext context, @NonNull InventoryDragEvent event) {

    }

    @Override
    protected void onClose(@NonNull ViewerContext context, @NonNull InventoryCloseEvent event) {

    }

    @Override
    public void onPrepare(@NonNull ViewerContext context, @NonNull InventoryView view, @NonNull Inventory inventory, @NonNull List<MenuItem> items) {

    }

    @Override
    public void onReady(@NonNull ViewerContext context, @NonNull InventoryView view, @NonNull Inventory inventory) {

    }

    @Override
    public void onRender(@NonNull ViewerContext context, @NonNull InventoryView view, @NonNull Inventory inventory) {

    }
}
