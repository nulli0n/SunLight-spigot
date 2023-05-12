package su.nightexpress.sunlight.module.homes.config;

import su.nexmedia.engine.api.config.JOption;
import su.nightexpress.sunlight.module.homes.util.Placeholders;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class HomesConfig {

    public static final JOption<Integer> DATA_CLEANUP_INTERVAL = JOption.create("Data.Cleanup_Interval", 900,
        "Sets how often (in seconds) homes loaded for offline users will be removed from the memory.");

    public static final JOption<Boolean> CHECK_BUILD_ACCESS = JOption.create("Creation.Check_Build_Access", true,
        "When 'true', calls a BlockCanBuildEvent to check if player is out of other claims/protection zones.");
    public static final JOption<Set<String>> WORLD_BLACKLIST = JOption.create("Creation.World_Blacklist", Set.of("world_name", "another_world"),
        "A list of worlds, where homes can not be created.");
    public static final JOption<Set<String>> REGION_BLACKLIST = JOption.create("Creation.Region_Blacklist", Set.of("pvp_arena"),
        "A list of WorldGuard regions, where homes can not be created.");

    public static final JOption<Map<String, Integer>> HOMES_PER_RANK = new JOption<Map<String, Integer>>("Creation.Amount_Per_Rank",
        (cfg, path, def) -> cfg.getSection(path).stream().collect(Collectors.toMap(String::toLowerCase, v -> cfg.getInt(path + "." + v))),
        () -> Map.of(Placeholders.DEFAULT, 1, "premium", 3, "admin", -1),
        "Amount of possible homes to create for certain permission groups.",
        "If player has multiple groups, the best value will be used.",
        "You must have Vault and permissions plugin installed for this feature to work.",
        "Use -1 for unlimited amount.")
        .setWriter((cfg, path, map) -> map.forEach((rank, amount) -> cfg.set(path + "." + rank, amount)));
}
