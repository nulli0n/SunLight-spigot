package su.nightexpress.sunlight.actions.action.list;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.sunlight.actions.action.AbstractActionExecutor;
import su.nightexpress.sunlight.actions.action.ActionId;
import su.nightexpress.sunlight.actions.parameter.ParameterResult;

public class Action_Lightning extends AbstractActionExecutor {

    public Action_Lightning() {
        super(ActionId.LIGHTNING);
    }

    @Override
    protected void execute(@NotNull Player player, @NotNull ParameterResult result) {
        player.getWorld().strikeLightning(player.getLocation());
    }
}
