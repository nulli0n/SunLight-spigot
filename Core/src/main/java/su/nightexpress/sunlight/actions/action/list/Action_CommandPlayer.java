package su.nightexpress.sunlight.actions.action.list;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.utils.Placeholders;
import su.nightexpress.sunlight.actions.action.AbstractActionExecutor;
import su.nightexpress.sunlight.actions.action.ActionId;
import su.nightexpress.sunlight.actions.parameter.ParameterId;
import su.nightexpress.sunlight.actions.parameter.ParameterResult;

public class Action_CommandPlayer extends AbstractActionExecutor {

    public Action_CommandPlayer() {
        super(ActionId.COMMAND_PLAYER);
        this.registerParameter(ParameterId.MESSAGE);
    }

    @Override
    protected void execute(@NotNull Player player, @NotNull ParameterResult result) {
        String text = (String) result.getValue(ParameterId.MESSAGE);
        if (text == null) return;

        text = Placeholders.forPlayer(player).apply(text);
        player.performCommand(text);
    }
}
