package su.nightexpress.sunlight.module.kits.menu;

import org.bukkit.Material;
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
import su.nightexpress.nightcore.ui.inventory.item.MenuItem;
import su.nightexpress.nightcore.ui.inventory.menu.AbstractObjectMenu;
import su.nightexpress.nightcore.ui.inventory.viewer.ViewerContext;
import su.nightexpress.nightcore.util.bukkit.NightItem;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.module.kits.KitFiles;
import su.nightexpress.sunlight.module.kits.KitsModule;
import su.nightexpress.sunlight.module.kits.model.Kit;
import su.nightexpress.sunlight.module.kits.model.KitContent;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static su.nightexpress.nightcore.util.text.night.wrapper.TagWrappers.*;

public class KitPreviewMenu extends AbstractObjectMenu<Kit> {

    private final KitsModule module;

    private List<Integer> fusedSlots;

    public KitPreviewMenu(@NotNull SunLightPlugin plugin, @NotNull KitsModule module) {
        super(MenuType.GENERIC_9X6, "Kit Preview", Kit.class);
        this.module = module;

        this.load(plugin, FileConfig.load(module.getLocalUIPath(), KitFiles.FILE_UI_KIT_PREVIEW));
    }

    @Override
    public void registerActions() {

    }

    @Override
    public void registerConditions() {

    }

    @Override
    public void defineDefaultLayout() {
        this.addBackgroundItem(Material.GRAY_STAINED_GLASS_PANE, IntStream.range(5, 9).toArray());
        this.addBackgroundItem(Material.BLACK_STAINED_GLASS_PANE, IntStream.range(9, 18).toArray());

        this.addDefaultButton("return", MenuItem.builder()
            .defaultState(NightItem.fromType(Material.ARROW).setDisplayName(WHITE.wrap("Back to Kits")), context -> {
                this.module.openKitsMenu(context.getPlayer());
            })
            .slots(8)
            .build()
        );
    }

    @Override
    protected void onLoad(@NotNull FileConfig config) {
        int[] hotbarSlots = ConfigProperty.of(ConfigTypes.INT_ARRAY, "Content.Hotbar-Slots", IntStream.range(45, 54).toArray()).resolveWithDefaults(config);
        int[] itemSlots = ConfigProperty.of(ConfigTypes.INT_ARRAY, "Content.Inventory-Slots", IntStream.range(18, 45).toArray()).resolveWithDefaults(config);
        int[] armorSlots = ConfigProperty.of(ConfigTypes.INT_ARRAY, "Content.Armor-Slots", IntStream.range(0, 4).toArray()).resolveWithDefaults(config);
        int[] extraSlots = ConfigProperty.of(ConfigTypes.INT_ARRAY, "Content.Extra-Slots", new int[]{4}).resolveWithDefaults(config);

        this.fusedSlots = new ArrayList<>();
        this.fusedSlots.addAll(IntStream.of(hotbarSlots).boxed().toList());
        this.fusedSlots.addAll(IntStream.of(itemSlots).boxed().toList());
        this.fusedSlots.addAll(IntStream.of(armorSlots).boxed().toList());
        this.fusedSlots.addAll(IntStream.of(extraSlots).boxed().toList());
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
        Kit kit = this.getObject(context);
        KitContent content = kit.definition().getContent();

        content.getItemBySlotMap().forEach((slotIndex, item) -> {
            if (slotIndex >= this.fusedSlots.size()) return;

            int slot = this.fusedSlots.get(slotIndex);

            item.itemStack().ifPresent(itemStack -> {
                inventory.setItem(slot, itemStack);
            });
        });
    }

    @Override
    public void onReady(@NotNull ViewerContext context, @NotNull InventoryView view, @NotNull Inventory inventory) {

    }

    @Override
    public void onRender(@NotNull ViewerContext context, @NotNull InventoryView view, @NotNull Inventory inventory) {

    }
}
