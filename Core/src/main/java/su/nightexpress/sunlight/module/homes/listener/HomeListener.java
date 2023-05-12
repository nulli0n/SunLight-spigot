package su.nightexpress.sunlight.module.homes.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.manager.AbstractListener;
import su.nightexpress.sunlight.SunLight;
import su.nightexpress.sunlight.module.homes.HomesModule;

public class HomeListener extends AbstractListener<SunLight> {

    private final HomesModule homesModule;

    public HomeListener(@NotNull HomesModule homesModule) {
        super(homesModule.plugin());
        this.homesModule = homesModule;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onUserJoin(AsyncPlayerPreLoginEvent e) {
        if (e.getLoginResult() != AsyncPlayerPreLoginEvent.Result.ALLOWED) return;
        this.homesModule.loadHomes(e.getUniqueId());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onUserQuit(PlayerQuitEvent e) {
        this.homesModule.unloadHomes(e.getPlayer().getUniqueId());
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onHomeRespawn(PlayerRespawnEvent e) {
        Player player = e.getPlayer();
        this.homesModule.getHomeToRespawn(player).ifPresent(home -> {
            e.setRespawnLocation(home.getLocation());
        });
    }
}
