package su.nightexpress.sunlight.command.teleport;

import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.config.JYML;
import su.nexmedia.engine.api.lang.LangMessage;
import su.nightexpress.sunlight.Perms;
import su.nightexpress.sunlight.SunLight;
import su.nightexpress.sunlight.config.Lang;

public class TeleportInviteCommand extends AbstractRequestCommand {

    public static final String NAME = "invite";

    public TeleportInviteCommand(@NotNull SunLight plugin, @NotNull JYML cfg) {
        super(plugin, cfg, new String[]{NAME}, Perms.COMMAND_TELEPORT_INVITE);
        this.setDescription(plugin.getMessage(Lang.COMMAND_TELEPORT_INVITE_DESC));
        this.setUsage(plugin.getMessage(Lang.COMMAND_TELEPORT_INVITE_USAGE));
        this.setPlayerOnly(true);
    }

    @Override
    public boolean isSummon() {
        return true;
    }

    @Override
    @NotNull
    public LangMessage getMessageForSender() {
        return plugin.getMessage(Lang.COMMAND_TELEPORT_INVITE_NOTIFY_SENDER);
    }

    @Override
    @NotNull
    public LangMessage getMessageForTarget() {
        return plugin.getMessage(Lang.COMMAND_TELEPORT_INVITE_NOTIFY_TARGET);
    }
}
