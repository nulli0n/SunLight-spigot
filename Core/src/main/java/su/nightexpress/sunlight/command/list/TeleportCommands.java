package su.nightexpress.sunlight.command.list;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.command.experimental.CommandContext;
import su.nightexpress.nightcore.command.experimental.TabContext;
import su.nightexpress.nightcore.command.experimental.argument.ArgumentTypes;
import su.nightexpress.nightcore.command.experimental.argument.ParsedArguments;
import su.nightexpress.nightcore.command.experimental.builder.DirectNodeBuilder;
import su.nightexpress.nightcore.command.experimental.node.DirectNode;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.util.Lists;
import su.nightexpress.nightcore.util.NumberUtil;
import su.nightexpress.sunlight.Placeholders;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.command.*;
import su.nightexpress.sunlight.command.template.CommandTemplate;
import su.nightexpress.sunlight.config.Lang;
import su.nightexpress.sunlight.utils.Teleporter;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public class TeleportCommands {

    public static final String NODE_LOCATION = "teleport_location";
    public static final String NODE_SEND     = "teleport_send";
    public static final String NODE_SUMMON   = "teleport_summon";
    public static final String NODE_TO       = "teleport_to";
    public static final String NODE_TOP      = "teleport_top";

    public static void load(@NotNull SunLightPlugin plugin) {
        CommandRegistry.registerDirectExecutor(NODE_LOCATION, (template, config) -> builderLocation(plugin, template, config));
        CommandRegistry.registerDirectExecutor(NODE_SEND, (template, config) -> builderSend(plugin, template, config));
        CommandRegistry.registerDirectExecutor(NODE_SUMMON, (template, config) -> builderSummon(plugin, template, config));
        CommandRegistry.registerDirectExecutor(NODE_TO, (template, config) -> builderTo(plugin, template, config));
        CommandRegistry.registerDirectExecutor(NODE_TOP, (template, config) -> builderTop(plugin, template, config));

        CommandRegistry.addTemplate("teleport", CommandTemplate.group(new String[]{"teleport", "tp"},
            "Teleport commands.",
            CommandPerms.PREFIX + "teleport",
            CommandTemplate.direct(new String[]{"location"}, NODE_LOCATION),
            CommandTemplate.direct(new String[]{"send"}, NODE_SEND),
            CommandTemplate.direct(new String[]{"summon"}, NODE_SUMMON),
            CommandTemplate.direct(new String[]{"to"}, NODE_TO),
            CommandTemplate.direct(new String[]{"top"}, NODE_TOP)
        ));
        CommandRegistry.addTemplate("tppos", CommandTemplate.direct(new String[]{"tppos", "tploc"}, NODE_LOCATION));
        CommandRegistry.addTemplate("tpplayer", CommandTemplate.direct(new String[]{"tpplayer"}, NODE_SEND));
        CommandRegistry.addTemplate("summon", CommandTemplate.direct(new String[]{"summon"}, NODE_SUMMON));
        CommandRegistry.addTemplate("tpto", CommandTemplate.direct(new String[]{"tpto"}, NODE_TO));
        CommandRegistry.addTemplate("toppos", CommandTemplate.direct(new String[]{"toppos"}, NODE_TOP));
    }

    @NotNull
    public static DirectNodeBuilder builderLocation(@NotNull SunLightPlugin plugin, @NotNull CommandTemplate template, @NotNull FileConfig config) {
        return DirectNode.builder(plugin, template.getAliases())
            .description(Lang.COMMAND_TELEPORT_LOCATION_DESC)
            .permission(CommandPerms.TELEPORT_LOCATION)
            .withArgument(ArgumentTypes.integer(CommandArguments.X).localized(Lang.COMMAND_ARGUMENT_NAME_X).required().withSamples(context -> getTabLocation(context, Block::getX)))
            .withArgument(ArgumentTypes.integer(CommandArguments.Y).localized(Lang.COMMAND_ARGUMENT_NAME_Y).required().withSamples(context -> getTabLocation(context, Block::getY)))
            .withArgument(ArgumentTypes.integer(CommandArguments.Z).localized(Lang.COMMAND_ARGUMENT_NAME_Z).required().withSamples(context -> getTabLocation(context, Block::getZ)))
            .withArgument(ArgumentTypes.world(CommandArguments.WORLD))
            .withArgument(ArgumentTypes.playerName(CommandArguments.PLAYER).permission(CommandPerms.TELEPORT_LOCATION_OTHERS))
            .withFlag(CommandFlags.silent().permission(CommandPerms.TELEPORT_LOCATION_OTHERS))
            .executes((context, arguments) -> executeLocation(plugin, context, arguments))
            ;
    }

    private static List<String> getTabLocation(@NotNull TabContext context, Function<Block, Integer> function) {
        Player player = context.getPlayer();
        if (player == null) return Collections.emptyList();

        Block block = player.getTargetBlock(null, 100);
        return Lists.newList(NumberUtil.format(function.apply(block)));
    }

    public static boolean executeLocation(@NotNull SunLightPlugin plugin, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Player target = CommandTools.getTarget(plugin, context, arguments, CommandArguments.PLAYER, true);
        if (target == null) return false;

        World world = arguments.getWorldArgument(CommandArguments.WORLD, target.getWorld());
        int x = arguments.getIntArgument(CommandArguments.X);
        int y = arguments.getIntArgument(CommandArguments.Y);
        int z = arguments.getIntArgument(CommandArguments.Z);

        Block block = world.getBlockAt(x, y, z);
        Location location = block.getRelative(BlockFace.UP).getLocation();

        new Teleporter(target, location).centered().validateFloor().useOriginalDirection().teleport();

        if (context.getSender() != target) {
            Lang.COMMAND_TELEPORT_LOCATION_DONE.getMessage()
                .replace(Placeholders.forPlayer(target))
                .replace(Placeholders.forLocation(location))
                .send(context.getSender());
        }

        if (!arguments.hasFlag(CommandFlags.SILENT)) {
            Lang.COMMAND_TELEPORT_LOCATION_NOTIFY.getMessage()
                .replace(Placeholders.forLocation(location))
                .send(target);
        }

        return true;
    }



    @NotNull
    public static DirectNodeBuilder builderSend(@NotNull SunLightPlugin plugin, @NotNull CommandTemplate template, @NotNull FileConfig config) {
        return DirectNode.builder(plugin, template.getAliases())
            .description(Lang.COMMAND_TELEPORT_SEND_DESC)
            .permission(CommandPerms.TELEPORT_SEND)
            .withArgument(ArgumentTypes.playerName(CommandArguments.PLAYER).required())
            .withArgument(ArgumentTypes.playerName(CommandArguments.TARGET).required().localized(Lang.COMMAND_ARGUMENT_NAME_TARGET))
            .withFlag(CommandFlags.silent())
            .executes((context, arguments) -> executeSend(plugin, context, arguments))
            ;
    }

    public static boolean executeSend(@NotNull SunLightPlugin plugin, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Player player = CommandTools.getTarget(plugin, context, arguments, CommandArguments.PLAYER, true);
        Player target = CommandTools.getTarget(plugin, context, arguments, CommandArguments.TARGET, true);
        if (player == null || target == null) return false;

        new Teleporter(player, target).useOriginalDirection().teleport();

        if (player != context.getSender()) {
            Lang.COMMAND_TELEPORT_SEND_DONE.getMessage()
                .replace(Placeholders.GENERIC_SOURCE, player.getName())
                .replace(Placeholders.GENERIC_TARGET, target.getName())
                .send(context.getSender());
        }

        if (!arguments.hasFlag(CommandFlags.SILENT)) {
            Lang.COMMAND_TELEPORT_SEND_NOTIFY.getMessage()
                .replace(Placeholders.forPlayer(target))
                .send(player);
        }

        return true;
    }



    @NotNull
    public static DirectNodeBuilder builderSummon(@NotNull SunLightPlugin plugin, @NotNull CommandTemplate template, @NotNull FileConfig config) {
        return DirectNode.builder(plugin, template.getAliases())
            .playerOnly()
            .description(Lang.COMMAND_TELEPORT_SUMMON_DESC)
            .permission(CommandPerms.TELEPORT_SUMMON)
            .withArgument(ArgumentTypes.playerName(CommandArguments.PLAYER).required())
            .withFlag(CommandFlags.silent())
            .executes((context, arguments) -> executeSummon(plugin, context, arguments))
            ;
    }

    public static boolean executeSummon(@NotNull SunLightPlugin plugin, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Player executor = context.getExecutor();
        if (executor == null) return false;

        Player target = CommandTools.getTarget(plugin, context, arguments, CommandArguments.PLAYER, true);
        if (target == null) return false;

        if (target == executor) {
            context.send(Lang.ERROR_COMMAND_NOT_YOURSELF.getMessage());
            return false;
        }

        new Teleporter(target, executor).useOriginalDirection().teleport();

        Lang.COMMAND_TELEPORT_SUMMON_DONE.getMessage()
            .replace(Placeholders.forPlayer(target))
            .send(context.getSender());

        if (!arguments.hasFlag(CommandFlags.SILENT)) {
            Lang.COMMAND_TELEPORT_SUMMON_NOTIFY.getMessage()
                .replace(Placeholders.forPlayer(executor))
                .send(target);
        }

        return true;
    }



    @NotNull
    public static DirectNodeBuilder builderTo(@NotNull SunLightPlugin plugin, @NotNull CommandTemplate template, @NotNull FileConfig config) {
        return DirectNode.builder(plugin, template.getAliases())
            .playerOnly()
            .description(Lang.COMMAND_TELEPORT_TO_DESC)
            .permission(CommandPerms.TELEPORT_TO)
            .withArgument(ArgumentTypes.playerName(CommandArguments.PLAYER).required())
            .executes((context, arguments) -> executeTo(plugin, context, arguments))
            ;
    }

    public static boolean executeTo(@NotNull SunLightPlugin plugin, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Player player = context.getExecutor();
        if (player == null) return false;

        Player target = CommandTools.getTarget(plugin, context, arguments, CommandArguments.PLAYER, true);
        if (target == null) return false;

        if (target == player) {
            context.send(Lang.ERROR_COMMAND_NOT_YOURSELF.getMessage());
            return false;
        }

        player.teleport(target);
        Lang.COMMAND_TELEPORT_TO_DONE.getMessage().replace(Placeholders.forPlayer(target)).send(player);
        return true;
    }



    @NotNull
    public static DirectNodeBuilder builderTop(@NotNull SunLightPlugin plugin, @NotNull CommandTemplate template, @NotNull FileConfig config) {
        return DirectNode.builder(plugin, template.getAliases())
            .description(Lang.COMMAND_TELEPORT_TOP_DESC)
            .permission(CommandPerms.TELEPORT_TOP)
            .withArgument(ArgumentTypes.playerName(CommandArguments.PLAYER).permission(CommandPerms.TELEPORT_TOP_OTHERS))
            .withFlag(CommandFlags.silent().permission(CommandPerms.TELEPORT_TOP_OTHERS))
            .executes((context, arguments) -> executeTop(plugin, context, arguments))
            ;
    }

    public static boolean executeTop(@NotNull SunLightPlugin plugin, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Player target = CommandTools.getTarget(plugin, context, arguments, CommandArguments.PLAYER, true);
        if (target == null) return false;

        Block block = target.getWorld().getHighestBlockAt(target.getLocation()).getRelative(BlockFace.UP);
        Location location = block.getLocation();

        new Teleporter(target, location).centered().validateFloor().useOriginalDirection().teleport();

        if (context.getSender() != target) {
            Lang.COMMAND_TELEPORT_TOP_DONE.getMessage().replace(Placeholders.forPlayer(target)).send(context.getSender());
        }

        if (!arguments.hasFlag(CommandFlags.SILENT)) {
            Lang.COMMAND_TELEPORT_TOP_NOTIFY.getMessage().send(target);
        }
        return true;
    }
}
