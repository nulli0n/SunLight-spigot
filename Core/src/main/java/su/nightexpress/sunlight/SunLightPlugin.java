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
import su.nightexpress.sunlight.mc_1_20_6.MC_1_20_6;
import su.nightexpress.sunlight.mc_1_21.MC_1_21;
import su.nightexpress.sunlight.module.ModuleManager;
import su.nightexpress.sunlight.nms.SunNMS;
import su.nightexpress.sunlight.nms.v1_18_R2.V1_18_R2;
import su.nightexpress.sunlight.nms.v1_19_R3.V1_19_R3;
import su.nightexpress.sunlight.nms.v1_20_R1.V1_20_R1;
import su.nightexpress.sunlight.nms.v1_20_R2.V1_20_R2;
import su.nightexpress.sunlight.nms.v1_20_R3.V1_20_R3;

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
    }

    private void setupInternalNMS() {
        this.sunNMS = switch (Version.getCurrent()) {
            case V1_18_R2 -> new V1_18_R2();
            case V1_19_R3 -> new V1_19_R3();
            case V1_20_R1 -> new V1_20_R1();
            case V1_20_R2 -> new V1_20_R2();
            case V1_20_R3 -> new V1_20_R3();
            case MC_1_20_6 -> new MC_1_20_6();
            case MC_1_21 -> new MC_1_21();
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
