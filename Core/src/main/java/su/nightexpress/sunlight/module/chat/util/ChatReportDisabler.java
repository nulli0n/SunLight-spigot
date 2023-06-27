package su.nightexpress.sunlight.module.chat.util;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.*;
import net.md_5.bungee.chat.ComponentSerializer;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.Version;
import su.nightexpress.sunlight.SunLight;
import su.nightexpress.sunlight.SunLightAPI;
import su.nightexpress.sunlight.hook.impl.ProtocolLibHook;

import java.util.UUID;

public final class ChatReportDisabler extends PacketAdapter {

    private static ChatReportDisabler instance;

    public static void setup(@NotNull SunLight plugin) {
        if (instance == null) {
            ProtocolLibHook.PROTOCOL_MANAGER.addPacketListener(instance = new ChatReportDisabler(plugin));
        }
    }

    public static void shutdown(@NotNull SunLight plugin) {
        if (instance != null) {
            ProtocolLibHook.PROTOCOL_MANAGER.removePacketListener(instance);
            instance = null;
        }
    }

    private ChatReportDisabler(@NotNull SunLight plugin) {
        super(plugin, ListenerPriority.NORMAL,
            PacketType.Play.Server.SERVER_DATA,
            PacketType.Play.Server.CHAT,
            PacketType.Play.Server.SYSTEM_CHAT,
            PacketType.Play.Server.PLAYER_CHAT_HEADER);
    }

    @Override
    public void onPacketSending(PacketEvent event) {
        if (event.getPacketType() == PacketType.Play.Server.SERVER_DATA) {
            PacketContainer container = event.getPacket();
            if (Version.isBehind(Version.V1_19_R2)) {
                container.getBooleans().write(1, true);
            }
            else {
                container.getBooleans().write(0, true);
            }
        }
        else if (event.getPacketType() == PacketType.Play.Server.CHAT) {
            PacketContainer container = event.getPacket();

            if (Version.isAbove(Version.V1_19_R1)) {
                Object finePacket = SunLightAPI.PLUGIN.getSunNMS().fineChatPacket(container.getHandle());
                event.setPacket(new PacketContainer(PacketType.Play.Server.SYSTEM_CHAT, finePacket));
            }
            else {
                InternalStructure chatMessage = container.getStructures().read(0);

                InternalStructure signedMessageHeader = chatMessage.getStructures().read(0); // SignedMessageHeader c
                InternalStructure messageSignature = chatMessage.getStructures().read(1); // MessageSignature d
                InternalStructure signedMessageBody = chatMessage.getStructures().read(2); // SignedMessageBody e

                // Null previous signature
                // Null sender's UUID.
                try {
                    signedMessageHeader.getStructures().write(0, null);
                }
                catch (Exception ignored) {

                }
                signedMessageHeader.getUUIDs().write(0, new UUID(0L, 0L));
                chatMessage.getStructures().write(0, signedMessageHeader);

                // Null signature
                if (!Bukkit.getServer().getOnlineMode()) {
                    messageSignature.getByteArrays().write(0, new byte[0]);
                    chatMessage.getStructures().write(1, messageSignature);
                }

                // Null salt
                signedMessageBody.getLongs().write(0, 0L);
                chatMessage.getStructures().write(2, signedMessageBody);

                container.getStructures().write(0, chatMessage);
            }
        }
        else if (event.getPacketType() == PacketType.Play.Server.PLAYER_CHAT_HEADER) {
            event.setCancelled(true);

            /*PacketContainer container = event.getPacket();

            InternalStructure signedMessageHeader = container.getStructures().read(0); // SignedMessageHeader c
            InternalStructure messageSignature = container.getStructures().read(1); // MessageSignature d

            // Null previous signature
            // Null sender's UUID.
            try {
                signedMessageHeader.getStructures().write(0, null);
            }
            catch (Exception ignored) {

            }
            signedMessageHeader.getUUIDs().write(0, new UUID(0L, 0L));
            container.getStructures().write(0, signedMessageHeader);

            // Null signature
            if (!Bukkit.getServer().getOnlineMode()) {
                messageSignature.getByteArrays().write(0, new byte[0]);
                container.getStructures().write(1, messageSignature);
            }*/
        }
        else if (event.getPacketType() == PacketType.Play.Server.SYSTEM_CHAT) {
            PacketContainer container = event.getPacket();
            String content = container.getStrings().read(0);
            if (content == null) return;

            container.getStrings().write(0, ComponentSerializer.toString(ComponentSerializer.parse(content)));
        }
    }
}
