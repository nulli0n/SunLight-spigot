package su.nightexpress.sunlight;

import org.bukkit.GameMode;
import org.bukkit.inventory.EquipmentSlot;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.NexPlugin;
import su.nexmedia.engine.Version;
import su.nexmedia.engine.api.command.AbstractCommand;
import su.nexmedia.engine.api.command.GeneralCommand;
import su.nexmedia.engine.api.data.UserDataHolder;
import su.nexmedia.engine.integration.VaultHook;
import su.nexmedia.engine.utils.EngineUtils;
import su.nightexpress.sunlight.actions.ActionsManager;
import su.nightexpress.sunlight.actions.action.list.AVaultAdd;
import su.nightexpress.sunlight.command.CommandRegulator;
import su.nightexpress.sunlight.command.children.ReloadCommand;
import su.nightexpress.sunlight.command.list.WeatherCommand;
import su.nightexpress.sunlight.config.Config;
import su.nightexpress.sunlight.config.Lang;
import su.nightexpress.sunlight.data.DataHandler;
import su.nightexpress.sunlight.data.UserManager;
import su.nightexpress.sunlight.data.impl.SunUser;
import su.nightexpress.sunlight.hook.impl.PlaceholderHook;
import su.nightexpress.sunlight.module.ModuleManager;
import su.nightexpress.sunlight.nms.SunNMS;
import su.nightexpress.sunlight.nms.v1_18_R2.V1_18_R2;
import su.nightexpress.sunlight.nms.v1_19_R3.V1_19_R3;
import su.nightexpress.sunlight.nms.v1_20_R1.V1_20_R1;

import java.util.Collection;

public class SunLight extends NexPlugin<SunLight> implements UserDataHolder<SunLight, SunUser> {

    private DataHandler dataHandler;
    private UserManager userManager;

    private ActionsManager   actionsManager;
    private CommandRegulator commandRegulator;
    private ModuleManager    moduleManager;

    private SunNMS sunNMS;

    @Override
    @NotNull
    protected SunLight getSelf() {
        return this;
    }

    @Override
    public void enable() {
        this.setupInternalNMS();
        if (this.sunNMS == null) {
            this.error("Unsupported server version. Bye.");
            this.getPluginManager().disablePlugin(this);
            return;
        }

        this.actionsManager = new ActionsManager(this);
        this.actionsManager.setup();
        this.registerActions();

        this.configManager.extractResources("/custom_text/");

        this.commandRegulator = new CommandRegulator(this);
        this.commandRegulator.setup();

        this.moduleManager = new ModuleManager(this);
        this.moduleManager.setup();

        int commands = this.getCommandManager().getCommands().size();
        int subs = this.getCommandManager().getCommands().stream().map(AbstractCommand::getChildrens).mapToInt(Collection::size).sum();
        this.info("Registered total " + commands + " main commands and " + subs + " sub-commands.");
    }

    @Override
    public void disable() {
        if (EngineUtils.hasPlaceholderAPI()) {
            PlaceholderHook.shutdown();
        }
        if (this.moduleManager != null) {
            this.moduleManager.shutdown();
            this.moduleManager = null;
        }
        if (this.commandRegulator != null) {
            this.commandRegulator.shutdown();
            this.commandRegulator = null;
        }
        if (this.actionsManager != null) {
            this.actionsManager.shutdown();
            this.actionsManager = null;
        }
    }

    private void setupInternalNMS() {
        this.sunNMS = switch (Version.getCurrent()) {
            case V1_18_R2 -> new V1_18_R2();
            case V1_19_R3 -> new V1_19_R3();
            case V1_20_R1 -> new V1_20_R1();
            default -> null;
        };
    }

    @Override
    public boolean setupDataHandlers() {
        try {
            this.dataHandler = DataHandler.getInstance(this);
            this.dataHandler.setup();
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        this.userManager = new UserManager(this);
        this.userManager.setup();

        return true;
    }

    private void registerActions() {
        if (VaultHook.hasEconomy()) {
            this.getActionsManager().registerActionExecutor(new AVaultAdd());
        }
        /*if (this.getConfigManager().isModuleEnabled(ModuleId.MENU)) {
            this.getActionsManager().registerActionExecutor(new AOpenGUI());
        }*/
    }

    @Override
    public void registerCommands(@NotNull GeneralCommand<SunLight> mainCommand) {
        mainCommand.addChildren(new ReloadCommand(this));
    }

    @Override
    public void registerPermissions() {
        this.registerPermissions(Perms.class);
    }

    @Override
    public void registerHooks() {
        if (EngineUtils.hasPlaceholderAPI()) {
            PlaceholderHook.setup(this);
        }
    }

    @Override
    public void loadConfig() {
        this.getConfig().initializeOptions(Config.class);
    }

    @Override
    public void loadLang() {
        this.getLangManager().loadMissing(Lang.class);
        this.getLangManager().loadEnum(EquipmentSlot.class);
        this.getLangManager().loadEnum(GameMode.class);
        this.getLangManager().loadEnum(WeatherCommand.WeatherType.class);
        this.getLang().saveChanges();
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
    public ActionsManager getActionsManager() {
        return actionsManager;
    }

    @NotNull
    public CommandRegulator getCommandRegulator() {
        return commandRegulator;
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
