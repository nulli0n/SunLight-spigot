package su.nightexpress.sunlight.command.list;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.command.experimental.CommandContext;
import su.nightexpress.nightcore.command.experimental.argument.ArgumentTypes;
import su.nightexpress.nightcore.command.experimental.argument.ParsedArguments;
import su.nightexpress.nightcore.command.experimental.builder.DirectNodeBuilder;
import su.nightexpress.nightcore.command.experimental.node.DirectNode;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.sunlight.Placeholders;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.command.CommandFlags;
import su.nightexpress.sunlight.command.CommandPerms;
import su.nightexpress.sunlight.command.CommandArguments;
import su.nightexpress.sunlight.command.CommandRegistry;
import su.nightexpress.sunlight.command.template.CommandTemplate;
import su.nightexpress.sunlight.command.CommandTools;
import su.nightexpress.sunlight.config.Lang;
import su.nightexpress.sunlight.utils.SunUtils;

import java.util.stream.IntStream;

public class SpeedCommand {

    public static final String NAME = "speed";

    private static final float DEF_SPEED = 0.2F;
    private static final float MAX_SPEED = 1.0F;
    private static final int SPEEDS_AMOUNT = 10;

    public static void load(@NotNull SunLightPlugin plugin) {
        CommandRegistry.registerDirectExecutor(NAME, (template, config) -> builder(plugin, template, config));
        CommandRegistry.addSimpleTemplate(NAME);
    }

    // TODO Per speed permission

    public static DirectNodeBuilder builder(@NotNull SunLightPlugin plugin, @NotNull CommandTemplate template, @NotNull FileConfig config) {
        return DirectNode.builder(plugin, template.getAliases())
            .description(Lang.COMMAND_SPEED_DESC)
            .permission(CommandPerms.SPEED)
            .withArgument(ArgumentTypes.integerAbs(CommandArguments.VALUE).required().withSamples(tabContext -> IntStream.range(1, SPEEDS_AMOUNT + 1).boxed().map(String::valueOf).toList()))
            .withArgument(ArgumentTypes.playerName(CommandArguments.PLAYER).permission(CommandPerms.SPEED_OTHERS))
            .withFlag(CommandFlags.silent().permission(CommandPerms.SPEED_OTHERS))
            .executes((context, arguments) -> execute(plugin, context, arguments))
            ;
    }

    public static boolean execute(@NotNull SunLightPlugin plugin, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Player target = CommandTools.getTarget(plugin, context, arguments, CommandArguments.PLAYER, true);
        if (target == null) return false;

        int speed = SunUtils.clamp(arguments.getIntArgument(CommandArguments.VALUE), 1, SPEEDS_AMOUNT);

        float realSpeed = DEF_SPEED + (MAX_SPEED - DEF_SPEED) * (speed - 1) / (SPEEDS_AMOUNT - 1);

        target.setWalkSpeed(realSpeed);
        if (!target.isOnline()) target.saveData();


        if (context.getSender() != target) {
            Lang.COMMAND_SPEED_DONE_TARGET.getMessage()
                .replace(Placeholders.forPlayer(target))
                .replace(Placeholders.GENERIC_AMOUNT, speed)
                .send(context.getSender());
        }

        if (!arguments.hasFlag(CommandFlags.SILENT)) {
            Lang.COMMAND_SPEED_DONE_NOTIFY.getMessage()
                .replace(Placeholders.forSender(target))
                .replace(Placeholders.GENERIC_AMOUNT, speed)
                .send(target);
        }

        return true;
    }
}
