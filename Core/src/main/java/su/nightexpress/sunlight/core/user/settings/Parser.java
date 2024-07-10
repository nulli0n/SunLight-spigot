package su.nightexpress.sunlight.core.user.settings;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.util.NumberUtil;

import java.util.function.Function;

public interface Parser<T> {

    @NotNull T fromString(@NotNull String string);

    @NotNull String toString(@NotNull Object value);

    @NotNull
    static <T> Parser<T> simple(@NotNull Function<String, T> parser) {

        return new Parser<>() {
            @Override
            @NotNull
            public T fromString(@NotNull String string) {
                return parser.apply(string);
            }

            @Override
            @NotNull
            public String toString(@NotNull Object value) {
                return String.valueOf(value);
            }
        };

    }

    Parser<Boolean> BOOLEAN = simple(Boolean::parseBoolean);

    Parser<Integer> INTEGER = simple(string -> NumberUtil.getAnyInteger(string, 0));

    Parser<Integer> INTEGER_ABS = simple(string -> NumberUtil.getInteger(string, 0));

    Parser<Double> DOUBLE = simple(string -> NumberUtil.getAnyDouble(string, 0));

    Parser<Double> DOUBLE_ABS = simple(string -> NumberUtil.getDouble(string, 0));

    Parser<Long> LONG = simple(string -> (long) NumberUtil.getAnyInteger(string, 0));

    Parser<Long> LONG_ABS = simple(string -> (long) NumberUtil.getInteger(string, 0));

    Parser<String> STRING = simple(string -> string);
}
