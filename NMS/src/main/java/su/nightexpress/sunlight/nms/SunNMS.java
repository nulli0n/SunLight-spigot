package su.nightexpress.sunlight.nms;

import java.util.UUID;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.jspecify.annotations.NonNull;

import su.nightexpress.sunlight.api.PortableContainer;

public interface SunNMS {

    @NonNull
    Object fineChatPacket(@NonNull Object packet);

    @NonNull
    Player loadPlayerData(@NonNull UUID id, @NonNull String name);

    @NonNull
    Inventory getPlayerInventory(@NonNull Player player);

    void openPlayerInventory(@NonNull Player player, @NonNull Player owner);

    @NonNull
    Inventory getPlayerEnderChest(@NonNull Player player);

    void setGameMode(@NonNull Player player, @NonNull GameMode mode);

    void teleport(@NonNull Player player, @NonNull Location location);

    void openContainer(@NonNull Player player, @NonNull PortableContainer menuType);

    void dropFallingContent(@NonNull FallingBlock fallingBlock);
}
