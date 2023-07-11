package su.nightexpress.sunlight.command.teleport;

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
import su.nightexpress.sunlight.utils.SunUtils;

public class TeleportSummonCommand extends TargetCommand {

    public static final String NAME = "summon";

    public TeleportSummonCommand(@NotNull SunLight plugin) {
        super(plugin, new String[]{NAME}, Perms.COMMAND_TELEPORT_SUMMON, Perms.COMMAND_TELEPORT_SUMMON, 1);
        this.setAllowDataLoad();
        this.setDescription(plugin.getMessage(Lang.COMMAND_TELEPORT_SUMMON_DESC));
        this.setUsage(plugin.getMessage(Lang.COMMAND_TELEPORT_SUMMON_USAGE));
        this.setPlayerOnly(true);
        this.addFlag(CommandFlags.SILENT);
    }

    @Override
    protected void onExecute(@NotNull CommandSender sender, @NotNull CommandResult result) {
        Player target = this.getCommandTarget(sender, result);
        if (target == null) return;

        if (target == sender) {
            plugin.getMessage(Lang.ERROR_COMMAND_SELF).send(sender);
            return;
        }

        Player player = (Player) sender;
        SunUtils.teleport(target, player);

        plugin.getMessage(Lang.COMMAND_TELEPORT_SUMMON_TARGET)
            .replace(Placeholders.forPlayer(target))
            .send(sender);

        if (!result.hasFlag(CommandFlags.SILENT)) {
            plugin.getMessage(Lang.COMMAND_TELEPORT_SUMMON_NOTIFY)
                .replace(Placeholders.forSender(sender))
                .send(target);
        }
    }
}
