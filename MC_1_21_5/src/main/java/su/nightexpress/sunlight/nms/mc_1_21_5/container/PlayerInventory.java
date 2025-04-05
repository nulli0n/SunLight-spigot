package su.nightexpress.sunlight.nms.mc_1_21_5.container;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import org.bukkit.craftbukkit.v1_21_R4.entity.CraftHumanEntity;
import org.bukkit.craftbukkit.v1_21_R4.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_21_R4.inventory.CraftInventory;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.util.Reflex;

public class PlayerInventory extends Inventory {

    private static final String ITEMS_FIELD = "i"; // 'items'

    private final CraftInventory inventory;
    private final CraftPlayer    owner;

    public PlayerInventory(@NotNull CraftPlayer player) {
        super(player.getHandle(), player.getHandle().equipment);
        this.owner = player;
        this.inventory = new CraftInventory(this);
        this.reflectContents();
    }

    private void reflectContents() {
        Inventory origin = this.player.getInventory();

        Reflex.setFieldValue(this, ITEMS_FIELD, origin.getNonEquipmentItems());
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
    public int getContainerSize() {
        return super.getContainerSize() + 4;
    }

    @Override
    public boolean hasCustomName() {
        return true;
    }

    @Override
    public boolean stillValid(Player entityhuman) {
        return true;
    }

    public void update() {
        super.tick();
        this.player.getInventory().tick();
    }
}
