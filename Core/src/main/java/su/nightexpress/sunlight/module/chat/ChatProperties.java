package su.nightexpress.sunlight.module.chat;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.sunlight.module.chat.spy.SpyType;
import su.nightexpress.sunlight.user.property.UserProperty;

public class ChatProperties {

    public static final UserProperty<Boolean> CONVERSATIONS = UserProperty.create("accept_pm", Boolean.class, true, true);
    public static final UserProperty<Boolean> MENTIONS      = UserProperty.create("mentions", Boolean.class, true, true);

    public static final UserProperty<Boolean> SOCIAL_SPY_INFO  = UserProperty.create("spy_chat_social", Boolean.class, false, true);
    public static final UserProperty<Boolean> SOCIAL_SPY_LOG   = UserProperty.create("spy_log_social", Boolean.class, false, true);
    public static final UserProperty<Boolean> COMMAND_SPY_INFO = UserProperty.create("spy_chat_command", Boolean.class, false, true);
    public static final UserProperty<Boolean> COMMAND_SPY_LOG  = UserProperty.create("spy_log_command", Boolean.class, false, true);
    public static final UserProperty<Boolean> CHAT_SPY_INFO    = UserProperty.create("spy_chat_chat", Boolean.class, false, true);
    public static final UserProperty<Boolean> CHAT_SPY_LOG     = UserProperty.create("spy_log_chat", Boolean.class, false, true);

    @NotNull
    public static UserProperty<Boolean> getSpyInfoProperty(@NotNull SpyType type) {
        return switch (type) {
            case CHAT -> CHAT_SPY_INFO;
            case COMMAND -> COMMAND_SPY_INFO;
            case SOCIAL -> SOCIAL_SPY_INFO;
        };
    }

    @NotNull
    public static UserProperty<Boolean> getSpyLogProperty(@NotNull SpyType type) {
        return switch (type) {
            case CHAT -> CHAT_SPY_LOG;
            case COMMAND -> COMMAND_SPY_LOG;
            case SOCIAL -> SOCIAL_SPY_LOG;
        };
    }
}
