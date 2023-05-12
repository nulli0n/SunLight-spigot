package su.nightexpress.sunlight.command.ignore;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.CommandResult;
import su.nexmedia.engine.api.command.GeneralCommand;
import su.nexmedia.engine.command.list.HelpSubCommand;
import su.nightexpress.sunlight.Perms;
import su.nightexpress.sunlight.SunLight;
import su.nightexpress.sunlight.config.Lang;

public class IgnoreCommand extends GeneralCommand<SunLight> {

    public static final String NAME = "ignore";

    public IgnoreCommand(@NotNull SunLight plugin, @NotNull String[] aliases) {
        super(plugin, aliases, Perms.COMMAND_IGNORE);
        this.setDescription(plugin.getMessage(Lang.COMMAND_IGNORE_DESC));
        this.setUsage(plugin.getMessage(Lang.COMMAND_IGNORE_USAGE));
        this.setPlayerOnly(true);

        this.addDefaultCommand(new HelpSubCommand<>(plugin));
        this.addChildren(new IgnoreAddCommand(plugin));
        this.addChildren(new IgnoreRemoveCommand(plugin));
        this.addChildren(new IgnoreListCommand(plugin));
    }

    @Override
    protected void onExecute(@NotNull CommandSender sender, @NotNull CommandResult result) {

    }
}
