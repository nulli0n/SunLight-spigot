package su.nightexpress.sunlight.module.kits.editor;

import static su.nightexpress.nightcore.util.text.night.wrapper.TagWrappers.BR;
import static su.nightexpress.nightcore.util.text.night.wrapper.TagWrappers.SOFT_RED;
import static su.nightexpress.sunlight.SLPlaceholders.GENERIC_STATE;
import static su.nightexpress.sunlight.SLPlaceholders.GENERIC_TIME;
import static su.nightexpress.sunlight.SLPlaceholders.GENERIC_VALUE;
import static su.nightexpress.sunlight.module.kits.KitsPlaceholders.KIT_DESCRIPTION;
import static su.nightexpress.sunlight.module.kits.KitsPlaceholders.KIT_NAME;

import java.util.List;
import java.util.concurrent.TimeUnit;

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
import su.nightexpress.nightcore.locale.LangContainer;
import su.nightexpress.nightcore.locale.LangEntry;
import su.nightexpress.nightcore.locale.entry.IconLocale;
import su.nightexpress.nightcore.ui.inventory.item.ItemState;
import su.nightexpress.nightcore.ui.inventory.item.MenuItem;
import su.nightexpress.nightcore.ui.inventory.menu.AbstractObjectMenu;
import su.nightexpress.nightcore.ui.inventory.viewer.ViewerContext;
import su.nightexpress.nightcore.util.NumberUtil;
import su.nightexpress.nightcore.util.bukkit.NightItem;
import su.nightexpress.nightcore.util.time.TimeFormatType;
import su.nightexpress.nightcore.util.time.TimeFormats;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.module.kits.KitsModule;
import su.nightexpress.sunlight.module.kits.config.KitsLang;
import su.nightexpress.sunlight.module.kits.dialog.KitDialogKeys;
import su.nightexpress.sunlight.module.kits.model.Kit;

public class KitSettingsEditorMenu extends AbstractObjectMenu<Kit> implements LangContainer {

    public static final IconLocale ICON_NAME = LangEntry.iconBuilder("Kits.UI.Editor.KitSettings.Name")
        .name("Display Name")
        .appendCurrent("Current", KIT_NAME).br()
        .appendClick("Click to change")
        .build();

    public static final IconLocale ICON_DESCRIPTION = LangEntry.iconBuilder("Kits.UI.Editor.KitSettings.Description")
        .name("Description")
        .rawLore(KIT_DESCRIPTION).br()
        .appendClick("Click to change")
        .build();

    public static final IconLocale ICON_ICON = LangEntry.iconBuilder("Kits.UI.Editor.KitSettings.Icon")
        .name("Icon")
        .appendCurrent("Icon Mode", GENERIC_STATE).br()
        .appendInfo("Enable the icon mode", "and click item in inventory", "to replace kit icon.").br()
        .appendClick("Click to toggle")
        .build();

    public static final IconLocale ICON_PERMISSION = LangEntry.iconBuilder("Kits.UI.Editor.KitSettings.Permission")
        .name("Permission Requirement")
        .appendCurrent("Required", GENERIC_STATE)
        .appendCurrent("Node", GENERIC_VALUE)
        .br()
        .appendInfo("Whether permission is requried", "to use this kit.")
        .br()
        .appendClick("Click to toggle")
        .build();

    public static final IconLocale ICON_COOLDOWN = LangEntry.iconBuilder("Kits.UI.Editor.KitSettings.Cooldown")
        .name("Cooldown")
        .appendCurrent("Current", GENERIC_TIME).br()
        .appendInfo("Sets the kit cooldown.").br()
        .appendClick("Click to change")
        .build();

    public static final IconLocale ICON_COST = LangEntry.iconBuilder("Kits.UI.Editor.KitSettings.Cost")
        .name("Cost")
        .appendCurrent("Current", GENERIC_VALUE).br()
        .appendInfo("Sets how much player must pay", "to use this kit.").br()
        .appendClick("Click to change")
        .build();

    public static final IconLocale ICON_PRIORITY = LangEntry.iconBuilder("Kits.UI.Editor.KitSettings.Priority")
        .name("Priority")
        .appendCurrent("Current", GENERIC_VALUE).br()
        .appendInfo("Kits with higher priority", "appears first in the GUI.").br()
        .appendClick("Click to change")
        .build();

    public static final IconLocale ICON_COMMANDS = LangEntry.iconBuilder("Kits.UI.Editor.KitSettings.Commands")
        .name("Commands")
        .rawLore(GENERIC_VALUE).br()
        .appendInfo("Runs listed commands when", "player receives this kit.")
        .br()
        .appendClick("Click to change")
        .build();

    public static final IconLocale ICON_INVENTORY = LangEntry.iconBuilder("Kits.UI.Editor.KitSettings.Inventory")
        .name("Inventory Content")
        .appendClick("Click to edit")
        .build();

    public static final IconLocale ICON_RETURN = LangEntry.iconBuilder("Kits.UI.Editor.KitSettings.Return")
        .accentColor(SOFT_RED)
        .name("Back to Kits")
        .appendClick("Click to return")
        .build();

    private final KitsModule module;

    private boolean iconMode;

    public KitSettingsEditorMenu(@NonNull SunLightPlugin plugin, @NonNull KitsModule module) {
        super(MenuType.GENERIC_9X5, KitsLang.EDITOR_TITLE_SETTINGS.text(), Kit.class);
        this.module = module;

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
        this.addDefaultButton("return", MenuItem.button()
            .defaultState(NightItem.fromType(Material.ARROW).localized(ICON_RETURN), context -> {
                this.module.openEditor(context.getPlayer());
            })
            .slots(40)
            .build()
        );

        this.addDefaultButton("kit_name", MenuItem.button()
            .defaultState(ItemState.builder()
                .icon(NightItem.fromType(Material.NAME_TAG).localized(ICON_NAME))
                .displayModifier((context, item) -> item.replace(builder -> builder.with(this.getObject(context)
                    .placeholders())))
                .action(context -> {
                    this.plugin.showDialog(context.getPlayer(), KitDialogKeys.KIT_NAME, this.getObject(context),
                        () -> context.getViewer().refresh());
                })
                .build()
            )
            .slots(11)
            .build()
        );

        this.addDefaultButton("kit_description", MenuItem.button()
            .defaultState(ItemState.builder()
                .icon(NightItem.fromType(Material.WRITABLE_BOOK).localized(ICON_DESCRIPTION))
                .displayModifier((context, item) -> item.replace(builder -> builder.with(this.getObject(context)
                    .placeholders())))
                .action(context -> {
                    this.plugin.showDialog(context.getPlayer(), KitDialogKeys.KIT_DESCRIPTION, this.getObject(context),
                        () -> context.getViewer().refresh());
                })
                .build()
            )
            .slots(12)
            .build()
        );


        this.addDefaultButton("kit_priority", MenuItem.button()
            .defaultState(ItemState.builder()
                .icon(NightItem.fromType(Material.COMPARATOR).localized(ICON_PRIORITY))
                .displayModifier((context, item) -> item.replace(builder -> builder
                    .with(GENERIC_VALUE, () -> String.valueOf(this.getObject(context).definition().getPriority())))
                )
                .action(context -> {
                    this.plugin.showDialog(context.getPlayer(), KitDialogKeys.KIT_PRIORITY, this.getObject(context),
                        () -> context.getViewer().refresh());
                })
                .build()
            )
            .slots(14)
            .build()
        );

        this.addDefaultButton("kit_permission", MenuItem.button()
            .defaultState(ItemState.builder()
                .icon(NightItem.fromType(Material.REDSTONE).localized(ICON_PERMISSION))
                .displayModifier((context, item) -> item.replace(builder -> builder
                    .with(GENERIC_STATE, () -> CoreLang.STATE_ENABLED_DISALBED.get(this.getObject(context).definition()
                        .isPermissionRequired()))
                    .with(GENERIC_VALUE, () -> this.getObject(context).getPermission())
                ))
                .action(context -> {
                    Kit kit = this.getObject(context);
                    kit.definition().setPermissionRequired(!kit.definition().isPermissionRequired());
                    kit.markDirty();
                    context.getViewer().refresh();
                })
                .build()
            )
            .slots(15)
            .build()
        );

        this.addDefaultButton("kit_cost", MenuItem.button()
            .defaultState(ItemState.builder()
                .icon(NightItem.fromType(Material.GOLD_NUGGET).localized(ICON_COST))
                .displayModifier((context, item) -> item.replace(builder -> builder
                    .with(GENERIC_VALUE, () -> NumberUtil.format(this.getObject(context).definition().getCost()))
                ))
                .action(context -> {
                    this.plugin.showDialog(context.getPlayer(), KitDialogKeys.KIT_COST, this.getObject(context),
                        () -> context.getViewer().refresh());
                })
                .build()
            )
            .slots(20)
            .build()
        );

        this.addDefaultButton("kit_cooldown", MenuItem.button()
            .defaultState(ItemState.builder()
                .icon(NightItem.fromType(Material.CLOCK).localized(ICON_COOLDOWN))
                .displayModifier((context, item) -> item.replace(builder -> builder
                    .with(GENERIC_TIME, () -> TimeFormats.formatAmount(TimeUnit.SECONDS.toMillis(this.getObject(context)
                        .definition().getCooldown()), TimeFormatType.LITERAL))
                ))
                .action(context -> {
                    this.plugin.showDialog(context.getPlayer(), KitDialogKeys.KIT_COOLDOWN, this.getObject(context),
                        () -> context.getViewer().refresh());
                })
                .build()
            )
            .slots(21)
            .build()
        );

        this.addDefaultButton("commands", MenuItem.button()
            .defaultState(ItemState.builder()
                .icon(NightItem.fromType(Material.COMMAND_BLOCK).localized(ICON_COMMANDS))
                .displayModifier((context, item) -> item.replace(builder -> builder
                    .with(GENERIC_VALUE, () -> String.join(BR, this.getObject(context).definition().getCommands()))
                ))
                .action(context -> {
                    this.plugin.showDialog(context.getPlayer(), KitDialogKeys.KIT_COMMANDS, this.getObject(context),
                        () -> context.getViewer().refresh());
                })
                .build()
            )
            .slots(22)
            .build()
        );

        this.addDefaultButton("inventory_content", MenuItem.button()
            .defaultState(ItemState.builder()
                .icon(NightItem.fromType(Material.CHEST_MINECART).localized(ICON_INVENTORY))
                .displayModifier((context, item) -> item.replace(builder -> builder.with(this.getObject(context)
                    .placeholders())))
                .action(context -> {
                    this.module.openContentEditor(context.getPlayer(), this.getObject(context));
                })
                .build()
            )
            .slots(24)
            .build()
        );
    }

    @Override
    protected void onLoad(@NonNull FileConfig config) {

    }

    @Override
    protected void onClick(@NonNull ViewerContext context, @NonNull InventoryClickEvent event) {
        if (this.iconMode) {
            if (event.getRawSlot() <= event.getInventory().getSize()) return;

            ItemStack itemStack = event.getCurrentItem();
            if (itemStack == null || itemStack.getType().isAir()) return;

            Kit kit = this.getObject(context);
            kit.definition().setIcon(NightItem.fromItemStack(itemStack));
            kit.markDirty();
            this.iconMode = false;
            context.getViewer().refresh();
        }
    }

    @Override
    protected void onDrag(@NonNull ViewerContext context, @NonNull InventoryDragEvent event) {

    }

    @Override
    protected void onClose(@NonNull ViewerContext context, @NonNull InventoryCloseEvent event) {
        this.iconMode = false;
    }

    @Override
    public void onPrepare(@NonNull ViewerContext context, @NonNull InventoryView view, @NonNull Inventory inventory, @NonNull List<MenuItem> items) {
        items.add(MenuItem.builder()
            .defaultState(ItemState.defaultBuilder()
                .icon(this.getObject(context).definition().getIcon().localized(ICON_ICON))
                .displayModifier((viewerContext, item) -> item.replace(builder -> builder.with(GENERIC_STATE,
                    () -> CoreLang.STATE_ENABLED_DISALBED.get(this.iconMode))))
                .action(actionContext -> {
                    this.iconMode = !this.iconMode;
                    context.getViewer().refresh();
                })
                .build()
            )
            .slots(13)
            .build()
        );
    }

    @Override
    public void onReady(@NonNull ViewerContext context, @NonNull InventoryView view, @NonNull Inventory inventory) {

    }

    @Override
    public void onRender(@NonNull ViewerContext context, @NonNull InventoryView view, @NonNull Inventory inventory) {

    }
}
