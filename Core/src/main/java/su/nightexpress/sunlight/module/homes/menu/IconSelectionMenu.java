package su.nightexpress.sunlight.module.homes.menu;

import static su.nightexpress.nightcore.util.text.night.wrapper.TagWrappers.WHITE;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MenuType;
import org.jspecify.annotations.NonNull;

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

public class IconSelectionMenu extends AbstractObjectMenu<Home> {

    private final HomesModule module;

    private ItemPopulator<HomeIcon> iconPopulator;

    private record HomeIcon(@NonNull String id, @NonNull AdaptedItem adaptedItem) {
    }

    public IconSelectionMenu(@NonNull SunLightPlugin plugin, @NonNull HomesModule module) {
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

        this.addDefaultButton("return", MenuItem.button()
            .defaultState(ItemState.builder()
                .icon(NightItem.fromType(Material.IRON_DOOR).setDisplayName(WHITE.wrap("Return")))
                .action(context -> this.module.openHomeSettings(context.getPlayer(), this.getObject(context)))
                .build()
            )
            .slots(31)
            .build()
        );
    }

    @Override
    protected void onLoad(@NonNull FileConfig config) {
        int[] iconSlots = ConfigProperty.of(ConfigTypes.INT_ARRAY, "Icons.Slots", IntStream.range(0, 27).toArray())
            .resolveWithDefaults(config);

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
                    .replace(builder -> builder.with(SLPlaceholders.GENERIC_NAME, () -> ItemUtil.getNameSerialized(
                        itemStack)))
                    .hideAllComponents();
            })
            .slots(iconSlots)
            .build();
    }

    @Override
    protected void onClick(@NonNull ViewerContext context, @NonNull InventoryClickEvent event) {

    }

    @Override
    protected void onDrag(@NonNull ViewerContext context, @NonNull InventoryDragEvent event) {

    }

    @Override
    protected void onClose(@NonNull ViewerContext context, @NonNull InventoryCloseEvent event) {

    }

    @Override
    public void onPrepare(@NonNull ViewerContext context, @NonNull InventoryView view, @NonNull Inventory inventory, @NonNull List<MenuItem> items) {
        List<HomeIcon> icons = new ArrayList<>();

        this.module.getSettings().getIconPresets().forEach((id, item) -> {
            icons.add(new HomeIcon(id, item));
        });

        this.iconPopulator.populateTo(context, icons, items);
    }

    @Override
    public void onReady(@NonNull ViewerContext context, @NonNull InventoryView view, @NonNull Inventory inventory) {

    }

    @Override
    public void onRender(@NonNull ViewerContext context, @NonNull InventoryView view, @NonNull Inventory inventory) {

    }
}
