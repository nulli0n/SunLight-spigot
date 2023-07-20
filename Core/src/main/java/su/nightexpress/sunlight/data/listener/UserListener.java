package su.nightexpress.sunlight.data.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.manager.AbstractListener;
import su.nightexpress.sunlight.SunLight;
import su.nightexpress.sunlight.data.impl.IgnoredUser;
import su.nightexpress.sunlight.data.impl.SunUser;
import su.nightexpress.sunlight.utils.SunUtils;

public class UserListener extends AbstractListener<SunLight> {

    public UserListener(@NotNull SunLight plugin) {
        super(plugin);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onUserJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        SunUser user = plugin.getUserManager().getUserData(player);

        user.updatePlayerName();
        user.setIp(SunUtils.getIP(player));
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
