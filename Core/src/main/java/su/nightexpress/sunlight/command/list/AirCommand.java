package su.nightexpress.sunlight.command.list;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.CommandFlag;
import su.nexmedia.engine.api.command.CommandResult;
import su.nexmedia.engine.api.lang.LangMessage;
import su.nexmedia.engine.utils.NumberUtil;
import su.nightexpress.sunlight.Perms;
import su.nightexpress.sunlight.Placeholders;
import su.nightexpress.sunlight.SunLight;
import su.nightexpress.sunlight.command.CommandFlags;
import su.nightexpress.sunlight.command.api.ChangeCommand;
import su.nightexpress.sunlight.config.Lang;

import java.util.Arrays;
import java.util.List;

public class AirCommand extends ChangeCommand {

    public static final String NAME = "air";

    private static final CommandFlag<Boolean> FLAG_MAX = CommandFlag.booleanFlag("max");

    public AirCommand(@NotNull SunLight plugin, @NotNull String[] aliases) {
        super(plugin, aliases, Perms.COMMAND_AIR, Perms.COMMAND_AIR_OTHERS);
        this.setDescription(plugin.getMessage(Lang.COMMAND_AIR_DESC));
        this.setUsage(plugin.getMessage(Lang.COMMAND_AIR_USAGE));
        this.addFlag(FLAG_MAX, CommandFlags.SILENT);
    }

    @Override
    protected List<String> getTab(@NotNull Player player) {
        return Arrays.asList("50", "100", "300", "600", "900");
    }

    @Override
    protected void run(@NotNull CommandSender sender, @NotNull CommandResult result, @NotNull Mode mode, @NotNull Player target, double amount) {
        boolean isMax = result.hasFlag(FLAG_MAX);
        int has = isMax ? target.getMaximumAir() : target.getRemainingAir();
        int set = switch (mode) {
            case REMOVE -> Math.max(0, has - (int) amount);
            case SET -> (int) amount;
            case ADD -> has + (int) amount;
        };

        if (isMax) target.setMaximumAir(set);
        else target.setRemainingAir(set);

        LangMessage message = switch (mode) {
            case SET -> plugin.getMessage(Lang.COMMAND_AIR_SET_TARGET);
            case REMOVE -> plugin.getMessage(Lang.COMMAND_AIR_REMOVE_TARGET);
            case ADD -> plugin.getMessage(Lang.COMMAND_AIR_ADD_TARGET);
        };

        LangMessage notify = switch (mode) {
            case ADD -> plugin.getMessage(Lang.COMMAND_AIR_ADD_NOTIFY);
            case SET -> plugin.getMessage(Lang.COMMAND_AIR_SET_NOTIFY);
            case REMOVE -> plugin.getMessage(Lang.COMMAND_AIR_REMOVE_NOTIFY);
        };

        int current = target.getRemainingAir();
        int max = target.getMaximumAir();

        if (target != sender) {
            message
                .replace(Placeholders.GENERIC_CURRENT, String.valueOf(current))
                .replace(Placeholders.GENERIC_MAX, String.valueOf(max))
                .replace(Placeholders.GENERIC_AMOUNT, NumberUtil.format(amount))
                .replace(Placeholders.forPlayer(target))
                .send(sender);
        }
        if (!result.hasFlag(CommandFlags.SILENT)) {
            notify
                .replace(Placeholders.GENERIC_CURRENT, String.valueOf(current))
                .replace(Placeholders.GENERIC_MAX, String.valueOf(max))
                .replace(Placeholders.GENERIC_AMOUNT, NumberUtil.format(amount))
                .replace(Placeholders.forPlayer(target))
                .send(target);
        }
    }
}
