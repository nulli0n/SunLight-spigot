package su.nightexpress.sunlight.command.list;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.CommandResult;
import su.nightexpress.sunlight.Perms;
import su.nightexpress.sunlight.Placeholders;
import su.nightexpress.sunlight.SunLight;
import su.nightexpress.sunlight.command.CommandFlags;
import su.nightexpress.sunlight.command.api.TargetCommand;
import su.nightexpress.sunlight.config.Lang;

import java.util.List;
import java.util.stream.IntStream;

public class FlySpeedCommand extends TargetCommand {

    public static final String NAME = "flyspeed";

    private static final float DEF_SPEED = 0.1F;
    private static final float MAX_SPEED = 1.0F;
    private static final int SPEEDS_AMOUNT = 10;

    public FlySpeedCommand(@NotNull SunLight plugin, @NotNull String[] aliases) {
        super(plugin, aliases, Perms.COMMAND_FLY_SPEED, Perms.COMMAND_FLY_SPEED_OTHERS, 1);
        this.setDescription(plugin.getMessage(Lang.COMMAND_FLY_SPEED_DESC));
        this.setUsage(plugin.getMessage(Lang.COMMAND_FLY_SPEED_USAGE));
        this.addFlag(CommandFlags.SILENT);
    }

    @Override
    @NotNull
    public List<String> getTab(@NotNull Player player, int arg, @NotNull String[] args) {
        if (arg == 1) {
            return IntStream.range(0, SPEEDS_AMOUNT + 1).boxed().map(String::valueOf).toList();
        }
        return super.getTab(player, arg, args);
    }

    @Override
    protected void onExecute(@NotNull CommandSender sender, @NotNull CommandResult result) {
        Player target = this.getCommandTarget(sender, result);
        if (target == null) return;

        int speed = Math.max(0, result.getInt(0, 1));
        if (speed > SPEEDS_AMOUNT) speed = SPEEDS_AMOUNT;
        speed -= 1;

        float realSpeed = speed < 0 ? DEF_SPEED : DEF_SPEED + (MAX_SPEED - DEF_SPEED) * speed / (SPEEDS_AMOUNT - 1);

        target.setFlySpeed(realSpeed);

        if (!result.hasFlag(CommandFlags.SILENT)) {
            plugin.getMessage(Lang.COMMAND_FLY_SPEED_DONE_NOTIFY)
                .replace(Placeholders.forSender(sender))
                .replace(Placeholders.GENERIC_AMOUNT, speed + 1)
                .send(target);
        }
        if (sender != target) {
            plugin.getMessage(Lang.COMMAND_FLY_SPEED_DONE_TARGET)
                .replace(Placeholders.forPlayer(target))
                .replace(Placeholders.GENERIC_AMOUNT, speed + 1)
                .send(sender);
        }
    }
}
