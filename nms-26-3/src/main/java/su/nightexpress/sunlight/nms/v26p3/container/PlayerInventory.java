package su.nightexpress.sunlight.nms.v26p3.container;

import org.bukkit.craftbukkit.entity.CraftHumanEntity;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.inventory.CraftInventory;
import org.jspecify.annotations.NullMarked;

import net.minecraft.world.entity.EntityEquipment;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import su.nightexpress.nightcore.util.Reflex;
import su.nightexpress.nightcore.util.Version;

@NullMarked
public class PlayerInventory extends Inventory {

    private final CraftInventory inventory;
    private final CraftPlayer    owner;

    public PlayerInventory(CraftPlayer player) {
        super(player.getHandle(), getEquipment(player));
        this.owner = player;
        this.inventory = new PatchedCraftInventory(this);
        this.reflectContents();
    }

    private static EntityEquipment getEquipment(CraftPlayer craftPlayer) {
        if (Version.isSpigot()) {
            return (EntityEquipment) Reflex.getFieldValue(craftPlayer.getHandle(), "equipment", "m");
        }
        return craftPlayer.getHandle().getInventory().equipment;
    }

    private void reflectContents() {
        Inventory origin = this.player.getInventory();

        Reflex.setFieldValue(this, "items", origin.getNonEquipmentItems());
    }


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
    public int getContainerSize() {
        return 45;
    }

    @Override
    public boolean hasCustomName() {
        return true;
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }
}
