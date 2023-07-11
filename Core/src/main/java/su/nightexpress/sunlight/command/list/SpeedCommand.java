package su.nightexpress.sunlight.command.list;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.CommandFlag;
import su.nexmedia.engine.api.command.CommandResult;
import su.nexmedia.engine.api.lang.LangMessage;
import su.nightexpress.sunlight.Perms;
import su.nightexpress.sunlight.Placeholders;
import su.nightexpress.sunlight.SunLight;
import su.nightexpress.sunlight.command.CommandFlags;
import su.nightexpress.sunlight.command.api.TargetCommand;
import su.nightexpress.sunlight.config.Lang;

import java.util.List;
import java.util.stream.IntStream;

public class SpeedCommand extends TargetCommand {

    public static final String NAME = "speed";

    private static final CommandFlag<Boolean> FLAG_FLY = CommandFlag.booleanFlag("fly");

    public SpeedCommand(@NotNull SunLight plugin, @NotNull String[] aliases) {
        super(plugin, aliases, Perms.COMMAND_SPEED, Perms.COMMAND_SPEED_OTHERS, 1);
        this.setDescription(plugin.getMessage(Lang.COMMAND_SPEED_DESC));
        this.setUsage(plugin.getMessage(Lang.COMMAND_SPEED_USAGE));
        this.addFlag(CommandFlags.SILENT, FLAG_FLY);
    }

    @Override
    @NotNull
    public List<String> getTab(@NotNull Player player, int arg, @NotNull String[] args) {
        if (arg == 1) {
            return IntStream.range(0, 11).boxed().map(String::valueOf).toList();
        }
        return super.getTab(player, arg, args);
    }

    @Override
    protected void onExecute(@NotNull CommandSender sender, @NotNull CommandResult result) {
        Player target = this.getCommandTarget(sender, result);
        if (target == null) return;

        boolean isFly = result.hasFlag(FLAG_FLY);

        int speed = result.getInt(0, 1);
        if (speed > 10) speed = 10;
        else if (speed < 0) speed = 0;

        float defSpeed = isFly || speed > 1 ? 0.1F : 0.2F;
        float realSpeed = (float) speed * defSpeed;

        LangMessage msgNotify = plugin.getMessage(isFly ? Lang.COMMAND_SPEED_DONE_FLY_NOTIFY : Lang.COMMAND_SPEED_DONE_WALK_NOTIFY);
        LangMessage msgTarget = plugin.getMessage(isFly ? Lang.COMMAND_SPEED_DONE_FLY_TARGET : Lang.COMMAND_SPEED_DONE_WALK_TARGET);

        if (isFly) {
            target.setFlySpeed(realSpeed);
        }
        else {
            target.setWalkSpeed(realSpeed);
        }

        if (!result.hasFlag(CommandFlags.SILENT)) {
            msgNotify.replace(Placeholders.forSender(sender)).replace(Placeholders.GENERIC_AMOUNT, speed).send(target);
        }
        if (sender != target) {
            msgTarget.replace(Placeholders.forPlayer(target)).replace(Placeholders.GENERIC_AMOUNT, speed).send(sender);
        }
    }
}
