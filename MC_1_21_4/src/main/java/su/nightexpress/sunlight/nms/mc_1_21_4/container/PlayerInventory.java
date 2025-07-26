package su.nightexpress.sunlight.nms.mc_1_21_4.container;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import org.bukkit.craftbukkit.v1_21_R3.entity.CraftHumanEntity;
import org.bukkit.craftbukkit.v1_21_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_21_R3.inventory.CraftInventory;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.util.Reflex;

import java.util.Arrays;

public class PlayerInventory extends Inventory {

    private static final String ITEMS_FIELD        = "g"; // 'items'
    private static final String ARMOR_FIELD        = "h"; // 'armor'
    private static final String OFFHAND_FIELD      = "i"; // 'offhand'
    private static final String COMPARTMENTS_FIELD = "l"; // 'compartments'

    private final CraftInventory inventory;
    private final CraftPlayer    owner;

    public PlayerInventory(@NotNull CraftPlayer player) {
        super(player.getHandle());
        this.owner = player;
        this.inventory = new CraftInventory(this);
        this.reflectContents();
    }

    private void reflectContents() {
        Inventory origin = this.player.getInventory();

        Reflex.setFieldValue(this, ITEMS_FIELD, origin.items);
        Reflex.setFieldValue(this, ARMOR_FIELD, origin.armor);
        Reflex.setFieldValue(this, OFFHAND_FIELD, origin.offhand);
        Reflex.setFieldValue(this, COMPARTMENTS_FIELD, Arrays.asList(origin.items, origin.armor, origin.offhand));
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
}
