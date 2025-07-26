package su.nightexpress.sunlight.module.afk.listener;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EquipmentSlot;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.manager.AbstractListener;
import su.nightexpress.sunlight.Placeholders;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.api.event.PlayerPrivateMessageEvent;
import su.nightexpress.sunlight.api.event.PlayerTeleportRequestEvent;
import su.nightexpress.sunlight.module.ptp.TeleportRequest;
import su.nightexpress.sunlight.module.afk.AfkModule;
import su.nightexpress.sunlight.module.afk.config.AfkConfig;
import su.nightexpress.sunlight.module.afk.config.AfkLang;

public class AfkListener extends AbstractListener<SunLightPlugin> {

    private final AfkModule module;

    public AfkListener(@NotNull SunLightPlugin plugin, @NotNull AfkModule module) {
        super(plugin);
        this.module = module;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onAfkNotifyPM(PlayerPrivateMessageEvent event) {
        if (!(event.getTarget() instanceof Player target)) return;

        if (this.module.isAfk(target)) {
            CommandSender from = event.getSender();
            AfkLang.AFK_NOTIFY_PM.getMessage().replace(Placeholders.forPlayer(target)).send(from);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onAfkNotifyTeleport(PlayerTeleportRequestEvent event) {
        TeleportRequest request = event.getRequest();
        Player sender = request.getSender();
        Player target = request.getTarget();
        if (sender == null || target == null) return;

        if (this.module.isAfk(target)) {
            AfkLang.AFK_NOTIFY_TELEPORT.getMessage().replace(Placeholders.forPlayer(target)).send(sender);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onStateJoin(PlayerJoinEvent event) {
        this.module.track(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onStateQuit(PlayerQuitEvent event) {
        this.module.untrack(event.getPlayer(), true);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onStateDeath(PlayerDeathEvent event) {
        this.module.exitAfk(event.getEntity());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onActivityInteract(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;

        this.module.trackActivity(event.getPlayer(), AfkConfig.WAKE_UP_ACTIVITY_POINTS_INTERACTION.get());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onActivityChat(AsyncPlayerChatEvent event) {
        this.module.trackActivity(event.getPlayer(), AfkConfig.WAKE_UP_ACTIVITY_POINTS_CHAT.get());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onActivityCommand(PlayerCommandPreprocessEvent event) {
        this.module.trackActivity(event.getPlayer(), AfkConfig.WAKE_UP_ACTIVITY_POINTS_COMMAND.get());
    }
}
