package su.nightexpress.sunlight.actions.action.list;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.sunlight.actions.action.AbstractActionExecutor;
import su.nightexpress.sunlight.actions.action.ActionId;
import su.nightexpress.sunlight.actions.parameter.ParameterId;
import su.nightexpress.sunlight.actions.parameter.ParameterResult;
import su.nightexpress.sunlight.actions.parameter.value.ParameterValueNumber;

public class Action_Burn extends AbstractActionExecutor {

    public Action_Burn() {
        super(ActionId.BURN);
        this.registerParameter(ParameterId.DURATION);
    }

    @Override
    protected void execute(@NotNull Player player, @NotNull ParameterResult result) {
        ParameterValueNumber number = (ParameterValueNumber) result.getValue(ParameterId.DURATION);
        if (number == null) return;

        int duration = (int) number.getValue(0);
        if (duration <= 0) return;

        player.setFireTicks(duration);
    }
}
