package su.nightexpress.sunlight.actions.condition.list;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.sunlight.actions.condition.AbstractConditionValidator;
import su.nightexpress.sunlight.actions.condition.ConditionId;
import su.nightexpress.sunlight.actions.parameter.ParameterId;
import su.nightexpress.sunlight.actions.parameter.ParameterResult;
import su.nightexpress.sunlight.actions.parameter.value.ParameterValueNumber;

public class Condition_WorldTime extends AbstractConditionValidator {

    public Condition_WorldTime() {
        super(ConditionId.WORLD_TIME);
        this.registerParameter(ParameterId.AMOUNT);
        this.registerParameter(ParameterId.NAME);
    }

    @Override
    protected boolean validate(@NotNull Player player, @NotNull ParameterResult result) {
        String worldName = (String) result.getValue(ParameterId.NAME);
        World world = worldName != null ? Bukkit.getServer().getWorld(worldName) : null;

        ParameterValueNumber amount = (ParameterValueNumber) result.getValue(ParameterId.AMOUNT);
        if (amount == null) return true;

        long timeRequired = (long) amount.getValue(0);
        ParameterValueNumber.Operator oper = amount.getOperator();

        long timeWorld = world == null ? player.getWorld().getTime() : world.getTime();
        return oper.compare(timeWorld, timeRequired);
    }
}
