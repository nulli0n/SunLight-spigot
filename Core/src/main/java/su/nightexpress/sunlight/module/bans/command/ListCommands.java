package su.nightexpress.sunlight.module.bans.command;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.command.experimental.CommandContext;
import su.nightexpress.nightcore.command.experimental.argument.ParsedArguments;
import su.nightexpress.nightcore.command.experimental.builder.DirectNodeBuilder;
import su.nightexpress.nightcore.command.experimental.node.DirectNode;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.command.CommandRegistry;
import su.nightexpress.sunlight.command.template.CommandTemplate;
import su.nightexpress.sunlight.module.bans.BansModule;
import su.nightexpress.sunlight.module.bans.config.BansLang;
import su.nightexpress.sunlight.module.bans.config.BansPerms;
import su.nightexpress.sunlight.module.bans.punishment.PunishmentType;

public class ListCommands {

    public static final String NODE_BAN    = "banlist";
    public static final String NODE_MUTE   = "mutelist";
    public static final String NODE_WARN   = "warnlist";

    public static void load(@NotNull SunLightPlugin plugin, @NotNull BansModule module) {
        CommandRegistry.registerDirectExecutor(NODE_BAN, (template, config) -> builder(plugin, module, template, config, PunishmentType.BAN)
            .description(BansLang.COMMAND_BAN_LIST_DESC)
            .permission(BansPerms.COMMAND_BAN_LIST)
        );

        CommandRegistry.registerDirectExecutor(NODE_MUTE, (template, config) -> builder(plugin, module, template, config, PunishmentType.MUTE)
            .description(BansLang.COMMAND_MUTE_LIST_DESC)
            .permission(BansPerms.COMMAND_MUTE_LIST)
        );

        CommandRegistry.registerDirectExecutor(NODE_WARN, (template, config) -> builder(plugin, module, template, config, PunishmentType.WARN)
            .description(BansLang.COMMAND_WARN_LIST_DESC)
            .permission(BansPerms.COMMAND_WARN_LIST)
        );

        CommandRegistry.addSimpleTemplate(NODE_BAN);
        CommandRegistry.addSimpleTemplate(NODE_MUTE);
        CommandRegistry.addSimpleTemplate(NODE_WARN);
    }

    public static DirectNodeBuilder builder(@NotNull SunLightPlugin plugin, @NotNull BansModule module, @NotNull CommandTemplate template, @NotNull FileConfig config,
                                            @NotNull PunishmentType type) {
        return DirectNode.builder(plugin, template.getAliases())

            .executes((context, arguments) -> execute(plugin, module, context, arguments, type))
            .playerOnly()
            ;
    }

    public static boolean execute(@NotNull SunLightPlugin plugin, @NotNull BansModule module, @NotNull CommandContext context, @NotNull ParsedArguments arguments,
                                  @NotNull PunishmentType type) {
        if (context.getExecutor() == null) return false;

        module.openPunishments(context.getExecutor(), type);
        return true;
    }
}
