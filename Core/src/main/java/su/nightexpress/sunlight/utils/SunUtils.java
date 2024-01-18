package su.nightexpress.sunlight.utils;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.utils.regex.RegexUtil;
import su.nightexpress.sunlight.SunLightAPI;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.time.LocalTime;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class SunUtils {

    @Deprecated public static final List<String> ENTITY_TYPES = Stream.of(EntityType.values()).filter(EntityType::isSpawnable).map(Enum::name).toList();
    public static final List<String> ITEM_TYPES = Stream.of(Material.values()).filter(Material::isItem).map(Enum::name).map(String::toLowerCase).toList();
    public static final     Pattern      PATTERN_IP = Pattern.compile(
        "(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)");

    @NotNull
    public static String getIP(@NotNull Player player) {
        InetSocketAddress inet = player.getAddress();
        return inet == null ? "null" : getIP(inet.getAddress());
    }

    @NotNull
    public static String getIP(@NotNull InetAddress inet) {
        return inet.getHostAddress();
    }

    public static boolean isIpAddress(@NotNull String str) {
        return RegexUtil.matches(PATTERN_IP, str);
    }

    public static void teleport(@NotNull Player player, @NotNull Entity to) {
        teleport(player, to.getLocation());
    }

    public static void teleport(@NotNull Player player, @NotNull Location location) {
        if (player.isOnline()) {
            player.teleport(location);
            return;
        }
        SunLightAPI.PLUGIN.getSunNMS().teleport(player, location);
    }

    public static void setGameMode(@NotNull Player player, @NotNull GameMode mode) {
        if (player.isOnline()) {
            player.setGameMode(mode);
            return;
        }
        SunLightAPI.PLUGIN.getSunNMS().setGameMode(player, mode);
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
}
