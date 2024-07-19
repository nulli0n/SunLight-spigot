package su.nightexpress.sunlight.nms;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public interface SunNMS {

    default boolean canDestroyBlocks(@NotNull EntityExplodeEvent event) {
        return true;
    }

    default boolean canDestroyBlocks(@NotNull BlockExplodeEvent event) {
        return true;
    }

    @NotNull Object fineChatPacket(@NotNull Object packet);

    @NotNull Player loadPlayerData(@NotNull UUID id, @NotNull String name);

    @NotNull Inventory getPlayerInventory(@NotNull Player player);

    @NotNull Inventory getPlayerEnderChest(@NotNull Player player);

    void setGameMode(@NotNull Player player, @NotNull GameMode mode);

    void teleport(@NotNull Player player, @NotNull Location location);

    void openAnvil(@NotNull Player player);

    void openEnchanting(@NotNull Player player);

    void openGrindstone(@NotNull Player player);

    void openLoom(@NotNull Player player);

    void openSmithing(@NotNull Player player);

    void openCartography(@NotNull Player player);

    void openStonecutter(@NotNull Player player);

    void dropFallingContent(@NotNull FallingBlock fallingBlock);
}
