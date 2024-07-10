package su.nightexpress.sunlight.module.bans.command;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.command.experimental.CommandContext;
import su.nightexpress.nightcore.command.experimental.argument.ArgumentTypes;
import su.nightexpress.nightcore.command.experimental.argument.ParsedArguments;
import su.nightexpress.nightcore.command.experimental.builder.DirectNodeBuilder;
import su.nightexpress.nightcore.command.experimental.node.DirectNode;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.command.CommandFlags;
import su.nightexpress.sunlight.command.CommandArguments;
import su.nightexpress.sunlight.command.CommandRegistry;
import su.nightexpress.sunlight.command.template.CommandTemplate;
import su.nightexpress.sunlight.module.bans.BansModule;
import su.nightexpress.sunlight.module.bans.config.BansLang;
import su.nightexpress.sunlight.module.bans.config.BansPerms;
import su.nightexpress.sunlight.module.bans.punishment.PunishData;
import su.nightexpress.sunlight.module.bans.punishment.PunishedIP;
import su.nightexpress.sunlight.module.bans.punishment.PunishedPlayer;
import su.nightexpress.sunlight.module.bans.punishment.PunishmentType;
import su.nightexpress.sunlight.utils.SunUtils;
import su.nightexpress.sunlight.utils.UserInfo;

import java.util.ArrayList;
import java.util.List;

public class UnPunishCommands {

    public static final String NODE_UNBAN    = "unban";
    public static final String NODE_UNMUTE   = "unmute";
    public static final String NODE_UNWARN   = "unwarn";

    public static void load(@NotNull SunLightPlugin plugin, @NotNull BansModule module) {
        CommandRegistry.registerDirectExecutor(NODE_UNBAN, (template, config) -> builder(plugin, module, template, config, PunishmentType.BAN)
            .description(BansLang.COMMAND_UNBAN_DESC)
            .permission(BansPerms.COMMAND_UNBAN)
        );

        CommandRegistry.registerDirectExecutor(NODE_UNMUTE, (template, config) -> builder(plugin, module, template, config, PunishmentType.MUTE)
            .description(BansLang.COMMAND_UNMUTE_DESC)
            .permission(BansPerms.COMMAND_UNMUTE)
        );

        CommandRegistry.registerDirectExecutor(NODE_UNWARN, (template, config) -> builder(plugin, module, template, config, PunishmentType.WARN)
            .description(BansLang.COMMAND_UNWARN_DESC)
            .permission(BansPerms.COMMAND_UNWARN)
        );

        CommandRegistry.addSimpleTemplate(NODE_UNBAN);
        CommandRegistry.addSimpleTemplate(NODE_UNMUTE);
        CommandRegistry.addSimpleTemplate(NODE_UNWARN);
    }

    public static DirectNodeBuilder builder(@NotNull SunLightPlugin plugin, @NotNull BansModule module, @NotNull CommandTemplate template, @NotNull FileConfig config,
                                            @NotNull PunishmentType type) {
        return DirectNode.builder(plugin, template.getAliases())

            .withArgument(ArgumentTypes.string(CommandArguments.PLAYER)
                .required()
                .localized(BansLang.COMMAND_ARGUMENT_NAME_IP_OR_NAME)
                .withSamples(tabContext -> {
                    List<String> list = new ArrayList<>(module.getPunishedPlayers(type).stream().filter(PunishData::isActive).map(PunishedPlayer::getPlayerName).toList());
                    if (type == PunishmentType.BAN) {
                        list.addAll(module.getPunishedIPs().stream().filter(PunishData::isActive).map(PunishedIP::getAddress).toList());
                    }
                    return list;
                })
            )
            .withFlag(CommandFlags.silent())
            .executes((context, arguments) -> execute(plugin, module, context, arguments, type))
            ;
    }

    public static boolean execute(@NotNull SunLightPlugin plugin, @NotNull BansModule module, @NotNull CommandContext context, @NotNull ParsedArguments arguments,
                                  @NotNull PunishmentType type) {

        String name = arguments.getStringArgument(CommandArguments.PLAYER);
        boolean silent = arguments.hasFlag(CommandFlags.SILENT);

        if (SunUtils.isInetAddress(name)) {
            return module.unbanIP(name, context.getSender(), silent);
        }

        plugin.getUserManager().getUserDataAndPerformAsync(name, user -> {
            if (user == null) {
                context.errorBadPlayer();
                return;
            }

            UserInfo userInfo = new UserInfo(user);

            module.unpunishPlayer(userInfo, context.getSender(), type, silent);
        });

        return true;
    }
}
