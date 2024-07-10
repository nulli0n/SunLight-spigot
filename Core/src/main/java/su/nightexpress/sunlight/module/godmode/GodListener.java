package su.nightexpress.sunlight.module.godmode;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.manager.AbstractListener;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.module.godmode.config.GodConfig;
import su.nightexpress.sunlight.module.godmode.config.GodLang;

public class GodListener extends AbstractListener<SunLightPlugin> {

    private final GodModule module;

    public GodListener(@NotNull SunLightPlugin plugin, @NotNull GodModule module) {
        super(plugin);
        this.module = module;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onWorldChange(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();

        if (!this.module.isAllowedWorld(player) && this.module.hasAnyGod(player)) {
            GodLang.NOTIFY_BAD_WORLD.getMessage().send(player);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onDamageIncoming(EntityDamageEvent event) {
        Entity victim = event.getEntity();
        if (!(victim instanceof Player player)) return;
        if (!this.module.isAllowedWorld(player)) return;

        if (this.module.hasClassicGod(player)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onDamageOutgoing(EntityDamageByEntityEvent event) {
        Entity damager = event.getDamager();
        if (damager instanceof Projectile projectile && projectile.getShooter() instanceof LivingEntity living) damager = living;

        if (!(damager instanceof Player player)) return;
        if (!this.module.isAllowedWorld(player)) return;
        if (!this.module.hasClassicGod(player)) return;

        Entity victim = event.getEntity();
        if (victim instanceof Player && GodConfig.OUT_DAMAGE_PLAYERS.get()) return;
        else if (GodConfig.OUT_DAMAGE_MOBS.get()) return;

        event.setCancelled(true);
        GodLang.NOTIFY_DISABLED_DAMAGE.getMessage().send(player);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onFoodChange(FoodLevelChangeEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (!this.module.hasFoodGod(player)) return;

        event.setCancelled(true);
    }
}
