package su.nightexpress.sunlight.module.vanish;

import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.core.config.CoreLang;
import su.nightexpress.nightcore.util.Players;
import su.nightexpress.nightcore.util.text.NightMessage;
import su.nightexpress.sunlight.api.provider.VanishProvider;
import su.nightexpress.sunlight.config.PermissionTree;
import su.nightexpress.sunlight.hook.placeholder.PlaceholderRegistry;
import su.nightexpress.sunlight.module.Module;
import su.nightexpress.sunlight.module.ModuleContext;
import su.nightexpress.sunlight.module.vanish.command.VanishCommand;
import su.nightexpress.sunlight.module.vanish.config.VanishConfig;
import su.nightexpress.sunlight.module.vanish.config.VanishLang;
import su.nightexpress.sunlight.module.vanish.config.VanishPerms;
import su.nightexpress.sunlight.user.SunUser;
import su.nightexpress.sunlight.user.property.UserProperty;
import su.nightexpress.sunlight.user.property.UserPropertyRegistry;

public class VanishModule extends Module implements VanishProvider {

    public static final UserProperty<Boolean> VANISH = UserProperty.create("vanish", Boolean.class, false, true);

    private BossBar vanishIndicator;

    public VanishModule(@NotNull ModuleContext context) {
        super(context);
    }

    @Override
    protected void loadModule(@NotNull FileConfig config) {
        config.initializeOptions(VanishConfig.class);
        this.plugin.injectLang(VanishLang.class);
        UserPropertyRegistry.register(VANISH);

        this.addListener(new VanishListener(this.plugin, this));

        if (VanishConfig.BAR_INDICATOR_ENABLED.get()) {
            String title = VanishConfig.BAR_INDICATOR_VANISHED_TITLE.get();
            BarColor color = VanishConfig.BAR_INDICATOR_VANISHED_COLOR.get();
            BarStyle style = VanishConfig.BAR_INDICATOR_VANISHED_STYLE.get();

            this.vanishIndicator = this.plugin.getServer().createBossBar(NightMessage.asLegacy(title), color, style);
        }

        this.plugin.runTask(this::updateOnlinePlayers);
    }

    @Override
    protected void unloadModule() {
        Players.getOnline().forEach(player -> this.vanish(player, false));

        this.vanishIndicator.removeAll();
        this.vanishIndicator = null;
    }

    @Override
    protected void registerPermissions(@NotNull PermissionTree root) {
        root.merge(VanishPerms.MODULE);
    }

    @Override
    protected void registerCommands() {
        this.commandRegistry.addProvider("vanish", new VanishCommand(this.plugin, this, this.userManager));
    }

    @Override
    public void registerPlaceholders(@NotNull PlaceholderRegistry registry) {
        registry.register("vanish_state", (player, payload) -> {
            return CoreLang.STATE_YES_NO.get(this.userManager.getOrFetch(player).getPropertyOrDefault(VANISH));
        });
    }

    private void updateOnlinePlayers() {
        Players.getOnline().forEach(player -> {
            if (!this.isVanished(player)) return;

            this.vanish(player, true);
        });
    }

    @Override
    public boolean isVanished(@NotNull Player player) {
        SunUser user = this.plugin.getUserManager().getOrFetch(player);
        return user.getPropertyOrDefault(VANISH);
    }

    public void vanish(@NotNull Player player, boolean isVanished) {
        // TODO Add meta "vanished" configurable
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
