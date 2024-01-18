package su.nightexpress.sunlight.actions.action.list;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.sunlight.actions.action.AbstractActionExecutor;
import su.nightexpress.sunlight.actions.action.ActionId;
import su.nightexpress.sunlight.actions.parameter.ParameterId;
import su.nightexpress.sunlight.actions.parameter.ParameterResult;
import su.nightexpress.sunlight.actions.parameter.value.ParameterValueNumber;

public class Action_Potion extends AbstractActionExecutor {

    public Action_Potion() {
        super(ActionId.POTION);
        this.registerParameter(ParameterId.NAME);
        this.registerParameter(ParameterId.DURATION);
        this.registerParameter(ParameterId.AMOUNT);
    }

    @Override
    protected void execute(@NotNull Player player, @NotNull ParameterResult result) {
        String name = (String) result.getValue(ParameterId.NAME);
        if (name == null) return;

        PotionEffectType effectType = PotionEffectType.getByName(name.toUpperCase());
        if (effectType == null) return;

        ParameterValueNumber numberDuration = (ParameterValueNumber) result.getValue(ParameterId.DURATION);
        if (numberDuration == null) return;

        int duration = (int) numberDuration.getValue(0);
        if (duration <= 0) return;

        ParameterValueNumber numberAmp = (ParameterValueNumber) result.getValue(ParameterId.AMOUNT);
        if (numberAmp == null) return;

        int amplifier = (int) Math.max(0, numberAmp.getValue(0) - 1);
        PotionEffect effect = new PotionEffect(effectType, duration, amplifier);

        player.addPotionEffect(effect);
    }
}
