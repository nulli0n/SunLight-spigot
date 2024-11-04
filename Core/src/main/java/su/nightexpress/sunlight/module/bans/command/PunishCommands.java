package su.nightexpress.sunlight.module.bans.command;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.command.experimental.CommandContext;
import su.nightexpress.nightcore.command.experimental.argument.ArgumentTypes;
import su.nightexpress.nightcore.command.experimental.argument.CommandArgument;
import su.nightexpress.nightcore.command.experimental.argument.ParsedArguments;
import su.nightexpress.nightcore.command.experimental.builder.ArgumentBuilder;
import su.nightexpress.nightcore.command.experimental.builder.DirectNodeBuilder;
import su.nightexpress.nightcore.command.experimental.node.DirectNode;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.util.Lists;
import su.nightexpress.nightcore.util.Players;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.command.CommandArguments;
import su.nightexpress.sunlight.command.CommandFlags;
import su.nightexpress.sunlight.command.CommandRegistry;
import su.nightexpress.sunlight.command.template.CommandTemplate;
import su.nightexpress.sunlight.module.bans.BansModule;
import su.nightexpress.sunlight.module.bans.config.BansLang;
import su.nightexpress.sunlight.module.bans.config.BansPerms;
import su.nightexpress.sunlight.module.bans.punishment.PunishmentReason;
import su.nightexpress.sunlight.module.bans.punishment.PunishmentType;
import su.nightexpress.sunlight.module.bans.util.BanTime;
import su.nightexpress.sunlight.module.bans.util.TimeUnit;
import su.nightexpress.sunlight.utils.SunUtils;

import java.util.ArrayList;

public class PunishCommands {

    public static final String NODE_KICK   = "kick";
    public static final String NODE_BAN    = "ban";
    public static final String NODE_BAN_IP = "banip";
    public static final String NODE_MUTE   = "mute";
    public static final String NODE_WARN   = "warn";

    private static final String ARG_REASON = "reason";

    public static void load(@NotNull SunLightPlugin plugin, @NotNull BansModule module) {
        CommandRegistry.registerDirectExecutor(NODE_BAN, (template, config) -> builderType(plugin, module, template, config, PunishmentType.BAN)
            .description(BansLang.COMMAND_BAN_DESC)
            .permission(BansPerms.COMMAND_BAN)
        );

        CommandRegistry.registerDirectExecutor(NODE_MUTE, (template, config) -> builderType(plugin, module, template, config, PunishmentType.MUTE)
            .description(BansLang.COMMAND_MUTE_DESC)
            .permission(BansPerms.COMMAND_MUTE)
        );

        CommandRegistry.registerDirectExecutor(NODE_WARN, (template, config) -> builderType(plugin, module, template, config, PunishmentType.WARN)
            .description(BansLang.COMMAND_WARN_DESC)
            .permission(BansPerms.COMMAND_WARN)
        );

        CommandRegistry.registerDirectExecutor(NODE_BAN_IP, (template, config) -> builderIP(plugin, module, template, config));
        CommandRegistry.registerDirectExecutor(NODE_KICK, (template, config) -> builderKick(plugin, module, template, config));

        CommandRegistry.addSimpleTemplate(NODE_KICK);
        CommandRegistry.addSimpleTemplate(NODE_BAN);
        CommandRegistry.addSimpleTemplate(NODE_BAN_IP);
        CommandRegistry.addSimpleTemplate(NODE_MUTE);
        CommandRegistry.addSimpleTemplate(NODE_WARN);
    }

    @NotNull
    private static ArgumentBuilder<BanTime> timeArgument() {
        return CommandArgument.builder(CommandArguments.TIME, (str, context) -> BansModule.parseBanTime(str))
            .localized(BansLang.COMMAND_ARGUMENT_NAME_TIME)
            .customFailure(BansLang.ERROR_COMMAND_INVALID_TIME_ARGUMENT)
            .withSamples(tabContext -> Lists.newList(
                TimeUnit.PERMANENT.getAliases()[0],
                "300" + TimeUnit.SECONDS.getAliases()[0],
                "60" + TimeUnit.MINUTES.getAliases()[0],
                "2" + TimeUnit.HOURS.getAliases()[0],
                "7" + TimeUnit.DAYS.getAliases()[0],
                "1" + TimeUnit.WEEKS.getAliases()[0],
                "1" + TimeUnit.MONTHS.getAliases()[0],
                "1" + TimeUnit.YEARS.getAliases()[0]
            ))
            ;
    }

    @NotNull
    private static ArgumentBuilder<PunishmentReason> reasonArgument() {
        return CommandArgument.builder(ARG_REASON, (str, context) -> BansModule.getReason(str))
            .localized(BansLang.COMMAND_ARGUMENT_NAME_REASON)
            .customFailure(BansLang.ERROR_COMMAND_INVALID_REASON_ARGUMENT)
            .withSamples(tabContext -> new ArrayList<>(BansModule.getReasonMap().keySet()))
            ;
    }

    public static DirectNodeBuilder builderType(@NotNull SunLightPlugin plugin, @NotNull BansModule module, @NotNull CommandTemplate template, @NotNull FileConfig config,
                                                @NotNull PunishmentType type) {
        return DirectNode.builder(plugin, template.getAliases())
            .withArgument(ArgumentTypes.playerName(CommandArguments.PLAYER).required())
            .withArgument(timeArgument())
            .withArgument(reasonArgument())
            .withFlag(CommandFlags.silent())
            .executes((context, arguments) -> executeType(plugin, module, context, arguments, type))
            ;
    }

    public static DirectNodeBuilder builderIP(@NotNull SunLightPlugin plugin, @NotNull BansModule module, @NotNull CommandTemplate template, @NotNull FileConfig config) {
        return DirectNode.builder(plugin, template.getAliases())
            .description(BansLang.COMMAND_BAN_IP_DESC)
            .permission(BansPerms.COMMAND_BAN_IP)
            .withArgument(ArgumentTypes.string(CommandArguments.IP)
                .required()
                .localized(BansLang.COMMAND_ARGUMENT_NAME_IP_OR_NAME)
                .withSamples(tabContext -> Players.playerNames(tabContext.getPlayer())))
            .withArgument(timeArgument())
            .withArgument(reasonArgument())
            .withFlag(CommandFlags.silent())
            .executes((context, arguments) -> executeBanIP(plugin, module, context, arguments))
            ;
    }

    public static DirectNodeBuilder builderKick(@NotNull SunLightPlugin plugin, @NotNull BansModule module, @NotNull CommandTemplate template, @NotNull FileConfig config) {
        return DirectNode.builder(plugin, template.getAliases())
            .description(BansLang.COMMAND_KICK_DESC)
            .permission(BansPerms.COMMAND_KICK)
            .withArgument(ArgumentTypes.player(CommandArguments.PLAYER).required())
            .withArgument(reasonArgument())
            .withFlag(CommandFlags.silent())
            .executes((context, arguments) -> executeKick(plugin, module, context, arguments))
            ;
    }

    public static boolean executeType(@NotNull SunLightPlugin plugin, @NotNull BansModule module, @NotNull CommandContext context, @NotNull ParsedArguments arguments,
                                      @NotNull PunishmentType type) {

        plugin.getUserManager().manageUser(arguments.getStringArgument(CommandArguments.PLAYER), user -> {
            if (user == null) {
                context.errorBadPlayer();
                return;
            }

            //UserInfo userInfo = new UserInfo(user);

            BanTime banTime =  arguments.getArgument(CommandArguments.TIME, BanTime.class, BanTime.PERMANENT);
            PunishmentReason reason = arguments.getArgument(ARG_REASON, PunishmentReason.class, BansModule.getDefaultReason());
            boolean silent = arguments.hasFlag(CommandFlags.SILENT);

            module.punishPlayer(user, context.getSender(), reason, banTime, type, silent);
        });

        return true;
    }

    public static boolean executeBanIP(@NotNull SunLightPlugin plugin, @NotNull BansModule module, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        String address = arguments.getStringArgument(CommandArguments.IP);
        BanTime banTime = arguments.getArgument(CommandArguments.TIME, BanTime.class, BanTime.PERMANENT);
        PunishmentReason reason = arguments.getArgument(ARG_REASON, PunishmentReason.class, BansModule.getDefaultReason());
        boolean silent = arguments.hasFlag(CommandFlags.SILENT);

        if (SunUtils.isInetAddress(address)) {
            return module.banIP(address, context.getSender(), reason, banTime, silent);
        }
        else {
            plugin.getUserManager().manageUser(address, user -> {
                if (user == null) {
                    context.errorBadPlayer();
                    return;
                }

                module.banIP(user.getInetAddress(), context.getSender(), reason, banTime, silent);
            });
        }
        return true;
    }

    public static boolean executeKick(@NotNull SunLightPlugin plugin, @NotNull BansModule module, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Player player = arguments.getPlayerArgument(CommandArguments.PLAYER);
        PunishmentReason reason = arguments.getArgument(ARG_REASON, PunishmentReason.class, BansModule.getDefaultReason());

        return module.kick(player, context.getSender(), reason, arguments.hasFlag(CommandFlags.SILENT));
    }
}
