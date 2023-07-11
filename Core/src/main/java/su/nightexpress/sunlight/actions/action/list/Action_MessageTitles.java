package su.nightexpress.sunlight.actions.action.list;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.utils.Placeholders;
import su.nightexpress.sunlight.actions.action.AbstractActionExecutor;
import su.nightexpress.sunlight.actions.action.ActionId;
import su.nightexpress.sunlight.actions.parameter.ParameterId;
import su.nightexpress.sunlight.actions.parameter.ParameterResult;
import su.nightexpress.sunlight.actions.parameter.value.ParameterValueNumber;

public class Action_MessageTitles extends AbstractActionExecutor {

    public Action_MessageTitles() {
        super(ActionId.MESSAGE_TITLES);
        this.registerParameter(ParameterId.TITLES_TITLE);
        this.registerParameter(ParameterId.TITLES_SUBTITLE);
        this.registerParameter(ParameterId.TITLES_FADE_IN);
        this.registerParameter(ParameterId.TITLES_FADE_OUT);
        this.registerParameter(ParameterId.TITLES_STAY);
    }

    @Override
    protected void execute(@NotNull Player player, @NotNull ParameterResult result) {
        String title = (String) result.getValue(ParameterId.TITLES_TITLE);
        String subtitle = (String) result.getValue(ParameterId.TITLES_SUBTITLE);
        if (title == null && subtitle == null) return;

        ParameterValueNumber numberIn = (ParameterValueNumber) result.getValue(ParameterId.TITLES_FADE_IN);
        ParameterValueNumber numberStay = (ParameterValueNumber) result.getValue(ParameterId.TITLES_STAY);
        ParameterValueNumber numberOut = (ParameterValueNumber) result.getValue(ParameterId.TITLES_FADE_OUT);
        if (numberIn == null) return;
        if (numberStay == null) return;
        if (numberOut == null) return;

        int fadeIn = (int) numberIn.getValue(0);
        int stay = (int) numberStay.getValue(0);
        int fadeOut = (int) numberOut.getValue(0);

        String title2 = title == null ? "" : Placeholders.forPlayer(player).apply(title);
        String subtitle2 = subtitle == null ? "" : Placeholders.forPlayer(player).apply(subtitle);

        player.sendTitle(title2, subtitle2, fadeIn, stay, fadeOut);
    }
}
