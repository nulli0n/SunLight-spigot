package su.nightexpress.sunlight.module.nerfphantoms;

import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.core.config.CoreLang;
import su.nightexpress.sunlight.config.PermissionTree;
import su.nightexpress.sunlight.hook.placeholder.PlaceholderRegistry;
import su.nightexpress.sunlight.module.Module;
import su.nightexpress.sunlight.module.ModuleContext;
import su.nightexpress.sunlight.module.nerfphantoms.command.PhantomsCommandProvider;
import su.nightexpress.sunlight.module.nerfphantoms.config.PhantomsConfig;
import su.nightexpress.sunlight.module.nerfphantoms.config.PhantomsLang;
import su.nightexpress.sunlight.module.nerfphantoms.config.PhantomsPerms;
import su.nightexpress.sunlight.module.nerfphantoms.listener.PhantomsListener;
import su.nightexpress.sunlight.user.SunUser;
import su.nightexpress.sunlight.user.property.UserPropertyRegistry;

public class PhantomsModule extends Module {

    public PhantomsModule(@NotNull ModuleContext context) {
        super(context);
    }

    @Override
    protected void loadModule(@NotNull FileConfig config) {
        config.initializeOptions(PhantomsConfig.class);
        this.plugin.injectLang(PhantomsLang.class);
        UserPropertyRegistry.register(PhantomsProperties.ANTI_PHANTOM);

        this.addListener(new PhantomsListener(this.plugin, this));
        this.addAsyncTask(this::resetRestTime, 600); // TODO Config
    }

    @Override
    protected void unloadModule() {

    }

    @Override
    protected void registerPermissions(@NotNull PermissionTree root) {
        root.merge(PhantomsPerms.ROOT);
    }

    protected void registerCommands() {
        this.commandRegistry.addProvider("nophantom", new PhantomsCommandProvider(this.plugin, this, this.userManager));
    }

    @Override
    public void registerPlaceholders(@NotNull PlaceholderRegistry registry) {
        registry.register("phantoms_antiphantom_state", (player, payload) -> {
            return CoreLang.STATE_YES_NO.get(this.userManager.getOrFetch(player).getPropertyOrDefault(
                PhantomsProperties.ANTI_PHANTOM));
        });
    }

    private void resetRestTime() {
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            SunUser user = plugin.getUserManager().getOrFetch(player);
            if (!user.getPropertyOrDefault(PhantomsProperties.ANTI_PHANTOM)) continue;

            player.setStatistic(Statistic.TIME_SINCE_REST, 0);
        }
    }
}
