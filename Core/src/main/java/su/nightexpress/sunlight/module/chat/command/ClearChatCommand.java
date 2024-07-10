package su.nightexpress.sunlight.module.chat.command;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.command.experimental.CommandContext;
import su.nightexpress.nightcore.command.experimental.argument.ParsedArguments;
import su.nightexpress.nightcore.command.experimental.builder.DirectNodeBuilder;
import su.nightexpress.nightcore.command.experimental.node.DirectNode;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.command.CommandRegistry;
import su.nightexpress.sunlight.command.template.CommandTemplate;
import su.nightexpress.sunlight.module.chat.config.ChatPerms;
import su.nightexpress.sunlight.module.chat.config.ChatLang;
import su.nightexpress.sunlight.module.chat.util.Placeholders;
import su.nightexpress.sunlight.utils.SunUtils;

import java.util.Collection;

public class ClearChatCommand {

    public static final String NODE = "clearchat";

    public static void load(@NotNull SunLightPlugin plugin) {
        CommandRegistry.registerDirectExecutor(NODE, (template, config) -> builder(plugin, template, config));

        CommandRegistry.addSimpleTemplate(NODE);
    }

    @NotNull
    public static DirectNodeBuilder builder(@NotNull SunLightPlugin plugin, @NotNull CommandTemplate template, @NotNull FileConfig config) {
        return DirectNode.builder(plugin, template.getAliases())
            .description(ChatLang.COMMAND_CLEAR_CHAT_DESC)
            .permission(ChatPerms.COMMAND_CLEARCHAT)
            .executes((context, arguments) -> execute(plugin, context, arguments))
            ;
    }

    public static boolean execute(@NotNull SunLightPlugin plugin, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Collection<? extends Player> players = plugin.getServer().getOnlinePlayers();

        for (int i = 0; i < 100; i++) {
            players.forEach(player -> player.sendMessage(" "));
        }

        ChatLang.COMMAND_CLEAR_CHAT_DONE.getMessage().replace(Placeholders.GENERIC_NAME, SunUtils.getSenderName(context.getSender())).broadcast();
        return true;
    }
}
