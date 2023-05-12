package su.nightexpress.sunlight.module.warps;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nexmedia.engine.api.config.JYML;
import su.nexmedia.engine.command.CommandRegister;
import su.nexmedia.engine.hooks.Hooks;
import su.nexmedia.engine.utils.StringUtil;
import su.nightexpress.sunlight.Perms;
import su.nightexpress.sunlight.SunLight;
import su.nightexpress.sunlight.module.Module;
import su.nightexpress.sunlight.module.warps.command.basic.WarpsCommand;
import su.nightexpress.sunlight.module.warps.config.WarpsConfig;
import su.nightexpress.sunlight.module.warps.config.WarpsLang;
import su.nightexpress.sunlight.module.warps.impl.Warp;
import su.nightexpress.sunlight.module.warps.menu.WarpsMenu;
import su.nightexpress.sunlight.module.warps.type.WarpSortType;
import su.nightexpress.sunlight.module.warps.type.WarpType;
import su.nightexpress.sunlight.module.warps.util.WarpsPerms;
import su.nightexpress.sunlight.utils.SunUtils;
import su.nightexpress.sunlight.utils.UserInfo;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class WarpsModule extends Module {

    public static final String DIR_WARPS = "/warps/";

    private final Map<String, Warp> warps;

    private WarpsMenu warpsMenu;

    public WarpsModule(@NotNull SunLight plugin, @NotNull String id) {
        super(plugin, id);
        this.warps = new HashMap<>();
    }

    @Override
    public void onLoad() {
        this.plugin.registerPermissions(WarpsPerms.class);
        this.plugin.getLangManager().loadMissing(WarpsLang.class);
        this.plugin.getLangManager().setupEnum(WarpSortType.class);
        this.plugin.getLangManager().setupEnum(WarpType.class);
        this.plugin.getLang().saveChanges();
        this.getConfig().initializeOptions(WarpsConfig.class);

        this.plugin.runTask(task -> this.loadWarps());
        this.warpsMenu = new WarpsMenu(this);
        this.plugin.getCommandRegulator().register(WarpsCommand.NAME, (cfg1, aliases) -> new WarpsCommand(this, aliases));
    }

    private void loadWarps() {
        for (JYML cfg : JYML.loadAll(this.getAbsolutePath() + DIR_WARPS, true)) {
            Warp warp = new Warp(this, cfg);
            if (warp.load()) {
                this.getWarpsMap().put(warp.getId(), warp);
            }
            else this.error("Warp not loaded: '" + cfg.getFile().getName() + "' !");
        }

        this.info("Loaded " + this.getWarpsMap().size() + " warps.");
        CommandRegister.syncCommands();
    }

    @Override
    public void onShutdown() {
        this.getWarps().forEach(Warp::clear);
        this.getWarpsMap().clear();

        if (this.warpsMenu != null) {
            this.warpsMenu.clear();
            this.warpsMenu = null;
        }
    }

    public void delete(@NotNull Warp warp) {
        if (warp.getFile().delete()) {
            warp.clear();
            this.getWarpsMap().remove(warp.getId());
        }
    }

    public boolean create(@NotNull Player player, @NotNull String id, boolean isForced) {
        Location location = player.getLocation();

        if (!isForced) {
            int maxAllowed = this.getWarpsMaxAmount(player);
            if (maxAllowed >= 0 && this.getWarpsCreatedAmount(player) >= maxAllowed) {
                this.plugin.getMessage(WarpsLang.WARP_CREATE_ERROR_LIMIT).send(player);
                return false;
            }

            if (!player.hasPermission(WarpsPerms.BYPASS_CREATION_WORLD)) {
                if (WarpsConfig.WARP_SET_WORLD_BLACKLIST.get().contains(player.getWorld().getName())) {
                    this.plugin.getMessage(WarpsLang.WARP_CREATE_ERROR_WORLD).send(player);
                    return false;
                }
            }

            if (!player.hasPermission(WarpsPerms.BYPASS_CREATION_SAFE) && !SunUtils.isSafeLocation(location)) {
                this.plugin.getMessage(WarpsLang.WARP_CREATE_ERROR_UNSAFE).send(player);
                return false;
            }
        }

        Warp warp = this.getWarpById(id);
        if (warp != null) {
            if (!warp.isOwner(player)) {
                this.plugin.getMessage(WarpsLang.WARP_CREATE_ERROR_EXISTS).replace(warp.replacePlaceholders()).send(player);
                return false;
            }
            else {
                warp.setLocation(location);
                warp.save();
                this.plugin.getMessage(WarpsLang.WARP_CREATE_DONE_RELOCATE).replace(warp.replacePlaceholders()).send(player);
            }
        }
        else {
            JYML cfg = new JYML(this.getAbsolutePath() + DIR_WARPS, id + ".yml");
            warp = new Warp(this, cfg);
            warp.setOwner(new UserInfo(player));
            warp.setName(StringUtil.capitalizeUnderscored(id));
            warp.setIcon(new ItemStack(Material.COMPASS));
            warp.setLocation(location);
            warp.setType(player.hasPermission(Perms.PLUGIN) ? WarpType.SERVER : WarpType.PLAYER);
            warp.save();
            warp.load();
            this.getWarpsMap().put(warp.getId(), warp);
            this.plugin.getMessage(WarpsLang.WARP_CREATE_DONE_FRESH).replace(warp.replacePlaceholders()).send(player);
        }
        return true;
    }

    @NotNull
    public WarpsMenu getWarpsMenu() {
        return warpsMenu;
    }

    public int getWarpsMaxAmount(@NotNull Player player) {
        return Hooks.getGroupValueInt(player, WarpsConfig.WARP_SET_AMOUNT_PER_GROUP.get(), true);
    }

    public int getWarpsCreatedAmount(@NotNull Player player) {
        return this.getWarpsCreated(player).size();
    }

    @NotNull
    public Map<String, Warp> getWarpsMap() {
        return this.warps;
    }

    @NotNull
    public Collection<Warp> getWarps() {
        return this.getWarpsMap().values();
    }

    @NotNull
    public Collection<Warp> getWarpsAvailable(@NotNull Player player) {
        return this.getWarps().stream().filter(warp -> warp.isAvailable(player)).collect(Collectors.toSet());
    }

    @NotNull
    public Collection<Warp> getWarpsCreated(@NotNull Player player) {
        return this.getWarps().stream().filter(warp -> warp.isOwner(player)).collect(Collectors.toSet());
    }

    @NotNull
    public List<Warp> getWarps(@NotNull WarpType warpType) {
        return this.getWarps().stream().filter(warp -> warp.getType() == warpType).toList();
    }

    @Nullable
    public Warp getWarpById(@NotNull String id) {
        return this.getWarpsMap().get(id.toLowerCase());
    }
}
