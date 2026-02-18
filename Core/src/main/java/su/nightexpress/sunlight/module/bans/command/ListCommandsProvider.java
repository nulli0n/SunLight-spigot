package su.nightexpress.sunlight.module.bans.command;

import org.bukkit.permissions.Permission;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.commands.builder.LiteralNodeBuilder;
import su.nightexpress.nightcore.commands.context.CommandContext;
import su.nightexpress.nightcore.locale.entry.TextLocale;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.command.provider.type.AbstractCommandProvider;
import su.nightexpress.sunlight.module.bans.BansModule;
import su.nightexpress.sunlight.module.bans.config.BansLang;
import su.nightexpress.sunlight.module.bans.config.BansPerms;
import su.nightexpress.sunlight.module.bans.punishment.PunishmentType;

public class ListCommandsProvider extends AbstractCommandProvider {

    public static final String NODE_BAN    = "banlist";
    public static final String NODE_MUTE   = "mutelist";
    public static final String NODE_WARN   = "warnlist";

    private final BansModule module;

    public ListCommandsProvider(@NotNull SunLightPlugin plugin, @NotNull BansModule module) {
        super(plugin);
        this.module = module;
    }

    @Override
    public void registerDefaults() {
        this.registerLiteral("banlist", true, new String[]{"banlist"}, builder -> this.build(builder, PunishmentType.BAN));
        this.registerLiteral("mutelist", true, new String[]{"mutelist"}, builder -> this.build(builder, PunishmentType.MUTE));
        this.registerLiteral("warnlist", true, new String[]{"warnlist"}, builder -> this.build(builder, PunishmentType.WARN));
    }

    private void build(@NotNull LiteralNodeBuilder builder, @NotNull PunishmentType type) {
        TextLocale description = switch (type) {
            case BAN -> BansLang.COMMAND_BAN_LIST_DESC;
            case MUTE -> BansLang.COMMAND_MUTE_LIST_DESC;
            case WARN -> BansLang.COMMAND_WARN_LIST_DESC;
        };

        Permission permission = switch (type) {
            case BAN -> BansPerms.COMMAND_BAN_LIST;
            case MUTE -> BansPerms.COMMAND_MUTE_LIST;
            case WARN -> BansPerms.COMMAND_WARN_LIST;
        };

        builder
            .playerOnly()
            .description(description)
            .permission(permission)
            .executes((context, arguments) -> this.showMenu(context, type));
    }

    private boolean showMenu(@NotNull CommandContext context, @NotNull PunishmentType type) {
        this.module.openPunishments(context.getPlayerOrThrow(), type);
        return true;
    }
}
