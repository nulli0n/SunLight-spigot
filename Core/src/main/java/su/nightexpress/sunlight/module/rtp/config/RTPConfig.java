package su.nightexpress.sunlight.module.rtp.config;

import org.bukkit.block.BlockFace;
import su.nexmedia.engine.Version;
import su.nexmedia.engine.api.config.JOption;
import su.nightexpress.sunlight.Placeholders;
import su.nightexpress.sunlight.module.rtp.impl.RangeInfo;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class RTPConfig {

    public static final JOption<String> DEFAULT_WORLD = JOption.create("Default_World", "world",
        "Sets the default world if RTP is not available in player's world.");

    public static final JOption<Boolean> FORCE_TO_DEFAULT = JOption.create("Force_To_Default", true,
        "Sets whether or not RTP will search location in Default World, if no ranges are available in the current one.");

    public static final JOption<Integer> LOCATION_SEARCH_ATTEMPTS = JOption.create("LocationSearch.Attempts", 5,
        "Amount of RTP attempts to get a valid teleport location.");

    public static final JOption<Boolean> LOCATION_SEARCH_GENERATED_CHUNKS_ONLY = JOption.create("LocationSearch.Generated_Chunks_Only", true,
        "Sets whether or not only chunks that have already been generated will be used when searching for a teleport location.",
        "NOTE: Works only for " + Version.V1_19_R3.getLocalized() + " or above!");

    public static final JOption<Boolean> LOCATION_SEARCH_LOADED_CHUNKS_ONLY = JOption.create("LocationSearch.Loaded_Chunks_Only", false,
        "Sets whether or not only chunks that have been loaded by players will be used when searching for a teleport location.",
        "NOTE: Settings this on 'true' may result in a lot of failures when server lacks online players.");

    public static final JOption<Map<String, Map<String, RangeInfo>>> LOCATION_SEARCH_RANGES = new JOption<>("LocationSearch.Ranges",
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
        Map.of("world", Map.of(
            Placeholders.DEFAULT, new RangeInfo(0, 0, 500, 2500, Set.of(BlockFace.NORTH, BlockFace.SOUTH, BlockFace.WEST, BlockFace.EAST))
            )
        ),
        "List of per-world range values to search location in.",
        "You can create as many entries per world as you want.",
        "Put here your ACTUAL worlds, especially if your main world is named differently than default 'world'."
    ).setWriter((cfg, path, map) -> map.forEach((world, infoMap) -> infoMap.forEach((id, range) -> range.write(cfg, path + "." + world + "." + id))));
}
