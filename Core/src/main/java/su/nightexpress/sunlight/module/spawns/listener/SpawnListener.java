package su.nightexpress.sunlight.module.spawns.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.manager.AbstractListener;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.data.user.SunUser;
import su.nightexpress.sunlight.module.spawns.SpawnsModule;
import su.nightexpress.sunlight.module.spawns.config.SpawnsConfig;
import su.nightexpress.sunlight.module.spawns.impl.Spawn;

public class SpawnListener extends AbstractListener<SunLightPlugin> {

    private final SpawnsModule module;

    public SpawnListener(@NotNull SunLightPlugin plugin, @NotNull SpawnsModule module) {
        super(plugin);
        this.module = module;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onSpawnJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        SunUser user = this.plugin.getUserManager().getOrFetch(player);
        Spawn spawn;
        if (!user.hasPlayedBefore()) {
            if (!SpawnsConfig.NEWBIE_TELEPORT_ENABLED.get()) return;

            spawn = this.module.getNewbieSpawn(player);
        }
        else spawn = this.module.getLoginSpawn(player);

        if (spawn == null || !spawn.isValid()) return;

        spawn.teleport(player, true, true);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onSpawnRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        if (player.getBedSpawnLocation() != null && SpawnsConfig.RESPECT_PLAYER_RESPAWN_LOCATION.get()) return;

        Spawn spawn = this.module.getDeathSpawn(player);
        if (spawn == null || !spawn.isValid()) return;

        event.setRespawnLocation(spawn.getLocation());
    }
}
