package su.nightexpress.sunlight;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.NightDataPlugin;
import su.nightexpress.nightcore.command.experimental.ImprovedCommands;
import su.nightexpress.nightcore.command.experimental.node.ChainedNode;
import su.nightexpress.nightcore.config.PluginDetails;
import su.nightexpress.nightcore.util.Plugins;
import su.nightexpress.nightcore.util.Version;
import su.nightexpress.sunlight.command.CommandRegistry;
import su.nightexpress.sunlight.command.list.ReloadCommand;
import su.nightexpress.sunlight.config.Config;
import su.nightexpress.sunlight.config.Lang;
import su.nightexpress.sunlight.config.Perms;
import su.nightexpress.sunlight.core.CoreManager;
import su.nightexpress.sunlight.data.DataHandler;
import su.nightexpress.sunlight.data.UserManager;
import su.nightexpress.sunlight.data.user.SunUser;
import su.nightexpress.sunlight.hook.impl.PlaceholderHook;
import su.nightexpress.sunlight.module.ModuleManager;
import su.nightexpress.sunlight.nms.SunNMS;
import su.nightexpress.sunlight.nms.mc_1_21_4.MC_1_21_4;

public class SunLightPlugin extends NightDataPlugin<SunUser> implements ImprovedCommands {

    private DataHandler dataHandler;
    private UserManager userManager;

    private CoreManager coreManager;
    private ModuleManager moduleManager;

    private SunNMS sunNMS;

    @Override
    @NotNull
    protected PluginDetails getDefaultDetails() {
        return PluginDetails.create("SunLight", new String[]{"sunlight", "sl"})
            .setConfigClass(Config.class)
            .setLangClass(Lang.class)
            .setPermissionsClass(Perms.class);
    }

    @Override
    public void enable() {
        this.loadAPI();
        this.setupInternalNMS();
        if (this.sunNMS == null) {
            this.error("Unsupported server version!");
            this.getPluginManager().disablePlugin(this);
            return;
        }

        this.registerCommands();

        this.dataHandler = new DataHandler(this);
        this.dataHandler.setup();

        this.userManager = new UserManager(this);
        this.userManager.setup();

        this.coreManager = new CoreManager(this);
        this.coreManager.setup();

        this.moduleManager = new ModuleManager(this);
        this.moduleManager.setup();

        CommandRegistry.setup(this);

        if (Plugins.hasPlaceholderAPI()) {
            PlaceholderHook.setup(this);
        }
    }

    @Override
    public void disable() {
        if (Plugins.hasPlaceholderAPI()) {
            PlaceholderHook.shutdown();
        }

        if (this.moduleManager != null) this.moduleManager.shutdown();

        CommandRegistry.shutdown(this);
        SunLightAPI.clear();
    }

    private void loadAPI() {
        SunLightAPI.load(this);
    }

    private void setupInternalNMS() {
        this.sunNMS = switch (Version.getCurrent()) {
            case MC_1_21_4 -> new MC_1_21_4();
            default -> null;
        };
    }

    private void registerCommands() {
        ChainedNode rootNode = this.getRootNode();

        ReloadCommand.inject(this, rootNode);
    }

    @Override
    @NotNull
    public DataHandler getData() {
        return this.dataHandler;
    }

    @NotNull
    @Override
    public UserManager getUserManager() {
        return userManager;
    }

    @NotNull
    public CoreManager getCoreManager() {
        return coreManager;
    }

    @NotNull
    public ModuleManager getModuleManager() {
        return moduleManager;
    }

    @NotNull
    public SunNMS getSunNMS() {
        return this.sunNMS;
    }
}
