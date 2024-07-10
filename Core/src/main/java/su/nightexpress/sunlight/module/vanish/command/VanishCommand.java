package su.nightexpress.sunlight.module.vanish.command;

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
import su.nightexpress.sunlight.core.user.settings.Setting;
import su.nightexpress.sunlight.data.user.SunUser;
import su.nightexpress.sunlight.module.vanish.VanishModule;
import su.nightexpress.sunlight.module.vanish.config.VanishLang;
import su.nightexpress.sunlight.module.vanish.config.VanishPerms;

public class VanishCommand {

    public static final String NODE = "vanish_toggle";

    public static void load(@NotNull SunLightPlugin plugin, @NotNull VanishModule module) {
        CommandRegistry.registerDirectExecutor(NODE, (template, config) -> builderMode(plugin, module, template, config));

        CommandRegistry.addTemplate("vanish", CommandTemplate.direct(new String[]{"vanish"}, NODE));
    }

    @NotNull
    public static DirectNodeBuilder builderMode(@NotNull SunLightPlugin plugin, @NotNull VanishModule module, @NotNull CommandTemplate template, @NotNull FileConfig config) {
        return DirectNode.builder(plugin, template.getAliases())
            .description(VanishLang.COMMAND_VANISH_DESC)
            .permission(VanishPerms.COMMAND_VANISH)
            .withArgument(CommandArguments.toggleMode(CommandArguments.MODE))
            .withArgument(ArgumentTypes.playerName(CommandArguments.PLAYER).permission(VanishPerms.COMMAND_VANISH_OTHERS))
            .withFlag(CommandFlags.silent().permission(VanishPerms.COMMAND_VANISH_OTHERS))
            .executes((context, arguments) -> toggleVanish(plugin, module, context, arguments))
            ;
    }

    public static boolean toggleVanish(@NotNull SunLightPlugin plugin, @NotNull VanishModule module, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Player target = CommandTools.getTarget(plugin, context, arguments, CommandArguments.PLAYER, false);
        if (target == null) return false;

        ToggleMode mode = CommandTools.getToggleMode(plugin, context, arguments, CommandArguments.MODE);

        SunUser user = plugin.getUserManager().getUserData(target);
        Setting<Boolean> setting = VanishModule.VANISH;

        boolean state = mode.apply(user.getSettings().get(setting));
        user.getSettings().set(setting, state);
        plugin.getUserManager().scheduleSave(user);

        module.vanish(target, state);

        if (context.getSender() != target) {
            VanishLang.COMMAND_VANISH_TARGET.getMessage()
                .replace(Placeholders.GENERIC_STATE, Lang.getEnabledOrDisabled(state))
                .replace(Placeholders.forPlayer(target))
                .send(context.getSender());
        }

        if (!arguments.hasFlag(CommandFlags.SILENT)) {
            VanishLang.COMMAND_VANISH_NOTIFY.getMessage()
                .replace(Placeholders.GENERIC_STATE, Lang.getEnabledOrDisabled(state))
                .send(target);
        }

        return true;
    }
}
