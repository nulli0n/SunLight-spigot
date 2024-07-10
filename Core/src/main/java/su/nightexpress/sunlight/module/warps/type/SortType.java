package su.nightexpress.sunlight.module.warps.type;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.manager.AbstractFileData;
import su.nightexpress.nightcore.util.Colorizer;
import su.nightexpress.sunlight.module.warps.impl.Warp;

import java.util.Comparator;

public enum SortType {

    WARP_NAME(Comparator.comparing(warp -> Colorizer.strip(warp.getName()))),
    WARP_ID(Comparator.comparing(AbstractFileData::getId)),
    WARP_TYPE(Comparator.comparingInt(warp -> warp.getType().ordinal())),
    VISIT_COST(Comparator.comparingDouble(Warp::getVisitCostMoney))
    ;

    private final Comparator<? super Warp> comparator;

    SortType(@NotNull Comparator<? super Warp> comparator) {
        this.comparator = comparator;
    }

    @NotNull
    public Comparator<? super Warp> getComparator() {
        return comparator;
    }
}
