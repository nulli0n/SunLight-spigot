package su.nightexpress.sunlight.nms.mc_1_21_8.container;

import net.minecraft.world.entity.EntityEquipment;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import org.bukkit.craftbukkit.entity.CraftHumanEntity;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.inventory.CraftInventory;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.util.Reflex;
import su.nightexpress.nightcore.util.Version;

public class PlayerInventory extends Inventory {

    private final CraftInventory inventory;
    private final CraftPlayer    owner;

    public PlayerInventory(@NotNull CraftPlayer player) {
        super(player.getHandle(), getEquipment(player));
        this.owner = player;
        this.inventory = new PatchedCraftInventory(this);
        this.reflectContents();
    }

    private static EntityEquipment getEquipment(@NotNull CraftPlayer craftPlayer) {
        if (Version.isSpigot()) {
            return (EntityEquipment) Reflex.getFieldValue(craftPlayer.getHandle(), "equipment", "m");
        }
        return craftPlayer.getHandle().getInventory().equipment;
    }

    private void reflectContents() {
        Inventory origin = this.player.getInventory();
        String field = Version.isPaper() ? "items" : "k";

        Reflex.setFieldValue(this, field, origin.getNonEquipmentItems());
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
    public int getContainerSize() {
        return 45;
    }

    @Override
    public boolean hasCustomName() {
        return true;
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return true;
    }
}
