package su.nightexpress.sunlight.module.homes;

import org.bukkit.Tag;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.bridge.item.AdaptedItem;
import su.nightexpress.nightcore.integration.item.impl.AdaptedVanillaStack;
import su.nightexpress.nightcore.util.BukkitThing;

import java.util.HashMap;
import java.util.Map;

public class HomeDefaults {

    public static final String DEFAULT_HOME_ID = "default";

    public static final String DEFAULT_LIST_ALIAS     = "homelist";
    public static final String DEFAULT_TELEPORT_ALIAS = "home";

    @NotNull
    public static Map<String, AdaptedItem> getDefaultIconPresets() {
        Map<String, AdaptedItem> map = new HashMap<>();

        Tag.BEDS.getValues().forEach(material -> {
            map.put(BukkitThing.getValue(material), AdaptedVanillaStack.of(new ItemStack(material)));
        });

        return map;
    }
}
