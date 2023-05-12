package su.nightexpress.sunlight.data.impl.settings;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class BasicSettings {

    private final Map<UserSetting<?>, SettingValue<?>> settings;

    public BasicSettings() {
        this(new HashMap<>());
    }

    public BasicSettings(@NotNull Map<UserSetting<?>, SettingValue<?>> settings) {
        this.settings = settings;
    }

    @NotNull
    public Map<UserSetting<?>, SettingValue<?>> getSettings() {
        return settings;
    }

    public <T> boolean has(@NotNull UserSetting<T> setting) {
        return this.settings.containsKey(setting);
    }

    public <T> void set(@NotNull UserSetting<T> setting, @NotNull T value) {
        this.settings.put(setting, SettingValue.of(value));
    }

    @SuppressWarnings("unchecked")
    public <T> T get(@NotNull UserSetting<T> setting) {
        return (T) this.settings.getOrDefault(setting, SettingValue.of(setting.getDefaultValue())).getValue();
    }
}
