package su.nightexpress.sunlight.module.afk.command;

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
import su.nightexpress.sunlight.command.CommandArguments;
import su.nightexpress.sunlight.command.CommandRegistry;
import su.nightexpress.sunlight.command.CommandTools;
import su.nightexpress.sunlight.command.mode.ToggleMode;
import su.nightexpress.sunlight.command.template.CommandTemplate;
import su.nightexpress.sunlight.module.afk.AfkModule;
import su.nightexpress.sunlight.module.afk.config.AfkLang;
import su.nightexpress.sunlight.module.afk.config.AfkPerms;

public class AfkCommand {

    public static final String NAME = "afk";

    public static void load(@NotNull SunLightPlugin plugin, @NotNull AfkModule module) {
        CommandRegistry.registerDirectExecutor(NAME, (template, config) -> builder(plugin, module, template, config));
        CommandRegistry.addSimpleTemplate(NAME);
    }

    public static DirectNodeBuilder builder(@NotNull SunLightPlugin plugin, @NotNull AfkModule module, @NotNull CommandTemplate template, @NotNull FileConfig config) {
        return DirectNode.builder(plugin, template.getAliases())
            .description(AfkLang.COMMAND_AFK_DESC)
            .permission(AfkPerms.COMMAND_AFK)
            .withArgument(CommandArguments.toggleMode(CommandArguments.MODE))
            .withArgument(ArgumentTypes.playerName(CommandArguments.PLAYER).permission(AfkPerms.COMMAND_AFK_OTHERS))
            //.withFlag(CommandFlags.silent().permission(AfkPerms.COMMAND_AFK_OTHERS))
            .executes((context, arguments) -> execute(plugin, module, context, arguments))
            ;
    }

    public static boolean execute(@NotNull SunLightPlugin plugin, @NotNull AfkModule module, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Player target = CommandTools.getTarget(plugin, context, arguments, CommandArguments.PLAYER, true);
        if (target == null) return false;

        ToggleMode mode = arguments.getArgument(CommandArguments.MODE, ToggleMode.class, ToggleMode.TOGGLE);
        boolean state = mode.apply(module.isAfk(target));

        if (state) {
            module.enterAfk(target);
        }
        else {
            module.exitAfk(target);
        }

        if (context.getSender() != target) {
            AfkLang.COMMAND_AFK_DONE_OTHERS.getMessage()
                .replace(Placeholders.forPlayer(target))
                .send(context.getSender());
        }

        return true;
    }
}
