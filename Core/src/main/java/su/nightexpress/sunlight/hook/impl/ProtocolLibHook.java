package su.nightexpress.sunlight.hook.impl;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ProtocolLibHook {

    public static final int TEAM_CREATED    = 0;
    public static final int TEAM_REMOVED    = 1;
    public static final int TEAM_UPDATED    = 2;
    public static final int PLAYERS_ADDED   = 3;
    public static final int PLAYERS_REMOVED = 4;

    public static final ProtocolManager PROTOCOL_MANAGER = ProtocolLibrary.getProtocolManager();

    public static void sendPacketServer(@NotNull Player player, @NotNull PacketContainer packet) {
        try {
            PROTOCOL_MANAGER.sendServerPacket(player, packet);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
