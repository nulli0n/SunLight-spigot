package su.nightexpress.sunlight.module.bans.command.warn;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.sunlight.data.impl.SunUser;
import su.nightexpress.sunlight.module.bans.BansModule;
import su.nightexpress.sunlight.module.bans.command.AbstractPunishCommand;
import su.nightexpress.sunlight.module.bans.config.BansLang;
import su.nightexpress.sunlight.module.bans.punishment.PunishmentType;
import su.nightexpress.sunlight.module.bans.config.BansPerms;

public class WarnCommand extends AbstractPunishCommand {

    public static final String NAME = "warn";

    public WarnCommand(@NotNull BansModule bansModule, @NotNull String[] aliases) {
        super(bansModule, aliases, BansPerms.COMMAND_WARN, PunishmentType.WARN);
        this.setDescription(plugin.getMessage(BansLang.COMMAND_WARN_DESC));
        this.setUsage(plugin.getMessage(BansLang.COMMAND_WARN_USAGE));
    }

    @Override
    protected boolean checkUserName(@NotNull CommandSender sender, @NotNull String userName) {
        SunUser user = plugin.getUserManager().getUserData(userName);
        if (user == null) {
            this.errorPlayer(sender);
            return false;
        }
        return super.checkUserName(sender, userName);
    }
}
