package su.nightexpress.sunlight.module.rtp.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.manager.AbstractListener;
import su.nightexpress.sunlight.SunLight;
import su.nightexpress.sunlight.module.rtp.RTPModule;

public class RTPListener extends AbstractListener<SunLight> {

    private final RTPModule module;

    public RTPListener(@NotNull RTPModule module) {
        super(module.plugin());
        this.module = module;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerQuit(@NotNull PlayerQuitEvent e) {
        this.module.stopSearch(e.getPlayer());
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerQuit(@NotNull PlayerDeathEvent e) {
        this.module.stopSearch(e.getEntity());
    }
}
