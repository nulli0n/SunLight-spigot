package su.nightexpress.sunlight.module.bans.command.ban;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.sunlight.module.bans.BansModule;
import su.nightexpress.sunlight.module.bans.command.AbstractUnPunishCommand;
import su.nightexpress.sunlight.module.bans.config.BansLang;
import su.nightexpress.sunlight.module.bans.punishment.PunishmentType;
import su.nightexpress.sunlight.module.bans.config.BansPerms;

public class UnbanCommand extends AbstractUnPunishCommand {

    public static final String NAME = "unban";

    public UnbanCommand(@NotNull BansModule bansModule, @NotNull String[] aliases) {
        super(bansModule, aliases, BansPerms.COMMAND_UNBAN, PunishmentType.BAN);
        this.setDescription(plugin.getMessage(BansLang.COMMAND_UNBAN_DESC));
        this.setUsage(plugin.getMessage(BansLang.COMMAND_UNBAN_USAGE));
    }
}
