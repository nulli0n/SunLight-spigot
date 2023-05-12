package su.nightexpress.sunlight.module.bans.command.list;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.sunlight.module.bans.BansModule;
import su.nightexpress.sunlight.module.bans.command.api.AbstractListCommand;
import su.nightexpress.sunlight.module.bans.config.BansLang;
import su.nightexpress.sunlight.module.bans.punishment.PunishmentType;
import su.nightexpress.sunlight.module.bans.util.BansPerms;

public class WarnListCommand extends AbstractListCommand {

    public static final String NAME = "warnlist";

    public WarnListCommand(@NotNull BansModule bansModule, @NotNull String[] aliases) {
        super(bansModule, aliases, BansPerms.COMMAND_WARNLIST, PunishmentType.WARN);
        this.setDescription(this.plugin.getMessage(BansLang.COMMAND_WARNLIST_DESC));
    }
}
