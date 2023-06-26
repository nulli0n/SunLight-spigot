package su.nightexpress.sunlight.module.kits.menu;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.config.JYML;
import su.nexmedia.engine.api.menu.MenuItemType;
import su.nexmedia.engine.api.menu.impl.ConfigMenu;
import su.nexmedia.engine.api.menu.impl.MenuViewer;
import su.nightexpress.sunlight.SunLight;
import su.nightexpress.sunlight.module.kits.Kit;

public class KitPreviewMenu extends ConfigMenu<SunLight> {

    private static int[] ITEM_SLOTS  = new int[]{};
    private static int[] ARMOR_SLOTS = new int[4];
    private static int[] EXTRA_SLOTS = new int[1];

    private final Kit kit;

    public KitPreviewMenu(@NotNull Kit kit) {
        super(kit.plugin(), JYML.loadOrExtract(kit.plugin(), kit.getModule().getLocalPath() + "/menu/", "kit_preview.yml"));

        this.kit = kit;
        ITEM_SLOTS = cfg.getIntArray("Item_Slots");
        ARMOR_SLOTS = cfg.getIntArray("Armor_Slots");
        EXTRA_SLOTS = cfg.getIntArray("Extra_Slots");

        this.registerHandler(MenuItemType.class)
            .addClick(MenuItemType.RETURN, (viewer, event) -> kit.getModule().getKitsMenu().openNextTick(viewer, 1));

        this.load();
    }

    @Override
    public void onReady(@NotNull MenuViewer viewer, @NotNull Inventory inventory) {
        super.onReady(viewer, inventory);
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
    }
}
