package su.nightexpress.sunlight.module.backlocation.listener;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.manager.AbstractListener;
import su.nightexpress.nightcore.util.Players;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.module.backlocation.BackLocationModule;
import su.nightexpress.sunlight.module.backlocation.config.BackLocationConfig;
import su.nightexpress.sunlight.module.backlocation.config.BackLocationPerms;
import su.nightexpress.sunlight.module.backlocation.data.LocationType;

public class BackLocationListener extends AbstractListener<SunLightPlugin> {

    private final BackLocationModule module;

    public BackLocationListener(@NotNull SunLightPlugin plugin, @NotNull BackLocationModule module) {
        super(plugin);
        this.module = module;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBackLocationSave(PlayerTeleportEvent event) {
        if (!BackLocationConfig.PREVIOUS_ENABLED.get()) return;

        Location destination = event.getTo();
        if (destination == null) return;

        Player player = event.getPlayer();
        World world = player.getWorld();

        if (!Players.isReal(player)) return;
        if (!player.hasPermission(BackLocationPerms.BYPASS_PREVIOUS_CAUSES) && this.module.isDisabledCause(event.getCause())) {
            return;
        }
        if (!player.hasPermission(BackLocationPerms.BYPASS_PREVIOUS_WORLDS) && this.module.isDisabledWorld(world, LocationType.PREVIOUS)) {
            return;
        }

        Location from = event.getFrom();
        World toWorld = destination.getWorld();
        if (world == toWorld && from.distance(destination) <= BackLocationConfig.PREVIOUS_MIN_DISTANCE_DIFFERENCE.get()) {
            if (this.module.hasLocation(player, LocationType.PREVIOUS)) return;
        }

        this.module.saveLocation(player, from, LocationType.PREVIOUS);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onDeathLocationSave(PlayerDeathEvent event) {
        if (!BackLocationConfig.DEATH_ENABLED.get()) return;

        Player player = event.getEntity();
        if (!player.hasPermission(BackLocationPerms.BYPASS_DEATH_WORLDS) && this.module.isDisabledWorld(player.getWorld(), LocationType.DEATH)) {
            return;
        }

        this.module.saveLocation(player, player.getLocation(), LocationType.DEATH);
    }
}
