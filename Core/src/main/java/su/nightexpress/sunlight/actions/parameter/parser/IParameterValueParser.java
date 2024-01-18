package su.nightexpress.sunlight.actions.parameter.parser;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nexmedia.engine.utils.Colorizer;

@Deprecated // TODO Move in Parameter class
public interface IParameterValueParser<T> {

    IParameterValueParser<String>  STRING  = Colorizer::apply;
    IParameterValueParser<Boolean> BOOLEAN = Boolean::parseBoolean;
    ParameterParserNumber          NUMBER  = new ParameterParserNumber();

    @Nullable T parse(@NotNull String str);
}
