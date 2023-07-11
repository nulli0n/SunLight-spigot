package su.nightexpress.sunlight.command.list;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.CommandResult;
import su.nexmedia.engine.utils.Placeholders;
import su.nightexpress.sunlight.Perms;
import su.nightexpress.sunlight.SunLight;
import su.nightexpress.sunlight.command.CommandFlags;
import su.nightexpress.sunlight.command.api.TargetCommand;
import su.nightexpress.sunlight.config.Lang;

public class ExtinguishCommand extends TargetCommand {

    public static final String NAME = "extinguish";

    public ExtinguishCommand(@NotNull SunLight plugin, @NotNull String[] aliases) {
        super(plugin, aliases, Perms.COMMAND_EXTINGUISH, Perms.COMMAND_EXTINGUISH_OTHERS, 0);
        this.setAllowDataLoad();
        this.setDescription(plugin.getMessage(Lang.COMMAND_EXTINGUISH_DESC));
        this.setUsage(plugin.getMessage(Lang.COMMAND_EXTINGUISH_USAGE));
        this.addFlag(CommandFlags.SILENT);
    }

    @Override
    protected void onExecute(@NotNull CommandSender sender, @NotNull CommandResult result) {
        Player target = this.getCommandTarget(sender, result);
        if (target == null) return;

        target.setFireTicks(0);
        if (!target.isOnline()) target.saveData();

        if (sender != target) {
            plugin.getMessage(Lang.COMMAND_EXTINGUISH_TARGET).replace(Placeholders.forPlayer(target)).send(sender);
        }
        if (!result.hasFlag(CommandFlags.SILENT)) {
            plugin.getMessage(Lang.COMMAND_EXTINGUISH_NOTIFY).send(target);
        }
    }
}
