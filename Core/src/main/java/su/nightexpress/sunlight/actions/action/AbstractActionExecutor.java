package su.nightexpress.sunlight.actions.action;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.sunlight.actions.ActionManipulator;
import su.nightexpress.sunlight.actions.parameter.AbstractParametized;
import su.nightexpress.sunlight.actions.parameter.ParameterId;
import su.nightexpress.sunlight.actions.parameter.ParameterResult;
import su.nightexpress.sunlight.actions.parameter.value.ParameterValueNumber;

public abstract class AbstractActionExecutor extends AbstractParametized {

    public AbstractActionExecutor(@NotNull String key) {
        super(key);
        this.registerParameter(ParameterId.DELAY);
    }

    protected abstract void execute(@NotNull Player player, @NotNull ParameterResult result);

    public final void process(@NotNull Player player, @NotNull String str, @NotNull ActionManipulator manipulator) {
        ParameterResult result = this.getParameterResult(str);
        ParameterValueNumber numberDelay = (ParameterValueNumber) result.getValue(ParameterId.DELAY);
        int delay = numberDelay == null ? 0 : (int) numberDelay.getValue(0);

        if (delay > 0) {
            String strDelay = str.replace(ParameterId.DELAY, "nodelay");
            ENGINE.getServer().getScheduler().runTaskLater(ENGINE, () -> {
                this.process(player, strDelay, manipulator);}, delay);
            return;
        }

        if (this.getName().equalsIgnoreCase(ActionId.GOTO)) {
            String sectionId = (String) result.getValue(ParameterId.NAME);
            if (sectionId == null) return;

            manipulator.process(player, sectionId);
            return;
        }

        this.execute(player, result);
    }
}
