package su.nightexpress.sunlight.utils;

import org.jetbrains.annotations.NotNull;

import java.time.LocalTime;

public class WorldTime {

    public static final long MODIFIER = 1000L;
    public static final long MAX_TICKS = 24L * MODIFIER;
    public static final long MIN_TICKS = 0L;

    public static long clamp(long ticks) {
        return Math.clamp(ticks, MIN_TICKS, MAX_TICKS);
    }

    @NotNull
    public static LocalTime getTimeOfTicks(long ticks) {
        double point = ticks * 3.6;

        int hours = (int) (point / 60D / 60D);
        int minutes = (int) ((point / 60D) % 60);
        int seconds = (int) (point % 60);
        return LocalTime.of(hours, minutes, seconds).plusHours(6);
    }
}
