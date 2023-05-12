package su.nightexpress.sunlight.module.kits.util;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.utils.PDCUtil;
import su.nightexpress.sunlight.SunLightAPI;

import java.util.Optional;
import java.util.UUID;

public class KitsUtils {

    public static final NamespacedKey KEY_ITEM_OWNER = new NamespacedKey(SunLightAPI.PLUGIN, "kits.item_owner");

    @NotNull
    public static Optional<UUID> getItemOwner(@NotNull ItemStack item) {
        String data = PDCUtil.getString(item, KEY_ITEM_OWNER).orElse(null);
        return data == null ? Optional.empty() : Optional.of(UUID.fromString(data));
    }

    public static void setItemOwner(@NotNull ItemStack item, @NotNull Entity entity) {
        setItemOwner(item, entity.getUniqueId());
    }

    public static void setItemOwner(@NotNull ItemStack item, @NotNull UUID uuid) {
        PDCUtil.set(item, KEY_ITEM_OWNER, uuid.toString());
    }

    public static boolean isItemOwner(@NotNull ItemStack item, @NotNull Entity entity) {
        return isItemOwner(item, entity.getUniqueId());
    }

    public static boolean isItemOwner(@NotNull ItemStack item, @NotNull UUID uuid) {
        Optional<UUID> ownerId = getItemOwner(item);
        return ownerId.isEmpty() || ownerId.get().equals(uuid);
    }
}
