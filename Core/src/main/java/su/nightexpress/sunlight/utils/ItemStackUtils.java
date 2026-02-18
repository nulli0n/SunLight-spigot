package su.nightexpress.sunlight.utils;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.nightcore.util.ItemUtil;

import java.util.concurrent.atomic.AtomicInteger;

public class ItemStackUtils {

    public static void repairItem(@Nullable ItemStack itemStack) {
        if (itemStack == null || !isDamageable(itemStack)) return;

        setItemDamage(itemStack, 0);
    }

    public static boolean isDamageable(@NotNull ItemStack itemStack) {
        return itemStack.getType().getMaxDurability() > 0;
    }

    public static int setItemDamage(@NotNull ItemStack itemStack, int damage) {
        if (!isDamageable(itemStack)) throw new IllegalStateException("Item can not be damaged");

        AtomicInteger result = new AtomicInteger(damage);

        ItemUtil.editMeta(itemStack, Damageable.class, meta -> {
            if (meta.getDamage() == damage) return;

            int absDamage = Math.max(0, damage);
            int maxDamage = itemStack.getType().getMaxDurability();
            int finalDamage = Math.min(absDamage, maxDamage);

            meta.setDamage(finalDamage);
            result.set(finalDamage);
        });

        return result.get();
    }

    public static void addEnchantment(@NotNull ItemStack itemStack, @NotNull Enchantment enchantment, int level) {
        ItemUtil.editMeta(itemStack, meta -> {
            if (meta instanceof EnchantmentStorageMeta storageMeta) {
                if (level > 0) {
                    storageMeta.addStoredEnchant(enchantment, level, true);
                }
                else storageMeta.removeStoredEnchant(enchantment);
            }
            else {
                if (level > 0) {
                    meta.addEnchant(enchantment, level, true);
                }
                else meta.removeEnchant(enchantment);
            }
        });
    }

    public static void removeEnchantment(@NotNull ItemStack itemStack, @NotNull Enchantment enchantment) {
        ItemUtil.editMeta(itemStack, meta -> {
            if (meta instanceof EnchantmentStorageMeta storageMeta) {
                storageMeta.removeStoredEnchant(enchantment);
            }
            else {
                meta.removeEnchant(enchantment);
            }
        });
    }

    public static void removeEnchantments(@NotNull ItemStack itemStack) {
        ItemUtil.editMeta(itemStack, meta -> {
            if (meta instanceof EnchantmentStorageMeta storageMeta) {
                storageMeta.getStoredEnchants().keySet().forEach(storageMeta::removeStoredEnchant);
            }
            else {
                meta.getEnchants().keySet().forEach(meta::removeEnchant);
            }
        });
    }
}
