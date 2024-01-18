package su.nightexpress.sunlight.actions.parameter.parser;

import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.utils.StringUtil;
import su.nightexpress.sunlight.actions.parameter.value.ParameterValueNumber;

@Deprecated
public class ParameterParserNumber implements IParameterValueParser<ParameterValueNumber> {

    @Override
    @NotNull
    public ParameterValueNumber parse(@NotNull String str) {
        boolean perc = str.contains("%");
        ParameterValueNumber.Operator operator = ParameterValueNumber.Operator.parse(str);
        int sub = str.startsWith(operator.prefix) ? operator.prefix.length() : 0;

        str = str.substring(sub).replace("%", "");
        double amount = StringUtil.getDouble(str, 0D, true);

        ParameterValueNumber number = new ParameterValueNumber(amount);
        number.setPercent(perc);
        number.setOperator(operator);

        return number;
    }
}
