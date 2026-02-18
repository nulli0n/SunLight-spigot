package su.nightexpress.sunlight.module.kits.model;

import org.bukkit.Material;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.configuration.ConfigProperty;
import su.nightexpress.nightcore.configuration.ConfigType;
import su.nightexpress.nightcore.configuration.ConfigTypes;
import su.nightexpress.nightcore.util.bukkit.NightItem;

import java.util.Collections;
import java.util.List;

public class KitSchema {

    private static final ConfigType<KitContent> KIT_CONTENT_CONFIG_TYPE = ConfigType.of(
        KitContent::read,
        FileConfig::set
    );

    public static final ConfigProperty<String> NAME = ConfigProperty.of(ConfigTypes.STRING, "Name", "Unnamed");

    public static final ConfigProperty<List<String>> DESCRIPTION = ConfigProperty.of(ConfigTypes.STRING_LIST, "Description", Collections.emptyList());

    public static final ConfigProperty<Boolean> PERMISSION_REQUIRED = ConfigProperty.of(ConfigTypes.BOOLEAN, "Permission_Required", false);

    public static final ConfigProperty<Integer> COOLDOWN = ConfigProperty.of(ConfigTypes.INT, "Cooldown", 0);

    public static final ConfigProperty<Double> COST = ConfigProperty.of(ConfigTypes.DOUBLE, "Cost.Money", 0D);

    public static final ConfigProperty<Integer> PRIORITY = ConfigProperty.of(ConfigTypes.INT, "Priority", 0);

    public static final ConfigProperty<NightItem> ICON = ConfigProperty.of(ConfigTypes.NIGHT_ITEM, "Icon", NightItem.fromType(Material.DIAMOND_SWORD));

    public static final ConfigProperty<List<String>> COMMANDS = ConfigProperty.of(ConfigTypes.STRING_LIST, "Commands", Collections.emptyList());

    public static final ConfigProperty<KitContent> CONTENT = ConfigProperty.of(KIT_CONTENT_CONFIG_TYPE, "Content", KitContent.empty());
}
