package su.nightexpress.sunlight.module.warps.config;

import su.nightexpress.nightcore.config.ConfigValue;
import su.nightexpress.nightcore.util.RankMap;

import java.util.Map;
import java.util.Set;

public class WarpsConfig {

    public static final ConfigValue<Set<String>> WARP_SET_WORLD_BLACKLIST = ConfigValue.create("Warp.Creation.World_Blacklist",
        Set.of("special_world", "other_world_123"),
        "A list of worlds, where players can't create warps.",
        "This setting can be bypassed with the '" + WarpsPerms.BYPASS_CREATION_WORLD + "' permission.");

    public static final ConfigValue<RankMap<Integer>> WARP_SET_AMOUNT_PER_GROUP = ConfigValue.create("Warp.Creation.Amount_Per_Rank",
        (cfg, path, def) -> RankMap.readInt(cfg, path, 0),
        (cfg, path, map) -> map.write(cfg, path),
        () -> new RankMap<>(RankMap.Mode.RANK, "warps.amount.", 0, Map.of("moderator", 3, "admin", -1)),
        "Amount of possible warps to create for certain permission groups.",
        "If player is in multiple groups listed here, the greater value will be used.",
        "If player is not in any group listed here, the 'default' value will be used if present.",
        "-*-",
        "If you want to use this based on player's permissions rather than groups, you can do so by",
        "giving '" + WarpsPerms.PREFIX_AMOUNT + "[name]' permission, where '[name]' is name from this list.",
        "Make sure to use names that are different to your permission groups. Otherwise they will be overriden.",
        "-*-",
        "Use '-1' for unlimited warps amount.",
        "You must have Vault installed for this feature to work. No extra permissions required."
    );

    public static final ConfigValue<Integer> WARP_DESCRIPTION_MAX_SIZE = ConfigValue.create("Warp.Description.MaxSize",
        40,
        "Sets the maximal text length for a warp description.",
        "This setting can be bypassed with the '" + WarpsPerms.BYPASS_DESCRIPTION_SIZE.getName() + "' permission.");
}
