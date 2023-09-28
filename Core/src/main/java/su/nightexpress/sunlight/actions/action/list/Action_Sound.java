package su.nightexpress.sunlight.actions.action.list;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.utils.PlayerUtil;
import su.nexmedia.engine.utils.StringUtil;
import su.nightexpress.sunlight.actions.action.AbstractActionExecutor;
import su.nightexpress.sunlight.actions.action.ActionId;
import su.nightexpress.sunlight.actions.parameter.ParameterId;
import su.nightexpress.sunlight.actions.parameter.ParameterResult;

public class Action_Sound extends AbstractActionExecutor {

    public Action_Sound() {
        super(ActionId.SOUND);
        this.registerParameter(ParameterId.NAME);
    }

    @Override
    protected void execute(@NotNull Player player, @NotNull ParameterResult result) {
        String name = (String) result.getValue(ParameterId.NAME);
        if (name == null) return;

        Sound sound = StringUtil.getEnum(name, Sound.class).orElse(null);
        if (sound == null) return;

        //PlayerUtil.sound(player, sound);
    }
}
