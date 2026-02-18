package su.nightexpress.sunlight.module.homes.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.manager.AbstractListener;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.module.homes.HomesModule;

public class HomeListener extends AbstractListener<SunLightPlugin> {

    private final HomesModule module;

    public HomeListener(@NotNull SunLightPlugin plugin, @NotNull HomesModule module) {
        super(plugin);
        this.module = module;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        this.module.handleRespawn(event);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onBedModeInteract(PlayerInteractEvent event) {
        this.module.handleBedInteract(event);
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
