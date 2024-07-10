package su.nightexpress.sunlight.module.chat.module.announcer;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.manager.AbstractListener;
import su.nightexpress.sunlight.SunLightPlugin;

import java.util.UUID;

public class AnnounceListener extends AbstractListener<SunLightPlugin> {

    private final AnnounceManager manager;

    public AnnounceListener(@NotNull SunLightPlugin plugin, @NotNull AnnounceManager manager) {
        super(plugin);
        this.manager = manager;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onQuit(PlayerQuitEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        this.manager.getAnnouncers().forEach(announcer -> announcer.clearIndex(uuid));
    }
}
