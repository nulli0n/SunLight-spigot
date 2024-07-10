package su.nightexpress.sunlight.module.chat.module.spy;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.manager.AbstractListener;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.api.event.PlayerPrivateMessageEvent;
import su.nightexpress.sunlight.module.chat.event.AsyncSunlightPlayerChatEvent;

public class SpyListener extends AbstractListener<SunLightPlugin> {

    private final SpyManager spyManager;

    public SpyListener(@NotNull SunLightPlugin plugin, @NotNull SpyManager spyManager) {
        super(plugin);
        this.spyManager = spyManager;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onSpyChat(AsyncSunlightPlayerChatEvent event) {
        this.spyManager.handleSpyMode(event);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onSpyCommand(PlayerCommandPreprocessEvent event) {
        this.spyManager.handleSpyMode(event);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onSpySocial(PlayerPrivateMessageEvent event) {
        this.spyManager.handleSpyMode(event);
    }
}
