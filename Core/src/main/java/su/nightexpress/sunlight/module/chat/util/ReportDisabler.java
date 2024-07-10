package su.nightexpress.sunlight.module.chat.util;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.*;
import net.md_5.bungee.chat.ComponentSerializer;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.util.Version;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.hook.impl.ProtocolLibHook;

import java.util.UUID;

public final class ReportDisabler extends PacketAdapter {

//    private static ChatReportDisabler instance;

    private final SunLightPlugin plugin;

    public ReportDisabler(@NotNull SunLightPlugin plugin) {
        super(plugin, ListenerPriority.NORMAL,
            PacketType.Play.Server.SERVER_DATA,
            PacketType.Play.Server.CHAT,
            PacketType.Play.Server.SYSTEM_CHAT,
            PacketType.Play.Server.LOGIN
        );
        this.plugin = plugin;
    }

    public void setup() {
//        if (instance == null) {
            ProtocolLibHook.PROTOCOL_MANAGER.addPacketListener(this);
//        }
    }

    public void shutdown() {
//        if (instance != null) {
            ProtocolLibHook.PROTOCOL_MANAGER.removePacketListener(this);
//            instance = null;
//        }
    }

    @Override
    public void onPacketSending(PacketEvent event) {
        if (event.getPacketType() == PacketType.Play.Server.LOGIN) {
            if (!Version.isAtLeast(Version.MC_1_20_6)) return;

            PacketContainer container = event.getPacket();
            container.getBooleans().write(4, true);
        }
        else if (event.getPacketType() == PacketType.Play.Server.SERVER_DATA) {
            if (Version.isAbove(Version.V1_20_R3)) return;

            PacketContainer container = event.getPacket();
            if (Version.isBehind(Version.V1_19_R3)) {
                container.getBooleans().write(1, true);
            }
            else {
                container.getBooleans().write(0, true);
            }
        }
        else if (event.getPacketType() == PacketType.Play.Server.CHAT) {
            PacketContainer container = event.getPacket();

            if (Version.isAtLeast(Version.V1_19_R3)) {
                Object finePacket = this.plugin.getSunNMS().fineChatPacket(container.getHandle());
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
        else if (event.getPacketType() == PacketType.Play.Server.SYSTEM_CHAT) {
            PacketContainer container = event.getPacket();
//            if (Version.isAtLeast(Version.V1_20_R3)) {
//                //WrappedChatComponent content = container.getChatComponents().read(0);
//                //if (content == null) return;
//
//                //container.getChatComponents().write(0, ComponentSerializer.toString(ComponentSerializer.parse(content)));
//            }
            /*else*/ if (Version.isBehind(Version.V1_20_R3)) {
                String content = container.getStrings().read(0);
                if (content == null) return;

                container.getStrings().write(0, ComponentSerializer.toString(ComponentSerializer.parse(content)));
            }
        }
    }
}
