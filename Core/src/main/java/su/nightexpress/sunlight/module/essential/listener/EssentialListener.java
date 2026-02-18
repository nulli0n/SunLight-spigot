package su.nightexpress.sunlight.module.essential.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.manager.AbstractListener;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.module.essential.EssentialModule;

public class EssentialListener extends AbstractListener<SunLightPlugin> {

    private final EssentialModule module;

    public EssentialListener(@NotNull SunLightPlugin plugin, @NotNull EssentialModule module) {
        super(plugin);
        this.module = module;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        this.module.updatePlayerName(player);
    }
}
