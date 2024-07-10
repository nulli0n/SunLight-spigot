package su.nightexpress.sunlight.core.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.manager.AbstractListener;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.core.user.IgnoredUser;
import su.nightexpress.sunlight.data.user.SunUser;
import su.nightexpress.sunlight.utils.SunUtils;

public class UserListener extends AbstractListener<SunLightPlugin> {

    public UserListener(@NotNull SunLightPlugin plugin) {
        super(plugin);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onQuit(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        SunUser user = plugin.getUserManager().getUserData(player);

        user.setNewlyCreated(false);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        SunUser user = plugin.getUserManager().getUserData(player);

        user.updatePlayerName();
        user.setInetAddress(SunUtils.getRawAddress(player));
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onUserIgnoreChat(AsyncPlayerChatEvent e) {
        Player pSender = e.getPlayer();
        SunUser userSender = plugin.getUserManager().getUserData(pSender);

        e.getRecipients().removeIf(pReceiver -> {
            SunUser userReceiver = plugin.getUserManager().getUserData(pReceiver);

            IgnoredUser ignoredSender = userReceiver.getIgnoredUser(pSender);
            if (ignoredSender == null) return false;

            return ignoredSender.isHideChatMessages();
        });
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onUserCommandCooldown(PlayerCommandPreprocessEvent e) {
        Player player = e.getPlayer();
        String commandLine = e.getMessage();
        if (!this.plugin.getUserManager().checkCommandCooldown(player, commandLine)) {
            e.setCancelled(true);
        }
    }
}
