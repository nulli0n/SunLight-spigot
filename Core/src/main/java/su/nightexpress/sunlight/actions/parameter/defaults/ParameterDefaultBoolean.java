package su.nightexpress.sunlight.actions.parameter.defaults;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.sunlight.actions.parameter.AbstractParameter;
import su.nightexpress.sunlight.actions.parameter.parser.IParameterValueParser;

@Deprecated // TODO Move in Paramter class as static constructor.
public class ParameterDefaultBoolean extends AbstractParameter<Boolean> {

    public ParameterDefaultBoolean(@NotNull String key, @NotNull String flag) {
        super(key, flag);
    }

    @Override
    @NotNull
    public IParameterValueParser<Boolean> getParser() {
        return IParameterValueParser.BOOLEAN;
    }
}
