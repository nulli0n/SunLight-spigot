package su.nightexpress.sunlight.module.warps.menu;

import static su.nightexpress.nightcore.util.text.night.wrapper.TagWrappers.BOLD;
import static su.nightexpress.nightcore.util.text.night.wrapper.TagWrappers.DARK_GRAY;
import static su.nightexpress.nightcore.util.text.night.wrapper.TagWrappers.GOLD;
import static su.nightexpress.nightcore.util.text.night.wrapper.TagWrappers.GRAY;
import static su.nightexpress.nightcore.util.text.night.wrapper.TagWrappers.GREEN;
import static su.nightexpress.nightcore.util.text.night.wrapper.TagWrappers.ORANGE;
import static su.nightexpress.nightcore.util.text.night.wrapper.TagWrappers.RED;
import static su.nightexpress.nightcore.util.text.night.wrapper.TagWrappers.UNDERLINED;
import static su.nightexpress.nightcore.util.text.night.wrapper.TagWrappers.WHITE;
import static su.nightexpress.sunlight.SLPlaceholders.GENERIC_PAGE;
import static su.nightexpress.sunlight.SLPlaceholders.GENERIC_SLOT;
import static su.nightexpress.sunlight.SLPlaceholders.GENERIC_STATE;
import static su.nightexpress.sunlight.SLPlaceholders.GENERIC_VALUE;
import static su.nightexpress.sunlight.module.warps.WarpsPlaceholders.WARP_DESCRIPTION;
import static su.nightexpress.sunlight.module.warps.WarpsPlaceholders.WARP_NAME;

import java.util.List;
import java.util.stream.IntStream;

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
import su.nightexpress.nightcore.core.config.CoreLang;
import su.nightexpress.nightcore.ui.dialog.wrap.DialogKey;
import su.nightexpress.nightcore.ui.inventory.action.ObjectActionContext;
import su.nightexpress.nightcore.ui.inventory.item.ItemState;
import su.nightexpress.nightcore.ui.inventory.item.MenuItem;
import su.nightexpress.nightcore.ui.inventory.menu.AbstractObjectMenu;
import su.nightexpress.nightcore.ui.inventory.viewer.ViewerContext;
import su.nightexpress.nightcore.util.ArrayUtil;
import su.nightexpress.nightcore.util.Lists;
import su.nightexpress.nightcore.util.bukkit.NightItem;
import su.nightexpress.sunlight.module.warps.Warp;
import su.nightexpress.sunlight.module.warps.WarpsModule;
import su.nightexpress.sunlight.module.warps.dialog.WarpsDialogKeys;

public class WarpOptionsMenu extends AbstractObjectMenu<Warp> {

    private final WarpsModule manager;

    public WarpOptionsMenu(@NonNull WarpsModule manager) {
        super(MenuType.GENERIC_9X5, "Warp Settings", Warp.class);
        this.manager = manager;
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

        this.addDefaultButton("back", MenuItem.button()
            .defaultState(ItemState.builder()
                .icon(NightItem.fromType(Material.COMPASS).setDisplayName(GREEN.wrap("Back to Warps")))
                .action(this.createObjectAction(this::backToWarps))
                .build()
            )
            .slots(40)
            .build()
        );

        this.addDefaultButton("name", MenuItem.button()
            .defaultState(ItemState.builder()
                .icon(NightItem.fromType(Material.NAME_TAG)
                    .setDisplayName(GOLD.and(BOLD).wrap("Name"))
                    .setLore(Lists.newList(
                        DARK_GRAY.wrap("» " + GRAY.wrap("Current: ") + WHITE.wrap(WARP_NAME)),
                        "",
                        GOLD.wrap("→ " + UNDERLINED.wrap("Click to edit"))
                    ))
                    .hideAllComponents()
                )
                .displayModifier((context, item) -> item.replace(builder -> builder.with(this.getObject(context)
                    .placeholders())))
                .action(this.createObjectAction(this::editName))
                .build()
            )
            .slots(20)
            .build()
        );

        this.addDefaultButton("description", MenuItem.button()
            .defaultState(ItemState.builder()
                .icon(NightItem.fromType(Material.WRITABLE_BOOK)
                    .setDisplayName(GOLD.and(BOLD).wrap("Description"))
                    .setLore(Lists.newList(
                        WARP_DESCRIPTION,
                        "",
                        GOLD.wrap("→ " + UNDERLINED.wrap("Click to edit"))
                    ))
                    .hideAllComponents()
                )
                .displayModifier((context, item) -> item.replace(builder -> builder.with(this.getObject(context)
                    .placeholders())))
                .action(this.createObjectAction(this::editDescription))
                .build()
            )
            .slots(21)
            .build()
        );

        this.addDefaultButton("icon", MenuItem.button()
            .defaultState(ItemState.builder()
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

        this.addDefaultButton("permission", MenuItem.button()
            .defaultState(ItemState.builder()
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
                    .with(GENERIC_STATE, () -> CoreLang.STATE_YES_NO.get(this.getObject(context)
                        .isPermissionRequired()))
                ))
                .action(this.createObjectAction(this::editPermission))
                .build()
            )
            .slots(23)
            .build()
        );

        this.addDefaultButton("slots", MenuItem.button()
            .defaultState(ItemState.builder()
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

        this.addDefaultButton("command", MenuItem.button()
            .defaultState(ItemState.builder()
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
        this.plugin.showDialog(context.getPlayer(), key, context.getObject(), () -> context.getViewer().refresh());
    }

    @Override
    protected void onLoad(@NonNull FileConfig config) {

    }

    @Override
    protected void onClick(@NonNull ViewerContext context, @NonNull InventoryClickEvent event) {
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
