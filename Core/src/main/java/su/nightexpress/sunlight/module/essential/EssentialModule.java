package su.nightexpress.sunlight.module.essential;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.core.config.CoreLang;
import su.nightexpress.nightcore.integration.permission.PermissionBridge;
import su.nightexpress.nightcore.util.EntityUtil;
import su.nightexpress.nightcore.util.bridge.wrapper.NightComponent;
import su.nightexpress.sunlight.config.PermissionTree;
import su.nightexpress.sunlight.hook.placeholder.PlaceholderRegistry;
import su.nightexpress.sunlight.module.Module;
import su.nightexpress.sunlight.module.ModuleContext;
import su.nightexpress.sunlight.module.essential.command.*;
import su.nightexpress.sunlight.module.essential.listener.EssentialListener;
import su.nightexpress.sunlight.module.essential.listener.InvulnerabilityListener;
import su.nightexpress.sunlight.teleport.TeleportManager;
import su.nightexpress.sunlight.user.SunUser;
import su.nightexpress.sunlight.user.property.UserPropertyRegistry;

public class EssentialModule extends Module {

    private final TeleportManager   teleportManager;
    private final EssentialSettings settings;

    public EssentialModule(@NotNull ModuleContext context, @NotNull TeleportManager teleportManager) {
        super(context);
        this.teleportManager = teleportManager;
        this.settings = new EssentialSettings();
    }

    @Override
    protected void loadModule(@NotNull FileConfig config) {
        UserPropertyRegistry.register(EssentialProperties.CUSTOM_NAME);

        this.plugin.injectLang(EssentialLang.class);
        this.settings.load(config);
        this.registerCommands();

        this.addListener(new EssentialListener(this.plugin, this));

        if (this.settings.isInvulnerabilityEnabled()) {
            this.addListener(new InvulnerabilityListener(this.plugin, this, this.settings));
        }
    }

    @Override
    protected void unloadModule() {

    }

    @Override
    protected void registerPermissions(@NotNull PermissionTree root) {
        root.merge(EssentialPerms.MODULE);
    }

    protected void registerCommands() {
        if (this.settings.isInvulnerabilityEnabled()) {
            this.commandRegistry.addProvider("ess-invulnerability", new InvulnerabilityCommandProvider(this.plugin, this, this.settings, this.userManager));
        }
        if (PermissionBridge.hasProvider()) {
            this.commandRegistry.addProvider("staff", new StaffCommandProvider(this.plugin, this, this.settings));
        }

        this.commandRegistry.addProvider("air", new AirCommandProvider(this.plugin, this, this.userManager));
        this.commandRegistry.addProvider("broadcast", new BroadcastCommandProvider(this.plugin, this.settings.broadcastFormat.get()));
        this.commandRegistry.addProvider("condense", new CondenseCommandProvider(this.plugin, this));
        this.commandRegistry.addProvider("dimension", new DimensionCommandProvider(this.plugin, this, this.userManager, this.teleportManager));
        this.commandRegistry.addProvider("disposal", new DisposalCommandProvider(this.plugin, this, this.settings));
        this.commandRegistry.addProvider("enchant", new EnchantCommandsProvider(this.plugin, this, this.userManager));
        this.commandRegistry.addProvider("experience", new ExperienceCommandsProvider(this.plugin, this, this.userManager));
        this.commandRegistry.addProvider("fireticks", new FireTicksCommandsProvider(this.plugin, this, this.userManager));
        this.commandRegistry.addProvider("fly", new FlyCommandProvider(this.plugin, this, this.userManager));
        this.commandRegistry.addProvider("flyspeed", new FlySpeedCommandProvider(this.plugin, this, this.userManager));
        this.commandRegistry.addProvider("foodlevel", new FoodLevelCommandProvider(this.plugin, this, this.settings, this.userManager));
        this.commandRegistry.addProvider("forcerun", new ForceRunCommandProvider(this.plugin, this));
        this.commandRegistry.addProvider("forcesay", new ForceSayCommandProvider(this.plugin, this));
        this.commandRegistry.addProvider("gamemode", new GamemodeCommandProvider(this.plugin, this, this.userManager));
        this.commandRegistry.addProvider("hat", new HatCommandProvider(this.plugin, this));
        this.commandRegistry.addProvider("health", new HealthCommandProvider(this.plugin, this, this.settings, this.userManager));
        this.commandRegistry.addProvider("near", new NearCommandProvider(this.plugin, this, this.settings, this.userManager));
        this.commandRegistry.addProvider("nickname", new NickCommandsProvider(this.plugin, this, this.settings, this.userManager));
        this.commandRegistry.addProvider("playerinfo", new PlayerInfoCommandProvider(this.plugin, this, this.settings, this.userManager));
        this.commandRegistry.addProvider("skull", new SkullCommandProvider(this.plugin, this));
        this.commandRegistry.addProvider("smite", new SmiteCommandProvider(this.plugin, this));
        this.commandRegistry.addProvider("speed", new SpeedCommandProvider(this.plugin, this, this.userManager));
        this.commandRegistry.addProvider("suicide", new SuicideCommandProvider(this.plugin, this));
        this.commandRegistry.addProvider("teleport", new TeleportCommandsProvider(this.plugin, this, this.userManager, this.teleportManager));
        this.commandRegistry.addProvider("time", new TimeCommandProvider(this.plugin, this, this.settings));
        this.commandRegistry.addProvider("playertime", new PlayerTimeCommandProvider(this.plugin, this, this.settings));
        this.commandRegistry.addProvider("weather", new WeatherCommandProvider(this.plugin, this));
    }

    @Override
    public void registerPlaceholders(@NotNull PlaceholderRegistry registry) {
        if (this.settings.isInvulnerabilityEnabled()) {
            registry.register("essential_invulnerability_state", (player, payload) -> {
                return CoreLang.STATE_ENABLED_DISALBED.get(player.isInvulnerable());
            });

            registry.register("essential_invulnerability_bool", (player, payload) -> {
                return String.valueOf(player.isInvulnerable());
            });

            registry.register("essential_custom_name", (player, payload) -> {
                return this.getCustomName(player);
            });
        }
    }

    @NonNull
    public String getCustomName(@NonNull Player player) {
        SunUser user = this.userManager.getOrFetch(player);

        return user.getPropertyOr(EssentialProperties.CUSTOM_NAME, user.getName());
    }


    public void setCustomName(@NonNull SunUser user, @Nullable String name) {
        if (name == null) {
            user.removeProperty(EssentialProperties.CUSTOM_NAME);
        }
        else {
            user.setProperty(EssentialProperties.CUSTOM_NAME, name);
        }
        user.player().ifPresent(this::updatePlayerName);
        user.markDirty();
    }

    public void updatePlayerName(@NotNull Player player) {
        SunUser user = this.userManager.getOrFetch(player);

        // TODO Add property 'forced'
        if (!player.hasPermission(NickCommandsProvider.NICK_CHANGE)) {
            user.removeProperty(EssentialProperties.CUSTOM_NAME);
        }

        if (user.hasProperty(EssentialProperties.CUSTOM_NAME)) {
            String customName = user.getPropertyOrDefault(EssentialProperties.CUSTOM_NAME);
            EntityUtil.setCustomName(player, customName);
        }
        else {
            EntityUtil.setCustomName(player, (NightComponent) null);
        }
    }
}
