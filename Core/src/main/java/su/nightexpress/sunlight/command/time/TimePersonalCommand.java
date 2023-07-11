package su.nightexpress.sunlight.command.time;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.CommandFlag;
import su.nexmedia.engine.api.command.CommandResult;
import su.nexmedia.engine.utils.NumberUtil;
import su.nightexpress.sunlight.Perms;
import su.nightexpress.sunlight.Placeholders;
import su.nightexpress.sunlight.SunLight;
import su.nightexpress.sunlight.command.CommandFlags;
import su.nightexpress.sunlight.command.api.TargetCommand;
import su.nightexpress.sunlight.config.Config;
import su.nightexpress.sunlight.config.Lang;
import su.nightexpress.sunlight.utils.SunUtils;

import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

public class TimePersonalCommand extends TargetCommand {

    public static final String NAME = "personal";

    private static final CommandFlag<Boolean> FLAG_RESET = CommandFlag.booleanFlag("r");

    public TimePersonalCommand(@NotNull SunLight plugin) {
        super(plugin, new String[]{NAME}, Perms.COMMAND_TIME_PERSONAL, Perms.COMMAND_TIME_PERSONAL_OTHERS, 2);
        this.setDescription(plugin.getMessage(Lang.COMMAND_TIME_PERSONAL_DESC));
        this.setUsage(plugin.getMessage(Lang.COMMAND_TIME_PERSONAL_USAGE));
        this.addFlag(CommandFlags.SILENT, FLAG_RESET);
    }

    @Override
    @NotNull
    public List<String> getTab(@NotNull Player player, int arg, @NotNull String[] args) {
        if (arg == 1) {
            return Arrays.asList("0", "6000", "12000", "18000", "24000");
        }
        return super.getTab(player, arg, args);
    }

    @Override
    protected void onExecute(@NotNull CommandSender sender, @NotNull CommandResult result) {
        if (result.length() < 2) {
            this.printUsage(sender);
            return;
        }

        Player target = this.getCommandTarget(sender, result);
        if (target == null) return;

        if (result.hasFlag(FLAG_RESET)) {
            target.resetPlayerTime();
        }
        else {
            long ticks = Math.min(24000, Math.max(0, result.getInt(1, 0)));
            target.setPlayerTime(ticks, false);
        }
        long ticks = target.getPlayerTime() % 24000L;
        LocalTime time = SunUtils.getTimeOfTicks(ticks);

        if (sender != target) {
            plugin.getMessage(Lang.COMMAND_TIME_PERSONAL_TARGET)
                .replace(Placeholders.forPlayer(target))
                .replace(Placeholders.GENERIC_TIME, time.format(Config.GENERAL_TIME_FORMAT.get()))
                .replace(Placeholders.GENERIC_TOTAL, NumberUtil.format(ticks))
                .send(sender);
        }
        if (!result.hasFlag(CommandFlags.SILENT)) {
            plugin.getMessage(Lang.COMMAND_TIME_PERSONAL_NOTIFY)
                .replace(Placeholders.GENERIC_TIME, time.format(Config.GENERAL_TIME_FORMAT.get()))
                .replace(Placeholders.GENERIC_TOTAL, NumberUtil.format(ticks))
                .send(sender);
        }
    }
}
