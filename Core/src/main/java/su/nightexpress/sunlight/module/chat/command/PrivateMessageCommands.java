package su.nightexpress.sunlight.module.chat.command;

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
import su.nightexpress.sunlight.core.user.settings.SettingRegistry;
import su.nightexpress.sunlight.data.user.SunUser;
import su.nightexpress.sunlight.module.chat.ChatModule;
import su.nightexpress.sunlight.module.chat.config.ChatLang;
import su.nightexpress.sunlight.module.chat.config.ChatPerms;

import java.util.UUID;

public class PrivateMessageCommands {

    public static final String NODE_SEND = "pm_send";
    public static final String NODE_REPLY = "pm_reply";
    public static final String NODE_TOGGLE = "pm_toggle";

    public static final String DEF_PM_ALIAS = "pm";

    public static void load(@NotNull SunLightPlugin plugin, @NotNull ChatModule module) {
        CommandRegistry.registerDirectExecutor(NODE_SEND, (template, config) -> builderSend(plugin, module, template, config));
        CommandRegistry.registerDirectExecutor(NODE_REPLY, (template, config) -> builderReply(plugin, module, template, config));
        CommandRegistry.registerDirectExecutor(NODE_TOGGLE, (template, config) -> builderToggle(plugin, module, template, config));

        CommandRegistry.addTemplate("pm", CommandTemplate.direct(new String[]{DEF_PM_ALIAS, "whisper", "w", "tell", "msg"}, NODE_SEND));
        CommandRegistry.addTemplate("reply", CommandTemplate.direct(new String[]{"reply", "r"}, NODE_REPLY));
        CommandRegistry.addTemplate("pmtoggle", CommandTemplate.direct(new String[]{"pmtoggle"}, NODE_TOGGLE));
    }



    @NotNull
    public static DirectNodeBuilder builderSend(@NotNull SunLightPlugin plugin, @NotNull ChatModule module, @NotNull CommandTemplate template, @NotNull FileConfig config) {
        return DirectNode.builder(plugin, template.getAliases())
            .description(ChatLang.COMMAND_TELL_DESC)
            .permission(ChatPerms.COMMAND_TELL)
            .withArgument(ArgumentTypes.player(CommandArguments.PLAYER).required())
            .withArgument(ArgumentTypes.string(CommandArguments.TEXT).required().complex().localized(ChatLang.COMMAND_ARGUMENT_NAME_TEXT))
            .executes((context, arguments) -> sendMessage(plugin, module, context, arguments))
            ;
    }

    public static boolean sendMessage(@NotNull SunLightPlugin plugin, @NotNull ChatModule module, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Player receiver = arguments.getPlayerArgument(CommandArguments.PLAYER);
        String message = arguments.getStringArgument(CommandArguments.TEXT);
        return module.handlePrivateMessage(context.getSender(), receiver, message);
    }



    @NotNull
    public static DirectNodeBuilder builderReply(@NotNull SunLightPlugin plugin, @NotNull ChatModule module, @NotNull CommandTemplate template, @NotNull FileConfig config) {
        return DirectNode.builder(plugin, template.getAliases())
            .playerOnly()
            .description(ChatLang.COMMAND_REPLY_DESC)
            .permission(ChatPerms.COMMAND_REPLY)
            .withArgument(ArgumentTypes.string(CommandArguments.TEXT).required().complex().localized(ChatLang.COMMAND_ARGUMENT_NAME_TEXT))
            .executes((context, arguments) -> reply(plugin, module, context, arguments))
            ;
    }

    public static boolean reply(@NotNull SunLightPlugin plugin, @NotNull ChatModule module, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Player player = context.getPlayerOrThrow();

        UUID targetId = module.getChatData(player).getLastConversationWith();
        if (targetId == null) {
            context.send(ChatLang.COMMAND_REPLY_ERROR_EMPTY.getMessage());
            return false;
        }

        Player target = plugin.getServer().getPlayer(targetId);
        if (target == null) {
            context.errorBadPlayer();
            return false;
        }

        String message = arguments.getStringArgument(CommandArguments.TEXT);

        return module.handlePrivateMessage(player, target, message);
    }



    @NotNull
    public static DirectNodeBuilder builderToggle(@NotNull SunLightPlugin plugin, @NotNull ChatModule module, @NotNull CommandTemplate template, @NotNull FileConfig config) {
        return DirectNode.builder(plugin, template.getAliases())
            .description(ChatLang.COMMAND_TOGGLE_PM_DESC)
            .permission(ChatPerms.COMMAND_TOGGLE_PM)
            .withArgument(CommandArguments.toggleMode(CommandArguments.MODE))
            .withArgument(ArgumentTypes.playerName(CommandArguments.PLAYER).permission(ChatPerms.COMMAND_TOGGLE_PM_OTHERS))
            .withFlag(CommandFlags.silent().permission(ChatPerms.COMMAND_TOGGLE_PM_OTHERS))
            .executes((context, arguments) -> togglePM(plugin, module, context, arguments))
            ;
    }

    public static boolean togglePM(@NotNull SunLightPlugin plugin, @NotNull ChatModule module, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Player target = CommandTools.getTarget(plugin, context, arguments, CommandArguments.PLAYER, true);
        if (target == null) return false;

        ToggleMode mode = CommandTools.getToggleMode(plugin, context, arguments, CommandArguments.MODE);

        Setting<Boolean> setting = SettingRegistry.ACCEPT_PM;
        SunUser user = plugin.getUserManager().getUserData(target);
        boolean state = mode.apply(user.getSettings().get(setting));

        user.getSettings().set(setting, state);
        plugin.getUserManager().scheduleSave(user);

        if (context.getSender() != target) {
            ChatLang.COMMAND_TOGGLE_PM_TOGGLE_TARGET.getMessage()
                .replace(Placeholders.GENERIC_STATE, Lang.getEnabledOrDisabled(state))
                .replace(Placeholders.forPlayer(target))
                .send(context.getSender());
        }

        if (!arguments.hasFlag(CommandFlags.SILENT)) {
            ChatLang.COMMAND_TOGGLE_PM_TOGGLE_NOTIFY.getMessage()
                .replace(Placeholders.GENERIC_STATE, Lang.getEnabledOrDisabled(state))
                .send(target);
        }

        return true;
    }
}
