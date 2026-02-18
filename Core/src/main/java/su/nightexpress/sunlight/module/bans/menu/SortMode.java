package su.nightexpress.sunlight.module.bans.menu;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.sunlight.module.bans.punishment.AbstractPunishment;

import java.util.Comparator;

public enum SortMode {

    NAME_ASCENT(Comparator.comparing(AbstractPunishment::getName)),
    NAME_DESCENT(NAME_ASCENT.comparator.reversed()),
    OLDEST(Comparator.comparingLong(AbstractPunishment::getCreationDate)),
    NEWEST(OLDEST.comparator.reversed()),
    LEAST_DURATION(Comparator.comparingLong(AbstractPunishment::getDuration)),
    MOST_DURATION(LEAST_DURATION.comparator.reversed()),
    PUNISHER(Comparator.comparing(AbstractPunishment::getWho)),
    REASON(Comparator.comparing(AbstractPunishment::getReason));

    private final Comparator<AbstractPunishment> comparator;

    SortMode(@NotNull Comparator<AbstractPunishment> comparator) {
        this.comparator = comparator;
    }

    @NotNull
    public Comparator<AbstractPunishment> comparator() {
        return this.comparator;
    }
}
