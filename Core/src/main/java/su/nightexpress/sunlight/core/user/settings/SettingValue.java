package su.nightexpress.sunlight.core.user.settings;

import org.jetbrains.annotations.NotNull;

public class SettingValue<T> {

    private final T value;

    public SettingValue(@NotNull T value) {
        this.value = value;
    }

    @NotNull
    public static <T> SettingValue<T> of(@NotNull T value) {
        return new SettingValue<>(value);
    }

    @NotNull
    public T getValue() {
        return value;
    }
}
