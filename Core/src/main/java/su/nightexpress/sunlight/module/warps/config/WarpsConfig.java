package su.nightexpress.sunlight.module.warps.config;

import su.nexmedia.engine.api.config.JOption;
import su.nightexpress.sunlight.module.warps.util.WarpsPerms;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class WarpsConfig {

    public static final JOption<Set<String>> WARP_SET_WORLD_BLACKLIST = JOption.create("Warp.Creation.World_Blacklist",
        Set.of("special_world", "other_world_123"),
        "A list of worlds, where players can't create warps.",
        "This setting can be bypassed with the '" + WarpsPerms.BYPASS_CREATION_WORLD + "' permission.");

    public static final JOption<Map<String, Integer>> WARP_SET_AMOUNT_PER_GROUP = new JOption<>("Warp.Creation.Amount_Per_Rank",
        (cfg, path, def) -> cfg.getSection(path).stream().collect(Collectors.toMap(String::toLowerCase, v -> cfg.getInt(path + "." + v))),
        Map.of("moderator", 3, "admin", -1),
        "Sets how many warps players with certain permission groups can set.",
        "Use '-1' for unlimited warps amount.",
        "You must have Vault installed for this feature to work. No extra permissions required.");

    public static final JOption<Integer> WARP_DESCRIPTION_MAX_SIZE = JOption.create("Warp.Description.MaxSize",
        40,
        "Sets the maximal text length for a warp description.",
        "This setting can be bypassed with the '" + WarpsPerms.BYPASS_DESCRIPTION_SIZE.getName() + "' permission.");
}
