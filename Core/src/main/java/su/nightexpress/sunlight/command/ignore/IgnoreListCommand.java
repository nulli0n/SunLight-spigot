package su.nightexpress.sunlight.command.ignore;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.CommandResult;
import su.nightexpress.sunlight.Perms;
import su.nightexpress.sunlight.SunLight;
import su.nightexpress.sunlight.command.api.TargetCommand;
import su.nightexpress.sunlight.config.Lang;

public class IgnoreListCommand extends TargetCommand {

    public static final String NAME = "list";

    public IgnoreListCommand(@NotNull SunLight plugin) {
        super(plugin, new String[]{NAME}, Perms.COMMAND_IGNORE, Perms.COMMAND_IGNORE_OTHERS, 1);
        this.setAllowDataLoad();
        this.setDescription(plugin.getMessage(Lang.COMMAND_IGNORE_LIST_DESC));
        this.setUsage(plugin.getMessage(Lang.COMMAND_IGNORE_LIST_USAGE));
        this.setPlayerOnly(true);
    }

    @Override
    protected void onExecute(@NotNull CommandSender sender, @NotNull CommandResult result) {
        Player target = this.getCommandTarget(sender, result);
        if (target == null) return;

        Player player = (Player) sender;
        if (sender != target) {
            plugin.getUserManager().getIgnoreListMenu().open(player, target.getUniqueId());
        }
        else {
            plugin.getUserManager().getIgnoreListMenu().open(player, 1);
        }
    }
}
