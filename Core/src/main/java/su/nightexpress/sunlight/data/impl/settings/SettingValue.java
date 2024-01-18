package su.nightexpress.sunlight.data.impl.settings;

import org.jetbrains.annotations.NotNull;

public class SettingValue<E> {

    private final E value;

    public SettingValue(@NotNull E value) {
        this.value = value;
    }

    @NotNull
    public static <T> SettingValue<T> of(@NotNull T value) {
        return new SettingValue<>(value);
    }

    @NotNull
    public E getValue() {
        return value;
    }
}
