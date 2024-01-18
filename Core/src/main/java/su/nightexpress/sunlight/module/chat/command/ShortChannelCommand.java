package su.nightexpress.sunlight.module.chat.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.CommandResult;
import su.nightexpress.sunlight.module.GeneralModuleCommand;
import su.nightexpress.sunlight.module.chat.ChatChannel;
import su.nightexpress.sunlight.module.chat.ChatModule;
import su.nightexpress.sunlight.module.chat.config.ChatPerms;
import su.nightexpress.sunlight.module.chat.config.ChatLang;

import java.util.HashSet;
import java.util.stream.Stream;

public class ShortChannelCommand extends GeneralModuleCommand<ChatModule> {

    private final ChatChannel channel;

    public ShortChannelCommand(@NotNull ChatModule chatModule, @NotNull ChatChannel channel) {
        super(chatModule, new String[]{channel.getCommandAlias()}, ChatPerms.COMMAND_CHANNEL + channel.getId());
        this.setDescription(plugin.getMessage(ChatLang.COMMAND_SHORT_CHANNEL_DESC));
        this.setUsage(plugin.getMessage(ChatLang.COMMAND_SHORT_CHANNEL_USAGE));
        this.setPlayerOnly(true);

        this.channel = channel;
    }

    @Override
    protected void onExecute(@NotNull CommandSender sender, @NotNull CommandResult result) {
        Player player = (Player) sender;

        if (result.length() == 0) {
            this.module.getChannelManager().setActiveChannel(player, this.channel);
            return;
        }

        String message = String.join(" ", Stream.of(result.getArgs()).toList());
        String prefix = this.channel.getMessagePrefix();

        plugin.runTaskAsync(task -> {
            AsyncPlayerChatEvent chatEvent = new AsyncPlayerChatEvent(true, player, prefix + message, new HashSet<>(plugin.getServer().getOnlinePlayers()));
            plugin.getPluginManager().callEvent(chatEvent);
        });

    }
}
