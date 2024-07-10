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
import su.nightexpress.sunlight.command.*;
import su.nightexpress.sunlight.command.mode.ToggleMode;
import su.nightexpress.sunlight.command.template.CommandTemplate;
import su.nightexpress.sunlight.config.Lang;

public class FlyCommand {

    public static final String NAME = "fly";

    public static void load(@NotNull SunLightPlugin plugin) {
        CommandRegistry.registerDirectExecutor(NAME, (template, config) -> builder(plugin, template, config));
        CommandRegistry.addSimpleTemplate(NAME);
    }

    // TODO Explicit nodes fly_on, fly_off, fly_toggle

    public static DirectNodeBuilder builder(@NotNull SunLightPlugin plugin, @NotNull CommandTemplate template, @NotNull FileConfig config) {
        return DirectNode.builder(plugin, template.getAliases())
            .description(Lang.COMMAND_FLY_DESC)
            .permission(CommandPerms.FLY)
            .withArgument(CommandArguments.toggleMode(CommandArguments.MODE))
            .withArgument(ArgumentTypes.playerName(CommandArguments.PLAYER).permission(CommandPerms.FLY_OTHERS))
            .withFlag(CommandFlags.silent().permission(CommandPerms.FLY_OTHERS))
            .executes((context, arguments) -> execute(plugin, context, arguments))
            ;
    }

    public static boolean execute(@NotNull SunLightPlugin plugin, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Player target = CommandTools.getTarget(plugin, context, arguments, CommandArguments.PLAYER, true);
        if (target == null) return false;

        ToggleMode mode = arguments.getArgument(CommandArguments.MODE, ToggleMode.class, ToggleMode.TOGGLE);
        target.setAllowFlight(mode.apply(target.getAllowFlight()));

        if (context.getSender() != target) {
            Lang.COMMAND_FLY_TARGET.getMessage()
                .replace(Placeholders.forPlayer(target))
                .replace(Placeholders.GENERIC_STATE, Lang.getEnabledOrDisabled(target.getAllowFlight())).send(context.getSender());
        }

        if (!arguments.hasFlag(CommandFlags.SILENT)) {
            Lang.COMMAND_FLY_NOTIFY.getMessage()
                .replace(Placeholders.GENERIC_STATE, Lang.getEnabledOrDisabled(target.getAllowFlight())).send(target);
        }

        return true;
    }
}
