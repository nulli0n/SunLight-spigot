package su.nightexpress.sunlight.module.bans.menu;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nexmedia.engine.api.config.JYML;
import su.nexmedia.engine.api.menu.AutoPaged;
import su.nexmedia.engine.api.menu.MenuItemType;
import su.nexmedia.engine.api.menu.click.ClickHandler;
import su.nexmedia.engine.api.menu.click.ItemClick;
import su.nexmedia.engine.api.menu.impl.ConfigMenu;
import su.nexmedia.engine.api.menu.impl.MenuOptions;
import su.nexmedia.engine.api.menu.impl.MenuViewer;
import su.nexmedia.engine.utils.Colorizer;
import su.nexmedia.engine.utils.ItemUtil;
import su.nightexpress.sunlight.SunLight;
import su.nightexpress.sunlight.config.Lang;
import su.nightexpress.sunlight.data.impl.SunUser;
import su.nightexpress.sunlight.module.bans.BansModule;
import su.nightexpress.sunlight.module.bans.punishment.Punishment;
import su.nightexpress.sunlight.module.bans.punishment.PunishmentType;
import su.nightexpress.sunlight.module.bans.util.BansPerms;
import su.nightexpress.sunlight.module.bans.util.Placeholders;

import java.util.*;

public class PunishmentListMenu extends ConfigMenu<SunLight> implements AutoPaged<Punishment> {

    private final BansModule bansModule;

    private final String defTexture;
    private final String       nameActive;
    private final List<String> loreActive;
    private final String       nameExpired;
    private final List<String> loreExpired;
    private final int[]        objectSlots;

    private final Map<Player, PunishmentType> typeMap;
    private final Map<Player, String> openOthers;

    public PunishmentListMenu(@NotNull BansModule bansModule) {
        super(bansModule.plugin(), JYML.loadOrExtract(bansModule.plugin(), bansModule.getLocalPath() + "/menu/list.yml"));
        this.bansModule = bansModule;
        this.openOthers = new WeakHashMap<>();
        this.typeMap = new WeakHashMap<>();

        this.defTexture = cfg.getString("Punishment.Default_Head_Texture", "");
        this.nameActive = Colorizer.apply(cfg.getString("Punishment.Active.Name", Placeholders.PUNISHMENT_TYPE));
        this.nameExpired = Colorizer.apply(cfg.getString("Punishment.Expired.Name", Placeholders.PUNISHMENT_TYPE));
        this.loreActive = Colorizer.apply(cfg.getStringList("Punishment.Active.Lore"));
        this.loreExpired = Colorizer.apply(cfg.getStringList("Punishment.Expired.Lore"));
        this.objectSlots = cfg.getIntArray("Punishment.Slots");

        this.registerHandler(MenuItemType.class)
            .addClick(MenuItemType.CLOSE, (viewer, event) -> plugin.runTask(task -> viewer.getPlayer().closeInventory()))
            .addClick(MenuItemType.PAGE_NEXT, ClickHandler.forNextPage(this))
            .addClick(MenuItemType.PAGE_PREVIOUS, ClickHandler.forPreviousPage(this));

        this.load();
    }

    @NotNull
    public PunishmentType getPunishmentType(@NotNull Player player) {
        return this.typeMap.getOrDefault(player, PunishmentType.BAN);
    }

    public void open(@NotNull Player player, @NotNull PunishmentType type) {
        this.typeMap.put(player, type);
        this.open(player, 1);
    }

    public void open(@NotNull Player player, @NotNull PunishmentType type, @NotNull String userOther) {
        this.openOthers.put(player, userOther);
        this.open(player, type);
    }

    @Nullable
    private String getUser(@NotNull Player player) {
        return this.openOthers.get(player);
    }

    @Override
    public void onPrepare(@NotNull MenuViewer viewer, @NotNull MenuOptions options) {
        super.onPrepare(viewer, options);

        options.setTitle(options.getTitle()
            .replace(Placeholders.PUNISHMENT_TYPE, plugin.getLangManager().getEnum(this.getPunishmentType(viewer.getPlayer())))
        );

        this.getItemsForPage(viewer).forEach(this::addItem);
    }

    @Override
    public int[] getObjectSlots() {
        return objectSlots;
    }

    @Override
    @NotNull
    public List<Punishment> getObjects(@NotNull Player player) {
        // userName can be as player name and as IP address.
        String userName = this.getUser(player);
        PunishmentType punishmentType = this.getPunishmentType(player);

        List<Punishment> list = new ArrayList<>();
        if (userName != null) {
            list.addAll(bansModule.getPunishments(userName, punishmentType));

            // SunUser is needed to add punishments for user's IP address (if there are any)
            SunUser user = plugin.getUserManager().getUserData(userName);

            // If user of our 'userName' is present, then also display all punishments of his IP address.
            if (user != null) list.addAll(bansModule.getPunishments(user.getIp(), punishmentType));
        }
        else {
            list.addAll(this.bansModule.getPunishments(punishmentType));
        }
        list.sort(Comparator.comparing(Punishment::isActive).thenComparingLong(Punishment::getCreatedDate).reversed());
        //list.sort(Comparator.comparingLong(Punishment::getCreatedDate).thenComparing(Punishment::isExpired).reversed());

        return list;
    }

    @Override
    @NotNull
    public ItemStack getObjectStack(@NotNull Player player, @NotNull Punishment punishment) {
        ItemStack item = new ItemStack(Material.PLAYER_HEAD);
        ItemUtil.mapMeta(item, meta -> {
            if (punishment.isExpired()) {
                meta.setDisplayName(this.nameExpired);
                meta.setLore(this.loreExpired);
            }
            else {
                meta.setDisplayName(this.nameActive);
                meta.setLore(this.loreActive);
            }
            if (meta instanceof SkullMeta skullMeta) {
                if (punishment.getUserId() != null) {
                    skullMeta.setOwningPlayer(plugin.getServer().getOfflinePlayer(punishment.getUserId()));
                }
            }
            meta.addItemFlags(ItemFlag.values());
            ItemUtil.replace(meta, punishment.replacePlaceholders());
        });
        if (punishment.getUserId() == null) {
            ItemUtil.setSkullTexture(item, this.defTexture);
        }
        return item;
    }

    @Override
    @NotNull
    public ItemClick getObjectClick(@NotNull Punishment punishment) {
        return (viewer, e) -> {
            Player player = viewer.getPlayer();
            if (e.isShiftClick()) {

                if (e.isRightClick() && punishment.isExpired()) {
                    if (!player.hasPermission(BansPerms.HISTORY_REMOVE)) {
                        plugin.getMessage(Lang.ERROR_PERMISSION_DENY).send(player);
                        return;
                    }
                    this.bansModule.deletePunishment(punishment);
                    this.plugin.runTask(task -> this.open(player, viewer.getPage()));
                    return;
                }

                if (e.isLeftClick() && !punishment.isExpired()) {
                    if (!player.hasPermission(BansPerms.HISTORY_EXPIRE)) {
                        plugin.getMessage(Lang.ERROR_PERMISSION_DENY).send(player);
                        return;
                    }

                    punishment.expire();
                    this.plugin.runTaskAsync(task -> {
                        this.bansModule.getDataHandler().savePunishment(punishment);
                    });
                    this.plugin.runTask(task -> this.open(player, viewer.getPage()));
                }
            }
        };
    }

    @Override
    public void onClose(@NotNull MenuViewer viewer, @NotNull InventoryCloseEvent event) {
        super.onClose(viewer, event);
        this.openOthers.remove(viewer.getPlayer());
        this.typeMap.remove(viewer.getPlayer());
    }
}
