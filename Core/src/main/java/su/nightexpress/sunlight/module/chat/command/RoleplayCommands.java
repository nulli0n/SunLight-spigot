package su.nightexpress.sunlight.module.chat.command;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.command.experimental.CommandContext;
import su.nightexpress.nightcore.command.experimental.argument.ArgumentTypes;
import su.nightexpress.nightcore.command.experimental.argument.ParsedArguments;
import su.nightexpress.nightcore.command.experimental.builder.DirectNodeBuilder;
import su.nightexpress.nightcore.command.experimental.node.DirectNode;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.util.Players;
import su.nightexpress.sunlight.Placeholders;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.command.CommandArguments;
import su.nightexpress.sunlight.command.CommandRegistry;
import su.nightexpress.sunlight.command.template.CommandTemplate;
import su.nightexpress.sunlight.module.chat.config.ChatConfig;
import su.nightexpress.sunlight.module.chat.config.ChatLang;
import su.nightexpress.sunlight.module.chat.config.ChatPerms;

public class RoleplayCommands {

    public static final String NODE_ME = "me";

    public static void load(@NotNull SunLightPlugin plugin) {
        CommandRegistry.registerDirectExecutor(NODE_ME, (template, config) -> builderMe(plugin, template, config));

        CommandRegistry.addSimpleTemplate(NODE_ME);
    }

    @NotNull
    public static DirectNodeBuilder builderMe(@NotNull SunLightPlugin plugin, @NotNull CommandTemplate template, @NotNull FileConfig config) {
        return DirectNode.builder(plugin, template.getAliases())
            .playerOnly()
            .description(ChatLang.COMMAND_ME_DESC)
            .permission(ChatPerms.COMMAND_ME)
            .withArgument(ArgumentTypes.string(CommandArguments.TEXT).required().complex().localized(ChatLang.COMMAND_ARGUMENT_NAME_TEXT))
            .executes((context, arguments) -> executeMe(plugin, context, arguments))
            ;
    }

    public static boolean executeMe(@NotNull SunLightPlugin plugin, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Player player = context.getPlayerOrThrow();
        String action = arguments.getStringArgument(CommandArguments.TEXT);
        String format = Placeholders.forPlayer(player).apply(ChatConfig.ROLEPLAY_COMMANDS_ME_FORMAT.get().replace(Placeholders.GENERIC_MESSAGE, action));

        plugin.getServer().getOnlinePlayers().forEach(other -> Players.sendModernMessage(other, format));
        return true;
    }
}
