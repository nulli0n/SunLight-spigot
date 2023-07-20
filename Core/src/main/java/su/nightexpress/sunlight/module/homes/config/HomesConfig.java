package su.nightexpress.sunlight.module.homes.config;

import su.nexmedia.engine.api.config.JOption;
import su.nexmedia.engine.utils.PlayerRankMap;
import su.nightexpress.sunlight.module.homes.util.Placeholders;
import su.nightexpress.sunlight.module.warps.config.WarpsPerms;

import java.util.Map;
import java.util.Set;

public class HomesConfig {

    public static final JOption<Integer> DATA_CLEANUP_INTERVAL = JOption.create("Data.Cleanup_Interval", 900,
        "Sets how often (in seconds) homes loaded for offline users will be removed from the memory.");

    public static final JOption<Boolean> CHECK_BUILD_ACCESS = JOption.create("Creation.Check_Build_Access", true,
        "When 'true', calls a BlockCanBuildEvent to check if player is out of other claims/protection zones.");
    public static final JOption<Set<String>> WORLD_BLACKLIST = JOption.create("Creation.World_Blacklist", Set.of("world_name", "another_world"),
        "A list of worlds, where homes can not be created.");
    public static final JOption<Set<String>> REGION_BLACKLIST = JOption.create("Creation.Region_Blacklist", Set.of("pvp_arena"),
        "A list of WorldGuard regions, where homes can not be created.");

    public static final JOption<PlayerRankMap<Integer>> HOMES_PER_RANK = new JOption<PlayerRankMap<Integer>>("Creation.Amount_Per_Rank",
        (cfg, path, def) -> PlayerRankMap.read(cfg, path, Integer.class),
        () -> new PlayerRankMap<>(Map.of(Placeholders.DEFAULT, 1, "premium", 3, "admin", -1)),
        "Amount of possible homes to create for certain permission groups.",
        "If player is in multiple groups listed here, the greater value will be used.",
        "If player is not in any group listed here, the 'default' value will be used if present.",
        "-*-",
        "If you want to use this based on player's permissions rather than groups, you can do so by",
        "giving '" + HomesPerms.PREFIX_AMOUNT + "[name]' permission, where '[name]' is name from this list.",
        "Make sure to use names that are different to your permission groups. Otherwise they will be overriden.",
        "-*-",
        "You must have Vault and permissions plugin installed for this feature to work.",
        "Use '-1' for unlimited amount."
    ).mapReader(map -> map.setNegativeBetter(true).setCheckAsPermission(WarpsPerms.PREFIX_AMOUNT)).setWriter((cfg, path, map) -> map.write(cfg, path));
}
