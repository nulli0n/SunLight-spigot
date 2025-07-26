package su.nightexpress.sunlight.nms.mc_1_21_8.container;

import net.minecraft.world.Container;
import org.bukkit.craftbukkit.inventory.CraftInventory;
import org.bukkit.event.inventory.InventoryType;
import org.jetbrains.annotations.NotNull;

public class PatchedCraftInventory extends CraftInventory {

    public PatchedCraftInventory(Container inventory) {
        super(inventory);
    }

    @Override
    @NotNull
    public InventoryType getType() {
        // Force tell that it's a custom chest and not PLAYER type inventory.
        // Otherwise an error is thrown sometimes due to CraftContainer.getNotchInventoryType in the CraftContainer constructor.
        return InventoryType.CHEST;
    }
}
