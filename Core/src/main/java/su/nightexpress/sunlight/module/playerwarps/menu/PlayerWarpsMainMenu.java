package su.nightexpress.sunlight.module.playerwarps.menu;

import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.MenuType;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;
import su.nightexpress.nightcore.bridge.currency.Currency;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.configuration.ConfigProperty;
import su.nightexpress.nightcore.configuration.ConfigTypes;
import su.nightexpress.nightcore.locale.LangContainer;
import su.nightexpress.nightcore.locale.LangEntry;
import su.nightexpress.nightcore.locale.entry.IconLocale;
import su.nightexpress.nightcore.ui.inventory.action.MenuItemAction;
import su.nightexpress.nightcore.ui.inventory.item.ItemPopulator;
import su.nightexpress.nightcore.ui.inventory.item.ItemState;
import su.nightexpress.nightcore.ui.inventory.item.MenuItem;
import su.nightexpress.nightcore.ui.inventory.menu.AbstractMenu;
import su.nightexpress.nightcore.ui.inventory.viewer.ViewerContext;
import su.nightexpress.nightcore.util.Lists;
import su.nightexpress.nightcore.util.NumberUtil;
import su.nightexpress.nightcore.util.bukkit.NightItem;
import su.nightexpress.nightcore.util.time.TimeFormatType;
import su.nightexpress.nightcore.util.time.TimeFormats;
import su.nightexpress.sunlight.module.playerwarps.PlayerWarpsModule;
import su.nightexpress.sunlight.module.playerwarps.PlayerWarpsPlaceholders;
import su.nightexpress.sunlight.module.playerwarps.core.PlayerWarpsSettings;
import su.nightexpress.sunlight.module.playerwarps.category.NormalCategory;
import su.nightexpress.sunlight.module.playerwarps.featuring.FeaturedData;
import su.nightexpress.sunlight.module.playerwarps.featuring.FeaturedSlot;
import su.nightexpress.sunlight.module.playerwarps.PlayerWarp;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

import static su.nightexpress.nightcore.util.text.night.wrapper.TagWrappers.*;
import static su.nightexpress.sunlight.SLPlaceholders.*;
import static su.nightexpress.sunlight.module.warps.WarpsPlaceholders.*;

public class PlayerWarpsMainMenu extends AbstractMenu implements LangContainer {

    private static final IconLocale ICON_CATEGORY = LangEntry.iconBuilder("PlayerWarps.UI.MainMenu.WarpCategory")
        .rawName(PlayerWarpsPlaceholders.CATEGORY_NAME)
        .rawLore(
            DARK_GRAY.wrap("» ") + GRAY.wrap("Warps: " + WHITE.wrap(GENERIC_AMOUNT)),
            "",
            PlayerWarpsPlaceholders.CATEGORY_DESCRIPTION,
            "",
            GOLD.wrap("→ " + UNDERLINED.wrap("Click to browse"))
        )
        .build();

    private static final IconLocale ICON_FEATURED_WARP = LangEntry.iconBuilder("PlayerWarps.UI.MainMenu.FeaturedWarp")
        .rawName(YELLOW.wrap("⭐") + " " + WARP_NAME + " " + YELLOW.wrap("⭐"))
        .rawLore(
            YELLOW.wrap("Featured: " + GENERIC_TIME),
            "",
            DARK_GRAY.wrap("» ") + GRAY.wrap("Owner: " + WHITE.wrap(PlayerWarpsPlaceholders.WARP_OWNER_NAME)),
            DARK_GRAY.wrap("» ") + GRAY.wrap("Visits: " + WHITE.wrap(PlayerWarpsPlaceholders.WARP_VISITS)),
            DARK_GRAY.wrap("» ") + GRAY.wrap("Description:"),
            WARP_DESCRIPTION,
            "",
            YELLOW.wrap("→ " + UNDERLINED.wrap("Click to teleport"))
        )
        .build();

    private static final IconLocale ICON_POPULAR_WARP = LangEntry.iconBuilder("PlayerWarps.UI.MainMenu.PopularWarp")
        .rawName(SOFT_AQUA.wrap("[Top #" + GENERIC_VALUE + "]") + " " + WARP_NAME)
        .rawLore(
            SOFT_AQUA.wrap("Most Visited Warp #" + GENERIC_VALUE),
            "",
            DARK_GRAY.wrap("» ") + GRAY.wrap("Owner: " + WHITE.wrap(PlayerWarpsPlaceholders.WARP_OWNER_NAME)),
            DARK_GRAY.wrap("» ") + GRAY.wrap("Visits: " + WHITE.wrap(PlayerWarpsPlaceholders.WARP_VISITS)),
            DARK_GRAY.wrap("» ") + GRAY.wrap("Description:"),
            WARP_DESCRIPTION,
            "",
            SOFT_AQUA.wrap("→ " + UNDERLINED.wrap("Click to teleport"))
        )
        .build();

    private final PlayerWarpsModule module;
    private final PlayerWarpsSettings settings;

    private final MenuItemAction viewOwnAction;
    private final MenuItemAction viewAllAction;

    private ItemPopulator<NormalCategory> categoryPopulator;

    private NightItem featuredIcon;
    private NightItem popularIcon;

    public PlayerWarpsMainMenu(@NotNull PlayerWarpsModule module, @NonNull PlayerWarpsSettings settings) {
        super(MenuType.GENERIC_9X6, "Player Warps");
        this.module = module;
        this.settings = settings;

        this.viewOwnAction = this::viewOwnWarps;
        this.viewAllAction = this::viewAllWarps;
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
        this.addBackgroundItem(Material.BLACK_STAINED_GLASS_PANE, IntStream.range(45, 54).toArray());

        this.addBackgroundItem(Material.GRAY_STAINED_GLASS_PANE, IntStream.range(19, 26).toArray());
        this.addBackgroundItem(Material.GRAY_STAINED_GLASS_PANE, IntStream.range(28, 35).toArray());

        this.addDefaultButton("view_own", MenuItem.builder()
            .defaultState(ItemState.defaultBuilder()
                .icon(NightItem.fromType(Material.PLAYER_HEAD)
                    .setDisplayName(GREEN.and(BOLD).wrap("View Own Warps"))
                    .setLore(Lists.newList(
                        DARK_GRAY.wrap("» " + GRAY.wrap("Warps: ") + WHITE.wrap(GENERIC_AMOUNT)),
                        "",
                        GRAY.wrap("View all warps created by you."),
                        "",
                        GREEN.wrap("→ " + UNDERLINED.wrap("Click to open"))
                    ))
                    .hideAllComponents()
                )
                .displayModifier((context, item) -> item
                    .setPlayerProfile(context.getPlayer())
                    .replace(builder -> builder
                        .with(GENERIC_AMOUNT, () -> NumberUtil.format(this.module.getRepository().countOwnedWarps(context.getPlayer().getUniqueId())))
                    )
                )
                .action(this.viewOwnAction)
                .build()
            )
            .slots(45)
            .build()
        );

        this.addDefaultButton("view_all", MenuItem.builder()
            .defaultState(ItemState.defaultBuilder()
                .icon(NightItem.fromType(Material.ENDER_EYE)
                    .setDisplayName(GREEN.and(BOLD).wrap("View All Warps"))
                    .setLore(Lists.newList(
                        DARK_GRAY.wrap("» " + GRAY.wrap("Warps: ") + WHITE.wrap(GENERIC_AMOUNT)),
                        "",
                        GRAY.wrap("View all the player warps."),
                        "",
                        GREEN.wrap("→ " + UNDERLINED.wrap("Click to open"))
                    ))
                    .hideAllComponents()
                )
                .displayModifier((context, item) -> item.replace(builder -> builder
                    .with(GENERIC_AMOUNT, () -> NumberUtil.format(this.module.getRepository().size()))
                ))
                .action(this.viewAllAction)
                .build()
            )
            .slots(53)
            .build()
        );
    }

    @Override
    protected void onLoad(@NotNull FileConfig config) {
        int[] categorySlots = ConfigProperty.of(ConfigTypes.INT_ARRAY, "Category.Slots", new int[]{19,20,21,22,23,24,25, 28,29,30,31,32,33,34}).resolveWithDefaults(config);

        this.categoryPopulator = ItemPopulator.builder(NormalCategory.class)
            .itemProvider((context, category) -> category.icon()
                .hideAllComponents()
                .localized(ICON_CATEGORY)
                .replace(builder -> builder
                    .with(category.placeholders())
                    .with(GENERIC_AMOUNT, () -> NumberUtil.format(this.module.getRepository().countWarps(category)))
                )
            )
            .actionProvider(category -> context -> this.viewWarps(context, category))
            .slots(categorySlots)
            .build();

        this.featuredIcon = ConfigProperty.of(ConfigTypes.NIGHT_ITEM, "Featured.Icon", NightItem.fromType(Material.YELLOW_DYE)
            .setDisplayName(YELLOW.wrap("⭐" + BOLD.wrap(" Featured Slot ") + "⭐"))
            .setLore(Lists.newList(
                GRAY.wrap("Purchase this slot for " + YELLOW.wrap(PlayerWarpsPlaceholders.SLOT_PRICE) + " to"),
                GRAY.wrap("feature your warp for " + WHITE.wrap(PlayerWarpsPlaceholders.SLOT_DURATION) + "!"),
                "",
                YELLOW.wrap("→ " + UNDERLINED.wrap("Click to purchase"))
            ))
        ).resolveWithDefaults(config);

        this.popularIcon = ConfigProperty.of(ConfigTypes.NIGHT_ITEM, "Popular.Icon", NightItem.fromType(Material.GRAY_DYE)
            .setDisplayName(GRAY.wrap("Most Visited Warp #" + GENERIC_VALUE))
            .setLore(Lists.newList(
                GRAY.wrap("Nothing yet.")
            ))
        ).resolveWithDefaults(config);
    }

    private void viewWarps(@NonNull ViewerContext context, @NonNull NormalCategory category) {
        this.module.openWarpsList(context.getPlayer(), category, null, null);
    }

    private void viewOwnWarps(@NonNull ViewerContext context) {
        this.module.openOwnWarpsList(context.getPlayer(), context.getPlayer().getUniqueId());
    }

    private void viewAllWarps(@NonNull ViewerContext context) {
        this.module.openAllWarpsList(context.getPlayer());
    }

    private void displayFeaturedWarps(@NotNull Inventory inventory, @NotNull List<MenuItem> items) {
        Set<Integer> usedSlots = new HashSet<>();

        this.module.getRepository().getFeaturedWarps().forEach(warp -> {
            FeaturedData data = warp.getFeaturedData();
            if (data == null || !data.isActive()) return;

            FeaturedSlot slot = this.settings.getFeaturingSlot(data.slotId());
            if (slot == null) return;

            int slotIndex = data.slotIndex();
            int[] inventorySlots = slot.inventorySlots();
            if (slotIndex >= inventorySlots.length) return;

            int inventorySlot = inventorySlots[slotIndex];
            if (inventorySlot >= inventory.getSize()) return;

            items.add(MenuItem.builder()
                .defaultState(ItemState.defaultBuilder()
                    .icon(warp.getIcon()
                        .localized(ICON_FEATURED_WARP)
                        .replace(builder -> builder
                            .with(warp.placeholders())
                            .with(GENERIC_TIME, () -> TimeFormats.formatDuration(data.endTimestamp(), TimeFormatType.LITERAL))
                        )
                    )
                    .action(context -> this.module.clickWarp(context, warp))
                    .build()
                )
                .slots(inventorySlot)
                .build()
            );

            usedSlots.add(inventorySlot);
        });

        this.settings.getFeaturingSlotMap().forEach((id, slot) -> {
            Currency currency = slot.currency().orElse(null);
            if (currency == null) return;

            for (int index = 0; index < slot.inventorySlots().length; index++) {
                int slotIndex = index;
                int inventorySlot = slot.inventorySlots()[index];

                if (usedSlots.contains(inventorySlot)) continue;

                items.add(MenuItem.builder()
                    .defaultState(ItemState.defaultBuilder()
                        .icon(this.featuredIcon.copy().replace(builder -> builder.with(slot.placeholders())))
                        .action(context -> this.module.openFeaturingDialog(context.getPlayer(), slot, slotIndex))
                        .build()
                    )
                    .slots(inventorySlot)
                    .build()
                );
            }
        });
    }

    private void displayPopularWarps(@NotNull List<MenuItem> items) {
        List<PlayerWarp> popularWarps = this.module.getRepository().getPopularWarps();
        int[] popularSlots = this.settings.getPopularSlots();

        for (int index = 0; index < popularSlots.length; index++) {
            int top = index + 1;
            int inventorySlot = popularSlots[index];

            if (index < popularWarps.size()) {
                PlayerWarp warp = popularWarps.get(index);

                items.add(MenuItem.builder()
                    .defaultState(ItemState.defaultBuilder()
                        .icon(warp.getIcon()
                            .localized(ICON_POPULAR_WARP)
                            .replace(builder -> builder
                                .with(warp.placeholders())
                                .with(GENERIC_VALUE, () -> String.valueOf(top))
                            )
                        )
                        .action(context -> this.module.clickWarp(context, warp))
                        .build()
                    )
                    .slots(inventorySlot)
                    .build()
                );
            }
            else {
                items.add(MenuItem.builder()
                    .defaultState(this.popularIcon.copy().replace(builder -> builder.with(GENERIC_VALUE, () -> String.valueOf(top))))
                    .slots(inventorySlot)
                    .build()
                );
            }
        }
    }

    private void displayCategories(@NotNull ViewerContext context, @NotNull List<MenuItem> items) {
        List<NormalCategory> categories = this.settings.getCategories().stream().sorted(Comparator.comparing(NormalCategory::id)).toList();

        this.categoryPopulator.populateTo(context, categories, items);
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
        if (this.settings.isFeaturingEnabled()) {
            this.displayFeaturedWarps(inventory, items);
        }

        if (this.settings.isPopularEnabled()) {
            this.displayPopularWarps(items);
        }

        this.displayCategories(context, items);
    }

    @Override
    public void onReady(@NotNull ViewerContext context, @NotNull InventoryView view, @NotNull Inventory inventory) {

    }

    @Override
    public void onRender(@NotNull ViewerContext context, @NotNull InventoryView view, @NotNull Inventory inventory) {

    }
}
