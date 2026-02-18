package su.nightexpress.sunlight.module.bans.punishment;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public record PunishmentData(@NotNull UUID id,
                             @NotNull PunishmentType type,
                             @NotNull String reason,
                             @NotNull String who,
                             long duration,
                             long creationTimestamp,
                             long expirationTimestamp) {

}
