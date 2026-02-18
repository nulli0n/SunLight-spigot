package su.nightexpress.sunlight.module.homes;

import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.Bed;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Colorable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.core.config.CoreLang;
import su.nightexpress.nightcore.user.UserInfo;
import su.nightexpress.nightcore.util.LowerCase;
import su.nightexpress.nightcore.util.NumberUtil;
import su.nightexpress.nightcore.util.Players;
import su.nightexpress.nightcore.util.StringUtil;
import su.nightexpress.nightcore.util.geodata.pos.BlockPos;
import su.nightexpress.nightcore.util.placeholder.CommonPlaceholders;
import su.nightexpress.sunlight.SLPlaceholders;
import su.nightexpress.sunlight.config.PermissionTree;
import su.nightexpress.sunlight.hook.placeholder.PlaceholderRegistry;
import su.nightexpress.sunlight.module.Module;
import su.nightexpress.sunlight.module.ModuleContext;
import su.nightexpress.sunlight.module.homes.command.HomeAdminCommandProvider;
import su.nightexpress.sunlight.module.homes.command.HomeCommonCommandProvider;
import su.nightexpress.sunlight.module.homes.config.HomesLang;
import su.nightexpress.sunlight.module.homes.config.HomesPerms;
import su.nightexpress.sunlight.module.homes.config.HomesSettings;
import su.nightexpress.sunlight.module.homes.data.HomeDataManager;
import su.nightexpress.sunlight.module.homes.dialog.HomeDialogKeys;
import su.nightexpress.sunlight.module.homes.dialog.impl.HomeInvitePlayerDialog;
import su.nightexpress.sunlight.module.homes.dialog.impl.HomeNameDialog;
import su.nightexpress.sunlight.module.homes.event.PlayerHomeCreateEvent;
import su.nightexpress.sunlight.module.homes.event.PlayerHomeRemoveEvent;
import su.nightexpress.sunlight.module.homes.event.PlayerHomeTeleportEvent;
import su.nightexpress.sunlight.module.homes.impl.Home;
import su.nightexpress.sunlight.module.homes.listener.HomeListener;
import su.nightexpress.sunlight.module.homes.menu.HomeSettingsMenu;
import su.nightexpress.sunlight.module.homes.menu.HomesMenu;
import su.nightexpress.sunlight.module.homes.menu.IconSelectionMenu;
import su.nightexpress.sunlight.module.homes.menu.InvitedPlayersMenu;
import su.nightexpress.sunlight.module.homes.repository.GlobalHomeRepository;
import su.nightexpress.sunlight.module.homes.repository.UserHomeRepository;
import su.nightexpress.sunlight.teleport.*;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class HomesModule extends Module {

    private final TeleportManager      teleportManager;
    private final HomeDataManager      dataManager;
    private final GlobalHomeRepository repository;
    private final HomesSettings        settings;

    private HomesMenu          homesMenu;
    private HomeSettingsMenu   homeMenu;
    private IconSelectionMenu  iconSelectionMenu;
    private InvitedPlayersMenu invitedPlayersMenu;

    private boolean loaded;

    public HomesModule(@NotNull ModuleContext context, @NotNull TeleportManager teleportManager) {
        super(context);
        this.teleportManager = teleportManager;
        this.dataManager = new HomeDataManager(this, this.dataHandler);
        this.repository = new GlobalHomeRepository();
        this.settings = new HomesSettings();
    }

    @Override
    protected void loadModule(@NotNull FileConfig config) {
        this.settings.load(config);
        this.plugin.injectLang(HomesLang.class);
        this.dataManager.init();

        this.loadUI();
        this.loadHomes();

        this.addListener(new HomeListener(this.plugin, this));

        this.addAsyncTask(this::saveHomes, this.settings.getDataSaveInterval());
    }

    @Override
    protected void unloadModule() {
        this.loaded = false;

        this.saveHomes();
        this.homesMenu = null;

        this.repository.clear();
    }

    @Override
    protected void registerPermissions(@NotNull PermissionTree root) {
        root.merge(HomesPerms.ROOT);
    }

    @Override
    protected void registerCommands() {
        this.commandRegistry.addProvider("homes-common", new HomeCommonCommandProvider(this.plugin, this, this.userManager));
        this.commandRegistry.addProvider("homes-admin", new HomeAdminCommandProvider(this.plugin, this, this.userManager));
    }

    @Override
    public void registerPlaceholders(@NotNull PlaceholderRegistry registry) {
        registry.register("homes_limit", (player, payload) -> {
            int limit = this.getMaxHomesValue(player);
            return limit >= 0 ? NumberUtil.format(limit) : CoreLang.OTHER_INFINITY.text();
        });

        registry.register("homes_amount", (player, payload) -> {
            return NumberUtil.format(this.countHomes(player));
        });

        registry.register("homes_favorite_home", (player, payload) -> {
            return this.favoriteHome(player).map(Home::getName).orElse(CoreLang.OTHER_NONE.text());
        });
    }

    private void loadUI() {
        this.homesMenu = new HomesMenu(this.plugin, this);
        this.homeMenu = new HomeSettingsMenu(this.plugin, this, this.dialogRegistry);
        this.iconSelectionMenu = new IconSelectionMenu(this.plugin, this);
        this.invitedPlayersMenu = new InvitedPlayersMenu(this.plugin, this, this.dialogRegistry);

        this.dialogRegistry.register(HomeDialogKeys.HOME_NAME, new HomeNameDialog());
        this.dialogRegistry.register(HomeDialogKeys.HOME_INVITE_PLAYER_NAME, new HomeInvitePlayerDialog(this.plugin, this, this.userManager));
    }

    private void loadHomes() {
        this.plugin.runTaskAsync(() -> {
            this.dataManager.getHomes().forEach(home -> {
                this.repository.add(home);
                home.activate();
            });
            this.loaded = true;
            this.info("Loaded and cached all player homes.");
        });
    }

    public void saveHomes() {
        Set<Home> homes = this.repository.getAll(Home::isDirty).stream().peek(Home::markClean).collect(Collectors.toSet());

        this.dataManager.saveHomes(homes);
    }

    public void deleteHome(@NotNull Home home) {
        this.repository.remove(home);
        this.plugin.runTaskAsync(() -> this.dataManager.deleteHome(home));
    }

    @NotNull
    public HomesSettings getSettings() {
        return this.settings;
    }

    @NotNull
    public GlobalHomeRepository getRepository() {
        return this.repository;
    }

    @NotNull
    public UserHomeRepository getUserRepository(@NotNull UUID playerId) {
        return this.repository.getUserRepository(playerId);
    }

    @NotNull
    public Set<Home> getHomes(@NotNull Player player) {
        return this.getHomes(player.getUniqueId());
    }

    @NotNull
    public Set<Home> getHomes(@NotNull UUID playerId) {
        return this.repository.getUserRepository(playerId).getAll();
    }

    @Nullable
    public Home getHome(@NotNull Player player, @NotNull String homeId) {
        return this.getHome(player.getUniqueId(), homeId);
    }

    @Nullable
    public Home getHome(@NotNull UUID playerId, @NotNull String homeId) {
        return this.getUserRepository(playerId).getById(homeId);
    }

    @Nullable
    public Home getFavoriteHome(@NotNull Player player) {
        return this.getHomes(player).stream().filter(Home::isFavorite).findFirst().orElse(null);
    }

    @NotNull
    public Optional<Home> favoriteHome(@NotNull Player player) {
        return Optional.ofNullable(this.getFavoriteHome(player));
    }

    public boolean hasHome(@NotNull Player player) {
        return this.countHomes(player) > 0;
    }

    public int countHomes(@NotNull Player player) {
        return this.getHomes(player).size();
    }

    public int getMaxHomesValue(@NotNull Player player) {
        return this.settings.getHomesByRankAmount().getGreatestOrNegative(player).intValue();
    }

    private boolean checkDataLoaded(@NotNull CommandSender sender) {
        if (!this.loaded) {
            this.sendPrefixed(HomesLang.DATA_NOT_LOADED, sender);
            return false;
        }

        return true;
    }

    public void openHomes(@NotNull Player player) {
        this.openHomes(player, player.getUniqueId());
    }

    public boolean openHomes(@NotNull Player player, @NotNull UUID target) {
        return this.homesMenu.show(this.plugin, player, target);
    }

    public boolean openHomeSettings(@NotNull Player player, @NotNull Home home) {
        return this.homeMenu.show(this.plugin, player, home);
    }

    public boolean openIconSelection(@NotNull Player player, @NotNull Home home) {
        return this.iconSelectionMenu.show(this.plugin, player, home);
    }

    public boolean openInvitedPlayersMenu(@NotNull Player player, @NotNull Home home) {
        return this.invitedPlayersMenu.show(this.plugin, player, home);
    }

    public boolean canCreateMoreHomes(@NotNull Player player) {
        int max = this.getMaxHomesValue(player);
        if (max < 0) return true;

        return this.countHomes(player) < max;
    }

    public void handleRespawn(@NotNull PlayerRespawnEvent event) {
        this.favoriteHome(event.getPlayer()).filter(Home::isActive).ifPresent(home -> event.setRespawnLocation(home.toLocation()));
    }

    public void handleBedInteract(@NotNull PlayerInteractEvent event) {
        if (!this.settings.isBedModeEnabled()) return;
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (event.useInteractedBlock() == Event.Result.DENY) return;

        Block block = event.getClickedBlock();
        if (block == null || !(block.getBlockData() instanceof Bed)) return;
        if (!(block.getState() instanceof Colorable colorable)) return;

        Player player = event.getPlayer();
        Location location = block.getLocation();
        DyeColor color = Optional.ofNullable(colorable.getColor()).orElse(DyeColor.RED);

        String homeId;
        boolean overrideRespawn = this.settings.isBedModeOverrideRespawn();

        if (this.settings.isBedModeWithColors()) {
            homeId = LowerCase.INTERNAL.apply(color.name());
        }
        else {
            homeId = HomeDefaults.DEFAULT_HOME_ID;
        }

        Home currentHome = this.getHome(player, homeId);

        // Set home if not set or override when sneaking.
        if (currentHome == null || player.isSneaking()) {
            event.setUseInteractedBlock(Event.Result.DENY);

            // Allow to just sleep on bed if can't create more homes.
            if (currentHome == null && !this.canCreateMoreHomes(player)) {
                player.sleep(block.getLocation(), false);
                return;
            }

            // Set or override home's location.
            if (!this.setHome(player, homeId, location, false)) return;

            Home setHome = this.getHome(player, homeId);
            if (setHome == null) return;

            player.swingMainHand();

            if (!overrideRespawn) return;

            // Clear old favorite home.
            this.favoriteHome(player).ifPresent(favorite -> {
                favorite.setFavorite(false);
                favorite.markDirty();
            });

            // Set new home as favorite one.
            setHome.setFavorite(true);
            //player.setRespawnLocation(location);
            return;
        }

        if (overrideRespawn) {
            event.setUseInteractedBlock(Event.Result.DENY);
            player.swingMainHand();
            this.openHomeSettings(player, currentHome);
            //player.sleep(location, false);
        }
    }

    public void handleWorldLoad(@NotNull WorldLoadEvent event) {
        World world = event.getWorld();

        this.repository.getAll().stream().filter(Home::isInactive).filter(home -> home.isWorld(world)).forEach(home -> home.activate(world));
    }

    public void handleWorldUnload(@NotNull WorldUnloadEvent event) {
        World world = event.getWorld();

        this.repository.getAll().stream().filter(Home::isActive).filter(home -> home.isWorld(world)).forEach(Home::deactivate);
    }

    public boolean checkLocation(@NotNull Player player, @NotNull Location location, boolean notify) {
        if (this.settings.isCheckBuildAccess() && !player.hasPermission(HomesPerms.BYPASS_CREATION_PROTECTION)) {
            Block against = location.getBlock();
            Block placed = against.getRelative(BlockFace.UP);
            ItemStack item = new ItemStack(Material.STONE);
            BlockPlaceEvent event = new BlockPlaceEvent(placed, placed.getState(), against, item, player, true, EquipmentSlot.HAND);
            plugin.getPluginManager().callEvent(event);
            if (event.isCancelled()) {
                if (notify) this.sendPrefixed(HomesLang.HOME_SET_ERROR_PROTECTION, player);
                return false;
            }
        }

        return true;
    }

    public boolean setHome(@NotNull Player player, @NotNull String name, boolean force) {
        return this.setHome(player, name, player.getLocation(), force);
    }

    public boolean setHome(@NotNull Player player, @NotNull String aname, @NotNull Location location, boolean force) {
        if (!this.checkDataLoaded(player)) return false;

        String id = StringUtil.lowerCaseUnderscore(aname);

        Home currentHome = this.getHome(player.getUniqueId(), id);

        if (!force) {
            int maxHomesValue = this.getMaxHomesValue(player);
            int countHomes = this.countHomes(player);
            if (currentHome == null && maxHomesValue >= 0 && countHomes >= maxHomesValue) {
                this.sendPrefixed(HomesLang.HOME_SET_ERROR_LIMIT, player, builder -> builder
                    .with(SLPlaceholders.GENERIC_AMOUNT, () -> String.valueOf(countHomes))
                    .with(SLPlaceholders.GENERIC_MAX, () -> String.valueOf(maxHomesValue))
                );
                return false;
            }

            if (!this.checkLocation(player, location, true)) {
                return false;
            }

            if (!player.hasPermission(HomesPerms.BYPASS_CREATION_WORLDS) && this.settings.isBlacklistedWorld(player.getWorld().getName())) {
                this.sendPrefixed(HomesLang.HOME_SET_ERROR_WORLD, player);
                return false;
            }

            PlayerHomeCreateEvent event = new PlayerHomeCreateEvent(player, id, location, currentHome == null);
            this.plugin.getPluginManager().callEvent(event);
            if (event.isCancelled()) return false;
        }

        if (currentHome == null) {
            Home created = this.createHome(id, UserInfo.of(player), location);
            if (this.countHomes(player) == 0) {
                created.setFavorite(true);
            }
            this.sendPrefixed(HomesLang.HOME_SET_CREATED, player, builder -> builder.with(created.placeholders()));
        }
        else {
            currentHome.updateLocation(location);
            currentHome.markDirty();
            this.sendPrefixed(HomesLang.HOME_SET_MOVED, player, builder -> builder.with(currentHome.placeholders()));
        }
        return true;
    }

    @NotNull
    public Home createHome(@NotNull String id, @NotNull UserInfo owner, @NotNull Location location) {
        Home home = Home.createDefault(id, owner, this.settings.getDefaultIconId(), location.getWorld(), BlockPos.from(location));
        home.activate();

        this.repository.add(home);
        this.plugin.runTaskAsync(() -> this.dataManager.addHome(home));
        return home;
    }

    public boolean removeHome(@NotNull Player player, @NotNull Home home) {
        this.deleteHome(home);
        this.sendPrefixed(HomesLang.HOME_DELETE_DONE, player, builder -> builder.with(home.placeholders()));

        PlayerHomeRemoveEvent event = new PlayerHomeRemoveEvent(player, home);
        this.plugin.getPluginManager().callEvent(event);
        return true;
    }

    public boolean teleportToHome(@NotNull Player player, @NotNull Home home) {
        if (!home.isActive()) {
            this.sendPrefixed(HomesLang.HOME_TELEPORT_ERROR_INACTIVE, player, builder -> builder.with(home.placeholders()));
            return false;
        }

        PlayerHomeTeleportEvent event = new PlayerHomeTeleportEvent(player, home);
        this.plugin.getPluginManager().callEvent(event);
        if (event.isCancelled()) return false;

        boolean isOwner = home.isOwner(player);
        boolean bypass = player.hasPermission(HomesPerms.BYPASS_UNSAFE_LOCATION);

        Location location = home.toLocation();

        TeleportContext teleportContext = TeleportContext.builder(this, player, location)
            .withFlag(TeleportFlag.CENTERED)
            .withFlagIf(TeleportFlag.LOOK_FOR_SURFACE, () -> !isOwner && !bypass)
            .withFlagIf(TeleportFlag.AVOID_LAVA, () -> !isOwner && !bypass)
            .callback(() -> {
                this.sendPrefixed(isOwner ? HomesLang.HOME_TELEPORT_SUCCESS : HomesLang.HOME_VISIT_SUCCESS, player, builder -> builder.with(home.placeholders()));
            })
            .build();

        return this.teleportManager.teleport(teleportContext, TeleportType.HOME);
    }

    public boolean visitHome(@NotNull Player player, @NotNull Home home) {
        if (!home.canVisit(player)) {
            this.sendPrefixed(HomesLang.HOME_VISIT_ERROR_NOT_PERMITTED, player, builder -> builder.with(home.placeholders()));
            return false;
        }

        return this.teleportToHome(player, home);
    }

    public boolean inviteToHome(@NotNull Player player, @NotNull Home home, @NotNull UserInfo profile) {
        if (profile.isUser(player)) {
            this.sendPrefixed(HomesLang.HOME_INVITE_ERROR_YOURSELF, player);
            return false;
        }

        home.addInvitedPlayer(profile);
        home.markDirty();

        Player target = Players.getPlayer(profile.id());
        if (target != null) {
            this.sendPrefixed(HomesLang.HOME_INVITE_SUCCESS_NOTIFY, player, builder -> builder
                .with(CommonPlaceholders.PLAYER.resolver(player))
                .with(home.placeholders())
            );
        }

        this.sendPrefixed(HomesLang.HOME_INVITE_SUCCESS_FEEDBACK, player, builder -> builder
            .with(CommonPlaceholders.PLAYER_NAME, () -> target == null ? profile.name() : target.getName())
            .with(home.placeholders())
        );

        return true;
    }
}
