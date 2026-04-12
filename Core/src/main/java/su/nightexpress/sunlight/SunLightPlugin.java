package su.nightexpress.sunlight;

import java.nio.file.Path;
import java.util.Optional;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import su.nightexpress.nightcore.NightPlugin;
import su.nightexpress.nightcore.commands.Commands;
import su.nightexpress.nightcore.commands.command.NightCommand;
import su.nightexpress.nightcore.config.PluginDetails;
import su.nightexpress.nightcore.core.config.CoreLang;
import su.nightexpress.nightcore.util.Plugins;
import su.nightexpress.nightcore.util.Version;
import su.nightexpress.sunlight.api.SunlightAPI;
import su.nightexpress.sunlight.api.provider.AfkProvider;
import su.nightexpress.sunlight.api.provider.VanishProvider;
import su.nightexpress.sunlight.command.CommandRegistry;
import su.nightexpress.sunlight.config.Config;
import su.nightexpress.sunlight.config.Lang;
import su.nightexpress.sunlight.config.PermissionTree;
import su.nightexpress.sunlight.config.Perms;
import su.nightexpress.sunlight.data.DataHandler;
import su.nightexpress.sunlight.hook.impl.PlaceholderHook;
import su.nightexpress.sunlight.module.LoadCondition;
import su.nightexpress.sunlight.module.ModuleContext;
import su.nightexpress.sunlight.module.ModuleContextProvider;
import su.nightexpress.sunlight.module.ModuleDefinition;
import su.nightexpress.sunlight.module.ModuleId;
import su.nightexpress.sunlight.module.ModuleLoader;
import su.nightexpress.sunlight.module.ModuleRegistry;
import su.nightexpress.sunlight.module.afk.AfkModule;
import su.nightexpress.sunlight.module.backlocation.BackLocationModule;
import su.nightexpress.sunlight.module.bans.BansModule;
import su.nightexpress.sunlight.module.chat.ChatModule;
import su.nightexpress.sunlight.module.deathmessages.DeathMessagesModule;
import su.nightexpress.sunlight.module.essential.EssentialModule;
import su.nightexpress.sunlight.module.extras.ExtrasModule;
import su.nightexpress.sunlight.module.greetings.GreetingsModule;
import su.nightexpress.sunlight.module.homes.HomesModule;
import su.nightexpress.sunlight.module.inventories.InventoriesModule;
import su.nightexpress.sunlight.module.items.ItemsModule;
import su.nightexpress.sunlight.module.kits.KitsModule;
import su.nightexpress.sunlight.module.nametags.NametagsModule;
import su.nightexpress.sunlight.module.nerfphantoms.PhantomsModule;
import su.nightexpress.sunlight.module.playerwarps.PlayerWarpsModule;
import su.nightexpress.sunlight.module.ptp.PTPModule;
import su.nightexpress.sunlight.module.rtp.RTPModule;
import su.nightexpress.sunlight.module.scheduler.SchedulerModule;
import su.nightexpress.sunlight.module.scoreboard.ScoreboardModule;
import su.nightexpress.sunlight.module.spawns.SpawnsModule;
import su.nightexpress.sunlight.module.tab.TabModule;
import su.nightexpress.sunlight.module.texts.TextsModule;
import su.nightexpress.sunlight.module.vanish.VanishModule;
import su.nightexpress.sunlight.module.warmups.WarmupsModule;
import su.nightexpress.sunlight.module.warps.WarpsModule;
import su.nightexpress.sunlight.module.worlds.WorldsModule;
import su.nightexpress.sunlight.nms.SunNMS;
import su.nightexpress.sunlight.nms.mc_1_21_11.MC_1_21_11;
import su.nightexpress.sunlight.nms.v26p1.NMSv26p1;
import su.nightexpress.sunlight.teleport.TeleportManager;
import su.nightexpress.sunlight.user.UserManager;

public class SunLightPlugin extends NightPlugin implements SunlightAPI, ModuleContextProvider {

    private static SunlightAPI api;

    private CommandRegistry commandRegistry;
    private ModuleRegistry  moduleRegistry;

    private DataHandler dataHandler;
    private UserManager userManager;

    private TeleportManager teleportManager;

    private SunNMS sunNMS;

    @NonNull
    public static SunlightAPI getAPI() {
        return api;
    }

    public SunLightPlugin() {
        api = this;
    }

    @Override
    @NotNull
    protected PluginDetails getDefaultDetails() {
        return PluginDetails.create("SunLight", new String[]{"sunlight", "sl"})
            .setConfigClass(Config.class);
    }

    @Override
    protected void addRegistries() {
        this.registerLang(Lang.class);
    }

    @Override
    protected boolean disableCommandManager() {
        return true;
    }

    @Override
    protected void onStartup() {
        this.commandRegistry = new CommandRegistry(this);
        this.moduleRegistry = new ModuleRegistry();
    }

    @Override
    public void enable() {
        this.setupInternalNMS();

        this.dataHandler = new DataHandler(this);
        this.dataHandler.setup();

        this.userManager = new UserManager(this, this.dataHandler);
        this.userManager.setup();

        this.teleportManager = new TeleportManager(this, this.sunNMS);
        this.teleportManager.setup();

        /*if (this.moduleRegistry.isCompleted()) {
            this.info("Reloading all modules...");
            this.moduleRegistry.reload();
        }
        else {*/
        //this.info("Initializing modules...");
        this.loadModules();
        //}

        this.commandRegistry.setup();
        this.registerCommands();
        this.registerPermissions(Perms.ROOT);

        if (Plugins.hasPlaceholderAPI()) {
            PlaceholderHook.setup(this);
        }
    }

    @Override
    public void disable() {
        if (Plugins.hasPlaceholderAPI()) {
            PlaceholderHook.shutdown();
        }

        if (this.moduleRegistry != null) this.moduleRegistry.clear();
        if (this.dialogRegistry != null) this.dialogRegistry.clear();
        if (this.userManager != null) this.userManager.shutdown();
        if (this.dataHandler != null) this.dataHandler.shutdown();
        if (this.commandRegistry != null) this.commandRegistry.shutdown();
    }

    @Override
    protected void onShutdown() {
        super.onShutdown();
    }

    private void loadModules() {
        ModuleLoader loader = new ModuleLoader(this, this.moduleRegistry);

        loader.register(ModuleId.WORLDS, ModuleDefinition.named("Worlds"),
            context -> new WorldsModule(context, this.teleportManager));

        loader.register(ModuleId.AFK, ModuleDefinition.named("AFK"), AfkModule::new);
        loader.register(ModuleId.BANS, ModuleDefinition.named("Bans"), BansModule::new);
        loader.register(ModuleId.BACK_LOCATION, ModuleDefinition.named("Back"),
            context -> new BackLocationModule(context, this.teleportManager));
        loader.register(ModuleId.CUSTOM_TEXT, ModuleDefinition.named("Custom Text"), TextsModule::new);
        loader.register(ModuleId.CHAT, ModuleDefinition.named("Chat"), ChatModule::new);
        loader.register(ModuleId.DEATH_MESSAGES, ModuleDefinition.named("Death Messages"), DeathMessagesModule::new);
        loader.register(ModuleId.ESSENTIAL, ModuleDefinition.named("Essential"),
            context -> new EssentialModule(context, this.teleportManager));
        loader.register(ModuleId.EXTRAS, ModuleDefinition.named("Extras"), ExtrasModule::new);
        loader.register(ModuleId.GREETINGS, ModuleDefinition.named("Greetings"), GreetingsModule::new);
        loader.register(ModuleId.HOMES, ModuleDefinition.named("Homes"),
            context -> new HomesModule(context, this.teleportManager));
        loader.register(ModuleId.INVENTORIES, ModuleDefinition.named("Inventories"),
            context -> new InventoriesModule(context, this.sunNMS));
        loader.register(ModuleId.ITEMS, ModuleDefinition.named("Items"), ItemsModule::new);
        loader.register(ModuleId.KITS, ModuleDefinition.named("Kits"), KitsModule::new);
        loader.register(ModuleId.NAME_TAGS, ModuleDefinition.named("Nametags"), NametagsModule::new,
            LoadCondition::packetLibrary);
        loader.register(ModuleId.NERF_PHANTOMS, ModuleDefinition.named("Nerf Phantoms"), PhantomsModule::new);
        loader.register(ModuleId.PLAYER_WARPS, ModuleDefinition.named("Player Warps"),
            context -> new PlayerWarpsModule(context, this.teleportManager));
        loader.register(ModuleId.PTP, ModuleDefinition.named("PTP"),
            context -> new PTPModule(context, this.teleportManager));
        loader.register(ModuleId.RTP, ModuleDefinition.named("RTP"),
            context -> new RTPModule(context, this.teleportManager));
        loader.register(ModuleId.SCHEDULER, ModuleDefinition.named("Scheduler"), SchedulerModule::new);
        loader.register(ModuleId.SCOREBOARD, ModuleDefinition.named("Scoreboard"), ScoreboardModule::new,
            LoadCondition::packetLibrary);
        loader.register(ModuleId.SPAWNS, ModuleDefinition.named("Spawn"),
            context -> new SpawnsModule(context, this.teleportManager));
        loader.register(ModuleId.TAB, ModuleDefinition.named("Tab"), TabModule::new);
        loader.register(ModuleId.VANISH, ModuleDefinition.named("Vanish"), VanishModule::new);
        loader.register(ModuleId.WARMUPS, ModuleDefinition.named("Warmups"),
            context -> new WarmupsModule(context, this.teleportManager));
        loader.register(ModuleId.WARPS, ModuleDefinition.named("Warps"),
            context -> new WarpsModule(context, this.teleportManager));

        //loader.register(ModuleId.SPAWNERS, ModuleDefinition.named("Spawners"), SpawnersModule::new);
        //loader.register(ModuleId.SOCIALS, ModuleDefinition.named("Socials"), SocialsModule::new);

        loader.loadAll();
    }

    @Override
    @NotNull
    public ModuleContext createModuleContext(@NotNull String id, @NotNull Path path, @NotNull ModuleDefinition definition) {
        return new ModuleContext(this, this.dataHandler, this.userManager, this.commandRegistry, this.dialogRegistry, id, path, definition);
    }

    private void setupInternalNMS() {
        try {
            this.sunNMS = switch (Version.getCurrent()) {
                case MC_1_21_11 -> new MC_1_21_11();
                default -> new NMSv26p1();
            };
        }
        catch (Exception | NoClassDefFoundError e) {
            e.printStackTrace();
        }

        if (this.sunNMS == null) {
            this.warn("Could not load internals handler. Some features will be unvailable.");
        }
    }

    private void registerCommands() {
        this.rootCommand = NightCommand.forPlugin(this, builder -> builder
            .branch(Commands.literal("reload")
                .description(CoreLang.COMMAND_RELOAD_DESC)
                .permission(Perms.COMMAND_RELOAD)
                .executes((context, arguments) -> {
                    this.doReload(context.getSender());
                    return true;
                })
            )
        );
    }

    private void registerPermissions(@NotNull PermissionTree tree) {
        tree.toList().forEach(permission -> {
            if (this.getPluginManager().getPermission(permission.getName()) == null) {
                this.getPluginManager().addPermission(permission);
            }
        });
    }

    @NotNull
    public DataHandler getData() {
        return this.dataHandler;
    }

    @NotNull
    public UserManager getUserManager() {
        return userManager;
    }

    @NotNull
    public ModuleRegistry getModuleRegistry() {
        return this.moduleRegistry;
    }

    @Nullable
    public SunNMS getInternals() {
        return this.sunNMS;
    }

    @NotNull
    public Optional<SunNMS> internals() {
        return Optional.ofNullable(this.sunNMS);
    }

    @NotNull
    public CommandRegistry getCommandRegistry() {
        return this.commandRegistry;
    }

    @NotNull
    public TeleportManager getTeleportManager() {
        return this.teleportManager;
    }

    @Override
    @NonNull
    public Optional<? extends AfkProvider> afkProvider() {
        return this.moduleRegistry.byType(AfkModule.class);
    }

    @Override
    @NonNull
    public Optional<? extends VanishProvider> vanishProvider() {
        return this.moduleRegistry.byType(VanishModule.class);
    }
}
