package su.nightexpress.sunlight.module.homes.config;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.config.ConfigValue;
import su.nightexpress.nightcore.util.*;
import su.nightexpress.sunlight.module.homes.util.Placeholders;

import java.util.Map;
import java.util.Set;

public class HomesConfig {

    public static final ConfigValue<Integer> DATA_SAVE_INTERVAL = ConfigValue.create("Data.Save_Interval",
        30,
        "Sets how often (in seconds) loaded homes will save their changes to the database.");

    public static final ConfigValue<Integer> DATA_CLEANUP_INTERVAL = ConfigValue.create("Data.Cleanup_Interval",
        900,
        "Sets how often (in seconds) homes loaded for offline users will be removed from the memory.");



    public static final ConfigValue<ItemStack> DEFAULT_ICON = ConfigValue.create("Home.Defaut_Icon",
        ItemUtil.getSkinHead("cf7cdeefc6d37fecab676c584bf620832aaac85375e9fcbff27372492d69f"),
        "Default home icon used for new homes.",
        "Item Options: " + Placeholders.WIKI_ITEMS_URL
    );


    public static final ConfigValue<Boolean> BED_MODE_ENABLED = ConfigValue.create("Creation.BedMode.Enabled",
        true,
        "Sets whether or not players can use beds to set home locations."
    );

    public static final ConfigValue<Boolean> BED_MODE_OVERRIDE_RESPAWN = ConfigValue.create("Creation.BedMode.OverrideRespawn",
        false,
        "When enabled, overrides vanilla bed respawn point mechanic.",
        "Respawn point is set to the latest created/updated bed home."
    );

    public static final ConfigValue<Boolean> BED_MODE_COLORS = ConfigValue.create("Creation.BedMode.Colors",
        false,
        "When enabled, each bed color will set different home point."
    );


    public static final ConfigValue<Boolean> CHECK_BUILD_ACCESS = ConfigValue.create("Creation.Check_Build_Access",
        true,
        "When enabled, simulates player block place event to check for build access.",
        "If building is not allowed, home can't be created."
    );

    public static final ConfigValue<Set<String>> WORLD_BLACKLIST = ConfigValue.create("Creation.World_Blacklist",
        Lists.newSet("world_name", "another_world"),
        "A list of worlds, where homes can not be created."
    );

    public static final ConfigValue<RankMap<Integer>> HOMES_PER_RANK = ConfigValue.create("Creation.Amount_Per_Rank",
        (cfg, path, def) -> RankMap.readInt(cfg, path, 1),
        (cfg, path, map) -> map.write(cfg, path),
        () -> new RankMap<>(
            RankMap.Mode.RANK,
            HomesPerms.PREFIX_AMOUNT,
            1,
            Map.of("premium", 3, "admin", -1)
        ),
        "Sets how much homes a player can create depends on their rank/permissions.",
        "",
        "If player is in multiple groups listed here, the greater value will be used.",
        "If player is not in any group listed here, the 'default' value will be used if present.",
        "",
        "You must have "  + Plugins.VAULT + " and permissions plugins installed for this feature to work.",
        "Use '-1' for unlimited amount."
    );

    @NotNull
    public static ItemStack getDefaultIcon() {
        return new ItemStack(DEFAULT_ICON.get());
    }
}
