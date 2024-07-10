package su.nightexpress.sunlight.module.ptp.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.manager.AbstractListener;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.module.ptp.PTPModule;

public class PTPListener extends AbstractListener<SunLightPlugin> {

    private final PTPModule module;

    public PTPListener(@NotNull SunLightPlugin plugin, @NotNull PTPModule module) {
        super(plugin);
        this.module = module;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onQuit(@NotNull PlayerQuitEvent event) {
        this.module.clearRequests(event.getPlayer());
    }
}
