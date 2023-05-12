package su.nightexpress.sunlight.module.bans.punishment;

import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.config.JYML;
import su.nightexpress.sunlight.module.bans.util.BanTime;

public class RankDuration {

    private final int     amount;
    private final BanTime timeUnit;

    public RankDuration(int amount, @NotNull BanTime timeUnit) {
        this.amount = amount;
        this.timeUnit = timeUnit;
    }

    @NotNull
    public static RankDuration read(@NotNull JYML cfg, @NotNull String path) {
        int amount = cfg.getInt(path + ".Amount", -1);
        BanTime timeUnit = cfg.getEnum(path + ".TimeUnit", BanTime.class, BanTime.SECONDS);
        return new RankDuration(amount, timeUnit);
    }

    public void write(@NotNull JYML cfg, @NotNull String path) {
        cfg.set(path + ".Amount", this.getAmount());
        cfg.set(path + ".TimeUnit", this.getTimeUnit().name());
    }

    public int getAmount() {
        return amount;
    }

    @NotNull
    public BanTime getTimeUnit() {
        return timeUnit;
    }

    public long getMillis() {
        return this.getTimeUnit().getModifier() * this.getAmount();
    }
}
