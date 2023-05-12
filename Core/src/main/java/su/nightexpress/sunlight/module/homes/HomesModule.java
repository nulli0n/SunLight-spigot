package su.nightexpress.sunlight.module.homes;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockCanBuildEvent;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.hooks.Hooks;
import su.nexmedia.engine.hooks.external.WorldGuardHook;
import su.nexmedia.engine.utils.StringUtil;
import su.nightexpress.sunlight.SunLight;
import su.nightexpress.sunlight.config.Lang;
import su.nightexpress.sunlight.data.impl.SunUser;
import su.nightexpress.sunlight.module.Module;
import su.nightexpress.sunlight.module.homes.command.admin.HomesAdminCommand;
import su.nightexpress.sunlight.module.homes.command.basic.HomesCommand;
import su.nightexpress.sunlight.module.homes.config.HomesConfig;
import su.nightexpress.sunlight.module.homes.config.HomesLang;
import su.nightexpress.sunlight.module.homes.event.PlayerHomeCreateEvent;
import su.nightexpress.sunlight.module.homes.event.PlayerHomeRemoveEvent;
import su.nightexpress.sunlight.module.homes.impl.Home;
import su.nightexpress.sunlight.module.homes.impl.HomeType;
import su.nightexpress.sunlight.module.homes.listener.HomeListener;
import su.nightexpress.sunlight.module.homes.menu.HomesMenu;
import su.nightexpress.sunlight.module.homes.task.HomesCleanupTask;
import su.nightexpress.sunlight.module.homes.util.HomesCache;
import su.nightexpress.sunlight.module.homes.util.HomesPerms;
import su.nightexpress.sunlight.utils.UserInfo;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class HomesModule extends Module {

    private final Map<UUID, Map<String, Home>> homes;
    private final HomesCache                   cache;

    private HomesMenu        homesMenu;
    private HomesCleanupTask cleanupTask;

    public HomesModule(@NotNull SunLight plugin, @NotNull String id) {
        super(plugin, id);
        this.homes = new ConcurrentHashMap<>();
        this.cache = new HomesCache(this);
    }

    @Override
    public void onLoad() {
        this.getConfig().initializeOptions(HomesConfig.class);
        this.plugin.getLangManager().loadMissing(HomesLang.class);
        this.plugin.getLangManager().setupEnum(HomeType.class);
        this.plugin.getLang().saveChanges();
        this.plugin.registerPermissions(HomesPerms.class);
        this.plugin.getCommandRegulator().register(HomesCommand.NAME, (cfg1, aliases) -> new HomesCommand(this, aliases));
        this.plugin.getCommandRegulator().register(HomesAdminCommand.NAME, (cfg1, aliases) -> new HomesAdminCommand(this, aliases));

        this.addListener(new HomeListener(this));

        this.plugin.getServer().getOnlinePlayers().forEach(player -> this.loadHomes(player.getUniqueId()));
        this.cache.initialize();

        this.cleanupTask = new HomesCleanupTask(this);
        this.cleanupTask.start();
    }

    @Override
    public void onShutdown() {
        if (this.cleanupTask != null) {
            this.cleanupTask.stop();
            this.cleanupTask = null;
        }
        if (this.homesMenu != null) {
            this.homesMenu.clear();
            this.homesMenu = null;
        }
        this.homes.values().forEach(homes -> homes.values().forEach(home -> {
            this.plugin.getData().saveHome(home);
            home.clear();
        }));
        this.homes.clear();
        this.cache.clear();
    }

    @NotNull
    public HomesCache getCache() {
        return cache;
    }

    @NotNull
    public Map<UUID, Map<String, Home>> getHomes() {
        return homes;
    }

    @NotNull
    public Map<String, Home> getHomes(@NotNull Player player) {
        return this.getHomes(player.getUniqueId());
    }

    @NotNull
    public Map<String, Home> getHomes(@NotNull UUID playerId) {
        return this.getHomes().computeIfAbsent(playerId, k -> new HashMap<>());
    }

    @NotNull
    public Optional<Home> getHome(@NotNull Player player, @NotNull String homeId) {
        return this.getHome(player.getUniqueId(), homeId);
    }

    @NotNull
    public Optional<Home> getHome(@NotNull UUID playerId, @NotNull String homeId) {
        return Optional.ofNullable(this.getHomes(playerId).get(homeId.toLowerCase()));
    }

    @NotNull
    public Optional<Home> getHomeDefault(@NotNull Player player) {
        return this.getHomes(player).values().stream().filter(Home::isDefault).findFirst();
    }

    @NotNull
    public Optional<Home> getHomeToRespawn(@NotNull Player player) {
        return this.getHomes(player).values().stream().filter(Home::isRespawnPoint).findFirst();
    }

    public int getHomesAmount(@NotNull Player player) {
        return this.getHomes(player).size();
    }

    public int getHomesMaxAmount(@NotNull Player player) {
        return Hooks.getGroupValueInt(player, HomesConfig.HOMES_PER_RANK.get(), true);
    }

    public void loadHomesIfAbsent(@NotNull UUID playerId) {
        if (!this.getHomes().containsKey(playerId)) {
            this.loadHomes(playerId);
        }
    }

    public void loadHomes(@NotNull UUID playerId) {
        this.unloadHomes(playerId);
        this.plugin.getData().getHomes(playerId).forEach(home -> this.getHomes(playerId).put(home.getId(), home));
    }

    public void unloadHomes(@NotNull UUID playerId) {
        this.getHomes(playerId).values().forEach(Home::clear);
        this.getHomes().remove(playerId);
        this.getHomesMenu().clearHolder(playerId);
    }

    public boolean isLoaded(@NotNull UUID playerId) {
        return this.getHomes().containsKey(playerId);
    }

    @NotNull
    public HomesMenu getHomesMenu() {
        if (this.homesMenu == null) {
            this.homesMenu = new HomesMenu(this);
        }
        return homesMenu;
    }

    public boolean canSetHome(@NotNull Player player, @NotNull Location location, boolean notify) {
        if (HomesConfig.CHECK_BUILD_ACCESS.get() && !player.hasPermission(HomesPerms.BYPASS_CREATION_PROTECTION)) {
            Block block = location.getBlock();
            BlockCanBuildEvent event = new BlockCanBuildEvent(block, player, block.getBlockData(), true);
            plugin.getPluginManager().callEvent(event);
            if (!event.isBuildable()) {
                if (notify) this.plugin.getMessage(HomesLang.HOME_SET_ERROR_PROTECTION).send(player);
                return false;
            }
        }

        if (!player.hasPermission(HomesPerms.BYPASS_CREATION_WORLDS)) {
            String world = player.getWorld().getName();
            if (HomesConfig.WORLD_BLACKLIST.get().contains(world)) {
                if (notify) this.plugin.getMessage(HomesLang.HOME_SET_ERROR_WORLD).send(player);
                return false;
            }
        }

        if (Hooks.hasWorldGuard() && !player.hasPermission(HomesPerms.BYPASS_CREATION_REGIONS)) {
            String region = WorldGuardHook.getRegion(location);
            if (HomesConfig.REGION_BLACKLIST.get().contains(region)) {
                if (notify) this.plugin.getMessage(HomesLang.HOME_SET_ERROR_REGION).send(player);
                return false;
            }
        }
        return true;
    }

    public boolean setHome(@NotNull Player player, @NotNull String id, boolean force) {
        id = StringUtil.lowerCaseUnderscore(id);

        SunUser user = plugin.getUserManager().getUserData(player);
        Home home = this.getHome(player.getUniqueId(), id).orElse(null);
        Location location = player.getLocation();
        // TODO Check for safe location

        if (!force) {
            int homesMax = this.getHomesMaxAmount(player);
            if (homesMax >= 0) {
                if (home == null && this.getHomesAmount(player) >= homesMax) {
                    this.plugin.getMessage(HomesLang.HOME_SET_ERROR_LIMIT).send(player);
                    return false;
                }
            }

            if (!this.canSetHome(player, location, true)) {
                return false;
            }

            PlayerHomeCreateEvent event = new PlayerHomeCreateEvent(player, id, location, home == null);
            this.plugin.getPluginManager().callEvent(event);
            if (event.isCancelled()) return false;
        }

        if (home != null) {
            home.setLocation(location);
            home.save();
            this.plugin.getMessage(HomesLang.HOME_SET_EDITED).replace(home.replacePlaceholders()).send(player);
        }
        else {
            home = this.createHome(id, new UserInfo(player), location);
            this.plugin.getMessage(HomesLang.HOME_SET_SUCCESS).replace(home.replacePlaceholders()).send(player);
        }
        return true;
    }

    @NotNull
    public Home createHome(@NotNull String homeId, @NotNull UserInfo owner, @NotNull Location location) {
        Home home = new Home(plugin, homeId, owner, location);
        this.getHomes(owner.getId()).put(home.getId(), home);
        if (this.getHomes(owner.getId()).size() == 1) {
            home.setDefault(true);
        }
        this.getCache().cache(home);
        this.plugin.runTaskAsync(task -> this.plugin.getData().addHome(home));
        return home;
    }

    public boolean removeHome(@NotNull Player player, @NotNull String homeId) {
        Home home = this.getHome(player, homeId).orElse(null);
        if (home == null) {
            this.plugin.getMessage(HomesLang.HOME_ERROR_INVALID).send(player);
            return false;
        }

        this.plugin.getMessage(HomesLang.HOME_DELETE_DONE).replace(home.replacePlaceholders()).send(player);
        this.removeHome(home);

        PlayerHomeRemoveEvent event = new PlayerHomeRemoveEvent(player, home);
        this.plugin.getPluginManager().callEvent(event);

        return true;
    }

    public void removeHome(@NotNull Home home) {
        home.clear();
        this.deleteHome(home.getOwner(), home.getId());
    }

    public void deleteHome(@NotNull UserInfo owner, @NotNull String homeId) {
        this.getHomes(owner.getId()).remove(homeId);
        this.plugin.runTaskAsync(task -> this.plugin.getData().deleteHome(owner.getId(), homeId));
        this.getCache().uncache(owner.getName(), homeId);
    }

    public void setHomeType(@NotNull Home home, @NotNull HomeType homeType) {
        home.setType(homeType);
        home.save();
        this.getCache().cache(home);
    }

    public boolean addHomeInvite(@NotNull Player player, @NotNull Home home, @NotNull String userName) {
        if (player.getName().equalsIgnoreCase(userName)) {
            plugin.getMessage(Lang.ERROR_COMMAND_SELF).send(player);
            return false;
        }

        SunUser user = plugin.getUserManager().getUserData(userName);
        if (user == null) {
            plugin.getMessage(Lang.ERROR_PLAYER_INVALID).send(player);
            return false;
        }

        home.addInvitedPlayer(new UserInfo(user));
        home.save();
        this.getCache().cache(home);
        return true;
    }
}
