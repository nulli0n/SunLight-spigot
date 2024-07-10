package su.nightexpress.sunlight.module.rtp.config;

import org.bukkit.block.BlockFace;
import su.nightexpress.nightcore.config.ConfigValue;
import su.nightexpress.nightcore.util.Version;
import su.nightexpress.sunlight.Placeholders;
import su.nightexpress.sunlight.module.rtp.impl.RangeInfo;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class RTPConfig {

    public static final ConfigValue<String> DEFAULT_WORLD = ConfigValue.create("Default_World",
        "world",
        "Sets the default world if RTP is not available in player's world.");

    public static final ConfigValue<Boolean> FORCE_TO_DEFAULT = ConfigValue.create("Force_To_Default",
        true,
        "Sets whether or not RTP will search location in Default World, if no ranges are available in the current one.");

    public static final ConfigValue<Integer> LOCATION_SEARCH_ATTEMPTS = ConfigValue.create("LocationSearch.Attempts",
        5,
        "Amount of RTP attempts to get a valid teleport location.");

    public static final ConfigValue<Boolean> LOCATION_SEARCH_GENERATED_CHUNKS_ONLY = ConfigValue.create("LocationSearch.Generated_Chunks_Only",
        true,
        "Sets whether or not only chunks that have already been generated will be used when searching for a teleport location.",
        "NOTE: Works only for " + Version.V1_19_R3.getLocalized() + " or above!");

    public static final ConfigValue<Boolean> LOCATION_SEARCH_LOADED_CHUNKS_ONLY = ConfigValue.create("LocationSearch.Loaded_Chunks_Only",
        false,
        "Sets whether or not only chunks that have been loaded by players will be used when searching for a teleport location.",
        "NOTE: Settings this on 'true' may result in a lot of failures when server lacks online players.");

    public static final ConfigValue<Map<String, Map<String, RangeInfo>>> LOCATION_SEARCH_RANGES = ConfigValue.create("LocationSearch.Ranges",
        (cfg, path, def) -> {
            Map<String, Map<String, RangeInfo>> map = new HashMap<>();
            for (String world : cfg.getSection(path)) {
                for (String id : cfg.getSection(path + "." + world)) {
                    RangeInfo rangeInfo = RangeInfo.read(cfg, path + "." + world + "." + id);
                    map.computeIfAbsent(world, k -> new HashMap<>()).put(id.toLowerCase(), rangeInfo);
                }
            }
            return map;
        },
        (cfg, path, map) -> map.forEach((world, infoMap) -> infoMap.forEach((id, range) -> range.write(cfg, path + "." + world + "." + id))),
        () -> Map.of("world", Map.of(
            Placeholders.DEFAULT, new RangeInfo(0, 0, 500, 2500, Set.of(BlockFace.NORTH, BlockFace.SOUTH, BlockFace.WEST, BlockFace.EAST))
            )
        ),
        "List of per-world range values to search location in.",
        "You can create as many entries per world as you want.",
        "Put here your ACTUAL worlds, especially if your main world is named differently than default 'world'."
    );
}
