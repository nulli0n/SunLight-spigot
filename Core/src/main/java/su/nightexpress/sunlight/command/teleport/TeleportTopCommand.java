package su.nightexpress.sunlight.command.teleport;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.CommandResult;
import su.nexmedia.engine.utils.LocationUtil;
import su.nightexpress.sunlight.Perms;
import su.nightexpress.sunlight.Placeholders;
import su.nightexpress.sunlight.SunLight;
import su.nightexpress.sunlight.command.CommandFlags;
import su.nightexpress.sunlight.command.api.TargetCommand;
import su.nightexpress.sunlight.config.Lang;
import su.nightexpress.sunlight.utils.SunUtils;

public class TeleportTopCommand extends TargetCommand {

    public static final String NAME = "top";

    public TeleportTopCommand(@NotNull SunLight plugin) {
        super(plugin, new String[]{NAME}, Perms.COMMAND_TELEPORT_TOP, Perms.COMMAND_TELEPORT_TOP_OTHERS, 1);
        this.setAllowDataLoad();
        this.setDescription(plugin.getMessage(Lang.COMMAND_TELEPORT_TOP_DESC));
        this.setUsage(plugin.getMessage(Lang.COMMAND_TELEPORT_TOP_USAGE));
        this.addFlag(CommandFlags.SILENT);
    }

    @Override
    protected void onExecute(@NotNull CommandSender sender, @NotNull CommandResult result) {
        Player target = this.getCommandTarget(sender, result);
        if (target == null) return;

        Block bHighest = target.getWorld().getHighestBlockAt(target.getLocation()).getRelative(BlockFace.UP);
        Location location = LocationUtil.getCenter(bHighest.getLocation(), false);
        location.setYaw(target.getEyeLocation().getYaw());
        location.setPitch(target.getEyeLocation().getPitch());
        SunUtils.teleport(target, location);

        if (sender != target) {
            plugin.getMessage(Lang.COMMAND_TELEPORT_TOP_TARGET)
                .replace(Placeholders.forPlayer(target))
                .send(sender);
        }
        if (!result.hasFlag(CommandFlags.SILENT)) {
            plugin.getMessage(Lang.COMMAND_TELEPORT_TOP_NOTIFY)
                .replace(Placeholders.forSender(sender))
                .send(target);
        }
    }
}
