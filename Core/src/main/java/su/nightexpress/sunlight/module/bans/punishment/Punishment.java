package su.nightexpress.sunlight.module.bans.punishment;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nexmedia.engine.api.placeholder.Placeholder;
import su.nexmedia.engine.api.placeholder.PlaceholderMap;
import su.nexmedia.engine.lang.LangManager;
import su.nexmedia.engine.utils.Colorizer;
import su.nexmedia.engine.utils.TimeUtil;
import su.nexmedia.engine.utils.regex.RegexUtil;
import su.nightexpress.sunlight.SunLightAPI;
import su.nightexpress.sunlight.config.Config;
import su.nightexpress.sunlight.config.Lang;
import su.nightexpress.sunlight.module.bans.util.Placeholders;

import java.util.UUID;

public class Punishment implements Placeholder {

    private final UUID           id;
    private final PunishmentType type;
    private final String         user;
    private final String         reason;
    private final String         admin;
    private final long           createdDate;
    private final boolean        isIp;
    private final PlaceholderMap placeholderMap;

    private UUID userId;
    private long expireDate;

    public Punishment(
        @NotNull PunishmentType type,
        @NotNull String user,
        @NotNull String reason,
        @NotNull String admin,
        long expireDate
    ) {
        this(UUID.randomUUID(), null, type, user, reason, admin, System.currentTimeMillis(), expireDate);
    }

    public Punishment(
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
        this.reason = Colorizer.apply(reason);
        this.admin = admin;
        this.createdDate = createdDate;
        this.expireDate = expireDate;
        this.isIp = RegexUtil.isIpAddress(this.getUser());

        this.placeholderMap = new PlaceholderMap()
            .add(Placeholders.PUNISHMENT_TYPE, SunLightAPI.PLUGIN.getLangManager().getEnum(this.getType()))
            .add(Placeholders.PUNISHMENT_USER, this.getUser())
            .add(Placeholders.PUNISHMENT_REASON, this.getReason())
            .add(Placeholders.PUNISHMENT_PUNISHER, this.getAdmin())
            .add(Placeholders.PUNISHMENT_EXPIRES_IN, () -> !this.isPermanent() ? TimeUtil.formatTimeLeft(this.getExpireDate()) : LangManager.getPlain(Lang.OTHER_NEVER))
            .add(Placeholders.PUNISHMENT_CREATION_DATE, () -> Config.GENERAL_DATE_FORMAT.get().format(this.getCreatedDate()))
            .add(Placeholders.PUNISHMENT_EXPIRE_DATE, () -> !this.isPermanent() ? Config.GENERAL_DATE_FORMAT.get().format(this.getExpireDate()) : LangManager.getPlain(Lang.OTHER_NEVER))
        ;
    }

    @Override
    @NotNull
    public PlaceholderMap getPlaceholders() {
        return this.placeholderMap;
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

    public void expire() {
        this.expireDate = System.currentTimeMillis();
    }

    public boolean isActive() {
        return !this.isExpired();
    }

    public boolean isExpired() {
        return !this.isPermanent() && System.currentTimeMillis() >= this.getExpireDate();
    }

    public boolean isPermanent() {
        return this.getExpireDate() < 0;
    }

    @Override
    public String toString() {
        return "Punishment{" +
            "type=" + type +
            ", user='" + user + '\'' +
            ", reason='" + reason + '\'' +
            ", admin='" + admin + '\'' +
            ", createdDate=" + createdDate +
            ", expireDate=" + expireDate +
            '}';
    }
}
