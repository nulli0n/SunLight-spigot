package su.nightexpress.sunlight.actions.action.list;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.utils.EntityUtil;
import su.nightexpress.sunlight.actions.action.AbstractActionExecutor;
import su.nightexpress.sunlight.actions.action.ActionId;
import su.nightexpress.sunlight.actions.parameter.ParameterResult;

public class Action_Firework extends AbstractActionExecutor {

    public Action_Firework() {
        super(ActionId.FIREWORK);
    }

    @Override
    protected void execute(@NotNull Player player, @NotNull ParameterResult result) {
        EntityUtil.spawnRandomFirework(player.getLocation());
    }
}
