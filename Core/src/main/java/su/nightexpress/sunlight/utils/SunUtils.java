package su.nightexpress.sunlight.utils;

import com.google.common.net.InetAddresses;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Registry;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.nightcore.util.BukkitThing;
import su.nightexpress.nightcore.util.ItemUtil;
import su.nightexpress.nightcore.util.Players;
import su.nightexpress.nightcore.util.Version;
import su.nightexpress.sunlight.SunLightAPI;
import su.nightexpress.sunlight.config.Config;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.time.LocalTime;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class SunUtils {

    public static final String CONSOLE_NAME = Bukkit.getServer().getConsoleSender().getName();
    public static final String LOCAL_ADDRESS = "127.0.0.1";

    @SuppressWarnings("deprecation")
    public static List<String> getPotionEffects(@NotNull Predicate<PotionEffectType> predicate) {
        if (Version.isBehind(Version.V1_20_R3)) {
            return Stream.of(PotionEffectType.values()).map(BukkitThing::toString).toList();
        }
        return BukkitThing.allFromRegistry(Registry.EFFECT).stream().filter(predicate).map(BukkitThing::toString).toList();
    }

    public static List<String> getMaterials(@NotNull Predicate<Material> predicate) {
        return BukkitThing.getMaterials().stream().filter(predicate).map(BukkitThing::toString).toList();
    }

    public static List<String> getEntityTypes(@NotNull Predicate<EntityType> predicate) {
        return Stream.of(EntityType.values()).filter(predicate).map(BukkitThing::toString).toList();
    }

    @NotNull
    public static String createIdentifier(@NotNull Player player) {
        String uuid = player.getUniqueId().toString();

        // Bedrock players have UUIDs leading with zeros.
        if (Players.isBedrock(player)) {
            uuid = new StringBuilder(uuid).reverse().toString();
        }

        return uuid;
    }

    public static int clamp(long value, long min, long max) {
        if (min > max) {
            throw new IllegalArgumentException(min + " > " + max);
        }
        return (int) Math.min(max, Math.max(value, min));
    }

    @NotNull
    public static String noSpace(@NotNull String str) {
        return str.trim().replaceAll("\\s+", "");
    }

    @NotNull
    public static String oneSpace(@NotNull String str) {
        return str.trim().replaceAll("\\s+", " ");
    }

    @NotNull
    public static String formatDate(long timestamp) {
        return Config.GENERAL_DATE_FORMAT.get().format(timestamp);
    }

    @NotNull
    public static String formatTime(@NotNull LocalTime localTime) {
        return localTime.format(Config.GENERAL_TIME_FORMAT.get());
    }

    @NotNull
    public static String getSenderName(@NotNull String name) {
        if (name.equalsIgnoreCase(CONSOLE_NAME)) {
            return Config.CONSOLE_NAME.get();
        }
        Player player = Players.getPlayer(name);
        if (player != null) {
            return player.getDisplayName();
        }
        return name;
    }

    @NotNull
    public static String getSenderName(@NotNull CommandSender sender) {
        if (sender instanceof ConsoleCommandSender) {
            return Config.CONSOLE_NAME.get();
        }
        if (sender instanceof Player player) {
            return player.getDisplayName();
        }
        return sender.getName();
    }

    @NotNull
    public static String getRawAddress(@NotNull Player player) {
        InetSocketAddress address = player.getAddress();
        return address == null ? LOCAL_ADDRESS : getRawAddress(address.getAddress());
    }

    @NotNull
    public static String getRawAddress(@NotNull InetAddress address) {
        return address.getHostAddress();
    }

    public static boolean isInetAddress(@NotNull String address) {
        return InetAddresses.isInetAddress(address);
    }

    public static boolean isLocalAddress(@NotNull String address) {
        return address.equalsIgnoreCase(LOCAL_ADDRESS);
    }

    public static boolean teleport(@NotNull Player player, @NotNull Entity target) {
        return teleport(player, target.getLocation());
    }

    public static boolean teleport(@NotNull Player player, @NotNull Location location) {
        if (player.isOnline()) {
            return player.teleport(location);
        }
        SunLightAPI.PLUGIN.getSunNMS().teleport(player, location);
        return true;
    }

    @NotNull
    public static LocalTime getTimeOfTicks(long ticks) {
        double point = ticks * 3.6;

        int hours = (int) (point / 60D / 60D);
        int minutes = (int) ((point / 60D) % 60);
        int seconds = (int) (point % 60);
        return LocalTime.of(hours, minutes, seconds).plusHours(6);
    }

    public static boolean isSafeLocation(@NotNull Location location) {
        Block block = location.getBlock();

        Block above = block.getRelative(BlockFace.UP);
        if (!above.isEmpty() || above.getType() == Material.LAVA || above.getType().isSolid()) {
            return false;
        }

        Block under = block.getRelative(BlockFace.DOWN);
        if (under.isEmpty() || under.getType() == Material.LAVA || !under.getType().isSolid()) {
            return false;
        }

        return block.getType() != Material.LAVA;
    }

    public static boolean repairItem(@Nullable ItemStack item) {
        return damageItem(item, 0);
    }

    public static boolean damageItem(@Nullable ItemStack item, int damage) {
        if (item == null) return false;

        ItemMeta meta = item.getItemMeta();
        if (!(meta instanceof Damageable damageable)) return false;

        damageable.setDamage(Math.max(0, Math.abs(damage)));
        item.setItemMeta(meta);
        return true;
    }

    public static boolean enchantItem(@NotNull ItemStack itemStack, @NotNull Enchantment enchantment, int level) {
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
        return true;
    }

    public static boolean disenchantItem(@NotNull ItemStack itemStack, @Nullable Enchantment enchantment) {
        ItemUtil.editMeta(itemStack, meta -> {
            if (meta instanceof EnchantmentStorageMeta storageMeta) {
                if (enchantment != null) {
                    storageMeta.removeStoredEnchant(enchantment);
                }
                else storageMeta.getStoredEnchants().keySet().forEach(storageMeta::removeStoredEnchant);
            }
            else {
                if (enchantment != null) {
                    meta.removeEnchant(enchantment);
                }
                else meta.getEnchants().keySet().forEach(meta::removeEnchant);
            }
        });
        return true;
    }
}
