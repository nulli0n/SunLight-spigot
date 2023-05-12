package su.nightexpress.sunlight.actions.condition.list;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.hooks.external.VaultHook;
import su.nightexpress.sunlight.actions.condition.AbstractConditionValidator;
import su.nightexpress.sunlight.actions.condition.ConditionId;
import su.nightexpress.sunlight.actions.parameter.ParameterId;
import su.nightexpress.sunlight.actions.parameter.ParameterResult;
import su.nightexpress.sunlight.actions.parameter.value.ParameterValueNumber;

public class Condition_EconomyBalance extends AbstractConditionValidator {

    public Condition_EconomyBalance() {
        super(ConditionId.ECONOMY_BALANCE);
        this.registerParameter(ParameterId.AMOUNT);
    }

    @Override
    protected boolean validate(@NotNull Player player, @NotNull ParameterResult result) {
        if (!VaultHook.hasEconomy()) return true;

        ParameterValueNumber amount = (ParameterValueNumber) result.getValue(ParameterId.AMOUNT);
        if (amount == null) return true;

        double moneyRequired = amount.getValue(0D);
        if (moneyRequired <= 0D) return true;

        ParameterValueNumber.Operator operator = amount.getOperator();

        double moneyBalance = VaultHook.getBalance(player);
        return operator.compare(moneyBalance, moneyRequired);
    }
}
