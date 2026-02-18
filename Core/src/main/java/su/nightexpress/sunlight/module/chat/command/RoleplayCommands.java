package su.nightexpress.sunlight.module.chat.command;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.commands.Arguments;
import su.nightexpress.nightcore.commands.context.CommandContext;
import su.nightexpress.nightcore.commands.context.ParsedArguments;
import su.nightexpress.nightcore.util.Players;
import su.nightexpress.nightcore.util.placeholder.CommonPlaceholders;
import su.nightexpress.nightcore.util.placeholder.PlaceholderContext;
import su.nightexpress.sunlight.SLPlaceholders;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.command.CommandArguments;
import su.nightexpress.sunlight.command.provider.type.AbstractCommandProvider;
import su.nightexpress.sunlight.config.Lang;
import su.nightexpress.sunlight.module.chat.core.ChatLang;
import su.nightexpress.sunlight.module.chat.ChatModule;
import su.nightexpress.sunlight.module.chat.core.ChatPerms;

public class RoleplayCommands extends AbstractCommandProvider {

    private final ChatModule module;

    public RoleplayCommands(@NotNull SunLightPlugin plugin, @NotNull ChatModule module) {
        super(plugin);
        this.module = module;
    }

    @Override
    public void registerDefaults() {
        this.registerLiteral("me", true, new String[]{"me"}, builder -> builder
            .playerOnly()
            .description(ChatLang.COMMAND_ME_DESC)
            .permission(ChatPerms.COMMAND_ME)
            .withArguments(Arguments.greedyString(CommandArguments.TEXT).localized(Lang.COMMAND_ARGUMENT_NAME_TEXT))
            .executes(this::showAction)
        );
    }

    private boolean showAction(@NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Player player = context.getPlayerOrThrow();
        String text = arguments.getString(CommandArguments.TEXT);
        String format = this.module.getSettings().getRoleplayMeFormat();

        String formatted = PlaceholderContext.builder()
            .with(SLPlaceholders.GENERIC_MESSAGE, () -> text)
            .with(CommonPlaceholders.PLAYER.resolver(player))
            .build()
            .apply(format);

        this.plugin.getServer().getOnlinePlayers().forEach(other -> Players.sendMessage(other, formatted));
        return true;
    }
}
