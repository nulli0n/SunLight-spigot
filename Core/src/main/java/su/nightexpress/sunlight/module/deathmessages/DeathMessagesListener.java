package su.nightexpress.sunlight.module.deathmessages;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.manager.AbstractListener;
import su.nightexpress.sunlight.SunLightPlugin;

public class DeathMessagesListener extends AbstractListener<SunLightPlugin> {

    private final DeathMessagesModule module;

    public DeathMessagesListener(@NotNull SunLightPlugin plugin, @NotNull DeathMessagesModule module) {
        super(plugin);
        this.module = module;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDeath(PlayerDeathEvent event) {
        this.module.handleDeathEvent(event);
    }
}
