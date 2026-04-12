package su.nightexpress.sunlight.module.warps;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import su.nightexpress.nightcore.commands.command.NightCommand;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.ui.inventory.action.ActionContext;
import su.nightexpress.nightcore.util.*;
import su.nightexpress.nightcore.util.placeholder.PlaceholderContext;
import su.nightexpress.sunlight.config.PermissionTree;
import su.nightexpress.sunlight.hook.placeholder.PlaceholderRegistry;
import su.nightexpress.sunlight.module.Module;
import su.nightexpress.sunlight.module.ModuleContext;
import su.nightexpress.sunlight.module.warps.command.WarpsCommandProvider;
import su.nightexpress.sunlight.module.warps.core.WarpsLang;
import su.nightexpress.sunlight.module.warps.core.WarpsPerms;
import su.nightexpress.sunlight.module.warps.core.WarpsSettings;
import su.nightexpress.sunlight.module.warps.dialog.WarpsDialogKeys;
import su.nightexpress.sunlight.module.warps.dialog.impl.WarpCommandDialog;
import su.nightexpress.sunlight.module.warps.dialog.impl.WarpDescriptionDialog;
import su.nightexpress.sunlight.module.warps.dialog.impl.WarpNameDialog;
import su.nightexpress.sunlight.module.warps.dialog.impl.WarpSlotsDialog;
import su.nightexpress.sunlight.module.warps.event.WarpTeleportEvent;
import su.nightexpress.sunlight.module.warps.exception.WarpLoadException;
import su.nightexpress.sunlight.module.warps.menu.WarpListMenu;
import su.nightexpress.sunlight.module.warps.menu.WarpOptionsMenu;
import su.nightexpress.sunlight.teleport.TeleportContext;
import su.nightexpress.sunlight.teleport.TeleportFlag;
import su.nightexpress.sunlight.teleport.TeleportManager;
import su.nightexpress.sunlight.teleport.TeleportType;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class WarpsModule extends Module {

    private final TeleportManager teleportManager;

    private final Map<String, Warp> repository;
    private final WarpsSettings     settings;

    private WarpOptionsMenu settingsMenu;
    private WarpListMenu    listMenu;

    public WarpsModule(@NonNull ModuleContext context, @NonNull TeleportManager teleportManager) {
        super(context);
        this.teleportManager = teleportManager;
        this.repository = new HashMap<>();
        this.settings = new WarpsSettings();
    }

    @Override
    protected void loadModule(@NonNull FileConfig config) {
        this.settings.load(config);
        this.plugin.injectLang(WarpsLang.class);

        this.dialogRegistry.register(WarpsDialogKeys.WARP_NAME, WarpNameDialog::new);
        this.dialogRegistry.register(WarpsDialogKeys.WARP_DESCRIPTION, WarpDescriptionDialog::new);
        this.dialogRegistry.register(WarpsDialogKeys.WARP_COMMAND, () -> new WarpCommandDialog(this));
        this.dialogRegistry.register(WarpsDialogKeys.WARP_SLOTS, WarpSlotsDialog::new);

        this.settingsMenu = new WarpOptionsMenu(this);
        this.settingsMenu.load(this.plugin, FileConfig.load(this.getLocalUIPath(), WarpsFiles.FILE_WARP_SETTINGS));

        this.listMenu = new WarpListMenu(this);
        this.listMenu.load(this.plugin, FileConfig.load(this.getLocalUIPath(), WarpsFiles.FILE_WARP_LIST));
        this.plugin.injectLang(this.listMenu);

        this.loadWarps();
        this.updateWarpCommands();

        this.addAsyncTask(this::saveDirtyWarps, this.getSettings().getSaveInterval());
    }

    @Override
    protected void unloadModule() {
        this.saveDirtyWarps();
        this.getWarps().forEach(this::unloadWarp);
    }

    @Override
    protected void registerPermissions(@NonNull PermissionTree root) {
        root.merge(WarpsPerms.MODULE);
    }

    @Override
    protected void registerCommands() {
        this.commandRegistry.addProvider("warps", new WarpsCommandProvider(this.plugin, this, this.userManager));
    }

    @Override
    public void registerPlaceholders(@NonNull PlaceholderRegistry registry) {
        registry.register("warps_total_amount", (player, payload) -> {
            return NumberUtil.format(this.repository.size());
        });
    }

    @NonNull
    public String getWarpsDirectory() {
        return WarpsFiles.DIR_WARPS;
    }

    @NonNull
    public WarpsSettings getSettings() {
        return this.settings;
    }

    @Nullable
    public Warp getWarpById(@NonNull String id) {
        return this.repository.get(LowerCase.INTERNAL.apply(id));
    }

    @NonNull
    public Set<Warp> getWarps() {
        return Set.copyOf(this.repository.values());
    }

    @NonNull
    public Set<Warp> getAvailableWarps(@NonNull Player player) {
        return this.getWarps().stream().filter(warp -> warp.canUse(player)).collect(Collectors.toSet());
    }

    public boolean hasAvailableWarps(@NonNull Player player) {
        return this.getWarps().stream().anyMatch(warp -> warp.canUse(player));
    }

    public void handleWorldLoad(@NonNull WorldLoadEvent event) {
        World world = event.getWorld();

        this.getWarps().stream().filter(Warp::isInactive).filter(warp -> warp.isWorld(world)).forEach(warp -> warp
            .activate(world));
    }

    public void handleWorldUnload(@NonNull WorldUnloadEvent event) {
        World world = event.getWorld();

        this.getWarps().stream().filter(Warp::isActive).filter(warp -> warp.isWorld(world)).forEach(Warp::deactivate);
    }

    public boolean openWarpsMenu(@NonNull Player player) {
        if (!this.hasAvailableWarps(player)) {
            this.sendPrefixed(WarpsLang.BROWSER_EMPTY, player);
            return false;
        }

        return this.listMenu.show(this.plugin, player);
    }

    public boolean openWarpSettings(@NonNull Player player, @NonNull Warp warp) {
        return this.settingsMenu.show(this.plugin, player, warp);
    }

    private void saveDirtyWarps() {
        this.getWarps().forEach(Warp::saveIfDirty);
    }

    public void loadWarps() {
        String dir = this.getSystemPath() + this.getWarpsDirectory();

        FileUtil.findYamlFiles(dir).forEach(file -> {
            String id = FileUtil.getNameWithoutExtension(file);
            Warp warp = new Warp(file, id);
            this.loadWarp(warp);
        });

        this.info("Loaded %s warps.".formatted(String.valueOf(this.repository.size())));
    }

    public boolean loadWarp(@NonNull Warp warp) {
        try {
            warp.load();
            warp.activate();
            this.repository.put(warp.getId(), warp);
            return true;
        }
        catch (WarpLoadException exception) {
            this.error("Could not load warp '%s': %s".formatted(warp.getFile(), exception.getMessage()));
            return false;
        }
    }

    public void unloadWarp(@NonNull Warp warp) {
        warp.deactivate();
        warp.clearCommand();
        this.repository.remove(warp.getId());
    }

    public void updateWarpCommands() {
        this.getWarps().forEach(this::updateWarpCommand);
    }

    public void updateWarpCommand(@NonNull Warp warp) {
        warp.clearCommand();

        if (!warp.isCommandEnabled()) return;

        String label = warp.getCommandLabel();

        NightCommand command = NightCommand.literal(this.plugin, label, builder -> builder
            .playerOnly()
            .description(PlaceholderContext.builder().with(warp.placeholders()).build().apply(
                WarpsLang.COMMAND_WARP_DESC.text()))
            .permission(warp.getPermission())
            .executes((context, arguments) -> {
                return this.teleportToWarp(warp, context.getPlayerOrThrow(), false);
            })
        );

        if (command.register()) {
            warp.setCommand(command);
        }
    }

    public boolean removeWarp(@NonNull CommandSender sender, @NonNull Warp warp, boolean force) {
        this.sendPrefixed(WarpsLang.WARP_DELETE_NOTIFY, sender, builder -> builder.with(warp.placeholders()));
        this.delete(warp);
        return true;
    }

    public void delete(@NonNull Warp warp) {
        try {
            this.unloadWarp(warp);
            Files.delete(warp.getFile());
        }
        catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public void clickWarp(@NonNull ActionContext context, @NonNull Warp warp) {
        Player player = context.getPlayer();
        InventoryClickEvent event = context.getEvent();

        if (event.isRightClick()) {
            if (warp.canEdit(player)) {
                this.openWarpSettings(player, warp);
            }
            return;
        }

        player.closeInventory();
        this.teleportToWarp(warp, player, false);
    }

    public boolean create(@NonNull Player player, @NonNull String name, boolean force) {
        Location location = player.getLocation();
        String id = Strings.varStyle(name).orElse(null);
        if (id == null) {
            this.sendPrefixed(WarpsLang.WARP_CREATION_INVALID_ID, player);
            return false;
        }

        Warp existent = this.getWarpById(id);
        if (existent != null) {
            this.sendPrefixed(WarpsLang.WARP_CREATION_ALREADY_EXISTS, player, builder -> builder.with(existent
                .placeholders()));
            return false;
        }

        Path file = Path.of(this.getSystemPath() + this.getWarpsDirectory(), FileConfig.withExtension(id));
        Warp warp = new Warp(file, id);

        warp.setName(StringUtil.capitalizeUnderscored(id));
        warp.setIcon(this.getSettings().getDefaultIcon());
        warp.setLocation(location);
        warp.save();
        this.loadWarp(warp);
        this.sendPrefixed(WarpsLang.WARP_CREATION_NOTIFY, player, builder -> builder.with(warp.placeholders()));

        return true;
    }

    public boolean updateWarp(@NonNull Player player, @NonNull Warp warp) {
        warp.setLocation(player.getLocation());
        warp.markDirty();
        this.sendPrefixed(WarpsLang.WARP_UPDATE_NOTIFY, player, builder -> builder.with(warp.placeholders()));
        return true;
    }

    public boolean teleportToWarp(@NonNull Warp warp, @NonNull Player player, boolean force) {
        if (!warp.isActive()) {
            this.sendPrefixed(WarpsLang.ERROR_INACTIVE_WARP, player, builder -> builder.with(warp.placeholders()));
            return false;
        }

        if (!force && !warp.hasPermission(player)) {
            this.sendPrefixed(WarpsLang.ERROR_NO_WARP_PERMISSION, player, replacer -> replacer.with(warp
                .placeholders()));
            return false;
        }

        WarpTeleportEvent event = new WarpTeleportEvent(player, warp);
        this.plugin.getPluginManager().callEvent(event);
        if (event.isCancelled()) return false;

        Location location = warp.getLocation();

        TeleportContext teleportContext = TeleportContext.builder(this, player, location)
            .withFlag(TeleportFlag.LOOK_FOR_SURFACE)
            .withFlag(TeleportFlag.AVOID_LAVA)
            .withFlag(TeleportFlag.CENTERED)
            .withFlagIf(TeleportFlag.BYPASS_WARMUP, () -> force)
            .callback(() -> {
                this.sendPrefixed(WarpsLang.WARP_TELEPORT_NOTIFY, player, builder -> builder.with(warp.placeholders()));
            })
            .build();

        return this.teleportManager.teleport(teleportContext, TeleportType.WARP);
    }
}
