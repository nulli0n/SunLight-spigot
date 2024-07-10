package su.nightexpress.sunlight.module.godmode.command;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.command.experimental.CommandContext;
import su.nightexpress.nightcore.command.experimental.argument.ArgumentTypes;
import su.nightexpress.nightcore.command.experimental.argument.ParsedArguments;
import su.nightexpress.nightcore.command.experimental.builder.DirectNodeBuilder;
import su.nightexpress.nightcore.command.experimental.node.DirectNode;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.sunlight.command.CommandArguments;
import su.nightexpress.sunlight.command.CommandRegistry;
import su.nightexpress.sunlight.command.CommandTools;
import su.nightexpress.sunlight.Placeholders;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.command.CommandFlags;
import su.nightexpress.sunlight.command.mode.ToggleMode;
import su.nightexpress.sunlight.command.template.CommandTemplate;
import su.nightexpress.sunlight.config.Lang;
import su.nightexpress.sunlight.data.user.SunUser;
import su.nightexpress.sunlight.core.user.settings.Setting;
import su.nightexpress.sunlight.module.godmode.GodModule;
import su.nightexpress.sunlight.module.godmode.config.GodLang;
import su.nightexpress.sunlight.module.godmode.config.GodPerms;

public class GodCommands {

    public static final String NODE_GOD      = "god_toggle";
    public static final String NODE_FOOD_GOD = "foodgod_toggle";

    public static void load(@NotNull SunLightPlugin plugin, @NotNull GodModule module) {
        CommandRegistry.registerDirectExecutor(NODE_GOD, (template, config) -> builderMode(plugin, module, template, config));
        CommandRegistry.registerDirectExecutor(NODE_FOOD_GOD, (template, config) -> builderFood(plugin, module, template, config));

        CommandRegistry.addTemplate("god", CommandTemplate.direct(new String[]{"god"}, NODE_GOD));
        CommandRegistry.addTemplate("foodgod", CommandTemplate.direct(new String[]{"foodgod"}, NODE_FOOD_GOD));
    }

    @NotNull
    public static DirectNodeBuilder builderMode(@NotNull SunLightPlugin plugin, @NotNull GodModule module, @NotNull CommandTemplate template, @NotNull FileConfig config) {
        return DirectNode.builder(plugin, template.getAliases())
            .description(GodLang.COMMAND_GOD_DESC)
            .permission(GodPerms.COMMAND_GOD)
            .withArgument(CommandArguments.toggleMode(CommandArguments.MODE))
            .withArgument(ArgumentTypes.playerName(CommandArguments.PLAYER).permission(GodPerms.COMMAND_GOD_OTHERS))
            .withFlag(CommandFlags.silent().permission(GodPerms.COMMAND_GOD_OTHERS))
            .executes((context, arguments) -> toggleGod(plugin, module, context, arguments))
            ;
    }

    public static boolean toggleGod(@NotNull SunLightPlugin plugin, @NotNull GodModule module, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Player target = CommandTools.getTarget(plugin, context, arguments, CommandArguments.PLAYER, false);
        if (target == null) return false;

        ToggleMode mode = CommandTools.getToggleMode(plugin, context, arguments, CommandArguments.MODE);

        SunUser user = plugin.getUserManager().getUserData(target);
        Setting<Boolean> setting = GodModule.GOD_MODE;

        boolean state = mode.apply(user.getSettings().get(setting));
        user.getSettings().set(setting, state);
        plugin.getUserManager().scheduleSave(user);

        // Notify about god mode in disabled world.
        if (state && !module.isAllowedWorld(target)) {
            GodLang.NOTIFY_BAD_WORLD.getMessage().send(target);
        }

        if (context.getSender() != target) {
            GodLang.COMMAND_GOD_TOGGLE_TARGET.getMessage()
                .replace(Placeholders.GENERIC_STATE, Lang.getEnabledOrDisabled(state))
                .replace(Placeholders.forPlayer(target))
                .send(context.getSender());
        }

        if (!arguments.hasFlag(CommandFlags.SILENT)) {
            GodLang.COMMAND_GOD_TOGGLE_NOTIFY.getMessage()
                .replace(Placeholders.GENERIC_STATE, Lang.getEnabledOrDisabled(state))
                .send(target);
        }

        return true;
    }



    @NotNull
    public static DirectNodeBuilder builderFood(@NotNull SunLightPlugin plugin, @NotNull GodModule module, @NotNull CommandTemplate template, @NotNull FileConfig config) {
        return DirectNode.builder(plugin, template.getAliases())
            .description(GodLang.COMMAND_FOOD_GOD_DESC)
            .permission(GodPerms.COMMAND_FOOD_GOD)
            .withArgument(CommandArguments.toggleMode(CommandArguments.MODE))
            .withArgument(ArgumentTypes.playerName(CommandArguments.PLAYER).permission(GodPerms.COMMAND_FOOD_GOD_OTHERS))
            .withFlag(CommandFlags.silent().permission(GodPerms.COMMAND_FOOD_GOD_OTHERS))
            .executes((context, arguments) -> toggleFoodGod(plugin, module, context, arguments))
            ;
    }

    public static boolean toggleFoodGod(@NotNull SunLightPlugin plugin, @NotNull GodModule module, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Player target = CommandTools.getTarget(plugin, context, arguments, CommandArguments.PLAYER, false);
        if (target == null) return false;

        ToggleMode mode = CommandTools.getToggleMode(plugin, context, arguments, CommandArguments.MODE);

        SunUser user = plugin.getUserManager().getUserData(target);
        Setting<Boolean> setting = GodModule.FOOD_GOD;

        boolean state = mode.apply(user.getSettings().get(setting));
        user.getSettings().set(setting, state);
        plugin.getUserManager().scheduleSave(user);

        if (context.getSender() != target) {
            GodLang.COMMAND_FOOD_GOD_TARGET.getMessage()
                .replace(Placeholders.forPlayer(target))
                .replace(Placeholders.GENERIC_STATE, Lang.getEnabledOrDisabled(state))
                .send(context.getSender());
        }

        if (!arguments.hasFlag(CommandFlags.SILENT)) {
            GodLang.COMMAND_FOOD_GOD_NOTIFY.getMessage()
                .replace(Placeholders.GENERIC_STATE, Lang.getEnabledOrDisabled(state))
                .send(target);
        }

        return true;
    }
}
