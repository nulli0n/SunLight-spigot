package su.nightexpress.sunlight.module.spawns.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.manager.AbstractListener;
import su.nightexpress.sunlight.SunLight;
import su.nightexpress.sunlight.module.spawns.SpawnsModule;

public class SpawnListener extends AbstractListener<SunLight> {

    private final SpawnsModule spawnsModule;

    public SpawnListener(@NotNull SpawnsModule spawnsModule) {
        super(spawnsModule.plugin());
        this.spawnsModule = spawnsModule;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onSpawnJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        this.spawnsModule.getSpawnByLogin(player).ifPresent(spawn -> player.teleport(spawn.getLocation()));
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onSpawnRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        this.spawnsModule.getSpawnByDeath(player).ifPresent(spawn -> event.setRespawnLocation(spawn.getLocation()));
    }
}
