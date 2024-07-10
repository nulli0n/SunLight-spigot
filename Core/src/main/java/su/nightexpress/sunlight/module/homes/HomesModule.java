package su.nightexpress.sunlight.module.homes;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.nightcore.util.StringUtil;
import su.nightexpress.sunlight.Placeholders;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.config.Lang;
import su.nightexpress.sunlight.data.user.SunUser;
import su.nightexpress.sunlight.module.Module;
import su.nightexpress.sunlight.module.ModuleInfo;
import su.nightexpress.sunlight.module.homes.command.HomeAdminCommands;
import su.nightexpress.sunlight.module.homes.command.HomeCommands;
import su.nightexpress.sunlight.module.homes.config.HomesConfig;
import su.nightexpress.sunlight.module.homes.config.HomesLang;
import su.nightexpress.sunlight.module.homes.config.HomesPerms;
import su.nightexpress.sunlight.module.homes.event.PlayerHomeCreateEvent;
import su.nightexpress.sunlight.module.homes.event.PlayerHomeRemoveEvent;
import su.nightexpress.sunlight.module.homes.impl.Home;
import su.nightexpress.sunlight.module.homes.impl.HomeType;
import su.nightexpress.sunlight.module.homes.listener.HomeListener;
import su.nightexpress.sunlight.module.homes.menu.HomeMenu;
import su.nightexpress.sunlight.module.homes.menu.HomesMenu;
import su.nightexpress.sunlight.module.homes.util.HomesCache;
import su.nightexpress.sunlight.utils.SunUtils;
import su.nightexpress.sunlight.utils.UserInfo;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class HomesModule extends Module {

    private final Map<UUID, Map<String, Home>> homeMap;
    private final Set<Home>                    scheduledToSave;
    private final HomesCache                   cache;

    private HomesMenu homesMenu;
    private HomeMenu  homeMenu;

    public HomesModule(@NotNull SunLightPlugin plugin, @NotNull String id) {
        super(plugin, id);
        this.homeMap = new ConcurrentHashMap<>();
        this.scheduledToSave = ConcurrentHashMap.newKeySet();
        this.cache = new HomesCache(plugin, this);
    }

    @Override
    protected void gatherInfo(@NotNull ModuleInfo moduleInfo) {
        moduleInfo.setConfigClass(HomesConfig.class);
        moduleInfo.setLangClass(HomesLang.class);
        moduleInfo.setPermissionsClass(HomesPerms.class);
    }

    @Override
    protected void onModuleLoad() {
        this.loadUI();
        this.loadCommands();
        this.loadHomes();

        this.addListener(new HomeListener(this.plugin, this));

        this.addTask(this.plugin.createAsyncTask(this::cleanUp).setSecondsInterval(HomesConfig.DATA_CLEANUP_INTERVAL.get()));
        this.addTask(this.plugin.createAsyncTask(this::saveHomes).setSecondsInterval(HomesConfig.DATA_SAVE_INTERVAL.get()));
    }

    @Override
    protected void onModuleUnload() {
        this.saveHomes();
        if (this.homesMenu != null) this.homesMenu.clear();

        this.homeMap.clear();
        this.cache.clear();
    }

    private void loadCommands() {
        HomeCommands.load(this.plugin, this);
        HomeAdminCommands.load(this.plugin, this);
    }

    private void loadUI() {
        this.homesMenu = new HomesMenu(this.plugin, this);
        this.homeMenu = new HomeMenu(this.plugin, this);
    }

    private void loadHomes() {
        this.plugin.getServer().getOnlinePlayers().forEach(player -> this.loadHomes(player.getUniqueId()));
        this.cache.initialize();
    }

    public void cleanUp() {
        new HashMap<>(this.homeMap).keySet().stream().toList().forEach(ownerId -> {
            if (!this.plugin.getUserManager().isLoaded(ownerId)) {
                this.unloadHomes(ownerId);
            }
        });
    }

    public void saveHomes() {
        if (this.scheduledToSave.isEmpty()) return;

        this.plugin.getData().saveHomes(this.scheduledToSave);
        this.scheduledToSave.clear();
    }

    public void saveHome(@NotNull Home home) {
        this.scheduledToSave.add(home);
    }

    public void deleteHome(@NotNull UserInfo owner, @NotNull String homeId) {
        this.getHomes(owner.getId()).remove(homeId);
        this.plugin.runTaskAsync(task -> this.plugin.getData().deleteHome(owner.getId(), homeId));
        this.cache.remove(owner.getName(), homeId);
    }

    @NotNull
    public HomesCache getCache() {
        return this.cache;
    }

    @NotNull
    public Map<UUID, Map<String, Home>> getHomeMap() {
        return this.homeMap;
    }

    @NotNull
    public Map<String, Home> getHomes(@NotNull Player player) {
        return this.getHomes(player.getUniqueId());
    }

    @NotNull
    public Map<String, Home> getHomes(@NotNull UUID playerId) {
        return this.homeMap.computeIfAbsent(playerId, k -> new HashMap<>());
    }

    @Nullable
    public Home getHome(@NotNull Player player, @NotNull String homeId) {
        return this.getHome(player.getUniqueId(), homeId);
    }

    @Nullable
    public Home getHome(@NotNull UUID playerId, @NotNull String homeId) {
        return this.getHomes(playerId).get(homeId.toLowerCase());
    }

    @Nullable
    public Home getDefaultHome(@NotNull Player player) {
        return this.getHomes(player).values().stream().filter(Home::isDefault).findFirst().orElse(null);
    }

    @Nullable
    public Home getRespawnHome(@NotNull Player player) {
        return this.getHomes(player).values().stream().filter(Home::isRespawnPoint).findFirst().orElse(null);
    }

    public boolean hasHome(@NotNull Player player) {
        return this.getHomesAmount(player) > 0;
    }

    public int getHomesAmount(@NotNull Player player) {
        return this.getHomes(player).size();
    }

    public int getHomesMaxAmount(@NotNull Player player) {
        return HomesConfig.HOMES_PER_RANK.get().getGreatestOrNegative(player);
    }

    public void loadHomesIfAbsent(@NotNull UUID playerId) {
        if (!this.homeMap.containsKey(playerId)) {
            this.loadHomes(playerId);
        }
    }

    public void loadHomes(@NotNull UUID playerId) {
        this.unloadHomes(playerId);
        this.plugin.getData().getHomes(playerId).forEach(home -> this.getHomes(playerId).put(home.getId(), home));
    }

    public void unloadHomes(@NotNull UUID playerId) {
        this.homeMap.remove(playerId);
    }

    public boolean isLoaded(@NotNull UUID playerId) {
        return this.homeMap.containsKey(playerId);
    }

    public void openHomes(@NotNull Player player) {
        this.openHomes(player, player.getUniqueId());
    }

    public void openHomes(@NotNull Player player, @NotNull UUID target) {
        this.homesMenu.open(player, target);
    }

    public void openHomeSettings(@NotNull Player player, @NotNull Home home) {
        this.homeMenu.open(player, home);
    }

    public boolean checkLocation(@NotNull Player player, @NotNull Location location, boolean notify) {
        if (!player.hasPermission(HomesPerms.BYPASS_CREATION_WORLDS)) {
            String world = player.getWorld().getName();
            if (HomesConfig.WORLD_BLACKLIST.get().contains(world)) {
                if (notify) HomesLang.HOME_SET_ERROR_WORLD.getMessage().send(player);
                return false;
            }
        }

        if (!player.hasPermission(HomesPerms.BYPASS_UNSAFE)) {
            if (!SunUtils.isSafeLocation(location)) {
                if (notify) HomesLang.HOME_SET_ERROR_UNSAFE.getMessage().send(player);
                return false;
            }
        }

        if (HomesConfig.CHECK_BUILD_ACCESS.get() && !player.hasPermission(HomesPerms.BYPASS_CREATION_PROTECTION)) {
            Block against = location.getBlock();
            Block placed = against.getRelative(BlockFace.UP);
            ItemStack item = new ItemStack(Material.STONE);
            BlockPlaceEvent event = new BlockPlaceEvent(placed, placed.getState(), against, item, player, true, EquipmentSlot.HAND);
            plugin.getPluginManager().callEvent(event);
            if (event.isCancelled()) {
                if (notify) HomesLang.HOME_SET_ERROR_PROTECTION.getMessage().send(player);
                return false;
            }
        }

        return true;
    }

    public boolean setHome(@NotNull Player player, @NotNull String name, boolean force) {
        return this.setHome(player, name, player.getLocation(), force);
    }

    public boolean setHome(@NotNull Player player, @NotNull String name, @NotNull Location location, boolean force) {
        name = StringUtil.lowerCaseUnderscore(name);

        Home home = this.getHome(player.getUniqueId(), name);

        if (!force) {
            int homesMax = this.getHomesMaxAmount(player);
            if (homesMax >= 0) {
                if (home == null && this.getHomesAmount(player) >= homesMax) {
                    HomesLang.HOME_SET_ERROR_LIMIT.getMessage().send(player);
                    return false;
                }
            }

            if (!this.checkLocation(player, location, true)) {
                return false;
            }

            PlayerHomeCreateEvent event = new PlayerHomeCreateEvent(player, name, location, home == null);
            this.plugin.getPluginManager().callEvent(event);
            if (event.isCancelled()) return false;
        }

        if (home != null) {
            home.setLocation(location);
            this.saveHome(home);
            HomesLang.HOME_SET_EDITED.getMessage().replace(home.replacePlaceholders()).send(player);
        }
        else {
            boolean hasHomes = !this.getHomes(player).isEmpty();

            home = this.createHome(name, new UserInfo(player), location);
            home.setDefault(!hasHomes);

            this.getHomes(player).put(home.getId(), home);
            HomesLang.HOME_SET_SUCCESS.getMessage().replace(home.replacePlaceholders()).send(player);
        }
        return true;
    }

    /**
     * Creates a new home for a player, caches it and stores it in the database, but does NOT stores it in the home map.
     * @param name Home name (identifier).
     * @param owner Home owner data.
     * @param location Home location.
     * @return Created home object.
     */
    @NotNull
    public Home createHome(@NotNull String name, @NotNull UserInfo owner, @NotNull Location location) {
        Home home = new Home(this.plugin, name, owner, location);
        this.cache.add(home);
        this.plugin.runTaskAsync(task -> this.plugin.getData().addHome(home));
        return home;
    }

    public void removeHome(@NotNull Player player, @NotNull Home home) {
        HomesLang.HOME_DELETE_DONE.getMessage().replace(home.replacePlaceholders()).send(player);
        this.removeHome(home);

        PlayerHomeRemoveEvent event = new PlayerHomeRemoveEvent(player, home);
        this.plugin.getPluginManager().callEvent(event);
    }

    public void removeHome(@NotNull Home home) {
        this.deleteHome(home.getOwner(), home.getId());
    }

    public void setHomeType(@NotNull Home home, @NotNull HomeType homeType) {
        home.setType(homeType);
        this.saveHome(home);
        this.cache.add(home);
    }

    public void addHomeInvite(@NotNull Player player, @NotNull Home home, @NotNull String userName) {
        if (player.getName().equalsIgnoreCase(userName)) {
            Lang.ERROR_COMMAND_NOT_YOURSELF.getMessage().send(player);
            return;
        }


        this.plugin.getUserManager().manageUser(userName, user -> {
            if (user == null) {
                Lang.ERROR_INVALID_PLAYER.getMessage().send(player);
                return;
            }

            this.addHomeInvite(player, home, user);

            HomesLang.COMMAND_HOME_INVITE_DONE.getMessage()
                .replace(Placeholders.PLAYER_NAME, user.getName())
                .replace(home.replacePlaceholders())
                .send(player);
        });
    }

    public void addHomeInvite(@NotNull Player player, @NotNull Home home, @NotNull SunUser user) {
        home.addInvitedPlayer(new UserInfo(user));
        this.saveHome(home);
        this.cache.add(home);
    }
}
