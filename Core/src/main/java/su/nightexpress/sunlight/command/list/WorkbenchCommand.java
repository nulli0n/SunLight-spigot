package su.nightexpress.sunlight.command.list;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.CommandResult;
import su.nightexpress.sunlight.Perms;
import su.nightexpress.sunlight.Placeholders;
import su.nightexpress.sunlight.SunLight;
import su.nightexpress.sunlight.command.CommandFlags;
import su.nightexpress.sunlight.command.api.TargetCommand;
import su.nightexpress.sunlight.config.Lang;

public class WorkbenchCommand extends TargetCommand {

    public static final String NAME = "workbench";

    public WorkbenchCommand(@NotNull SunLight plugin, @NotNull String[] aliases) {
        super(plugin, aliases, Perms.COMMAND_WORKBENCH, Perms.COMMAND_WORKBENCH_OTHERS, 0);
        this.setDescription(plugin.getMessage(Lang.COMMAND_WORKBENCH_DESC));
        this.setUsage(plugin.getMessage(Lang.COMMAND_WORKBENCH_USAGE));
        this.addFlag(CommandFlags.SILENT);
    }

    @Override
    protected void onExecute(@NotNull CommandSender sender, @NotNull CommandResult result) {
        Player target = this.getCommandTarget(sender, result);
        if (target == null) return;

        target.openWorkbench(null, true);

        if (!result.hasFlag(CommandFlags.SILENT)) {
            plugin.getMessage(Lang.COMMAND_WORKBENCH_NOTIFY).send(target);
        }
        if (sender != target) {
            plugin.getMessage(Lang.COMMAND_WORKBENCH_TARGET)
                .replace(Placeholders.Player.replacer(target))
                .send(sender);
        }
    }
}
