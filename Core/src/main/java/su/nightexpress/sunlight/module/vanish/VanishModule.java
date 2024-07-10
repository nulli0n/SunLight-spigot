package su.nightexpress.sunlight.module.vanish;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.core.user.settings.Setting;
import su.nightexpress.sunlight.core.user.settings.SettingRegistry;
import su.nightexpress.sunlight.data.user.SunUser;
import su.nightexpress.sunlight.module.Module;
import su.nightexpress.sunlight.module.ModuleInfo;
import su.nightexpress.sunlight.module.vanish.command.VanishCommand;
import su.nightexpress.sunlight.module.vanish.config.VanishConfig;
import su.nightexpress.sunlight.module.vanish.config.VanishLang;
import su.nightexpress.sunlight.module.vanish.config.VanishPerms;

public class VanishModule extends Module {

    public static final Setting<Boolean> VANISH = SettingRegistry.register(Setting.create("vanish", false, false));

    public VanishModule(@NotNull SunLightPlugin plugin, @NotNull String id) {
        super(plugin, id);
    }

    @Override
    protected void gatherInfo(@NotNull ModuleInfo moduleInfo) {
        moduleInfo.setConfigClass(VanishConfig.class);
        moduleInfo.setLangClass(VanishLang.class);
        moduleInfo.setPermissionsClass(VanishPerms.class);
    }

    @Override
    protected void onModuleLoad() {
        VanishCommand.load(this.plugin, this);

        this.addListener(new VanishListener(this.plugin, this));
    }

    @Override
    protected void onModuleUnload() {

    }

    public boolean isVanished(@NotNull Player player) {
        SunUser user = this.plugin.getUserManager().getUserData(player);
        return user.getSettings().get(VANISH);
    }

    public void vanish(@NotNull Player player, boolean isVanished) {
        for (Player other : this.plugin.getServer().getOnlinePlayers()) {
            if (isVanished) {
                if (!other.hasPermission(VanishPerms.BYPASS_SEE)) {
                    other.hidePlayer(this.plugin, player);
                }
            }
            else {
                other.showPlayer(this.plugin, player);
            }
        }
    }
}
