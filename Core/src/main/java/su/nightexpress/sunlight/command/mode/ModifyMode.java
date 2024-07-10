package su.nightexpress.sunlight.command.mode;

import org.jetbrains.annotations.NotNull;

import java.util.function.BiFunction;

public enum ModifyMode {

    ADD(Double::sum),
    SET((in, amount) -> amount),
    REMOVE((in, amount) -> in - amount);

    private final BiFunction<Double, Double, Double> function;

    ModifyMode(@NotNull BiFunction<Double, Double, Double> function) {
        this.function = function;
    }

    public double modify(double input, double amount) {
        return this.function.apply(input, amount);
    }
}
