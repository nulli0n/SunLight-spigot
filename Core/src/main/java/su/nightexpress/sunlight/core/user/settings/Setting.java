package su.nightexpress.sunlight.core.user.settings;

import org.jetbrains.annotations.NotNull;

public class Setting<T> {

    private final String    name;
    private final Class<T>  clazz;
    private final T         defaultValue;
    private final Parser<T> parser;
    private final boolean   persistent;

    public Setting(@NotNull String name, @NotNull Class<T> clazz, @NotNull T defaultValue, @NotNull Parser<T> parser, boolean persistent) {
        this.name = name.toLowerCase().replace(" ", "_");
        this.clazz = clazz;
        this.defaultValue = defaultValue;
        this.parser = parser;
        this.persistent = persistent;
    }

    @NotNull
    public static Setting<Boolean> create(@NotNull String name, @NotNull Boolean defaultValue, boolean persistent) {
        return new Setting<>(name, Boolean.class, defaultValue, Parser.BOOLEAN, persistent);
    }

    @NotNull
    public static Setting<Integer> create(@NotNull String name, @NotNull Integer defaultValue, boolean persistent) {
        return new Setting<>(name, Integer.class, defaultValue, Parser.INTEGER, persistent);
    }

    @NotNull
    public static Setting<Long> create(@NotNull String name, @NotNull Long defaultValue, boolean persistent) {
        return new Setting<>(name, Long.class, defaultValue, Parser.LONG, persistent);
    }

    @NotNull
    public static Setting<String> create(@NotNull String name, @NotNull String defaultValue, boolean persistent) {
        return new Setting<>(name, String.class, defaultValue, Parser.STRING, persistent);
    }

    @NotNull
    public T fromString(@NotNull String string) {
        return this.parser.fromString(string);
    }

    @NotNull
    public String toString(@NotNull Object value) {
        return this.parser.toString(value);
    }

    @NotNull
    public String getName() {
        return name;
    }

    @NotNull
    public Class<T> getClazz() {
        return clazz;
    }

    @NotNull
    public Parser<T> getParser() {
        return parser;
    }

    @NotNull
    public T getDefaultValue() {
        return defaultValue;
    }

    public boolean isPersistent() {
        return persistent;
    }
}
