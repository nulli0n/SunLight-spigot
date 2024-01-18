package su.nightexpress.sunlight.actions.action.list;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.utils.Placeholders;
import su.nexmedia.engine.utils.PlayerUtil;
import su.nightexpress.sunlight.actions.action.AbstractActionExecutor;
import su.nightexpress.sunlight.actions.action.ActionId;
import su.nightexpress.sunlight.actions.parameter.ParameterId;
import su.nightexpress.sunlight.actions.parameter.ParameterResult;

public class Action_MessageChat extends AbstractActionExecutor {

    public Action_MessageChat() {
        super(ActionId.MESSAGE_CHAT);
        this.registerParameter(ParameterId.MESSAGE);
    }

    @Override
    protected void execute(@NotNull Player player, @NotNull ParameterResult result) {
        String text = (String) result.getValue(ParameterId.MESSAGE);
        if (text == null) return;

        text = Placeholders.forPlayer(player).apply(text);

        PlayerUtil.sendRichMessage(player, text);
    }
}
