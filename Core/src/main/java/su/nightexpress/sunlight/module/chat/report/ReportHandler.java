package su.nightexpress.sunlight.module.chat.report;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.manager.SimpleManager;
import su.nightexpress.sunlight.SunLightPlugin;

public abstract class ReportHandler extends SimpleManager<SunLightPlugin> {

    public ReportHandler(@NotNull SunLightPlugin plugin) {
        super(plugin);
    }
}
