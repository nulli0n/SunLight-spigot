package su.nightexpress.sunlight.module.nerfphantoms.command;

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
import su.nightexpress.sunlight.module.nerfphantoms.PhantomsModule;
import su.nightexpress.sunlight.module.nerfphantoms.config.PhantomsLang;
import su.nightexpress.sunlight.module.nerfphantoms.config.PhantomsPerms;

public class NoPhantomCommand {

    public static final String NODE = "nophantom_toggle";

    public static void load(@NotNull SunLightPlugin plugin, @NotNull PhantomsModule module) {
        CommandRegistry.registerDirectExecutor(NODE, (template, config) -> builderRoot(plugin, module, template, config));
        CommandRegistry.addTemplate("nophantom", CommandTemplate.direct(new String[]{"nophantom"}, NODE));
    }

    @NotNull
    public static DirectNodeBuilder builderRoot(@NotNull SunLightPlugin plugin, @NotNull PhantomsModule module, @NotNull CommandTemplate template, @NotNull FileConfig config) {
        return DirectNode.builder(plugin, template.getAliases())
            .description(PhantomsLang.COMMAND_NO_PHANTOM_DESC)
            .permission(PhantomsPerms.COMMAND_NO_PHANTOM)
            .withArgument(CommandArguments.toggleMode(CommandArguments.MODE))
            .withArgument(ArgumentTypes.playerName(CommandArguments.PLAYER).permission(PhantomsPerms.COMMAND_NO_PHANTOM_OTHERS))
            .withFlag(CommandFlags.silent().permission(PhantomsPerms.COMMAND_NO_PHANTOM_OTHERS))
            .executes((context, arguments) -> execute(plugin, module, context, arguments))
            ;
    }

    public static boolean execute(@NotNull SunLightPlugin plugin, @NotNull PhantomsModule module, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Player target = CommandTools.getTarget(plugin, context, arguments, CommandArguments.PLAYER, true);
        if (target == null) return false;

        ToggleMode mode = CommandTools.getToggleMode(plugin, context, arguments, CommandArguments.MODE);

        SunUser user = plugin.getUserManager().getUserData(target);
        boolean state = mode.apply(user.getSettings().get(PhantomsModule.ANTI_PHANTOM));
        module.setAntiPhantom(target, state);

        if (context.getSender() != target) {
            PhantomsLang.COMMAND_NO_PHANTOM_TOGGLE_OTHERS.getMessage()
                .replace(Placeholders.GENERIC_STATE, Lang.getEnabledOrDisabled(state))
                .replace(Placeholders.forPlayer(target))
                .send(context.getSender());
        }

        if (!arguments.hasFlag(CommandFlags.SILENT)) {
            PhantomsLang.COMMAND_NO_PHANTOM_TOGGLE_NOTIFY.getMessage()
                .replace(Placeholders.GENERIC_STATE, Lang.getEnabledOrDisabled(state))
                .send(target);
        }

        return true;
    }
}
