package su.nightexpress.sunlight.module.chat.module.deathmessage;

import org.bukkit.damage.DamageSource;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.manager.AbstractListener;
import su.nightexpress.nightcore.util.Players;
import su.nightexpress.sunlight.SunLightPlugin;

public class DeathMessageListener extends AbstractListener<SunLightPlugin> {

    private final DeathMessageManager manager;

    public DeathMessageListener(@NotNull SunLightPlugin plugin, @NotNull DeathMessageManager manager) {
        super(plugin);
        this.manager = manager;
    }

    @SuppressWarnings("UnstableApiUsage")
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDeath(PlayerDeathEvent event) {
        event.setDeathMessage(null);

        Player player = event.getEntity();
        EntityDamageEvent lastEvent = player.getLastDamageCause();
        if (lastEvent == null) return;

        DamageSource damageSource = lastEvent.getDamageSource();
        String message = this.manager.getMessage(player, damageSource);
        if (message == null) return;

        this.manager.getRecievers(player).forEach(entity -> Players.sendModernMessage(entity, message));
    }
}
