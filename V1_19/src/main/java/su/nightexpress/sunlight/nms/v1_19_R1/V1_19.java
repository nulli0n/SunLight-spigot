package su.nightexpress.sunlight.nms.v1_19_R1;

import com.mojang.authlib.GameProfile;
import net.minecraft.network.protocol.game.ClientboundOpenScreenPacket;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_19_R1.CraftServer;
import org.bukkit.craftbukkit.v1_19_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_19_R1.util.CraftChatMessage;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.sunlight.nms.SunNMS;

import java.util.UUID;

public class V1_19 implements SunNMS {

    @Override
    @NotNull
    public Object fineChatPacket(@NotNull Object packet) {
        return packet;
    }

    @Override
    @NotNull
    public org.bukkit.entity.Player loadPlayerData(@NotNull UUID id, @NotNull String name) {
        GameProfile profile = new GameProfile(id, name);
        DedicatedServer server = ((CraftServer)Bukkit.getServer()).getServer();
        ServerLevel level = server.getLevel(Level.OVERWORLD);
        if (level == null) throw new IllegalStateException("Server level is null");

        ServerPlayer entity = new ServerPlayer(server, level, profile, null);
        CraftPlayer player = entity.getBukkitEntity();
        player.loadData();
        return player;
    }

    @Override
    @NotNull
    public Inventory getPlayerEnderChest(@NotNull Player player) {
        return new PlayerEnderChest((CraftPlayer) player).getInventory();
    }

    @Override
    @NotNull
    public Inventory getPlayerInventory(@NotNull Player player) {
        return new PlayerInventory((CraftPlayer) player).getInventory();
    }

    @Override
    public void setGameMode(@NotNull Player player, @NotNull GameMode mode) {
        if (player.isOnline()) {
            player.setGameMode(mode);
            return;
        }
        CraftPlayer craftPlayer = (CraftPlayer) player;
        ServerPlayer serverPlayer = craftPlayer.getHandle();
        serverPlayer.gameMode.changeGameModeForPlayer(GameType.byName(mode.name().toLowerCase()));
        craftPlayer.saveData();
    }

    public void teleport(@NotNull Player player, @NotNull Location location) {
        if (player.isOnline()) {
            player.teleport(location);
            return;
        }
        CraftPlayer craftPlayer = (CraftPlayer) player;
        ServerPlayer serverPlayer = craftPlayer.getHandle();
        serverPlayer.setPosRaw(location.getX(), location.getY(), location.getZ());
        if (player.getWorld() != location.getWorld() && location.getWorld() != null) {
            CraftWorld craftWorld = (CraftWorld) location.getWorld();
            serverPlayer.level = craftWorld.getHandle();
        }
        craftPlayer.saveData();
    }

    @Override
    public void openAnvil(@NotNull Player player) {
        CraftPlayer craftPlayer = (CraftPlayer) player;
        ServerPlayer nmsPlayer = craftPlayer.getHandle();

        int counter = nmsPlayer.nextContainerCounter();
        nmsPlayer.connection.send(new ClientboundOpenScreenPacket(counter, MenuType.ANVIL, CraftChatMessage.fromString("Repairing")[0]));
    }

    @Override
    public void openEnchanting(@NotNull Player player) {

    }
}
