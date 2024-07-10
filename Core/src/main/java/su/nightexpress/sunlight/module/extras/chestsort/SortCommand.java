package su.nightexpress.sunlight.module.extras.chestsort;

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

public class SortCommand {

    public static final String NODE = "chestsort_toggle";

    public static void load(@NotNull SunLightPlugin plugin, @NotNull SortManager manager) {
        CommandRegistry.registerDirectExecutor(NODE, (template, config) -> builderToggle(plugin, manager, template, config));

        CommandRegistry.addTemplate("chestsort", CommandTemplate.direct(new String[]{"chestsort"}, NODE));
    }

    @NotNull
    public static DirectNodeBuilder builderToggle(@NotNull SunLightPlugin plugin, @NotNull SortManager manager, @NotNull CommandTemplate template, @NotNull FileConfig config) {
        return DirectNode.builder(plugin, template.getAliases())
            .description(ExtrasLang.COMMAND_CHEST_SORT_DESC)
            .permission(ExtrasPerms.COMMAND_CHEST_SORT)
            .withArgument(CommandArguments.toggleMode(CommandArguments.MODE))
            .withArgument(ArgumentTypes.playerName(CommandArguments.PLAYER).permission(ExtrasPerms.COMMAND_CHEST_SORT_OTHERS))
            .withFlag(CommandFlags.silent().permission(ExtrasPerms.COMMAND_CHEST_SORT_OTHERS))
            .executes((context, arguments) -> toggle(plugin, manager, context, arguments))
            ;
    }

    public static boolean toggle(@NotNull SunLightPlugin plugin, @NotNull SortManager manager, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Player target = CommandTools.getTarget(plugin, context, arguments, CommandArguments.PLAYER, true);
        if (target == null) return false;

        ToggleMode mode = CommandTools.getToggleMode(plugin, context, arguments, CommandArguments.MODE);
        SunUser user = plugin.getUserManager().getUserData(target);
        boolean state = mode.apply(SortManager.isChestSortEnabled(user));

        user.getSettings().set(SortManager.SETTING_CHEST_SORT, state);
        plugin.getUserManager().scheduleSave(user);

        if (context.getSender() != target) {
            ExtrasLang.COMMAND_CHEST_SORT_TARGET.getMessage()
                .replace(Placeholders.forPlayer(target))
                .replace(Placeholders.GENERIC_STATE, Lang.getEnabledOrDisabled(state))
                .send(context.getSender());
        }

        if (!arguments.hasFlag(CommandFlags.SILENT)) {
            ExtrasLang.COMMAND_CHEST_SORT_NOTIFY.getMessage()
                .replace(Placeholders.GENERIC_STATE, Lang.getEnabledOrDisabled(state))
                .send(target);
        }

        return true;
    }
}
