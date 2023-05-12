package su.nightexpress.sunlight.nms.v1_19_R2;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.PlayerEnderChestContainer;
import org.bukkit.craftbukkit.v1_19_R2.entity.CraftHumanEntity;
import org.bukkit.craftbukkit.v1_19_R2.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_19_R2.inventory.CraftInventory;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.utils.Reflex;

public class PlayerEnderChest extends PlayerEnderChestContainer {

    private final CraftInventory inventory = new CraftInventory(this);
    private final CraftPlayer    owner;

    public PlayerEnderChest(CraftPlayer owner) {
        super(owner.getHandle());
        this.owner = owner;
        Reflex.setFieldValue(this, "c", owner.getHandle().getEnderChestInventory().items);
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
    public void onClose(CraftHumanEntity who) {
        super.onClose(who);
        this.saveOnExit();
    }

    @Override
    public boolean stillValid(Player entityhuman) {
        return true;
    }
}
