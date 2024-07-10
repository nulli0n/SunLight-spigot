package su.nightexpress.sunlight.module.chat.module.joinquit;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.manager.AbstractListener;
import su.nightexpress.sunlight.SunLightPlugin;

public class JoinMessageListener extends AbstractListener<SunLightPlugin> {

    private final JoinMessageManager manager;

    public JoinMessageListener(@NotNull SunLightPlugin plugin, @NotNull JoinMessageManager manager) {
        super(plugin);
        this.manager = manager;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChatJoinMessage(PlayerJoinEvent event) {
        event.setJoinMessage(null);

        Player player = event.getPlayer();
        JoinMessage message = this.manager.getJoinMessage(player);
        if (message == null) return;

        message.display(this.plugin, player);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onChatQuitMessage(PlayerQuitEvent event) {
        event.setQuitMessage(null);

        Player player = event.getPlayer();
        JoinMessage message = this.manager.getQuitMessage(player);
        if (message == null) return;

        message.display(this.plugin, player);
    }
}
