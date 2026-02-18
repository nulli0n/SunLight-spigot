package su.nightexpress.sunlight.module.warps;

import su.nightexpress.nightcore.language.LangAssets;
import su.nightexpress.nightcore.util.NumberUtil;
import su.nightexpress.nightcore.util.placeholder.TypedPlaceholder;

public class WarpsPlaceholders {

    public static final String WARP_ID          = "%warp_id%";
    public static final String WARP_NAME        = "%warp_name%";
    public static final String WARP_DESCRIPTION = "%warp_description%";
    public static final String WARP_WORLD       = "%warp_location_world%";
    public static final String WARP_LOCATION_X  = "%warp_location_x%";
    public static final String WARP_LOCATION_Y  = "%warp_location_y%";
    public static final String WARP_LOCATION_Z  = "%warp_location_z%";

    public static final TypedPlaceholder<Warp> WARP = TypedPlaceholder.builder(Warp.class)
        .with(WARP_ID, Warp::getId)
        .with(WARP_NAME, Warp::getName)
        .with(WARP_DESCRIPTION, warp -> String.join("\n", warp.getDescription()))
        .with(WARP_LOCATION_X, warp -> NumberUtil.format(warp.getBlockPos().getX()))
        .with(WARP_LOCATION_Y, warp -> NumberUtil.format(warp.getBlockPos().getY()))
        .with(WARP_LOCATION_Z, warp -> NumberUtil.format(warp.getBlockPos().getZ()))
        .with(WARP_WORLD, warp -> warp.isActive() ? LangAssets.get(warp.getWorld()) : warp.getWorldName())
        .build();
}
