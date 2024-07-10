package su.nightexpress.sunlight.core.user.settings;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class SettingRegistry {

    private static final Map<String, Setting<?>> REGISTRY = new HashMap<>();

    public static final Setting<String>  CUSTOM_NAME   = register(Setting.create("custom_name", "", true));
    public static final Setting<Boolean> ACCEPT_PM     = register(Setting.create("accept_pm", true, true));

    @NotNull
    public static <T> Setting<T> register(@NotNull String name, @NotNull Class<T> clazz, @NotNull T defaultValue, @NotNull Parser<T> parser, boolean persistent) {
        return register(new Setting<>(name, clazz, defaultValue, parser, persistent));
    }

    @NotNull
    public static <T> Setting<T> register(@NotNull Setting<T> setting) {
        REGISTRY.put(setting.getName(), setting);
        return setting;
    }

    @Nullable
    public static Setting<?> getByName(@NotNull String name) {
        return REGISTRY.get(name);
    }

    @NotNull
    public static Collection<Setting<?>> values() {
        return REGISTRY.values();
    }
}
