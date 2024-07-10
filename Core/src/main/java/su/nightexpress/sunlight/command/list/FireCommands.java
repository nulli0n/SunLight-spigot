package su.nightexpress.sunlight.command.list;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.command.experimental.CommandContext;
import su.nightexpress.nightcore.command.experimental.argument.ArgumentTypes;
import su.nightexpress.nightcore.command.experimental.argument.ParsedArguments;
import su.nightexpress.nightcore.command.experimental.builder.DirectNodeBuilder;
import su.nightexpress.nightcore.command.experimental.node.DirectNode;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.util.Lists;
import su.nightexpress.nightcore.util.NumberUtil;
import su.nightexpress.sunlight.Placeholders;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.command.CommandFlags;
import su.nightexpress.sunlight.command.CommandPerms;
import su.nightexpress.sunlight.command.CommandArguments;
import su.nightexpress.sunlight.command.CommandRegistry;
import su.nightexpress.sunlight.command.template.CommandTemplate;
import su.nightexpress.sunlight.command.CommandTools;
import su.nightexpress.sunlight.config.Lang;

public class FireCommands {

    public static final String NODE_SET   = "fire_set";
    public static final String NODE_RESET = "fire_reset";

    public static void load(@NotNull SunLightPlugin plugin) {
        CommandRegistry.registerDirectExecutor(NODE_SET, (template, config) -> builderSet(plugin, template, config));
        CommandRegistry.registerDirectExecutor(NODE_RESET, (template, config) -> builderReset(plugin, template, config));

        CommandRegistry.addTemplate("fire", CommandTemplate.group(
            new String[]{"fire"},
            "Fire ticks commands.",
            CommandPerms.PREFIX + "fire",
            CommandTemplate.direct(new String[]{"set"}, NODE_SET),
            CommandTemplate.direct(new String[]{"reset"}, NODE_RESET)
        ));

        CommandRegistry.addTemplate("ignite", CommandTemplate.direct(new String[]{"ignite"}, NODE_SET));
        CommandRegistry.addTemplate("extinguish", CommandTemplate.direct(new String[]{"extinguish", "ext"}, NODE_RESET));
    }

    @NotNull
    public static DirectNodeBuilder builderSet(@NotNull SunLightPlugin plugin, @NotNull CommandTemplate template, @NotNull FileConfig config) {
        return DirectNode.builder(plugin, template.getAliases())
            .description(Lang.COMMAND_FIRE_SET_DESC)
            .permission(CommandPerms.FIRE_SET)
            .withArgument(ArgumentTypes.integer(CommandArguments.TIME).required()
                .localized(Lang.COMMAND_ARGUMENT_NAME_TIME)
                .withSamples(context -> Lists.newList("5", "10", "30"))
            )
            .withArgument(ArgumentTypes.playerName(CommandArguments.PLAYER).permission(CommandPerms.FIRE_SET_OTHERS))
            .withFlag(CommandFlags.silent().permission(CommandPerms.FIRE_SET_OTHERS))
            .executes((context, arguments) -> setTicks(plugin, context, arguments))
            ;
    }

    public static boolean setTicks(@NotNull SunLightPlugin plugin, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Player target = CommandTools.getTarget(plugin, context, arguments, CommandArguments.PLAYER, true);
        if (target == null) return false;

        int seconds = arguments.getIntArgument(CommandArguments.TIME);
        int ticks = seconds * 20;

        target.setFireTicks(ticks);
        if (!target.isOnline()) target.saveData();

        if (context.getSender() != target) {
            Lang.COMMAND_FIRE_SET_INFO.getMessage()
                .replace(Placeholders.GENERIC_AMOUNT, NumberUtil.format(ticks))
                .replace(Placeholders.GENERIC_TIME, NumberUtil.format(seconds))
                .replace(Placeholders.forPlayer(target))
                .send(context.getSender());
        }

        if (!arguments.hasFlag(CommandFlags.SILENT)) {
            Lang.COMMAND_FIRE_SET_TARGET.getMessage()
                .replace(Placeholders.GENERIC_AMOUNT, NumberUtil.format(ticks))
                .replace(Placeholders.GENERIC_TIME, NumberUtil.format(seconds))
                .replace(Placeholders.forPlayer(target))
                .send(target);
        }

        return true;
    }



    @NotNull
    public static DirectNodeBuilder builderReset(@NotNull SunLightPlugin plugin, @NotNull CommandTemplate template, @NotNull FileConfig config) {
        return DirectNode.builder(plugin, template.getAliases())
            .description(Lang.COMMAND_FIRE_RESET_DESC)
            .permission(CommandPerms.FIRE_RESET)
            .withArgument(ArgumentTypes.playerName(CommandArguments.PLAYER).permission(CommandPerms.FIRE_RESET_OTHERS))
            .withFlag(CommandFlags.silent().permission(CommandPerms.FIRE_RESET_OTHERS))
            .executes((context, arguments) -> resetTicks(plugin, context, arguments))
            ;
    }

    public static boolean resetTicks(@NotNull SunLightPlugin plugin, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Player target = CommandTools.getTarget(plugin, context, arguments, CommandArguments.PLAYER, true);
        if (target == null) return false;

        target.setFireTicks(0);
        if (!target.isOnline()) target.saveData();

        if (context.getSender() != target) {
            Lang.COMMAND_FIRE_RESET_INFO.getMessage()
                .replace(Placeholders.forPlayer(target))
                .send(context.getSender());
        }

        if (!arguments.hasFlag(CommandFlags.SILENT)) {
            Lang.COMMAND_FIRE_RESET_TARGET.getMessage()
                .replace(Placeholders.forPlayer(target))
                .send(target);
        }

        return true;
    }
}
