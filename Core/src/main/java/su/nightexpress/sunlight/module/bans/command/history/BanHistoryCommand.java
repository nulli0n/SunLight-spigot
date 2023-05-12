package su.nightexpress.sunlight.module.bans.command.history;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.sunlight.module.bans.BansModule;
import su.nightexpress.sunlight.module.bans.command.api.AbstractHistoryCommand;
import su.nightexpress.sunlight.module.bans.config.BansLang;
import su.nightexpress.sunlight.module.bans.punishment.PunishmentType;
import su.nightexpress.sunlight.module.bans.util.BansPerms;

public class BanHistoryCommand extends AbstractHistoryCommand {

    public static final String NAME = "banhistory";

    public BanHistoryCommand(@NotNull BansModule bansModule, @NotNull String[] aliases) {
        super(bansModule, aliases, BansPerms.COMMAND_BANHISTORY, PunishmentType.BAN);
        this.setDescription(this.plugin.getMessage(BansLang.COMMAND_HISTORY_BAN_DESC));
        this.setUsage(this.plugin.getMessage(BansLang.COMMAND_HISTORY_BAN_USAGE));
    }
}
