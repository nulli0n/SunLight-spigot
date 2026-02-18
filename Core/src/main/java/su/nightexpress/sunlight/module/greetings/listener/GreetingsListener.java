package su.nightexpress.sunlight.module.greetings.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.manager.AbstractListener;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.module.greetings.GreetingsModule;

public class GreetingsListener extends AbstractListener<SunLightPlugin> {

    private final GreetingsModule module;

    public GreetingsListener(@NotNull SunLightPlugin plugin, @NotNull GreetingsModule module) {
        super(plugin);
        this.module = module;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onJoin(PlayerJoinEvent event) {
        this.module.handleJoinEvent(event);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onQuit(PlayerQuitEvent event) {
        this.module.handleQuitEvent(event);
    }
}
