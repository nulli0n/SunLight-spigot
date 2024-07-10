package su.nightexpress.sunlight.nms.v1_20_R1.container;

import net.minecraft.core.NonNullList;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftHumanEntity;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_20_R1.inventory.CraftInventory;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.util.Pair;
import su.nightexpress.nightcore.util.Reflex;

import java.util.Arrays;

public class PlayerInventory extends Inventory {

    private final CraftInventory inventory = new CraftInventory(this);
    private final CraftPlayer    owner;

    public PlayerInventory(@NotNull CraftPlayer player) {
        super(player.getHandle());
        this.owner = player;
        this.reflectContents(this, this.player.getInventory().items, this.player.getInventory().armor, this.player.getInventory().offhand);
    }

    private void reflectContents(Inventory inventory, NonNullList<ItemStack> items, NonNullList<ItemStack> armor, NonNullList<ItemStack> offhand) {
        Reflex.setFieldValue(inventory, "i", items);
        Reflex.setFieldValue(inventory, "j", armor);
        Reflex.setFieldValue(inventory, "k", offhand);
        Reflex.setFieldValue(inventory, "o", Arrays.asList(items, armor, offhand));
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

    private Pair<Integer, NonNullList<ItemStack>> fineSlotAndItems(int slot) {
        NonNullList<ItemStack> list = this.items;
        if (slot >= list.size()) {
            slot -= list.size();
            list = this.armor;
        }
        else {
            slot = this.getReversedItemSlotNum(slot);
        }

        if (slot >= list.size()) {
            slot -= list.size();
            list = this.offhand;
        }
        else if (list == this.armor) {
            slot = this.getReversedArmorSlotNum(slot);
        }

        return Pair.of(slot, list);
    }

    @Override
    public ItemStack getItem(int slot) {
        Pair<Integer, NonNullList<ItemStack>> pair = this.fineSlotAndItems(slot);
        NonNullList<ItemStack> list = pair.getSecond();
        slot = pair.getFirst();

        if (slot < list.size()) return list.get(slot);

        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        Pair<Integer, NonNullList<ItemStack>> pair = this.fineSlotAndItems(slot);
        NonNullList<ItemStack> list = pair.getSecond();
        slot = pair.getFirst();

        if (slot >= list.size()) {
            return ItemStack.EMPTY;
        }

        if (!(list.get(slot)).isEmpty()) {
            return ContainerHelper.removeItem(list, slot, amount);
        }
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        Pair<Integer, NonNullList<ItemStack>> pair = this.fineSlotAndItems(slot);
        NonNullList<ItemStack> list = pair.getSecond();
        slot = pair.getFirst();

        if (slot >= list.size()) {
            return ItemStack.EMPTY;
        }
        if ((list.get(slot)).isEmpty()) return ItemStack.EMPTY;
        ItemStack itemstack = list.get(slot);
        list.set(slot, ItemStack.EMPTY);
        return itemstack;
    }

    @Override
    public void setItem(int slot, ItemStack itemStack) {
        Pair<Integer, NonNullList<ItemStack>> pair = this.fineSlotAndItems(slot);
        NonNullList<ItemStack> list = pair.getSecond();
        slot = pair.getFirst();

        if (slot >= list.size()) {
            this.player.drop(itemStack, true);
            return;
        }
        list.set(slot, itemStack);
    }

    private int getReversedItemSlotNum(int slot) {
        if (slot >= 27) {
            return slot - 27;
        }
        return slot + 9;
    }

    private int getReversedArmorSlotNum(int slot) {
        if (slot == 0) return 3;
        if (slot == 1) return 2;
        if (slot == 2) return 1;
        if (slot == 3) return 0;
        return slot;
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
