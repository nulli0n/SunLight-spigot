package su.nightexpress.sunlight.module.bans.command.mute;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.sunlight.module.bans.BansModule;
import su.nightexpress.sunlight.module.bans.command.api.AbstractPunishCommand;
import su.nightexpress.sunlight.module.bans.config.BansLang;
import su.nightexpress.sunlight.module.bans.punishment.PunishmentType;
import su.nightexpress.sunlight.module.bans.util.BansPerms;

public class MuteCommand extends AbstractPunishCommand {

    public static final String NAME = "mute";

    public MuteCommand(@NotNull BansModule bansModule, @NotNull String[] aliases) {
        super(bansModule, aliases, BansPerms.COMMAND_MUTE, PunishmentType.MUTE);
        this.setUsage(plugin.getMessage(BansLang.COMMAND_MUTE_USAGE));
        this.setDescription(plugin.getMessage(BansLang.COMMAND_MUTE_DESC));
    }
}
