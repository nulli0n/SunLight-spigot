package su.nightexpress.sunlight.module.warps.type;

import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.utils.Colorizer;
import su.nightexpress.sunlight.module.warps.impl.Warp;

import java.util.Comparator;

public enum WarpSortType {

    WARP_NAME((warp1, warp2) -> {
        return Colorizer.strip(warp1.getName()).compareTo(Colorizer.strip(warp2.getName()));
    }),
    WARP_ID((warp1, warp2) -> {
        return warp1.getId().compareTo(warp2.getId());
    }),
    WARP_TYPE((warp1, warp2) -> {
        return warp1.getType().ordinal() - warp2.getType().ordinal();
    }),
    VISIT_COST(Comparator.comparingDouble(Warp::getVisitCostMoney))
    ;

    private final Comparator<? super Warp> comparator;

    WarpSortType(@NotNull Comparator<? super Warp> comparator) {
        this.comparator = comparator;
    }

    @NotNull
    public Comparator<? super Warp> getComparator() {
        return comparator;
    }
}
