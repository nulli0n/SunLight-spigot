package su.nightexpress.sunlight.module.afk.task;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.server.AbstractTask;
import su.nightexpress.sunlight.SunLight;
import su.nightexpress.sunlight.module.afk.AfkModule;

public class AfkTickTask extends AbstractTask<SunLight> {

    private final AfkModule module;

    public AfkTickTask(@NotNull AfkModule module) {
        super(module.plugin(), 1, true);
        this.module = module;
    }

    @Override
    public void action() {
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            this.module.getTrack(player).tick();
        }
    }
}
