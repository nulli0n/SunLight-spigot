package su.nightexpress.sunlight.module.bans.command.mute;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.sunlight.module.bans.BansModule;
import su.nightexpress.sunlight.module.bans.command.api.AbstractUnPunishCommand;
import su.nightexpress.sunlight.module.bans.config.BansLang;
import su.nightexpress.sunlight.module.bans.punishment.PunishmentType;
import su.nightexpress.sunlight.module.bans.util.BansPerms;

public class UnmuteCommand extends AbstractUnPunishCommand {

    public static final String NAME = "unmute";

    public UnmuteCommand(@NotNull BansModule bansModule, @NotNull String[] aliases) {
        super(bansModule, aliases, BansPerms.COMMAND_UNMUTE, PunishmentType.MUTE);
        this.setUsage(plugin.getMessage(BansLang.COMMAND_UNMUTE_USAGE));
        this.setDescription(plugin.getMessage(BansLang.COMMAND_UNMUTE_DESC));
    }
}
