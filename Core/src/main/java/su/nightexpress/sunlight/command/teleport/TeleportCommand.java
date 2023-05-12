package su.nightexpress.sunlight.command.teleport;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.CommandResult;
import su.nexmedia.engine.api.command.GeneralCommand;
import su.nexmedia.engine.api.config.JYML;
import su.nexmedia.engine.command.list.HelpSubCommand;
import su.nightexpress.sunlight.Perms;
import su.nightexpress.sunlight.SunLight;
import su.nightexpress.sunlight.config.Lang;

public class TeleportCommand extends GeneralCommand<SunLight> {

    public static final String NAME = "teleport";

    public TeleportCommand(@NotNull SunLight plugin, @NotNull JYML cfg, @NotNull String[] aliases) {
        super(plugin, aliases, Perms.COMMAND_TELEPORT);
        this.setDescription(plugin.getMessage(Lang.COMMAND_TELEPORT_DESC));
        this.setUsage(plugin.getMessage(Lang.COMMAND_TELEPORT_USAGE));

        this.addDefaultCommand(new HelpSubCommand<>(plugin));
        this.addChildren(new TeleportAcceptCommand(plugin));
        this.addChildren(new TeleportDeclineCommand(plugin));
        this.addChildren(new TeleportInviteCommand(plugin, cfg));
        this.addChildren(new TeleportLocationCommand(plugin));
        this.addChildren(new TeleportRequestCommand(plugin, cfg));
        this.addChildren(new TeleportToggleCommand(plugin));
        this.addChildren(new TeleportSummonCommand(plugin));
        this.addChildren(new TeleportToCommand(plugin));
        this.addChildren(new TeleportSendCommand(plugin));
        this.addChildren(new TeleportTopCommand(plugin));
    }

    @Override
    protected void onExecute(@NotNull CommandSender sender, @NotNull CommandResult result) {

    }
}
