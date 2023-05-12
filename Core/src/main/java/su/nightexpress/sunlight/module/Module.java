package su.nightexpress.sunlight.module;

import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.config.JYML;
import su.nexmedia.engine.api.manager.AbstractManager;
import su.nexmedia.engine.api.manager.ILogger;
import su.nexmedia.engine.utils.StringUtil;
import su.nightexpress.sunlight.SunLight;

public abstract class Module extends AbstractManager<SunLight> implements ILogger {

    private final String id;
    private final String name;
    private final JYML cfg;

    public Module(@NotNull SunLight plugin, @NotNull String id) {
        super(plugin);
        this.id = id.toLowerCase();
        this.name = StringUtil.capitalizeUnderscored(id);
        this.cfg = JYML.loadOrExtract(this.plugin, this.getLocalPath(), "settings.yml");
    }

    public boolean canLoad() {
        return true;
    }

    @NotNull
    public JYML getConfig() {
        return this.cfg;
    }

    @NotNull
    public String getId() {
        return this.id;
    }

    @NotNull
    public final String getName() {
        return this.name;
    }

    @NotNull
    public String getLocalPath() {
        return ModuleManager.DIR_MODULES + this.getId();
    }

    @NotNull
    public final String getAbsolutePath() {
        return this.plugin.getDataFolder() + this.getLocalPath();
    }

    @NotNull
    private String buildLog(@NotNull String msg) {
        return "[" + this.getName() + "] " + msg;
    }

    @Override
    public final void info(@NotNull String msg) {
        this.plugin.info(this.buildLog(msg));
    }

    @Override
    public final void warn(@NotNull String msg) {
        this.plugin.warn(this.buildLog(msg));
    }

    @Override
    public final void error(@NotNull String msg) {
        this.plugin.error(this.buildLog(msg));
    }
}
