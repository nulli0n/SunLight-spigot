package su.nightexpress.sunlight.module.bans.menu;

import org.bukkit.Material;
import org.bukkit.entity.Player;
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
import su.nightexpress.nightcore.core.config.CoreLang;
import su.nightexpress.nightcore.locale.LangContainer;
import su.nightexpress.nightcore.locale.LangEntry;
import su.nightexpress.nightcore.locale.entry.EnumLocale;
import su.nightexpress.nightcore.locale.entry.IconLocale;
import su.nightexpress.nightcore.ui.inventory.item.ItemPopulator;
import su.nightexpress.nightcore.ui.inventory.item.ItemState;
import su.nightexpress.nightcore.ui.inventory.item.MenuItem;
import su.nightexpress.nightcore.ui.inventory.menu.AbstractObjectMenu;
import su.nightexpress.nightcore.ui.inventory.viewer.ViewerContext;
import su.nightexpress.nightcore.user.UserInfo;
import su.nightexpress.nightcore.util.Lists;
import su.nightexpress.nightcore.util.bukkit.NightItem;
import su.nightexpress.nightcore.util.placeholder.PlaceholderContext;
import su.nightexpress.sunlight.SLPlaceholders;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.module.bans.BansModule;
import su.nightexpress.sunlight.module.bans.config.BansLang;
import su.nightexpress.sunlight.module.bans.config.BansPerms;
import su.nightexpress.sunlight.module.bans.punishment.PlayerPunishment;
import su.nightexpress.sunlight.module.bans.punishment.PunishmentType;

import java.util.List;
import java.util.stream.IntStream;

import static su.nightexpress.nightcore.util.text.night.wrapper.TagWrappers.*;
import static su.nightexpress.sunlight.module.bans.BansPlaceholders.*;

public class HistoryMenu extends AbstractObjectMenu<HistoryMenu.Data> implements LangContainer {

    private static final EnumLocale<PunishmentType> TYPE_LOCALE = LangEntry.builder("Bans.UI.History.TypeName").enumeration(PunishmentType.class);

    private static final IconLocale LOCALE_ACTIVE = LangEntry.iconBuilder("Bans.UI.History.Icon.Active")
        .rawName(WHITE.wrap(SLPlaceholders.GENERIC_TYPE) + DARK_GRAY.wrap(" • " + GREEN.wrap("Active")))
        .rawLore(
            ITALIC.and(DARK_GRAY).wrap("\"" + PUNISHMENT_REASON + "\""),
            "",
            RED.wrap("➥ " + GRAY.wrap("Staff: ") + PUNISHMENT_WHO),
            RED.wrap("➥ " + GRAY.wrap("Date: ") + PUNISHMENT_CREATION_DATE),
            RED.wrap("➥ " + GRAY.wrap("Duration: ") + PUNISHMENT_DURATION),
            "",
            GOLD.wrap("⌛ " + GRAY.wrap("Expires in: ") + PUNISHMENT_EXPIRES_IN)
        )
        .build();

    private static final IconLocale LOCALE_PAUSED = LangEntry.iconBuilder("Bans.UI.History.Icon.Paused")
        .rawName(WHITE.wrap(SLPlaceholders.GENERIC_TYPE) + DARK_GRAY.wrap(" • " + YELLOW.wrap("Inactive")))
        .rawLore(
            ITALIC.and(DARK_GRAY).wrap("\"" + PUNISHMENT_REASON + "\""),
            "",
            RED.wrap("➥ " + GRAY.wrap("Staff: ") + PUNISHMENT_WHO),
            RED.wrap("➥ " + GRAY.wrap("Date: ") + PUNISHMENT_CREATION_DATE),
            RED.wrap("➥ " + GRAY.wrap("Duration: ") + PUNISHMENT_DURATION),
            "",
            GOLD.wrap("⌛ " + GRAY.wrap("Expires in: ") + PUNISHMENT_EXPIRES_IN)
        )
        .build();

    private static final IconLocale LOCALE_EXPIRED = LangEntry.iconBuilder("Bans.UI.History.Icon.Expired")
        .rawName(WHITE.wrap(SLPlaceholders.GENERIC_TYPE) + DARK_GRAY.wrap(" • " + GREEN.wrap("Expired")))
        .rawLore(
            ITALIC.and(DARK_GRAY).wrap("\"" + PUNISHMENT_REASON + "\""),
            "",
            RED.wrap("➥ " + GRAY.wrap("Staff: ") + PUNISHMENT_WHO),
            RED.wrap("➥ " + GRAY.wrap("Date: ") + PUNISHMENT_CREATION_DATE),
            RED.wrap("➥ " + GRAY.wrap("Duration: ") + PUNISHMENT_DURATION),
            "",
            DARK_GREEN.wrap("⌛ " + GRAY.wrap("Expired: ") + PUNISHMENT_EXPIRATION_DATE)
        )
        .build();
    
    public record Data(@NotNull UserInfo userInfo, @NotNull PunishmentType type, @NotNull SortMode sortMode, boolean showExpired) {}

    private final SunLightPlugin plugin;
    private final BansModule module;

    private NightItem historyActiveIcon;
    private NightItem historyPausedIcon;
    private NightItem histroyExpiredIcon;

    private ItemPopulator<PlayerPunishment> populator;

    public HistoryMenu(@NotNull SunLightPlugin plugin, @NotNull BansModule module) {
        super(MenuType.GENERIC_9X5, BLACK.wrap("[%s] History for %s".formatted(SLPlaceholders.GENERIC_TYPE, SLPlaceholders.GENERIC_TARGET)), Data.class);
        this.plugin = plugin;
        this.module = module;

        this.plugin.injectLang(this);
    }

    public boolean show(@NotNull Player player, @NotNull UserInfo userInfo, @NotNull PunishmentType type) {
        return this.show(player, userInfo, type, SortMode.NEWEST, true);
    }

    private boolean show(@NotNull Player player, @NotNull UserInfo userInfo, @NotNull PunishmentType type, @NotNull SortMode mode, boolean showExpired) {
        return this.show(this.plugin, player, new Data(userInfo, type, mode, showExpired));
    }

    @Override
    @NotNull
    protected String getRawTitle(@NotNull ViewerContext context) {
        Data data = this.getObject(context);

        return PlaceholderContext.builder()
            .with(SLPlaceholders.GENERIC_TYPE, () -> TYPE_LOCALE.getLocalized(data.type))
            .with(SLPlaceholders.GENERIC_TARGET, data.userInfo::name)
            .build()
            .apply(super.getRawTitle(context));
    }

    @Override
    public void registerActions() {

    }

    @Override
    public void registerConditions() {

    }

    @Override
    public void defineDefaultLayout() {
        this.addNextPageItem(Material.ARROW, 41);
        this.addPreviousPageItem(Material.ARROW, 39);

        this.addBackgroundItem(Material.BLACK_STAINED_GLASS_PANE, IntStream.range(0, 9).toArray());
        this.addBackgroundItem(Material.BLACK_STAINED_GLASS_PANE, IntStream.range(36, 45).toArray());

        this.addDefaultButton("sort_mode", MenuItem.builder()
            .defaultState(ItemState.defaultBuilder()
                .icon(NightItem.fromType(Material.COMPARATOR)
                    .setDisplayName(GOLD.wrap("Sorting Mode"))
                    .setLore(List.of(
                        DARK_GRAY.wrap("»" + GRAY.wrap(" Selected: ") + WHITE.wrap(SLPlaceholders.GENERIC_MODE)),
                        "",
                        GRAY.wrap("Sets display order."),
                        "",
                        GOLD.wrap("→ " + UNDERLINED.wrap("Click to toggle"))
                    ))
                )
                .displayModifier((context, item) -> {
                    item.replace(builder -> builder.with(SLPlaceholders.GENERIC_MODE, () -> BansLang.SORT_MODE.getLocalized(this.getObject(context).sortMode)));
                })
                .action(context -> {
                    Data data = this.getObject(context);
                    SortMode nextMode = Lists.next(data.sortMode);
                    this.show(context.getPlayer(), data.userInfo, data.type, nextMode, data.showExpired);
                })
                .build()
            )
            .slots(43)
            .build()
        );

        this.addDefaultButton("show_expired", MenuItem.builder()
            .defaultState(ItemState.defaultBuilder()
                .icon(NightItem.fromType(Material.CLOCK)
                    .setDisplayName(YELLOW.wrap("Show Expired"))
                    .setLore(List.of(
                        DARK_GRAY.wrap("»" + GRAY.wrap(" Status: ") + WHITE.wrap(SLPlaceholders.GENERIC_STATE)),
                        "",
                        GRAY.wrap("Whether to show expired entries."),
                        "",
                        YELLOW.wrap("→ " + UNDERLINED.wrap("Click to toggle"))
                    ))
                )
                .displayModifier((context, item) -> {
                    item.replace(builder -> builder.with(SLPlaceholders.GENERIC_STATE, () -> CoreLang.STATE_ENABLED_DISALBED.get(this.getObject(context).showExpired)));
                })
                .action(context -> {
                    Data data = this.getObject(context);
                    this.show(context.getPlayer(), data.userInfo, data.type, data.sortMode, !data.showExpired);
                })
                .build()
            )
            .slots(37)
            .build()
        );
    }

    @Override
    protected void onLoad(@NotNull FileConfig config) {
        this.historyActiveIcon = ConfigProperty.of(ConfigTypes.NIGHT_ITEM, "History.Icon.Active", NightItem.fromType(Material.LIME_DYE)).resolveWithDefaults(config);
        this.historyPausedIcon = ConfigProperty.of(ConfigTypes.NIGHT_ITEM, "History.Icon.Paused", NightItem.fromType(Material.YELLOW_DYE)).resolveWithDefaults(config);
        this.histroyExpiredIcon = ConfigProperty.of(ConfigTypes.NIGHT_ITEM, "History.Icon.Expired", NightItem.fromType(Material.GRAY_DYE)).resolveWithDefaults(config);

        int[] historySlots = ConfigProperty.of(ConfigTypes.INT_ARRAY, "History.Slots", IntStream.range(9, 36).toArray()).resolveWithDefaults(config);

        this.populator = ItemPopulator.builder(PlayerPunishment.class)
            .slots(historySlots)
            .itemProvider((context, punishment) -> {
                Data data = this.getObject(context);
                NightItem icon;
                IconLocale locale;

                if (!punishment.isExpired()) {
                    icon = punishment.isActive() ? this.historyActiveIcon : this.historyPausedIcon;
                    locale = punishment.isActive() ? LOCALE_ACTIVE : LOCALE_PAUSED;
                }
                else {
                    icon = this.histroyExpiredIcon;
                    locale = LOCALE_EXPIRED;
                }

                return icon.copy()
                    .hideAllComponents()
                    .localized(locale)
                    .replace(builder -> builder
                        .with(SLPlaceholders.GENERIC_TYPE, () -> BansLang.PUNISHMENT_TYPE.getLocalized(data.type))
                        .with(punishment.placeholders())
                    );
            })
            .actionProvider(punishment -> actionContext -> {
                InventoryClickEvent event = actionContext.getEvent();
                Player player = actionContext.getPlayer();
                Data data = this.getObject(actionContext);

                if (event.getClick() == ClickType.DROP) {
                    if (!player.hasPermission(BansPerms.PUNISHMENT_DELETE)) return;

                    this.module.deletePlayerPunishment(punishment);
                }
                else if (event.isLeftClick()) {
                    if (!player.hasPermission(BansPerms.PUNISHMENT_TOGGLE)) return;
                    if (punishment.isExpired()) return;

                    punishment.setActive(!punishment.isActive());
                    punishment.markDirty();

                    this.module.getPunishmentRepository(data.type).updatePlayerPunishmentReferences(punishment);
                }
                else return;

                actionContext.getViewer().refresh();
            })
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
    public void onPrepare(@NotNull ViewerContext context, @NotNull InventoryView view, @NotNull Inventory inventory, @NotNull List<MenuItem> list) {
        Data data = this.getObject(context);
        List<PlayerPunishment> punishments = this.module.getPunishmentRepository(data.type).getPlayerPunishments(data.userInfo.id()).stream()
            .filter(punishment -> data.showExpired || !punishment.isExpired())
            .sorted(data.sortMode.comparator())
            .toList();

        this.populator.populateTo(context, punishments, list);
    }

    @Override
    public void onReady(@NotNull ViewerContext context, @NotNull InventoryView view, @NotNull Inventory inventory) {

    }

    @Override
    public void onRender(@NotNull ViewerContext context, @NotNull InventoryView view, @NotNull Inventory inventory) {

    }
}
