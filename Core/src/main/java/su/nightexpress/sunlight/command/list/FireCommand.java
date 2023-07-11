package su.nightexpress.sunlight.command.list;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.CommandResult;
import su.nexmedia.engine.api.lang.LangMessage;
import su.nexmedia.engine.utils.NumberUtil;
import su.nightexpress.sunlight.Perms;
import su.nightexpress.sunlight.Placeholders;
import su.nightexpress.sunlight.SunLight;
import su.nightexpress.sunlight.command.api.ChangeCommand;
import su.nightexpress.sunlight.config.Lang;

import java.util.Arrays;
import java.util.List;

public class FireCommand extends ChangeCommand {

    public static final String NAME = "fire";

    public FireCommand(@NotNull SunLight plugin, @NotNull String[] aliases) {
        super(plugin, aliases, Perms.COMMAND_FIRE, Perms.COMMAND_FIRE_OTHERS);
        this.setAllowDataLoad();
        this.setDescription(plugin.getMessage(Lang.COMMAND_FIRE_DESC));
        this.setUsage(plugin.getMessage(Lang.COMMAND_FIRE_USAGE));
    }

    @Override
    protected List<String> getTab(@NotNull Player player) {
        return Arrays.asList("0", "100", "200", String.valueOf(20 * 60));
    }

    @Override
    protected void run(@NotNull CommandSender sender, @NotNull CommandResult result, @NotNull Mode mode, @NotNull Player target, double amount) {
        int has = target.getFireTicks();
        int set = mode.modify(has, (int) amount);

        target.setFireTicks(set);
        if (!target.isOnline()) target.saveData();

        LangMessage message = switch (mode) {
            case SET -> plugin.getMessage(Lang.COMMAND_FIRE_SET_TARGET);
            case REMOVE -> plugin.getMessage(Lang.COMMAND_FIRE_REMOVE_TARGET);
            case ADD -> plugin.getMessage(Lang.COMMAND_FIRE_ADD_TARGET);
        };

        int current = target.getFireTicks();
        double seconds = current / 20D;

        message
            .replace(Placeholders.GENERIC_TOTAL, NumberUtil.format(current))
            .replace(Placeholders.GENERIC_AMOUNT, NumberUtil.format(amount))
            .replace(Placeholders.GENERIC_TIME, NumberUtil.format(seconds))
            .replace(Placeholders.forPlayer(target))
            .send(sender);
    }
}
