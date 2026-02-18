package su.nightexpress.sunlight.module.extras;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.sunlight.config.PermissionTree;
import su.nightexpress.sunlight.hook.placeholder.PlaceholderRegistry;
import su.nightexpress.sunlight.module.Module;
import su.nightexpress.sunlight.module.ModuleContext;
import su.nightexpress.sunlight.module.extras.chairs.ChairsManager;
import su.nightexpress.sunlight.module.extras.chestsort.SortManager;
import su.nightexpress.sunlight.module.extras.config.ExtrasConfig;
import su.nightexpress.sunlight.module.extras.config.ExtrasLang;
import su.nightexpress.sunlight.module.extras.config.ExtrasPerms;
import su.nightexpress.sunlight.module.extras.listener.ExtrasGenericListener;
import su.nightexpress.sunlight.module.extras.listener.PhysicsExplosionListener;

public class ExtrasModule extends Module {

    private ChairsManager  chairsManager;
    private SortManager    sortManager;

    public ExtrasModule(@NotNull ModuleContext context) {
        super(context);
    }

    @Override
    protected void loadModule(@NotNull FileConfig config) {
        config.initializeOptions(ExtrasConfig.class);
        this.plugin.injectLang(ExtrasLang.class);

        if (ExtrasConfig.CHAIRS_ENABLED.get()) {
            this.chairsManager = new ChairsManager(this.plugin, this);
            this.chairsManager.setup();
        }
        if (ExtrasConfig.CHEST_SORT_ENABLED.get()) {
            this.sortManager = new SortManager(this.plugin, this);
            this.sortManager.setup();
        }
        if (ExtrasConfig.PHYSIC_EXPLOSIONS_ENABLED.get()) {
            this.addListener(new PhysicsExplosionListener(this.plugin));
        }
        this.addListener(new ExtrasGenericListener(this.plugin, this));
    }

    @Override
    protected void unloadModule() {
        if (this.chairsManager != null) this.chairsManager.shutdown();
        if (this.sortManager != null) this.sortManager.shutdown();
    }

    @Override
    protected void registerPermissions(@NotNull PermissionTree root) {
        root.merge(ExtrasPerms.MODULE);
    }

    @Override
    protected void registerCommands() {

    }

    @Override
    public void registerPlaceholders(@NotNull PlaceholderRegistry registry) {
        // TODO
        /*if (params.equalsIgnoreCase("chairs_state")) {
            return CoreLang.getYesOrNo(user.getSettings().get(ChairsManager.SETTING_CHAIRS));
        }
        if (params.equalsIgnoreCase("chestsort_state")) {
            return CoreLang.getYesOrNo(user.getSettings().get(SortManager.SETTING_CHEST_SORT));
        }*/
    }

    @Nullable
    public ChairsManager getChairsManager() {
        return chairsManager;
    }

    @Nullable
    public SortManager getChestSortManager() {
        return sortManager;
    }
}
