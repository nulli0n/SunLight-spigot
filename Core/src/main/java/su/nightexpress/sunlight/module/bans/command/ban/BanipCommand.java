package su.nightexpress.sunlight.module.bans.command.ban;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.utils.regex.RegexUtil;
import su.nightexpress.sunlight.data.impl.SunUser;
import su.nightexpress.sunlight.module.bans.BansModule;
import su.nightexpress.sunlight.module.bans.command.api.AbstractPunishCommand;
import su.nightexpress.sunlight.module.bans.config.BansLang;
import su.nightexpress.sunlight.module.bans.punishment.PunishmentType;
import su.nightexpress.sunlight.module.bans.util.BansPerms;
import su.nightexpress.sunlight.module.bans.util.Placeholders;

public class BanipCommand extends AbstractPunishCommand {

    public static final String NAME = "banip";

    public BanipCommand(@NotNull BansModule bansModule, @NotNull String[] aliases) {
        super(bansModule, aliases, BansPerms.COMMAND_BANIP, PunishmentType.BAN);
        this.setDescription(plugin.getMessage(BansLang.COMMAND_BANIP_DESC));
        this.setUsage(plugin.getMessage(BansLang.COMMAND_BANIP_USAGE));
    }

    @Override
    @NotNull
    protected String fineUserName(@NotNull CommandSender sender, @NotNull String userName) {
        SunUser user = plugin.getUserManager().getUserData(userName);
        if (user != null) {
            userName = user.getIp();
        }
        return super.fineUserName(sender, userName);
    }

    @Override
    protected boolean checkUserName(@NotNull CommandSender sender, @NotNull String userName) {
        if (!RegexUtil.isIpAddress(userName)) {
            plugin.getMessage(BansLang.PUNISHMENT_ERROR_NOT_IP).replace(Placeholders.GENERIC_USER, userName).send(sender);
            return false;
        }
        return true;
    }
}
