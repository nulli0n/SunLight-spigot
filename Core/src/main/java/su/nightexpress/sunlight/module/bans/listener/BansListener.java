package su.nightexpress.sunlight.module.bans.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.manager.AbstractListener;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.module.bans.BansModule;

public class BansListener extends AbstractListener<SunLightPlugin> {

    private final BansModule module;

    public BansListener(@NotNull SunLightPlugin plugin, @NotNull BansModule module) {
        super(plugin);
        this.module = module;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onBanLogin(AsyncPlayerPreLoginEvent event) {
        this.module.handleLoginEvent(event);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onMuteCommand(PlayerCommandPreprocessEvent event) {
        this.module.handleCommandEvent(event);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        this.module.handleJoin(event);
    }
}
