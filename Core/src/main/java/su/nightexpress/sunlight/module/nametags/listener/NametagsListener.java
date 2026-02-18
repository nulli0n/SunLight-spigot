package su.nightexpress.sunlight.module.nametags.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.manager.AbstractListener;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.module.nametags.NametagsModule;

public class NametagsListener extends AbstractListener<SunLightPlugin> {

    private final NametagsModule module;

    public NametagsListener(@NotNull SunLightPlugin plugin, @NotNull NametagsModule module) {
        super(plugin);
        this.module = module;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        this.module.handleJoin(event);
    }
}
