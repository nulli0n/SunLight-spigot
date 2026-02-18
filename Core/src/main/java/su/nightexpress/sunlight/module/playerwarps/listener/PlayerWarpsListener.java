package su.nightexpress.sunlight.module.playerwarps.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;
import org.jspecify.annotations.NonNull;
import su.nightexpress.nightcore.manager.AbstractListener;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.module.playerwarps.PlayerWarpsModule;

public class PlayerWarpsListener extends AbstractListener<SunLightPlugin> {

    private final PlayerWarpsModule module;

    public PlayerWarpsListener(@NonNull SunLightPlugin plugin, @NonNull PlayerWarpsModule module) {
        super(plugin);
        this.module = module;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onWorldLoad(WorldLoadEvent event) {
        this.module.handleWorldLoad(event);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onWorldUnload(WorldUnloadEvent event) {
        this.module.handleWorldUnload(event);
    }
}
