package su.nightexpress.sunlight.command.teleport;

import org.bukkit.Location;
import org.bukkit.World;
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

import java.util.Collections;
import java.util.List;

public class TeleportLocationCommand extends TargetCommand {

    public static final String NAME = "location";

    public TeleportLocationCommand(@NotNull SunLight plugin) {
        super(plugin, new String[]{NAME}, Perms.COMMAND_TELEPORT_LOCATION, Perms.COMMAND_TELEPORT_LOCATION_OTHERS, 4);
        this.setAllowDataLoad();
        this.setDescription(plugin.getMessage(Lang.COMMAND_TELEPORT_LOCATION_DESC));
        this.setUsage(plugin.getMessage(Lang.COMMAND_TELEPORT_LOCATION_USAGE));
        this.addFlag(CommandFlags.WORLD, CommandFlags.SILENT);
    }

    @Override
    @NotNull
    public List<String> getTab(@NotNull Player player, int arg, @NotNull String[] args) {
        if (arg == 1) {
            return Collections.singletonList("<x>");
        }
        if (arg == 2) {
            return Collections.singletonList("<y>");
        }
        if (arg == 3) {
            return Collections.singletonList("<z>");
        }
        return super.getTab(player, arg, args);
    }

    @Override
    protected void onExecute(@NotNull CommandSender sender, @NotNull CommandResult result) {
        if (result.length() < 4) {
            this.printUsage(sender);
            return;
        }

        Player target = this.getCommandTarget(sender, result);
        if (target == null) return;

        World world;
        if (result.hasFlag(CommandFlags.WORLD)) {
            world = result.getFlag(CommandFlags.WORLD);

            if (world == null) {
                this.plugin.getMessage(Lang.ERROR_WORLD_INVALID).send(sender);
                return;
            }
        }
        else world = target.getWorld();

        double x = result.getDouble(1, 0);
        double y = result.getDouble(2, 0);
        double z = result.getDouble(3, 0);

        Location targetLoc = target.getLocation();
        Location location = new Location(world, x, y, z, targetLoc.getYaw(), targetLoc.getPitch());
        SunUtils.teleport(target, location);

        if (sender != target) {
            plugin.getMessage(Lang.COMMAND_TELEPORT_LOCATION_DONE_TARGET)
                .replace(Placeholders.forPlayer(target))
                .replace(Placeholders.forLocation(location))
                .send(sender);
        }
        if (!result.hasFlag(CommandFlags.SILENT)) {
            plugin.getMessage(Lang.COMMAND_TELEPORT_LOCATION_DONE_NOTIFY)
                .replace(Placeholders.forSender(sender))
                .replace(Placeholders.forLocation(location))
                .send(target);
        }
    }
}
