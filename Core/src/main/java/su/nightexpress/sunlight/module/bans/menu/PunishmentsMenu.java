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
import su.nightexpress.nightcore.locale.LangContainer;
import su.nightexpress.nightcore.locale.LangEntry;
import su.nightexpress.nightcore.locale.entry.EnumLocale;
import su.nightexpress.nightcore.locale.entry.IconLocale;
import su.nightexpress.nightcore.ui.inventory.item.ItemPopulator;
import su.nightexpress.nightcore.ui.inventory.item.ItemState;
import su.nightexpress.nightcore.ui.inventory.item.MenuItem;
import su.nightexpress.nightcore.ui.inventory.menu.AbstractObjectMenu;
import su.nightexpress.nightcore.ui.inventory.viewer.ViewerContext;
import su.nightexpress.nightcore.util.Lists;
import su.nightexpress.nightcore.util.bukkit.NightItem;
import su.nightexpress.nightcore.util.placeholder.PlaceholderContext;
import su.nightexpress.nightcore.util.profile.PlayerProfiles;
import su.nightexpress.sunlight.SLPlaceholders;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.module.bans.BansModule;
import su.nightexpress.sunlight.module.bans.config.BansLang;
import su.nightexpress.sunlight.module.bans.config.BansPerms;
import su.nightexpress.sunlight.module.bans.punishment.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static su.nightexpress.nightcore.util.text.night.wrapper.TagWrappers.*;
import static su.nightexpress.sunlight.module.bans.BansPlaceholders.*;

public class PunishmentsMenu extends AbstractObjectMenu<PunishmentsMenu.Data> implements LangContainer {

    private static final EnumLocale<PunishmentType> TYPE_LOCALE = LangEntry.builder("Bans.UI.List.TypeName").enumeration(PunishmentType.class);

    private final SunLightPlugin plugin;
    private final BansModule module;

    private static final IconLocale LOCALE_ACTIVE = LangEntry.iconBuilder("Bans.UI.List.Icon.Active")
        .rawName(WHITE.wrap(PUNISHMENT_TARGET) + DARK_GRAY.wrap(" • " + GREEN.wrap("Active")))
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

    private static final IconLocale LOCALE_PAUSED = LangEntry.iconBuilder("Bans.UI.List.Icon.Paused")
        .rawName(WHITE.wrap(PUNISHMENT_TARGET) + DARK_GRAY.wrap(" • " + YELLOW.wrap("Inactive")))
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

    private static final IconLocale LOCALE_EXPIRED = LangEntry.iconBuilder("Bans.UI.List.Icon.Expired")
        .rawName(WHITE.wrap(PUNISHMENT_TARGET) + DARK_GRAY.wrap(" • " + GREEN.wrap("Expired")))
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

    private NightItem playerIcon;
    private NightItem inetIcon;

    private ItemPopulator<AbstractPunishment> populator;

    public record Data(@NotNull PunishmentType type, @NotNull SortMode sortMode, boolean showExpired) {}

    public PunishmentsMenu(@NotNull SunLightPlugin plugin, @NotNull BansModule module) {
        super(MenuType.GENERIC_9X5, BLACK.wrap("[%s] List".formatted(SLPlaceholders.GENERIC_TYPE)), Data.class);
        this.plugin = plugin;
        this.module = module;

        this.plugin.injectLang(this);
    }

    public boolean show(@NotNull Player player, @NotNull PunishmentType type) {
        return this.show(player, type, SortMode.NEWEST, false);
    }

    private boolean show(@NotNull Player player, @NotNull PunishmentType type, @NotNull SortMode mode, boolean showExpired) {
        return this.show(this.plugin, player, new Data(type, mode, showExpired));
    }

    @Override
    @NotNull
    protected String getRawTitle(@NotNull ViewerContext context) {
        Data data = this.getObject(context);

        return PlaceholderContext.builder()
            .with(SLPlaceholders.GENERIC_TYPE, () -> TYPE_LOCALE.getLocalized(data.type))
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

        this.addDefaultButton("sort_mode", su.nightexpress.nightcore.ui.inventory.item.MenuItem.builder()
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
                    this.show(context.getPlayer(), data.type, nextMode, data.showExpired);
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
                    item.replace(builder -> builder.with(SLPlaceholders.GENERIC_STATE, () -> su.nightexpress.nightcore.core.config.CoreLang.STATE_ENABLED_DISALBED.get(this.getObject(context).showExpired)));
                })
                .action(context -> {
                    Data data = this.getObject(context);
                    this.show(context.getPlayer(), data.type, data.sortMode, !data.showExpired);
                })
                .build()
            )
            .slots(37)
            .build()
        );
    }

    @Override
    protected void onLoad(@NotNull FileConfig config) {
        this.playerIcon = ConfigProperty.of(ConfigTypes.NIGHT_ITEM, "Punishment.Icon.Player", NightItem.fromType(Material.PLAYER_HEAD)).resolveWithDefaults(config);
        this.inetIcon = ConfigProperty.of(ConfigTypes.NIGHT_ITEM, "Punishment.Icon.Inet", NightItem.fromType(Material.COMPARATOR)).resolveWithDefaults(config);

        int[] historySlots = ConfigProperty.of(ConfigTypes.INT_ARRAY, "History.Slots", IntStream.range(9, 36).toArray()).resolveWithDefaults(config);

        this.populator = ItemPopulator.builder(AbstractPunishment.class)
            .slots(historySlots)
            .itemProvider((context, punishment) -> {
                Data data = this.getObject(context);
                NightItem icon;
                IconLocale locale;

                if (punishment instanceof PlayerPunishment playerPunishment) {
                    icon = this.playerIcon.copy().setPlayerProfile(PlayerProfiles.createProfile(playerPunishment.getPlayerId(), playerPunishment.getPlayerName()));
                }
                else {
                    icon = this.inetIcon.copy();
                }

                if (!punishment.isExpired()) {
                    locale = punishment.isActive() ? LOCALE_ACTIVE : LOCALE_PAUSED;
                }
                else {
                    locale = LOCALE_EXPIRED;
                }

                return icon
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

                    if (punishment instanceof PlayerPunishment playerPunishment) {
                        this.module.deletePlayerPunishment(playerPunishment);
                    }
                    else if (punishment instanceof InetPunishment inetPunishment) {
                        this.module.deleteInetPunishment(inetPunishment);
                    }
                }
                else if (event.isLeftClick()) {
                    if (!player.hasPermission(BansPerms.PUNISHMENT_TOGGLE)) return;
                    if (punishment.isExpired()) return;

                    punishment.setActive(!punishment.isActive());
                    punishment.markDirty();

                    if (punishment instanceof PlayerPunishment playerPunishment) {
                        this.module.getPunishmentRepository(data.type).updatePlayerPunishmentReferences(playerPunishment);
                    }
                    else if (punishment instanceof InetPunishment inetPunishment) {
                        this.module.getPunishmentRepository(data.type).updateInetPunishmentReferences(inetPunishment);
                    }
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
        List<AbstractPunishment> punishments = new ArrayList<>();
        PunishmentRepository repository = this.module.getPunishmentRepository(data.type);

        punishments.addAll(repository.getPlayerPunishments());
        punishments.addAll(repository.getInetPunishments());
        if (!data.showExpired) {
            punishments.removeIf(AbstractPunishment::isExpired);
        }
        punishments.sort(data.sortMode.comparator());

        this.populator.populateTo(context, punishments, list);
    }

    @Override
    public void onReady(@NotNull ViewerContext context, @NotNull InventoryView view, @NotNull Inventory inventory) {

    }

    @Override
    public void onRender(@NotNull ViewerContext context, @NotNull InventoryView view, @NotNull Inventory inventory) {

    }
}
