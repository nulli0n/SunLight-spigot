package su.nightexpress.sunlight.module.bans.command;

import org.bukkit.entity.Player;
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
import su.nightexpress.sunlight.module.bans.punishment.PlayerPunishment;
import su.nightexpress.sunlight.module.bans.punishment.PunishmentType;
import su.nightexpress.sunlight.user.UserManager;
import su.nightexpress.sunlight.utils.FutureUtils;

public class HistoryCommandsProvider extends AbstractCommandProvider {

    private final BansModule module;
    private final UserManager userManager;

    public HistoryCommandsProvider(@NotNull SunLightPlugin plugin, @NotNull BansModule module, @NotNull UserManager userManager) {
        super(plugin);
        this.module = module;
        this.userManager = userManager;
    }

    @Override
    public void registerDefaults() {
        this.registerLiteral("banhistory", true, new String[]{"banhistory"}, builder -> this.builder(builder, PunishmentType.BAN));
        this.registerLiteral("mutehistory", true, new String[]{"mutehistory"}, builder -> this.builder(builder, PunishmentType.MUTE));
        this.registerLiteral("warnhistory", true, new String[]{"warnhistory"}, builder -> this.builder(builder, PunishmentType.WARN));
    }

    private void builder(@NotNull LiteralNodeBuilder builder, @NotNull PunishmentType type) {
        TextLocale description = switch (type) {
            case BAN -> BansLang.COMMAND_BAN_HISTORY_DESC;
            case MUTE -> BansLang.COMMAND_MUTE_HISTORY_DESC;
            case WARN -> BansLang.COMMAND_WARN_HISTORY_DESC;
        };

        Permission permission = switch (type) {
            case BAN -> BansPerms.COMMAND_BAN_HISTORY;
            case MUTE -> BansPerms.COMMAND_MUTE_HISTORY;
            case WARN -> BansPerms.COMMAND_WARN_HISTORY;
        };

        builder
            .playerOnly()
            .description(description)
            .permission(permission)
            .withArguments(
                Arguments.playerName(CommandArguments.PLAYER)
                    .suggestions((reader, tabContext) -> this.module.getPlayerPunishments(type).stream().map(PlayerPunishment::getPlayerName).toList())
            )
            .executes((context, arguments) -> this.showHistory(context, arguments, type));
    }

    private boolean showHistory(@NotNull CommandContext context, @NotNull ParsedArguments arguments, @NotNull PunishmentType type) {
        Player viewer = context.getPlayerOrThrow();
        String targetName = arguments.getString(CommandArguments.PLAYER);

        this.userManager.loadTargetProfile(targetName).thenAcceptAsync(profile -> {
            if (profile == null) {
                context.errorBadPlayer();
                return;
            }

            this.module.openHistory(viewer, profile, type);
        }, this.plugin::runTask).whenComplete(FutureUtils::printStacktrace);

        return true;
    }
}
