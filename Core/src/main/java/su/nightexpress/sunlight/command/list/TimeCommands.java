package su.nightexpress.sunlight.command.list;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.command.experimental.CommandContext;
import su.nightexpress.nightcore.command.experimental.RootCommand;
import su.nightexpress.nightcore.command.experimental.ServerCommand;
import su.nightexpress.nightcore.command.experimental.argument.ArgumentTypes;
import su.nightexpress.nightcore.command.experimental.argument.ParsedArguments;
import su.nightexpress.nightcore.command.experimental.builder.DirectNodeBuilder;
import su.nightexpress.nightcore.command.experimental.node.DirectNode;
import su.nightexpress.nightcore.config.ConfigValue;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.language.LangAssets;
import su.nightexpress.nightcore.util.NumberUtil;
import su.nightexpress.sunlight.Placeholders;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.command.*;
import su.nightexpress.sunlight.command.template.CommandTemplate;
import su.nightexpress.sunlight.config.Config;
import su.nightexpress.sunlight.config.Lang;
import su.nightexpress.sunlight.utils.SunUtils;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

public class TimeCommands {

    public static final String NODE_SET  = "time_set";
    public static final String NODE_SHOW = "time_show";

    public static final String NODE_PERSONAL_SET   = "time_personal_set";
    public static final String NODE_PERSONAL_RESET = "time_personal_reset";

    //private static final Function<String, String> NODE_SET_DIRECT = name -> "time_set_" + name.toLowerCase();

    private static final long MODIFIER = 1000L;
    private static final long MAX_TICKS = 24L * MODIFIER;
    private static final long MIN_TICKS = 0L;

    private static Map<String, Long> timeMap;

    public static void load(@NotNull SunLightPlugin plugin, @NotNull FileConfig fileConfig) {
        timeMap = ConfigValue.forMap("Settings.Time.Shortcuts",
            (cfg, path, sId) -> cfg.getLong(path + "." + sId),
            (cfg, path, map) -> map.forEach((name, ticks) -> cfg.set(path + "." + name, ticks)),
            () -> {
                Map<String, Long> map = new HashMap<>();
                map.put("day", 1000L);
                map.put("noon", 6000L);
                map.put("sunset", 12000L);
                map.put("night", 13000L);
                map.put("midnight", 18000L);
                map.put("sunrise", 23000L);
                return map;
            },
            "In this section you can create custom shortcuts for the '" + NODE_SET + "' command.",
            "In other words, it will create one-shot time set commands with specified name and time value."
            ).read(fileConfig);

        CommandRegistry.registerDirectExecutor(NODE_SHOW, (template, config) -> builderShow(plugin, template, config));
        CommandRegistry.registerDirectExecutor(NODE_SET, (template, config) -> builderSet(plugin, template, config));

        CommandRegistry.registerDirectExecutor(NODE_PERSONAL_SET, (template, config) -> builderSetPersonal(plugin, template, config));
        CommandRegistry.registerDirectExecutor(NODE_PERSONAL_RESET, (template, config) -> builderResetPersonal(plugin, template, config));

        CommandRegistry.addTemplate("time", CommandTemplate.group(new String[]{"time"},
            "Time commands.",
            CommandPerms.PREFIX + "time",
            CommandTemplate.direct(new String[]{"set"}, NODE_SET),
            CommandTemplate.direct(new String[]{"show"}, NODE_SHOW)
        ));

        CommandRegistry.addTemplate("time_personal", CommandTemplate.group(new String[]{"ptime"},
            "Personal Time commands.",
            CommandPerms.PREFIX + "ptime",
            CommandTemplate.direct(new String[]{"set"}, NODE_PERSONAL_SET),
            CommandTemplate.direct(new String[]{"reset"}, NODE_PERSONAL_RESET)
        ));

        timeMap.forEach((name, ticks) -> {
//            String node = NODE_SET_DIRECT.apply(name);
//            CommandRegistry.registerDirectExecutor(node, (template, config) -> builderSetDirect(plugin, template, config, ticks));
//            CommandRegistry.addTemplate(node, CommandTemplate.direct(new String[]{name}, node));

            ServerCommand command = RootCommand.direct(plugin, name, builder -> builder
                .description(Lang.COMMAND_TIME_SET_DESC)
                .permission(CommandPerms.TIME_SET)
                .withArgument(ArgumentTypes.world(CommandArguments.WORLD))
                .executes((context, arguments) -> executeSet(plugin, context, arguments, ticks))
            );
            CommandRegistry.register(plugin, command);
        });
    }

    private static long parseTicks(@NotNull String input) {
        return timeMap.getOrDefault(input.toLowerCase(), (long) NumberUtil.getInteger(input, 0));
    }

    private static long clamp(long ticks) {
        return Math.clamp(ticks, MIN_TICKS, MAX_TICKS);
    }



    @NotNull
    public static DirectNodeBuilder builderSet(@NotNull SunLightPlugin plugin, @NotNull CommandTemplate template, @NotNull FileConfig config) {
        return DirectNode.builder(plugin, template.getAliases())
            .description(Lang.COMMAND_TIME_SET_DESC)
            .permission(CommandPerms.TIME_SET)
            .withArgument(ArgumentTypes.string(CommandArguments.TIME).required().withSamples(context -> {
                List<String> list = new ArrayList<>(IntStream.range(0, 25).boxed().map(hour -> hour * MODIFIER).map(String::valueOf).toList());
                list.addAll(0, timeMap.keySet());
                return list;
            }))
            .withArgument(ArgumentTypes.world(CommandArguments.WORLD))
            .executes((context, arguments) -> executeSet(plugin, context, arguments, 0L))
            ;
    }

    @NotNull
    public static DirectNodeBuilder builderSetDirect(@NotNull SunLightPlugin plugin, @NotNull CommandTemplate template, @NotNull FileConfig config, long ticks) {
        return DirectNode.builder(plugin, template.getAliases())
            .description(Lang.COMMAND_TIME_SET_DESC)
            .permission(CommandPerms.TIME_SET)
            .withArgument(ArgumentTypes.world(CommandArguments.WORLD))
            .executes((context, arguments) -> executeSet(plugin, context, arguments, ticks))
            ;
    }

    public static boolean executeSet(@NotNull SunLightPlugin plugin, @NotNull CommandContext context, @NotNull ParsedArguments arguments, long ticks) {
        World world = CommandTools.getWorld(plugin, context, arguments, CommandArguments.WORLD);
        if (world == null) return false;

        if (arguments.hasArgument(CommandArguments.TIME)) {
            ticks = parseTicks(arguments.getStringArgument(CommandArguments.TIME));
        }

        ticks = clamp(ticks);
        world.setTime(ticks);
        LocalTime localTime = SunUtils.getTimeOfTicks(world.getTime());

        context.send(Lang.COMMAND_TIME_SET_DONE.getMessage()
            .replace(Placeholders.GENERIC_WORLD, LangAssets.get(world))
            .replace(Placeholders.GENERIC_TIME, localTime.format(Config.GENERAL_TIME_FORMAT.get()))
            .replace(Placeholders.GENERIC_TOTAL, NumberUtil.format(ticks))
        );
        return true;
    }



    @NotNull
    public static DirectNodeBuilder builderShow(@NotNull SunLightPlugin plugin, @NotNull CommandTemplate template, @NotNull FileConfig config) {
        return DirectNode.builder(plugin, template.getAliases())
            .description(Lang.COMMAND_TIME_SHOW_DESC)
            .permission(CommandPerms.TIME_SHOW)
            .withArgument(ArgumentTypes.world(CommandArguments.WORLD))
            .executes((context, arguments) -> executeShow(plugin, context, arguments))
            ;
    }

    public static boolean executeShow(@NotNull SunLightPlugin plugin, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        World world = CommandTools.getWorld(plugin, context, arguments, CommandArguments.WORLD);
        if (world == null) return false;

        Player player = context.getExecutor();

        long worldTicks = world.getTime();
        LocalTime worldTime = SunUtils.getTimeOfTicks(worldTicks);
        LocalTime serverTime = LocalTime.now();

        context.send(Lang.COMMAND_TIME_SHOW_INFO.getMessage()
            .replace(Placeholders.GENERIC_WORLD, LangAssets.get(world))
            .replace(Placeholders.GENERIC_TIME, SunUtils.formatTime(worldTime))
            .replace(Placeholders.GENERIC_TICKS, NumberUtil.format(worldTicks))
            .replace(Placeholders.GENERIC_GLOBAL, SunUtils.formatTime(serverTime))
        );
        return true;
    }



    @NotNull
    public static DirectNodeBuilder builderSetPersonal(@NotNull SunLightPlugin plugin, @NotNull CommandTemplate template, @NotNull FileConfig config) {
        return DirectNode.builder(plugin, template.getAliases())
            .description(Lang.COMMAND_TIME_PERSONAL_SET_DESC)
            .permission(CommandPerms.TIME_PERSONAL_SET)
            .withArgument(ArgumentTypes.string(CommandArguments.TIME).required().withSamples(context -> {
                List<String> list = new ArrayList<>(IntStream.range(0, 25).boxed().map(hour -> hour * MODIFIER).map(String::valueOf).toList());
                list.addAll(0, timeMap.keySet());
                return list;
            }))
            .withArgument(ArgumentTypes.playerName(CommandArguments.PLAYER).permission(CommandPerms.TIME_PERSONAL_SET_OTHERS))
            .withFlag(CommandFlags.silent().permission(CommandPerms.TIME_PERSONAL_SET_OTHERS))
            .executes((context, arguments) -> executeSetPersonal(plugin, context, arguments))
            ;
    }

    public static boolean executeSetPersonal(@NotNull SunLightPlugin plugin, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Player target = CommandTools.getTarget(plugin, context, arguments, CommandArguments.PLAYER, false); // Personal time is not persistent.
        if (target == null) return false;

        String input = arguments.getStringArgument(CommandArguments.TIME);
        long ticks = clamp(parseTicks(input));

        target.setPlayerTime(ticks, true);
        long totalTicks = target.getPlayerTime() % MAX_TICKS;
        LocalTime time = SunUtils.getTimeOfTicks(totalTicks);

        if (context.getSender() != target) {
            Lang.COMMAND_TIME_PERSONAL_SET_DONE.getMessage()
                .replace(Placeholders.forPlayer(target))
                .replace(Placeholders.GENERIC_TIME, time.format(Config.GENERAL_TIME_FORMAT.get()))
                .replace(Placeholders.GENERIC_TOTAL, NumberUtil.format(totalTicks))
                .send(context.getSender());
        }

        if (!arguments.hasFlag(CommandFlags.SILENT)) {
            Lang.COMMAND_TIME_PERSONAL_SET_NOTIFY.getMessage()
                .replace(Placeholders.GENERIC_TIME, time.format(Config.GENERAL_TIME_FORMAT.get()))
                .replace(Placeholders.GENERIC_TOTAL, NumberUtil.format(totalTicks))
                .send(target);
        }

        return true;
    }



    @NotNull
    public static DirectNodeBuilder builderResetPersonal(@NotNull SunLightPlugin plugin, @NotNull CommandTemplate template, @NotNull FileConfig config) {
        return DirectNode.builder(plugin, template.getAliases())
            .description(Lang.COMMAND_TIME_PERSONAL_RESET_DESC)
            .permission(CommandPerms.TIME_PERSONAL_RESET)
            .withArgument(ArgumentTypes.playerName(CommandArguments.PLAYER).permission(CommandPerms.TIME_PERSONAL_RESET_OTHERS))
            .withFlag(CommandFlags.silent().permission(CommandPerms.TIME_PERSONAL_RESET_OTHERS))
            .executes((context, arguments) -> executeResetPersonal(plugin, context, arguments))
            ;
    }

    public static boolean executeResetPersonal(@NotNull SunLightPlugin plugin, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Player target = CommandTools.getTarget(plugin, context, arguments, CommandArguments.PLAYER, false); // Personal time is not persistent.
        if (target == null) return false;

        target.resetPlayerTime();

        if (context.getSender() != target) {
            Lang.COMMAND_TIME_PERSONAL_RESET_DONE.getMessage().send(context.getSender());
        }

        if (!arguments.hasFlag(CommandFlags.SILENT)) {
            Lang.COMMAND_TIME_PERSONAL_RESET_NOTIFY.getMessage().send(target);
        }
        return true;
    }
}
