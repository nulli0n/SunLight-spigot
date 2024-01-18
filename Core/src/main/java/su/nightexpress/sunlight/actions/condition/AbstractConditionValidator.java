package su.nightexpress.sunlight.actions.condition;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.sunlight.actions.parameter.AbstractParametized;
import su.nightexpress.sunlight.actions.parameter.ParameterId;
import su.nightexpress.sunlight.actions.parameter.ParameterResult;

public abstract class AbstractConditionValidator extends AbstractParametized {

    public AbstractConditionValidator(@NotNull String key) {
        super(key);
        this.registerParameter(ParameterId.MESSAGE);
    }

    protected abstract boolean validate(@NotNull Player executor, @NotNull ParameterResult result);

    public final boolean process(@NotNull Player executor, @NotNull String str) {
        ParameterResult result = this.getParameterResult(str);
        if (!this.validate(executor, result)) {
            String message = (String) result.getValue(ParameterId.MESSAGE);
            if (message != null) executor.sendMessage(message);
            return false;
        }
        return true;
    }
}
