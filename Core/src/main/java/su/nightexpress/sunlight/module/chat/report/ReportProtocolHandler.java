package su.nightexpress.sunlight.module.chat.report;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.sunlight.SunLightPlugin;

public final class ReportProtocolHandler implements ReportHandler {

    private final SunLightPlugin plugin;

    private Listener listener;

    public ReportProtocolHandler(@NotNull SunLightPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void load() {
        if (this.listener == null) {
            ProtocolLibrary.getProtocolManager().addPacketListener(this.listener = new Listener(this.plugin));
        }
    }

    @Override
    public void unload() {
        if (this.listener != null) {
            ProtocolLibrary.getProtocolManager().removePacketListener(this.listener);
            this.listener = null;
        }
    }

    private static class Listener extends PacketAdapter {

        private final SunLightPlugin plugin;

        public Listener(@NotNull SunLightPlugin plugin) {
            super(plugin, ListenerPriority.NORMAL,
                PacketType.Play.Server.SERVER_DATA,
                PacketType.Play.Server.CHAT,
                PacketType.Play.Server.SYSTEM_CHAT,
                PacketType.Play.Server.LOGIN
            );
            this.plugin = plugin;
        }

        @Override
        public void onPacketSending(PacketEvent event) {
            if (event.getPacketType() == PacketType.Play.Server.LOGIN) {
                PacketContainer container = event.getPacket();
                container.getBooleans().write(4, true);
            }
            else if (event.getPacketType() == PacketType.Play.Server.CHAT) {
                PacketContainer container = event.getPacket();

                this.plugin.internals().ifPresent(nms -> {
                    Object finePacket = nms.fineChatPacket(container.getHandle());
                    event.setPacket(new PacketContainer(PacketType.Play.Server.SYSTEM_CHAT, finePacket));
                });
            }
        }
    }
}
