package su.nightexpress.sunlight.module.kits.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NonNull;

import su.nightexpress.nightcore.bridge.item.AdaptedItem;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.config.Writeable;
import su.nightexpress.nightcore.integration.item.impl.AdaptedItemStack;
import su.nightexpress.nightcore.util.Numbers;

public class KitContent implements Writeable {

    private final Map<Integer, AdaptedItem> itemBySlotMap;

    public KitContent(@NonNull Map<Integer, AdaptedItem> itemBySlotMap) {
        this.itemBySlotMap = itemBySlotMap;
    }

    @NonNull
    public static KitContent empty() {
        return new KitContent(new HashMap<>());
    }

    @NonNull
    public static KitContent copyOf(@NonNull KitContent other) {
        return new KitContent(new HashMap<>(other.getItemBySlotMap()));
    }

    @NonNull
    public static KitContent read(@NonNull FileConfig config, @NonNull String path) {
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
    public void write(@NonNull FileConfig config, @NonNull String path) {
        config.remove(path);

        this.itemBySlotMap.forEach((slot, adaptedItem) -> config.set(path + "." + slot, adaptedItem));
    }

    @NonNull
    public List<ItemStack> give(@NonNull BiConsumer<Integer, ItemStack> consumer) {
        List<ItemStack> leftovers = new ArrayList<>();
        this.itemBySlotMap.forEach((slot, adaptedItem) -> {
            adaptedItem.itemStack().ifPresent(itemStack -> {
                consumer.accept(slot, itemStack);
            });
        });

        return leftovers;
    }

    @NonNull
    public Map<Integer, AdaptedItem> getItemBySlotMap() {
        return this.itemBySlotMap;
    }
}
