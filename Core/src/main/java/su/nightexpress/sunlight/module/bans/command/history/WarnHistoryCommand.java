package su.nightexpress.sunlight.module.bans.command.history;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.sunlight.module.bans.BansModule;
import su.nightexpress.sunlight.module.bans.command.api.AbstractHistoryCommand;
import su.nightexpress.sunlight.module.bans.config.BansLang;
import su.nightexpress.sunlight.module.bans.punishment.PunishmentType;
import su.nightexpress.sunlight.module.bans.util.BansPerms;

public class WarnHistoryCommand extends AbstractHistoryCommand {

    public static final String NAME = "warnhistory";

    public WarnHistoryCommand(@NotNull BansModule bansModule, @NotNull String[] aliases) {
        super(bansModule, aliases, BansPerms.COMMAND_WARNHISTORY, PunishmentType.WARN);
        this.setDescription(this.plugin.getMessage(BansLang.COMMAND_HISTORY_WARN_DESC));
        this.setUsage(this.plugin.getMessage(BansLang.COMMAND_HISTORY_WARN_USAGE));
    }
}
