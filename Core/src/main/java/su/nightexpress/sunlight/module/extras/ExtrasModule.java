package su.nightexpress.sunlight.module.extras;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.module.Module;
import su.nightexpress.sunlight.module.ModuleInfo;
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

    public ExtrasModule(@NotNull SunLightPlugin plugin, @NotNull String id) {
        super(plugin, id);
    }

    @Override
    protected void gatherInfo(@NotNull ModuleInfo moduleInfo) {
        moduleInfo.setConfigClass(ExtrasConfig.class);
        moduleInfo.setLangClass(ExtrasLang.class);
        moduleInfo.setPermissionsClass(ExtrasPerms.class);
    }

    @Override
    protected void onModuleLoad() {
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
    protected void onModuleUnload() {
        if (this.chairsManager != null) this.chairsManager.shutdown();
        if (this.sortManager != null) this.sortManager.shutdown();
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
