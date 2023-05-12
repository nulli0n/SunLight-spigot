package su.nightexpress.sunlight.actions.condition.list;

import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.utils.EntityUtil;
import su.nightexpress.sunlight.actions.condition.AbstractConditionValidator;
import su.nightexpress.sunlight.actions.condition.ConditionId;
import su.nightexpress.sunlight.actions.parameter.ParameterId;
import su.nightexpress.sunlight.actions.parameter.ParameterResult;
import su.nightexpress.sunlight.actions.parameter.value.ParameterValueNumber;

public class Condition_Health extends AbstractConditionValidator {

    public Condition_Health() {
        super(ConditionId.HEALTH);
        this.registerParameter(ParameterId.AMOUNT);
    }

    @Override
    protected boolean validate(@NotNull Player player, @NotNull ParameterResult result) {
        ParameterValueNumber amount = (ParameterValueNumber) result.getValue(ParameterId.AMOUNT);
        if (amount == null) return true;

        double healthRequired = amount.getValue(0D);
        boolean isPercent = amount.isPercent();
        ParameterValueNumber.Operator operator = amount.getOperator();


        double healthTarget = player.getHealth();
        double healthTargetMax = EntityUtil.getAttribute(player, Attribute.GENERIC_MAX_HEALTH);

        if (isPercent) {
            healthTarget = healthTarget / healthTargetMax * 100D;
        }

        return operator.compare(healthTarget, healthRequired);
    }
}
