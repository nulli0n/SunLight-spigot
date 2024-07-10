package su.nightexpress.sunlight.module.bans.menu;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
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
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.config.Lang;
import su.nightexpress.sunlight.module.bans.BansModule;
import su.nightexpress.sunlight.module.bans.config.BansLang;
import su.nightexpress.sunlight.module.bans.config.BansPerms;
import su.nightexpress.sunlight.module.bans.punishment.PunishData;
import su.nightexpress.sunlight.module.bans.punishment.PunishmentType;
import su.nightexpress.sunlight.utils.UserInfo;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.stream.IntStream;

import static su.nightexpress.nightcore.util.text.tag.Tags.*;
import static su.nightexpress.sunlight.module.bans.util.Placeholders.*;

public class HistoryMenu extends ConfigMenu<SunLightPlugin> implements AutoFilled<PunishData>, Linked<HistoryMenu.Source<?>> {

    private static final String FILE_NAME = "history.yml";

    private static final String ACTION_EXPIRE = "%action_expire%";
    private static final String ACTION_DELETE = "%action_delete%";

    private final BansModule                      module;
    private final ViewLink<HistoryMenu.Source<?>> link;

    private final ItemHandler showExpiredHandler;
    private final ItemHandler sortModeHandler;

    private Map<PunishmentType, String> typeNames;

    private ItemStack activeItem;
    private ItemStack expiredItem;

    private String       nameActive;
    private List<String> loreActive;
    private String       nameExpired;
    private List<String> loreExpired;
    private int[]        objectSlots;

    private List<String> expireAction;
    private List<String> deleteAction;

    public abstract static class Source<T> {

        public final T object;
        public final PunishmentType type;

        public boolean showExpired;
        public SortMode sortMode;

        public Source(@NotNull T object, @NotNull PunishmentType type) {
            this.object = object;
            this.type = type;
            this.showExpired = true;
            this.sortMode = SortMode.DATE;
        }
    }

    public static class PlayerSource extends Source<UserInfo> {

        public PlayerSource(@NotNull UserInfo object, @NotNull PunishmentType type) {
            super(object, type);
        }
    }

    public static class AddressSource extends Source<String> {

        public AddressSource(@NotNull String object, @NotNull PunishmentType type) {
            super(object, type);
        }
    }

    public HistoryMenu(@NotNull SunLightPlugin plugin, @NotNull BansModule module) {
        super(plugin, FileConfig.loadOrExtract(plugin, module.getLocalUIPath(), FILE_NAME));
        this.module = module;
        this.link = new ViewLink<>();

        this.addHandler(this.showExpiredHandler = new ItemHandler("show_expired", (viewer, event) -> {
            Source<?> source = this.getLink(viewer);
            source.showExpired = !source.showExpired;
            this.runNextTick(() -> this.flush(viewer));
        }));

        this.addHandler(this.sortModeHandler = new ItemHandler("sort_mode", (viewer, event) -> {
            Source<?> source = this.getLink(viewer);
            source.sortMode = Lists.next(source.sortMode, sortMode -> sortMode != SortMode.NAME);
            this.runNextTick(() -> this.flush(viewer));
        }));

        this.load();

        this.getItems().forEach(menuItem -> {
            menuItem.getOptions().addDisplayModifier((viewer, itemStack) -> {
                if (menuItem.getHandler() == this.showExpiredHandler) {
                    ItemReplacer.replace(itemStack, str -> str.replace(GENERIC_STATE, Lang.getEnabledOrDisabled(this.getLink(viewer).showExpired)));
                }
                else if (menuItem.getHandler() == this.sortModeHandler) {
                    ItemReplacer.replace(itemStack, str -> str.replace(GENERIC_TYPE, BansLang.SORT_MODE.getLocalized(this.getLink(viewer).sortMode)));
                }
            });
        });
    }

    @NotNull
    @Override
    public ViewLink<HistoryMenu.Source<?>> getLink() {
        return link;
    }

    @Override
    public void onPrepare(@NotNull MenuViewer viewer, @NotNull MenuOptions options) {
        Source<?> source = this.getLink(viewer);
        String target = null;
        if (source instanceof AddressSource addressSource) {
            target = addressSource.object;
        }
        else if (source instanceof PlayerSource playerSource) {
            target = playerSource.object.getName();
        }

        options.setTitle(options.getTitle()
            .replace(PUNISHMENT_TARGET, String.valueOf(target))
            .replace(GENERIC_TYPE, typeNames.getOrDefault(source.type, source.type.name()))
        );

        this.autoFill(viewer);
    }

    @Override
    protected void onReady(@NotNull MenuViewer viewer, @NotNull Inventory inventory) {

    }

    @Override
    public void onAutoFill(@NotNull MenuViewer viewer, @NotNull AutoFill<PunishData> autoFill) {
        Player player = viewer.getPlayer();

        List<PunishData> list = new ArrayList<>();
        Source<?> source = this.getLink(player);
        AtomicInteger counter = new AtomicInteger(1);

        if (source instanceof AddressSource addressSource) {
            list.addAll(this.module.getIPPunishments(addressSource.object));
        }
        else if (source instanceof PlayerSource playerSource) {
            list.addAll(module.getPlayerPunishments(playerSource.object.getId(), playerSource.type));
        }

        if (!source.showExpired) {
            list.removeIf(Predicate.not(PunishData::isActive));
        }

        list.sort(Comparator.comparing(PunishData::isActive).reversed().thenComparing(source.sortMode.getComparator()));

        autoFill.setSlots(this.objectSlots);
        autoFill.setItems(list);
        autoFill.setItemCreator(bannedData -> {
            ItemStack item = new ItemStack(bannedData.isExpired() ? this.expiredItem : this.activeItem);

            List<String> expireLore = !bannedData.isExpired() && player.hasPermission(BansPerms.PUNISHMENT_EXPIRE) ? this.expireAction : Collections.emptyList();
            List<String> deleteLore = bannedData.isExpired() && player.hasPermission(BansPerms.PUNISHMENT_DELETE) ? this.deleteAction : Collections.emptyList();

            ItemReplacer.create(item).hideFlags().trimmed()
                .setDisplayName(bannedData.isExpired() ? this.nameExpired : this.nameActive)
                .setLore(bannedData.isExpired() ? this.loreExpired : this.loreActive)
                .replaceLoreExact(ACTION_EXPIRE, expireLore)
                .replaceLoreExact(ACTION_DELETE, deleteLore)
                .replace(bannedData.replacePlaceholders())
                .replace(GENERIC_AMOUNT, NumberUtil.format(counter.getAndIncrement()))
                .writeMeta();

            return item;
        });
        autoFill.setClickAction(bannedData -> (viewer1, event) -> {
            if (event.getClick() == ClickType.DROP && bannedData.isExpired()) {
                if (!player.hasPermission(BansPerms.PUNISHMENT_DELETE)) return;

                this.module.deletePunishment(bannedData, source.type);
            }
            else if (event.isShiftClick() && event.isRightClick() && !bannedData.isExpired()) {
                if (!player.hasPermission(BansPerms.PUNISHMENT_EXPIRE)) return;

                bannedData.expire();
                this.plugin.runTaskAsync(task -> {
                    this.module.getDataHandler().saveData(bannedData, source.type);
                });
            }
            else return;

            this.runNextTick(() -> this.flush(viewer));
        });
    }

    @Override
    @NotNull
    protected MenuOptions createDefaultOptions() {
        return new MenuOptions(BLACK.enclose(GENERIC_TYPE + " for " + PUNISHMENT_TARGET), MenuSize.CHEST_54);
    }

    @Override
    @NotNull
    protected List<MenuItem> createDefaultItems() {
        List<MenuItem> list = new ArrayList<>();

        ItemStack showExpiredItem = new ItemStack(Material.WRITABLE_BOOK);
        ItemUtil.editMeta(showExpiredItem, meta -> {
            meta.setDisplayName(LIGHT_YELLOW.enclose(BOLD.enclose("Show Expired")));
            meta.setLore(Lists.newList(
                LIGHT_GRAY.enclose("Status: " + LIGHT_YELLOW.enclose(GENERIC_STATE)),
                "",
                LIGHT_GRAY.enclose(LIGHT_YELLOW.enclose("[▶]") + " Click to " + LIGHT_YELLOW.enclose("toggle") + ".")
            ));
        });
        list.add(new MenuItem(showExpiredItem).setPriority(10).setSlots(51).setHandler(this.showExpiredHandler));

        ItemStack sortModeItem = new ItemStack(Material.COMPARATOR);
        ItemUtil.editMeta(sortModeItem, meta -> {
            meta.setDisplayName(LIGHT_YELLOW.enclose(BOLD.enclose("Sort Mode")));
            meta.setLore(Lists.newList(
                LIGHT_GRAY.enclose("Current: " + LIGHT_YELLOW.enclose(GENERIC_TYPE)),
                "",
                LIGHT_GRAY.enclose(LIGHT_YELLOW.enclose("[▶]") + " Click to " + LIGHT_YELLOW.enclose("switch") + ".")
            ));
        });
        list.add(new MenuItem(sortModeItem).setPriority(10).setSlots(47).setHandler(this.sortModeHandler));

        ItemStack close = ItemUtil.getSkinHead(SKIN_WRONG_MARK);
        ItemUtil.editMeta(close, meta -> {
            meta.setDisplayName(Lang.EDITOR_ITEM_CLOSE.getDefaultName());
        });
        list.add(new MenuItem(close).setPriority(10).setSlots(49).setHandler(ItemHandler.forClose(this)));

        ItemStack nextPage = ItemUtil.getSkinHead(SKIN_ARROW_RIGHT);
        ItemUtil.editMeta(nextPage, meta -> {
            meta.setDisplayName(Lang.EDITOR_ITEM_NEXT_PAGE.getDefaultName());
        });
        list.add(new MenuItem(nextPage).setPriority(10).setSlots(53).setHandler(ItemHandler.forNextPage(this)));

        ItemStack backPage = ItemUtil.getSkinHead(SKIN_ARROW_LEFT);
        ItemUtil.editMeta(backPage, meta -> {
            meta.setDisplayName(Lang.EDITOR_ITEM_PREVIOUS_PAGE.getDefaultName());
        });
        list.add(new MenuItem(backPage).setPriority(10).setSlots(45).setHandler(ItemHandler.forPreviousPage(this)));

        return list;
    }

    @Override
    protected void loadAdditional() {
        this.typeNames = ConfigValue.forMap("Punishment.TypeName",
            str -> StringUtil.getEnum(str, PunishmentType.class).orElse(null),
            (cfg, path, id) -> cfg.getString(path + "." + id, ""),
            (cfg, path, map) -> map.forEach((type, name) -> cfg.set(path + "." + type.name(), name)),
            () -> Map.of(
                PunishmentType.BAN, "Ban history",
                PunishmentType.MUTE, "Mute history",
                PunishmentType.WARN, "Warn history"
            )
        ).read(cfg);

        this.activeItem = ConfigValue.create("Punishment.Active.Icon",
            new ItemStack(Material.RED_DYE)
        ).read(cfg);

        this.expiredItem = ConfigValue.create("Punishment.Expired.Icon",
            new ItemStack(Material.GRAY_DYE)
        ).read(cfg);

        this.nameActive = ConfigValue.create("Punishment.Active.Name",
            LIGHT_RED.enclose(BOLD.enclose("Entry")) + " " + WHITE.enclose("#" + GENERIC_AMOUNT)
        ).read(cfg);

        this.nameExpired = ConfigValue.create("Punishment.Expired.Name",
            LIGHT_YELLOW.enclose(BOLD.enclose("Entry")) + " " + WHITE.enclose("#" + GENERIC_AMOUNT) + " " + DARK_GRAY.enclose("(Expired)")
        ).read(cfg);

        this.loreActive = ConfigValue.create("Punishment.Active.Lore", Lists.newList(
            LIGHT_RED.enclose("▪ " + LIGHT_GRAY.enclose("Punisher: ") + PUNISHMENT_PUNISHER),
            LIGHT_RED.enclose("▪ " + LIGHT_GRAY.enclose("Reason: ") + PUNISHMENT_REASON),
            LIGHT_RED.enclose("▪ " + LIGHT_GRAY.enclose("Date: ") + PUNISHMENT_CREATION_DATE),
            LIGHT_RED.enclose("▪ " + LIGHT_GRAY.enclose("Expires in: ") + PUNISHMENT_EXPIRES_IN),
            "",
            ACTION_EXPIRE
        )).read(cfg);

        this.loreExpired = ConfigValue.create("Punishment.Expired.Lore", Lists.newList(
            LIGHT_YELLOW.enclose("▪ " + LIGHT_GRAY.enclose("Punisher: ") + PUNISHMENT_PUNISHER),
            LIGHT_YELLOW.enclose("▪ " + LIGHT_GRAY.enclose("Reason: ") + PUNISHMENT_REASON),
            LIGHT_YELLOW.enclose("▪ " + LIGHT_GRAY.enclose("Date: ") + PUNISHMENT_CREATION_DATE),
            LIGHT_YELLOW.enclose("▪ " + LIGHT_GRAY.enclose("Expired: ") + PUNISHMENT_EXPIRATION_DATE),
            "",
            ACTION_DELETE
        )).read(cfg);

        this.expireAction = ConfigValue.create("Punishment.Action.Expire", Lists.newList(
            LIGHT_GRAY.enclose(LIGHT_RED.enclose("[▶]") + " Shift-Right to " + LIGHT_RED.enclose("make expired") + ".")
        )).read(cfg);

        this.deleteAction = ConfigValue.create("Punishment.Action.Delete", Lists.newList(
            LIGHT_GRAY.enclose(LIGHT_YELLOW.enclose("[▶]") + " [Q/Drop Key] to " + LIGHT_YELLOW.enclose("delete") + ".")
        )).read(cfg);

        this.objectSlots = ConfigValue.create("Punishment.Slots", IntStream.range(0, 45).toArray()).read(cfg);
    }
}
