package su.nightexpress.sunlight.command.list;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.command.experimental.CommandContext;
import su.nightexpress.nightcore.command.experimental.argument.ArgumentTypes;
import su.nightexpress.nightcore.command.experimental.argument.ParsedArguments;
import su.nightexpress.nightcore.command.experimental.builder.DirectNodeBuilder;
import su.nightexpress.nightcore.command.experimental.node.DirectNode;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.language.message.LangMessage;
import su.nightexpress.sunlight.Placeholders;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.command.CommandArguments;
import su.nightexpress.sunlight.command.CommandPerms;
import su.nightexpress.sunlight.command.CommandRegistry;
import su.nightexpress.sunlight.command.template.CommandTemplate;
import su.nightexpress.sunlight.config.Lang;

public class SudoCommand {

    public static final String NODE_CHAT    = "sudo_chat";
    public static final String NODE_COMMAND = "sudo_command";

    public static void load(@NotNull SunLightPlugin plugin) {
        CommandRegistry.registerDirectExecutor(NODE_CHAT, (template, config) -> builder(plugin, template, config, Type.CHAT)
            .description(Lang.COMMAND_SUDO_CHAT_DESC)
            .permission(CommandPerms.SUDO_CHAT)
        );

        CommandRegistry.registerDirectExecutor(NODE_COMMAND, (template, config) -> builder(plugin, template, config, Type.COMMAND)
            .description(Lang.COMMAND_SUDO_COMMAND_DESC)
            .permission(CommandPerms.SUDO_COMMAND)
        );

        CommandRegistry.addTemplate("sudo", CommandTemplate.group(new String[]{"sudo"},
            "Sudo commands.",
            CommandPerms.PREFIX + "sudo",
            CommandTemplate.direct(new String[]{"chat"}, NODE_CHAT),
            CommandTemplate.direct(new String[]{"command"}, NODE_COMMAND)
        ));
    }

    public enum Type {COMMAND, CHAT}

    @NotNull
    public static DirectNodeBuilder builder(@NotNull SunLightPlugin plugin, @NotNull CommandTemplate template, @NotNull FileConfig config, @NotNull Type type) {
        return DirectNode.builder(plugin, template.getAliases())
            .withArgument(ArgumentTypes.player(CommandArguments.PLAYER).required())
            .withArgument(ArgumentTypes.string(CommandArguments.TEXT).required().complex().localized(Lang.COMMAND_ARGUMENT_NAME_TEXT))
            .executes((context, arguments) -> execute(plugin, context, arguments, type))
            ;
    }

    public static boolean execute(@NotNull SunLightPlugin plugin, @NotNull CommandContext context, @NotNull ParsedArguments arguments, @NotNull Type type) {
        Player target = arguments.getPlayerArgument(CommandArguments.PLAYER);
        if (context.getSender() == target) {
            context.send(Lang.ERROR_COMMAND_NOT_YOURSELF.getMessage());
            return false;
        }

        if (target.hasPermission(CommandPerms.SUDO_BYPASS)) {
            context.errorPermission();
            return false;
        }

        String command = arguments.getStringArgument(CommandArguments.TEXT);
        LangMessage message;
        if (type == Type.CHAT) {
            target.chat(command);
            message = Lang.COMMAND_SUDO_DONE_CHAT.getMessage();
        }
        else {
            target.performCommand(command);
            message = Lang.COMMAND_SUDO_DONE_COMMAND.getMessage();
        }
        message.replace(Placeholders.GENERIC_COMMAND, command).replace(Placeholders.forPlayer(target)).send(context.getSender());
        return true;
    }
}
