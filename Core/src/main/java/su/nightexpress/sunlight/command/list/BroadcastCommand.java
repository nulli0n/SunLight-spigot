package su.nightexpress.sunlight.command.list;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.command.experimental.CommandContext;
import su.nightexpress.nightcore.command.experimental.argument.ArgumentTypes;
import su.nightexpress.nightcore.command.experimental.argument.ParsedArguments;
import su.nightexpress.nightcore.command.experimental.builder.DirectNodeBuilder;
import su.nightexpress.nightcore.command.experimental.node.DirectNode;
import su.nightexpress.nightcore.config.ConfigValue;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.util.Players;
import su.nightexpress.sunlight.Placeholders;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.command.CommandPerms;
import su.nightexpress.sunlight.command.CommandArguments;
import su.nightexpress.sunlight.command.CommandRegistry;
import su.nightexpress.sunlight.command.template.CommandTemplate;
import su.nightexpress.sunlight.config.Lang;

import static su.nightexpress.nightcore.util.Placeholders.WIKI_TEXT_URL;
import static su.nightexpress.nightcore.util.text.tag.Tags.*;
import static su.nightexpress.sunlight.Placeholders.GENERIC_MESSAGE;
import static su.nightexpress.sunlight.Placeholders.TAG_LINE_BREAK;

public class BroadcastCommand {

    public static final String NODE = "broadcast";

    private static String format;

    public static void load(@NotNull SunLightPlugin plugin) {
        CommandRegistry.registerDirectExecutor(NODE, (template, config) -> builder(plugin, template, config));

        CommandRegistry.addTemplate(NODE, CommandTemplate.direct(new String[]{NODE, "bc"}, NODE));
    }

    @NotNull
    public static DirectNodeBuilder builder(@NotNull SunLightPlugin plugin, @NotNull CommandTemplate template, @NotNull FileConfig config) {
        format = ConfigValue.create("Settings.Broadcast.Format",
            TAG_LINE_BREAK + LIGHT_YELLOW.enclose(BOLD.enclose("BROADCAST:")) + TAG_LINE_BREAK + LIGHT_GRAY.enclose(GENERIC_MESSAGE) + TAG_LINE_BREAK,
            "Sets message format for the '" + NODE + "' command.",
            "Text Formations: " + WIKI_TEXT_URL
        ).read(config);

        return DirectNode.builder(plugin, template.getAliases())
            .description(Lang.COMMAND_BROADCAST_DESC)
            .permission(CommandPerms.BROADCAST)
            .withArgument(ArgumentTypes.string(CommandArguments.TEXT).required().complex().localized(Lang.COMMAND_ARGUMENT_NAME_TEXT))
            .executes((context, arguments) -> execute(plugin, context, arguments))
            ;
    }

    public static boolean execute(@NotNull SunLightPlugin plugin, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        String text = arguments.getStringArgument(CommandArguments.TEXT);
        String message = format.replace(Placeholders.GENERIC_MESSAGE, text);

        plugin.getServer().getOnlinePlayers().forEach(player -> Players.sendModernMessage(player, message));
        Players.sendModernMessage(plugin.getServer().getConsoleSender(), message);
        return true;
    }
}
