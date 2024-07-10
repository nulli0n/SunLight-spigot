package su.nightexpress.sunlight.module.nerfphantoms;

import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.core.user.settings.Setting;
import su.nightexpress.sunlight.core.user.settings.SettingRegistry;
import su.nightexpress.sunlight.data.user.SunUser;
import su.nightexpress.sunlight.module.Module;
import su.nightexpress.sunlight.module.ModuleInfo;
import su.nightexpress.sunlight.module.nerfphantoms.command.NoPhantomCommand;
import su.nightexpress.sunlight.module.nerfphantoms.config.PhantomsConfig;
import su.nightexpress.sunlight.module.nerfphantoms.config.PhantomsLang;
import su.nightexpress.sunlight.module.nerfphantoms.config.PhantomsPerms;
import su.nightexpress.sunlight.module.nerfphantoms.listener.PhantomsListener;

public class PhantomsModule extends Module {

    public static final Setting<Boolean> ANTI_PHANTOM = SettingRegistry.register(Setting.create("anti_phantom", false, true));

    public PhantomsModule(@NotNull SunLightPlugin plugin, @NotNull String id) {
        super(plugin, id);
    }

    @Override
    protected void gatherInfo(@NotNull ModuleInfo moduleInfo) {
        moduleInfo.setConfigClass(PhantomsConfig.class);
        moduleInfo.setLangClass(PhantomsLang.class);
        moduleInfo.setPermissionsClass(PhantomsPerms.class);
    }

    @Override
    protected void onModuleLoad() {
        this.registerCommands();

        this.addListener(new PhantomsListener(this.plugin, this));
        this.addTask(this.plugin.createTask(this::resetRestTime).setSecondsInterval(600));
    }

    @Override
    protected void onModuleUnload() {

    }

    private void registerCommands() {
        NoPhantomCommand.load(this.plugin, this);
    }


    public void setAntiPhantom(@NotNull Player player, boolean state) {
        SunUser user = plugin.getUserManager().getUserData(player);
        user.getSettings().set(ANTI_PHANTOM, state);
        this.plugin.getUserManager().scheduleSave(user);
    }

    private void resetRestTime() {
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            SunUser user = plugin.getUserManager().getUserData(player);
            if (!user.getSettings().get(ANTI_PHANTOM)) continue;

            player.setStatistic(Statistic.TIME_SINCE_REST, 0);
        }
    }
}
