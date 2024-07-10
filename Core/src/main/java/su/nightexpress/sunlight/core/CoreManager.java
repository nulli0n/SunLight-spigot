package su.nightexpress.sunlight.core;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.manager.SimpleManager;
import su.nightexpress.sunlight.SunLightPlugin;

public class CoreManager extends SimpleManager<SunLightPlugin> {

    public CoreManager(@NotNull SunLightPlugin plugin) {
        super(plugin);
    }

    @Override
    protected void onLoad() {

    }

    @Override
    protected void onShutdown() {

    }
}
