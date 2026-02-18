package su.nightexpress.sunlight.module.playerwarps.category;

import org.jspecify.annotations.NonNull;
import su.nightexpress.sunlight.module.playerwarps.PlayerWarp;

public interface WarpCategory {

    @NonNull String name();

    boolean isWarpOfThis(@NonNull PlayerWarp warp);
}
