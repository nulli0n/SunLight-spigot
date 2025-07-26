package su.nightexpress.sunlight.module.vanish;

import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.util.Players;
import su.nightexpress.nightcore.util.text.NightMessage;
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

    public static final Setting<Boolean> VANISH = SettingRegistry.register(Setting.create("vanish", false, true));

    private BossBar vanishIndicator;

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

        if (VanishConfig.BAR_INDICATOR_ENABLED.get()) {
            String title = VanishConfig.BAR_INDICATOR_VANISHED_TITLE.get();
            BarColor color = VanishConfig.BAR_INDICATOR_VANISHED_COLOR.get();
            BarStyle style = VanishConfig.BAR_INDICATOR_VANISHED_STYLE.get();

            this.vanishIndicator = this.plugin.getServer().createBossBar(NightMessage.asLegacy(title), color, style);
        }

        this.plugin.runTask(task -> this.updateOnlinePlayers());
    }

    @Override
    protected void onModuleUnload() {
        Players.getOnline().forEach(player -> this.vanish(player, false));

        this.vanishIndicator.removeAll();
        this.vanishIndicator = null;
    }

    private void updateOnlinePlayers() {
        Players.getOnline().forEach(player -> {
            if (!this.isVanished(player)) return;

            this.vanish(player, true);
        });
    }

    public boolean isVanished(@NotNull Player player) {
        SunUser user = this.plugin.getUserManager().getOrFetch(player);
        return user.getSettings().get(VANISH);
    }

    public void vanish(@NotNull Player player, boolean isVanished) {
        for (Player other : this.plugin.getServer().getOnlinePlayers()) {
            if (isVanished) {
                if (!other.hasPermission(VanishPerms.BYPASS_SEE)) {
                    other.hidePlayer(this.plugin, player);
                }
                if (this.vanishIndicator != null) this.vanishIndicator.addPlayer(player);
            }
            else {
                other.showPlayer(this.plugin, player);
                if (this.vanishIndicator != null) this.vanishIndicator.removePlayer(player);
            }
        }
    }
}
