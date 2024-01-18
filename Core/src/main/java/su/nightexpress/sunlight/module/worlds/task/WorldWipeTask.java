package su.nightexpress.sunlight.module.worlds.task;

import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.server.AbstractTask;
import su.nightexpress.sunlight.SunLight;
import su.nightexpress.sunlight.module.worlds.WorldsModule;
import su.nightexpress.sunlight.module.worlds.impl.WorldConfig;

public class WorldWipeTask extends AbstractTask<SunLight> {

    private final WorldsModule module;

    public WorldWipeTask(@NotNull WorldsModule module) {
        super(module.plugin(), 60, false);
        this.module = module;
    }

    @Override
    public void action() {
        this.module.getWorldConfigs().forEach(WorldConfig::autoWipe);
    }
}
