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

public class AnvilCommand {

    public static final String NAME = "anvil";

    public static void load(@NotNull SunLightPlugin plugin) {
        CommandRegistry.registerDirectExecutor(NAME, (template, config) -> builderRoot(plugin, template, config));
        CommandRegistry.addSimpleTemplate(NAME);
    }

    @NotNull
    public static DirectNodeBuilder builderRoot(@NotNull SunLightPlugin plugin, @NotNull CommandTemplate template, @NotNull FileConfig config) {
        return DirectNode.builder(plugin, template.getAliases())
            .description(Lang.COMMAND_ANVIL_DESC)
            .permission(CommandPerms.ANVIL)
            .withArgument(ArgumentTypes.playerName(CommandArguments.PLAYER).permission(CommandPerms.ANVIL_OTHERS))
            .withFlag(CommandFlags.silent().permission(CommandPerms.ANVIL_OTHERS))
            .executes((context, arguments) -> execute(plugin, context, arguments));
    }

    public static boolean execute(@NotNull SunLightPlugin plugin, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Player target = CommandTools.getTarget(plugin, context, arguments, CommandArguments.PLAYER, false);
        if (target == null) return false;

        plugin.getSunNMS().openAnvil(target);

        if (target != context.getSender()) {
            Lang.COMMAND_ANVIL_TARGET.getMessage().replace(Placeholders.forPlayer(target)).send(context.getSender());
        }
        if (!arguments.hasFlag(CommandFlags.SILENT)) {
            Lang.COMMAND_ANVIL_NOTIFY.getMessage().send(target);
        }

        return true;
    }
}
