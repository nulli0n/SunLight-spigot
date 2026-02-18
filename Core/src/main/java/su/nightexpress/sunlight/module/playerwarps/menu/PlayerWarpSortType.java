package su.nightexpress.sunlight.module.playerwarps.menu;

import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;
import su.nightexpress.nightcore.util.Lists;
import su.nightexpress.sunlight.module.playerwarps.PlayerWarp;
import su.nightexpress.sunlight.module.playerwarps.core.PlayerWarpsLang;

import java.util.Comparator;

public enum PlayerWarpSortType {

    NAME(Comparator.comparing(PlayerWarp::getName)),
    DATE_CREATION(Comparator.comparingLong(PlayerWarp::getCreationTimestamp).reversed()),
    VISITS(Comparator.comparingLong(PlayerWarp::getTotalVisits).reversed()),
    ;

    private final Comparator<PlayerWarp> comparator;

    PlayerWarpSortType(@NotNull Comparator<PlayerWarp> comparator) {
        this.comparator = comparator;
    }

    @NonNull
    public Comparator<PlayerWarp> getComparator() {
        return this.comparator;
    }

    @NonNull
    public PlayerWarpSortType next() {
        return Lists.next(this);
    }

    @NonNull
    public String localized() {
        return PlayerWarpsLang.SORT_TYPE.getLocalized(this);
    }
}
