package su.nightexpress.sunlight.module.chat.command;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.commands.context.CommandContext;
import su.nightexpress.nightcore.commands.context.ParsedArguments;
import su.nightexpress.sunlight.SLPlaceholders;
import su.nightexpress.sunlight.SLUtils;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.command.CommandArguments;
import su.nightexpress.sunlight.command.provider.type.AbstractCommandProvider;
import su.nightexpress.sunlight.module.chat.core.ChatLang;
import su.nightexpress.sunlight.module.chat.ChatModule;
import su.nightexpress.sunlight.module.chat.core.ChatPerms;

import java.util.Collection;

public class ClearChatCommandProvider extends AbstractCommandProvider {

    private final ChatModule module;

    public ClearChatCommandProvider(@NotNull SunLightPlugin plugin, @NotNull ChatModule module) {
        super(plugin);
        this.module = module;
    }

    @Override
    public void registerDefaults() {
        this.registerLiteral("clearchat", true, new String[]{"clearchat"}, builder -> builder
            .description(ChatLang.COMMAND_CLEAR_CHAT_DESC)
            .permission(ChatPerms.COMMAND_CLEARCHAT)
            .executes(this::clearChat)
            .withFlags(CommandArguments.FLAG_SILENT)
        );
    }

    private boolean clearChat(@NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Collection<? extends Player> players = this.plugin.getServer().getOnlinePlayers();

        for (int i = 0; i < 100; i++) {
            players.forEach(player -> player.sendMessage(" "));
        }

        if (!context.hasFlag(CommandArguments.FLAG_SILENT)) {
            this.module.broadcastPrefixed(ChatLang.CLEAR_CHAT_NOTIFY, replacer -> replacer.with(
                SLPlaceholders.GENERIC_NAME, () -> SLUtils.getSenderName(context.getSender())));
        }
        return true;
    }
}
