package su.nightexpress.sunlight.module.bans.punishment;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.sunlight.module.bans.time.BanTime;

public record PunishmentContext(@NotNull PunishmentType type,
                                @NotNull PunishmentReason reason,
                                @NotNull BanTime time,
                                boolean silent) {

}
