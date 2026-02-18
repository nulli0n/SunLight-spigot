package su.nightexpress.sunlight.module.bans.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.commands.Arguments;
import su.nightexpress.nightcore.commands.Commands;
import su.nightexpress.nightcore.commands.argument.ArgumentType;
import su.nightexpress.nightcore.commands.builder.ArgumentNodeBuilder;
import su.nightexpress.nightcore.commands.builder.LiteralNodeBuilder;
import su.nightexpress.nightcore.commands.context.CommandContext;
import su.nightexpress.nightcore.commands.context.ParsedArguments;
import su.nightexpress.nightcore.commands.exceptions.CommandSyntaxException;
import su.nightexpress.nightcore.core.config.CoreLang;
import su.nightexpress.nightcore.locale.entry.TextLocale;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.command.CommandArguments;
import su.nightexpress.sunlight.command.provider.type.AbstractCommandProvider;
import su.nightexpress.sunlight.config.Lang;
import su.nightexpress.sunlight.module.bans.BansModule;
import su.nightexpress.sunlight.module.bans.config.BansLang;
import su.nightexpress.sunlight.module.bans.config.BansPerms;
import su.nightexpress.sunlight.module.bans.punishment.PunishmentContext;
import su.nightexpress.sunlight.module.bans.punishment.PunishmentReason;
import su.nightexpress.sunlight.module.bans.punishment.PunishmentType;
import su.nightexpress.sunlight.module.bans.time.BanTime;
import su.nightexpress.sunlight.module.bans.time.BanTimeUnit;
import su.nightexpress.sunlight.user.UserManager;

import java.net.InetAddress;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class PunishmentCommandsProvider extends AbstractCommandProvider {

    private static final String ARG_REASON = "reason";

    private final BansModule module;
    private final UserManager userManager;

    private final ArgumentType<BanTimeUnit>      timeUnitArgumentType;
    private final ArgumentType<PunishmentReason> punishmentReasonArgumentType;

    public PunishmentCommandsProvider(@NotNull SunLightPlugin plugin, @NotNull BansModule module, @NotNull UserManager userManager) {
        super(plugin);
        this.module = module;
        this.userManager = userManager;

        this.timeUnitArgumentType = (builder, string) -> Optional.ofNullable(module.getTimeUnitByAlias(string))
            .orElseThrow(() -> CommandSyntaxException.custom(BansLang.COMMAND_SYNTAX_INVALID_TIME_UNIT));

        this.punishmentReasonArgumentType = (builder, string) -> Optional.ofNullable(this.module.getReasonById(string))
            .orElseThrow(() -> CommandSyntaxException.custom(BansLang.COMMAND_SYNTAX_INVALID_REASON));
    }

    @Override
    public void registerDefaults() {
        this.registerLiteral("ban", true, new String[]{"ban"}, builder -> this.buildPunishment(builder, PunishmentType.BAN, true));
        this.registerLiteral("mute", true, new String[]{"mute"}, builder -> this.buildPunishment(builder, PunishmentType.MUTE, true));
        this.registerLiteral("warn", true, new String[]{"warn"}, builder -> this.buildPunishment(builder, PunishmentType.WARN, true));
        this.registerLiteral("tempban", true, new String[]{"tempban"}, builder -> this.buildPunishment(builder, PunishmentType.BAN, false));
        this.registerLiteral("tempmute", true, new String[]{"tempmute"}, builder -> this.buildPunishment(builder, PunishmentType.MUTE, false));
        this.registerLiteral("tempwarn", true, new String[]{"tempwarn"}, builder -> this.buildPunishment(builder, PunishmentType.WARN, false));

        this.registerLiteral("banip", true, new String[]{"banip", "ipban"}, builder -> builder
            .description(BansLang.COMMAND_BAN_IP_DESC)
            .permission(BansPerms.COMMAND_BAN_IP)
            .withArguments(
                CommandArguments.inetAddress(CommandArguments.INET_ADDRESS),
                reasonArgument().optional()
            )
            .withFlags(CommandArguments.FLAG_SILENT)
            .executes(this::banInet)
        );

        this.registerLiteral("kick", true, new String[]{"kick"}, builder -> builder
            .description(BansLang.COMMAND_KICK_DESC)
            .permission(BansPerms.COMMAND_KICK)
            .withArguments(
                Arguments.player(CommandArguments.PLAYER),
                reasonArgument().optional()
            )
            .withFlags(CommandArguments.FLAG_SILENT)
            .executes(this::kick)
        );
    }

    @NotNull
    private ArgumentNodeBuilder<PunishmentReason> reasonArgument() {
        return Commands.argument(ARG_REASON, this.punishmentReasonArgumentType)
            .localized(BansLang.COMMAND_ARGUMENT_NAME_REASON)
            .suggestions((reader, tabContext) -> this.module.getReasonIds());
    }

    private void buildPunishment(@NotNull LiteralNodeBuilder builder, @NotNull PunishmentType type, boolean isPermament) {
        TextLocale description = switch (type) {
            case BAN -> BansLang.COMMAND_BAN_DESC;
            case MUTE -> BansLang.COMMAND_MUTE_DESC;
            case WARN -> BansLang.COMMAND_WARN_DESC;
        };

        Permission permission = switch (type) {
            case BAN -> BansPerms.COMMAND_BAN;
            case MUTE -> BansPerms.COMMAND_MUTE;
            case WARN -> BansPerms.COMMAND_WARN;
        };

        builder
            .description(description)
            .permission(permission)
            .withFlags(CommandArguments.FLAG_SILENT)
            .executes((context, arguments) -> this.punishPlayer(context, arguments, type, isPermament));

        if (isPermament) {
            builder.withArguments(
                Arguments.playerName(CommandArguments.PLAYER),
                reasonArgument().optional()
            );
        }
        else {
            builder.withArguments(
                Arguments.playerName(CommandArguments.PLAYER),
                Arguments.integer(CommandArguments.AMOUNT, 1)
                    .localized(Lang.COMMAND_ARGUMENT_NAME_TIME)
                    .suggestions((reader, context) -> List.of("1", "10", "30", "60")),
                Commands.argument(CommandArguments.TYPE, this.timeUnitArgumentType)
                    .localized(BansLang.COMMAND_ARGUMENT_NAME_TIME_UNIT)
                    .suggestions((reader, context) -> this.module.getTimeUnitAliases()),
                reasonArgument().optional()
            );
        }
    }

    private boolean punishPlayer(@NotNull CommandContext context, @NotNull ParsedArguments arguments, @NotNull PunishmentType type, boolean isPermanent) {
        CommandSender sender = context.getSender();
        String playerName = arguments.getString(CommandArguments.PLAYER);

        this.userManager.loadTargetProfile(playerName).thenCompose(profile -> {
            if (profile == null) {
                this.module.sendPrefixed(CoreLang.ERROR_INVALID_PLAYER, context.getSender());
                return CompletableFuture.completedFuture(null);
            }

            PunishmentReason reason = arguments.getOr(ARG_REASON, PunishmentReason.class, this.module.getDefaultReason());
            boolean silent = context.hasFlag(CommandArguments.FLAG_SILENT);

            BanTime banTime;
            if (isPermanent) {
                banTime = BanTime.permanent();
            }
            else {
                int duration = arguments.getInt(CommandArguments.AMOUNT);
                BanTimeUnit timeUnit = arguments.get(CommandArguments.TYPE, BanTimeUnit.class);
                banTime = BanTime.temporary(timeUnit, duration);
            }

            PunishmentContext punishmentContext = new PunishmentContext(type, reason, banTime, silent);

            return this.module.punishPlayer(sender, profile, punishmentContext);
        });
        return true;
    }

    private boolean banInet(@NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        InetAddress address = arguments.get(CommandArguments.INET_ADDRESS, InetAddress.class);
        PunishmentReason reason = arguments.getOr(ARG_REASON, PunishmentReason.class, this.module.getDefaultReason());
        boolean silent = context.hasFlag(CommandArguments.FLAG_SILENT);

        BanTime banTime = BanTime.permanent();

        return module.banInet(context.getSender(), address, reason, banTime, silent);
    }

    private boolean kick(@NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Player player = arguments.getPlayer(CommandArguments.PLAYER);
        PunishmentReason reason = arguments.getOr(ARG_REASON, PunishmentReason.class, this.module.getDefaultReason());
        boolean silent = context.hasFlag(CommandArguments.FLAG_SILENT);

        return this.module.kick(context.getSender(), player, reason, silent);
    }
}
