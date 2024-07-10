package su.nightexpress.sunlight.module.bans.punishment;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.util.placeholder.Placeholder;
import su.nightexpress.nightcore.util.placeholder.PlaceholderMap;
import su.nightexpress.sunlight.module.bans.util.Placeholders;

import java.util.UUID;

public abstract class PunishData implements Placeholder {

    private final UUID   id;
    private final String reason;
    private final String admin;
    private final long   createDate;

    protected final PlaceholderMap placeholderMap;

    private long expireDate;

    public PunishData(@NotNull UUID id, @NotNull String reason, @NotNull String admin, long createDate, long expireDate) {
        this.id = id;
        this.reason = reason;
        this.admin = admin;
        this.createDate = createDate;
        this.expireDate = expireDate;
        this.placeholderMap = Placeholders.forPunishData(this);
    }

    @Override
    @NotNull
    public PlaceholderMap getPlaceholders() {
        return this.placeholderMap;
    }

    public abstract boolean isApplicable(@NotNull Player player);

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

    @NotNull
    public UUID getId() {
        return id;
    }

    @NotNull
    public String getReason() {
        return reason;
    }

    @NotNull
    public String getAdmin() {
        return admin;
    }

    public long getCreateDate() {
        return createDate;
    }

    public long getExpireDate() {
        return expireDate;
    }
}
