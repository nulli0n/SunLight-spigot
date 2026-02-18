package su.nightexpress.sunlight.module.playerwarps.category;

import org.jspecify.annotations.NonNull;
import su.nightexpress.sunlight.module.playerwarps.PlayerWarp;
import su.nightexpress.sunlight.module.playerwarps.core.PlayerWarpsLang;

public class AllCategory implements WarpCategory {

    @Override
    @NonNull
    public String name() {
        return PlayerWarpsLang.CATEGORY_ALL_NAME.text();
    }

    @Override
    public boolean isWarpOfThis(@NonNull PlayerWarp warp) {
        return true;
    }
}
