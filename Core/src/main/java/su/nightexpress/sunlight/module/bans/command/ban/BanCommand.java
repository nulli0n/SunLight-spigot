package su.nightexpress.sunlight.module.bans.command.ban;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.sunlight.module.bans.BansModule;
import su.nightexpress.sunlight.module.bans.command.api.AbstractPunishCommand;
import su.nightexpress.sunlight.module.bans.config.BansLang;
import su.nightexpress.sunlight.module.bans.punishment.PunishmentType;
import su.nightexpress.sunlight.module.bans.util.BansPerms;

public class BanCommand extends AbstractPunishCommand {

    public static final String NAME = "ban";

    public BanCommand(@NotNull BansModule bansModule, @NotNull String[] aliases) {
        super(bansModule, aliases, BansPerms.COMMAND_BAN, PunishmentType.BAN);
        this.setDescription(plugin.getMessage(BansLang.COMMAND_BAN_DESC));
        this.setUsage(plugin.getMessage(BansLang.COMMAND_BAN_USAGE));
    }
}
