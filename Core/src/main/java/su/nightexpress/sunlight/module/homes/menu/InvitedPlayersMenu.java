package su.nightexpress.sunlight.module.homes.menu;

import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.MenuType;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.configuration.ConfigProperty;
import su.nightexpress.nightcore.configuration.ConfigTypes;
import su.nightexpress.nightcore.ui.inventory.item.ItemPopulator;
import su.nightexpress.nightcore.ui.inventory.item.ItemState;
import su.nightexpress.nightcore.ui.inventory.item.MenuItem;
import su.nightexpress.nightcore.ui.inventory.menu.AbstractObjectMenu;
import su.nightexpress.nightcore.ui.inventory.viewer.ViewerContext;
import su.nightexpress.nightcore.user.UserInfo;
import su.nightexpress.nightcore.util.bukkit.NightItem;
import su.nightexpress.nightcore.util.placeholder.CommonPlaceholders;
import su.nightexpress.nightcore.util.profile.PlayerProfiles;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.dialog.DialogRegistry;
import su.nightexpress.sunlight.module.homes.HomesFiles;
import su.nightexpress.sunlight.module.homes.HomesModule;
import su.nightexpress.sunlight.module.homes.config.HomesLang;
import su.nightexpress.sunlight.module.homes.dialog.HomeDialogKeys;
import su.nightexpress.sunlight.module.homes.impl.Home;

import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

import static su.nightexpress.nightcore.util.text.night.wrapper.TagWrappers.*;

public class InvitedPlayersMenu extends AbstractObjectMenu<Home> {

    private final HomesModule module;
    private final DialogRegistry dialogRegistry;

    private ItemPopulator<UserInfo> playerPopulator;

    public InvitedPlayersMenu(@NotNull SunLightPlugin plugin, @NotNull HomesModule module, @NotNull DialogRegistry dialogRegistry) {
        super(MenuType.GENERIC_9X4, "Invited Players", Home.class);
        this.module = module;
        this.dialogRegistry = dialogRegistry;

        this.load(plugin, FileConfig.load(module.getLocalUIPath(), HomesFiles.FILE_UI_INVITED_PLAYERS));
    }

    @Override
    public void registerActions() {

    }

    @Override
    public void registerConditions() {

    }

    @Override
    public void defineDefaultLayout() {
        this.addBackgroundItem(Material.BLACK_STAINED_GLASS_PANE, IntStream.range(27, 36).toArray());

        this.addDefaultButton("return", MenuItem.builder()
            .defaultState(ItemState.defaultBuilder()
                .icon(NightItem.fromType(Material.IRON_DOOR).setDisplayName(WHITE.wrap("Return")))
                .action(context -> this.module.openHomeSettings(context.getPlayer(), this.getObject(context)))
                .build()
            )
            .slots(31)
            .build()
        );

        this.addDefaultButton("add_player", MenuItem.builder()
            .defaultState(ItemState.defaultBuilder()
                .icon(NightItem.fromType(Material.BELL)
                    .setDisplayName(YELLOW.and(BOLD).wrap("Add Player"))
                )
                .action(context -> this.dialogRegistry.show(context.getPlayer(), HomeDialogKeys.HOME_INVITE_PLAYER_NAME, this.getObject(context), () -> context.getViewer().refresh()))
                .build()
            )
            .slots(34)
            .build()
        );
    }

    @Override
    protected void onLoad(@NotNull FileConfig config) {
        int[] playerSlots = ConfigProperty.of(ConfigTypes.INT_ARRAY, "Players.Slots", IntStream.range(0, 27).toArray()).resolveWithDefaults(config);

        this.playerPopulator = ItemPopulator.builder(UserInfo.class)
            .actionProvider(profile -> context -> {
                if (context.getEvent().getClick() == ClickType.DROP) {
                    Home home = this.getObject(context);

                    home.getInvitedPlayers().remove(profile);
                    home.markDirty();

                    context.getViewer().refresh();
                }
            })
            .itemProvider((context, userInfo) -> {
                return NightItem.fromType(Material.PLAYER_HEAD)
                    .localized(HomesLang.UI_INVITED_PLAYERS_PLAYER)
                    .hideAllComponents()
                    .setPlayerProfile(PlayerProfiles.createProfile(userInfo.id(), userInfo.name()))
                    .replace(builder -> builder
                        .with(CommonPlaceholders.PLAYER_NAME, userInfo::name)
                    );
            })
            .slots(playerSlots)
            .build();
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
        Home home = this.getObject(context);
        List<UserInfo> invitedPlayers = home.getInvitedPlayers().stream().sorted(Comparator.comparing(UserInfo::name)).toList();

        this.playerPopulator.populateTo(context, invitedPlayers, items);
    }

    @Override
    public void onReady(@NotNull ViewerContext context, @NotNull InventoryView view, @NotNull Inventory inventory) {

    }

    @Override
    public void onRender(@NotNull ViewerContext context, @NotNull InventoryView view, @NotNull Inventory inventory) {

    }
}
