package su.nightexpress.sunlight.module.chat.channel;

import org.jetbrains.annotations.NotNull;

public record ChannelCommand(boolean enabled, @NotNull String alias) {

}
