package su.nightexpress.sunlight;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.sunlight.module.ModuleManager;
import su.nightexpress.sunlight.module.warmups.WarmupsModule;
import su.nightexpress.sunlight.nms.SunNMS;

public class SunLightAPI {

    private static SunLightPlugin plugin;

    static void load(@NotNull SunLightPlugin instance) {
        plugin = instance;
    }

    static void clear() {
        plugin = null;
    }

    @NotNull
    public static SunLightPlugin getPlugin() {
        if (plugin == null) throw new IllegalStateException("API is not yet initialized!");

        return plugin;
    }

    @NotNull
    public static SunNMS getInternals() {
        return getPlugin().getSunNMS();
    }

    @NotNull
    public static ModuleManager getModuleManager() {
        return getPlugin().getModuleManager();
    }

    @Nullable
    public static WarmupsModule getWarmupsModule() {
        return getModuleManager().getModule(WarmupsModule.class).orElse(null);
    }
}
