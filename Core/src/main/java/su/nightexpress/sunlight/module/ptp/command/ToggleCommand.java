package su.nightexpress.sunlight.module.ptp.command;

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
import su.nightexpress.sunlight.module.ptp.PTPModule;
import su.nightexpress.sunlight.module.ptp.config.PTPLang;
import su.nightexpress.sunlight.module.ptp.config.PTPPerms;

public class ToggleCommand {

    public static final String NODE = "ptp_toggle";

    public static void load(@NotNull SunLightPlugin plugin, @NotNull PTPModule module) {
        CommandRegistry.registerDirectExecutor(NODE, (template, config) -> builder(plugin, module, template, config));

        CommandRegistry.addTemplate("tptoggle", CommandTemplate.direct(new String[]{"tptoggle"}, NODE));
    }

    @NotNull
    public static DirectNodeBuilder builder(@NotNull SunLightPlugin plugin, @NotNull PTPModule module, @NotNull CommandTemplate template, @NotNull FileConfig config) {
        return DirectNode.builder(plugin, template.getAliases())
            .description(PTPLang.COMMAND_TOGGLE_DESC)
            .permission(PTPPerms.COMMAND_TOGGLE)
            .withArgument(CommandArguments.toggleMode(CommandArguments.MODE))
            .withArgument(ArgumentTypes.playerName(CommandArguments.PLAYER).permission(PTPPerms.COMMAND_TOGGLE_OTHERS))
            .withFlag(CommandFlags.silent().permission(PTPPerms.COMMAND_TOGGLE_OTHERS))
            .executes((context, arguments) -> execute(plugin, module, context, arguments))
            ;
    }

    public static boolean execute(@NotNull SunLightPlugin plugin, @NotNull PTPModule module, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Player target = CommandTools.getTarget(plugin, context, arguments, CommandArguments.PLAYER, true);
        if (target == null) return false;

        ToggleMode mode = CommandTools.getToggleMode(plugin, context, arguments, CommandArguments.MODE);

        SunUser user = plugin.getUserManager().getOrFetch(target);
        boolean state = mode.apply(user.getSettings().get(PTPModule.TELEPORT_REQUESTS));
        user.getSettings().set(PTPModule.TELEPORT_REQUESTS, state);
        plugin.getUserManager().save(user);

        if (context.getSender() != target) {
            PTPLang.COMMAND_TOGGLE_DONE.getMessage()
                .replace(Placeholders.forPlayer(target))
                .replace(Placeholders.GENERIC_STATE, Lang.getEnabledOrDisabled(state))
                .send(context.getSender());
        }

        if (!arguments.hasFlag(CommandFlags.SILENT)) {
            PTPLang.COMMAND_TOGGLE_NOTIFY.getMessage()
                .replace(Placeholders.GENERIC_STATE, Lang.getEnabledOrDisabled(state))
                .send(target);
        }

        return true;
    }
}
