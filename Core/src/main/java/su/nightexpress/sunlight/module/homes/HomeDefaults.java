package su.nightexpress.sunlight.module.homes;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Tag;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import su.nightexpress.nightcore.bridge.item.AdaptedItem;
import su.nightexpress.nightcore.integration.item.ItemBridge;
import su.nightexpress.nightcore.util.BukkitThing;

public class HomeDefaults {

    public static final String DEFAULT_HOME_ID = "default";

    public static final String DEFAULT_LIST_ALIAS     = "homelist";
    public static final String DEFAULT_TELEPORT_ALIAS = "home";

    @NotNull
    public static Map<String, AdaptedItem> getDefaultIconPresets() {
        Map<String, AdaptedItem> map = new HashMap<>();

        Tag.BEDS.getValues().forEach(material -> {
            map.put(BukkitThing.getValue(material), ItemBridge.get().adaptOrDummy(new ItemStack(material)));
        });

        return map;
    }
}
