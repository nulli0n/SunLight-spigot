package su.nightexpress.sunlight.module.essential.command;

import org.bukkit.permissions.Permission;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.commands.Arguments;
import su.nightexpress.nightcore.commands.context.CommandContext;
import su.nightexpress.nightcore.commands.context.ParsedArguments;
import su.nightexpress.nightcore.locale.LangEntry;
import su.nightexpress.nightcore.locale.entry.TextLocale;
import su.nightexpress.nightcore.util.Players;
import su.nightexpress.nightcore.util.bridge.wrapper.NightComponent;
import su.nightexpress.nightcore.util.text.night.NightMessage;
import su.nightexpress.sunlight.SLPlaceholders;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.command.CommandArguments;
import su.nightexpress.sunlight.command.provider.type.AbstractCommandProvider;
import su.nightexpress.sunlight.config.Lang;
import su.nightexpress.sunlight.module.essential.EssentialPerms;

import java.util.List;

import static su.nightexpress.nightcore.util.text.night.wrapper.TagWrappers.BR;

public class BroadcastCommandProvider extends AbstractCommandProvider {

    private static final Permission PERMISSION  = EssentialPerms.COMMAND.permission("broadcast");
    private static final TextLocale DESCRIPTION = LangEntry.builder("Command.Broadcast.Desc").text("Broadcast a message.");

    private final String format;

    public BroadcastCommandProvider(@NotNull SunLightPlugin plugin, @NotNull List<String> format) {
        super(plugin);
        this.format = String.join(BR, format);
    }

    @Override
    public void registerDefaults() {
        this.registerLiteral("broadcast", true, new String[]{"broadcast", "bc"}, builder -> builder
            .description(DESCRIPTION)
            .permission(PERMISSION)
            .withArguments(Arguments.greedyString(CommandArguments.TEXT).localized(Lang.COMMAND_ARGUMENT_NAME_TEXT.text()))
            .executes(this::broadcast)
        );
    }

    private boolean broadcast(@NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        String text = arguments.getString(CommandArguments.TEXT);
        String message = this.format.replace(SLPlaceholders.GENERIC_MESSAGE, text);
        NightComponent component = NightMessage.parse(message);

        Players.getOnline().forEach(player -> Players.sendMessage(player, component));
        Players.sendMessage(plugin.getServer().getConsoleSender(), component);
        return true;
    }
}
