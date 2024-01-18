package su.nightexpress.sunlight.actions.parameter.defaults;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.sunlight.actions.parameter.AbstractParameter;
import su.nightexpress.sunlight.actions.parameter.parser.IParameterValueParser;

@Deprecated // TODO Move in Paramter class as static constructor.
public class ParameterDefaultString extends AbstractParameter<String> {

    public ParameterDefaultString(@NotNull String key, @NotNull String flag) {
        super(key, flag);
    }

    @Override
    public @NotNull IParameterValueParser<String> getParser() {
        return IParameterValueParser.STRING;
    }
}
