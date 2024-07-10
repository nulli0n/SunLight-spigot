package su.nightexpress.sunlight.command.list;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.command.experimental.CommandContext;
import su.nightexpress.nightcore.command.experimental.argument.ArgumentTypes;
import su.nightexpress.nightcore.command.experimental.argument.ParsedArguments;
import su.nightexpress.nightcore.command.experimental.builder.DirectNodeBuilder;
import su.nightexpress.nightcore.command.experimental.node.DirectNode;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.language.LangAssets;
import su.nightexpress.sunlight.Placeholders;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.command.CommandFlags;
import su.nightexpress.sunlight.command.CommandPerms;
import su.nightexpress.sunlight.command.CommandArguments;
import su.nightexpress.sunlight.command.CommandRegistry;
import su.nightexpress.sunlight.command.template.CommandTemplate;
import su.nightexpress.sunlight.command.CommandTools;
import su.nightexpress.sunlight.config.Lang;
import su.nightexpress.sunlight.utils.SunUtils;

public class DimensionCommand {

    public static final String NAME = "dimension";

    public static void load(@NotNull SunLightPlugin plugin) {
        CommandRegistry.registerDirectExecutor(NAME, (template, config) -> builderRoot(plugin, template, config));
        CommandRegistry.addTemplate(NAME, CommandTemplate.direct(new String[]{NAME, "dim", "world"}, NAME));
    }

    @NotNull
    public static DirectNodeBuilder builderRoot(@NotNull SunLightPlugin plugin, @NotNull CommandTemplate template, @NotNull FileConfig config) {
        return DirectNode.builder(plugin, template.getAliases())
            .description(Lang.COMMAND_DIMENSION_DESC)
            .permission(CommandPerms.DIMENSION)
            .withArgument(ArgumentTypes.world(CommandArguments.WORLD).required())
            .withArgument(ArgumentTypes.playerName(CommandArguments.PLAYER).permission(CommandPerms.DIMENSION_OTHERS))
            .withFlag(CommandFlags.silent().permission(CommandPerms.DIMENSION_OTHERS))
            .executes((context, arguments) -> execute(plugin, context, arguments))
            ;
    }

    public static boolean execute(@NotNull SunLightPlugin plugin, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Player target = CommandTools.getTarget(plugin, context, arguments, CommandArguments.PLAYER, true);
        if (target == null) return false;

        World world = arguments.getWorldArgument(CommandArguments.WORLD);
        SunUtils.teleport(target, world.getSpawnLocation()); // Data saved via NMS.

        if (context.getSender() != target) {
            Lang.COMMAND_DIMENSION_TARGET.getMessage()
                .replace(Placeholders.forPlayer(target))
                .replace(Placeholders.GENERIC_WORLD, LangAssets.get(world))
                .send(context.getSender());
        }

        if (!arguments.hasFlag(CommandFlags.SILENT)) {
            Lang.COMMAND_DIMENSION_NOTIFY.getMessage()
                .replace(Placeholders.GENERIC_WORLD, LangAssets.get(world))
                .send(target);
        }

        return true;
    }
}
