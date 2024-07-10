package su.nightexpress.sunlight.command.mode;

import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public enum ToggleMode {

    ON(in -> true),
    OFF(in -> false),
    TOGGLE(in -> !in);

    private final Function<Boolean, Boolean> function;

    ToggleMode(@NotNull Function<Boolean, Boolean> function) {
        this.function = function;
    }

    public boolean apply(boolean input) {
        return this.function.apply(input);
    }
}
