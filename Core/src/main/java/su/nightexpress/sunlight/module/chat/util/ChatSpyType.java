package su.nightexpress.sunlight.module.chat.util;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.sunlight.data.impl.settings.UserSetting;

public enum ChatSpyType {

    SOCIAL(
        UserSetting.register("spy_chat_social", false, UserSetting.PARSER_BOOLEAN, true),
        UserSetting.register("spy_log_social", false, UserSetting.PARSER_BOOLEAN, true)
    ),
    COMMAND(
        UserSetting.register("spy_chat_command", false, UserSetting.PARSER_BOOLEAN, true),
        UserSetting.register("spy_log_command", false, UserSetting.PARSER_BOOLEAN, true)
    ),
    CHAT(
        UserSetting.register("spy_chat_chat", false, UserSetting.PARSER_BOOLEAN, true),
        UserSetting.register("spy_log_chat", false, UserSetting.PARSER_BOOLEAN, true)
    );

    private final UserSetting<Boolean> settingChat;
    private final UserSetting<Boolean> settingLog;

    ChatSpyType(@NotNull UserSetting<Boolean> settingChat, @NotNull UserSetting<Boolean> settingLog) {
        this.settingChat = settingChat;
        this.settingLog = settingLog;
    }

    @NotNull
    public UserSetting<Boolean> getSettingChat() {
        return this.settingChat;
    }

    @NotNull
    public UserSetting<Boolean> getSettingLog() {
        return settingLog;
    }
}
