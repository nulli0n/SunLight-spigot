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
import su.nightexpress.sunlight.module.ptp.PTPModule;
import su.nightexpress.sunlight.module.ptp.TeleportRequest;
import su.nightexpress.sunlight.module.ptp.config.PTPLang;
import su.nightexpress.sunlight.module.ptp.config.PTPPerms;
import su.nightexpress.sunlight.utils.UserInfo;

import java.util.Collections;

public class AcceptCommands {

    public static final String NODE_ACCEPT  = "ptp_accept";
    public static final String NODE_DECLINE = "ptp_decline";

    public static final String ACCEPT_NAME  = "tpyes";
    public static final String DECLINE_NAME = "tpno";

    public static void load(@NotNull SunLightPlugin plugin, @NotNull PTPModule module) {
        CommandRegistry.registerDirectExecutor(NODE_ACCEPT, (template, config) -> builder(plugin, module, template, config, true)
            .description(PTPLang.COMMAND_ACCEPT_DESC)
            .permission(PTPPerms.COMMAND_ACCEPT)
        );

        CommandRegistry.registerDirectExecutor(NODE_DECLINE, (template, config) -> builder(plugin, module, template, config, false)
            .description(PTPLang.COMMAND_DECLINE_DESC)
            .permission(PTPPerms.COMMAND_DECLINE)
        );

        CommandRegistry.addTemplate(ACCEPT_NAME, CommandTemplate.direct(new String[]{ACCEPT_NAME}, NODE_ACCEPT));
        CommandRegistry.addTemplate(DECLINE_NAME, CommandTemplate.direct(new String[]{DECLINE_NAME}, NODE_DECLINE));
    }

    @NotNull
    public static DirectNodeBuilder builder(@NotNull SunLightPlugin plugin, @NotNull PTPModule module, @NotNull CommandTemplate template, @NotNull FileConfig config,
                                            boolean accept) {
        return DirectNode.builder(plugin, template.getAliases())
            .playerOnly()
            .withArgument(ArgumentTypes.player(CommandArguments.PLAYER)
                .withSamples(context -> {
                    if (context.getPlayer() == null) return Collections.emptyList();

                    return module.getRequests(context.getPlayer()).stream().map(TeleportRequest::getSenderInfo).map(UserInfo::getName).toList();
                })
            )
            .executes((context, arguments) -> execute(plugin, module, context, arguments, accept))
            ;
    }

    public static boolean execute(@NotNull SunLightPlugin plugin, @NotNull PTPModule module, @NotNull CommandContext context, @NotNull ParsedArguments arguments,
                                  boolean accept) {
        Player player = context.getExecutor();
        if (player == null) return false;

        String from = arguments.hasArgument(CommandArguments.PLAYER) ? arguments.getStringArgument(CommandArguments.PLAYER) : null;

        return accept ? module.accept(player, from) : module.decline(player, from);
    }
}
