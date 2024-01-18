package su.nightexpress.sunlight.actions.action.list;

import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.utils.EntityUtil;
import su.nightexpress.sunlight.actions.action.AbstractActionExecutor;
import su.nightexpress.sunlight.actions.action.ActionId;
import su.nightexpress.sunlight.actions.parameter.ParameterId;
import su.nightexpress.sunlight.actions.parameter.ParameterResult;
import su.nightexpress.sunlight.actions.parameter.value.ParameterValueNumber;

public class Action_Health extends AbstractActionExecutor {

    public Action_Health() {
        super(ActionId.HEALTH);
        this.registerParameter(ParameterId.AMOUNT);
    }

    @Override
    protected void execute(@NotNull Player player, @NotNull ParameterResult result) {
        ParameterValueNumber value = (ParameterValueNumber) result.getValue(ParameterId.AMOUNT);
        if (value == null) return;

        double hp = value.getValue(0);
        if (hp == 0) return;

        boolean percent = value.isPercent();

        double hp2 = hp;
        double maxHp = EntityUtil.getAttribute(player, Attribute.GENERIC_MAX_HEALTH);
        if (percent) {
            hp2 = maxHp * (hp / 100D);
        }

        player.setHealth(Math.min(player.getHealth() + hp2, maxHp));
    }
}
