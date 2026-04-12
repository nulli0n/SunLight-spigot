package su.nightexpress.sunlight.module.spawns.editor;

import static su.nightexpress.nightcore.util.text.night.wrapper.TagWrappers.BLUE;
import static su.nightexpress.nightcore.util.text.night.wrapper.TagWrappers.GREEN;
import static su.nightexpress.nightcore.util.text.night.wrapper.TagWrappers.RED;
import static su.nightexpress.nightcore.util.text.night.wrapper.TagWrappers.WHITE;
import static su.nightexpress.sunlight.module.spawns.SpawnsPlaceholders.SPAWN_WORLD;
import static su.nightexpress.sunlight.module.spawns.SpawnsPlaceholders.SPAWN_X;
import static su.nightexpress.sunlight.module.spawns.SpawnsPlaceholders.SPAWN_Y;
import static su.nightexpress.sunlight.module.spawns.SpawnsPlaceholders.SPAWN_Z;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
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
import su.nightexpress.nightcore.util.bukkit.NightItem;
import su.nightexpress.sunlight.SLPlaceholders;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.module.spawns.Spawn;
import su.nightexpress.sunlight.module.spawns.SpawnsModule;
import su.nightexpress.sunlight.module.spawns.config.SpawnsLang;
import su.nightexpress.sunlight.module.spawns.dialog.SpawnsDialogKeys;

public class SpawnSettingsEditor extends AbstractObjectMenu<Spawn> implements LangContainer {

    // TODO Better texts in icons and dialogs

    private static final IconLocale ICON_RETURN = LangEntry.iconBuilder("Spawns.Editor.Spawn.Return")
        .accentColor(GREEN)
        .name("To Spawns")
        .build();

    private static final IconLocale ICON_RESPAWN_RULES = LangEntry.iconBuilder("Spawns.Editor.Spawn.RespawnRules")
        .name("Respawn Rules")
        .appendCurrent("Status", SLPlaceholders.GENERIC_STATE).br()
        .appendInfo("Rules controlling which players", "should be moved to this spawn", "on respawn.").br()
        .appendClick("Click to edit")
        .build();

    private static final IconLocale ICON_LOGIN_RULES = LangEntry.iconBuilder("Spawns.Editor.Spawn.LoginRules")
        .name("Login Rules")
        .appendCurrent("Status", SLPlaceholders.GENERIC_STATE).br()
        .appendInfo("Rules controlling which players", "should be moved to this spawn", "on join.").br()
        .appendClick("Click to edit")
        .build();

    private static final IconLocale ICON_PRIORITY = LangEntry.iconBuilder("Spawns.Editor.Spawn.Priority")
        .name("Priority")
        .appendCurrent("Current", SLPlaceholders.GENERIC_VALUE).br()
        .appendInfo("Spawns with greater priority", "will comes first.").br()
        .appendClick("Click to change")
        .build();

    private static final IconLocale ICON_PERMISSION = LangEntry.iconBuilder("Spawns.Editor.Spawn.PermissionRequirement")
        .name("Permission Requirement")
        .appendCurrent("Status", SLPlaceholders.GENERIC_STATE).br()
        .appendInfo("Whether permission is required", "to access this spawn.").br()
        .appendClick("Click to toggle")
        .build();

    private static final IconLocale ICON_LOCATION = LangEntry.iconBuilder("Spawns.Editor.Spawn.Location")
        .name("Location")
        .appendCurrent("Current", RED.wrap(SPAWN_X) + ", " + GREEN.wrap(SPAWN_Y) + ", " + BLUE.wrap(SPAWN_Z) + " @ " +
            WHITE.wrap(SPAWN_WORLD)).br()
        .appendClick("Click to override")
        .build();

    private static final IconLocale ICON_NAME = LangEntry.iconBuilder("Spawns.Editor.Spawn.Name")
        .name("Name")
        .appendCurrent("Current", SLPlaceholders.GENERIC_NAME).br()
        .appendClick("Click to change")
        .build();

    private final SpawnsModule module;

    public SpawnSettingsEditor(@NonNull SunLightPlugin plugin, @NonNull SpawnsModule module) {
        super(MenuType.GENERIC_9X4, SpawnsLang.EDITOR_TITLE_SETTINGS.text(), Spawn.class);
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
            .defaultState(ItemState.builder()
                .icon(NightItem.fromType(Material.ENDER_EYE).localized(ICON_RETURN))
                .action(context -> this.module.openEditor(context.getPlayer()))
                .build()
            )
            .slots(31)
            .build()
        );

        this.addDefaultButton("name", MenuItem.button()
            .defaultState(ItemState.builder()
                .icon(NightItem.fromType(Material.NAME_TAG).localized(ICON_NAME))
                .displayModifier((context, item) -> item.replace(builder -> builder
                    .with(SLPlaceholders.GENERIC_NAME, () -> this.getObject(context).getName())
                ))
                .action(context -> {
                    this.plugin.showDialog(context.getPlayer(), SpawnsDialogKeys.SPAWN_NAME, this.getObject(context),
                        () -> context.getViewer().refresh());
                })
                .build()
            )
            .slots(10)
            .build()
        );

        this.addDefaultButton("permission", MenuItem.button()
            .defaultState(ItemState.builder()
                .icon(NightItem.fromType(Material.REDSTONE).localized(ICON_PERMISSION))
                .displayModifier((context, item) -> item.replace(builder -> builder
                    .with(SLPlaceholders.GENERIC_STATE, () -> CoreLang.STATE_ENABLED_DISALBED.get(this.getObject(
                        context).isPermissionRequired()))
                ))
                .action(context -> {
                    Spawn spawn = this.getObject(context);
                    spawn.setPermissionRequired(!spawn.isPermissionRequired());
                    spawn.markDirty();
                    context.getViewer().refresh();
                })
                .build()
            )
            .slots(11)
            .build()
        );

        this.addDefaultButton("priority", MenuItem.button()
            .defaultState(ItemState.builder()
                .icon(NightItem.fromType(Material.COMPARATOR).localized(ICON_PRIORITY))
                .displayModifier((context, item) -> item.replace(builder -> builder
                    .with(SLPlaceholders.GENERIC_VALUE, () -> String.valueOf(this.getObject(context).getPriority()))
                ))
                .action(context -> {
                    this.plugin.showDialog(context.getPlayer(), SpawnsDialogKeys.SPAWN_PRIORITY, this.getObject(
                        context), () -> context.getViewer().refresh());
                })
                .build()
            )
            .slots(12)
            .build()
        );

        this.addDefaultButton("location", MenuItem.button()
            .defaultState(ItemState.builder()
                .icon(NightItem.fromType(Material.COMPASS).localized(ICON_LOCATION))
                .displayModifier((context, item) -> item.replace(builder -> builder.with(this.getObject(context)
                    .placeholders())))
                .action(context -> {
                    Spawn spawn = this.getObject(context);
                    spawn.setLocation(context.getPlayer().getLocation());
                    spawn.markDirty();
                    context.getViewer().refresh();
                })
                .build()
            )
            .slots(13)
            .build()
        );

        this.addDefaultButton("login_rules", MenuItem.button()
            .defaultState(ItemState.builder()
                .icon(NightItem.fromType(Material.IRON_DOOR).localized(ICON_LOGIN_RULES))
                .displayModifier((context, item) -> item.replace(builder -> builder
                    .with(SLPlaceholders.GENERIC_STATE, () -> CoreLang.STATE_ENABLED_DISALBED.get(this.getObject(
                        context).getLoginRule().isEnabled()))
                ))
                .action(context -> {
                    this.plugin.showDialog(context.getPlayer(), SpawnsDialogKeys.SPAWN_LOGIN_RULES, this.getObject(
                        context), () -> context.getViewer().refresh());
                })
                .build()
            )
            .slots(14)
            .build()
        );

        this.addDefaultButton("respawn_rules", MenuItem.button()
            .defaultState(ItemState.builder()
                .icon(NightItem.fromType(Material.RED_BED).localized(ICON_RESPAWN_RULES))
                .displayModifier((context, item) -> item.replace(builder -> builder
                    .with(SLPlaceholders.GENERIC_STATE, () -> CoreLang.STATE_ENABLED_DISALBED.get(this.getObject(
                        context).getRespawnRule().isEnabled()))
                ))
                .action(context -> {
                    this.plugin.showDialog(context.getPlayer(), SpawnsDialogKeys.SPAWN_RESPAWN_RULES, this.getObject(
                        context), () -> context.getViewer().refresh());
                })
                .build()
            )
            .slots(15)
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

    }

    @Override
    public void onReady(@NonNull ViewerContext context, @NonNull InventoryView view, @NonNull Inventory inventory) {

    }

    @Override
    public void onRender(@NonNull ViewerContext context, @NonNull InventoryView view, @NonNull Inventory inventory) {

    }
}
