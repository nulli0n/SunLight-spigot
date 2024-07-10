package su.nightexpress.sunlight.module.warps.util;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.util.ItemUtil;
import su.nightexpress.sunlight.module.warps.type.WarpType;

public class WarpUtils {

    @NotNull
    public static ItemStack getDefaultIcon(@NotNull Player player, @NotNull WarpType type) {
        if (type == WarpType.SERVER) return new ItemStack(Material.COMPASS);

        ItemStack itemStack = new ItemStack(Material.PLAYER_HEAD);
        ItemUtil.editMeta(itemStack, meta -> {
            SkullMeta skullMeta = (SkullMeta) meta;
            skullMeta.setOwnerProfile(player.getPlayerProfile());
        });
        return itemStack;
    }
}
