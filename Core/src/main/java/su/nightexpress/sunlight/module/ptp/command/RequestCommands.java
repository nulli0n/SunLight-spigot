package su.nightexpress.sunlight.module.ptp.command;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.command.experimental.CommandContext;
import su.nightexpress.nightcore.command.experimental.argument.ArgumentTypes;
import su.nightexpress.nightcore.command.experimental.argument.ParsedArguments;
import su.nightexpress.nightcore.command.experimental.builder.DirectNodeBuilder;
import su.nightexpress.nightcore.command.experimental.node.DirectNode;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.command.CommandArguments;
import su.nightexpress.sunlight.command.CommandRegistry;
import su.nightexpress.sunlight.command.template.CommandTemplate;
import su.nightexpress.sunlight.config.Lang;
import su.nightexpress.sunlight.module.ptp.Mode;
import su.nightexpress.sunlight.module.ptp.PTPModule;
import su.nightexpress.sunlight.module.ptp.config.PTPLang;
import su.nightexpress.sunlight.module.ptp.config.PTPPerms;

public class RequestCommands {

    public static final String NODE_REQUEST = "ptp_request";
    public static final String NODE_INVITE  = "ptp_invite";

    public static void load(@NotNull SunLightPlugin plugin, @NotNull PTPModule module) {
        CommandRegistry.registerDirectExecutor(NODE_REQUEST, (template, config) -> builder(plugin, module, template, config, Mode.REQUEST)
            .description(PTPLang.COMMAND_REQUEST_DESC)
            .permission(PTPPerms.COMMAND_REQUEST)
        );

        CommandRegistry.registerDirectExecutor(NODE_INVITE, (template, config) -> builder(plugin, module, template, config, Mode.INVITE)
            .description(PTPLang.COMMAND_INVITE_DESC)
            .permission(PTPPerms.COMMAND_INVITE)
        );

        CommandRegistry.addTemplate("tpa", CommandTemplate.direct(new String[]{"tpa", "call"}, NODE_REQUEST));
        CommandRegistry.addTemplate("tphere", CommandTemplate.direct(new String[]{"tphere"}, NODE_INVITE));
    }

    @NotNull
    public static DirectNodeBuilder builder(@NotNull SunLightPlugin plugin, @NotNull PTPModule module, @NotNull CommandTemplate template, @NotNull FileConfig config,
                                            @NotNull Mode mode) {
        return DirectNode.builder(plugin, template.getAliases())
            .playerOnly()
            .description(PTPLang.COMMAND_REQUEST_DESC)
            .permission(PTPPerms.COMMAND_REQUEST)
            .withArgument(ArgumentTypes.player(CommandArguments.PLAYER).required())
            .executes((context, arguments) -> execute(plugin, module, context, arguments, mode))
            ;
    }

    public static boolean execute(@NotNull SunLightPlugin plugin, @NotNull PTPModule module, @NotNull CommandContext context, @NotNull ParsedArguments arguments,
                                   @NotNull Mode mode) {

        Player player = context.getExecutor();
        if (player == null) return false;

        Player target = arguments.getPlayerArgument(CommandArguments.PLAYER);
        if (player == target) {
            context.send(Lang.ERROR_COMMAND_NOT_YOURSELF.getMessage());
            return false;
        }

        return module.sendRequest(player, target, mode);
    }
}
