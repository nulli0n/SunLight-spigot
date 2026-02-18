package su.nightexpress.sunlight.module.spawns.listener;

import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.manager.AbstractListener;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.module.spawns.Spawn;
import su.nightexpress.sunlight.module.spawns.SpawnsModule;

public class SpawnListener extends AbstractListener<SunLightPlugin> {

    private final SpawnsModule module;

    public SpawnListener(@NotNull SunLightPlugin plugin, @NotNull SpawnsModule module) {
        super(plugin);
        this.module = module;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onWorldLoad(WorldLoadEvent event) {
        World world = event.getWorld();

        this.module.getSpawns().stream().filter(spawn -> spawn.isWorld(world)).forEach(spawn -> spawn.activate(world));
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onWorldUnload(WorldUnloadEvent event) {
        World world = event.getWorld();

        this.module.getSpawns().stream().filter(spawn -> spawn.isWorld(world)).forEach(Spawn::deactivate);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onSpawnJoin(PlayerJoinEvent event) {
        this.module.handleJoin(event);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onSpawnRespawn(PlayerRespawnEvent event) {
        this.module.handleRespawn(event);
    }
}
