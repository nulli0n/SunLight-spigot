package su.nightexpress.sunlight.module.homes.menu;

import static su.nightexpress.nightcore.util.text.night.wrapper.TagWrappers.BOLD;
import static su.nightexpress.nightcore.util.text.night.wrapper.TagWrappers.GOLD;
import static su.nightexpress.nightcore.util.text.night.wrapper.TagWrappers.GRAY;
import static su.nightexpress.nightcore.util.text.night.wrapper.TagWrappers.GREEN;
import static su.nightexpress.nightcore.util.text.night.wrapper.TagWrappers.ORANGE;
import static su.nightexpress.nightcore.util.text.night.wrapper.TagWrappers.RED;
import static su.nightexpress.nightcore.util.text.night.wrapper.TagWrappers.UNDERLINED;
import static su.nightexpress.nightcore.util.text.night.wrapper.TagWrappers.WHITE;
import static su.nightexpress.nightcore.util.text.night.wrapper.TagWrappers.YELLOW;
import static su.nightexpress.sunlight.module.homes.HomePlaceholders.HOME_FAVORITE;
import static su.nightexpress.sunlight.module.homes.HomePlaceholders.HOME_INVITED_PLAYERS;
import static su.nightexpress.sunlight.module.homes.HomePlaceholders.HOME_NAME;
import static su.nightexpress.sunlight.module.homes.HomePlaceholders.HOME_TYPE;

import java.util.List;
import java.util.stream.IntStream;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.MenuType;
import org.jspecify.annotations.NonNull;

import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.ui.inventory.action.ActionContext;
import su.nightexpress.nightcore.ui.inventory.item.ItemState;
import su.nightexpress.nightcore.ui.inventory.item.MenuItem;
import su.nightexpress.nightcore.ui.inventory.menu.AbstractObjectMenu;
import su.nightexpress.nightcore.ui.inventory.viewer.ViewerContext;
import su.nightexpress.nightcore.util.Lists;
import su.nightexpress.nightcore.util.bukkit.NightItem;
import su.nightexpress.nightcore.util.text.night.wrapper.TagWrappers;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.module.homes.HomesFiles;
import su.nightexpress.sunlight.module.homes.HomesModule;
import su.nightexpress.sunlight.module.homes.dialog.HomeDialogKeys;
import su.nightexpress.sunlight.module.homes.impl.Home;

public class HomeSettingsMenu extends AbstractObjectMenu<Home> {

    private final HomesModule module;

    public HomeSettingsMenu(@NonNull SunLightPlugin plugin, @NonNull HomesModule module) {
        super(MenuType.GENERIC_9X5, "Home Settings", Home.class);
        this.module = module;

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

        this.addDefaultButton("return", MenuItem.button()
            .defaultState(ItemState.builder()
                .icon(NightItem.fromType(Material.SPECTRAL_ARROW)
                    .setDisplayName(WHITE.wrap("Back to Homes"))
                )
                .action(context -> this.module.openHomes(context.getPlayer(), this.getObject(context).getOwner().id()))
                .build()
            )
            .slots(36)
            .build()
        );

        this.addDefaultButton("name", MenuItem.button()
            .defaultState(ItemState.builder()
                .icon(NightItem.fromType(Material.NAME_TAG)
                    .setDisplayName(YELLOW.and(BOLD).wrap("Display Name"))
                    .setLore(Lists.newList(
                        WHITE.wrap("» ") + GRAY.wrap("Current: ") + YELLOW.wrap(HOME_NAME),
                        "",
                        YELLOW.wrap("→ " + UNDERLINED.wrap("Click to change"))
                    ))
                )
                .displayModifier((context, item) -> item.replace(builder -> builder.with(this.getObject(context)
                    .placeholders())))
                .action(context -> {
                    this.plugin.showDialog(context.getPlayer(), HomeDialogKeys.HOME_NAME, this.getObject(context),
                        () -> context.getViewer().refresh());
                })
                .build()
            )
            .slots(19)
            .build()
        );

        this.addDefaultButton("type", MenuItem.button()
            .defaultState(ItemState.builder()
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
                .displayModifier((context, item) -> item.replace(builder -> builder.with(this.getObject(context)
                    .placeholders())))
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

        this.addDefaultButton("favorite", MenuItem.button()
            .defaultState(ItemState.builder()
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
                .displayModifier((context, item) -> item.replace(builder -> builder.with(this.getObject(context)
                    .placeholders())))
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

        this.addDefaultButton("icon", MenuItem.button()
            .defaultState(ItemState.builder()
                .icon(NightItem.fromType(Material.ITEM_FRAME)
                    .setDisplayName(YELLOW.and(BOLD).wrap("Icon"))
                    .setLore(Lists.newList(
                        GRAY.wrap("Select an icon for your home!"),
                        "",
                        YELLOW.wrap("→ " + UNDERLINED.wrap("Click to change"))
                    ))
                )
                .displayModifier((context, item) -> item.replace(builder -> builder.with(this.getObject(context)
                    .placeholders())))
                .action(context -> this.module.openIconSelection(context.getPlayer(), this.getObject(context)))
                .build()
            )
            .slots(23)
            .build()
        );

        this.addDefaultButton("invited_players", MenuItem.button()
            .defaultState(ItemState.builder()
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
                .displayModifier((context, item) -> item.replace(builder -> builder.with(this.getObject(context)
                    .placeholders())))
                .action(context -> this.module.openInvitedPlayersMenu(context.getPlayer(), this.getObject(context)))
                .build()
            )
            .slots(25)
            .build()
        );

        this.addDefaultButton("delete", MenuItem.button()
            .defaultState(ItemState.builder()
                .icon(NightItem.fromType(Material.BARRIER)
                    .setDisplayName(RED.and(BOLD).wrap("Delete Home"))
                    .setLore(Lists.newList(
                        GRAY.wrap("Permanently delete this home."),
                        "",
                        RED.wrap("→ " + UNDERLINED.wrap("Click to delete"))
                    ))
                )
                .action(this::handleDelete)
                .build()
            )
            .slots(44)
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

    private void handleDelete(@NonNull ActionContext context) {
        Home home = this.getObject(context);
        Player player = context.getPlayer();

        this.plugin.showDialog(player, HomeDialogKeys.HOME_DELETION, home, () -> this.module.openHomes(player));
    }
}
