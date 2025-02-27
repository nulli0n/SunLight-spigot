package su.nightexpress.sunlight.hook;

import su.nightexpress.nightcore.util.Plugins;

public class HookId {

    public static final String PROTOCOL_LIB  = "ProtocolLib";
    public static final String PACKET_EVENTS = "packetevents";
    public static final String DISCORD_SRV   = "DiscordSRV";

    public static boolean hasDiscordSRV() {
        return Plugins.isInstalled(DISCORD_SRV);
    }
}
