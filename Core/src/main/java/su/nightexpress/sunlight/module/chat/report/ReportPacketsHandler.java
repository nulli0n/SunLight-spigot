package su.nightexpress.sunlight.module.chat.report;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListenerAbstract;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.chat.ChatType;
import com.github.retrooper.packetevents.protocol.chat.message.ChatMessage_v1_19_3;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.packettype.PacketTypeCommon;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerChatMessage;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerJoinGame;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerServerData;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSystemChatMessage;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.util.Version;
import su.nightexpress.sunlight.SunLightPlugin;

public class ReportPacketsHandler extends ReportHandler {

    private Listener listener;

    public ReportPacketsHandler(@NotNull SunLightPlugin plugin) {
        super(plugin);
    }

    @Override
    protected void onLoad() {
        if (this.listener == null) {
            PacketEvents.getAPI().getEventManager().registerListener(this.listener = new Listener());
        }
    }

    @Override
    protected void onShutdown() {
        if (this.listener != null) {
            PacketEvents.getAPI().getEventManager().unregisterListener(this.listener);
            this.listener = null;
        }
    }

    private static class Listener extends PacketListenerAbstract {

        public Listener() {
            super(PacketListenerPriority.NORMAL);
        }

        @Override
        public void onPacketSend(PacketSendEvent event) {
            PacketTypeCommon type = event.getPacketType();

            if (type == PacketType.Play.Server.JOIN_GAME) {
                if (Version.isBehind(Version.MC_1_20_6)) return;

                WrapperPlayServerJoinGame joinGame = new WrapperPlayServerJoinGame(event);
                joinGame.setEnforcesSecureChat(true);

                event.markForReEncode(true);
            }
            else if (type == PacketType.Play.Server.SERVER_DATA) {
                WrapperPlayServerServerData serverData = new WrapperPlayServerServerData(event);
                serverData.setEnforceSecureChat(true);

                event.markForReEncode(true);
            }
            else if (type == PacketType.Play.Server.CHAT_MESSAGE) {
                WrapperPlayServerChatMessage chatMessage = new WrapperPlayServerChatMessage(event);
                if (chatMessage.getMessage() instanceof ChatMessage_v1_19_3 message) {
                    var unsigned = message.getUnsignedChatContent();

                    Component component = unsigned.orElseGet(message::getChatContent);
                    ChatType.Bound bound = message.getChatFormatting();

                    ChatType chatType = bound.getType();//chatMessage.getMessage().getType();

                    ChatType.Bound decorator = new ChatType.Bound(chatType, bound.getName(), bound.getTargetName());
                    component = chatType.getChatDecoration().decorate(component, decorator);

                    WrapperPlayServerSystemChatMessage system = new WrapperPlayServerSystemChatMessage(false, component);
                    event.setCancelled(true);

                    event.getUser().sendPacket(system);
                }
            }
        }
    }
}
