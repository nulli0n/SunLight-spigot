package su.nightexpress.sunlight.module.bans.util;

import org.jetbrains.annotations.NotNull;

public class BanTime {

    public static final BanTime PERMANENT = new BanTime(TimeUnit.PERMANENT, 0L);

    private final TimeUnit timeUnit;
    private final long amount;

    public BanTime(@NotNull TimeUnit timeUnit, long amount) {
        this.timeUnit = timeUnit;
        this.amount = Math.abs(amount);
    }

    public long toTimestamp() {
        if (this.timeUnit == TimeUnit.PERMANENT) return -1L;

        return System.currentTimeMillis() + this.getInMillis();
    }

    public long getInMillis() {
        return this.amount * this.timeUnit.getModifier() + 100L; // 100L for better time format.
    }

    public boolean isPermanent() {
        return this.timeUnit == TimeUnit.PERMANENT;
    }

    @NotNull
    public TimeUnit getTimeUnit() {
        return timeUnit;
    }

    public long getAmount() {
        return amount;
    }
}
