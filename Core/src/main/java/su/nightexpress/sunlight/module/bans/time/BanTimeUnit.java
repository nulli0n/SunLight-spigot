package su.nightexpress.sunlight.module.bans.time;

public enum BanTimeUnit implements BanTimeAccumulator {

    SECONDS(1000L),
    MINUTES(1000L * 60L),
    HOURS(1000L * 60L * 60L),
    DAYS(1000L * 60L * 60L * 24L),
    WEEKS(1000L * 60L * 60L * 24L * 7L),
    MONTHS(1000L * 60L * 60L * 24L * 30L),
    YEARS(1000L * 60L * 60L * 24L * 365L),
    ;

    private final long modifier;

    BanTimeUnit(long modifier) {
        this.modifier = modifier;
    }

    @Override
    public long accumulate(long quantity) {
        return quantity * this.modifier;
    }
}
