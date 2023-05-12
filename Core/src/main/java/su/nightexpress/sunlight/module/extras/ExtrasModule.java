package su.nightexpress.sunlight.module.extras;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.sunlight.SunLight;
import su.nightexpress.sunlight.module.Module;
import su.nightexpress.sunlight.module.extras.config.ExtrasConfig;
import su.nightexpress.sunlight.module.extras.config.ExtrasLang;
import su.nightexpress.sunlight.module.extras.config.ExtrasPerms;
import su.nightexpress.sunlight.module.extras.impl.chairs.ChairsManager;
import su.nightexpress.sunlight.module.extras.impl.chestsort.SortManager;
import su.nightexpress.sunlight.module.extras.listener.ExtrasGenericListener;
import su.nightexpress.sunlight.module.extras.listener.PhysicsExplosionListener;

public class ExtrasModule extends Module {

    private ChairsManager  chairsManager;
    private SortManager    sortManager;

    public ExtrasModule(@NotNull SunLight plugin, @NotNull String id) {
        super(plugin, id);
    }

    @Override
    public void onLoad() {
        this.plugin.registerPermissions(ExtrasPerms.class);
        this.plugin.getLangManager().loadMissing(ExtrasLang.class);
        this.plugin.getLang().saveChanges();
        this.getConfig().initializeOptions(ExtrasConfig.class);

        if (ExtrasConfig.CHAIRS_ENABLED.get()) {
            this.chairsManager = new ChairsManager(this);
            this.chairsManager.setup();
        }
        if (ExtrasConfig.CHEST_SORT_ENABLED.get()) {
            this.sortManager = new SortManager(this);
            this.sortManager.setup();
        }
        if (ExtrasConfig.PHYSIC_EXPLOSIONS_ENABLED.get()) {
            this.addListener(new PhysicsExplosionListener(plugin));
        }
        this.addListener(new ExtrasGenericListener(this));
    }

    @Override
    public void onShutdown() {
        if (this.chairsManager != null) {
            this.chairsManager.shutdown();
            this.chairsManager = null;
        }
        if (this.sortManager != null) {
            this.sortManager.shutdown();
            this.sortManager = null;
        }
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
