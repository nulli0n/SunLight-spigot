package su.nightexpress.sunlight.module.warmups.listener;

import org.bukkit.block.Block;
import org.bukkit.damage.DamageSource;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.manager.AbstractListener;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.api.event.SunlightPlayerTeleportEvent;
import su.nightexpress.sunlight.module.warmups.WarmupsModule;
import su.nightexpress.sunlight.module.warmups.config.WarmupsConfig;
import su.nightexpress.sunlight.module.warmups.config.WarmupsPerms;
import su.nightexpress.sunlight.teleport.TeleportContext;
import su.nightexpress.sunlight.teleport.TeleportFlag;
import su.nightexpress.sunlight.teleport.TeleportType;

public class WarmupsListener extends AbstractListener<SunLightPlugin> {

    private final WarmupsModule module;

    public WarmupsListener(@NotNull SunLightPlugin plugin, @NotNull WarmupsModule module) {
        super(plugin);
        this.module = module;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onTeleport(SunlightPlayerTeleportEvent event) {
        TeleportContext context = event.getContext();

        if (context.hasSender()) return;
        if (context.hasFlag(TeleportFlag.BYPASS_WARMUP)) return;

        Player player = context.getTarget();
        if (player.hasPermission(WarmupsPerms.BYPASS_TELEPORT)) return;

        TeleportType type = event.getType();
        if (!this.module.canHandleTeleport(type)) return;

        event.setIntercepted(true);

        this.module.handleTeleport(context, type);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onWarmupQuit(PlayerQuitEvent event) {
        this.module.cancelWarmup(event.getPlayer(), true);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onWarmupDeath(PlayerDeathEvent event) {
        this.module.cancelWarmup(event.getEntity(), true);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onWarmupTeleport(PlayerTeleportEvent event) {
        this.module.cancelWarmup(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onWarmupInteract(PlayerInteractEvent event) {
        if (!WarmupsConfig.WARMUP_CANCEL_ON_INTERACT.get()) return;

        Player player = event.getPlayer();
        if (!this.module.hasWarmup(player)) return;

        if (event.useItemInHand() == Event.Result.DENY && event.useInteractedBlock() == Event.Result.DENY) return;

        var action = event.getAction();
        if (action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK) {
            this.module.cancelWarmup(player);
            return;
        }

        Block block = event.getClickedBlock();
        if (block != null && block.getType().isInteractable()) {
            this.module.cancelWarmup(player);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onWarmupDamage(EntityDamageEvent event) {
        if (!WarmupsConfig.WARMUP_CANCEL_ON_DAMAGE.get()) return;

        if (event.getEntity() instanceof Player victim && this.module.hasWarmup(victim)) {
            this.module.cancelWarmup(victim);
        }

        DamageSource source = event.getDamageSource();
        if (source.getCausingEntity() instanceof Player damager && this.module.hasWarmup(damager)) {
            this.module.cancelWarmup(damager);
        }
    }
}
