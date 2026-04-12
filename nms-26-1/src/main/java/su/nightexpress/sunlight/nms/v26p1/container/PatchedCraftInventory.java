package su.nightexpress.sunlight.nms.v26p1.container;

import org.bukkit.craftbukkit.inventory.CraftInventory;
import org.bukkit.event.inventory.InventoryType;
import org.jspecify.annotations.NonNull;

import net.minecraft.world.Container;

public class PatchedCraftInventory extends CraftInventory {

    public PatchedCraftInventory(Container inventory) {
        super(inventory);
    }

    @Override
    @NonNull
    public InventoryType getType() {
        // Force tell that it's a custom chest and not PLAYER type inventory.
        // Otherwise an error is thrown sometimes due to CraftContainer.getNotchInventoryType in the CraftContainer constructor.
        return InventoryType.CHEST;
    }
}
