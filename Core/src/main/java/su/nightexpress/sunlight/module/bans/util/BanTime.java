package su.nightexpress.sunlight.module.bans.util;

import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.utils.StringUtil;
import su.nightexpress.sunlight.module.bans.config.BansConfig;

import java.util.Collections;
import java.util.Set;

public enum BanTime {

    SECONDS(1000L),
    MINUTES(1000L * 60L),
    HOURS(1000L * 60L * 60L),
    DAYS(1000L * 60L * 60L * 24L),
    WEEKS(1000L * 60L * 60L * 24L * 7L),
    MONTHS(1000L * 60L * 60L * 24L * 30L),
    YEARS(1000L * 60L * 60L * 24L * 365L),
    ;

    private final long modifier;

    BanTime(long modifier) {
        this.modifier = modifier;
    }

    public static long parse(@NotNull String timeRaw) {
        long mod = SECONDS.getModifier();
        long time = 0L;

        for (BanTime banTime : BanTime.values()) {
            Set<String> aliases = BansConfig.GENERAL_TIME_ALIASES.get().getOrDefault(banTime, Collections.emptySet());
            String alias = aliases.stream().filter(timeRaw::endsWith).findFirst().orElse(null);
            if (alias == null || alias.isEmpty()) continue;

            time = StringUtil.getInteger(timeRaw.replace(alias, ""), 0);
            mod = banTime.getModifier();
            break;
        }
        return time <= 0L ? -1L : /*System.currentTimeMillis() + */time * mod;
    }

    public long getModifier() {
        return this.modifier;
    }
}
