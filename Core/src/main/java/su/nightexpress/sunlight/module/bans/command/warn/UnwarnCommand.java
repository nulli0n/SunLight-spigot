package su.nightexpress.sunlight.module.bans.command.warn;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.sunlight.module.bans.BansModule;
import su.nightexpress.sunlight.module.bans.command.api.AbstractUnPunishCommand;
import su.nightexpress.sunlight.module.bans.config.BansLang;
import su.nightexpress.sunlight.module.bans.punishment.PunishmentType;
import su.nightexpress.sunlight.module.bans.util.BansPerms;

public class UnwarnCommand extends AbstractUnPunishCommand {

    public static final String NAME = "unwarn";

    public UnwarnCommand(@NotNull BansModule bansModule, @NotNull String[] aliases) {
        super(bansModule, aliases, BansPerms.COMMAND_UNWARN, PunishmentType.WARN);
        this.setDescription(plugin.getMessage(BansLang.COMMAND_UNWARN_DESC));
        this.setUsage(plugin.getMessage(BansLang.COMMAND_UNWARN_USAGE));
    }
}
