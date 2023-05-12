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

public class TimeShowCommand extends AbstractCommand<SunLight> {

    public static final String NAME = "show";

    public static final String PLACEHOLDER_WORLD_TIME   = "%world_time%";
    public static final String PLACEHOLDER_WORLD_TICKS  = "%world_ticks%";
    public static final String PLACEHOLDER_PLAYER_TIME  = "%player_time%";
    public static final String PLACEHOLDER_PLAYER_TICKS = "%player_ticks%";
    public static final String PLACEHOLDER_SERVER_TIME  = "%server_time%";

    public TimeShowCommand(@NotNull SunLight plugin) {
        super(plugin, new String[]{NAME}, Perms.COMMAND_TIME_SHOW);
        this.setDescription(plugin.getMessage(Lang.COMMAND_TIME_SHOW_DESC));
        this.setUsage(plugin.getMessage(Lang.COMMAND_TIME_SHOW_USAGE));
        this.addFlag(CommandFlags.WORLD);
    }

    @Override
    protected void onExecute(@NotNull CommandSender sender, @NotNull CommandResult result) {
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

        long worldTicks = world.getTime();
        long playerTicks = sender instanceof Player player ? player.getPlayerTime() % 24000L : 0L;

        LocalTime worldTime = SunUtils.getTimeOfTicks(worldTicks);
        LocalTime playerTime = SunUtils.getTimeOfTicks(playerTicks);
        LocalTime serverTime = LocalTime.now();

        plugin.getMessage(Lang.COMMAND_TIME_SHOW_INFO)
            .replace(Placeholders.GENERIC_WORLD, LangManager.getWorld(world))
            .replace(PLACEHOLDER_WORLD_TIME, worldTime.format(Config.GENERAL_TIME_FORMAT.get()))
            .replace(PLACEHOLDER_WORLD_TICKS, NumberUtil.format(worldTicks))
            .replace(PLACEHOLDER_PLAYER_TIME, playerTime.format(Config.GENERAL_TIME_FORMAT.get()))
            .replace(PLACEHOLDER_PLAYER_TICKS, NumberUtil.format(playerTicks))
            .replace(PLACEHOLDER_SERVER_TIME, serverTime.format(Config.GENERAL_TIME_FORMAT.get()))
            .send(sender);
    }
}
