package su.nightexpress.sunlight.module.kits.util;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.util.PDCUtil;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.module.kits.config.KitsConfig;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class KitsUtils {

    public static NamespacedKey itemOwnerKey;

    public static void loadKeys(@NotNull SunLightPlugin plugin) {
        itemOwnerKey = new NamespacedKey(plugin, "kits.item_owner");
    }

    public static boolean isBindEnabled() {
        return KitsConfig.BIND_ITEMS_TO_PLAYERS.get();
    }

    @NotNull
    public static Optional<UUID> getItemOwner(@NotNull ItemStack item) {
        String data = PDCUtil.getString(item, itemOwnerKey).orElse(null);
        return data == null ? Optional.empty() : Optional.of(UUID.fromString(data));
    }

    public static void setItemOwner(@NotNull ItemStack item, @NotNull Entity entity) {
        setItemOwner(item, entity.getUniqueId());
    }

    public static void setItemOwner(@NotNull ItemStack item, @NotNull UUID uuid) {
        PDCUtil.set(item, itemOwnerKey, uuid.toString());
    }

    public static boolean isItemOwner(@NotNull ItemStack item, @NotNull Entity entity) {
        return isItemOwner(item, entity.getUniqueId());
    }

    public static boolean isItemOwner(@NotNull ItemStack item, @NotNull UUID uuid) {
        Optional<UUID> ownerId = getItemOwner(item);
        return ownerId.isEmpty() || ownerId.get().equals(uuid);
    }

    public static ItemStack[] filterNulls(ItemStack[] array) {
        for (int count = 0; count < array.length; count++) {
            if (array[count] == null) {
                array[count] = new ItemStack(Material.AIR);
            }
        }
        return array;
    }

    public static ItemStack[] bindToPlayer(@NotNull Player player, @NotNull ItemStack[] from) {
        boolean bind = KitsUtils.isBindEnabled();

        ItemStack[] array = new ItemStack[from.length];
        for (int index = 0; index < array.length; index++) {
            ItemStack item = new ItemStack(from[index]);
            if (bind) {
                KitsUtils.setItemOwner(item, player);
            }
            array[index] = item;
        }
        return array;
    }

    @NotNull
    public static ItemStack[] fuseItems(ItemStack[] kitItems, ItemStack[] inventory, @NotNull List<ItemStack> left) {
        for (int index = 0; index < inventory.length; index++) {
            if (index >= kitItems.length) break;

            ItemStack itemInv = inventory[index];
            ItemStack itemKit = kitItems[index];
            if (itemKit == null || itemKit.getType().isAir()) continue;

            if (itemInv == null || itemInv.getType().isAir()) {
                inventory[index] = new ItemStack(itemKit);
            }
            else left.add(itemKit);
        }
        return inventory;
    }
}
