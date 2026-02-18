package su.nightexpress.sunlight.module.rtp;

import org.bukkit.block.BlockFace;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.sunlight.module.rtp.model.LookupRange;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class RTPDefaults {

    private static final Set<BlockFace> DIRECTIONS = Set.of(BlockFace.NORTH, BlockFace.EAST, BlockFace.WEST, BlockFace.SOUTH);

    @NotNull
    public static Map<String, LookupRange> getDefaultRangeMap() {
        Map<String, LookupRange> map = new LinkedHashMap<>();

        map.put("world", new LookupRange(0, 0, 1000, 10_000, DIRECTIONS));
        map.put("world_nether", new LookupRange(0, 0, 1000, 5000, DIRECTIONS));
        map.put("world_the_end", new LookupRange(0, 0, 1000, 5000, DIRECTIONS));

        return map;
    }
}
