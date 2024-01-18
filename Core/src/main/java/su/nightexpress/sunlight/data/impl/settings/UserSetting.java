package su.nightexpress.sunlight.data.impl.settings;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nexmedia.engine.utils.StringUtil;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class UserSetting<E> {

    private static final Map<String, UserSetting<?>> REGISTRY = new HashMap<>();

    public static final Function<String, Boolean> PARSER_BOOLEAN = Boolean::parseBoolean;
    public static final Function<String, String>  PARSER_STRING  = str -> str;
    public static final Function<String, Double>  PARSER_DOUBLE  = str -> StringUtil.getDouble(str, 0, true);
    public static final Function<String, Integer> PARSER_INT     = str -> StringUtil.getInteger(str, 0, true);
    public static final Function<String, Long>    PARSER_LONG    = str -> (long) StringUtil.getInteger(str, 0, true);

    private final String name;
    private final E defaultValue;
    private final Function<String, E> parser;
    private final boolean isPersistent;

    public UserSetting(@NotNull String name, @NotNull E defaultValue, @NotNull Function<String, E> parser, boolean isPersistent) {
        this.name = StringUtil.oneSpace(name.toLowerCase()).replace(" ", "_");
        this.defaultValue = defaultValue;
        this.parser = parser;
        this.isPersistent = isPersistent;
    }

    @NotNull
    public static UserSetting<Boolean> asBoolean(@NotNull String name, @NotNull Boolean defaultValue, boolean isPersistent) {
        return register(name, defaultValue, PARSER_BOOLEAN, isPersistent);
    }

    @NotNull
    public static UserSetting<Integer> asInt(@NotNull String name, @NotNull Integer defaultValue, boolean isPersistent) {
        return register(name, defaultValue, PARSER_INT, isPersistent);
    }

    @NotNull
    public static UserSetting<Long> asLong(@NotNull String name, @NotNull Long defaultValue, boolean isPersistent) {
        return register(name, defaultValue, PARSER_LONG, isPersistent);
    }

    @NotNull
    public static <E> UserSetting<E> register(@NotNull String name, @NotNull E defaultValue, @NotNull Function<String, E> parser, boolean isPersistent) {
        UserSetting<E> setting = new UserSetting<>(name, defaultValue, parser, isPersistent);
        REGISTRY.put(setting.getName(), setting);
        return setting;
    }

    @Nullable
    public static UserSetting<?> getByName(@NotNull String name) {
        return REGISTRY.get(name);
    }

    @NotNull
    public static Collection<UserSetting<?>> values() {
        return REGISTRY.values();
    }

    @NotNull
    public String getName() {
        return name;
    }

    @NotNull
    public E getDefaultValue() {
        return defaultValue;
    }

    @NotNull
    public E parse(@NotNull String from) {
        return this.parser.apply(from);
    }

    public boolean isPersistent() {
        return isPersistent;
    }
}
