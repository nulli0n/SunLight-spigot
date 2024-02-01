package su.nightexpress.sunlight.command.time;

import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.AbstractCommand;
import su.nexmedia.engine.api.command.CommandResult;
import su.nexmedia.engine.lang.LangManager;
import su.nexmedia.engine.utils.NumberUtil;
import su.nightexpress.sunlight.Perms;
import su.nightexpress.sunlight.Placeholders;
import su.nightexpress.sunlight.SunLight;
import su.nightexpress.sunlight.command.CommandFlags;
import su.nightexpress.sunlight.config.Config;
import su.nightexpress.sunlight.config.Lang;
import su.nightexpress.sunlight.utils.SunUtils;

import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

public class TimeSetCommand extends AbstractCommand<SunLight> {

    public static final String NAME = "set";

    public TimeSetCommand(@NotNull SunLight plugin) {
        super(plugin, new String[]{NAME}, Perms.COMMAND_TIME_SET);
        this.setDescription(plugin.getMessage(Lang.COMMAND_TIME_SET_DESC));
        this.setUsage(plugin.getMessage(Lang.COMMAND_TIME_SET_USAGE));
        this.addFlag(CommandFlags.WORLD);
    }

    @Override
    @NotNull
    public List<String> getTab(@NotNull Player player, int arg, @NotNull String[] args) {
        if (arg == 1) {
            return Arrays.asList("0", "6000", "12000", "18000", "24000");
        }
        return super.getTab(player, arg, args);
    }

    @Override
    protected void onExecute(@NotNull CommandSender sender, @NotNull CommandResult result) {
        if (result.length() < 2) {
            this.printUsage(sender);
            return;
        }

        World world;
        if (result.hasFlag(CommandFlags.WORLD)) {
            world = result.getFlag(CommandFlags.WORLD);
            if (world == null) {
                this.plugin.getMessage(Lang.ERROR_WORLD_INVALID).send(sender);
                return;
            }
        }
        else {
            if (!(sender instanceof Player player)) {
                this.printUsage(sender);
                return;
            }
            world = player.getWorld();
        }

        long ticks = Math.min(24000, Math.max(0, result.getInt(1, 0)));
        world.setTime(ticks);
        LocalTime localTime = SunUtils.getTimeOfTicks(world.getTime());

        plugin.getMessage(Lang.COMMAND_TIME_SET_DONE)
            .replace(Placeholders.GENERIC_WORLD, LangManager.getWorld(world))
            .replace(Placeholders.GENERIC_TIME, localTime.format(Config.GENERAL_TIME_FORMAT.get()))
            .replace(Placeholders.GENERIC_TOTAL, NumberUtil.format(ticks))
            .send(sender);
    }
}
