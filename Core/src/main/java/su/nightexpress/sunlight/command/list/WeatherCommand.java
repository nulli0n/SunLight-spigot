package su.nightexpress.sunlight.command.list;

import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.CommandResult;
import su.nexmedia.engine.api.command.GeneralCommand;
import su.nexmedia.engine.lang.LangManager;
import su.nexmedia.engine.utils.CollectionsUtil;
import su.nexmedia.engine.utils.StringUtil;
import su.nightexpress.sunlight.Perms;
import su.nightexpress.sunlight.Placeholders;
import su.nightexpress.sunlight.SunLight;
import su.nightexpress.sunlight.command.CommandFlags;
import su.nightexpress.sunlight.config.Lang;

import java.util.List;
import java.util.function.Consumer;

public class WeatherCommand extends GeneralCommand<SunLight> {

    public static final String NAME = "weather";

    public WeatherCommand(@NotNull SunLight plugin, @NotNull String[] aliases) {
        super(plugin, aliases, Perms.COMMAND_WEATHER);
        this.setDescription(plugin.getMessage(Lang.COMMAND_WEATHER_DESC));
        this.setUsage(plugin.getMessage(Lang.COMMAND_WEATHER_USAGE));
        this.addFlag(CommandFlags.WORLD);
    }

    public enum WeatherType {
        CLEAR(world -> {
            world.setStorm(false);
            world.setThundering(false);
        }),
        STORM(world -> {
            world.setThundering(false);
            world.setStorm(true);
        }),
        THUNDER(world -> {
            world.setStorm(true);
            world.setThundering(true);
        });

        private final Consumer<World> consumer;

        WeatherType(@NotNull Consumer<World> consumer) {
            this.consumer = consumer;
        }

        public void apply(@NotNull World world) {
            this.consumer.accept(world);
        }
    }

    @Override
    @NotNull
    public List<String> getTab(@NotNull Player player, int arg, @NotNull String[] args) {
        if (arg == 1) {
            return CollectionsUtil.getEnumsList(WeatherType.class).stream().map(String::toLowerCase).toList();
        }
        return super.getTab(player, arg, args);
    }

    @Override
    protected void onExecute(@NotNull CommandSender sender, @NotNull CommandResult result) {
        if (result.length() < 1) {
            this.printUsage(sender);
            return;
        }

        World world;
        if (sender instanceof Player player && !result.hasFlag(CommandFlags.WORLD)) {
            world = player.getWorld();
        }
        else world = result.getFlag(CommandFlags.WORLD);

        if (world == null) {
            plugin.getMessage(Lang.ERROR_WORLD_INVALID).send(sender);
            return;
        }

        WeatherType type = StringUtil.getEnum(result.getArg(0), WeatherType.class).orElse(WeatherType.CLEAR);
        type.apply(world);

        plugin.getMessage(Lang.COMMAND_WEATHER_SET)
            .replace(Placeholders.GENERIC_TYPE, plugin.getLangManager().getEnum(type))
            .replace(Placeholders.GENERIC_WORLD, LangManager.getWorld(world))
            .send(sender);
    }
}
