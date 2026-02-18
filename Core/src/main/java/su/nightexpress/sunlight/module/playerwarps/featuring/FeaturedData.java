package su.nightexpress.sunlight.module.playerwarps.featuring;

import org.jspecify.annotations.NonNull;
import su.nightexpress.nightcore.util.TimeUtil;

public record FeaturedData(@NonNull String slotId, int slotIndex, long endTimestamp) {

    public boolean isActive() {
        return !TimeUtil.isPassed(this.endTimestamp);
    }
}
