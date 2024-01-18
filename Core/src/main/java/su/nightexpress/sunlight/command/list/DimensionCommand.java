package su.nightexpress.sunlight.command.list;

import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.CommandResult;
import su.nexmedia.engine.lang.LangManager;
import su.nexmedia.engine.utils.CollectionsUtil;
import su.nightexpress.sunlight.Perms;
import su.nightexpress.sunlight.Placeholders;
import su.nightexpress.sunlight.SunLight;
import su.nightexpress.sunlight.command.CommandFlags;
import su.nightexpress.sunlight.command.api.TargetCommand;
import su.nightexpress.sunlight.config.Lang;
import su.nightexpress.sunlight.utils.SunUtils;

import java.util.List;

public class DimensionCommand extends TargetCommand {

    public static final String NAME = "dimension";

    public DimensionCommand(@NotNull SunLight plugin, @NotNull String[] aliases) {
        super(plugin, aliases, Perms.COMMAND_DIMENSION, Perms.COMMAND_DIMENSION_OTHERS, 1);
        this.setAllowDataLoad();
        this.setDescription(plugin.getMessage(Lang.COMMAND_DIMENSION_DESC));
        this.setUsage(plugin.getMessage(Lang.COMMAND_DIMENSION_USAGE));
        this.addFlag(CommandFlags.SILENT);
    }

    @Override
    @NotNull
    public List<String> getTab(@NotNull Player player, int arg, @NotNull String[] args) {
        if (arg == 1) {
            return CollectionsUtil.worldNames();
        }
        return super.getTab(player, arg, args);
    }

    @Override
    protected void onExecute(@NotNull CommandSender sender, @NotNull CommandResult result) {
        if (result.length() < 1) {
            this.printUsage(sender);
            return;
        }

        Player target = this.getCommandTarget(sender, result);
        if (target == null) return;

        World world = plugin.getServer().getWorld(result.getArg(0));
        if (world == null) {
            plugin.getMessage(Lang.ERROR_WORLD_INVALID).send(sender);
            return;
        }

        SunUtils.teleport(target, world.getSpawnLocation());
        if (sender != target) {
            plugin.getMessage(Lang.COMMAND_DIMENSION_TARGET)
                .replace(Placeholders.forPlayer(target))
                .replace(Placeholders.GENERIC_WORLD, LangManager.getWorld(world))
                .send(sender);
        }
        if (!result.hasFlag(CommandFlags.SILENT)) {
            plugin.getMessage(Lang.COMMAND_DIMENSION_NOTIFY)
                .replace(Placeholders.GENERIC_WORLD, LangManager.getWorld(world))
                .send(target);
        }
    }
}
