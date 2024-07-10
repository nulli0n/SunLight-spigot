package su.nightexpress.sunlight.module.bans.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum TimeUnit {

    PERMANENT(-1L, new String[]{"permanent"}),
    SECONDS(1000L, new String[]{"sec", "s"}),
    MINUTES(1000L * 60L, new String[]{"min"}),
    HOURS(1000L * 60L * 60L, new String[]{"hour", "h"}),
    DAYS(1000L * 60L * 60L * 24L, new String[]{"day", "d"}),
    WEEKS(1000L * 60L * 60L * 24L * 7L, new String[]{"week", "w"}),
    MONTHS(1000L * 60L * 60L * 24L * 30L, new String[]{"mon"}),
    YEARS(1000L * 60L * 60L * 24L * 365L, new String[]{"year", "y"}),
    ;


    @Nullable
    public static TimeUnit byName(@NotNull String name) {
        for (TimeUnit timeUnit : TimeUnit.values()) {
            for (String alias : timeUnit.getAliases()) {
                if (alias.equalsIgnoreCase(name)) {
                    return timeUnit;
                }
            }
        }
        return null;
    }

    private final long modifier;
    private String[] aliases;

    TimeUnit(long modifier, String[] aliases) {
        this.modifier = modifier;
        this.aliases = aliases;
    }

    public long getAbsolute(long millis) {
        return millis / this.modifier;
    }

    public long getModifier() {
        return this.modifier;
    }

    public String[] getAliases() {
        return aliases;
    }

    public void setAliases(String[] aliases) {
        this.aliases = aliases;
    }
}
