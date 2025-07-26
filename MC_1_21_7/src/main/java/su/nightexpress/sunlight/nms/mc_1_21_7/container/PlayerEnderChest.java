package su.nightexpress.sunlight.nms.mc_1_21_7.container;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.PlayerEnderChestContainer;
import org.bukkit.craftbukkit.entity.CraftHumanEntity;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.inventory.CraftInventory;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.util.Reflex;

public class PlayerEnderChest extends PlayerEnderChestContainer {

    private static final String ITEMS_FIELD = "c"; // 'items'

    private final CraftInventory inventory = new CraftInventory(this);
    private final CraftPlayer    owner;

    public PlayerEnderChest(CraftPlayer owner) {
        super(owner.getHandle());
        this.owner = owner;
        Reflex.setFieldValue(this, ITEMS_FIELD, owner.getHandle().getEnderChestInventory().items);
    }

    @NotNull
    public org.bukkit.inventory.Inventory getInventory() {
        return this.inventory;
    }

    private void saveOnExit() {
        if (!this.transaction.isEmpty()) return;
        this.owner.saveData();
    }

    @Override
    public void onClose(@NotNull CraftHumanEntity who) {
        super.onClose(who);
        this.saveOnExit();
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return true;
    }
}
