package su.nightexpress.sunlight.module.bans.punishment;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.nightcore.util.Colorizer;
import su.nightexpress.sunlight.utils.SunUtils;

import java.util.UUID;

public class LegacyPunishment {

    private final UUID           id;
    private final PunishmentType type;
    private final String         user;
    private final String         reason;
    private final String         admin;
    private final long           createdDate;
    private final boolean        isIp;

    private       UUID userId;
    private final long expireDate;

    public LegacyPunishment(
        @NotNull UUID id,
        @Nullable UUID userId,
        @NotNull PunishmentType type,
        @NotNull String user,
        @NotNull String reason,
        @NotNull String admin,
        long createdDate,
        long expireDate
    ) {
        this.id = id;
        this.type = type;
        this.user = user;
        this.userId = userId;
        this.reason = Colorizer.restrip(reason);
        this.admin = admin;
        this.createdDate = createdDate;
        this.expireDate = expireDate;
        this.isIp = SunUtils.isInetAddress(this.getUser());
    }

    @NotNull
    public UUID getId() {
        return id;
    }

    @Nullable
    public UUID getUserId() {
        return userId;
    }

    public void setUserId(@NotNull UUID userId) {
        this.userId = userId;
    }

    @NotNull
    public PunishmentType getType() {
        return type;
    }

    @NotNull
    public String getUser() {
        return this.user;
    }

    public boolean isIp() {
        return isIp;
    }

    @NotNull
    public String getReason() {
        return this.reason;
    }

    @NotNull
    public String getAdmin() {
        return this.admin;
    }

    public long getCreatedDate() {
        return this.createdDate;
    }

    public long getExpireDate() {
        return this.expireDate;
    }
}
