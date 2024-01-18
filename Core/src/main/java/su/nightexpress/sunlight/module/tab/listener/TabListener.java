package su.nightexpress.sunlight.module.tab.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.manager.AbstractListener;
import su.nightexpress.sunlight.SunLight;
import su.nightexpress.sunlight.module.tab.TabModule;

public class TabListener extends AbstractListener<SunLight> {

    private final TabModule tabModule;

    public TabListener(@NotNull TabModule tabModule) {
        super(tabModule.plugin());
        this.tabModule = tabModule;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onUpdateJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        this.tabModule.updateAll(player);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onUpdateRespawn(PlayerRespawnEvent e) {
        Player player = e.getPlayer();
        this.tabModule.updateAll(player);
    }
}
