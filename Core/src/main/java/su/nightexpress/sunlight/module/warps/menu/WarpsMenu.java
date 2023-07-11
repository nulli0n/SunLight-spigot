package su.nightexpress.sunlight.module.warps.menu;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.config.JYML;
import su.nexmedia.engine.api.menu.AutoPaged;
import su.nexmedia.engine.api.menu.MenuItemType;
import su.nexmedia.engine.api.menu.click.ClickHandler;
import su.nexmedia.engine.api.menu.click.ItemClick;
import su.nexmedia.engine.api.menu.impl.ConfigMenu;
import su.nexmedia.engine.api.menu.impl.MenuOptions;
import su.nexmedia.engine.api.menu.impl.MenuViewer;
import su.nexmedia.engine.utils.*;
import su.nightexpress.sunlight.SunLight;
import su.nightexpress.sunlight.data.impl.SunUser;
import su.nightexpress.sunlight.data.impl.cooldown.CooldownInfo;
import su.nightexpress.sunlight.module.warps.WarpsModule;
import su.nightexpress.sunlight.module.warps.impl.Warp;
import su.nightexpress.sunlight.module.warps.type.WarpSortType;
import su.nightexpress.sunlight.module.warps.util.Placeholders;
import su.nightexpress.sunlight.module.warps.util.WarpsPerms;

import java.time.LocalTime;
import java.util.*;

public class WarpsMenu extends ConfigMenu<SunLight> implements AutoPaged<Warp> {

    private static final String PLACEHOLDER_TIMES         = "%times%";
    private static final String PLACEHOLDER_NO_PERMISSION = "%no_permission%";
    private static final String PLACEHOLDER_NO_MONEY      = "%no_money%";
    private static final String PLACEHOLDER_COOLDOWN     = "%cooldown%";
    private static final String PLACEHOLDER_SORTING_MODE = "%sorting_mode%";

    private final WarpsModule module;

    private final String       warpName;
    private final List<String> warpLore;
    private final int[]        warpSlots;

    private final List<String> loreTimesOpen;
    private final List<String> loreTimesClosed;
    private final List<String> loreNoPermission;
    private final List<String> loreNoMoney;
    private final List<String> loreCooldown;

    private final Map<Player, WarpSortType> userSortCache;

    public WarpsMenu(@NotNull WarpsModule module) {
        super(module.plugin(), JYML.loadOrExtract(module.plugin(), module.getLocalPath() + "/menu/", "warp_list.yml"));
        this.module = module;
        this.userSortCache = new WeakHashMap<>();

        this.warpName = Colorizer.apply(cfg.getString("Warp.Name", Placeholders.WARP_ID));
        this.warpLore = Colorizer.apply(cfg.getStringList("Warp.Lore.Default"));
        this.warpSlots = cfg.getIntArray("Warp.Slots");
        this.loreTimesOpen = Colorizer.apply(cfg.getStringList("Warp.Lore.Times_Open"));
        this.loreTimesClosed = Colorizer.apply(cfg.getStringList("Warp.Lore.Times_Closed"));
        this.loreNoPermission = Colorizer.apply(cfg.getStringList("Warp.Lore.No_Permission"));
        this.loreNoMoney = Colorizer.apply(cfg.getStringList("Warp.Lore.No_Money"));
        this.loreCooldown = Colorizer.apply(cfg.getStringList("Warp.Lore.Cooldown"));

        this.registerHandler(MenuItemType.class)
            .addClick(MenuItemType.CLOSE, (viewer, event) -> plugin.runTask(task -> viewer.getPlayer().closeInventory()))
            .addClick(MenuItemType.PAGE_NEXT, ClickHandler.forNextPage(this))
            .addClick(MenuItemType.PAGE_PREVIOUS, ClickHandler.forPreviousPage(this));

        this.registerHandler(Type.class)
            .addClick(Type.SORTING_MODE, (viewer, event) -> {
                WarpSortType userSort = this.getUserSortType(viewer.getPlayer());
                userSort = CollectionsUtil.next(userSort);
                this.userSortCache.put(viewer.getPlayer(), userSort);
                this.openNextTick(viewer, viewer.getPage());
            });

        this.load();

        this.getItems().forEach(menuItem -> {
            if (menuItem.getType() == Type.SORTING_MODE) {
                menuItem.getOptions().addDisplayModifier((viewer, item) -> {
                    WarpSortType sortType = this.getUserSortType(viewer.getPlayer());
                    ItemUtil.replace(item, line -> line.replace(PLACEHOLDER_SORTING_MODE, plugin.getLangManager().getEnum(sortType)));
                });
            }
        });
    }

    @Override
    public void onPrepare(@NotNull MenuViewer viewer, @NotNull MenuOptions options) {
        super.onPrepare(viewer, options);
        this.getItemsForPage(viewer).forEach(this::addItem);
    }

    enum Type {
        SORTING_MODE,
    }

    @NotNull
    private WarpSortType getUserSortType(@NotNull Player player) {
        return this.userSortCache.getOrDefault(player, WarpSortType.WARP_ID);
    }

    @Override
    public int[] getObjectSlots() {
        return warpSlots;
    }

    @Override
    @NotNull
    public List<Warp> getObjects(@NotNull Player player) {
        return this.module.getWarps().stream().sorted(this.getUserSortType(player).getComparator()).toList();
    }

    @Override
    @NotNull
    public ItemStack getObjectStack(@NotNull Player player, @NotNull Warp warp) {
        ItemStack item = new ItemStack(warp.getIcon());

        List<String> lore = new ArrayList<>(this.warpLore);
        List<String> loreTimes = Collections.emptyList();
        List<String> loreNoPerm = Collections.emptyList();
        List<String> loreNoMoney = Collections.emptyList();
        List<String> loreCooldown = Collections.emptyList();

        if (warp.hasVisitTimes()) {
            LocalTime time;
            if (!warp.isVisitTime(player)) {
                time = warp.getNearestVisitTime();
                if (time != null) loreTimes = new ArrayList<>(this.loreTimesClosed);
            }
            else {
                time = warp.getNearestCloseTime();
                if (time != null) loreTimes = new ArrayList<>(this.loreTimesOpen);
            }
            if (time != null) {
                loreTimes.replaceAll(str -> str.replace(Placeholders.GENERIC_TIME, time.format(Warp.TIME_FORMATTER)));
            }
        }

        if (warp.hasVisitCooldown()) {
            SunUser user = plugin.getUserManager().getUserData(player);
            CooldownInfo cooldownInfo = user.getCooldown(warp).orElse(null);
            if (cooldownInfo != null) {
                long expireDate = cooldownInfo.getExpireDate();
                loreCooldown = new ArrayList<>(this.loreCooldown);
                loreCooldown.replaceAll(str -> str.replace(Placeholders.GENERIC_COOLDOWN, TimeUtil.formatTimeLeft(expireDate)));
            }
        }

        if (!warp.hasPermission(player)) {
            loreNoPerm = this.loreNoPermission;
        }
        if (!warp.canAffordVisit(player)) {
            loreNoMoney = this.loreNoMoney;
        }

        lore = StringUtil.replaceInList(lore, PLACEHOLDER_TIMES, loreTimes);
        lore = StringUtil.replaceInList(lore, PLACEHOLDER_COOLDOWN, loreCooldown);
        lore = StringUtil.replaceInList(lore, PLACEHOLDER_NO_MONEY, loreNoMoney);
        lore = StringUtil.replaceInList(lore, PLACEHOLDER_NO_PERMISSION, loreNoPerm);

        List<String> lore2 = lore;
        ItemUtil.mapMeta(item, meta -> {
            meta.setDisplayName(this.warpName);
            meta.setLore(lore2);
            ItemUtil.replace(meta, warp.replacePlaceholders());
        });
        return item;
    }

    @Override
    @NotNull
    public ItemClick getObjectClick(@NotNull Warp warp) {
        return (viewer, event) -> {
            Player player = viewer.getPlayer();
            if (event.isLeftClick()) {
                warp.teleport(player, false);
                return;
            }

            if (event.isRightClick()) {
                if (warp.isOwner(player) || player.hasPermission(WarpsPerms.EDITOR_OTHERS)) {
                    warp.getEditor().openNextTick(viewer, 1);
                }
            }
        };
    }

    @Override
    public void onClose(@NotNull MenuViewer viewer, @NotNull InventoryCloseEvent event) {
        super.onClose(viewer, event);
        this.userSortCache.remove(viewer.getPlayer());
    }
}
