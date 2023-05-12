package su.nightexpress.sunlight.module.homes.task;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.server.AbstractTask;
import su.nightexpress.sunlight.SunLight;
import su.nightexpress.sunlight.module.homes.HomesModule;
import su.nightexpress.sunlight.module.homes.config.HomesConfig;

public class HomesCleanupTask extends AbstractTask<SunLight> {

    private final HomesModule module;

    public HomesCleanupTask(@NotNull HomesModule module) {
        super(module.plugin(), HomesConfig.DATA_CLEANUP_INTERVAL.get(), true);
        this.module = module;
    }

    @Override
    public void action() {
        this.module.getHomes().keySet().stream().toList().forEach(ownerId -> {
            Player player = plugin.getServer().getPlayer(ownerId);
            if (player == null) {
                this.plugin.runTask(task -> this.module.unloadHomes(ownerId));
            }
        });
    }
}
