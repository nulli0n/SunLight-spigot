package su.nightexpress.sunlight.command.time;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.CommandResult;
import su.nexmedia.engine.api.command.GeneralCommand;
import su.nexmedia.engine.api.config.JYML;
import su.nexmedia.engine.command.list.HelpSubCommand;
import su.nightexpress.sunlight.Perms;
import su.nightexpress.sunlight.SunLight;
import su.nightexpress.sunlight.config.Lang;

public class TimeCommand extends GeneralCommand<SunLight> {

    public static final String NAME = "time";

    public TimeCommand(@NotNull SunLight plugin, @NotNull JYML cfg, @NotNull String[] aliases) {
        super(plugin, aliases, Perms.COMMAND_TIME);
        this.setDescription(plugin.getMessage(Lang.COMMAND_TIME_DESC));
        this.setUsage(plugin.getMessage(Lang.COMMAND_TIME_USAGE));

        this.addChildren(new HelpSubCommand<>(plugin));
        this.addDefaultCommand(new TimeShowCommand(plugin));
        this.addChildren(new TimeSetCommand(plugin));
        this.addChildren(new TimePersonalCommand(plugin));
    }

    @Override
    protected void onExecute(@NotNull CommandSender sender, @NotNull CommandResult result) {

    }
}
