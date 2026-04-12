package su.nightexpress.sunlight.module.kits.editor;

import static su.nightexpress.nightcore.util.text.night.wrapper.TagWrappers.GREEN;
import static su.nightexpress.nightcore.util.text.night.wrapper.TagWrappers.RED;
import static su.nightexpress.nightcore.util.text.night.wrapper.TagWrappers.YELLOW;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.IntStream;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MenuType;
import org.bukkit.inventory.PlayerInventory;
import org.jspecify.annotations.NonNull;

import su.nightexpress.nightcore.NightPlugin;
import su.nightexpress.nightcore.bridge.item.AdaptedItem;
import su.nightexpress.nightcore.bridge.item.ItemAdapter;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.integration.item.ItemBridge;
import su.nightexpress.nightcore.locale.LangContainer;
import su.nightexpress.nightcore.locale.LangEntry;
import su.nightexpress.nightcore.locale.entry.IconLocale;
import su.nightexpress.nightcore.ui.inventory.action.ActionContext;
import su.nightexpress.nightcore.ui.inventory.item.MenuItem;
import su.nightexpress.nightcore.ui.inventory.menu.AbstractObjectMenu;
import su.nightexpress.nightcore.ui.inventory.viewer.ViewerContext;
import su.nightexpress.nightcore.util.bukkit.NightItem;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.module.kits.KitsModule;
import su.nightexpress.sunlight.module.kits.config.KitsLang;
import su.nightexpress.sunlight.module.kits.model.Kit;
import su.nightexpress.sunlight.module.kits.model.KitContent;

public class KitContentEditorMenu extends AbstractObjectMenu<KitContentEditorMenu.Data> implements LangContainer {

    public record Data(@NonNull Kit kit, @NonNull KitContent contentCopy) {
    }

    private static final IconLocale ICON_SAVE = LangEntry.iconBuilder("Kits.UI.Editor.KitContent.Save")
        .accentColor(GREEN)
        .name("Save").br()
        .appendInfo("Saves changes.").br()
        .appendClick("Click to save")
        .build();

    private static final IconLocale ICON_CANCEL = LangEntry.iconBuilder("Kits.UI.Editor.KitContent.Cancel")
        .accentColor(RED)
        .name("Cancel").br()
        .appendInfo("Discard changes and return.").br()
        .appendClick("Click to cancel")
        .build();

    private static final IconLocale ICON_COPY_INVENTORY = LangEntry.iconBuilder(
        "Kits.UI.Editor.KitContent.CopyInventory")
        .accentColor(YELLOW)
        .name("Copy Inventory").br()
        .appendInfo("Copies your whole inventory.").br()
        .appendClick("Click to copy")
        .build();

    private static final int[] HOTBAR_SLOTS    = IntStream.range(45, 54).toArray();
    private static final int[] INVENTORY_SLOTS = IntStream.range(18, 45).toArray();
    private static final int[] ARMOR_SLOTS     = IntStream.range(0, 4).toArray();
    private static final int[] EXTRA_SLOTS     = {4};

    private static final List<Integer> FUSED_SLOTS = new ArrayList<>();

    static {
        FUSED_SLOTS.addAll(IntStream.of(HOTBAR_SLOTS).boxed().toList());
        FUSED_SLOTS.addAll(IntStream.of(INVENTORY_SLOTS).boxed().toList());
        FUSED_SLOTS.addAll(IntStream.of(ARMOR_SLOTS).boxed().toList());
        FUSED_SLOTS.addAll(IntStream.of(EXTRA_SLOTS).boxed().toList());
    }

    private final KitsModule module;

    public KitContentEditorMenu(@NonNull SunLightPlugin plugin, @NonNull KitsModule module) {
        super(MenuType.GENERIC_9X6, KitsLang.EDITOR_TITLE_CONTENT.text(), Data.class);
        this.module = module;

        plugin.injectLang(this);
        this.load(plugin);
    }

    public boolean show(@NonNull NightPlugin plugin, @NonNull Player player, @NonNull Kit kit) {
        return this.show(plugin, player, new Data(kit, KitContent.copyOf(kit.definition().getContent())));
    }

    @Override
    public void registerActions() {

    }

    @Override
    public void registerConditions() {

    }

    @Override
    public void defineDefaultLayout() {
        this.addBackgroundItem(Material.BLACK_STAINED_GLASS_PANE, IntStream.range(9, 18).toArray());
        this.addBackgroundItem(Material.GRAY_STAINED_GLASS_PANE, 5);

        this.addDefaultButton("copy", MenuItem.button()
            .defaultState(NightItem.fromType(Material.NETHER_STAR).localized(ICON_COPY_INVENTORY), this::copyInventory)
            .slots(6)
            .build()
        );

        this.addDefaultButton("save", MenuItem.button()
            .defaultState(NightItem.fromType(Material.LIME_WOOL).localized(ICON_SAVE), this::save)
            .slots(7)
            .build()
        );

        this.addDefaultButton("cancel", MenuItem.button()
            .defaultState(NightItem.fromType(Material.RED_WOOL).localized(ICON_CANCEL), context -> {
                this.module.openSettingsEditor(context.getPlayer(), this.getObject(context).kit);
            })
            .slots(8)
            .build()
        );
    }

    @Override
    protected void onLoad(@NonNull FileConfig config) {

    }

    @Override
    protected void onClick(@NonNull ViewerContext context, @NonNull InventoryClickEvent event) {
        if (event.getRawSlot() > event.getInventory().getSize() || FUSED_SLOTS.contains(event.getRawSlot())) {
            event.setCancelled(false);
            context.getViewer().setNextClickIn(0L); // Remove click cooldown.
        }
    }

    @Override
    protected void onDrag(@NonNull ViewerContext context, @NonNull InventoryDragEvent event) {

    }

    @Override
    protected void onClose(@NonNull ViewerContext context, @NonNull InventoryCloseEvent event) {

    }

    @Override
    public void onPrepare(@NonNull ViewerContext context, @NonNull InventoryView view, @NonNull Inventory inventory, @NonNull List<MenuItem> items) {
        Data data = this.getObject(context);
        KitContent content = data.contentCopy;

        content.getItemBySlotMap().forEach((slotIndex, adaptedItem) -> {
            int slot = FUSED_SLOTS.get(slotIndex);

            adaptedItem.itemStack().ifPresent(itemStack -> {
                inventory.setItem(slot, itemStack);
            });
        });
    }

    @Override
    public void onReady(@NonNull ViewerContext context, @NonNull InventoryView view, @NonNull Inventory inventory) {

    }

    @Override
    public void onRender(@NonNull ViewerContext context, @NonNull InventoryView view, @NonNull Inventory inventory) {

    }

    private void save(@NonNull ActionContext context) {
        Data data = this.getObject(context);
        Kit kit = data.kit;
        KitContent content = data.contentCopy;
        Inventory inventory = context.getEvent().getInventory();

        this.transferItems(content, inventory, FUSED_SLOTS::get);

        kit.definition().setContent(content);
        kit.markDirty();
        this.module.openSettingsEditor(context.getPlayer(), kit);
    }

    private void copyInventory(@NonNull ActionContext context) {
        Data data = this.getObject(context);
        KitContent content = data.contentCopy;
        PlayerInventory inventory = context.getPlayer().getInventory();

        this.transferItems(content, inventory, slot -> slot);

        context.getViewer().refresh();
    }

    private void transferItems(@NonNull KitContent content, @NonNull Inventory inventory, @NonNull Function<Integer, Integer> slotMapper) {
        Map<Integer, AdaptedItem> itemBySlotMap = content.getItemBySlotMap();

        itemBySlotMap.clear();

        for (int slotIndex = 0; slotIndex < FUSED_SLOTS.size(); slotIndex++) {
            int slot = slotMapper.apply(slotIndex);
            if (slot >= inventory.getSize()) break;

            ItemStack itemStack = inventory.getItem(slot);
            if (itemStack == null || itemStack.getType().isAir()) continue;

            ItemAdapter<?> adapter = ItemBridge.adapter(itemStack).orElse(null);
            if (adapter == null) continue;

            AdaptedItem adaptedItem = adapter.adapt(itemStack).orElse(null);
            if (adaptedItem == null) continue;

            itemBySlotMap.put(slotIndex, adaptedItem);
        }
    }
}
