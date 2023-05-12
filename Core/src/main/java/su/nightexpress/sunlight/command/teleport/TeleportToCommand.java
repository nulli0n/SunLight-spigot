package su.nightexpress.sunlight.command.teleport;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.CommandResult;
import su.nightexpress.sunlight.Perms;
import su.nightexpress.sunlight.Placeholders;
import su.nightexpress.sunlight.SunLight;
import su.nightexpress.sunlight.command.api.TargetCommand;
import su.nightexpress.sunlight.config.Lang;

public class TeleportToCommand extends TargetCommand {

    public static final String NAME = "to";

    public TeleportToCommand(@NotNull SunLight plugin) {
        super(plugin, new String[]{NAME}, Perms.COMMAND_TELEPORT_TO, Perms.COMMAND_TELEPORT_TO, 1);
        this.setAllowDataLoad();
        this.setDescription(plugin.getMessage(Lang.COMMAND_TELEPORT_TO_DESC));
        this.setUsage(plugin.getMessage(Lang.COMMAND_TELEPORT_TO_USAGE));
        this.setPlayerOnly(true);
    }

    @Override
    protected void onExecute(@NotNull CommandSender sender, @NotNull CommandResult result) {
        if (result.length() < 2) {
            this.printUsage(sender);
            return;
        }

        Player target = this.getCommandTarget(sender, result);
        if (target == null) return;

        if (target == sender) {
            this.plugin.getMessage(Lang.ERROR_COMMAND_SELF).send(sender);
            return;
        }

        Player player = (Player) sender;
        player.teleport(target);

        plugin.getMessage(Lang.COMMAND_TELEPORT_TO_DONE)
            .replace(Placeholders.Player.replacer(target))
            .send(sender);
    }
}
