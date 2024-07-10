package su.nightexpress.sunlight.module.bans.util;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.config.FileConfig;

public class RankDuration {

    private final int      amount;
    private final TimeUnit timeUnit;

    public RankDuration(int amount, @NotNull TimeUnit timeUnit) {
        this.amount = amount;
        this.timeUnit = timeUnit;
    }

    @NotNull
    public static RankDuration read(@NotNull FileConfig cfg, @NotNull String path) {
        int amount = cfg.getInt(path + ".Amount", -1);
        TimeUnit timeUnit = cfg.getEnum(path + ".TimeUnit", TimeUnit.class, TimeUnit.SECONDS);
        return new RankDuration(amount, timeUnit);
    }

    public void write(@NotNull FileConfig cfg, @NotNull String path) {
        cfg.set(path + ".Amount", this.getAmount());
        cfg.set(path + ".TimeUnit", this.getTimeUnit().name());
    }

    public int getAmount() {
        return amount;
    }

    @NotNull
    public TimeUnit getTimeUnit() {
        return timeUnit;
    }

    public long getInMillis() {
        return this.getTimeUnit().getModifier() * this.getAmount();
    }
}
