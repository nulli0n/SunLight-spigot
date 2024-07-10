package su.nightexpress.sunlight.module.chat.module.spy;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.sunlight.core.user.settings.Setting;
import su.nightexpress.sunlight.core.user.settings.SettingRegistry;

public enum SpyType {

    SOCIAL(
        Setting.create("spy_chat_social", false, true),
        Setting.create("spy_log_social", false, true)
    ),
    COMMAND(
        Setting.create("spy_chat_command", false, true),
        Setting.create("spy_log_command", false, true)
    ),
    CHAT(
        Setting.create("spy_chat_chat", false, true),
        Setting.create("spy_log_chat", false, true)
    );

    private final Setting<Boolean> settingChat;
    private final Setting<Boolean> settingLog;

    SpyType(@NotNull Setting<Boolean> settingChat, @NotNull Setting<Boolean> settingLog) {
        this.settingChat = SettingRegistry.register(settingChat);
        this.settingLog = SettingRegistry.register(settingLog);
    }

    @NotNull
    public Setting<Boolean> getSettingChat() {
        return this.settingChat;
    }

    @NotNull
    public Setting<Boolean> getSettingLog() {
        return settingLog;
    }
}
