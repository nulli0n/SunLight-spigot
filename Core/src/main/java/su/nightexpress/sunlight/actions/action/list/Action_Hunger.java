package su.nightexpress.sunlight.actions.action.list;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.sunlight.actions.action.AbstractActionExecutor;
import su.nightexpress.sunlight.actions.action.ActionId;
import su.nightexpress.sunlight.actions.parameter.ParameterId;
import su.nightexpress.sunlight.actions.parameter.ParameterResult;
import su.nightexpress.sunlight.actions.parameter.value.ParameterValueNumber;

public class Action_Hunger extends AbstractActionExecutor {

    public Action_Hunger() {
        super(ActionId.HUNGER);
        this.registerParameter(ParameterId.AMOUNT);
    }

    @Override
    protected void execute(@NotNull Player player, @NotNull ParameterResult result) {
        ParameterValueNumber value = (ParameterValueNumber) result.getValue(ParameterId.AMOUNT);
        if (value == null) return;

        double amount = value.getValue(0);
        if (amount == 0) return;

        boolean percent = value.isPercent();

        double amount2 = amount;
        double max = 20;
        if (percent) {
            amount2 = max * (amount / 100D);
        }

        player.setFoodLevel((int) Math.min(player.getFoodLevel() + amount2, max));
    }
}
