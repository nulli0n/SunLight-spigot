package su.nightexpress.sunlight.hook.impl;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ProtocolLibHook {

    public static final ProtocolManager PROTOCOL_MANAGER = ProtocolLibrary.getProtocolManager();

    public static void sendPacketServer(@NotNull Player player, @NotNull PacketContainer packet) {
        try {
            PROTOCOL_MANAGER.sendServerPacket(player, packet);
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
