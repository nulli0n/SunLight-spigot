package su.nightexpress.sunlight.module.warps.core;

import org.bukkit.Material;
import org.jspecify.annotations.NonNull;
import su.nightexpress.nightcore.configuration.AbstractConfig;
import su.nightexpress.nightcore.configuration.ConfigProperty;
import su.nightexpress.nightcore.configuration.ConfigTypes;
import su.nightexpress.nightcore.util.bukkit.NightItem;

public class WarpsSettings extends AbstractConfig {

    protected final ConfigProperty<Integer> warpSaveInterval = this.addProperty(ConfigTypes.INT, "Warps.Save-Interval", 60);

    protected final ConfigProperty<NightItem> warpDefaultIcon = this.addProperty(ConfigTypes.NIGHT_ITEM, "Warps.Default-Icon",
        NightItem.fromType(Material.ENDER_PEARL)
    );

    public int getSaveInterval() {
        return this.warpSaveInterval.get();
    }

    @NonNull
    public NightItem getDefaultIcon() {
        return this.warpDefaultIcon.get().copy();
    }
}
