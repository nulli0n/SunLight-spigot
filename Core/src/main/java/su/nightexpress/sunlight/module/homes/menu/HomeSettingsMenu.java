package su.nightexpress.sunlight.module.homes.menu;

import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.MenuType;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.ui.inventory.item.ItemState;
import su.nightexpress.nightcore.ui.inventory.item.MenuItem;
import su.nightexpress.nightcore.ui.inventory.menu.AbstractObjectMenu;
import su.nightexpress.nightcore.ui.inventory.viewer.ViewerContext;
import su.nightexpress.nightcore.util.Lists;
import su.nightexpress.nightcore.util.bukkit.NightItem;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.dialog.DialogRegistry;
import su.nightexpress.sunlight.module.homes.HomesFiles;
import su.nightexpress.sunlight.module.homes.HomesModule;
import su.nightexpress.sunlight.module.homes.dialog.HomeDialogKeys;
import su.nightexpress.sunlight.module.homes.impl.Home;

import java.util.List;
import java.util.stream.IntStream;

import static su.nightexpress.nightcore.util.text.night.wrapper.TagWrappers.*;
import static su.nightexpress.sunlight.module.homes.HomePlaceholders.*;

public class HomeSettingsMenu extends AbstractObjectMenu<Home> {

    private final HomesModule module;
    private final DialogRegistry dialogRegistry;

    public HomeSettingsMenu(@NotNull SunLightPlugin plugin, @NotNull HomesModule module, @NotNull DialogRegistry dialogRegistry) {
        super(MenuType.GENERIC_9X5, "Home Settings", Home.class);
        this.module = module;
        this.dialogRegistry = dialogRegistry;

        this.load(plugin, FileConfig.load(module.getLocalUIPath(), HomesFiles.FILE_UI_HOME_SETTINGS));
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

        this.addDefaultButton("return", MenuItem.builder()
            .defaultState(ItemState.defaultBuilder()
                .icon(NightItem.fromType(Material.IRON_DOOR)
                    .setDisplayName(WHITE.wrap("Return"))
                )
                .action(context -> this.module.openHomes(context.getPlayer(), this.getObject(context).getOwner().id()))
                .build()
            )
            .slots(40)
            .build()
        );

        this.addDefaultButton("name", MenuItem.builder()
            .defaultState(ItemState.defaultBuilder()
                .icon(NightItem.fromType(Material.NAME_TAG)
                    .setDisplayName(YELLOW.and(BOLD).wrap("Display Name"))
                    .setLore(Lists.newList(
                        WHITE.wrap("» ") + GRAY.wrap("Current: ") + YELLOW.wrap(HOME_NAME),
                        "",
                        YELLOW.wrap("→ " + UNDERLINED.wrap("Click to change"))
                    ))
                )
                .displayModifier((context, item) -> item.replace(builder -> builder.with(this.getObject(context).placeholders())))
                .action(context -> {
                    this.dialogRegistry.show(context.getPlayer(), HomeDialogKeys.HOME_NAME, this.getObject(context), () -> context.getViewer().refresh());
                })
                .build()
            )
            .slots(19)
            .build()
        );

        this.addDefaultButton("type", MenuItem.builder()
            .defaultState(ItemState.defaultBuilder()
                .icon(NightItem.fromType(Material.ENDER_EYE)
                    .setDisplayName(GREEN.and(BOLD).wrap("Type"))
                    .setLore(Lists.newList(
                        WHITE.wrap("» ") + GRAY.wrap("Current: ") + GREEN.wrap(HOME_TYPE),
                        "",
                        GRAY.wrap("Public homes are open for everyone."),
                        GRAY.wrap("Private homes requires an invite."),
                        "",
                        GREEN.wrap("→ " + UNDERLINED.wrap("Click to toggle"))
                    ))
                )
                .displayModifier((context, item) -> item.replace(builder -> builder.with(this.getObject(context).placeholders())))
                .action(context -> {
                    Home home = this.getObject(context);
                    home.setType(Lists.next(home.getType()));
                    home.markDirty();
                    context.getViewer().refresh();
                })
                .build()
            )
            .slots(21)
            .build()
        );

        this.addDefaultButton("favorite", MenuItem.builder()
            .defaultState(ItemState.defaultBuilder()
                .icon(NightItem.fromType(Material.NETHER_STAR)
                    .setDisplayName(GOLD.and(BOLD).wrap("Favorite"))
                    .setLore(Lists.newList(
                        WHITE.wrap("» ") + GRAY.wrap("Favorite: ") + GOLD.wrap(HOME_FAVORITE),
                        "",
                        GRAY.wrap("Favorite home is used by default"),
                        GRAY.wrap("in all home commands and as"),
                        GRAY.wrap("respawn point."),
                        "",
                        GOLD.wrap("→ " + UNDERLINED.wrap("Click to toggle"))
                    ))
                )
                .displayModifier((context, item) -> item.replace(builder -> builder.with(this.getObject(context).placeholders())))
                .action(context -> {
                    Home home = this.getObject(context);

                    Home oldFavorite = this.module.getFavoriteHome(context.getPlayer());
                    if (oldFavorite != null && oldFavorite != home) {
                        oldFavorite.setFavorite(false);
                        oldFavorite.markDirty();
                    }

                    home.setFavorite(!home.isFavorite());
                    home.markDirty();
                    context.getViewer().refresh();
                })
                .build()
            )
            .slots(4)
            .build()
        );

        this.addDefaultButton("icon", MenuItem.builder()
            .defaultState(ItemState.defaultBuilder()
                .icon(NightItem.fromType(Material.ITEM_FRAME)
                    .setDisplayName(YELLOW.and(BOLD).wrap("Icon"))
                    .setLore(Lists.newList(
                        GRAY.wrap("Select an icon for your home!"),
                        "",
                        YELLOW.wrap("→ " + UNDERLINED.wrap("Click to change"))
                    ))
                )
                .displayModifier((context, item) -> item.replace(builder -> builder.with(this.getObject(context).placeholders())))
                .action(context -> this.module.openIconSelection(context.getPlayer(), this.getObject(context)))
                .build()
            )
            .slots(23)
            .build()
        );

        this.addDefaultButton("invited_players", MenuItem.builder()
            .defaultState(ItemState.defaultBuilder()
                .icon(NightItem.fromType(Material.WRITABLE_BOOK)
                    .setDisplayName(ORANGE.and(BOLD).wrap("Invited Players"))
                    .setLore(Lists.newList(
                        WHITE.wrap("» ") + GRAY.wrap("Players Invited: ") + ORANGE.wrap(HOME_INVITED_PLAYERS),
                        "",
                        GRAY.wrap("Share your home with friends!"),
                        "",
                        ORANGE.wrap("→ " + UNDERLINED.wrap("Click to edit"))
                    ))
                )
                .condition(context -> this.getObject(context).isPrivate())
                .displayModifier((context, item) -> item.replace(builder -> builder.with(this.getObject(context).placeholders())))
                .action(context -> this.module.openInvitedPlayersMenu(context.getPlayer(), this.getObject(context)))
                .build()
            )
            .slots(25)
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

    }

    @Override
    public void onReady(@NotNull ViewerContext context, @NotNull InventoryView view, @NotNull Inventory inventory) {

    }

    @Override
    public void onRender(@NotNull ViewerContext context, @NotNull InventoryView view, @NotNull Inventory inventory) {

    }
}
