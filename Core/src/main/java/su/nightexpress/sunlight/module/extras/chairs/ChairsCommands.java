package su.nightexpress.sunlight.module.extras.chairs;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
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
import su.nightexpress.sunlight.command.CommandArguments;
import su.nightexpress.sunlight.command.CommandFlags;
import su.nightexpress.sunlight.command.CommandRegistry;
import su.nightexpress.sunlight.command.CommandTools;
import su.nightexpress.sunlight.command.mode.ToggleMode;
import su.nightexpress.sunlight.command.template.CommandTemplate;
import su.nightexpress.sunlight.config.Lang;
import su.nightexpress.sunlight.data.user.SunUser;
import su.nightexpress.sunlight.module.extras.config.ExtrasLang;
import su.nightexpress.sunlight.module.extras.config.ExtrasPerms;

public class ChairsCommands {

    public static final String NODE_TOGGLE = "chairs_toggle";
    public static final String NODE_SIT = "chairs_sit";

    public static void load(@NotNull SunLightPlugin plugin, @NotNull ChairsManager manager) {
        CommandRegistry.registerDirectExecutor(NODE_TOGGLE, (template, config) -> builderToggle(plugin, manager, template, config));
        CommandRegistry.registerDirectExecutor(NODE_SIT, (template, config) -> builderSit(plugin, manager, template, config));

        CommandRegistry.addTemplate("chairs", CommandTemplate.direct(new String[]{"chairs"}, NODE_TOGGLE));
        CommandRegistry.addTemplate("sit", CommandTemplate.direct(new String[]{"sit"}, NODE_SIT));
    }

    @NotNull
    public static DirectNodeBuilder builderToggle(@NotNull SunLightPlugin plugin, @NotNull ChairsManager manager, @NotNull CommandTemplate template, @NotNull FileConfig config) {
        return DirectNode.builder(plugin, template.getAliases())
            .description(ExtrasLang.COMMAND_CHAIRS_DESC)
            .permission(ExtrasPerms.COMMAND_CHAIRS)
            .withArgument(CommandArguments.toggleMode(CommandArguments.MODE))
            .withArgument(ArgumentTypes.playerName(CommandArguments.PLAYER).permission(ExtrasPerms.COMMAND_CHAIRS_OTHERS))
            .withFlag(CommandFlags.silent().permission(ExtrasPerms.COMMAND_CHAIRS_OTHERS))
            .executes((context, arguments) -> toggleChairs(plugin, manager, context, arguments))
            ;
    }

    public static boolean toggleChairs(@NotNull SunLightPlugin plugin, @NotNull ChairsManager manager, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Player target = CommandTools.getTarget(plugin, context, arguments, CommandArguments.PLAYER, true);
        if (target == null) return false;

        ToggleMode mode = CommandTools.getToggleMode(plugin, context, arguments, CommandArguments.MODE);
        SunUser user = plugin.getUserManager().getOrFetch(target);
        boolean state = mode.apply(ChairsManager.isChairsEnabled(user));

        user.getSettings().set(ChairsManager.SETTING_CHAIRS, state);
        plugin.getUserManager().save(user);

        if (context.getSender() != target) {
            ExtrasLang.COMMAND_CHAIRS_TARGET.getMessage()
                .replace(Placeholders.forPlayer(target))
                .replace(Placeholders.GENERIC_STATE, Lang.getEnabledOrDisabled(state))
                .send(context.getSender());
        }

        if (!arguments.hasFlag(CommandFlags.SILENT)) {
            ExtrasLang.COMMAND_CHAIRS_NOTIFY.getMessage()
                .replace(Placeholders.GENERIC_STATE, Lang.getEnabledOrDisabled(state))
                .send(target);
        }

        return true;
    }



    @NotNull
    public static DirectNodeBuilder builderSit(@NotNull SunLightPlugin plugin, @NotNull ChairsManager manager, @NotNull CommandTemplate template, @NotNull FileConfig config) {
        return DirectNode.builder(plugin, template.getAliases())
            .description(ExtrasLang.COMMAND_SIT_DESC)
            .permission(ExtrasPerms.COMMAND_SIT)
            .withArgument(ArgumentTypes.playerName(CommandArguments.PLAYER).permission(ExtrasPerms.COMMAND_SIT_OTHERS))
            .withFlag(CommandFlags.silent().permission(ExtrasPerms.COMMAND_SIT_OTHERS))
            .executes((context, arguments) -> doSit(plugin, manager, context, arguments))
            ;
    }

    public static boolean doSit(@NotNull SunLightPlugin plugin, @NotNull ChairsManager manager, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Player target = CommandTools.getTarget(plugin, context, arguments, CommandArguments.PLAYER, true);
        if (target == null || manager.isSit(target)) return false;

        Block block = target.getLocation().getBlock();
        if (block.isEmpty() || !block.getType().isSolid()) block = block.getRelative(BlockFace.DOWN);
        if (block.isEmpty() || !block.getType().isSolid()) return false;

        manager.sitPlayer(target, block);
        return true;
    }
}
