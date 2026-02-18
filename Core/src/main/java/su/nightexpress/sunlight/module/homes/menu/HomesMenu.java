package su.nightexpress.sunlight.module.homes.menu;

import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.MenuType;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.bridge.item.AdaptedItem;
import su.nightexpress.nightcore.config.ConfigValue;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.locale.entry.IconLocale;
import su.nightexpress.nightcore.ui.inventory.item.ItemPopulator;
import su.nightexpress.nightcore.ui.inventory.item.MenuItem;
import su.nightexpress.nightcore.ui.inventory.menu.AbstractObjectMenu;
import su.nightexpress.nightcore.ui.inventory.viewer.ViewerContext;
import su.nightexpress.nightcore.util.bukkit.NightItem;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.module.homes.HomesFiles;
import su.nightexpress.sunlight.module.homes.HomesModule;
import su.nightexpress.sunlight.module.homes.config.HomesLang;
import su.nightexpress.sunlight.module.homes.impl.Home;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

import static su.nightexpress.nightcore.util.text.night.wrapper.TagWrappers.*;

public class HomesMenu extends AbstractObjectMenu<UUID> {

    private final HomesModule module;

    private int totalSlots;

    private ItemPopulator<Home> homePopulator;
    private ItemPopulator<Home> teleportPopulator;
    private ItemPopulator<Integer> lockPopulator;

    public HomesMenu(@NotNull SunLightPlugin plugin, @NotNull HomesModule module) {
        super(MenuType.GENERIC_9X6, BLACK.wrap("Homes"), UUID.class);
        this.module = module;

        this.load(plugin, FileConfig.load(module.getLocalUIPath(), HomesFiles.FILE_UI_HOME_LIST));
    }

    @Override
    public void registerActions() {

    }

    @Override
    public void registerConditions() {

    }

    @Override
    public void defineDefaultLayout() {
        this.addNextPageItem(Material.ARROW, 53);
        this.addPreviousPageItem(Material.ARROW, 45);
        this.addBackgroundItem(Material.BLACK_STAINED_GLASS_PANE, IntStream.range(0, 9).toArray());
        this.addBackgroundItem(Material.BLACK_STAINED_GLASS_PANE, IntStream.range(45, 54).toArray());
    }

    @Override
    protected void onLoad(@NotNull FileConfig config) {
        this.totalSlots = ConfigValue.create("Homes.Total-Slots", 14).read(config);

        int[] homeSlots = ConfigValue.create("Homes.Slots-Homes", IntStream.range(19, 26).toArray()).read(config);
        int[] teleportSlots = ConfigValue.create("Homes.Slots-Teleport", IntStream.range(28, 35).toArray()).read(config);

        NightItem teleportIcon = ConfigValue.create("Homes.Icon-Teleport", NightItem.fromType(Material.ENDER_PEARL)).read(config);
        NightItem lockedIcon = ConfigValue.create("Homes.Icon-Locked", NightItem.fromType(Material.IRON_BARS)).read(config);
        NightItem emptyIcon = ConfigValue.create("Homes.Icon-Empty", NightItem.fromType(Material.GRAY_DYE)).read(config);

        this.homePopulator = ItemPopulator.builder(Home.class)
            .actionProvider(home -> context -> {
                this.module.openHomeSettings(context.getPlayer(), home);
            })
            .itemProvider((context, home) -> {
                AdaptedItem adaptedItem = this.module.getSettings().getIconOrDefault(home.getIconId());
                NightItem item = adaptedItem.itemStack().map(NightItem::fromItemStack).orElse(NightItem.fromType(Material.RED_BED));
                IconLocale locale = home.isFavorite() ? HomesLang.UI_HOMES_FAVORITE : HomesLang.UI_HOMES_NORMAL;

                return item
                    .localized(locale)
                    .hideAllComponents()
                    .replace(builder -> builder.with(home.placeholders()));
            })
            .slots(homeSlots)
            .build();

        this.teleportPopulator = ItemPopulator.builder(Home.class)
            .actionProvider(home -> context -> {
                context.getPlayer().closeInventory();
                this.module.teleportToHome(context.getPlayer(), home);
            })
            .itemProvider((context, home) -> {
                if (!home.isActive()) return NightItem.fromType(Material.AIR);

                return teleportIcon
                    .copy()
                    .localized(HomesLang.UI_HOMES_TELEPORT)
                    .hideAllComponents()
                    .replace(builder -> builder.with(home.placeholders()));
            })
            .slots(teleportSlots)
            .build();

        this.lockPopulator = ItemPopulator.builder(Integer.class)
            .actionProvider(slot -> context -> {
                int maxHomes = this.module.getMaxHomesValue(context.getPlayer());
                int finedSlot = slot + 1;
                boolean isLocked = maxHomes >= 0 && finedSlot > maxHomes;
                if (isLocked) return;

                this.module.setHome(context.getPlayer(), String.valueOf(finedSlot), false);
                context.getViewer().refresh();
            })
            .itemProvider((context, slot) -> {
                int maxHomes = this.module.getMaxHomesValue(context.getPlayer());
                int finedSlot = slot + 1;
                boolean isLocked = maxHomes >= 0 && finedSlot > maxHomes;
                return (isLocked ? lockedIcon : emptyIcon).copy().localized(isLocked ? HomesLang.UI_HOMES_LOCKED : HomesLang.UI_HOMES_EMPTY).hideAllComponents();
            })
            .slots(homeSlots)
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
        UUID targetId = this.getObject(context);

        List<Home> homes = this.module.getHomes(targetId).stream().sorted(Comparator.comparing(Home::getId)).toList();
        List<Integer> slots = IntStream.range(0, this.totalSlots).boxed().toList();

        this.lockPopulator.populateTo(context, slots, items);
        this.homePopulator.populateTo(context, homes, items);
        this.teleportPopulator.populateTo(context, homes, items);
    }

    @Override
    public void onReady(@NotNull ViewerContext context, @NotNull InventoryView view, @NotNull Inventory inventory) {

    }

    @Override
    public void onRender(@NotNull ViewerContext context, @NotNull InventoryView view, @NotNull Inventory inventory) {

    }
}
