package su.nightexpress.sunlight.user.property;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class UserPropertyRegistry {

    private static final Map<String, UserProperty<?>> REGISTRY = new HashMap<>();

    @NotNull
    public static <T> UserProperty<T> register(@NotNull String name, @NotNull Class<T> type, @NotNull T defaultValue, boolean persistent) {
        return register(new UserProperty<>(name, type, defaultValue, persistent));
    }

    @NotNull
    public static <T> UserProperty<T> register(@NotNull UserProperty<T> setting) {
        REGISTRY.put(setting.getName(), setting);
        return setting;
    }

    @Nullable
    public static UserProperty<?> getByName(@NotNull String name) {
        return REGISTRY.get(name);
    }

    @NotNull
    public static Set<UserProperty<?>> values() {
        return Set.copyOf(REGISTRY.values());
    }
}
