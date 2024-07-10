package su.nightexpress.sunlight.module.warps.menu;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.config.ConfigValue;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.menu.MenuOptions;
import su.nightexpress.nightcore.menu.MenuSize;
import su.nightexpress.nightcore.menu.MenuViewer;
import su.nightexpress.nightcore.menu.api.AutoFill;
import su.nightexpress.nightcore.menu.api.AutoFilled;
import su.nightexpress.nightcore.menu.impl.ConfigMenu;
import su.nightexpress.nightcore.menu.item.ItemHandler;
import su.nightexpress.nightcore.menu.item.MenuItem;
import su.nightexpress.nightcore.menu.link.Linked;
import su.nightexpress.nightcore.menu.link.ViewLink;
import su.nightexpress.nightcore.util.*;
import su.nightexpress.nightcore.util.placeholder.PlaceholderMap;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.config.Lang;
import su.nightexpress.sunlight.data.user.SunUser;
import su.nightexpress.sunlight.core.cooldown.CooldownInfo;
import su.nightexpress.sunlight.module.warps.WarpsModule;
import su.nightexpress.sunlight.module.warps.config.WarpsLang;
import su.nightexpress.sunlight.module.warps.impl.Warp;
import su.nightexpress.sunlight.module.warps.type.WarpType;
import su.nightexpress.sunlight.module.warps.util.Placeholders;

import java.time.LocalTime;
import java.util.*;

import static su.nightexpress.sunlight.module.warps.util.Placeholders.*;
import static su.nightexpress.nightcore.util.text.tag.Tags.*;

public class WarpListMenu extends ConfigMenu<SunLightPlugin> implements AutoFilled<Warp>, Linked<DisplayInfo> {

    public static final String FILE = "warp_list.yml";

    private static final String TIMES           = "%times%";
    private static final String NO_PERMISSION   = "%no_permission%";
    private static final String NO_MONEY        = "%no_money%";
    private static final String COOLDOWN        = "%cooldown%";
    private static final String VISIT_COST      = "%visit_cost%";
    private static final String VISIT_COOLDOWN  = "%visit_cooldown%";
    private static final String SORT_TYPE       = "%sorting_mode%";
    private static final String OWN_ONLY        = "%own_only%";
    private static final String ACTION_TELEPORT = "%action_teleport%";
    private static final String ACTION_EDITOR   = "%action_editor%";

    private final WarpsModule           module;
    private final ItemHandler           sortHandler;
    private final ItemHandler           ownedHandler;
    private final ItemHandler           serverTypeHandler;
    private final ItemHandler           playerTypeHandler;
    private final ViewLink<DisplayInfo> link;

    private String       serverWarpName;
    private List<String> serverWarpLore;
    private String       playerWarpName;
    private List<String> playerWarpLore;
    private int[]        warpSlots;

    private List<String> loreTimesOpen;
    private List<String> loreTimesClosed;
    private List<String> loreNoPermission;
    private List<String> loreNoMoney;
    private List<String> loreCooldown;
    private List<String> loreVisitCost;
    private List<String> loreVisitCooldown;
    private List<String> loreTeleport;
    private List<String> loreEditor;

    public WarpListMenu(@NotNull SunLightPlugin plugin, @NotNull WarpsModule module) {
        super(plugin, FileConfig.loadOrExtract(plugin, module.getLocalUIPath(), FILE));
        this.module = module;
        this.link = new ViewLink<>();

        this.addHandler(this.sortHandler = new ItemHandler("sorting_mode", (viewer, event) -> {
            DisplayInfo info = this.getLink(viewer);
            info.sortType = Lists.next(info.sortType);
            this.runNextTick(() -> this.flush(viewer));
        }));

        this.addHandler(this.ownedHandler = new ItemHandler("own_toggle", (viewer, event) -> {
            DisplayInfo info = this.getLink(viewer);
            info.ownedOnly = !info.ownedOnly;
            this.runNextTick(() -> this.flush(viewer));
        }));

        this.addHandler(this.serverTypeHandler = new ItemHandler("server_warps", (viewer, event) -> {
            DisplayInfo info = this.getLink(viewer);
            info.type = WarpType.SERVER;
            this.runNextTick(() -> this.flush(viewer));
        }));

        this.addHandler(this.playerTypeHandler = new ItemHandler("player_warps", (viewer, event) -> {
            DisplayInfo info = this.getLink(viewer);
            info.type = WarpType.PLAYER;
            this.runNextTick(() -> this.flush(viewer));
        }));

        this.load();

        this.getItems().forEach(menuItem -> {
            if (menuItem.getHandler() == this.playerTypeHandler) {
                menuItem.getOptions().addVisibilityPolicy(viewer -> this.getLink(viewer).type != WarpType.PLAYER);
            }
            else if (menuItem.getHandler() == this.serverTypeHandler) {
                menuItem.getOptions().addVisibilityPolicy(viewer -> this.getLink(viewer).type != WarpType.SERVER);
            }
            else if (menuItem.getHandler() == this.ownedHandler) {
                menuItem.getOptions().addDisplayModifier((viewer, itemStack) -> {
                    ItemReplacer.replace(itemStack, line -> line.replace(OWN_ONLY, WarpsLang.getYesOrNo(this.getLink(viewer).ownedOnly)));
                });
            }
            else if (menuItem.getHandler() == this.sortHandler) {
                menuItem.getOptions().addDisplayModifier((viewer, item) -> {
                    ItemReplacer.replace(item, line -> line.replace(SORT_TYPE, WarpsLang.SORT_TYPE.getLocalized(this.getLink(viewer).sortType)));
                });
            }
        });
    }

    @NotNull
    @Override
    public ViewLink<DisplayInfo> getLink() {
        return link;
    }

    @Override
    public void onPrepare(@NotNull MenuViewer viewer, @NotNull MenuOptions options) {
        this.autoFill(viewer);
    }

    @Override
    protected void onReady(@NotNull MenuViewer viewer, @NotNull Inventory inventory) {

    }

    @Override
    public void onAutoFill(@NotNull MenuViewer viewer, @NotNull AutoFill<Warp> autoFill) {
        Player player = viewer.getPlayer();
        DisplayInfo displayInfo = this.getLink(player);

        autoFill.setSlots(this.warpSlots);
        autoFill.setItems(this.module.getWarps().stream()
            .filter(warp -> warp.getType() == displayInfo.type)
            .filter(warp -> !displayInfo.ownedOnly || warp.isOwner(player))
            .sorted(displayInfo.sortType.getComparator())
            .toList()
        );
        autoFill.setItemCreator(warp -> {
            ItemStack item = warp.getIcon();

            List<String> loreTimes = new ArrayList<>();
            List<String> loreNoPerm = new ArrayList<>();
            List<String> loreNoMoney = new ArrayList<>();
            List<String> loreCooldown = new ArrayList<>();
            List<String> loreVisitCost = new ArrayList<>();
            List<String> loreVisitCooldown = new ArrayList<>();
            List<String> loreTeleport = new ArrayList<>(this.loreTeleport);
            List<String> loreEditor = new ArrayList<>();

            PlaceholderMap placeholders = new PlaceholderMap();

            if (!warp.hasPermission(player)) {
                loreNoPerm = new ArrayList<>(this.loreNoPermission);
                loreTeleport.clear();
            }
            else {
                if (warp.canEdit(player)) {
                    loreEditor = new ArrayList<>(this.loreEditor);
                }

                if (warp.hasVisitTimes()) {
                    LocalTime time;
                    if (!warp.isVisitTime(player)) {
                        loreTeleport.clear();
                        time = warp.getNearestVisitTime();
                        if (time != null) loreTimes = new ArrayList<>(this.loreTimesClosed);
                    }
                    else {
                        time = warp.getNearestCloseTime();
                        if (time != null) loreTimes = new ArrayList<>(this.loreTimesOpen);
                    }
                    if (time != null) {
                        placeholders.add(Placeholders.GENERIC_TIME, time.format(Warp.TIME_FORMATTER));
                    }
                }

                if (warp.hasVisitCooldown()) {
                    SunUser user = plugin.getUserManager().getUserData(player);
                    CooldownInfo cooldownInfo = user.getCooldown(warp).orElse(null);
                    if (cooldownInfo != null) {
                        loreTeleport.clear();

                        long expireDate = cooldownInfo.getExpireDate();
                        loreCooldown = new ArrayList<>(this.loreCooldown);
                        placeholders.add(Placeholders.GENERIC_COOLDOWN, TimeUtil.formatDuration(expireDate));
                    }
                    else loreVisitCooldown = new ArrayList<>(this.loreVisitCooldown);
                }

                if (warp.hasVisitCost()) {
                    if (!warp.canAffordVisit(player)) {
                        loreTeleport.clear();
                        loreNoMoney = new ArrayList<>(this.loreNoMoney);
                    }
                    else loreVisitCost = new ArrayList<>(this.loreVisitCost);
                }
            }

            ItemReplacer.create(item).hideFlags().trimmed()
                .setDisplayName(warp.getType() == WarpType.SERVER ? this.serverWarpName : this.playerWarpName)
                .setLore(warp.getType() == WarpType.SERVER ? this.serverWarpLore : this.playerWarpLore)
                .injectLore(TIMES, loreTimes)
                .injectLore(COOLDOWN, loreCooldown)
                .injectLore(NO_MONEY, loreNoMoney)
                .injectLore(NO_PERMISSION, loreNoPerm)
                .injectLore(VISIT_COST, loreVisitCost)
                .injectLore(VISIT_COOLDOWN, loreVisitCooldown)
                .injectLore(ACTION_TELEPORT, loreTeleport)
                .injectLore(ACTION_EDITOR, loreEditor)
                .replace(placeholders)
                .replace(warp.getPlaceholders())
                .writeMeta();

            return item;
        });
        autoFill.setClickAction(warp -> (viewer1, event) -> {
            if (event.isLeftClick()) {
                warp.teleport(player, false);
                return;
            }

            if (event.isRightClick()) {
                if (warp.canEdit(player)) {
                    this.runNextTick(() -> warp.getModule().openWarpSettings(player, warp));
                }
            }
        });
    }

    @Override
    @NotNull
    protected MenuOptions createDefaultOptions() {
        return new MenuOptions(BLACK.enclose("Warps"), MenuSize.CHEST_45);
    }

    @Override
    @NotNull
    protected List<MenuItem> createDefaultItems() {
        List<MenuItem> list = new ArrayList<>();

        ItemStack panes = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        list.add(new MenuItem(panes).setSlots(0,1,2,3,4,5,6,7,8,9,14,15,17,18,23,24,26,27,32,33,35,36,37,38,39,40,41,42,43,44).setPriority(0));

        ItemStack nextPage = ItemUtil.getSkinHead(SKIN_ARROW_RIGHT);
        ItemUtil.editMeta(nextPage, meta -> {
            meta.setDisplayName(Lang.EDITOR_ITEM_NEXT_PAGE.getDefaultName());
        });
        list.add(new MenuItem(nextPage).setPriority(10).setSlots(23).setHandler(ItemHandler.forNextPage(this)));

        ItemStack backPage = ItemUtil.getSkinHead(SKIN_ARROW_LEFT);
        ItemUtil.editMeta(backPage, meta -> {
            meta.setDisplayName(Lang.EDITOR_ITEM_PREVIOUS_PAGE.getDefaultName());
        });
        list.add(new MenuItem(backPage).setPriority(10).setSlots(18).setHandler(ItemHandler.forPreviousPage(this)));

        ItemStack sort = ItemUtil.getSkinHead("5cce7359a25de6da56308e6a369c6372e2c30906c62647040da137a32addc9");
        ItemUtil.editMeta(sort, meta -> {
            meta.setDisplayName(LIGHT_YELLOW.enclose(BOLD.enclose("Display Order")));
            meta.setLore(Lists.newList(
                LIGHT_YELLOW.enclose("▪ " + LIGHT_GRAY.enclose("Current: ") + SORT_TYPE),
                "",
                LIGHT_GRAY.enclose(LIGHT_YELLOW.enclose("[▶]") + " Click to " + LIGHT_YELLOW.enclose("toggle") + ".")
            ));
        });
        list.add(new MenuItem(sort).setPriority(10).setSlots(16).setHandler(this.sortHandler));

        ItemStack ownToggle = new ItemStack(Material.OAK_SIGN);
        ItemUtil.editMeta(ownToggle, meta -> {
            meta.setDisplayName(LIGHT_YELLOW.enclose(BOLD.enclose("Own Only")));
            meta.setLore(Lists.newList(
                LIGHT_YELLOW.enclose("▪ " + LIGHT_GRAY.enclose("Enabled: ") + OWN_ONLY),
                "",
                LIGHT_GRAY.enclose(LIGHT_YELLOW.enclose("[▶]") + " Click to " + LIGHT_YELLOW.enclose("toggle") + ".")
            ));
        });
        list.add(new MenuItem(ownToggle).setPriority(10).setSlots(25).setHandler(this.ownedHandler));

        ItemStack playerWarps = ItemUtil.getSkinHead("100191e52d207a0ef4972ff8393e4ed1277b1b872e72e7830aff09e938f337ec");
        ItemUtil.editMeta(playerWarps, meta -> {
            meta.setDisplayName(LIGHT_YELLOW.enclose(BOLD.enclose("Player Warps")));
            meta.setLore(Lists.newList(
                LIGHT_GRAY.enclose("Browse warps created by our players!"),
                "",
                LIGHT_GRAY.enclose(LIGHT_YELLOW.enclose("[▶]") + " Click to " + LIGHT_YELLOW.enclose("navigate") + ".")
            ));
        });
        list.add(new MenuItem(playerWarps).setPriority(10).setSlots(34).setHandler(this.playerTypeHandler));

        ItemStack serverWarps = ItemUtil.getSkinHead("efbf8fdcac5b1419ea1954f2b6fcff65bfdb6511b601bf4408d9bbb311de7648");
        ItemUtil.editMeta(serverWarps, meta -> {
            meta.setDisplayName(LIGHT_YELLOW.enclose(BOLD.enclose("Server Warps")));
            meta.setLore(Lists.newList(
                LIGHT_GRAY.enclose("Browse our official, server warps!"),
                "",
                LIGHT_GRAY.enclose(LIGHT_YELLOW.enclose("[▶]") + " Click to " + LIGHT_YELLOW.enclose("navigate") + ".")
            ));
        });
        list.add(new MenuItem(serverWarps).setPriority(10).setSlots(34).setHandler(this.serverTypeHandler));
        
        return list;
    }

    @Override
    protected void loadAdditional() {
        this.serverWarpName = ConfigValue.create("Warp.Server.Name",
            LIGHT_YELLOW.enclose(BOLD.enclose(WARP_NAME)) + " " + GRAY.enclose("(ID: " + WHITE.enclose(WARP_ID) + ")")
        ).read(cfg);

        this.serverWarpLore = ConfigValue.create("Warp.Server.Lore", Lists.newList(
            DARK_GRAY.enclose("Official Warp"),
            "",
            WARP_DESCRIPTION,
            "",
            TIMES,
            NO_PERMISSION,
            NO_MONEY,
            VISIT_COST,
            VISIT_COOLDOWN,
            COOLDOWN,
            "",
            ACTION_TELEPORT,
            ACTION_EDITOR
        )).read(cfg);

        this.playerWarpName = ConfigValue.create("Warp.Player.Name",
            LIGHT_YELLOW.enclose(BOLD.enclose(WARP_NAME)) + " " + GRAY.enclose("(ID: " + WHITE.enclose(WARP_ID) + ")")
        ).read(cfg);

        this.playerWarpLore = ConfigValue.create("Warp.Player.Lore", Lists.newList(
            DARK_GRAY.enclose("Created by " + WARP_OWNER_NAME),
            "",
            WARP_DESCRIPTION,
            "",
            TIMES,
            NO_PERMISSION,
            NO_MONEY,
            VISIT_COST,
            VISIT_COOLDOWN,
            COOLDOWN,
            "",
            ACTION_TELEPORT,
            ACTION_EDITOR
        )).read(cfg);

        this.loreTeleport = ConfigValue.create("Warp.Lore.Teleport", Lists.newList(
            LIGHT_GRAY.enclose(LIGHT_YELLOW.enclose("[▶]") + " Left-Click to " + LIGHT_YELLOW.enclose("teleport") + ".")
        )).read(cfg);

        this.loreEditor = ConfigValue.create("Warp.Lore.Editor", Lists.newList(
            LIGHT_GRAY.enclose(LIGHT_YELLOW.enclose("[▶]") + " Right-Click to " + LIGHT_YELLOW.enclose("edit") + ".")
        )).read(cfg);
        
        this.loreTimesOpen = ConfigValue.create("Warp.Lore.Times_Open", Lists.newList(
            LIGHT_GRAY.enclose(LIGHT_GREEN.enclose("✔") + " Open until " + LIGHT_GREEN.enclose(GENERIC_TIME) + ".")
        )).read(cfg);

        this.loreTimesClosed = ConfigValue.create("Warp.Lore.Times_Closed", Lists.newList(
            LIGHT_GRAY.enclose(LIGHT_RED.enclose("[❗]") + " Closed until " + LIGHT_RED.enclose(GENERIC_TIME) + ".")
        )).read(cfg);

        this.loreNoPermission = ConfigValue.create("Warp.Lore.No_Permission", Lists.newList(
            LIGHT_GRAY.enclose(LIGHT_RED.enclose("[❗]") + " You don't have " + LIGHT_RED.enclose("permission") + " to visit this warp.")
        )).read(cfg);

        this.loreNoMoney = ConfigValue.create("Warp.Lore.No_Money", Lists.newList(
            LIGHT_GRAY.enclose(LIGHT_RED.enclose("[❗]") + " You don't have " + LIGHT_RED.enclose(WARP_VISIT_COST) + " to visit this warp.")
        )).read(cfg);

        this.loreVisitCost = ConfigValue.create("Warp.Lore.VisitCost", Lists.newList(
            LIGHT_GRAY.enclose(LIGHT_RED.enclose("[❗]") + " Visit Cost: " + LIGHT_RED.enclose("$" + WARP_VISIT_COST))
        )).read(cfg);

        this.loreVisitCooldown = ConfigValue.create("Warp.Lore.VisitCooldown", Lists.newList(
            LIGHT_GRAY.enclose(LIGHT_RED.enclose("[❗]") + " Visit Cooldown: " + LIGHT_RED.enclose(WARP_VISIT_COOLDOWN))
        )).read(cfg);

        this.loreCooldown = ConfigValue.create("Warp.Lore.Cooldown", Lists.newList(
            LIGHT_GRAY.enclose(LIGHT_RED.enclose("[❗]") + " You can visit this warp again in " + LIGHT_RED.enclose(GENERIC_COOLDOWN) + ".")
        )).read(cfg);

        this.warpSlots = ConfigValue.create("Warp.Slots", new int[] {10,11,12,13,19,20,21,22,28,29,30,31}).read(cfg);

    }
}
