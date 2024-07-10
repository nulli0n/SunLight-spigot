package su.nightexpress.sunlight.data.user;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.sunlight.core.user.settings.Setting;
import su.nightexpress.sunlight.core.user.settings.SettingValue;

import java.util.HashMap;
import java.util.Map;

public class UserSettings {

    private final Map<String, SettingValue<?>> values;

    public UserSettings() {
        this(new HashMap<>());
    }

    public UserSettings(@NotNull Map<String, SettingValue<?>> values) {
        this.values = values;
    }

    @NotNull
    public Map<String, SettingValue<?>> getValues() {
        return values;
    }

    public <T> boolean has(@NotNull Setting<T> setting) {
        return this.values.containsKey(setting.getName());
    }

    public <T> void set(@NotNull Setting<T> setting, @NotNull T value) {
        this.values.put(setting.getName(), SettingValue.of(value));
    }

    @NotNull
    public <T> T get(@NotNull Setting<T> setting) {
        return this.get(setting.getName(), setting.getClazz(), setting.getDefaultValue());
    }

    @NotNull
    public <T> T get(@NotNull String name, @NotNull Class<T> clazz, @NotNull T defaultValue) {
        name = name.toLowerCase();

        SettingValue<?> value = this.values.getOrDefault(name, SettingValue.of(defaultValue));

        Object result = value.getValue();
        if (clazz.isAssignableFrom(result.getClass())) {
            return clazz.cast(result);
        }
        else {
            throw new IllegalArgumentException("Setting '" + name + "' is defined as " + result.getClass().getSimpleName() + ", not " + clazz.getSimpleName());
        }
    }
}
