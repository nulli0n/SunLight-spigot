package su.nightexpress.sunlight.module.afk.listener;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.manager.AbstractListener;
import su.nightexpress.sunlight.Placeholders;
import su.nightexpress.sunlight.SunLight;
import su.nightexpress.sunlight.api.event.PlayerPrivateMessageEvent;
import su.nightexpress.sunlight.api.event.PlayerTeleportRequestEvent;
import su.nightexpress.sunlight.command.teleport.impl.TeleportRequest;
import su.nightexpress.sunlight.module.afk.AfkModule;
import su.nightexpress.sunlight.module.afk.config.AfkLang;

public class AfkListener extends AbstractListener<SunLight> {

    private final AfkModule module;

    public AfkListener(@NotNull AfkModule module) {
        super(module.plugin());
        this.module = module;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onAfkNotifyPM(PlayerPrivateMessageEvent e) {
        if (!(e.getTarget() instanceof Player target)) return;

        if (this.module.isAfk(target)) {
            CommandSender from = e.getSender();
            this.plugin.getMessage(AfkLang.AFK_NOTIFY_PM).replace(Placeholders.Player.replacer(target)).send(from);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onAfkNotifyTeleport(PlayerTeleportRequestEvent e) {
        TeleportRequest request = e.getRequest();
        Player from = plugin.getServer().getPlayer(request.getSender());
        Player target = plugin.getServer().getPlayer(request.getTarget());
        if (from == null || target == null) return;

        if (this.module.isAfk(target)) {
            this.plugin.getMessage(AfkLang.AFK_NOTIFY_TELEPORT).replace(Placeholders.Player.replacer(target)).send(from);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onAfkQuit(PlayerQuitEvent e) {
        this.module.untrack(e.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onAfkQuit(PlayerDeathEvent e) {
        this.module.exitAfk(e.getEntity());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onAfkInteract(PlayerInteractEvent e) {
        if (e.getHand() != EquipmentSlot.HAND) return;

        Player player = e.getPlayer();
        this.module.getTrack(player).onInteract();
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onAfkChat(AsyncPlayerChatEvent e) {
        Player player = e.getPlayer();
        this.module.getTrack(player).onInteract();
    }
}
