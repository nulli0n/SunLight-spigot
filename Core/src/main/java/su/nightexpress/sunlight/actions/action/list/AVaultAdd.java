package su.nightexpress.sunlight.actions.action.list;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.hooks.external.VaultHook;
import su.nightexpress.sunlight.actions.action.AbstractActionExecutor;
import su.nightexpress.sunlight.actions.parameter.ParameterId;
import su.nightexpress.sunlight.actions.parameter.ParameterResult;
import su.nightexpress.sunlight.actions.parameter.value.ParameterValueNumber;

public class AVaultAdd extends AbstractActionExecutor {

    public AVaultAdd() {
        super("VAULT_ADD");

        this.registerParameter(ParameterId.AMOUNT);
    }

    @Override
    protected void execute(@NotNull Player player, @NotNull ParameterResult result) {
        ParameterValueNumber number = (ParameterValueNumber) result.getValue(ParameterId.AMOUNT);
        if (number == null) return;

        double amount = number.getValue(0);
        if (amount == 0) return;

        VaultHook.addMoney(player, amount);
    }
}
