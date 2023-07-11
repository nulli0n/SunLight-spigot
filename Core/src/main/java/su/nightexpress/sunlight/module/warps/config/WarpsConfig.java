package su.nightexpress.sunlight.module.warps.config;

import su.nexmedia.engine.api.config.JOption;
import su.nexmedia.engine.utils.PlayerRankMap;
import su.nightexpress.sunlight.module.warps.util.WarpsPerms;

import java.util.Map;
import java.util.Set;

public class WarpsConfig {

    public static final JOption<Set<String>> WARP_SET_WORLD_BLACKLIST = JOption.create("Warp.Creation.World_Blacklist",
        Set.of("special_world", "other_world_123"),
        "A list of worlds, where players can't create warps.",
        "This setting can be bypassed with the '" + WarpsPerms.BYPASS_CREATION_WORLD + "' permission.");

    public static final JOption<PlayerRankMap<Integer>> WARP_SET_AMOUNT_PER_GROUP = new JOption<>("Warp.Creation.Amount_Per_Rank",
        (cfg, path, def) -> PlayerRankMap.read(cfg, path, Integer.class),
        new PlayerRankMap<>(Map.of("moderator", 3, "admin", -1)),
        "Sets how many warps players with certain permission groups can set.",
        "Use '-1' for unlimited warps amount.",
        "You must have Vault installed for this feature to work. No extra permissions required."
    ).mapReader(map -> map.setNegativeBetter(true)).setWriter((cfg, path, map) -> map.write(cfg, path));

    public static final JOption<Integer> WARP_DESCRIPTION_MAX_SIZE = JOption.create("Warp.Description.MaxSize",
        40,
        "Sets the maximal text length for a warp description.",
        "This setting can be bypassed with the '" + WarpsPerms.BYPASS_DESCRIPTION_SIZE.getName() + "' permission.");
}
