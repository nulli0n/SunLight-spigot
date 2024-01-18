package su.nightexpress.sunlight.actions.action.list;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.utils.EngineUtils;
import su.nexmedia.engine.utils.Placeholders;
import su.nexmedia.engine.utils.PlayerUtil;
import su.nightexpress.sunlight.actions.action.AbstractActionExecutor;
import su.nightexpress.sunlight.actions.action.ActionId;
import su.nightexpress.sunlight.actions.parameter.ParameterId;
import su.nightexpress.sunlight.actions.parameter.ParameterResult;

public class Action_MessageActionBar extends AbstractActionExecutor {

    public Action_MessageActionBar() {
        super(ActionId.MESSAGE_ACTION_BAR);
        this.registerParameter(ParameterId.MESSAGE);
    }

    @Override
    protected void execute(@NotNull Player player, @NotNull ParameterResult result) {
        String text = (String) result.getValue(ParameterId.MESSAGE);
        if (text == null) return;

        text = Placeholders.forPlayer(player).apply(text);
        if (EngineUtils.hasPlaceholderAPI()) {
            text = PlaceholderAPI.setPlaceholders(player, text);
        }
        PlayerUtil.sendActionBar(player, text);
    }
}
