package su.nightexpress.sunlight.module.tab.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
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
    public void onPlayerJoin(PlayerJoinEvent event) {
        this.module.handleJoin(event);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        this.module.handleQuit(event);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerWorldChange(PlayerChangedWorldEvent event) {
        this.module.updatePlayerList(event.getPlayer());
    }
}
