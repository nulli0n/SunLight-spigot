package su.nightexpress.sunlight.module.chat.listener;


import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.manager.AbstractListener;
import su.nightexpress.sunlight.SunLight;
import su.nightexpress.sunlight.api.event.PlayerPrivateMessageEvent;
import su.nightexpress.sunlight.data.impl.SunUser;
import su.nightexpress.sunlight.module.chat.ChatChannel;
import su.nightexpress.sunlight.module.chat.ChatModule;
import su.nightexpress.sunlight.module.chat.config.ChatConfig;
import su.nightexpress.sunlight.module.chat.event.AsyncSunlightPlayerChatEvent;
import su.nightexpress.sunlight.module.chat.util.ChatUtils;

public class ChatListener extends AbstractListener<SunLight> {

    private final ChatModule chatModule;

    public ChatListener(@NotNull ChatModule chatModule) {
        super(chatModule.plugin());
        this.chatModule = chatModule;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        SunUser user = plugin.getUserManager().getUserData(player);
        this.chatModule.getChannelsAvailable(player).stream().filter(ChatChannel::isAutoJoin).forEach(channel -> {
            this.chatModule.joinChannel(player, channel, ChatConfig.SILENT_CHANNEL_JOIN_ON_LOGIN.get());
        });
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerQuit(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        ChatUtils.clear(player);
        ChatChannel.PLAYER_CHANNEL_ACTIVE.remove(player.getName());
        ChatChannel.PLAYER_CHANNELS.remove(player.getName());
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onChatProcessMessageLowest(AsyncPlayerChatEvent e) {
        if (ChatConfig.CHAT_EVENT_PRIORITY.get() != EventPriority.LOWEST) return;
        this.chatModule.handleChatEvent(e);
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onChatProcessMessageLow(AsyncPlayerChatEvent e) {
        if (ChatConfig.CHAT_EVENT_PRIORITY.get() != EventPriority.LOW) return;
        this.chatModule.handleChatEvent(e);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onChatProcessMessageNormal(AsyncPlayerChatEvent e) {
        if (ChatConfig.CHAT_EVENT_PRIORITY.get() != EventPriority.NORMAL) return;
        this.chatModule.handleChatEvent(e);
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onChatProcessMessageHigh(AsyncPlayerChatEvent e) {
        if (ChatConfig.CHAT_EVENT_PRIORITY.get() != EventPriority.HIGH) return;
        this.chatModule.handleChatEvent(e);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onChatProcessMessageHighest(AsyncPlayerChatEvent e) {
        if (ChatConfig.CHAT_EVENT_PRIORITY.get() != EventPriority.HIGHEST) return;
        this.chatModule.handleChatEvent(e);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onChatProcessCommand(PlayerCommandPreprocessEvent e) {
        this.chatModule.handleCommandEvent(e);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onSpyChat(AsyncSunlightPlayerChatEvent e) {
        if (!ChatConfig.SPY_ENABLED.get()) return;
        this.chatModule.handleSpyMode(e);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onSpyCommand(PlayerCommandPreprocessEvent e) {
        if (!ChatConfig.SPY_ENABLED.get()) return;
        this.chatModule.handleSpyMode(e);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onSpySocial(PlayerPrivateMessageEvent e) {
        if (!ChatConfig.SPY_ENABLED.get()) return;
        this.chatModule.handleSpyMode(e);
    }
}
