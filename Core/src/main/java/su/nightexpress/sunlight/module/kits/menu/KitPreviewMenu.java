package su.nightexpress.sunlight.module.kits.menu;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.config.JYML;
import su.nexmedia.engine.api.menu.AbstractMenu;
import su.nexmedia.engine.api.menu.MenuClick;
import su.nexmedia.engine.api.menu.MenuItem;
import su.nexmedia.engine.api.menu.MenuItemType;
import su.nightexpress.sunlight.SunLight;
import su.nightexpress.sunlight.module.kits.Kit;

public class KitPreviewMenu extends AbstractMenu<SunLight> {

    private static int[] ITEM_SLOTS  = new int[]{};
    private static int[] ARMOR_SLOTS = new int[4];
    private static int[] EXTRA_SLOTS = new int[1];

    private final Kit kit;

    public KitPreviewMenu(@NotNull Kit kit) {
        super(kit.plugin(), JYML.loadOrExtract(kit.plugin(), kit.getKitManager().getLocalPath() + "/menu/kit_preview.yml"), "");

        this.kit = kit;
        ITEM_SLOTS = cfg.getIntArray("Item_Slots");
        ARMOR_SLOTS = cfg.getIntArray("Armor_Slots");
        EXTRA_SLOTS = cfg.getIntArray("Extra_Slots");

        MenuClick click = (player, type, e) -> {
            if (type instanceof MenuItemType type2) {
                if (type == MenuItemType.RETURN) {
                    kit.getKitManager().getKitsMenu().open(player, 1);
                }
            }
        };

        for (String sId : cfg.getSection("Content")) {
            MenuItem menuItem = cfg.getMenuItem("Content." + sId, MenuItemType.class);

            if (menuItem.getType() != null) {
                menuItem.setClickHandler(click);
            }
            this.addItem(menuItem);
        }
    }

    @Override
    public boolean onPrepare(@NotNull Player player, @NotNull Inventory inventory) {
        int count = 0;
        for (ItemStack item : this.kit.getItems()) {
            if (count >= ITEM_SLOTS.length || count >= inventory.getSize()) break;
            inventory.setItem(ITEM_SLOTS[count++], item);
        }

        int armorCount = 0;
        for (ItemStack armor : this.kit.getArmor()) {
            if (armorCount >= ARMOR_SLOTS.length || armorCount >= inventory.getSize()) break;
            inventory.setItem(ARMOR_SLOTS[armorCount++], armor);
        }

        for (int index = 0; index < EXTRA_SLOTS.length; index++) {
            inventory.setItem(EXTRA_SLOTS[index], kit.getExtras()[index]);
        }
        return true;
    }

    @Override
    public boolean cancelClick(@NotNull InventoryClickEvent e, @NotNull SlotType slotType) {
        return true;
    }
}
