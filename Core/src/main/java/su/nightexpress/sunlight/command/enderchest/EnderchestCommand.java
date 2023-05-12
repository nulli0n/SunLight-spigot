package su.nightexpress.sunlight.command.enderchest;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.CommandResult;
import su.nexmedia.engine.api.command.GeneralCommand;
import su.nexmedia.engine.command.list.HelpSubCommand;
import su.nightexpress.sunlight.Perms;
import su.nightexpress.sunlight.SunLight;
import su.nightexpress.sunlight.config.Lang;

public class EnderchestCommand extends GeneralCommand<SunLight> {

    public static final String NAME = "enderchest";

    public EnderchestCommand(@NotNull SunLight plugin, @NotNull String[] aliases) {
        super(plugin, aliases, Perms.COMMAND_ENDERCHEST);
        this.setDescription(plugin.getMessage(Lang.COMMAND_ENDERCHEST_DESC));
        this.setUsage(plugin.getMessage(Lang.COMMAND_ENDERCHEST_USAGE));

        this.addDefaultCommand(new HelpSubCommand<>(plugin));
        this.addChildren(new EnderchestClearCommand(plugin));
        this.addChildren(new EnderchestCopyCommand(plugin));
        this.addChildren(new EnderchestFillCommand(plugin));
        this.addChildren(new EnderchestOpenCommand(plugin));
        this.addChildren(new EnderchestRepairCommand(plugin));
    }

    @Override
    protected void onExecute(@NotNull CommandSender sender, @NotNull CommandResult result) {

    }
}
