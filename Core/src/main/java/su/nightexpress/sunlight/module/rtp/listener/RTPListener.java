package su.nightexpress.sunlight.module.rtp.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.manager.AbstractListener;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.module.rtp.RTPModule;

public class RTPListener extends AbstractListener<SunLightPlugin> {

    private final RTPModule module;

    public RTPListener(@NotNull SunLightPlugin plugin, @NotNull RTPModule module) {
        super(plugin);
        this.module = module;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerQuit(@NotNull PlayerQuitEvent event) {
        this.module.stopSearch(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerQuit(@NotNull PlayerDeathEvent event) {
        this.module.stopSearch(event.getEntity());
    }
}
