package su.nightexpress.sunlight.actions.condition.list;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.sunlight.actions.condition.AbstractConditionValidator;
import su.nightexpress.sunlight.actions.condition.ConditionId;
import su.nightexpress.sunlight.actions.parameter.ParameterId;
import su.nightexpress.sunlight.actions.parameter.ParameterResult;

public class Condition_Permission extends AbstractConditionValidator {

    public Condition_Permission() {
        super(ConditionId.PERMISSION);
        this.registerParameter(ParameterId.NAME);
    }

    @Override
    protected boolean validate(@NotNull Player player, @NotNull ParameterResult result) {
        String node = (String) result.getValue(ParameterId.NAME);
        if (node == null) return true;

        boolean negative = node.startsWith("-");
        return player.hasPermission(node) == !negative;
    }
}
