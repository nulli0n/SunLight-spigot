package su.nightexpress.sunlight.module.backlocation.command;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.command.experimental.CommandContext;
import su.nightexpress.nightcore.command.experimental.argument.ArgumentTypes;
import su.nightexpress.nightcore.command.experimental.argument.ParsedArguments;
import su.nightexpress.nightcore.command.experimental.builder.DirectNodeBuilder;
import su.nightexpress.nightcore.command.experimental.node.DirectNode;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.sunlight.Placeholders;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.command.CommandFlags;
import su.nightexpress.sunlight.command.CommandArguments;
import su.nightexpress.sunlight.command.CommandRegistry;
import su.nightexpress.sunlight.command.template.CommandTemplate;
import su.nightexpress.sunlight.command.CommandTools;
import su.nightexpress.sunlight.module.backlocation.BackLocationModule;
import su.nightexpress.sunlight.module.backlocation.config.BackLocationLang;
import su.nightexpress.sunlight.module.backlocation.config.BackLocationPerms;
import su.nightexpress.sunlight.module.backlocation.data.LocationType;

public class DeathBackCommand {

    public static final String NODE = "deathback";

    public static void load(@NotNull SunLightPlugin plugin, @NotNull BackLocationModule module) {
        CommandRegistry.registerDirectExecutor(NODE, (template, config) -> builder(plugin, module, template, config));
        CommandRegistry.addTemplate(NODE, CommandTemplate.direct(new String[]{NODE, "dback"}, NODE));
    }

    @NotNull
    public static DirectNodeBuilder builder(@NotNull SunLightPlugin plugin, @NotNull BackLocationModule module, @NotNull CommandTemplate template, @NotNull FileConfig config) {
        return DirectNode.builder(plugin, template.getAliases())
            .description(BackLocationLang.COMMAND_DEATH_BACK_DESC)
            .permission(BackLocationPerms.COMMAND_DEATHBACK)
            .withArgument(ArgumentTypes.playerName(CommandArguments.PLAYER).permission(BackLocationPerms.COMMAND_DEATHBACK_OTHERS))
            .withFlag(CommandFlags.silent().permission(BackLocationPerms.COMMAND_DEATHBACK_OTHERS))
            .executes((context, arguments) -> execute(plugin, module, context, arguments))
            ;
    }

    public static boolean execute(@NotNull SunLightPlugin plugin, @NotNull BackLocationModule module, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Player target = CommandTools.getTarget(plugin, context, arguments, CommandArguments.PLAYER, true);
        if (target == null) return false;

        boolean silent = arguments.hasFlag(CommandFlags.SILENT);
        if (!module.teleportToLocation(target, LocationType.DEATH, silent)) {
            if (context.getSender() != target) {
                context.send(BackLocationLang.COMMAND_DEATH_BACK_OTHERS_NOTHING.getMessage().replace(Placeholders.forPlayer(target)));
            }
            return false;
        }

        if (context.getSender() != target) {
            context.send(BackLocationLang.COMMAND_DEATH_BACK_OTHERS_DONE.getMessage().replace(Placeholders.forPlayer(target)));
        }
        return true;
    }
}
