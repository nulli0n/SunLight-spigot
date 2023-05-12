package su.nightexpress.sunlight.module.warps.menu;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.config.JYML;
import su.nexmedia.engine.api.menu.AbstractMenuAuto;
import su.nexmedia.engine.api.menu.MenuClick;
import su.nexmedia.engine.api.menu.MenuItem;
import su.nexmedia.engine.api.menu.MenuItemType;
import su.nexmedia.engine.utils.*;
import su.nightexpress.sunlight.SunLight;
import su.nightexpress.sunlight.data.impl.SunUser;
import su.nightexpress.sunlight.module.warps.WarpsModule;
import su.nightexpress.sunlight.module.warps.impl.Warp;
import su.nightexpress.sunlight.module.warps.type.WarpSortType;
import su.nightexpress.sunlight.module.warps.util.Placeholders;
import su.nightexpress.sunlight.module.warps.util.WarpsPerms;
import su.nightexpress.sunlight.data.impl.cooldown.CooldownInfo;

import java.time.LocalTime;
import java.util.*;

public class WarpsMenu extends AbstractMenuAuto<SunLight, Warp> {

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
        super(module.plugin(), JYML.loadOrExtract(module.plugin(), module.getLocalPath() + "/menu/warp_list.yml"), "");
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

        MenuClick click = (player, type, e) -> {
            if (type instanceof MenuItemType type2) {
                this.onItemClickDefault(player, type2);
            }
            else if (type instanceof Type type2) {
                if (type2 == Type.SORTING_MODE) {
                    WarpSortType userSort = this.getUserSortType(player);
                    userSort = CollectionsUtil.next(userSort);
                    this.userSortCache.put(player, userSort);
                    this.open(player, this.getPage(player));
                }
            }
        };

        for (String sId : cfg.getSection("Content")) {
            MenuItem menuItem = cfg.getMenuItem("Content." + sId, MenuItemType.class);

            if (menuItem.getType() != null) {
                menuItem.setClickHandler(click);
            }
            this.addItem(menuItem);
        }

        for (String sId : cfg.getSection("Special")) {
            MenuItem menuItem = cfg.getMenuItem("Special." + sId, Type.class);

            if (menuItem.getType() != null) {
                menuItem.setClickHandler(click);
            }
            this.addItem(menuItem);
        }
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
    protected List<Warp> getObjects(@NotNull Player player) {
        return new ArrayList<>(this.module.getWarps());
    }

    @Override
    @NotNull
    protected List<Warp> fineObjects(@NotNull List<Warp> objects, @NotNull Player player) {
        return objects.stream().sorted(this.getUserSortType(player).getComparator()).toList();
    }

    @Override
    @NotNull
    protected ItemStack getObjectStack(@NotNull Player player, @NotNull Warp warp) {
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

        lore = StringUtil.replace(lore, PLACEHOLDER_TIMES, false, loreTimes);
        lore = StringUtil.replace(lore, PLACEHOLDER_COOLDOWN, false, loreCooldown);
        lore = StringUtil.replace(lore, PLACEHOLDER_NO_MONEY, false, loreNoMoney);
        lore = StringUtil.replace(lore, PLACEHOLDER_NO_PERMISSION, false, loreNoPerm);

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
    protected MenuClick getObjectClick(@NotNull Player player, @NotNull Warp warp) {
        return (player1, type, e) -> {
            if (e.isLeftClick()) {
                warp.teleport(player1, false);
                return;
            }

            if (e.isRightClick()) {
                if (warp.isOwner(player1) || player1.hasPermission(WarpsPerms.EDITOR_OTHERS)) {
                    warp.getEditor().open(player1, 1);
                }
            }
        };
    }

    @Override
    public void onClose(@NotNull Player player, @NotNull InventoryCloseEvent e) {
        super.onClose(player, e);
        this.userSortCache.remove(player);
    }

    @Override
    public void onItemPrepare(@NotNull Player player, @NotNull MenuItem menuItem, @NotNull ItemStack item) {
        super.onItemPrepare(player, menuItem, item);

        if (menuItem.getType() == Type.SORTING_MODE) {
            WarpSortType sortType = this.getUserSortType(player);
            ItemUtil.replace(item, line -> line.replace(PLACEHOLDER_SORTING_MODE, plugin.getLangManager().getEnum(sortType)));
        }
    }

    @Override
    public boolean cancelClick(@NotNull InventoryClickEvent e, @NotNull SlotType slotType) {
        return true;
    }
}
