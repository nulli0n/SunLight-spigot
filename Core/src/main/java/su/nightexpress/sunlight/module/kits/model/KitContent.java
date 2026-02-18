package su.nightexpress.sunlight.module.kits.model;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.bridge.item.AdaptedItem;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.config.Writeable;
import su.nightexpress.nightcore.integration.item.impl.AdaptedItemStack;
import su.nightexpress.nightcore.util.Numbers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

public class KitContent implements Writeable {

    private final Map<Integer, AdaptedItem> itemBySlotMap;

    public KitContent(@NotNull Map<Integer, AdaptedItem> itemBySlotMap) {
        this.itemBySlotMap = itemBySlotMap;
    }

    @NotNull
    public static KitContent empty() {
        return new KitContent(new HashMap<>());
    }

    @NotNull
    public static KitContent copyOf(@NotNull KitContent other) {
        return new KitContent(new HashMap<>(other.getItemBySlotMap()));
    }

    @NotNull
    public static KitContent read(@NotNull FileConfig config, @NotNull String path) {
        Map<Integer, AdaptedItem> itemBySlotMap = new HashMap<>();

        config.getSection(path).forEach(sId -> {
            int slot = Numbers.parseInteger(sId).orElse(-1);
            if (slot < 0) return;

            AdaptedItem adaptedItem = AdaptedItemStack.read(config, path + "." + sId);
            if (adaptedItem == null) return;

            itemBySlotMap.put(slot, adaptedItem);
        });

        return new KitContent(itemBySlotMap);
    }

    @Override
    public void write(@NotNull FileConfig config, @NotNull String path) {
        config.remove(path);

        this.itemBySlotMap.forEach((slot, adaptedItem) -> config.set(path + "." + slot, adaptedItem));
    }

    @NotNull
    public List<ItemStack> give(/*@NotNull Player player, */@NotNull BiConsumer<Integer, ItemStack> consumer) {
        List<ItemStack> leftovers = new ArrayList<>();
        //PlayerInventory inventory = player.getInventory();

        this.itemBySlotMap.forEach((slot, adaptedItem) -> {
            adaptedItem.itemStack().ifPresent(itemStack -> {
                consumer.accept(slot, itemStack);

                /*ItemStack current = inventory.getItem(slot);
                if (current != null && !current.getType().isAir()) {
                    leftovers.add(new ItemStack(current));
                }
                inventory.setItem(slot, itemStack);*/
            });
        });

        return leftovers;
    }

    @NotNull
    public Map<Integer, AdaptedItem> getItemBySlotMap() {
        return this.itemBySlotMap;
    }
}
