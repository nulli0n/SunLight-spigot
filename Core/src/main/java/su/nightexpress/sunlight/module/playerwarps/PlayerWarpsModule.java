package su.nightexpress.sunlight.module.playerwarps;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.core.config.CoreLang;
import su.nightexpress.nightcore.integration.currency.EconomyBridge;
import su.nightexpress.nightcore.ui.inventory.action.ActionContext;
import su.nightexpress.nightcore.user.UserInfo;
import su.nightexpress.nightcore.util.FileUtil;
import su.nightexpress.nightcore.util.NumberUtil;
import su.nightexpress.nightcore.util.StringUtil;
import su.nightexpress.nightcore.util.Strings;
import su.nightexpress.nightcore.util.placeholder.CommonPlaceholders;
import su.nightexpress.sunlight.SLPlaceholders;
import su.nightexpress.sunlight.config.PermissionTree;
import su.nightexpress.sunlight.hook.placeholder.PlaceholderRegistry;
import su.nightexpress.sunlight.module.Module;
import su.nightexpress.sunlight.module.ModuleContext;
import su.nightexpress.sunlight.module.playerwarps.category.AllCategory;
import su.nightexpress.sunlight.module.playerwarps.category.NormalCategory;
import su.nightexpress.sunlight.module.playerwarps.category.OwnCategory;
import su.nightexpress.sunlight.module.playerwarps.category.WarpCategory;
import su.nightexpress.sunlight.module.playerwarps.command.PlayerWarpsCommands;
import su.nightexpress.sunlight.module.playerwarps.core.PlayerWarpsLang;
import su.nightexpress.sunlight.module.playerwarps.core.PlayerWarpsPerms;
import su.nightexpress.sunlight.module.playerwarps.core.PlayerWarpsSettings;
import su.nightexpress.sunlight.module.playerwarps.dialog.PlayerWarpsDialogKeys;
import su.nightexpress.sunlight.module.playerwarps.dialog.impl.PlayerWarpCategoryDialog;
import su.nightexpress.sunlight.module.playerwarps.dialog.impl.PlayerWarpDescriptionDialog;
import su.nightexpress.sunlight.module.playerwarps.dialog.impl.PlayerWarpFeaturingDialog;
import su.nightexpress.sunlight.module.playerwarps.dialog.impl.PlayerWarpNameDialog;
import su.nightexpress.sunlight.module.playerwarps.dialog.impl.PlayerWarpPriceDialog;
import su.nightexpress.sunlight.module.playerwarps.dialog.impl.PlayerWarpsSearchDialog;
import su.nightexpress.sunlight.module.playerwarps.event.PlayerWarpTeleportEvent;
import su.nightexpress.sunlight.module.playerwarps.exception.PlayerWarpLoadException;
import su.nightexpress.sunlight.module.playerwarps.featuring.FeaturedData;
import su.nightexpress.sunlight.module.playerwarps.featuring.FeaturedSlot;
import su.nightexpress.sunlight.module.playerwarps.menu.PlayerWarpOptionsMenu;
import su.nightexpress.sunlight.module.playerwarps.menu.PlayerWarpSortType;
import su.nightexpress.sunlight.module.playerwarps.menu.PlayerWarpsListMenu;
import su.nightexpress.sunlight.module.playerwarps.menu.PlayerWarpsMainMenu;
import su.nightexpress.sunlight.module.playerwarps.menu.WarpsListData;
import su.nightexpress.sunlight.teleport.TeleportContext;
import su.nightexpress.sunlight.teleport.TeleportFlag;
import su.nightexpress.sunlight.teleport.TeleportManager;
import su.nightexpress.sunlight.teleport.TeleportType;

public class PlayerWarpsModule extends Module {

    private final TeleportManager teleportManager;

    private final PlayerWarpRepository repository;
    private final PlayerWarpsSettings  settings;

    private PlayerWarpsMainMenu   mainMenu;
    private PlayerWarpOptionsMenu settingsMenu;
    private PlayerWarpsListMenu   warpsListMenu;

    public PlayerWarpsModule(@NonNull ModuleContext context, @NonNull TeleportManager teleportManager) {
        super(context);
        this.teleportManager = teleportManager;
        this.repository = new PlayerWarpRepository();
        this.settings = new PlayerWarpsSettings();
    }

    @Override
    protected void loadModule(@NonNull FileConfig config) {
        this.plugin.injectLang(PlayerWarpsLang.class);
        this.settings.load(config);

        this.mainMenu = new PlayerWarpsMainMenu(this, this.settings);
        this.mainMenu.load(this.plugin, FileConfig.load(this.getLocalUIPath(), PlayerWarpsFiles.FILE_MAIN_MENU));
        this.plugin.injectLang(this.mainMenu);

        this.settingsMenu = new PlayerWarpOptionsMenu(this);
        this.settingsMenu.load(this.plugin, FileConfig.load(this.getLocalUIPath(),
            PlayerWarpsFiles.FILE_WARP_SETTINGS));

        this.warpsListMenu = new PlayerWarpsListMenu(this.plugin, this, this.settings, this.repository);
        this.warpsListMenu.load(this.plugin, FileConfig.load(this.getLocalUIPath(), PlayerWarpsFiles.FILE_WARP_LIST));
        this.plugin.injectLang(this.warpsListMenu);

        this.dialogRegistry.register(PlayerWarpsDialogKeys.WARP_NAME, () -> new PlayerWarpNameDialog(this.settings));
        this.dialogRegistry.register(PlayerWarpsDialogKeys.WARP_DESCRIPTION,
            () -> new PlayerWarpDescriptionDialog(this.settings));
        this.dialogRegistry.register(PlayerWarpsDialogKeys.WARP_PRICE, PlayerWarpPriceDialog::new);
        this.dialogRegistry.register(PlayerWarpsDialogKeys.WARP_SEARCH, () -> new PlayerWarpsSearchDialog(this));
        this.dialogRegistry.register(PlayerWarpsDialogKeys.WARP_FEATURING, () -> new PlayerWarpFeaturingDialog(this));
        this.dialogRegistry.register(PlayerWarpsDialogKeys.WARP_CATEGORY, () -> new PlayerWarpCategoryDialog(this));

        this.loadWarps();

        if (this.settings.isFeaturingEnabled()) {
            this.addTask(this::updateFeaturedWarps, this.settings.getFeaturedWarpsUpdateInterval());
        }
        if (this.settings.isPopularEnabled()) {
            this.addTask(this::updatePopularWarps, this.settings.getPopularWarpsUpdateInterval());
        }

        this.addAsyncTask(this::saveDirtyWarps, this.getSettings().getSaveInterval());
    }

    @Override
    protected void unloadModule() {
        this.saveDirtyWarps();
    }

    @Override
    protected void registerPermissions(@NonNull PermissionTree root) {
        root.merge(PlayerWarpsPerms.ROOT);
    }

    @Override
    protected void registerCommands() {
        this.commandRegistry.addProvider("playerwarps", new PlayerWarpsCommands(this.plugin, this, this.userManager));
    }

    @Override
    public void registerPlaceholders(@NonNull PlaceholderRegistry registry) {
        registry.register("playerwarps_creation_limit", (player, payload) -> {
            int limit = this.getAllowedWarpsAmount(player);
            return limit >= 0 ? NumberUtil.format(limit) : CoreLang.OTHER_INFINITY.text();
        });

        registry.register("playerwarps_creation_available", (player, payload) -> {
            int limit = this.getAllowedWarpsAmount(player);
            int created = this.getOwnedWarpsAmount(player);
            return NumberUtil.format(Math.max(0, limit - created));
        });

        registry.register("playerwarps_own_amount", (player, payload) -> {
            return NumberUtil.format(this.getOwnedWarpsAmount(player));
        });

        registry.register("playerwarps_total_amount", (player, payload) -> {
            return NumberUtil.format(this.repository.size());
        });
    }

    public void loadWarps() {
        String dir = this.getSystemPath() + this.getWarpsDirectory();

        FileUtil.findYamlFiles(dir).forEach(file -> {
            String id = FileUtil.getNameWithoutExtension(file);
            PlayerWarp warp = new PlayerWarp(file, id);
            this.loadWarp(warp);
        });

        this.info("Loaded %s player warps.".formatted(String.valueOf(this.repository.size())));
    }

    public boolean loadWarp(@NonNull PlayerWarp warp) {
        try {
            warp.load();
            warp.activate();
            warp.updateCategory(this.settings);
            this.repository.add(warp);
            return true;
        }
        catch (PlayerWarpLoadException exception) {
            this.error("Could not load warp '%s': %s".formatted(warp.getFile(), exception.getMessage()));
            return false;
        }
    }

    protected void saveDirtyWarps() {
        this.repository.stream().forEach(PlayerWarp::saveIfDirty);
    }

    public void updateFeaturedWarps() {
        this.repository.updateFeaturedWarps();
        this.mainMenu.refresh();
    }

    public void updatePopularWarps() {
        this.repository.updatePopularWarps(this.settings.getPopularSlots().length);
        this.mainMenu.refresh();
    }

    public void handleWorldLoad(@NonNull WorldLoadEvent event) {
        World world = event.getWorld();

        this.repository.getAll().stream().filter(PlayerWarp::isInactive).filter(warp -> warp.isWorld(world)).forEach(
            warp -> warp.activate(world));
    }

    public void handleWorldUnload(@NonNull WorldUnloadEvent event) {
        World world = event.getWorld();

        this.repository.getAll().stream().filter(PlayerWarp::isActive).filter(warp -> warp.isWorld(world)).forEach(
            PlayerWarp::deactivate);
    }

    @NonNull
    public String getWarpsDirectory() {
        return PlayerWarpsFiles.DIR_WARPS;
    }

    @NonNull
    public PlayerWarpRepository getRepository() {
        return this.repository;
    }

    @NonNull
    public PlayerWarpsSettings getSettings() {
        return this.settings;
    }

    @NonNull
    public Set<PlayerWarp> getAvailableWarps(@NonNull Player player) {
        return this.repository.stream().filter(warp -> warp.canUse(player)).collect(Collectors.toSet());
    }

    @NonNull
    public Set<PlayerWarp> getOwnedWarps(@NonNull Player player) {
        return this.repository.getByOwner(player.getUniqueId());
    }

    public void openCategoryDialog(@NonNull Player player, @NonNull PlayerWarp warp, @Nullable Runnable callback) {
        this.dialogRegistry.show(player, PlayerWarpsDialogKeys.WARP_CATEGORY, warp, callback);
    }

    public void openNameDialog(@NonNull Player player, @NonNull PlayerWarp warp, @Nullable Runnable callback) {
        this.dialogRegistry.show(player, PlayerWarpsDialogKeys.WARP_NAME, warp, callback);
    }

    public void openDescriptionDialog(@NonNull Player player, @NonNull PlayerWarp warp, @Nullable Runnable callback) {
        this.dialogRegistry.show(player, PlayerWarpsDialogKeys.WARP_DESCRIPTION, warp, callback);
    }

    public void openPriceDialog(@NonNull Player player, @NonNull PlayerWarp warp, @Nullable Runnable callback) {
        this.dialogRegistry.show(player, PlayerWarpsDialogKeys.WARP_PRICE, warp, callback);
    }

    public void openSearchDialog(@NonNull Player player, @NonNull WarpsListData data) {
        this.dialogRegistry.show(player, PlayerWarpsDialogKeys.WARP_SEARCH, data, null);
    }

    public int getAllowedWarpsAmount(@NonNull Player player) {
        return this.settings.getMaxWarpsAmount(player);
    }

    public int getOwnedWarpsAmount(@NonNull Player player) {
        return this.getOwnedWarps(player).size();
    }

    public void openWarpsMenu(@NonNull Player player) {
        this.mainMenu.show(this.plugin, player);
    }

    public void openWarpSettings(@NonNull Player player, @NonNull PlayerWarp warp) {
        this.settingsMenu.show(this.plugin, player, warp);
    }

    public boolean openWarpsList(@NonNull Player player, @NonNull WarpCategory category, @Nullable PlayerWarpSortType sortType, @Nullable String searchText) {
        return this.warpsListMenu.show(player, category, sortType, searchText);
    }

    public boolean openOwnWarpsList(@NonNull Player player, @NonNull UUID playerId) {
        return this.openWarpsList(player, new OwnCategory(player), null, null);
    }

    public boolean openAllWarpsList(@NonNull Player player) {
        return this.openWarpsList(player, new AllCategory(), null, null);
    }

    public boolean openFeaturingDialog(@NonNull Player player, @NonNull FeaturedSlot slot, int slotIndex) {
        if (this.repository.getByOwner(player.getUniqueId()).stream().noneMatch(PlayerWarp::canFeature)) {
            this.sendPrefixed(PlayerWarpsLang.WARP_FEATURE_NOTHING, player);
            return false;
        }

        PlayerWarpFeaturingDialog.Data data = new PlayerWarpFeaturingDialog.Data(slot, slotIndex);

        this.dialogRegistry.show(player, PlayerWarpsDialogKeys.WARP_FEATURING, data, () -> this.mainMenu.refresh(
            player));

        return true;
    }

    public void clickWarp(@NonNull ActionContext context, @NonNull PlayerWarp warp) {
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

    public boolean removeWarp(@NonNull CommandSender sender, @NonNull PlayerWarp warp, boolean force) {
        if (sender instanceof Player player) {
            if (!warp.canEdit(player)) {
                this.sendPrefixed(PlayerWarpsLang.ERROR_NOT_OWN_WARP, player, builder -> builder.with(warp
                    .placeholders()));
                return false;
            }
        }

        this.sendPrefixed(PlayerWarpsLang.WARP_DELETE_NOTIFY, sender, builder -> builder.with(warp.placeholders()));
        this.delete(warp);
        return true;
    }

    public void delete(@NonNull PlayerWarp warp) {
        try {
            Files.delete(warp.getFile());
            this.repository.remove(warp);
        }
        catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public boolean create(@NonNull Player player, @NonNull String name, boolean force) {
        Location location = player.getLocation();
        String id = Strings.varStyle(name).orElse(null);
        if (id == null) {
            this.sendPrefixed(PlayerWarpsLang.WARP_CREATION_INVALID_ID, player);
            return false;
        }


        int idLimit = this.settings.getWarpIdCharacterLimit();
        if (idLimit > 0 && id.length() > idLimit) {
            this.sendPrefixed(PlayerWarpsLang.WARP_CREATION_LONG_ID, player, builder -> builder
                .with(CommonPlaceholders.GENERIC_AMOUNT, () -> String.valueOf(idLimit))
            );
            return false;
        }

        if (!force) {
            int maxAllowed = this.getAllowedWarpsAmount(player);
            int created = this.getOwnedWarpsAmount(player);
            if (maxAllowed >= 0 && created >= maxAllowed) {
                this.sendPrefixed(PlayerWarpsLang.WARP_CREATION_LIMIT_REACHED, player, builder -> builder
                    .with(CommonPlaceholders.GENERIC_AMOUNT, () -> NumberUtil.format(created))
                    .with(SLPlaceholders.GENERIC_MAX, () -> NumberUtil.format(maxAllowed))
                );
                return false;
            }

            if (!player.hasPermission(PlayerWarpsPerms.BYPASS_CREATION_WORLD)) {
                if (this.settings.isBlacklistedWorld(player.getWorld())) {
                    this.sendPrefixed(PlayerWarpsLang.WARP_CREATION_BANNED_WORLD, player);
                    return false;
                }
            }
        }

        PlayerWarp existent = this.getRepository().getById(id);
        if (existent != null) {
            this.sendPrefixed(PlayerWarpsLang.WARP_CREATION_ALREADY_EXISTS, player, builder -> builder.with(existent
                .placeholders()));
            return false;
        }

        NormalCategory category = this.settings.getPrimaryCategory();
        if (category == null) {
            this.sendPrefixed(PlayerWarpsLang.ERROR_NO_PRIMARY_CATEGORY, player);
            return false;
        }

        Path file = Path.of(this.getSystemPath() + this.getWarpsDirectory(), FileConfig.withExtension(id));
        PlayerWarp warp = new PlayerWarp(file, id);

        warp.setOwner(UserInfo.of(player));
        warp.setCategory(category);
        warp.setCreationTimestamp(System.currentTimeMillis());
        warp.setName(StringUtil.capitalizeUnderscored(id));
        warp.setIcon(this.getSettings().getDefaultIcon());
        warp.setLocation(location);
        warp.save();
        this.loadWarp(warp);
        this.sendPrefixed(PlayerWarpsLang.WARP_CREATION_NOTIFY, player, builder -> builder.with(warp.placeholders()));

        return true;
    }

    public boolean updateWarp(@NonNull Player player, @NonNull PlayerWarp warp, boolean force) {
        if (!warp.canEdit(player)) {
            this.sendPrefixed(PlayerWarpsLang.ERROR_NOT_OWN_WARP, player, builder -> builder.with(warp.placeholders()));
            return false;
        }

        warp.setLocation(player.getLocation());
        warp.markDirty();
        this.sendPrefixed(PlayerWarpsLang.WARP_UPDATE_NOTIFY, player, builder -> builder.with(warp.placeholders()));

        return true;
    }

    public boolean teleportToWarp(@NonNull PlayerWarp warp, @NonNull Player player, boolean force) {
        if (!warp.isActive()) {
            this.sendPrefixed(PlayerWarpsLang.WARP_JUMP_INACTIVE, player, builder -> builder.with(warp.placeholders()));
            return false;
        }

        if (warp.hasPrice() && !player.hasPermission(PlayerWarpsPerms.BYPASS_PRICE) && !warp.isOwner(player)) {
            double price = warp.getPrice();

            if (EconomyBridge.getEconomyBalance(player) < price) {
                this.sendPrefixed(PlayerWarpsLang.WARP_JUMP_INSUFFICIENT_FUNDS, player, builder -> builder.with(warp
                    .placeholders()));
                return false;
            }

            EconomyBridge.withdrawEconomy(player, price);
        }

        PlayerWarpTeleportEvent event = new PlayerWarpTeleportEvent(player, warp);
        this.plugin.getPluginManager().callEvent(event);
        if (event.isCancelled()) return false;

        Location location = warp.getLocation();
        TeleportContext teleportContext = TeleportContext.builder(this, player, location)
            .withFlag(TeleportFlag.LOOK_FOR_SURFACE)
            .withFlag(TeleportFlag.AVOID_LAVA)
            .withFlag(TeleportFlag.CENTERED)
            .withFlagIf(TeleportFlag.BYPASS_WARMUP, () -> force)
            .callback(() -> {
                if (!warp.isOwner(player)) {
                    warp.addVisitCount(); // TODO Per player cooldown
                    warp.markDirty();
                }

                this.sendPrefixed(PlayerWarpsLang.WARP_JUMP_NOTIFY, player, builder -> builder.with(warp
                    .placeholders()));
            })
            .build();

        return this.teleportManager.teleport(teleportContext, TeleportType.PLAYER_WARP);
    }

    public boolean purchaseFeaturedSlot(@NonNull Player player, @NonNull FeaturedSlot slot, int slotIndex, @NonNull String warpId) {
        PlayerWarp warp = this.repository.getById(warpId);
        if (warp == null) {
            this.sendPrefixed(PlayerWarpsLang.WARP_FEATURE_UNEXPECTED_ERROR, player);
            return false;
        }

        if (!warp.canEdit(player)) {
            this.sendPrefixed(PlayerWarpsLang.ERROR_NOT_OWN_WARP, player);
            return false;
        }

        if (warp.isFeatured()) {
            this.sendPrefixed(PlayerWarpsLang.WARP_FEATURE_UNEXPECTED_ERROR, player);
            return false;
        }

        if (this.repository.isFeatured(slot, slotIndex)) {
            this.sendPrefixed(PlayerWarpsLang.WARP_FEATURE_UNEXPECTED_ERROR, player);
            return false;
        }

        if (!slot.canAfford(player)) {
            this.sendPrefixed(PlayerWarpsLang.WARP_FEATURE_CANT_AFFORD, player, builder -> builder.with(slot
                .placeholders()));
            return false;
        }

        slot.pay(player);
        warp.setFeaturedData(new FeaturedData(slot.id(), slotIndex, slot.createEndTimestamp()));
        warp.markDirty();
        this.updateFeaturedWarps();

        this.sendPrefixed(PlayerWarpsLang.WARP_FEATURE_SUCCESS, player, builder -> builder.with(slot.placeholders())
            .with(warp.placeholders()));
        return true;
    }
}
