package su.nightexpress.sunlight.module.godmode;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.core.user.settings.Setting;
import su.nightexpress.sunlight.core.user.settings.SettingRegistry;
import su.nightexpress.sunlight.data.user.SunUser;
import su.nightexpress.sunlight.module.Module;
import su.nightexpress.sunlight.module.ModuleInfo;
import su.nightexpress.sunlight.module.godmode.command.GodCommands;
import su.nightexpress.sunlight.module.godmode.config.GodConfig;
import su.nightexpress.sunlight.module.godmode.config.GodLang;
import su.nightexpress.sunlight.module.godmode.config.GodPerms;

public class GodModule extends Module {

    public static final Setting<Boolean> FOOD_GOD = SettingRegistry.register(Setting.create("food_god", false, true));
    public static final Setting<Boolean> GOD_MODE = SettingRegistry.register(Setting.create("god_mode", false, false));

    public GodModule(@NotNull SunLightPlugin plugin, @NotNull String id) {
        super(plugin, id);
    }

    @Override
    protected void gatherInfo(@NotNull ModuleInfo moduleInfo) {
        moduleInfo.setConfigClass(GodConfig.class);
        moduleInfo.setLangClass(GodLang.class);
        moduleInfo.setPermissionsClass(GodPerms.class);
    }

    @Override
    protected void onModuleLoad() {
        this.loadCommands();

        this.addListener(new GodListener(this.plugin, this));
    }

    @Override
    protected void onModuleUnload() {

    }

    private void loadCommands() {
        GodCommands.load(this.plugin, this);
    }

    public boolean hasAnyGod(@NotNull Player player) {
        return this.hasClassicGod(player) || this.hasFoodGod(player);
    }

    public boolean hasClassicGod(@NotNull Player player) {
        SunUser user = plugin.getUserManager().getOrFetch(player);
        return user.getSettings().get(GOD_MODE);
    }

    public boolean hasFoodGod(@NotNull Player player) {
        SunUser user = plugin.getUserManager().getOrFetch(player);
        return user.getSettings().get(FOOD_GOD);
    }

    public boolean isAllowedWorld(@NotNull World world) {
        return !GodConfig.DISABLED_WORLDS.get().contains(world.getName());
    }

    public boolean isAllowedWorld(@NotNull Player player) {
        if (player.hasPermission(GodPerms.BYPASS_WORLDS)) return true;
        return this.isAllowedWorld(player.getWorld());
    }
}
