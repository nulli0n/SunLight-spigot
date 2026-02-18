package su.nightexpress.sunlight.module.bans.command;

import org.bukkit.permissions.Permission;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.commands.Arguments;
import su.nightexpress.nightcore.commands.builder.LiteralNodeBuilder;
import su.nightexpress.nightcore.commands.context.CommandContext;
import su.nightexpress.nightcore.commands.context.ParsedArguments;
import su.nightexpress.nightcore.locale.entry.TextLocale;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.command.CommandArguments;
import su.nightexpress.sunlight.command.provider.type.AbstractCommandProvider;
import su.nightexpress.sunlight.module.bans.BansModule;
import su.nightexpress.sunlight.module.bans.config.BansLang;
import su.nightexpress.sunlight.module.bans.config.BansPerms;
import su.nightexpress.sunlight.module.bans.punishment.InetPunishment;
import su.nightexpress.sunlight.module.bans.punishment.PlayerPunishment;
import su.nightexpress.sunlight.module.bans.punishment.PunishmentType;
import su.nightexpress.sunlight.user.UserManager;
import su.nightexpress.sunlight.utils.FutureUtils;

import java.net.InetAddress;

public class PardonCommandsProvider extends AbstractCommandProvider {

    private final BansModule module;
    private final UserManager userManager;

    public PardonCommandsProvider(@NotNull SunLightPlugin plugin, @NotNull BansModule module, @NotNull UserManager userManager) {
        super(plugin);
        this.module = module;
        this.userManager = userManager;
    }

    @Override
    public void registerDefaults() {
        this.registerLiteral("unban", true, new String[]{"unban"}, builder -> this.builderPlayer(builder, PunishmentType.BAN));
        this.registerLiteral("unmute", true, new String[]{"unmute"}, builder -> this.builderPlayer(builder, PunishmentType.MUTE));
        this.registerLiteral("unwarn", true, new String[]{"unwarn"}, builder -> this.builderPlayer(builder, PunishmentType.WARN));

        this.registerLiteral("unbanip", true, new String[]{"unbanip"}, this::builderInet);
    }

    private void builderInet(@NotNull LiteralNodeBuilder builder) {
        builder
            .description(BansLang.COMMAND_UNBAN_IP_DESC)
            .permission(BansPerms.COMMAND_UNBAN_IP)
            .withArguments(
                Arguments.playerName(CommandArguments.INET_ADDRESS)
                    .suggestions((reader, tabContext) -> this.module.getPunishmentRepository(PunishmentType.BAN).getActiveInetPunishments().stream().map(InetPunishment::getRawAddress).toList())
            )
            .withFlags(CommandArguments.FLAG_SILENT)
            .executes(this::pardonInet);
    }

    private void builderPlayer(@NotNull LiteralNodeBuilder builder, @NotNull PunishmentType type) {
        TextLocale description = switch (type) {
            case BAN -> BansLang.COMMAND_UNBAN_DESC;
            case MUTE -> BansLang.COMMAND_UNMUTE_DESC;
            case WARN -> BansLang.COMMAND_UNWARN_DESC;
        };

        Permission permission = switch (type) {
            case BAN -> BansPerms.COMMAND_UNBAN;
            case MUTE -> BansPerms.COMMAND_UNMUTE;
            case WARN -> BansPerms.COMMAND_UNWARN;
        };

        builder
            .description(description)
            .permission(permission)
            .withArguments(
                Arguments.playerName(CommandArguments.PLAYER)
                    .suggestions((reader, tabContext) -> this.module.getPunishmentRepository(type).getActivePlayerPunishments().stream().map(PlayerPunishment::getPlayerName).toList())
            )
            .withFlags(CommandArguments.FLAG_SILENT)
            .executes((context, arguments) -> this.pardonPlayer(context, arguments, type));
    }

    private boolean pardonInet(@NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        InetAddress address = arguments.get(CommandArguments.INET_ADDRESS, InetAddress.class);
        boolean silent = context.hasFlag(CommandArguments.FLAG_SILENT);

        return this.module.pardonInet(address, context.getSender(), silent);
    }

    private boolean pardonPlayer(@NotNull CommandContext context, @NotNull ParsedArguments arguments, @NotNull PunishmentType type) {
        String targetName = arguments.getString(CommandArguments.PLAYER);
        boolean silent = context.hasFlag(CommandArguments.FLAG_SILENT);

        this.userManager.loadTargetProfile(targetName).thenAcceptAsync(profile -> {
            if (profile == null) {
                context.errorBadPlayer();
                return;
            }

            this.module.pardonPlayer(profile, context.getSender(), type, silent);
        }, this.plugin::runTask).whenComplete(FutureUtils::printStacktrace);

        return true;
    }
}
