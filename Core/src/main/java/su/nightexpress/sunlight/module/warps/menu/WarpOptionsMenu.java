package su.nightexpress.sunlight.module.warps.menu;

import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MenuType;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.core.config.CoreLang;
import su.nightexpress.nightcore.ui.inventory.action.ObjectActionContext;
import su.nightexpress.nightcore.ui.inventory.item.ItemState;
import su.nightexpress.nightcore.ui.inventory.item.MenuItem;
import su.nightexpress.nightcore.ui.inventory.menu.AbstractObjectMenu;
import su.nightexpress.nightcore.ui.inventory.viewer.ViewerContext;
import su.nightexpress.nightcore.util.ArrayUtil;
import su.nightexpress.nightcore.util.Lists;
import su.nightexpress.nightcore.util.bukkit.NightItem;
import su.nightexpress.sunlight.dialog.DialogKey;
import su.nightexpress.sunlight.dialog.DialogRegistry;
import su.nightexpress.sunlight.module.warps.Warp;
import su.nightexpress.sunlight.module.warps.WarpsModule;
import su.nightexpress.sunlight.module.warps.dialog.WarpsDialogKeys;

import java.util.List;
import java.util.stream.IntStream;

import static su.nightexpress.nightcore.util.text.night.wrapper.TagWrappers.*;
import static su.nightexpress.sunlight.SLPlaceholders.*;
import static su.nightexpress.sunlight.module.warps.WarpsPlaceholders.*;

public class WarpOptionsMenu extends AbstractObjectMenu<Warp> {

    private final   WarpsModule    manager;
    protected final DialogRegistry dialogRegistry;

    public WarpOptionsMenu(@NonNull WarpsModule manager, @NonNull DialogRegistry dialogRegistry) {
        super(MenuType.GENERIC_9X5, "Warp Settings", Warp.class);
        this.manager = manager;
        this.dialogRegistry = dialogRegistry;
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
                .icon(NightItem.fromType(Material.COMPASS).setDisplayName(GREEN.wrap("Back to Warps")))
                .action(this.createObjectAction(this::backToWarps))
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
                .action(this.createObjectAction(this::editName))
                .build()
            )
            .slots(20)
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
                .action(this.createObjectAction(this::editDescription))
                .build()
            )
            .slots(21)
            .build()
        );

        this.addDefaultButton("icon", MenuItem.builder()
            .defaultState(ItemState.defaultBuilder()
                .icon(NightItem.fromType(Material.PAINTING)
                    .setDisplayName(GOLD.and(BOLD).wrap("Icon"))
                    .setLore(Lists.newList(
                        GRAY.wrap("Right click an item in your"),
                        GRAY.wrap("inventory to replace the icon.")
                    ))
                    .hideAllComponents()
                )
                .displayModifier((context, item) -> item.setMaterial(this.getObject(context).getIcon().getMaterial()))
                //.action(this.iconAction)
                .build()
            )
            .slots(22)
            .build()
        );

        this.addDefaultButton("permission", MenuItem.builder()
            .defaultState(ItemState.defaultBuilder()
                .icon(NightItem.fromType(Material.REDSTONE)
                    .setDisplayName(RED.and(BOLD).wrap("Permission Requirement"))
                    .setLore(Lists.newList(
                        DARK_GRAY.wrap("» " + GRAY.wrap("Enabled: ") + WHITE.wrap(GENERIC_STATE)),
                        "",
                        RED.wrap("→ " + UNDERLINED.wrap("Click to toggle"))
                    ))
                    .hideAllComponents()
                )
                .displayModifier((context, item) -> item.replace(builder -> builder
                    .with(GENERIC_STATE, () -> CoreLang.STATE_YES_NO.get(this.getObject(context).isPermissionRequired()))
                ))
                .action(this.createObjectAction(this::editPermission))
                .build()
            )
            .slots(23)
            .build()
        );

        this.addDefaultButton("slots", MenuItem.builder()
            .defaultState(ItemState.defaultBuilder()
                .icon(NightItem.fromType(Material.ITEM_FRAME)
                    .setDisplayName(GOLD.and(BOLD).wrap("Page & Slots"))
                    .setLore(Lists.newList(
                        DARK_GRAY.wrap("» " + GRAY.wrap("Page: ") + WHITE.wrap(GENERIC_PAGE)),
                        DARK_GRAY.wrap("» " + GRAY.wrap("Slots: ") + WHITE.wrap(GENERIC_SLOT)),
                        "",
                        GOLD.wrap("→ " + UNDERLINED.wrap("Click to edit"))
                    ))
                    .hideAllComponents()
                )
                .displayModifier((context, item) -> item.replace(builder -> builder
                    .with(GENERIC_PAGE, () -> String.valueOf(this.getObject(context).getMenuPage()))
                    .with(GENERIC_SLOT, () -> ArrayUtil.arrayToString(this.getObject(context).getMenuSlots()))
                ))
                .action(this.createObjectAction(this::editSlots))
                .build()
            )
            .slots(24)
            .build()
        );

        this.addDefaultButton("command", MenuItem.builder()
            .defaultState(ItemState.defaultBuilder()
                .icon(NightItem.fromType(Material.COMMAND_BLOCK)
                    .setDisplayName(ORANGE.and(BOLD).wrap("Command Alias"))
                    .setLore(Lists.newList(
                        DARK_GRAY.wrap("» " + GRAY.wrap("Enabled: ") + WHITE.wrap(GENERIC_STATE)),
                        DARK_GRAY.wrap("» " + GRAY.wrap("Current: ") + WHITE.wrap(GENERIC_VALUE)),
                        "",
                        ORANGE.wrap("→ " + UNDERLINED.wrap("Click to edit"))
                    ))
                    .hideAllComponents()
                )
                .displayModifier((context, item) -> item.replace(builder -> builder
                    .with(GENERIC_STATE, () -> CoreLang.STATE_YES_NO.get(this.getObject(context).isCommandEnabled()))
                    .with(GENERIC_VALUE, () -> "/" + this.getObject(context).getCommandLabel())
                ))
                .action(this.createObjectAction(this::editCommand))
                .build()
            )
            .slots(4)
            .build()
        );
    }

    protected void backToWarps(@NonNull ObjectActionContext<Warp> context) {
        this.manager.openWarpsMenu(context.getPlayer());
    }

    private void editPermission(@NonNull ObjectActionContext<Warp> context) {
        Warp warp = context.getObject();
        warp.setPermissionRequired(!warp.isPermissionRequired());
        warp.markDirty();
        context.getViewer().refresh();
    }

    private void editName(@NonNull ObjectActionContext<Warp> context) {
        this.openDialog(context, WarpsDialogKeys.WARP_NAME);
    }

    private void editDescription(@NonNull ObjectActionContext<Warp> context) {
        this.openDialog(context, WarpsDialogKeys.WARP_DESCRIPTION);
    }

    private void editSlots(@NonNull ObjectActionContext<Warp> context) {
        this.openDialog(context, WarpsDialogKeys.WARP_SLOTS);
    }

    private void editCommand(@NonNull ObjectActionContext<Warp> context) {
        this.openDialog(context, WarpsDialogKeys.WARP_COMMAND);
    }

    private void openDialog(@NonNull ObjectActionContext<Warp> context, @NonNull DialogKey<Warp> key) {
        this.dialogRegistry.show(context.getPlayer(), key, context.getObject(), () -> context.getViewer().refresh());
    }

    @Override
    protected void onLoad(@NotNull FileConfig config) {

    }

    @Override
    protected void onClick(@NotNull ViewerContext context, @NotNull InventoryClickEvent event) {
        if (event.isRightClick() && event.getRawSlot() >= event.getInventory().getSize()) {
            ItemStack itemStack = event.getCurrentItem();
            if (itemStack == null || itemStack.getType().isAir()) return;

            Warp warp = this.getObject(context);
            warp.setIcon(NightItem.fromItemStack(itemStack).hideAllComponents().ignoreNameAndLore());
            warp.markDirty();
            context.getViewer().refresh();
        }
    }

    @Override
    protected void onDrag(@NotNull ViewerContext context, @NotNull InventoryDragEvent event) {

    }

    @Override
    protected void onClose(@NotNull ViewerContext context, @NotNull InventoryCloseEvent event) {

    }

    @Override
    public void onPrepare(@NotNull ViewerContext context, @NotNull InventoryView view, @NotNull Inventory inventory, @NotNull List<MenuItem> items) {

    }

    @Override
    public void onReady(@NotNull ViewerContext context, @NotNull InventoryView view, @NotNull Inventory inventory) {

    }

    @Override
    public void onRender(@NotNull ViewerContext context, @NotNull InventoryView view, @NotNull Inventory inventory) {

    }
}
