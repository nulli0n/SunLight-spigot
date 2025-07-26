package su.nightexpress.sunlight.module.scoreboard.command;

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
import su.nightexpress.sunlight.module.scoreboard.ScoreboardModule;
import su.nightexpress.sunlight.module.scoreboard.config.SBLang;
import su.nightexpress.sunlight.module.scoreboard.config.SBPerms;

public class ScoreboardCommand {

    public static final String NODE = "scoreboard_toggle";

    public static void load(@NotNull SunLightPlugin plugin, @NotNull ScoreboardModule module) {
        CommandRegistry.registerDirectExecutor(NODE, (template, config) -> builder(plugin, module, template, config));
        CommandRegistry.addTemplate("scoreboard", CommandTemplate.direct(new String[]{"scoreboard", "board", "sb"}, NODE));
    }

    @NotNull
    public static DirectNodeBuilder builder(@NotNull SunLightPlugin plugin, @NotNull ScoreboardModule module, @NotNull CommandTemplate template, @NotNull FileConfig config) {
        return DirectNode.builder(plugin, template.getAliases())
            .description(SBLang.COMMAND_SCOREBOARD_DESC)
            .permission(SBPerms.COMMAND_SCOREBOARD)
            .withArgument(CommandArguments.toggleMode(CommandArguments.MODE))
            .withArgument(ArgumentTypes.playerName(CommandArguments.PLAYER).permission(SBPerms.COMMAND_SCOREBOARD_OTHERS))
            .withFlag(CommandFlags.silent().permission(SBPerms.COMMAND_SCOREBOARD_OTHERS))
            .executes((context, arguments) -> execute(plugin, module, context, arguments))
            ;
    }

    public static boolean execute(@NotNull SunLightPlugin plugin, @NotNull ScoreboardModule module, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Player target = CommandTools.getTarget(plugin, context, arguments, CommandArguments.PLAYER, true);
        if (target == null) return false;

        ToggleMode mode = CommandTools.getToggleMode(plugin, context, arguments, CommandArguments.MODE);

        SunUser user = plugin.getUserManager().getOrFetch(target);
        boolean state = mode.apply(user.getSettings().get(ScoreboardModule.SETTING_SCOREBOARD));

        if (state) {
            module.addBoard(target);
        }
        else {
            module.removeBoard(target);
        }

        user.getSettings().set(ScoreboardModule.SETTING_SCOREBOARD, state);
        plugin.getUserManager().save(user);

        if (context.getSender() != target) {
            SBLang.COMMAND_SCOREBOARD_TARGET.getMessage()
                .replace(Placeholders.forPlayer(target))
                .replace(Placeholders.GENERIC_STATE, Lang.getEnabledOrDisabled(state))
                .send(context.getSender());
        }

        if (!arguments.hasFlag(CommandFlags.SILENT)) {
            SBLang.COMMAND_SCOREBOARD_NOTIFY.getMessage()
                .replace(Placeholders.GENERIC_STATE, Lang.getEnabledOrDisabled(state))
                .send(target);
        }

        return true;
    }
}
