package su.nightexpress.sunlight.module.bans.command;

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
import su.nightexpress.sunlight.module.bans.BansModule;
import su.nightexpress.sunlight.module.bans.config.BansLang;
import su.nightexpress.sunlight.module.bans.config.BansPerms;
import su.nightexpress.sunlight.module.bans.punishment.PunishedIP;
import su.nightexpress.sunlight.module.bans.punishment.PunishedPlayer;
import su.nightexpress.sunlight.module.bans.punishment.PunishmentType;
import su.nightexpress.sunlight.utils.SunUtils;

import java.util.ArrayList;
import java.util.List;

public class HistoryCommands {

    public static final String NODE_BAN    = "banhistory";
    public static final String NODE_MUTE   = "mutehistory";
    public static final String NODE_WARN   = "warnhistory";

    public static void load(@NotNull SunLightPlugin plugin, @NotNull BansModule module) {
        CommandRegistry.registerDirectExecutor(NODE_BAN, (template, config) -> builder(plugin, module, template, config, PunishmentType.BAN)
            .description(BansLang.COMMAND_BAN_HISTORY_DESC)
            .permission(BansPerms.COMMAND_BAN_HISTORY)
        );

        CommandRegistry.registerDirectExecutor(NODE_MUTE, (template, config) -> builder(plugin, module, template, config, PunishmentType.MUTE)
            .description(BansLang.COMMAND_MUTE_HISTORY_DESC)
            .permission(BansPerms.COMMAND_MUTE_HISTORY)
        );

        CommandRegistry.registerDirectExecutor(NODE_WARN, (template, config) -> builder(plugin, module, template, config, PunishmentType.WARN)
            .description(BansLang.COMMAND_WARN_HISTORY_DESC)
            .permission(BansPerms.COMMAND_WARN_HISTORY)
        );

        CommandRegistry.addSimpleTemplate(NODE_BAN);
        CommandRegistry.addSimpleTemplate(NODE_MUTE);
        CommandRegistry.addSimpleTemplate(NODE_WARN);
    }

    public static DirectNodeBuilder builder(@NotNull SunLightPlugin plugin, @NotNull BansModule module, @NotNull CommandTemplate template, @NotNull FileConfig config,
                                            @NotNull PunishmentType type) {
        return DirectNode.builder(plugin, template.getAliases())
            .withArgument(ArgumentTypes.playerName(CommandArguments.PLAYER)
                .required()
                .localized(BansLang.COMMAND_ARGUMENT_NAME_IP_OR_NAME)
                .withSamples(tabContext -> {
                    List<String> list = new ArrayList<>(module.getPunishedPlayers(type).stream().map(PunishedPlayer::getPlayerName).toList());
                    if (type == PunishmentType.BAN) {
                        list.addAll(module.getPunishedIPs().stream().map(PunishedIP::getAddress).toList());
                    }
                    return list;
                })
            )
            .executes((context, arguments) -> execute(plugin, module, context, arguments, type))
            .playerOnly()
            ;
    }

    public static boolean execute(@NotNull SunLightPlugin plugin, @NotNull BansModule module, @NotNull CommandContext context, @NotNull ParsedArguments arguments,
                                  @NotNull PunishmentType type) {
        if (context.getExecutor() == null) return false;

        String nameOrAddress = arguments.getStringArgument(CommandArguments.PLAYER);
        if (SunUtils.isInetAddress(nameOrAddress)) {
            return module.openHistory(context.getExecutor(), nameOrAddress);
        }

        plugin.getUserManager().manageUser(nameOrAddress, user -> {
            if (user == null) {
                context.errorBadPlayer();
                return;
            }

            module.openHistory(context.getExecutor(), user, type);
        });
        return true;
    }
}
