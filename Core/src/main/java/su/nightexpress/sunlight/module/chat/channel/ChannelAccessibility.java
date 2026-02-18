package su.nightexpress.sunlight.module.chat.channel;

public record ChannelAccessibility(boolean autoJoin, boolean permissionToListen, boolean permissionToSpeak, int messageCooldown) {

}
