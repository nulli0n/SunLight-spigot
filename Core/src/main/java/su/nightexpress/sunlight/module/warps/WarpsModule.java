package su.nightexpress.sunlight.module.warps;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.nightcore.util.FileUtil;
import su.nightexpress.nightcore.util.StringUtil;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.module.Module;
import su.nightexpress.sunlight.module.ModuleInfo;
import su.nightexpress.sunlight.module.warps.command.WarpCommands;
import su.nightexpress.sunlight.module.warps.command.WarpShortcutCommand;
import su.nightexpress.sunlight.module.warps.config.WarpsConfig;
import su.nightexpress.sunlight.module.warps.config.WarpsLang;
import su.nightexpress.sunlight.module.warps.config.WarpsPerms;
import su.nightexpress.sunlight.module.warps.impl.Warp;
import su.nightexpress.sunlight.module.warps.menu.DisplayInfo;
import su.nightexpress.sunlight.module.warps.menu.WarpListMenu;
import su.nightexpress.sunlight.module.warps.menu.WarpSettingsMenu;
import su.nightexpress.sunlight.module.warps.type.WarpType;
import su.nightexpress.sunlight.module.warps.util.WarpUtils;
import su.nightexpress.sunlight.utils.SunUtils;
import su.nightexpress.sunlight.utils.UserInfo;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class WarpsModule extends Module {

    public static final String DIR_WARPS = "/warps/";

    private final Map<String, Warp> warpMap;

    private WarpListMenu     warpListMenu;
    private WarpSettingsMenu settingsMenu;

    public WarpsModule(@NotNull SunLightPlugin plugin, @NotNull String id) {
        super(plugin, id);
        this.warpMap = new HashMap<>();
    }

    @Override
    protected void gatherInfo(@NotNull ModuleInfo moduleInfo) {
        moduleInfo.setConfigClass(WarpsConfig.class);
        moduleInfo.setLangClass(WarpsLang.class);
        moduleInfo.setPermissionsClass(WarpsPerms.class);
    }

    @Override
    protected void onModuleLoad() {
        this.loadCommands();
        this.loadUI();
        this.loadWarps();
    }

    @Override
    protected void onModuleUnload() {
        if (this.warpListMenu != null) this.warpListMenu.clear();
        if (this.settingsMenu != null) this.settingsMenu.clear();

        this.warpMap.clear();
    }

    private void loadCommands() {
        WarpCommands.load(this.plugin, this);
    }

    private void loadUI() {
        this.warpListMenu = new WarpListMenu(this.plugin, this);
        this.settingsMenu = new WarpSettingsMenu(this.plugin, this);
    }

    private void loadWarps() {
        for (File file : FileUtil.getConfigFiles(this.getAbsolutePath() + DIR_WARPS, true)) {
            Warp warp = new Warp(this.plugin, this, file);
            this.loadWarp(warp);
        }

        this.info("Loaded " + this.warpMap.size() + " warps.");
    }

    private boolean loadWarp(@NotNull Warp warp) {
        if (warp.load()) {
            this.warpMap.put(warp.getId(), warp);
            return true;
        }

        this.error("Warp not loaded: '" + warp.getFile().getName() + "' !");
        return false;
    }

    public void delete(@NotNull Warp warp) {
        if (warp.getFile().delete()) {
            WarpShortcutCommand.unregister(this.plugin, warp);
            this.warpMap.remove(warp.getId());
        }
    }

    public boolean create(@NotNull Player player, @NotNull String id, boolean isForced) {
        Location location = player.getLocation();

        if (!isForced) {
            int maxAllowed = this.getAllowedWarpsAmount(player);
            if (maxAllowed >= 0 && this.getOwnedWarpsAmount(player) >= maxAllowed) {
                WarpsLang.WARP_CREATE_ERROR_LIMIT.getMessage().send(player);
                return false;
            }

            if (!player.hasPermission(WarpsPerms.BYPASS_CREATION_WORLD)) {
                if (WarpsConfig.WARP_SET_WORLD_BLACKLIST.get().contains(player.getWorld().getName())) {
                    WarpsLang.WARP_CREATE_ERROR_WORLD.getMessage().send(player);
                    return false;
                }
            }

            if (!player.hasPermission(WarpsPerms.BYPASS_CREATION_SAFE) && !SunUtils.isSafeLocation(location)) {
                WarpsLang.WARP_CREATE_ERROR_UNSAFE.getMessage().send(player);
                return false;
            }
        }

        Warp warp = this.getWarp(id);
        if (warp != null) {
            if (!warp.canEdit(player)) {
                WarpsLang.WARP_CREATE_ERROR_EXISTS.getMessage().replace(warp.replacePlaceholders()).send(player);
                return false;
            }
            else {
                warp.setLocation(location);
                warp.save();
                WarpsLang.WARP_CREATE_DONE_RELOCATE.getMessage().replace(warp.replacePlaceholders()).send(player);
            }
        }
        else {
            File file = new File(this.getAbsolutePath() + DIR_WARPS, id + ".yml");
            WarpType type = player.hasPermission(WarpsPerms.MODULE) ? WarpType.SERVER : WarpType.PLAYER;

            warp = new Warp(this.plugin, this, file);
            warp.setOwner(new UserInfo(player));
            warp.setName(StringUtil.capitalizeUnderscored(id));
            warp.setIcon(WarpUtils.getDefaultIcon(player, type));
            warp.setLocation(location);
            warp.setType(type);
            warp.save();
            this.loadWarp(warp);

            WarpsLang.WARP_CREATE_DONE_FRESH.getMessage().replace(warp.replacePlaceholders()).send(player);
        }
        return true;
    }

    @NotNull
    public Map<String, Warp> getWarpsMap() {
        return this.warpMap;
    }

    @NotNull
    public Collection<Warp> getWarps() {
        return this.warpMap.values();
    }

    public void openServerWarps(@NotNull Player player) {
        this.warpListMenu.open(player, new DisplayInfo(WarpType.SERVER));
    }

    public void openPlayerWarps(@NotNull Player player) {
        this.warpListMenu.open(player, new DisplayInfo(WarpType.PLAYER));
    }

    public void openWarpSettings(@NotNull Player player, @NotNull Warp warp) {
        this.settingsMenu.open(player, warp);
    }

    public int getAllowedWarpsAmount(@NotNull Player player) {
        return WarpsConfig.WARP_SET_AMOUNT_PER_GROUP.get().getGreatestOrNegative(player);
    }

    public int getOwnedWarpsAmount(@NotNull Player player) {
        return this.getOwnedWarps(player).size();
    }

    @NotNull
    public Collection<Warp> getAvailableWarps(@NotNull Player player) {
        return this.getWarps().stream().filter(warp -> warp.isAvailable(player)).collect(Collectors.toSet());
    }

    @NotNull
    public Collection<Warp> getOwnedWarps(@NotNull Player player) {
        return this.getWarps().stream().filter(warp -> warp.isOwner(player)).collect(Collectors.toSet());
    }

    @NotNull
    public List<Warp> getWarps(@NotNull WarpType warpType) {
        return this.getWarps().stream().filter(warp -> warp.getType() == warpType).toList();
    }

    @Nullable
    public Warp getWarp(@NotNull String id) {
        return this.warpMap.get(id.toLowerCase());
    }

    @Nullable
    public Warp getActiveWarp(@NotNull String id) {
        Warp warp = this.getWarp(id);
        return warp == null || !warp.isValid() ? null : warp;
    }
}
