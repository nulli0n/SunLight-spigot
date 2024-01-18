package su.nightexpress.sunlight.actions.parameter;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.sunlight.actions.parameter.parser.IParameterValueParser;

import java.util.regex.Pattern;

// TODO Remove abstraction
public abstract class AbstractParameter<T> {

    protected final String  name;
    protected final String  flag;
    protected final Pattern pattern;

    public AbstractParameter(@NotNull String name, @NotNull String flag) {
        this.name = name.toLowerCase();
        this.flag = flag;
        this.pattern = Pattern.compile("(~)+(" + this.getFlag() + ")+?(:)+(.*?)(;)");
    }

    @NotNull
    public final String getName() {
        return this.name;
    }

    @NotNull
    public final Pattern getPattern() {
        return this.pattern;
    }

    @NotNull
    public final String getFlag() {
        return this.flag;
    }

    @NotNull
    @Deprecated // TODO Create field, move in constructor
    public abstract IParameterValueParser<T> getParser();
}
