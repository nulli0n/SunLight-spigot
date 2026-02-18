package su.nightexpress.sunlight.module.afk.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EquipmentSlot;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.manager.AbstractListener;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.module.afk.ActivityType;
import su.nightexpress.sunlight.module.afk.AfkModule;

public class AfkListener extends AbstractListener<SunLightPlugin> {

    private final AfkModule module;

    public AfkListener(@NotNull SunLightPlugin plugin, @NotNull AfkModule module) {
        super(plugin);
        this.module = module;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoin(PlayerJoinEvent event) {
        this.module.track(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onQuit(PlayerQuitEvent event) {
        this.module.untrack(event.getPlayer(), true);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onIdleDeath(PlayerDeathEvent event) {
        this.module.exitAfk(event.getEntity(), false); // Force exit AFK mode on death.
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onIdleTeleport(PlayerTeleportEvent event) {
        this.module.exitAfk(event.getPlayer(), false); // Force exit AFK mode on teleportations.
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onActivityInteract(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;

        this.module.trackActivity(event.getPlayer(), ActivityType.INTERACT);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onActivityCommand(PlayerCommandPreprocessEvent event) {
        this.module.trackActivity(event.getPlayer(), ActivityType.COMMAND);
    }
}
