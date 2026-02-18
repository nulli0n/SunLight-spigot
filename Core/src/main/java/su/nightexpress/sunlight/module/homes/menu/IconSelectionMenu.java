package su.nightexpress.sunlight.module.homes.menu;

import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MenuType;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.bridge.item.AdaptedItem;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.configuration.ConfigProperty;
import su.nightexpress.nightcore.configuration.ConfigTypes;
import su.nightexpress.nightcore.ui.inventory.item.ItemPopulator;
import su.nightexpress.nightcore.ui.inventory.item.ItemState;
import su.nightexpress.nightcore.ui.inventory.item.MenuItem;
import su.nightexpress.nightcore.ui.inventory.menu.AbstractObjectMenu;
import su.nightexpress.nightcore.ui.inventory.viewer.ViewerContext;
import su.nightexpress.nightcore.util.ItemUtil;
import su.nightexpress.nightcore.util.bukkit.NightItem;
import su.nightexpress.sunlight.SLPlaceholders;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.module.homes.HomesFiles;
import su.nightexpress.sunlight.module.homes.HomesModule;
import su.nightexpress.sunlight.module.homes.config.HomesLang;
import su.nightexpress.sunlight.module.homes.impl.Home;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static su.nightexpress.nightcore.util.text.night.wrapper.TagWrappers.WHITE;

public class IconSelectionMenu extends AbstractObjectMenu<Home> {

    private final HomesModule module;

    private ItemPopulator<HomeIcon> iconPopulator;

    private record HomeIcon(@NotNull String id, @NotNull AdaptedItem adaptedItem) {}

    public IconSelectionMenu(@NotNull SunLightPlugin plugin, @NotNull HomesModule module) {
        super(MenuType.GENERIC_9X4, "Icon Selection", Home.class);
        this.module = module;

        this.load(plugin, FileConfig.load(module.getLocalUIPath(), HomesFiles.FILE_UI_HOME_ICON));
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
    }

    @Override
    protected void onLoad(@NotNull FileConfig config) {
        int[] iconSlots = ConfigProperty.of(ConfigTypes.INT_ARRAY, "Icons.Slots", IntStream.range(0, 27).toArray()).resolveWithDefaults(config);

        this.iconPopulator = ItemPopulator.builder(HomeIcon.class)
            .actionProvider(homeIcon -> context -> {
                Home home = this.getObject(context);

                home.setIconId(homeIcon.id());
                home.markDirty();

                this.module.openHomeSettings(context.getPlayer(), this.getObject(context));
            })
            .itemProvider((context, homeIcon) -> {
                ItemStack itemStack = homeIcon.adaptedItem.getItemStack();
                if (itemStack == null) return NightItem.fromType(Material.AIR);

                return NightItem.fromItemStack(itemStack)
                    .localized(HomesLang.UI_ICON_SELECTION_ICON)
                    .replace(builder -> builder.with(SLPlaceholders.GENERIC_NAME, () -> ItemUtil.getNameSerialized(itemStack)))
                    .hideAllComponents();
            })
            .slots(iconSlots)
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
        List<HomeIcon> icons = new ArrayList<>();

        this.module.getSettings().getIconPresets().forEach((id, item) -> {
            icons.add(new HomeIcon(id, item));
        });

        this.iconPopulator.populateTo(context, icons, items);
    }

    @Override
    public void onReady(@NotNull ViewerContext context, @NotNull InventoryView view, @NotNull Inventory inventory) {

    }

    @Override
    public void onRender(@NotNull ViewerContext context, @NotNull InventoryView view, @NotNull Inventory inventory) {

    }
}
