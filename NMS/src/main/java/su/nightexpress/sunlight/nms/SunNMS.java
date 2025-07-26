package su.nightexpress.sunlight.nms;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.sunlight.api.MenuType;

import java.util.UUID;

public interface SunNMS {

    @NotNull Object fineChatPacket(@NotNull Object packet);

    @NotNull Player loadPlayerData(@NotNull UUID id, @NotNull String name);

    @NotNull Inventory getPlayerInventory(@NotNull Player player);

    void openPlayerInventory(@NotNull Player player, @NotNull Player owner);

    @NotNull Inventory getPlayerEnderChest(@NotNull Player player);

    void setGameMode(@NotNull Player player, @NotNull GameMode mode);

    void teleport(@NotNull Player player, @NotNull Location location);

    void openContainer(@NotNull Player player, @NotNull MenuType menuType);

    void dropFallingContent(@NotNull FallingBlock fallingBlock);
}
