package su.nightexpress.sunlight.command.nick;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.CommandResult;
import su.nexmedia.engine.api.command.GeneralCommand;
import su.nexmedia.engine.api.config.JYML;
import su.nexmedia.engine.command.list.HelpSubCommand;
import su.nightexpress.sunlight.Perms;
import su.nightexpress.sunlight.SunLight;
import su.nightexpress.sunlight.config.Lang;

public class NickCommand extends GeneralCommand<SunLight> {

    public static final String NAME = "nick";

    public NickCommand(@NotNull SunLight plugin, @NotNull JYML cfg, @NotNull String[] aliases) {
        super(plugin, aliases, Perms.COMMAND_NICK);
        this.setDescription(plugin.getMessage(Lang.COMMAND_NICK_DESC));
        this.setUsage(plugin.getMessage(Lang.COMMAND_NICK_USAGE));

        this.addDefaultCommand(new HelpSubCommand<>(plugin));
        this.addChildren(new NickSetCommand(plugin));
        this.addChildren(new NickClearCommand(plugin));
        this.addChildren(new NickChangeCommand(plugin, cfg));
    }

    @Override
    protected void onExecute(@NotNull CommandSender sender, @NotNull CommandResult result) {

    }
}
