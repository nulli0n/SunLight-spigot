package su.nightexpress.sunlight.module.bans.time;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.nightcore.util.Enums;
import su.nightexpress.nightcore.util.Numbers;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class BanTime {

    private static final BanTime PERMANENT = new BanTime(quantity -> -1L, 1L);

    private final BanTimeAccumulator accumulator;
    private final long               amount;

    private BanTime(@NotNull BanTimeAccumulator accumulator, long amount) {
        this.accumulator = accumulator;
        this.amount = Math.abs(amount);
    }

    @NotNull
    public static BanTime temporary(@NotNull BanTimeAccumulator timeUnit, long quantity) {
        return new BanTime(timeUnit, quantity);
    }

    @NotNull
    public static BanTime permanent() {
        return PERMANENT;
    }

    @Nullable
    public static BanTime parse(@NotNull String string) {
        int index = string.indexOf(' ');
        String amountRaw = index > 0 ? string.substring(0, index) : string;
        String unitRaw = index > 0 ? string.substring(index) : null;

        int amount = Numbers.getAnyInteger(amountRaw, -1);
        BanTimeUnit unit = Optional.ofNullable(unitRaw).map(raw -> Enums.get(raw, BanTimeUnit.class)).orElse(BanTimeUnit.SECONDS);

        return temporary(unit, amount);
    }

    @NotNull
    public String serialize() {
        long qty;
        BanTimeUnit banTimeUnit;

        if (this.accumulator instanceof BanTimeUnit unit) {
            qty = this.amount;
            banTimeUnit = unit;
        }
        else {
            qty = TimeUnit.SECONDS.convert(this.accumulated(), TimeUnit.MILLISECONDS);
            banTimeUnit = BanTimeUnit.SECONDS;
        }

        return qty + " " + banTimeUnit.name();
    }

    public long futureTimestamp() {
        long result = this.accumulated();
        return result < 0 ? -1L : System.currentTimeMillis() + result;
    }

    public long accumulated() {
        return this.accumulator.accumulate(this.amount);
    }

    public boolean isPermanent() {
        return this == PERMANENT;
    }

    public boolean isGreater(@NotNull BanTime other) {
        return this.isGreater(other.accumulated());
    }

    public boolean isGreater(long other) {
        long result = this.accumulated();

        if (other < 0L) return false;
        if (result < 0L) return true;

        return result > other;
    }

    public boolean isSmaller(@NotNull BanTime other) {
        return this.isSmaller(other.accumulated());
    }

    public boolean isSmaller(long other) {
        long result = this.accumulated();

        if (result < 0L) return false;
        if (other < 0L) return true;

        return result < other;
    }

    @NotNull
    public BanTimeAccumulator getAccumulator() {
        return this.accumulator;
    }

    public long getAmount() {
        return this.amount;
    }
}
