package su.nightexpress.sunlight.module.chat.discord;

import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.api.ListenerPriority;
import github.scarsz.discordsrv.api.Subscribe;
import github.scarsz.discordsrv.api.events.DiscordGuildMessagePreProcessEvent;
import github.scarsz.discordsrv.api.events.GameChatMessagePreProcessEvent;
import github.scarsz.discordsrv.dependencies.jda.api.entities.TextChannel;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.manager.SimpleManager;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.data.user.SunUser;
import su.nightexpress.sunlight.module.chat.ChatChannel;
import su.nightexpress.sunlight.module.chat.ChatModule;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class DiscordHandler extends SimpleManager<SunLightPlugin> {

    private final ChatModule module;

    public DiscordHandler(@NotNull SunLightPlugin plugin, @NotNull ChatModule module) {
        super(plugin);
        this.module = module;
    }

    @Override
    protected void onLoad() {
        DiscordSRV.api.subscribe(this);
    }

    @Override
    protected void onShutdown() {
        DiscordSRV.api.unsubscribe(this);
    }

    public void sendToChannel(@NotNull ChatChannel channel, @NotNull String message) {
        TextChannel textChannel = DiscordSRV.getPlugin().getDestinationTextChannelForGameChannelName(channel.getId());
        if (textChannel != null) {
            textChannel.sendMessage(message).queue();
        }
    }

    // From in-game to Discord
    @Subscribe(priority = ListenerPriority.HIGHEST)
    public void onChatMessageFromInGame(GameChatMessagePreProcessEvent event) {
        event.setCancelled(true);
    }

    // From Discord to in-game
    @Subscribe(priority = ListenerPriority.HIGHEST)
    public void onChatMessageFromDiscord(DiscordGuildMessagePreProcessEvent event) {
        event.setCancelled(true);

        UUID playerId = DiscordSRV.getPlugin().getAccountLinkManager().getUuid(event.getAuthor().getId());
        if (playerId == null) return;

        String channelId = DiscordSRV.getPlugin().getDestinationGameChannelNameForTextChannel(event.getChannel());
        ChatChannel channel = this.module.getChannelManager().getChannel(channelId);
        if (channel == null) return;

        SunUser user = plugin.getUserManager().getUserData(playerId);
        if (user == null) return;

        Player player = user.getPlayer();
        if (player == null) player = plugin.getSunNMS().loadPlayerData(playerId, user.getName());

        String message = channel.getMessagePrefix() + event.getMessage().getContentRaw();

        Set<Player> recipients = new HashSet<>(this.plugin.getServer().getOnlinePlayers());
        AsyncPlayerChatEvent chatEvent = new AsyncPlayerChatEvent(true, player, message, recipients);

        this.plugin.runTaskAsync(task -> {
            this.plugin.getPluginManager().callEvent(chatEvent);
        });
    }
}
