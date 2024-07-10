package su.nightexpress.sunlight.module;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.manager.AbstractManager;
import su.nightexpress.nightcore.util.StringUtil;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.config.Config;

public abstract class Module extends AbstractManager<SunLightPlugin> {

    protected static final String CONFIG_NAME = "settings.yml";

    private final String     id;
    private final String     name;
    private final FileConfig config;

    public Module(@NotNull SunLightPlugin plugin, @NotNull String id) {
        super(plugin);
        this.id = id.toLowerCase();
        this.name = StringUtil.capitalizeUnderscored(id);
        this.config = FileConfig.loadOrExtract(this.plugin, this.getLocalPath(), CONFIG_NAME);
    }

    @Override
    protected final void onLoad() {
        ModuleInfo moduleInfo = new ModuleInfo();
        this.gatherInfo(moduleInfo);

        if (moduleInfo.getConfigClass() != null) this.config.initializeOptions(moduleInfo.getConfigClass());
        if (moduleInfo.getLangClass() != null) this.plugin.getLangManager().loadEntries(moduleInfo.getLangClass());
        if (moduleInfo.getPermissionsClass() != null) this.plugin.registerPermissions(moduleInfo.getPermissionsClass());

        this.onModuleLoad();

        this.config.saveChanges();
    }

    @Override
    protected final void onShutdown() {
        this.onModuleUnload();
    }

    protected abstract void gatherInfo(@NotNull ModuleInfo moduleInfo);

    protected abstract void onModuleLoad();

    protected abstract void onModuleUnload();

    public boolean canLoad() {
        return true;
    }

    @NotNull
    public FileConfig getConfig() {
        return this.config;
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
    public String getLocalUIPath() {
        return ModuleManager.DIR_MODULES + this.getId() + Config.DIR_MENU;
    }

    @NotNull
    public final String getAbsolutePath() {
        return this.plugin.getDataFolder() + this.getLocalPath();
    }

    @NotNull
    private String buildLog(@NotNull String msg) {
        return "[" + this.getName() + "] " + msg;
    }

    public final void info(@NotNull String msg) {
        this.plugin.info(this.buildLog(msg));
    }

    public final void warn(@NotNull String msg) {
        this.plugin.warn(this.buildLog(msg));
    }

    public final void error(@NotNull String msg) {
        this.plugin.error(this.buildLog(msg));
    }
}
