package su.nightexpress.sunlight.module.chat.module.spy;

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
import su.nightexpress.sunlight.module.chat.config.ChatLang;
import su.nightexpress.sunlight.module.chat.config.ChatPerms;

import java.util.function.Function;

public class SpyCommands {

    public static final String   NODE_LOGGER = "spy_logger";
    public static final Function<SpyType, String> NODE_MODE   = spyType -> "spy_mode_" + spyType.name().toLowerCase();

    public static void load(@NotNull SunLightPlugin plugin, @NotNull SpyManager spyManager) {
        CommandRegistry.registerDirectExecutor(NODE_LOGGER, (template, config) -> builderLogger(plugin, spyManager, template, config));

        for (SpyType spyType : SpyType.values()) {
            CommandRegistry.registerDirectExecutor(NODE_MODE.apply(spyType), (template, config) -> builderMode(plugin, spyManager, template, config, spyType));
        }

        CommandRegistry.addTemplate("spylogger", CommandTemplate.direct(new String[]{"spylogger"}, NODE_LOGGER));

        CommandRegistry.addTemplate("spymode", CommandTemplate.group(new String[]{"spymode"},
            "SpyMode commands.",
            ChatPerms.PREFIX_COMMAND + "spymode",
            CommandTemplate.direct(new String[]{"chat"}, NODE_MODE.apply(SpyType.CHAT)),
            CommandTemplate.direct(new String[]{"command"}, NODE_MODE.apply(SpyType.COMMAND)),
            CommandTemplate.direct(new String[]{"social"}, NODE_MODE.apply(SpyType.SOCIAL))
        ));

        CommandRegistry.addTemplate("chatspy", CommandTemplate.direct(new String[]{"chatspy"}, NODE_MODE.apply(SpyType.CHAT)));
        CommandRegistry.addTemplate("commandspy", CommandTemplate.direct(new String[]{"commandspy"}, NODE_MODE.apply(SpyType.COMMAND)));
        CommandRegistry.addTemplate("socialspy", CommandTemplate.direct(new String[]{"socialspy"}, NODE_MODE.apply(SpyType.SOCIAL)));
    }

    @NotNull
    public static DirectNodeBuilder builderLogger(@NotNull SunLightPlugin plugin, @NotNull SpyManager spyManager, @NotNull CommandTemplate template, @NotNull FileConfig config) {
        return DirectNode.builder(plugin, template.getAliases())
            .description(ChatLang.COMMAND_SPY_LOGGER_DESC)
            .permission(ChatPerms.COMMAND_SPY_LOGGER)
            .withArgument(CommandArguments.enumed(CommandArguments.TYPE, SpyType.class).required().localized(ChatLang.COMMAND_ARGUMENT_NAME_TYPE))
            .withArgument(ArgumentTypes.playerName(CommandArguments.PLAYER).required())
            .withArgument(CommandArguments.toggleMode(CommandArguments.MODE))
            .executes((context, arguments) -> toggleLogger(plugin, spyManager, context, arguments))
            ;
    }

    public static boolean toggleLogger(@NotNull SunLightPlugin plugin, @NotNull SpyManager spyManager, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        SpyType spyType = arguments.getArgument(CommandArguments.TYPE, SpyType.class);
        Player player = CommandTools.getTarget(plugin, context, arguments, CommandArguments.PLAYER, true);
        if (player == null) return false;

        ToggleMode mode = CommandTools.getToggleMode(plugin, context, arguments, CommandArguments.MODE);

        SunUser user = plugin.getUserManager().getUserData(player);
        boolean state = mode.apply(user.getSettings().get(spyType.getSettingLog()));

        user.getSettings().set(spyType.getSettingLog(), state);
        plugin.getUserManager().scheduleSave(user);

        ChatLang.COMMAND_SPY_LOGGER_DONE.getMessage()
            .replace(Placeholders.forPlayer(player))
            .replace(Placeholders.GENERIC_TYPE, ChatLang.SPY_TYPE.getLocalized(spyType))
            .replace(Placeholders.GENERIC_STATE, Lang.getEnabledOrDisabled(state))
            .send(context.getSender());

        return true;
    }



    @NotNull
    public static DirectNodeBuilder builderMode(@NotNull SunLightPlugin plugin, @NotNull SpyManager spyManager, @NotNull CommandTemplate template, @NotNull FileConfig config,
                                                @NotNull SpyType spyType) {
        return DirectNode.builder(plugin, template.getAliases())
            .description(ChatLang.COMMAND_SPY_MODE_DESC.getString().replace(Placeholders.GENERIC_TYPE, ChatLang.SPY_TYPE.getLocalized(spyType)))
            .permission(ChatPerms.COMMAND_SPY_MODE.apply(spyType))
            //.withArgument(CommandArguments.enumed(CommandArguments.TYPE, SpyType.class).required().localized(ChatLang.COMMAND_ARGUMENT_NAME_TYPE))
            .withArgument(CommandArguments.toggleMode(CommandArguments.MODE))
            .withArgument(ArgumentTypes.playerName(CommandArguments.PLAYER).permission(ChatPerms.COMMAND_SPY_MODE_OTHERS.apply(spyType)))
            .withFlag(CommandFlags.silent().permission(ChatPerms.COMMAND_SPY_MODE_OTHERS.apply(spyType)))
            .executes((context, arguments) -> toggleMode(plugin, spyManager, context, arguments, spyType))
            ;
    }

    public static boolean toggleMode(@NotNull SunLightPlugin plugin, @NotNull SpyManager spyManager, @NotNull CommandContext context, @NotNull ParsedArguments arguments,
                                     @NotNull SpyType spyType) {
        //SpyType spyType = arguments.getArgument(CommandArguments.TYPE, SpyType.class);
        Player player = CommandTools.getTarget(plugin, context, arguments, CommandArguments.PLAYER, true);
        if (player == null) return false;

        ToggleMode mode = CommandTools.getToggleMode(plugin, context, arguments, CommandArguments.MODE);

        SunUser user = plugin.getUserManager().getUserData(player);
        boolean state = mode.apply(user.getSettings().get(spyType.getSettingChat()));

        user.getSettings().set(spyType.getSettingChat(), state);
        plugin.getUserManager().scheduleSave(user);

        if (context.getSender() != player) {
            ChatLang.COMMAND_SPY_MODE_DONE_OTHERS.getMessage()
                .replace(Placeholders.forPlayer(player))
                .replace(Placeholders.GENERIC_TYPE, ChatLang.SPY_TYPE.getLocalized(spyType))
                .replace(Placeholders.GENERIC_STATE, Lang.getEnabledOrDisabled(state))
                .send(context.getSender());
        }

        if (!arguments.hasFlag(CommandFlags.SILENT)) {
            ChatLang.COMMAND_SPY_MODE_DONE_NOTIFY.getMessage()
                .replace(Placeholders.GENERIC_TYPE, ChatLang.SPY_TYPE.getLocalized(spyType))
                .replace(Placeholders.GENERIC_STATE, Lang.getEnabledOrDisabled(state))
                .send(player);
        }

        return true;
    }
}
