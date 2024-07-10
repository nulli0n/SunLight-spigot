package su.nightexpress.sunlight.module.rtp.command;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.command.experimental.CommandContext;
import su.nightexpress.nightcore.command.experimental.argument.ParsedArguments;
import su.nightexpress.nightcore.command.experimental.builder.DirectNodeBuilder;
import su.nightexpress.nightcore.command.experimental.node.DirectNode;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.command.CommandRegistry;
import su.nightexpress.sunlight.command.template.CommandTemplate;
import su.nightexpress.sunlight.module.rtp.RTPModule;
import su.nightexpress.sunlight.module.rtp.config.RTPLang;
import su.nightexpress.sunlight.module.rtp.config.RTPPerms;

public class RTPCommands {

    public static final String NODE = "rtp";

    public static void load(@NotNull SunLightPlugin plugin, @NotNull RTPModule module) {
        CommandRegistry.registerDirectExecutor(NODE, (template, config) -> builder(plugin, module, template, config));

        CommandRegistry.addTemplate(NODE, CommandTemplate.direct(new String[]{NODE, "wild"}, NODE));
    }

    @NotNull
    public static DirectNodeBuilder builder(@NotNull SunLightPlugin plugin, @NotNull RTPModule module, @NotNull CommandTemplate template, @NotNull FileConfig config) {
        return DirectNode.builder(plugin, template.getAliases())
            .playerOnly()
            .description(RTPLang.COMMAND_RTP_DESC)
            .permission(RTPPerms.COMMAND_RTP)
            .executes((context, arguments) -> execute(plugin, module, context, arguments))
            ;
    }

    public static boolean execute(@NotNull SunLightPlugin plugin, @NotNull RTPModule module, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Player player = context.getPlayerOrThrow();
        module.startSearch(player);
        return true;
    }
}
