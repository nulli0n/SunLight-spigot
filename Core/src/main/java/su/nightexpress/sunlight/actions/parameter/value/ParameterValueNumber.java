package su.nightexpress.sunlight.actions.parameter.value;

import org.jetbrains.annotations.NotNull;

@Deprecated
public class ParameterValueNumber {

    private double   value;
    private boolean  isPercent;
    private Operator operator;

    public ParameterValueNumber(double value) {
        this.value = value;
        this.setOperator(Operator.EQUALS);
    }

    public double getValue(double def) {
        return this.hasValue() ? this.value : def;
    }

    public boolean hasValue() {
        return this.value != -1;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public boolean hasOperator() {
        return this.operator != null;
    }

    @NotNull
    public ParameterValueNumber.Operator getOperator() {
        return this.operator;
    }

    public void setOperator(@NotNull Operator operator) {
        this.operator = operator;
    }

    public boolean isPercent() {
        return isPercent;
    }

    public void setPercent(boolean percent) {
        isPercent = percent;
    }

    public enum Operator {
        GREATER(">"), LOWER("<"), EQUALS("="),
        ;

        public String prefix;

        Operator(@NotNull String prefix) {
            this.prefix = prefix;
        }

        @NotNull
        public static ParameterValueNumber.Operator parse(@NotNull String str) {
            for (Operator operator : values()) {
                if (str.startsWith(operator.prefix)) {
                    return operator;
                }
            }
            return Operator.EQUALS;
        }

        public boolean compare(double from, double to) {
            return switch (this) {
                case GREATER -> from > to;
                case LOWER -> from < to;
                case EQUALS -> from == to;
            };
        }
    }
}
