package su.nightexpress.sunlight.user.property;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.util.Strings;

public class UserProperty<T> {

    private final String   name;
    private final Class<T> type;
    private final T        defaultValue;
    private final boolean  persistent;

    public UserProperty(@NotNull String name, @NotNull Class<T> type, @NotNull T defaultValue, boolean persistent) {
        this.name = Strings.varStyle(name).orElseThrow(() -> new IllegalArgumentException("Invalid property name"));
        this.type = type;
        this.defaultValue = defaultValue;
        this.persistent = persistent;
    }

    @NotNull
    public static <T> UserProperty<T> create(@NotNull String name, @NotNull Class<T> type, @NotNull T defaultValue, boolean persistent) {
        return new UserProperty<>(name, type, defaultValue, persistent);
    }

    @NotNull
    public String getName() {
        return this.name;
    }

    @NotNull
    public Class<T> getType() {
        return this.type;
    }

    @NotNull
    public T getDefaultValue() {
        return this.defaultValue;
    }

    public boolean isPersistent() {
        return this.persistent;
    }
}
