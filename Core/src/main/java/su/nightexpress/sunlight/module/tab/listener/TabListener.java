package su.nightexpress.sunlight.module.tab.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.manager.AbstractListener;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.module.tab.TabModule;

public class TabListener extends AbstractListener<SunLightPlugin> {

    private final TabModule module;

    public TabListener(@NotNull SunLightPlugin plugin, @NotNull TabModule module) {
        super(plugin);
        this.module = module;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onUpdateJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        this.module.updateAll(player);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onUpdateRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        this.module.updateAll(player);
    }
}
