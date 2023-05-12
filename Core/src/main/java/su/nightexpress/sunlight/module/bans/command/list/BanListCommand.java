package su.nightexpress.sunlight.module.bans.command.list;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.sunlight.module.bans.BansModule;
import su.nightexpress.sunlight.module.bans.command.api.AbstractListCommand;
import su.nightexpress.sunlight.module.bans.config.BansLang;
import su.nightexpress.sunlight.module.bans.punishment.PunishmentType;
import su.nightexpress.sunlight.module.bans.util.BansPerms;

public class BanListCommand extends AbstractListCommand {

    public static final String NAME = "banlist";

    public BanListCommand(@NotNull BansModule bansModule, @NotNull String[] aliases) {
        super(bansModule, aliases, BansPerms.COMMAND_BANLIST, PunishmentType.BAN);
        this.setDescription(this.plugin.getMessage(BansLang.COMMAND_BANLIST_DESC));
    }
}
